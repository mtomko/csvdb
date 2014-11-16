package csvdb

import java.util

import com.beust.jcommander.Parameter

import scala.collection.JavaConverters._
import scala.collection.breakOut

private[csvdb] object Args {
  @Parameter(names = Array("-?", "--help"), help = true)
  var help: Boolean = false

  // we probably need a grammar for providing delimiters so you can give a delimiter per file
  @Parameter(names = Array("-d"), description = "Per-file delimiter settings.")
  var delimiter: String = null

  @Parameter(names = Array("-H"),
             description = "Files with headers. Not implemented yet.")
  var filesWithHeaders: String = null

  @Parameter(names = Array("-q"),
             description = "File containing a query or set of queries to execute. Not implemented yet.")
  var queryFile: String = null

  @Parameter(description = "CSV or TSV Files to represent as tables.")
  var files: util.List[String] = new util.ArrayList[String]()

  lazy val filesByIndex: Map[Int, String] = files.asScala.zipWithIndex.map { case (file, idx) =>
    (idx + 1) -> file
  }(breakOut)

  //TODO: select a delimiter other than ';', because the shell treats them as command separators
  lazy val delimiterFor: Map[String, String] = {
    if (delimiter == null) delimiter = ""
    delimiter.split(";", -1).flatMap { ds: String =>
      ds.split(":").toList match {
        case file :: delim :: Nil => filesByIndex.get(file.toInt) map (_ -> delim)
        case _ => None
      }
    }(breakOut)
  }

}
