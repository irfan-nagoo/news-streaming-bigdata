package org.example.newsstreaming.constants;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import static org.example.newsstreaming.constants.MessagingConstants.INVALID_SENTIMENT_ERROR_MSG;

/**
 * @author irfan.nagoo
 */
@Getter
public enum SentimentType {

    POSITIVE("Positive"),
    NEGATIVE("Negative"),
    NEUTRAL("Neutral");

    @JsonValue
    private final String value;

    SentimentType(String value) {
        this.value = value;
    }

    public static SentimentType getType(String value) {
        for (SentimentType type : SentimentType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException(INVALID_SENTIMENT_ERROR_MSG);
    }

}
