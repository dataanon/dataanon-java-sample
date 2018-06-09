package com.github.dataanon;

import com.github.dataanon.dsl.Whitelist;
import com.github.dataanon.model.DbConfig;
import com.github.dataanon.strategy.AnonymizationStrategy;
import com.github.dataanon.strategy.datetime.DateRandomDelta;
import com.github.dataanon.strategy.datetime.DateTimeRandomDelta;
import com.github.dataanon.strategy.list.PickFromDatabase;
import com.github.dataanon.strategy.list.PickFromFile;
import com.github.dataanon.strategy.number.FixedDouble;
import kotlin.Unit;

import java.time.Duration;

public class Anonymizer {

    // Download and start H2 database server
    // Connect to source (movies_source) database and execute create_tables.sql script
    // Connect to destination (movies_dest) database and execute create_tables.sql script
    // Insert sample data into source database tables using scripts insert_movies.sql and insert_ratings.sql
    // Run this main program

    public static void main(String[] args) {

        DbConfig source = new DbConfig("jdbc:h2:tcp://localhost/~/movies_source", "sa", "");
        DbConfig dest = new DbConfig("jdbc:h2:tcp://localhost/~/movies_dest", "sa", "");

        new Whitelist(source, dest)
            .table("MOVIES", table -> {
                table.where("GENRE IN ('Drama','Action')");
                table.limit(1_00);
                table.whitelist("MOVIE_ID");
                table.anonymize("TITLE").using((AnonymizationStrategy<String>) (field, record) -> "MY MOVIE " + record.getRowNum());
                table.anonymize("GENRE").using(new PickFromDatabase<String>(source,"SELECT DISTINCT GENRE FROM MOVIES"));
                table.anonymize("RELEASE_DATE").using(new DateRandomDelta(10));
                return Unit.INSTANCE;
            })
            .table("RATINGS", table -> {
                table.whitelist("MOVIE_ID", "USER_ID");
                table.anonymize("RATING").using(new PickFromFile<Float>("/random-ratings.txt"));     //data file in resource folder
                table.anonymize("CREATED_AT").using(new DateTimeRandomDelta(Duration.ofSeconds(2000)));

                return Unit.INSTANCE;
            })
            .execute();
    }
}
