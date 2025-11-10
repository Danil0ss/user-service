package com.example.demo.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class PaymentCardResponseDTO implements Serializable {

    private Long id;

    private String number;

    private String holder;

    private LocalDate expirationDate;

    private Boolean active;
}