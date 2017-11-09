package com.github.dataanon;

import com.github.dataanon.dsl.Whitelist;
import com.github.dataanon.model.DbConfig;
import com.github.dataanon.strategy.AnonymizationStrategy;
import com.github.dataanon.strategy.number.FixedDouble;
import com.github.dataanon.strategy.string.FixedString;
import kotlin.Unit;

public class Anonymizer {
    public static void main(String[] args) {

        DbConfig source = new DbConfig("jdbc:h2:tcp://localhost/~/movies_source", "sa", "");
        DbConfig dest = new DbConfig("jdbc:h2:tcp://localhost/~/movies_dest", "sa", "");

        new Whitelist(source, dest)
                .table("MOVIES", table -> {
                    table.where("GENRE = 'Drama'");
                    table.whitelist("MOVIE_ID", "RELEASE_DATE");
                    table.anonymize("TITLE").using((AnonymizationStrategy<String>) (field, record) -> "MY MOVIE " + record.getRowNum());
                    table.anonymize("GENRE").using(new FixedString("Action"));
                    return Unit.INSTANCE;
                })
                .table("RATINGS", table -> {
                    table.whitelist("MOVIE_ID", "USER_ID", "CREATED_AT");
                    table.anonymize("RATING").using(new FixedDouble(4.3));
                    return Unit.INSTANCE;
                })
                .execute(-1, true);

    }
}
