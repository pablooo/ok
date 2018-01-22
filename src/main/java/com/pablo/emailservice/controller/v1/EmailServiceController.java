package com.pablo.emailservice.controller.v1;


import com.pablo.emailservice.model.EmailData;
import com.pablo.emailservice.response.Response;
import com.pablo.emailservice.response.ResponseType;
import com.pablo.emailservice.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Slf4j
public class EmailServiceController {

    public static final String API_VERSION = "application/vnd.pablo.api.v1+json";

    @Autowired
    private EmailService emailService;

    @ResponseBody
    @PostMapping(value = "/sendEmail", produces = API_VERSION, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> sendEmail(@RequestBody @Valid final EmailData emailData,
                                              final BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            final Response errorResponse = getErrorResponse(bindingResult);
            log.debug(String.format("%s has validation errors: %s", emailData, errorResponse.getMessage()));
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_ACCEPTABLE);
        }

        final Response response = emailService.send(emailData);
        if (response.getStatus() == ResponseType.SUCCESS) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Response getErrorResponse(BindingResult bindingResult) {
        if (bindingResult.getAllErrors().size() > 0) {
            final ObjectError error = bindingResult.getAllErrors().get(0);
            return new Response(String.format("Validation error %s: %s", error.getObjectName(),
                    error.getDefaultMessage()), ResponseType.FAIL);
        } else {
            return new Response(String.format("Validation error"), ResponseType.FAIL);
        }
    }
}
