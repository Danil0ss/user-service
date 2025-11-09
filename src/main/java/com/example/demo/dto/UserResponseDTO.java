package com.example.demo.dto;

import lombok.Data;
import java.util.List;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
public class UserResponseDTO {
    private Long id;
    private String name;
    private String surname;
    private LocalDate birthDate;
    private String email;
    private Boolean active;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private List<PaymentCardResponseDTO> paymentCards;
}
