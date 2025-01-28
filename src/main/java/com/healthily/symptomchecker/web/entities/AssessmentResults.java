package com.healthily.symptomchecker.web.entities;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class AssessmentResults {
    private String condition;
    private Map<String, Double> probablities;
}
