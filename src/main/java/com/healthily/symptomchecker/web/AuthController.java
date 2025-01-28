package com.healthily.symptomchecker.web;

import com.healthily.symptomchecker.domain.auth.UserLogin;
import com.healthily.symptomchecker.domain.auth.UserRegistration;
import com.healthily.symptomchecker.web.entities.Login;
import com.healthily.symptomchecker.web.entities.Registration;
import com.healthily.symptomchecker.web.entities.RegistrationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserRegistration userRegistration;

    private final UserLogin userLogin;

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> registerUser(@Valid @RequestBody Registration registration) {
        userRegistration.registerUser(registration);

        RegistrationResponse registrationResponse = RegistrationResponse.builder()
                .message("User registered successfully")
                .build();
        return ResponseEntity.ok(registrationResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@Valid @RequestBody Login login) {
        String userId = userLogin.loginUser(login.getEmail(), login.getPassword());
        return ResponseEntity.ok(userId);
    }
}
