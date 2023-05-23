package org.example.newsstreaming.constants;

/**
 * @author irfan.nagoo
 */
public interface MessagingConstants {

    String TOTAL_RECORD_MSG = "Total [%d] Records Found";
    String NOT_FOUND_MSG = "The News Sentiment data not found";
    String REQUEST_PROCESSED_MSG = "Request Processed Successfully";
    String DATE_RANGE_ERROR_MSG = "Invalid start and end date range";
    String FUTURE_DATE_ERROR_MSG = "End date can not be future date";
    String INVALID_SENTIMENT_ERROR_MSG = "Invalid Sentiment value. Valid values: [Positive, Negative]";
    String INVALID_REQUEST_ERROR_MSG = "Invalid request parameters";
    String INVALID_API_KEY_ERROR_MSG = "The apiKey or Authorization parameters are Invalid";
    String REST_PROCESSING_ERROR_MSG = "Processing error [%s] occurred on the destination side";
    String PROCESSING_ERROR_MSG = "Processing error occurred";
}
