package com.pablo.emailservice.provider;

import com.pablo.emailservice.model.EmailData;
import com.pablo.emailservice.response.Response;
import com.pablo.emailservice.response.ResponseType;
import com.pablo.emailservice.validator.Validator;
import com.sendgrid.*;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
@Setter
@Qualifier("sendGrid")
@ConfigurationProperties(prefix = "sendGrid")
public class SendGridProvider implements EmailServiceProvider {

    /**
     * API key come from environment variables.
     */
    private String apiKey;
    private String successMsg;
    private String failMsg;
    private String failApiKeyMsg;

    public com.pablo.emailservice.response.Response sendEmail(final EmailData email) {
        log.info("Sending email...");
        if (!Validator.isValid(apiKey)) {
            log.error(failApiKeyMsg);
            return new com.pablo.emailservice.response.Response(failApiKeyMsg, ResponseType.FAIL);
        }

        final Map<String, Object> requestParamMap = getRequestParams(email);
        final SendGrid sg = new SendGrid(apiKey);
        final Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            final Mail mail = (Mail) requestParamMap.get("mail");
            request.setBody(mail.build());
            final com.sendgrid.Response response = sg.api(request);

            if (response.getStatusCode() == HttpStatus.ACCEPTED.value() ||
                    response.getStatusCode() == HttpStatus.OK.value()) {
                log.info(successMsg);
                return new com.pablo.emailservice.response.Response(successMsg, ResponseType.SUCCESS);
            }
            log.error(String.format("Code: %s, message: %s", response.getBody(), response.getBody()));
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            return new com.pablo.emailservice.response.Response(failMsg, ResponseType.FAIL);
        }

        return new Response(failMsg, ResponseType.FAIL);
    }

    public Map<String, Object> getRequestParams(final EmailData email) {

        final Map<String, Object> result = new HashMap<>();
        final Mail mail = new Mail();
        mail.setFrom(new Email(email.getFrom()));
        mail.setSubject(email.getSubject());

        mail.addContent(new Content(MediaType.TEXT_PLAIN_VALUE, email.getBody()));
        final Personalization personalization = new Personalization();
        personalization.addTo(new Email(email.getTo()));
        if (StringUtils.isNotBlank(email.getCc())) {
            personalization.addCc(new Email(email.getCc()));
        }
        if (StringUtils.isNotBlank(email.getBcc())) {
            personalization.addBcc(new Email(email.getBcc()));
        }

        mail.addPersonalization(personalization);
        result.put("mail", mail);
        return result;
    }
}
