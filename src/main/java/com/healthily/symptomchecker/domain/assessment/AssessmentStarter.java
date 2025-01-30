package com.healthily.symptomchecker.domain.assessment;

import com.healthily.symptomchecker.web.entities.AssessmentResponse;

import java.util.Set;

public interface AssessmentStarter {

    AssessmentResponse beginAssessment(String userId, Set<String> initialSymptoms);
}
