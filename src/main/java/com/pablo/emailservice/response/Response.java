package com.pablo.emailservice.response;


import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Response {

    private final String message;
    private final ResponseType status;

    public Response(final String message, final ResponseType status) {
        this.message = message;
        this.status = status;
    }
}
