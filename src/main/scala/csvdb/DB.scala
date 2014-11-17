package csvdb

import java.io.File
import java.sql.{Connection, ResultSet, Statement}
import javax.sql.DataSource

import org.h2.jdbcx.JdbcDataSource
import resource._

import scala.io.Source
import scala.util.{Success, Failure, Try}

object DB {

  /** A simple result type processed by result handlers */
  sealed trait ResultType extends AutoCloseable {
    val sql: String
    override def close() = { /* only needed by select, since it encapsulates a ResultSet */ }
  }
  case class Select(sql: String, rs: ResultSet) extends ResultType {
    override def close() = rs.close()
  }
  case class Update(sql: String, rowsAffected: Int) extends ResultType
  case class Delete(sql: String, rowsAffected: Int) extends ResultType
  case class Other(sql: String, succeeded: Boolean) extends ResultType

  // constructs an in-memory H2 datasource
  private[this] lazy val ds: DataSource = {
    val _ = classOf[org.h2.Driver]
    val ds = new JdbcDataSource()
    // from http://stackoverflow.com/questions/5763747/h2-in-memory-database-what-am-i-missing
    ds.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
    ds.setUser("")
    ds.setPassword("")
    ds
  }

  /** Get a new connection from the underlying datasource, with autocommit turned off */
  def connection() = {
    val conn = ds.getConnection
    conn.setAutoCommit(false)
    conn
  }

  /** Execute the provided statement and apply the result handler `f` to the result */
  def executeStatement(f: ResultType => Unit)(sql: String)(implicit conn: Connection): Unit = {
    for(stmt <- managed(conn.createStatement);
        result <- managed(executeQueryOrUpdate(stmt, sql))) {
      f(result)
    }
  }

  def loadCsv(filename: String, delimiter: String)(implicit conn: Connection): Unit = {
    val columns = countColumns(filename, delimiter)
    val headers = syntheticHeaders(columns)
    val tableName = tableNameFor(filename)

    // create the table
    for (stmt <- managed(conn.createStatement())) {
      val sql =
        s"""create table $tableName as
           |select
           |  *
           |from csvread('$filename',
           |             '$headers',
           |             'charset=UTF-8')""".stripMargin
      Try { stmt.execute(sql) } match {
        case Success(_) =>
        case Failure(e) =>
          println(s"Unable to load table $tableName: ${e.getMessage}")
          System.exit(-2)
      }
    }

    // we're going to blindly index all columns for now
    1 to columns foreach { n =>
      for (stmt <- managed(conn.createStatement())) {
        Try { stmt.execute(s"create index ${tableName}_${n}_idx on $tableName(_$n)") } match {
          case Success(_) =>
          case Failure(e) =>
            println(s"Unable to index column $n, continuing anyway: ${e.getMessage}")
        }
      }
    }
  }

  private[csvdb] def syntheticHeaders(columns: Int): String =
    (1 to columns map { n: Int => s"_$n" }).mkString(",")

  /* use these simple regexes to determine what kind of statement we're running */
  private lazy val QueryRe = """^\s*(?:(?:select)|(?:with)).*""".r
  private lazy val UpdateRe = """^\s*update.*""".r
  private lazy val DeleteRe = """^\s*delete.*""".r

  private[csvdb] def executeQueryOrUpdate(stmt: Statement, sql: String): ResultType = {
    sql.toLowerCase match {
      case QueryRe() => Select(sql, stmt.executeQuery(sql))
      case UpdateRe() => Update(sql, stmt.executeUpdate(sql))
      case DeleteRe() => Delete(sql, stmt.executeUpdate(sql))
      case _ => Other(sql, stmt.execute(sql))
    }
  }

  private[csvdb] def tableNameFor(filename: String): String =
    new File(filename)
      .getName
      .replace('.', '_')
      .replace('-', '_')
      .replace(' ', '_')

  private[csvdb] def countColumns(file: String, delimiter: String): Int =
    managed(Source.fromFile(file)) acquireAndGet { source =>
      countColumns(source, delimiter)
    }

  private[csvdb] def countColumns(source: Source, delimiter: String): Int = {
    val lines = source.getLines().toSeq
    val fields = lines.head.split(delimiter, -1)
    fields.length
  }

}
