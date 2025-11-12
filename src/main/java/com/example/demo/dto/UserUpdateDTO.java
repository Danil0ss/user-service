package com.example.demo.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class UserUpdateDTO {

    @Size(max = 50, message = "Name must be less than 50 characters")
    private String name;

    @Size(max = 50, message = "Surname must be less than 50 characters")
    private String surname;

    private LocalDate birthDate;

    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must be less than 100 characters")
    private String email;

    private Boolean active;
}