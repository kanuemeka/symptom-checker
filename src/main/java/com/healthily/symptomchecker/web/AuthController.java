package com.healthily.symptomchecker.web;

import com.healthily.symptomchecker.domain.auth.UserLogin;
import com.healthily.symptomchecker.domain.auth.UserRegistration;
import com.healthily.symptomchecker.web.entities.Login;
import com.healthily.symptomchecker.web.entities.LoginSuccess;
import com.healthily.symptomchecker.web.entities.Registration;
import com.healthily.symptomchecker.web.entities.RegistrationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserRegistration userRegistration;

    private final UserLogin userLogin;

    private final Logger logger = Logger.getLogger(AuthController.class.getName());


    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> registerUser(@Valid @RequestBody Registration registration) {
        userRegistration.registerUser(registration);

        RegistrationResponse registrationResponse = RegistrationResponse.builder()
                .message("User registered successfully")
                .build();
        return ResponseEntity.ok(registrationResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginSuccess> loginUser(@Valid @RequestBody Login login) {
        String userId = userLogin.loginUser(login.getEmail(), login.getPassword());
        logger.info("User logged in successfully with userId: {}" + userId);
        return ResponseEntity.ok(LoginSuccess.builder().userId(userId).build());
    }
}
