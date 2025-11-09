package com.example.demo.mapper;

import com.example.demo.dto.PaymentCardCreateDTO;
import com.example.demo.dto.PaymentCardResponseDTO;
import com.example.demo.dto.UserCreateDTO;
import com.example.demo.entity.PaymentCard;
import com.example.demo.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentCardMapper {

    PaymentCardResponseDTO toDto(PaymentCard entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "user", ignore = true)
    PaymentCard toEntity(PaymentCardCreateDTO dto);
}
