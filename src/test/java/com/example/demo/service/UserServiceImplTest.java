package com.example.demo.service;

import com.example.demo.dto.UserCreateDTO;
import com.example.demo.dto.UserResponseDTO;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.PaymentCardMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.PaymentCardRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private PaymentCardRepository paymentCardRepository;
    @Mock private PaymentCardMapper paymentCardMapper;
    @Mock private CacheManager cacheManager;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_ShouldSaveAndReturnUserDto() {
        // GIVEN
        UserCreateDTO createDto = new UserCreateDTO();
        createDto.setName("John");

        User userToSave = new User();
        userToSave.setName("John");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("John");

        UserResponseDTO expectedResponse = new UserResponseDTO();
        expectedResponse.setId(1L);
        expectedResponse.setName("John");

        when(userMapper.toEntity(createDto)).thenReturn(userToSave);
        when(userRepository.save(userToSave)).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(expectedResponse);

        // WHEN
        UserResponseDTO actualResponse = userService.createUser(createDto);

        // THEN (Используем AssertJ - это красивее)
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getId()).isEqualTo(1L);
        assertThat(actualResponse.getName()).isEqualTo("John");

        verify(userRepository).save(userToSave);
    }

    @Test
    void getUserById_WhenExists_ShouldReturnUserDto() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        UserResponseDTO responseDto = new UserResponseDTO();
        responseDto.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(responseDto);

        UserResponseDTO result = userService.getUserById(userId);

        assertThat(result.getId()).isEqualTo(userId);
    }

    @Test
    void getUserById_WhenNotExists_ShouldThrowException() {
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");
    }
}