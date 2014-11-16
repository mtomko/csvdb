package csvdb

import java.io.File
import java.sql.{Connection, ResultSet}

import jline.TerminalFactory
import jline.console.ConsoleReader
import jline.console.history.FileHistory

import scala.annotation.tailrec
import scala.io.Source
import scala.util.{Failure, Try}

import resource._

object IO {

  def printResults(rs: ResultSet): Unit = {
    val md = rs.getMetaData
    val ncols = md.getColumnCount
    while(rs.next()) {
      println((1 to ncols map { c => rs.getObject(c) }).mkString("\t"))
    }
  }

  private[csvdb] def file(file: String)(implicit conn: Connection): Boolean = {
    for(source <- managed(Source.fromFile(file))) {
      //TODO: Fill this in
    }
    true
  }

  private[csvdb] def repl(implicit conn: Connection): Unit = {
    TerminalFactory.configure(TerminalFactory.AUTO)
    TerminalFactory.reset()
    val reader = new ConsoleReader("csvdb", System.in, System.out, TerminalFactory.get())
    reader.setHistoryEnabled(true)
    reader.setHistory(new FileHistory(new File(new File(System.getProperty("user.home")),
                                               ".csvdb_history")))

    @tailrec def loop(): Unit = {
      accumulateStatement(reader) match {
        case Some(statement) =>
          Try { DB.executeStatement(printResults)(statement) } match {
            case Failure(e) => println(e.getMessage)
            case _ =>
          }
          loop()
        case None =>
      }
    }
    loop()
  }

  private[csvdb] def accumulateStatement(reader: ConsoleReader): Option[String] = {
    @tailrec def acc(s: String, prompt: String): Option[String] = {
      val line = reader.readLine(prompt)
      if (line == null) None
      else if (line.startsWith("/")) Some(s)
      else {
        acc(s"$s $line", "| ")
      }
    }
    acc("", "> ")
  }

}
