package csvdb

import java.util

import com.beust.jcommander.Parameter

import scala.collection.JavaConverters._
import scala.collection.breakOut

private[csvdb] object Args {
  @Parameter(names = Array("-?", "--help"), help = true)
  var help: Boolean = false

  // we probably need a grammar for providing delimiters so you can give a delimiter per file
  @Parameter(names = Array("-d"), description = "Per-file delimiter settings")
  var delimiter: String = null

  @Parameter(names = Array("-H"), description = "Files with headers")
  var filesWithHeaders: String = null

  @Parameter(names = Array("-q"), description = "File containing a query or set of queries to execute")
  var queryFile: String = null

  @Parameter(description = "Files")
  var files: util.List[String] = new util.ArrayList[String]()

  lazy val filesByIndex: Map[Int, String] = files.asScala.zipWithIndex.map { case (file, idx) =>
    (idx + 1) -> file
  }(breakOut)

  lazy val delimiterFor: Map[String, String] = {
    delimiter.split(";", -1).flatMap { ds: String =>
      ds.split(":").toList match {
        case file :: delim :: Nil => filesByIndex.get(file.toInt) map (_ -> delim)
        case _ => None
      }
    }(breakOut)
  }

}
