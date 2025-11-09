package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PaymentCardResponseDTO {
    private Long id;
    private String number;
    private String holder;
    private LocalDate expirationDate;
    private Boolean active;
}