package csvdb

import org.scalatest.{Matchers, FlatSpec}

import scala.io.Source

class DBSpec extends FlatSpec with Matchers {

  "DB" should "sanitize file names" in {
    DB.tableNameFor("../../../exported-data.csv") should be ("exported_data_csv")
    DB.tableNameFor("exported-data.csv") should be ("exported_data_csv")
    DB.tableNameFor("my file.tsv") should be ("my_file_tsv")
  }
  it should "count columns based on the first line of input" in {
    val csv =
      """foo,bar,baz,this is a thing,quux\t
        |monkey,monkey,monkey
      """.stripMargin
    DB.countColumns(Source.fromString(csv), ",") should be (5)

    // this test fails if I don't use a string context; there's something funky going on here w.r.t.
    // the interpretation of tabs
    val tsv =
      s"""foo\tbar\tbaz\tthis is a thing\tquux,
         |monkey, monkey
       """.stripMargin

    DB.countColumns(Source.fromString(tsv), "\t") should be (5)
  }

}
