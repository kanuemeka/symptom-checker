package com.healthily.symptomchecker.web;

import com.healthily.symptomchecker.domain.assessment.AssessmentRetriever;
import com.healthily.symptomchecker.domain.assessment.AssessmentStarter;
import com.healthily.symptomchecker.domain.assessment.ResponseRecorder;
import com.healthily.symptomchecker.web.entities.Answer;
import com.healthily.symptomchecker.web.entities.AssessmentResponse;
import com.healthily.symptomchecker.web.entities.AssessmentResults;
import com.healthily.symptomchecker.web.entities.NewAssessment;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/assessment")
public class AssessmentController {

    private final AssessmentStarter assessmentStarter;

    private final ResponseRecorder responseRecorder;

    private final AssessmentRetriever assessmentRetriever;

    @PostMapping("/start")
    public ResponseEntity<AssessmentResponse>startAssessment(@Valid @RequestBody NewAssessment newAssessment) {
        AssessmentResponse assessmentResponse = assessmentStarter.beginAssessment(newAssessment.getUserId(), newAssessment.getInitialSymptoms());
        return ResponseEntity.ok(assessmentResponse);
    }

    @PostMapping("/{assessmentId}/answer")
    public ResponseEntity<String> answerQuestion(@PathVariable(name = "assessmentId") String assessmentId, @Valid @RequestBody Answer answer){
        String nextQuestionId = responseRecorder.recordResponse(
                assessmentId, answer.getQuestionId(), answer.getResponse().toString());

        if(nextQuestionId == null){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(nextQuestionId);
    }

    @GetMapping("{assessmentId}/results")
    public ResponseEntity<AssessmentResults> getAssessmentResults(
            @PathVariable(name = "assessmentId") String assessmentId){
        AssessmentResults assessmentResults = assessmentRetriever.getResults(assessmentId);
        return ResponseEntity.ok(assessmentResults);
    }
}
