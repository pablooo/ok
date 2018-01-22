package com.pablo.emailservice;

import com.pablo.emailservice.controller.v1.EmailServiceController;
import com.pablo.emailservice.model.EmailData;
import com.pablo.emailservice.provider.EmailServiceProvider;
import com.pablo.emailservice.response.Response;
import com.pablo.emailservice.response.ResponseType;
import com.pablo.emailservice.service.EmailService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;

@RunWith(SpringRunner.class)
@WebMvcTest(EmailServiceController.class)
public class EmailServiceTests {

    @TestConfiguration
    static class EmployeeServiceImplTestContextConfiguration {

        @Bean
        public EmailService emailService() {
            return new EmailService();
        }
    }

    @Autowired
    public EmailService emailService;

    @MockBean(name = "provider1")
    private EmailServiceProvider emailServiceProvider1;

    @MockBean(name = "provider2")
    private EmailServiceProvider emailServiceProvider2;

    @Before
    public void setUp() {
        Mockito.when(emailServiceProvider1.sendEmail(any(EmailData.class)))
                .thenReturn(new com.pablo.emailservice.response.Response("ERROR", ResponseType.FAIL));
        Mockito.when(emailServiceProvider2.sendEmail(any(EmailData.class)))
                .thenReturn(new com.pablo.emailservice.response.Response("SUCCESS", ResponseType.SUCCESS));
    }

    @Test
    public void whenOneProviderFailSecondShouldTrySend() {
        //given
        final String from = "test@gmail.com";
        final String to = "zorro@gmail.com";
        final String cc = "ala@gmail.com";
        final String bcc = "kot@gmail.com";
        final String subject = "topic";
        final String body = "some text";
        EmailData emailData = new EmailData(from, to, cc, bcc, subject, body);

        //when
        Response response = emailService.send(emailData);

        //then
        assertThat(response.getStatus(), equalTo(ResponseType.SUCCESS));
    }
}

