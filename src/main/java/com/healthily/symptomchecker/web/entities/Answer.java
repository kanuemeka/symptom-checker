package com.healthily.symptomchecker.web.entities;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Answer {
    private String questionId;
    private AnswerOption answerOption;
}
