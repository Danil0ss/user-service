package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/users/internal")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserRepository userRepository;

    record CreateUserInternalRequest(
            String name,
            String surname,
            LocalDate birthDate,
            String email
    ) {}

    @PostMapping("/create")
    public ResponseEntity<Long> createUserForAuth(@RequestBody CreateUserInternalRequest request) {
        User user = new User();
        user.setName(request.name());
        user.setSurname(request.surname());
        user.setBirthDate(request.birthDate());
        user.setEmail(request.email());
        user.setActive(true);

        User saved = userRepository.save(user);
        return ResponseEntity.ok(saved.getId());
    }
}
