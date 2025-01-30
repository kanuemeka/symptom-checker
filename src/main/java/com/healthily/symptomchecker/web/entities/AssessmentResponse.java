package com.healthily.symptomchecker.web.entities;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssessmentResponse {
    private String assessmentId;
    private String nextQuestionId;
}
