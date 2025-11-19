package com.example.demo.controller;

import com.example.demo.dto.PaymentCardCreateDTO;
import com.example.demo.dto.PaymentCardResponseDTO;
import com.example.demo.dto.PaymentCardUpdateDTO;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/cards")
@RequiredArgsConstructor
public class PaymentCardController {

    private final UserService userService;

    @GetMapping()
    public ResponseEntity<List<PaymentCardResponseDTO>> getCardsByUserId(@PathVariable Long userId){
        List<PaymentCardResponseDTO> cardDto =userService.getCardsByUserId(userId);
        return ResponseEntity.ok(cardDto);
    }

    @PostMapping()
    public ResponseEntity<PaymentCardResponseDTO> createCard(@PathVariable Long userId, @Valid @RequestBody PaymentCardCreateDTO createDto){
        PaymentCardResponseDTO createdCard = userService.createCard(userId,createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCard);
    }

    @PutMapping("/{cardId}")
    public ResponseEntity<PaymentCardResponseDTO> updareCard(@PathVariable Long cardId,@Valid @RequestBody PaymentCardUpdateDTO updateDto){
        PaymentCardResponseDTO updatedCard =userService.updateCard(cardId, updateDto);
        return ResponseEntity.ok(updatedCard);
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long cardId){
        userService.deleteCard(cardId);
        return ResponseEntity.noContent().build();
    }

}
