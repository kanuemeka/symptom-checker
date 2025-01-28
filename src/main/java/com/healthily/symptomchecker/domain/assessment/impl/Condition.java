package com.healthily.symptomchecker.domain.assessment.impl;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Condition {
    private String dataId;
    private Double prevalence;
}
