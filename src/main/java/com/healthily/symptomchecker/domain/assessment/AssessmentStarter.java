package com.healthily.symptomchecker.domain.assessment;

import com.healthily.symptomchecker.web.entities.Assessment;

import java.util.Set;

public interface AssessmentStarter {

    Assessment beginAssessment(String userId, Set<String> initialSymptoms);
}
