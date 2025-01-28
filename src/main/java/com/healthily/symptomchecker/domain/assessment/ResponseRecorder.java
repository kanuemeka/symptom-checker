package com.healthily.symptomchecker.domain.assessment;

import com.healthily.symptomchecker.data.repositories.AssessmentNotFound;

public interface ResponseRecorder {
    String recordResponse(String assessmentId, String questionId, String answer) throws AssessmentNotFound;
}
