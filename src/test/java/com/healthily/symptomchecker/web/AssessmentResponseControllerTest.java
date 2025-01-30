package com.healthily.symptomchecker.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthily.symptomchecker.domain.assessment.AssessmentRetriever;
import com.healthily.symptomchecker.domain.assessment.AssessmentStarter;
import com.healthily.symptomchecker.domain.assessment.ResponseRecorder;
import com.healthily.symptomchecker.web.entities.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AssessmentResponseControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AssessmentStarter assessmentStarter;

    @MockBean
    private ResponseRecorder responseRecorder;

    @MockBean
    private AssessmentRetriever assessmentRetriever;

    @Test
    public void shouldStartAssessmentSuccessfully() throws Exception {

        String userId = UUID.randomUUID().toString();
        String assessmentId = UUID.randomUUID().toString();

        Set<String> initialSymptoms = Set.of("Fever", "Runny Nose");
        String nextQuestionId = "1";

        NewAssessment newAssessment = NewAssessment.builder()
                .userId(userId)
                .initialSymptoms(initialSymptoms)
                .build();

        AssessmentResponse assessmentResponse = AssessmentResponse.builder()
                .assessmentId(assessmentId)
                .nextQuestionId(nextQuestionId)
                .build();


        when(assessmentStarter.beginAssessment(userId, initialSymptoms)).thenReturn(assessmentResponse);

        mvc.perform(MockMvcRequestBuilders.post("/assessment/start")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAssessment)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(assessmentResponse)));
    }

    @Test
    public void shouldRecordAnswerWithFollowUp() throws Exception {
        String assessmentId = UUID.randomUUID().toString();
        String questionId = "1";
        String nextQuestionId = "2";
        Answer answer = Answer.builder()
                .questionId(questionId)
                .response(AnswerOption.Yes)
                .build();
        when(responseRecorder.recordResponse(assessmentId, questionId, AnswerOption.Yes.toString()))
                .thenReturn(nextQuestionId);

        mvc.perform(MockMvcRequestBuilders.post("/assessment/{assessmentId}/answer", assessmentId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answer)))
                .andExpect(status().isOk())
                .andExpect(content().string(nextQuestionId));
    }

    @Test
    public void shouldRecordAnswerWithNoFollowUp() throws Exception {
        String assessmentId = UUID.randomUUID().toString();
        String questionId = "1";
        Answer answer = Answer.builder()
                .questionId(questionId)
                .response(AnswerOption.Yes)
                .build();
        when(responseRecorder.recordResponse(assessmentId, questionId, AnswerOption.Yes.toString()))
                .thenReturn(null);

        mvc.perform(MockMvcRequestBuilders.post("/assessment/{assessmentId}/answer", assessmentId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answer)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldGetAssessment() throws Exception {
        String assessmentId = UUID.randomUUID().toString();

        AssessmentResults assessmentResults = AssessmentResults.builder()
                .condition("Hayfever")
                .probablities(
                        Map.of("COVID-19", 0.2)
                )
                .build();

        when(assessmentRetriever.getResults(assessmentId))
                .thenReturn(assessmentResults);

        mvc.perform(MockMvcRequestBuilders.get("/assessment/{assessmentId}/results", assessmentId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                        .andExpect(content().json(objectMapper.writeValueAsString(assessmentResults)));

    }
}
