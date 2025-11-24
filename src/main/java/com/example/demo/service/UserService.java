package com.example.demo.service;

import com.example.demo.dto.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // Для пагинации

public interface UserService {
    UserResponseDTO createUser(UserCreateDTO dto);
    UserResponseDTO getUserById(Long id);
    Page<UserResponseDTO> getAllUsers(Pageable pageable, UserFilterDTO filter);
    UserResponseDTO updateUser(Long id, UserUpdateDTO dto);
    void setActiveStatus(Long id, boolean isActive);
    void deleteUser(Long id);
    List<PaymentCardResponseDTO> getCardsByUserId(Long userId);
    PaymentCardResponseDTO createCard(Long userId, PaymentCardCreateDTO cardDto);
    PaymentCardResponseDTO updateCard(Long id,PaymentCardUpdateDTO dto);
    void deleteCard(Long cardId);
    List<UserResponseDTO> getAllUsersForAdmin();
}