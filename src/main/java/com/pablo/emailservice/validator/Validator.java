package com.pablo.emailservice.validator;

import org.apache.commons.lang3.StringUtils;

public class Validator {

    public static boolean isValid(String apiKey) {
        if (StringUtils.isBlank(apiKey)) {
            return false;
        }
        return true;
    }
}
