package org.example.newsstreaming.entity;

import lombok.Getter;
import lombok.Setter;
import org.example.newsstreaming.domain.NewsSentimentObject;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author irfan.nagoo
 */

@Table("news_sentiment")
@Getter
@Setter
public class NewsSentiment {

    @PrimaryKey
    private NewsSentimentPK key;
    private UUID id;
    private String title;
    private String link;
    @Column("image_url")
    private String imageUrl;
    private String sentiment;
    @Column("create_timestamp")
    private LocalDateTime createTimestamp;

    public NewsSentimentObject toObject() {
        NewsSentimentObject newsSentiment = new NewsSentimentObject();
        newsSentiment.setId(this.id);
        newsSentiment.setTitle(this.title);
        newsSentiment.setLink(this.link);
        newsSentiment.setImageUrl(this.imageUrl);
        newsSentiment.setPubDate(this.key.getPubDate());
        newsSentiment.setSentiment(this.sentiment);
        newsSentiment.setCreateTimestamp(this.createTimestamp);
        return newsSentiment;
    }
}
