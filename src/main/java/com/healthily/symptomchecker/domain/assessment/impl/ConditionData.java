package com.healthily.symptomchecker.domain.assessment.impl;

import lombok.Getter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;

@Getter
@Component
public class ConditionData {

    private final List<Condition> conditions = new ArrayList<>();

    private final List<Symptom> symptoms = new ArrayList<>();

    public ConditionData() {
        try {
            InputStream conditionsInputStream = new ClassPathResource("conditions_data.csv").getInputStream();
            InputStream symptomsInputStream = new ClassPathResource("symptoms_data.csv").getInputStream();

            CSVParser conditionsParser = CSVParser.parse(conditionsInputStream, Charset.defaultCharset(),
                    CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
            CSVParser symptomsParser = CSVParser.parse(symptomsInputStream, Charset.defaultCharset(),
                    CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

            Iterable<CSVRecord> conditionsRecords = conditionsParser.getRecords();

            for (CSVRecord csvRecord : conditionsRecords) {
                Condition condition = new Condition(
                        csvRecord.get("data_id"),
                        Double.parseDouble(csvRecord.get("prevalence"))
                );

                conditions.add(condition);
            }

            Iterable<CSVRecord> symptomsRecords = symptomsParser.getRecords();

            for (CSVRecord csvRecord : symptomsRecords) {
                String dataId = csvRecord.get("data_id");
                Map<String, Double> conditionsProbablities = new HashMap<>();
                conditionsProbablities.put("Hayfever", Double.parseDouble(csvRecord.get("Hayfever")));
                conditionsProbablities.put("COVID-19", Double.parseDouble(csvRecord.get("COVID-19")));
                conditionsProbablities.put("Common Cold", Double.parseDouble(csvRecord.get("Common Cold")));

                Symptom symptom = new Symptom(dataId, conditionsProbablities);

                symptoms.add(symptom);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Unable to find required resource file" + e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
