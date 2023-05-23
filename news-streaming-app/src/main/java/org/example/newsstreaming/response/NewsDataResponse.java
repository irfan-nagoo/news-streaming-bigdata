package org.example.newsstreaming.response;

import lombok.Getter;
import lombok.Setter;
import org.example.newsstreaming.domain.NewsObject;

import java.util.List;

/**
 * @author irfan.nagoo
 */

@Getter
@Setter
public class NewsDataResponse {

    private String status;
    private Long totalResults;
    private List<NewsObject> results;
    private String nextPage;
}
