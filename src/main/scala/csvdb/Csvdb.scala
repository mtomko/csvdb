package csvdb

import com.beust.jcommander.JCommander
import resource._

import scala.collection.JavaConverters._

object Csvdb {

  def main(args: Array[String]): Unit = {
    // parse command-line arguments; must be assigned to a val for some reason, perhaps dealing with
    // the delayed initialization from App
    val jcmd = JCommander.newBuilder().addObject(Args).build()
    jcmd.setProgramName("csvdb")
    jcmd.parse(args: _*)

    if (Args.help) {
      jcmd.usage()
      System.exit(-1)
    }

    // for now, we just assume each file is a headerless csv; we call the columns _1, _2, ...
    managed(DB.connection()) acquireAndGet  { implicit conn =>
      Args.files.asScala foreach { filename =>
        val delimiter = Args.delimiterFor.getOrElse(filename, "\t")
        DB.loadCsv(filename, delimiter)
      }
      Option(Args.queryFile) match {
        case Some(file) => if (!IO.file(file)) System.exit(-1)
        case None => IO.repl
      }
    }
  }
}
