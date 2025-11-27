package com.example.demo.controller;

import com.example.demo.dto.PaymentCardCreateDTO;
import com.example.demo.dto.PaymentCardResponseDTO;
import com.example.demo.dto.PaymentCardUpdateDTO;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class PaymentCardController {

    private final UserService userService;

    @GetMapping("/me/cards")
    public ResponseEntity<List<PaymentCardResponseDTO>> getMyCards() {
        String userId = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        List<PaymentCardResponseDTO> cards =
                userService.getCardsByUserId(Long.parseLong(userId));

        return ResponseEntity.ok(cards);
    }

    @GetMapping("/{userId}/cards")
    public ResponseEntity<List<PaymentCardResponseDTO>> getCardsByUserId(@PathVariable Long userId){
        checkAdminRole();
        List<PaymentCardResponseDTO> cardDto =userService.getCardsByUserId(userId);
        return ResponseEntity.ok(cardDto);
    }

    @PostMapping("/{userId}/cards")
    public ResponseEntity<PaymentCardResponseDTO> createCard(@PathVariable Long userId, @Valid @RequestBody PaymentCardCreateDTO createDto){
        checkAdminRole();
        PaymentCardResponseDTO createdCard = userService.createCard(userId,createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCard);
    }

    @PutMapping("/{userId}/cards/{cardId}")
    public ResponseEntity<PaymentCardResponseDTO> updateCard(
            @PathVariable Long userId,
            @PathVariable Long cardId,
            @Valid @RequestBody PaymentCardUpdateDTO updateDto
    ) {
        checkAdminRole();
        PaymentCardResponseDTO updatedCard = userService.updateCard(cardId, updateDto);
        return ResponseEntity.ok(updatedCard);
    }


    @DeleteMapping("/{userId}/cards/{cardId}")
    public ResponseEntity<Void> deleteCard(
            @PathVariable Long userId,
            @PathVariable Long cardId
    ) {
        checkAdminRole();
        userService.deleteCard(cardId);
        return ResponseEntity.noContent().build();
    }


    private void checkAdminRole() {
        boolean isAdmin = SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        if (!isAdmin) {
            throw new AccessDeniedException("Требуется роль ADMIN");
        }
    }
}
