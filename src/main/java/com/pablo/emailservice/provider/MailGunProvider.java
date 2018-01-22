package com.pablo.emailservice.provider;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.pablo.emailservice.model.EmailData;
import com.pablo.emailservice.response.Response;
import com.pablo.emailservice.response.ResponseType;
import com.pablo.emailservice.validator.Validator;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
@Setter
@Qualifier("mailGun")
@ConfigurationProperties(prefix = "mailGun")
public class MailGunProvider implements EmailServiceProvider {

    private String urlTemplate;
    private String domain;
    /**
     * API key come from environment variables.
     */
    private String apiKey;
    private String successMsg;
    private String failMsg;
    private String failApiKeyMsg;

    public Response sendEmail(final EmailData email) {
        log.info("Sending email...");
        if (!Validator.isValid(apiKey)) {
            log.error(failApiKeyMsg);
            return new com.pablo.emailservice.response.Response(failApiKeyMsg, ResponseType.FAIL);
        }

        try {
            final HttpResponse<JsonNode> jsonResponse =
                    Unirest.post(String.format(urlTemplate, domain))
                            .header("accept", "application/json")
                            .basicAuth("api", apiKey)
                            .queryString(getRequestParams(email)).asJson();

            if (jsonResponse.getStatus() == HttpStatus.ACCEPTED.value() ||
                    jsonResponse.getStatus() == HttpStatus.OK.value()) {
                log.info(successMsg);
                return new Response(successMsg, ResponseType.SUCCESS);
            }

            log.error(String.format("Code: %s, message: %s", jsonResponse.getStatus(), jsonResponse.getBody()));
            return new Response(failMsg, ResponseType.FAIL);
        } catch (UnirestException e) {
            log.error(e.getMessage(), e);
        }

        return new Response(failMsg, ResponseType.FAIL);
    }

    public Map<String, Object> getRequestParams(final EmailData email) {
        final Map<String, Object> result = new HashMap<>();
        result.put("from", email.getFrom());
        result.put("to", email.getTo());
        result.put("subject", email.getSubject());
        result.put("text", email.getBody());
        if (StringUtils.isNotBlank(email.getCc())) {
            result.put("cc", email.getCc());
        }
        if (StringUtils.isNotBlank(email.getBcc())) {
            result.put("bcc", email.getBcc());
        }

        return result;
    }
}
