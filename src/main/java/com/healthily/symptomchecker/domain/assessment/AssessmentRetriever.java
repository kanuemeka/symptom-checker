package com.healthily.symptomchecker.domain.assessment;

import com.healthily.symptomchecker.data.repositories.AssessmentNotFound;
import com.healthily.symptomchecker.web.entities.AssessmentResults;

public interface AssessmentRetriever {
    AssessmentResults getResults(String assessmentId) throws AssessmentNotFound;
}
