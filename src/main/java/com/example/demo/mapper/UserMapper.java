package com.example.demo.mapper;

import com.example.demo.dto.UserCreateDTO;
import com.example.demo.dto.UserResponseDTO;
import com.example.demo.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses={PaymentCardMapper.class})
public interface UserMapper {


    UserResponseDTO toDto(User user);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "paymentCards", ignore = true)
    User toEntity(UserCreateDTO dto);

}