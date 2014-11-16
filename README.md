csvdb
=====

A utility that provides a simple SQL interface for querying and processing CSV and tab-delimited files.

Usage
=====
    % java -Xmx1G -jar target/bin/csvdb.jar -d '1:\t;2:,' file1.tsv file2.csv
    > select
    |   count(distinct a._1)
    | from file1_tsv a
    |   join file2_csv b on a._1 = b._1
    | /
    0
    > select count(b._1) from file2_csv b
    | /
    123411
    > % 
