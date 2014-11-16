package csvdb

import com.beust.jcommander.JCommander
import resource._

import scala.collection.JavaConverters._

object Csvdb extends App {
  // parse command-line arguments
  // must be assigned to val for some reason
  val _ = new JCommander(Args, args: _*)

  // for now, we just assume each file is a headerless csv; we call the columns _1, _2, ...
  managed(DB.connection) acquireAndGet  { implicit conn =>
    Args.files.asScala foreach { filename =>
      val delimiter = Args.delimiterFor.getOrElse(filename, "\t")
      DB.loadCsv(filename, delimiter)
    }
    IO.repl
  }
}
