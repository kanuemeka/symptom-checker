package com.healthily.symptomchecker.domain.assessment.impl;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class Symptom {
    private String dataId;
    private Map<String, Double> conditionProbabilities;

    public void addConditionProbability(String dataId, double probability) {
        if (conditionProbabilities == null) {
            conditionProbabilities = new HashMap<>();
        }
        conditionProbabilities.put(dataId, probability);
    }
}
