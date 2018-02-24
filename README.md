## Java Sample Project for Data Anonymization

1. Download and start H2 database server
1. Connect to source (movies_source) database and execute create_tables.sql script
1. Connect to destination (movies_dest) database and execute create_tables.sql script
1. Insert sample data into source database tables using scripts insert_movies.sql and insert_ratings.sql
1. Run this main program using following command
1. Now change Main program (Anonymizer.kt) as per your need and delete resources (*.sql files)

### Running it using maven command line

```
$ mvn exec:java
```


### Running it as executable jar

first package it and run the generated jar in target folder

```
$ mvn package
$ java -jar target/dataanon-java-sample-1.0-SNAPSHOT.jar
```

