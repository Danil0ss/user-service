package com.example.demo.service;

import com.example.demo.dto.UserCreateDTO;
import com.example.demo.dto.UserResponseDTO;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.PaymentCardMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.PaymentCardRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


public class UnitTests {
    private UserRepository userRepository;
    private UserMapper userMapper;
    private PaymentCardRepository paymentCardRepository;
    private PaymentCardMapper paymentCardMapper;
    private CacheManager cacheManager;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userMapper = mock(UserMapper.class);
        paymentCardRepository = mock(PaymentCardRepository.class);
        paymentCardMapper = mock(PaymentCardMapper.class);
        cacheManager = mock(CacheManager.class);

        userService = new UserServiceImpl(
                userRepository,
                userMapper,
                paymentCardRepository,
                paymentCardMapper,
                cacheManager
        );
    }

    @Test
    void createUser_shouldSaveAndReturnDto() {
        UserCreateDTO dto = new UserCreateDTO();
        dto.setName("Ivan");
        dto.setSurname("Ivanov");
        dto.setEmail("ivan@test.com");
        dto.setBirthDate(LocalDate.of(1995, 5, 20));

        User entity = new User();
        entity.setId(1L);
        entity.setName("Ivan");

        UserResponseDTO responseDto = new UserResponseDTO();
        responseDto.setId(1L);
        responseDto.setName("Ivan");

        when(userMapper.toEntity(dto)).thenReturn(entity);
        when(userRepository.save(entity)).thenReturn(entity);
        when(userMapper.toDto(entity)).thenReturn(responseDto);

        UserResponseDTO result = userService.createUser(dto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Ivan");

        verify(userMapper).toEntity(dto);
        verify(userRepository).save(entity);
        verify(userMapper).toDto(entity);
    }

    @Test
    void getUserById_whenExists_shouldReturnDto() {
        User entity = new User();
        entity.setId(5L);
        entity.setName("Petr");

        UserResponseDTO responseDto = new UserResponseDTO();
        responseDto.setId(5L);
        responseDto.setName("Petr");

        when(userRepository.findById(5L)).thenReturn(Optional.of(entity));
        when(userMapper.toDto(entity)).thenReturn(responseDto);

        UserResponseDTO result = userService.getUserById(5L);

        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getName()).isEqualTo("Petr");
    }

    @Test
    void getUserById_whenNotExists_shouldThrow() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void deleteUser_whenExists_shouldCallRepositoryDelete() {
        User entity = new User();
        entity.setId(10L);

        when(userRepository.findById(10L)).thenReturn(Optional.of(entity));

        userService.deleteUser(10L);

        verify(userRepository).findById(10L);
        verify(userRepository).delete(entity);
    }
}