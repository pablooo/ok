package com.pablo.emailservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pablo.emailservice.controller.v1.EmailServiceController;
import com.pablo.emailservice.model.EmailData;
import com.pablo.emailservice.response.Response;
import com.pablo.emailservice.response.ResponseType;
import com.pablo.emailservice.service.EmailService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(EmailServiceController.class)
public class EmailDataValidationTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    public EmailService emailService;

    @Before
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
        when(emailService.send(any(EmailData.class))).thenReturn(new Response("OK", ResponseType.SUCCESS));
    }

    @Test
    public void checkIfSendingEmailIsProtected() throws Exception {
        // given
        // No user logged

        // when
        MockHttpServletResponse response = mvc.perform(
                post("/sendEmail")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus(), equalTo(HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    @WithMockUser(username = "user", password = "test678")
    public void checkSendingEmailByLoggedUserWithoutEmailData() throws Exception {
        // given
        // Logged user (see @WithMockUser annotation)

        // when
        ResultActions resultActions = mvc.perform(post("/sendEmail")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("")
                .accept(EmailServiceController.API_VERSION));

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", password = "test678")
    public void checkSendingEmailByLoggedUserWithWrongToEmailField() throws Exception {
        // given
        // Logged user (see @WithMockUser annotation)
        ObjectMapper mapper = new ObjectMapper();
        EmailData emailData = new EmailData("test@mailinator.com", "mailinator.com", "", "", "topic", "some body");
        String emailDataJson = mapper.writeValueAsString(emailData);
        String expectedError = "{\"message\":\"Validation error emailData: field 'to' have incorrect email\",\"status\":\"FAIL\"}";

        //when
        ResultActions resultActions = mvc.perform(post("/sendEmail")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(emailDataJson)
                .accept(EmailServiceController.API_VERSION));

        //then
        resultActions
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(expectedError));
    }

    @Test
    @WithMockUser(username = "user", password = "test678")
    public void checkGettingApiByLoggedUserWithEmailData() throws Exception {
        // given
        // Logged user (see @WithMockUser annotation)
        EmailData emailData = new EmailData("test@mailinator.com", "to@mailinator.com", "", "", "topic", "some body");
        ObjectMapper mapper = new ObjectMapper();
        String emailDataJson = mapper.writeValueAsString(emailData);

        //when
        ResultActions resultActions = mvc.perform(post("/sendEmail").contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(emailDataJson)
                .accept(EmailServiceController.API_VERSION));

        //then
        resultActions.andExpect(status().isOk());
    }
}