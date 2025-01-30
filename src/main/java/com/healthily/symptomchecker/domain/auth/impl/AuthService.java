package com.healthily.symptomchecker.domain.auth.impl;

import com.healthily.symptomchecker.data.entities.User;
import com.healthily.symptomchecker.data.repositories.AuthError;
import com.healthily.symptomchecker.domain.auth.UserLogin;
import com.healthily.symptomchecker.domain.auth.UserRegistration;
import com.healthily.symptomchecker.web.entities.Registration;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService implements UserRegistration, UserLogin {

    private final DynamoDbTemplate dynamoDbTemplate;

    @Override
    public void registerUser(Registration registration) {
        User user = User.builder()
                .userId(UUID.randomUUID().toString())
                .email(registration.getEmail())
                .password(registration.getPassword())
                .age(registration.getAge())
                .gender(registration.getGender().toString())
                .build();

        dynamoDbTemplate.save(user);
    }

    @Override
    public String loginUser(String email, String password) {
        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":val1", AttributeValue.fromS(email));
        expressionValues.put(":val2", AttributeValue.fromS(password));

        Expression filterExpression = Expression.builder()
                .expression("email = :val1 and password = :val2")
                .expressionValues(expressionValues)
                .build();

        ScanEnhancedRequest scanEnhancedRequest = ScanEnhancedRequest.builder()
                .filterExpression(filterExpression).build();
        PageIterable<User> returnedList = dynamoDbTemplate.scan(scanEnhancedRequest, User.class);

        List<User> users = returnedList.items().stream().toList();

        if (users.isEmpty())
            throw new AuthError();

        return users.get(0).getUserId();
    }
}
