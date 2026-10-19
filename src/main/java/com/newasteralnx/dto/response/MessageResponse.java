package com.newasteralnx.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageResponse {

    private String message;
    /** Optional extra payload (e.g. demo reset OTP). Null in normal responses. */
    private String detail;

    public MessageResponse(String message) {
        this.message = message;
        this.detail = null;
    }
}
