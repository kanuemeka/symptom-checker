package com.healthily.symptomchecker.domain.auth.impl;

import com.healthily.symptomchecker.web.entities.Gender;
import com.healthily.symptomchecker.web.entities.Registration;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
public class AuthServiceTest {

    AuthService authService;

    @MockitoBean
    DynamoDbTemplate dynamoDbTemplate;

    @BeforeEach
    public void setUp() {
        authService = new AuthService(dynamoDbTemplate);
    }

    @Test
    public void shouldRegisterUser() {
        Registration registration = Registration.builder()
                .email("email@email.com")
                .password("password")
                .gender(Gender.MALE)
                .age(21)
                .build();

        authService.registerUser(registration);
    }
}
