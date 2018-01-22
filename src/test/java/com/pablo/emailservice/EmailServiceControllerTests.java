package com.pablo.emailservice;

import com.pablo.emailservice.controller.v1.EmailServiceController;
import com.pablo.emailservice.model.EmailData;
import com.pablo.emailservice.service.EmailService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BindingResult;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest(EmailServiceController.class)
public class EmailServiceControllerTests {

    @Autowired
    EmailServiceController emailServiceController;

    @MockBean
    public EmailService emailService;

    @Test
    public void dontSendInvalidEmailWhenTheBindingResultHasErrors() {
        //given
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        //when
        final String wrongFrom = "ala1@gmail.com";
        final String to = "zorro@gmail.com";
        final String cc = "ala@gmail.com";
        final String bcc = "kot@gmail.com";
        final String subject = "topic";
        final String body = "some text";
        EmailData emailData = new EmailData(wrongFrom, to, cc, bcc, subject, body);
        emailServiceController.sendEmail(emailData, bindingResult);

        //then
        verifyZeroInteractions(emailService);
    }
}

