package com.healthily.symptomchecker.web.entities;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Login {

    private String email;

    private String password;
}
