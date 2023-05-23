package org.example.newsstreaming.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.example.newsstreaming.constants.SentimentType;

import java.time.LocalDate;

/**
 * @author irfan.nagoo
 */

@Getter
@Setter
public class NewsSentimentReportResponse extends BaseResponse {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate periodStartDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate periodEndDate;
    private String positiveSentiments;
    private String negativeSentiments;
    private SentimentType periodOverallSentiment;

    public NewsSentimentReportResponse(String status, String message) {
        super(status, message);
    }
}
