package com.pablo.emailservice.model;


import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;


@Data
public class EmailData {

    @Email(message = "field 'from' have incorrect email")
    @NotBlank(message = "field 'from' is empty")
    private String from;

    @Email(message = "field 'to' have incorrect email")
    @NotBlank(message = "field 'to' is empty")
    private String to;

    /**
     * Carbon copy
     */
    @Email(message = "field 'cc' have incorrect email")
    private String cc;

    /**
     * Blind carbon copy
     */
    @Email(message = "field 'bcc' have incorrect email")
    private String bcc;

    @NotBlank(message = "field 'subject' is empty")
    private String subject;

    @NotBlank(message = "field 'body' is empty")
    private String body;

    public EmailData() {
    }

    public EmailData(String from, String to, String cc, String bcc, String subject, String body) {
        this.from = from;
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.subject = subject;
        this.body = body;
    }
}
