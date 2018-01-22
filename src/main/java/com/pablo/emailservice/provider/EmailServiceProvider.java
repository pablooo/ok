package com.pablo.emailservice.provider;

import com.pablo.emailservice.model.EmailData;
import com.pablo.emailservice.response.Response;

import java.util.Map;

public interface EmailServiceProvider {

    /**
     * Send email
     *
     * @param email
     * @return {@link Response}
     */
    Response sendEmail(EmailData email);

    /**
     * Get specific for provider request params map generated using EmailData object.
     *
     * @param email
     * @return specific for provider request params map
     */
    Map<String, Object> getRequestParams(final EmailData email);
}
