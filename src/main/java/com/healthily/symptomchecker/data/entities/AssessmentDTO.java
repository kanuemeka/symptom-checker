package com.healthily.symptomchecker.data.entities;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamoDbBean
public class AssessmentDTO {

    private String assessmentId;

    private String userId;

    private Map<String, String> symptomMap;

    private int numberOfQuestionsAsked;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("assessmentId")
    public String getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(String assessmentId) {
        this.assessmentId = assessmentId;
    }

    @DynamoDbAttribute("userId")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @DynamoDbAttribute("symptomMap")
    public Map<String, String> getSymptomMap() {
        return symptomMap;
    }

    public void setSymptomMap(Map<String, String> symptomMap) {
        this.symptomMap = symptomMap;
    }

    @DynamoDbAttribute("numberOfQuestionsAsked")
    public int getNumberOfQuestionsAsked() {
        return numberOfQuestionsAsked;
    }

    public void setNumberOfQuestionsAsked(int numberOfQuestionsAsked) {
        this.numberOfQuestionsAsked = numberOfQuestionsAsked;
    }

    public void incrementNumberOfQuestionsAsked() {
        this.numberOfQuestionsAsked++;
    }

    public void addSymptom(String symptom, String answer) {
        this.symptomMap.put(symptom, answer);
    }
}
