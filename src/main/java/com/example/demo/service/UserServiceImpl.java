package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.PaymentCard;
import com.example.demo.entity.User;
import com.example.demo.exception.BusinessLogicException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.PaymentCardMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.PaymentCardRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.specifications.UserSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PaymentCardRepository paymentCardRepository;
    private final PaymentCardMapper paymentCardMapper;
    private final CacheManager cacheManager;

    @Override
    @Transactional
    @CachePut(value = "users", key = "#result.id")
    public UserResponseDTO createUser(UserCreateDTO dto) {
        User user = userMapper.toEntity(dto);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#id")
    public UserResponseDTO getUserById(Long id) {
        log.info("Fetching user from DATABASE with id: {}", id);
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getAllUsers(Pageable pageable, UserFilterDTO filter) {
        Specification<User> userSpec = UserSpecification.filterUsers(filter);
        Page<User> userPage = userRepository.findAll(userSpec, pageable);
        return userPage.map(userMapper::toDto);
    }

    @Override
    @Transactional
    @CachePut(value = "users", key = "#id")
    public UserResponseDTO updateUser(Long id, UserUpdateDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getSurname() != null) user.setSurname(dto.getSurname());
        if (dto.getBirthDate() != null) user.setBirthDate(dto.getBirthDate());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getActive() != null) user.setActive(dto.getActive());

        User savedUser = userRepository.save(user);

        return userMapper.toDto(savedUser);
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void setActiveStatus(Long id, boolean isActive) {
        int updated = userRepository.updateActiveStatus(id, isActive);
        if (updated == 0) {
            throw new ResourceNotFoundException("User not found or status already " + isActive);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(Long id) {
        log.info("--- Deleting user from DATABASE and CACHE with id:{} ---",id);
        User deleteUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " not found"));
        userRepository.delete(deleteUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentCardResponseDTO> getCardsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return user.getPaymentCards().stream()
                .map(paymentCardMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public PaymentCardResponseDTO createCard(Long userId, PaymentCardCreateDTO cardDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        long cardCount=paymentCardRepository.countByUserId(userId);
        if (cardCount >=5) {
            throw new BusinessLogicException("Cards limit overflow");
        }
        PaymentCard card = paymentCardMapper.toEntity(cardDto);
        card.setUser(user);
        PaymentCard savedCard = paymentCardRepository.save(card);
        return paymentCardMapper.toDto(savedCard);
    }

    @Override
    @Transactional
    public PaymentCardResponseDTO updateCard(Long id, PaymentCardUpdateDTO dto) {
        PaymentCard card = paymentCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card with ID " + id + " not found"));

        Long userId = card.getUser().getId();

        int updatedRows = paymentCardRepository.updateCard(
                id,
                dto.getNumber(),
                dto.getHolder(),
                dto.getExpirationDate(),
                dto.getActive()
        );
        if (updatedRows == 0) {
            throw new ResourceNotFoundException("Card with ID " + id + " not found for update");
        }

        PaymentCard updatedCard = paymentCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Update failed, entity not found after update"));

        cacheManager.getCache("users").evict(userId);

        return paymentCardMapper.toDto(updatedCard);
    }

    @Override
    @Transactional
    public void deleteCard(Long cardId) {
        PaymentCard card = paymentCardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found: " + cardId));

        Long userId = card.getUser().getId();
        paymentCardRepository.delete(card);

        cacheManager.getCache("users").evict(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsersForAdmin() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public UserResponseDTO getUserByEmail(String email) {
        User user= userRepository.findByEmailAndActiveTrue(email)
                .orElseThrow(()->new ResourceNotFoundException("User not found"));
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public Long createUserInternal(UserCreateDTO dto) {
        User user= userMapper.toEntity(dto);
        return userRepository.save(user).getId();
    }
}
