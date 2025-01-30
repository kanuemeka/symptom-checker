package com.healthily.symptomchecker.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthily.symptomchecker.domain.auth.UserLogin;
import com.healthily.symptomchecker.domain.auth.UserRegistration;
import com.healthily.symptomchecker.web.entities.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRegistration userRegistration;

    @MockBean
    private UserLogin userLogin;

    @Test
    public void shouldRegisterUserSuccessfully() throws Exception {
        Registration registration = Registration.builder()
                .email("email@email.com")
                .password("password")
                .age(21)
                .gender(Gender.MALE)
                .build();

        RegistrationResponse registrationResponse = RegistrationResponse.builder()
                .message("User registered successfully")
                .build();

        mvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registration)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(registrationResponse)));

    }

    @Test
    public void shouldLoginSuccessfully() throws Exception {

        String email = "email@email.com";
        String password = "password";

        Login login = Login.builder()
                .email(email)
                .password(password)
                .build();


        String userId = UUID.randomUUID().toString();
        LoginSuccess expected = LoginSuccess.builder().userId(userId).build();

        when(userLogin.loginUser(email, password)).thenReturn(userId);

        mvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }
}
