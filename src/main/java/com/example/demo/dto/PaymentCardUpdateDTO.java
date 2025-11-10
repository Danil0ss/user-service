package com.example.demo.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PaymentCardUpdateDTO {
    @Size(min = 16, max = 19, message = "Card number must be between 16 and 19 symbols")
    private String number;

    @Size(max=100,message = "Holder name is too long")
    private String holder;

    @Future(message = "Expiration date must be in the future")
    private LocalDate expirationDate;
    private Boolean active;
}
