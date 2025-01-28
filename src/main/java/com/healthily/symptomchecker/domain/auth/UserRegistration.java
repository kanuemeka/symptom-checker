package com.healthily.symptomchecker.domain.auth;

import com.healthily.symptomchecker.web.entities.Registration;

@FunctionalInterface
public interface UserRegistration {

    void registerUser(Registration registration);
}
