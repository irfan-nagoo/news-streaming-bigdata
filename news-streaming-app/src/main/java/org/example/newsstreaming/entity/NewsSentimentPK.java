package org.example.newsstreaming.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author irfan.nagoo
 */

@PrimaryKeyClass
@Getter
@Setter
public class NewsSentimentPK implements Serializable {

    @PrimaryKeyColumn(name = "part_date", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private LocalDate partDate;
    @PrimaryKeyColumn(name = "pub_date", ordinal = 1, type = PrimaryKeyType.CLUSTERED,
            ordering = Ordering.DESCENDING)
    private LocalDateTime pubDate;

}
