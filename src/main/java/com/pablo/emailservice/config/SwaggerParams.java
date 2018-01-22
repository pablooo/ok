package com.pablo.emailservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "swagger")
public class SwaggerParams {

    private String basePackage;
    private String title;
    private String description;
    private String version;
    private String termsOfServiceUrl;
    private String contactName;
    private String contactUrl;
    private String contactEmail;
    private String license;
    private String licenseUrl;
    private boolean enabled = true;
}
