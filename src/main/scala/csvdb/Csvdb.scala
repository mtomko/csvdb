package csvdb

import com.beust.jcommander.JCommander

import scala.collection.JavaConverters._

object Csvdb extends App {

  // parse command-line arguments
  // must be assigned to val for some reason
  val _ = new JCommander(Args, args: _*)

  // for now, we just assume each file is a headerless csv; we call the columns _1, _2, ...
  implicit val conn = DB.connection
  try {
    Args.files.asScala foreach { filename =>
      val delimiter = Args.delimiterFor.getOrElse(filename, "\t")
      DB.loadCsv(filename, delimiter)
    }
    IO.repl
  } finally {
    conn.close()
  }
}
