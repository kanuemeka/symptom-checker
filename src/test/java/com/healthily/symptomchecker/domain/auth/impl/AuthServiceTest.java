package com.healthily.symptomchecker.domain.auth.impl;

import com.healthily.symptomchecker.data.entities.User;
import com.healthily.symptomchecker.web.entities.Gender;
import com.healthily.symptomchecker.web.entities.Registration;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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

//    @Test
//    public void shouldLoginUser() {
//        String userId = UUID.randomUUID().toString();
//        String email = "email@email.com";
//        String password = "password";
//
//        User user = User.builder()
//                .userId(userId)
//                .email(email)
//                .password(password)
//                .age(21)
//                .gender("Male")
//                .build();
//
//        when(dynamoDbTemplate.load(any(Key.class), any())).thenReturn(user);
//        String accessToken = authService.loginUser(email, password);
//
//        assertNotNull(accessToken);
//        assertEquals(userId, accessToken);
//    }
}
