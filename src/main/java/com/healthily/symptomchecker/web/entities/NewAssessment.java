package com.healthily.symptomchecker.web.entities;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class NewAssessment {
    private String userId;
    private Set<String> initialSymptoms;
}
