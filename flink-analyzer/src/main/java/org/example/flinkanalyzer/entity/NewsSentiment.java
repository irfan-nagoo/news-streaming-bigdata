package org.example.flinkanalyzer.entity;

import com.datastax.driver.mapping.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author irfan.nagoo
 */

@Table(keyspace = "news_analytics", name = "news_sentiment")
public class NewsSentiment {

    @PartitionKey
    @Column(name = "part_date")
    private LocalDate partDate;
    @ClusteringColumn
    @Column(name = "pub_date")
    private LocalDateTime pubDate;
    @Column(name = "id")
    private transient UUID id;
    @Transient
    private String timeUUID;
    @Column(name = "title")
    private String title;
    @Column(name = "link")
    private String link;
    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "sentiment")
    private String sentiment;
    @Column(name = "create_timestamp")
    private LocalDateTime createTimestamp;

    public LocalDate getPartDate() {
        return partDate;
    }

    public void setPartDate(LocalDate partDate) {
        this.partDate = partDate;
    }

    public LocalDateTime getPubDate() {
        return pubDate;
    }

    public void setPubDate(LocalDateTime pubDate) {
        this.pubDate = pubDate;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTimeUUID() {
        return timeUUID;
    }

    public void setTimeUUID(String timeUUID) {
        this.timeUUID = timeUUID;
        this.id = UUID.fromString(timeUUID);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public LocalDateTime getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(LocalDateTime createTimestamp) {
        this.createTimestamp = createTimestamp;
    }
}
