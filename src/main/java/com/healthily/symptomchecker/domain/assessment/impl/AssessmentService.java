package com.healthily.symptomchecker.domain.assessment.impl;

import com.healthily.symptomchecker.data.entities.AssessmentDTO;
import com.healthily.symptomchecker.data.repositories.AssessmentNotFound;
import com.healthily.symptomchecker.domain.assessment.AssessmentRetriever;
import com.healthily.symptomchecker.domain.assessment.AssessmentStarter;
import com.healthily.symptomchecker.domain.assessment.ResponseRecorder;
import com.healthily.symptomchecker.web.entities.Assessment;
import com.healthily.symptomchecker.web.entities.AssessmentResults;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssessmentService implements AssessmentStarter, ResponseRecorder, AssessmentRetriever {

    private final ConditionData conditionData;

    private final DynamoDbTemplate dynamoDbTemplate;

    @Override
    public Assessment beginAssessment(String userId, Set<String> initialSymptoms) {

        AssessmentDTO assessmentDTO = AssessmentDTO.builder()
                .userId(userId)
                .symptomMap(initialSymptoms.stream().collect(Collectors.toMap(
                        x -> x, x -> "Yes"
                )))
                .build();

        AssessmentDTO result = dynamoDbTemplate.save(assessmentDTO);

        List<Symptom> nextSymptomQuestions = getSymptomsNotInSet(initialSymptoms);

        Assessment.AssessmentBuilder assessmentBuilder = Assessment.builder();
        assessmentBuilder = assessmentBuilder.assessmentId(result.getAssessmentId());

        if(!nextSymptomQuestions.isEmpty()) {
            assessmentBuilder = assessmentBuilder.nextQuestionId(nextSymptomQuestions.get(0).getDataId());
        }

        return assessmentBuilder.build();
    }

    @Override
    public String recordResponse(String assessmentId, String questionId, String answer) throws AssessmentNotFound {

        Key key = Key.builder().partitionValue(assessmentId).build();
        AssessmentDTO assessmentDTO = dynamoDbTemplate.load(key, AssessmentDTO.class);

        if(assessmentDTO == null) {
            throw new AssessmentNotFound();
        }

        Map<String, String>symptomMap = assessmentDTO.getSymptomMap();
        Map<String, String>updatedMap = new HashMap<>();

        if(symptomMap!=null){
            updatedMap.putAll(symptomMap);
        }
        updatedMap.put(questionId, answer);
        assessmentDTO.setSymptomMap(updatedMap);

        assessmentDTO.incrementNumberOfQuestionsAsked();
        AssessmentDTO updated = dynamoDbTemplate.update(assessmentDTO);

        if(updated.getNumberOfQuestionsAsked()<3) {
            List<Symptom> nextSymptomQuestions = getSymptomsNotInSet(updated.getSymptomMap().keySet());
            if (!nextSymptomQuestions.isEmpty()) {
                return nextSymptomQuestions.get(0).getDataId();
            }
        }
        return null;
    }

    @Override
    public AssessmentResults getResults(String assessmentId) throws AssessmentNotFound {
        Key key = Key.builder().partitionValue(assessmentId).build();
        AssessmentDTO assessmentDTO = dynamoDbTemplate.load(key, AssessmentDTO.class);

        if(assessmentDTO == null) {
            throw new AssessmentNotFound();
        }

        //Get All Conditions cos we will be calculating probabilities for all of them
        List<Condition>conditions = conditionData.getConditions();

        //Get Symptoms our users answered 'Yes' to
        List<String> validSymptoms = new ArrayList<>();
        for (String validKey : assessmentDTO.getSymptomMap().keySet()) {
            if (assessmentDTO.getSymptomMap().get(validKey).equals("Yes")) {
                validSymptoms.add(validKey);
            }
        }

        List<Symptom>symptoms = conditionData.getSymptoms().stream().filter(
                symptom -> validSymptoms.contains(symptom.getDataId())).toList();

        Map<String, Double>conditionsUnitMap = new HashMap<>();
        double totalOfAllConditions = 0;
        for (Condition condition : conditions) {
            double conditionUnitPercentage = condition.getPrevalence() * 100;

            double totalSymptomsProbPerCondition = 0;
            for (Symptom symptom : symptoms) {
                Double conditionProbAndSymptom = symptom.getConditionProbabilities().get(condition.getDataId());
                double symptomProbabilityInCondition = conditionProbAndSymptom * conditionUnitPercentage;
                totalSymptomsProbPerCondition += symptomProbabilityInCondition;
            }
            conditionsUnitMap.put(condition.getDataId(), totalSymptomsProbPerCondition);
            totalOfAllConditions += totalSymptomsProbPerCondition;
        }

        Map<String, Double> conditionsProbabilities = new HashMap<>();
        for(String conditionKey : conditionsUnitMap.keySet()) {
            double value = conditionsUnitMap.get(conditionKey);
            double probability = value / totalOfAllConditions;
            conditionsProbabilities.put(conditionKey, round(probability, 2));
        }

        String condition = Collections.max(conditionsProbabilities.entrySet(), Map.Entry.comparingByValue()).getKey();

        return AssessmentResults.builder()
                .condition(condition)
                .probablities(conditionsProbabilities)
                .build();
    }

    private List<Symptom> getSymptomsNotInSet(Set<String> queriedSymptomIds) {

        return conditionData.getSymptoms().stream().filter(
                symptom -> !queriedSymptomIds.contains(symptom.getDataId())
        ).collect(Collectors.toList());
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
