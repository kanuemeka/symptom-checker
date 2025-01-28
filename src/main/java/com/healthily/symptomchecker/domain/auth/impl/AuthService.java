package com.healthily.symptomchecker.domain.auth.impl;

import com.healthily.symptomchecker.data.entities.UserDTO;
import com.healthily.symptomchecker.domain.auth.UserLogin;
import com.healthily.symptomchecker.domain.auth.UserRegistration;
import com.healthily.symptomchecker.web.entities.Registration;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService implements UserRegistration, UserLogin {

    private final DynamoDbTemplate dynamoDbTemplate;

    @Override
    public void registerUser(Registration registration) {
        UserDTO user = UserDTO.builder()
                .email(registration.getEmail())
                .password(registration.getPassword())
                .build();

        dynamoDbTemplate.save(user);
    }

    @Override
    public String loginUser(String email, String password) {
        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":val1", AttributeValue.fromS(email));
        expressionValues.put(":val2", AttributeValue.fromS(password));

        Expression filterExpression = Expression.builder()
                .expression("email = :val1")
                .expression("password = :val2")
                .expressionValues(expressionValues)
                .build();

        ScanEnhancedRequest scanEnhancedRequest = ScanEnhancedRequest.builder()
                .filterExpression(filterExpression).build();
        PageIterable<UserDTO> returnedList = dynamoDbTemplate.scan(scanEnhancedRequest, UserDTO.class);

        List<UserDTO> users = returnedList.items().stream().toList();

        if (!users.isEmpty()) {
            return users.get(0).getUserId();
        }
        return null;
    }
}
