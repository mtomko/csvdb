package csvdb

import java.io.File
import java.sql.Connection

import csvdb.DB._
import jline.TerminalFactory
import jline.console.ConsoleReader
import jline.console.history.FileHistory
import resource._

import scala.annotation.tailrec
import scala.io.Source
import scala.util.{Failure, Try}

object IO {

  /** Prints the results of a query, and the number of affected rows for deletes and updates */
  def printResults(results: ResultType): Unit = {
    results match {
      case Select(_, rs) =>
        val md = rs.getMetaData
        val ncols = md.getColumnCount
        @tailrec def loop(): Unit = {
          if (rs.next()) {
            println((1 to ncols map { c => rs.getString(c) }).mkString("\t"))
            loop()
          }
        }
        loop()
      case Update(_, rows) => println(s"$rows updated.")
      case Delete(_, rows) => println(s"$rows deleted.")
      case Other(_, succeeded) =>
        if (succeeded) println("Succeeded.")
        else println("Failed.")
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
    reader.setHistory(fileHistory)

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

  private[csvdb] lazy val fileHistory =
    new FileHistory(new File(new File(System.getProperty("user.home")),
                             ".csvdb_history"))

}
