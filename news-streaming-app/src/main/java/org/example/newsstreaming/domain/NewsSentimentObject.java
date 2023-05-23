package org.example.newsstreaming.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author irfan.nagoo
 */

@Getter
@Setter
public class NewsSentimentObject {
    private UUID id;
    private String title;
    private String link;
    private String imageUrl;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime pubDate;
    private String sentiment;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimestamp;

}
