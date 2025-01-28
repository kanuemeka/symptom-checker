package com.healthily.symptomchecker.web.entities;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Registration {

    private String email;

    private String password;

    private int age;

    private Gender gender;
}
