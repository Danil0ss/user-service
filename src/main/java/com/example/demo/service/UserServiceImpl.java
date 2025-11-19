package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.PaymentCard;
import com.example.demo.entity.User;
import com.example.demo.mapper.PaymentCardMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.PaymentCardRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.specifications.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PaymentCardRepository paymentCardRepository;
    private final PaymentCardMapper paymentCardMapper;

    @Override
    @Transactional
    public UserResponseDTO createUser(UserCreateDTO dto) {
        User user=userMapper.toEntity(dto);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
       return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(()->new RuntimeException("User not found"));
    }

    @Override
    public Page<UserResponseDTO> getAllUsers(Pageable pageable, UserFilterDTO filter) {
        Specification<User> userSpec = UserSpecification.filterUsers(filter);
        Page<User> userPage = userRepository.findAll(userSpec, pageable);
        return userPage.map(userMapper::toDto);
    }

    @Override
    @Transactional
    public UserResponseDTO updateUser(Long id, UserUpdateDTO dto) {
        int updatedRows= userRepository.updateUser(id,
                dto.getName(),
                dto.getSurname(),
                dto.getBirthDate(),
                dto.getEmail(),
                dto.getActive());
        if (updatedRows ==0)
            throw new RuntimeException("User with ID " + id + " not found for update");
        User updatedUser=userRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Update failed, entity not found after update"));
        return userMapper.toDto(updatedUser);
    }

    @Override
    @Transactional
    public void setActiveStatus(Long id, boolean isActive) {
        int updaterRows= userRepository.updateActiveStatus(id,isActive);
        if (updaterRows==0) {
            throw new RuntimeException("User with ID "+id+" not founded or status have been already set");
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
       User deleteUser = userRepository.findById(id)
                .orElseThrow(()->new RuntimeException("User with ID " + id +" not found"));
      userRepository.delete(deleteUser);
    }

    @Override
    public List<PaymentCardResponseDTO> getCardsByUserId(Long userId) {
        User user=userRepository.findById(userId)
                .orElseThrow(()->new RuntimeException("User not found"));
        return user.getPaymentCards().stream()
                .map(paymentCardMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public PaymentCardResponseDTO createCard(Long userId, PaymentCardCreateDTO cardDto) {
        User user=userRepository.findById(userId)
                .orElseThrow(()->new RuntimeException("User not founded"));
        if(user.getPaymentCards().size()>4){
            throw new RuntimeException("Cards limit overflow");
        }
        PaymentCard card= paymentCardMapper.toEntity(cardDto);
        card.setUser(user);
        PaymentCard savedCard=paymentCardRepository.save(card);
        return paymentCardMapper.toDto(savedCard);
    }

    @Override
    @Transactional
    public PaymentCardResponseDTO updateCard(Long id, PaymentCardUpdateDTO dto) {
        int updatedRows= paymentCardRepository.updateCard(id,
                dto.getNumber(),
                dto.getHolder(),
                dto.getExpirationDate(),
                dto.getActive());
        if(updatedRows==0) throw new RuntimeException("Card with ID " + id + " not found for update");
        PaymentCard updatedCard=paymentCardRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Update failed, entity not found after update"));
        return paymentCardMapper.toDto(updatedCard);
    }

    @Override
    @Transactional
    public void deleteCard(Long cardId) {
        PaymentCard deleteCard=paymentCardRepository.findById(cardId)
                .orElseThrow(()->new RuntimeException("Card with "+cardId+" not found"));
        paymentCardRepository.delete(deleteCard);
    }

}