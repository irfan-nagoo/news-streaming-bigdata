package org.example.newsstreaming.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import org.example.newsstreaming.deserializer.LocalDateTimeDeserializer;
import org.example.newsstreaming.serializer.LocalDateTimeSerializer;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author irfan.nagoo
 */

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NewsObject {

    private String title;
    private String link;
    private List<String> keywords;
    private List<String> creator;
    @JsonProperty("video_url")
    private String videoUrl;
    private String description;
    private String content;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime pubDate;
    @JsonProperty("image_url")
    private String imageUrl;
    @JsonProperty("source_id")
    private String sourceId;
    private List<String> category;
    private List<String> country;
    private String language;

}
