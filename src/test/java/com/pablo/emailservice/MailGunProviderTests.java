package com.pablo.emailservice;


import com.pablo.emailservice.model.EmailData;
import com.pablo.emailservice.provider.MailGunProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EmailServiceApplication.class)
public class MailGunProviderTests {

    @Autowired
    MailGunProvider mailGunProvider;

    @Test
    public void checkIfAllParamsAreMapped() {
        // given
        final String from = "test@gmail.com";
        final String to = "zorro@gmail.com";
        final String cc = "ala@gmail.com";
        final String bcc = "kot@gmail.com";
        final String subject = "topic";
        final String body = "some text";
        EmailData emailData = new EmailData(from, to, cc, bcc, subject, body);

        // when
        Map<String, Object> requestParams = mailGunProvider.getRequestParams(emailData);

        // then
        assertThat(requestParams.isEmpty(), equalTo(false));
        assertThat(requestParams.size(), equalTo(6));
        assertThat(requestParams.get("from"), equalTo(from));
        assertThat(requestParams.get("to"), equalTo(to));
        assertThat(requestParams.get("subject"), equalTo(subject));
        assertThat(requestParams.get("text"), equalTo(body));
        assertThat(requestParams.get("cc"), equalTo(cc));
        assertThat(requestParams.get("bcc"), equalTo(bcc));
    }

    @Test
    public void checkIfEmptyCcandBccParamsAreNotInParamMap() {
        // given
        final String from = "1@gmail.com";
        final String to = "2@gmail.com";
        final String cc = null;
        final String bcc = "";
        final String subject = "topic";
        final String body = "text123";
        EmailData emailData = new EmailData(from, to, cc, bcc, subject, body);

        // when
        Map<String, Object> requestParams = mailGunProvider.getRequestParams(emailData);

        // then
        assertThat(requestParams.isEmpty(), equalTo(false));
        assertThat(requestParams.size(), equalTo(4));
        assertThat(requestParams.get("from"), equalTo(from));
        assertThat(requestParams.get("to"), equalTo(to));
        assertThat(requestParams.get("subject"), equalTo(subject));
        assertThat(requestParams.get("text"), equalTo(body));
        assertThat(requestParams.containsKey("cc"), equalTo(false));
        assertThat(requestParams.containsKey("bcc"), equalTo(false));
    }
}