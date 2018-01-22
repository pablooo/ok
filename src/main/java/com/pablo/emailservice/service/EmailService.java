package com.pablo.emailservice.service;

import com.pablo.emailservice.model.EmailData;
import com.pablo.emailservice.provider.EmailServiceProvider;
import com.pablo.emailservice.response.Response;
import com.pablo.emailservice.response.ResponseType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Getter
@Slf4j
public class EmailService {

    @Autowired
    private List<EmailServiceProvider> providers;

    public Response send(final EmailData emailData) {

        for (EmailServiceProvider provider : providers) {
            final Response response = provider.sendEmail(emailData);
            if (response.getStatus() == ResponseType.SUCCESS) {
                return new Response(response.getMessage(), ResponseType.SUCCESS);
            }
        }

        log.error("Error - All providers failed");
        return new Response("Error - All providers failed", ResponseType.FAIL);
    }

    public EmailService() {
    }

    public EmailService(List<EmailServiceProvider> providers) {
        this.providers = providers;
    }
}
