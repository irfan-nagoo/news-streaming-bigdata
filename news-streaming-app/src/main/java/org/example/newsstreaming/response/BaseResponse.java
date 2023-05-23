package org.example.newsstreaming.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author irfan.nagoo
 */

@AllArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BaseResponse {

    private final String status;
    private final String message;
}
