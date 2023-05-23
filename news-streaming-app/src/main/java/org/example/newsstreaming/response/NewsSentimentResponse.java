package org.example.newsstreaming.response;

import lombok.Getter;
import lombok.Setter;
import org.example.newsstreaming.domain.NewsSentimentObject;

import java.util.List;

/**
 * @author irfan.nagoo
 */

@Getter
@Setter
public class NewsSentimentResponse extends BaseResponse {

    private List<NewsSentimentObject> newsSentiments;
    private String nextPage;

    public NewsSentimentResponse(String status, String message) {
        super(status, message);
    }
}
