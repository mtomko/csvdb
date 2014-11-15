package csvdb

import java.io.File
import java.sql.Connection
import javax.sql.DataSource

import jline.TerminalFactory
import jline.console.ConsoleReader
import jline.console.history.FileHistory
import org.h2.jdbcx.JdbcDataSource
import resource._

import scala.annotation.tailrec
import scala.io.Source

object Csvdb {

  val user = ""
  val pass = ""

  implicit lazy val ds: DataSource = {
    val _ = classOf[org.h2.Driver]
    val ds = new JdbcDataSource()
    //from http://stackoverflow.com/questions/5763747/h2-in-memory-database-what-am-i-missing
    ds.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
    ds.setUser(user)
    ds.setPassword(pass)
    ds
  }

  // for now, we just assume each file is a headerless csv; we call the columns _1, _2, ...
  def main(args: Array[String]): Unit = {
    implicit val conn = ds.getConnection
    conn.setAutoCommit(false)
    try {
      args foreach { filename =>
        loadCsv(filename, "\t")
      }
      repl
    } finally {
      conn.close()
    }
  }

  def repl(implicit conn: Connection): Unit = {
    TerminalFactory.configure(TerminalFactory.AUTO)
    TerminalFactory.reset()
    val reader = new ConsoleReader("> ", System.in, System.out, TerminalFactory.create())
    reader.setHistoryEnabled(true)
    reader.setHistory(new FileHistory(new File(new File(System.getProperty("user.home")),
                                                      ".csvdb_history")))

    @tailrec def loop(): Unit = {
      accumulateStatement(reader) match {
        case Some(statement) =>
          executeStatement(statement)
          loop()
        case None =>
          //println("Done")
      }
    }
    loop()
  }

  def executeStatement(statement: String)(implicit conn: Connection): Unit = {
    println(s"Executing statement $statement")
    for(stmt <- managed(conn.createStatement);
        rs <- managed(stmt.executeQuery(statement))) {
      val md = rs.getMetaData
      val ncols = md.getColumnCount
      while(rs.next()) {
        println((1 to ncols map { c => rs.getObject(c) }).mkString("\t"))
      }
    }
  }

  def accumulateStatement(reader: ConsoleReader): Option[String] = {
    @tailrec def acc(s: String): Option[String] = {
      val line = reader.readLine()
      if (line == null) None
      else if (line.startsWith("/")) Some(s)
      else acc(s"$s $line")
    }
    acc("")
  }

  def loadCsv(filename: String, delimiter: String)(implicit conn: Connection): Unit = {
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

  def tableNameFor(f: File): String =
    f.getName
      .replace('.', '_')
      .replace('-', '_')

  def countColumns(file: String, delimiter: String): Int = {
    val source = Source.fromFile(file)
    try {
      source.getLines().toSeq.head.split(delimiter, -1).length
    } finally {
      source.close()
    }
  }

}
