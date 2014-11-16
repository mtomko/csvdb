package csvdb

import java.io.File
import java.sql.{ResultSet, Connection}
import javax.sql.DataSource

import org.h2.jdbcx.JdbcDataSource
import resource._

import scala.io.Source

object DB {

  private[this] lazy val ds: DataSource = {
    val _ = classOf[org.h2.Driver]
    val ds = new JdbcDataSource()
    //from http://stackoverflow.com/questions/5763747/h2-in-memory-database-what-am-i-missing
    ds.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
    ds.setUser("")
    ds.setPassword("")
    ds
  }

  def connection = {
    val conn = ds.getConnection
    conn.setAutoCommit(false)
    conn
  }

  def executeStatement(f: ResultSet => Unit)(statement: String)(implicit conn: Connection): Unit = {
    for(stmt <- managed(conn.createStatement);
        rs <- managed(stmt.executeQuery(statement))) {
      f(rs)
    }
  }

  def loadCsv(filename: String, delimiter: String)
                            (implicit conn: Connection): Unit = {
    val lines = countColumns(filename, delimiter)
    val headers = (1 to lines map { n: Int => s"_$n" }).mkString(",")
    for (stmt <- managed(conn.createStatement())) {
      val file = new File(filename)
      val tableName = tableNameFor(file)
      stmt.execute(
        s"""create table $tableName as
           |select
           |  *
           |from csvread('$filename',
           |             '$headers',
           |             'charset=UTF-8')""".stripMargin)
      1 to lines foreach { n =>
        stmt.execute(s"create index ${tableName}_${n}_idx on $tableName(_$n)")
      }
    }
  }

  private[csvdb] def tableNameFor(f: File): String =
    f.getName
      .replace('.', '_')
      .replace('-', '_')

  private[csvdb] def countColumns(file: String, delimiter: String): Int = {
    val source = Source.fromFile(file)
    try {
      source.getLines().toSeq.head.split(delimiter, -1).length
    } finally {
      source.close()
    }
  }

}
