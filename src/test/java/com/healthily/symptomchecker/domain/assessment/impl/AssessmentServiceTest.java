package com.healthily.symptomchecker.domain.assessment.impl;

import com.healthily.symptomchecker.data.entities.Assessment;
import com.healthily.symptomchecker.web.entities.AssessmentResults;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AssessmentServiceTest {

    AssessmentService assessmentService;

    ConditionData conditionData = new ConditionData();

    @MockitoBean
    DynamoDbTemplate dynamoDbTemplate;

    @BeforeEach
    public void setUp() {
        assessmentService = new AssessmentService(conditionData, dynamoDbTemplate);
    }

    @Test
    public void shouldRecordAnswerAndGetNextQuestion() {
        String assessmentId = UUID.randomUUID().toString();

        Map<String, String> symptomsMap = new HashMap<>();
        symptomsMap.put("Sneezing", "Yes");
        Map<String, String> updatedMap = new HashMap<>(symptomsMap);
        updatedMap.put("Cough", "Yes");

        Assessment assessment = Assessment.builder()
                .userId("userId")
                .assessmentId(assessmentId)
                .symptomMap(symptomsMap)
                .build();

        Assessment updatedDTO = Assessment.builder()
                .userId("userId")
                .assessmentId(assessmentId)
                .numberOfQuestionsAsked(1)
                .symptomMap(updatedMap)
                .build();
        when(dynamoDbTemplate.load(any(Key.class), any())).thenReturn(assessment);
        when(dynamoDbTemplate.update(any())).thenReturn(updatedDTO);

        String question = "Cough";
        String answer = "Yes";

        String nextQuestion = assessmentService.recordResponse(assessmentId, question, answer);
        assertNotNull(nextQuestion);
        assertFalse(nextQuestion.isEmpty());
    }

    @Test
    public void shouldRecordAnswerAndGetNoMoreQuestions() {
        String assessmentId = UUID.randomUUID().toString();

        Map<String, String> symptomsMap = new HashMap<>();
        symptomsMap.put("Sneezing", "Yes");
        symptomsMap.put("Fatigue", "Yes");
        symptomsMap.put("Fever", "Yes");

        Map<String, String> updatedMap = new HashMap<>(symptomsMap);
        updatedMap.put("Cough", "Yes");

        Assessment assessment = Assessment.builder()
                .userId("userId")
                .assessmentId(assessmentId)
                .numberOfQuestionsAsked(3)
                .symptomMap(symptomsMap)
                .build();

        Assessment updatedDTO = Assessment.builder()
                .userId("userId")
                .assessmentId(assessmentId)
                .numberOfQuestionsAsked(3)
                .symptomMap(updatedMap)
                .build();
        when(dynamoDbTemplate.load(any(Key.class), any())).thenReturn(assessment);
        when(dynamoDbTemplate.update(any())).thenReturn(updatedDTO);

        String question = "Cough";
        String answer = "Yes";

        String nextQuestion = assessmentService.recordResponse(assessmentId, question, answer);
        assertNull(nextQuestion);
    }

    @Test
    public void shouldCalculateResultsOfSingleSymptom() {

        String assessmentId = UUID.randomUUID().toString();

        Assessment assessment = Assessment.builder()
                .assessmentId(assessmentId)
                .symptomMap(Map.of("Sneezing", "Yes"))
                .build();

        when(dynamoDbTemplate.load(any(Key.class), any())).thenReturn(assessment);
        AssessmentResults results = assessmentService.getResults(assessmentId);

        assertNotNull(results);
        assertEquals("Common Cold", results.getCondition());
    }

    @Test
    public void shouldCalculateResultsOfMultipleSymptoms() {

        String assessmentId = UUID.randomUUID().toString();

        Map<String, String>symtomsMap = new HashMap<>();
        symtomsMap.put("Sneezing", "Yes");
        symtomsMap.put("Runny nose", "Yes");
        symtomsMap.put("Nasal congestion", "No");
        symtomsMap.put("Cough", "No");

        Assessment assessment = Assessment.builder()
                .assessmentId(assessmentId)
                .symptomMap(symtomsMap)
                .build();

        when(dynamoDbTemplate.load(any(Key.class), any())).thenReturn(assessment);
        AssessmentResults results = assessmentService.getResults(assessmentId);

        assertNotNull(results);
        assertEquals("Common Cold", results.getCondition());
    }
}
