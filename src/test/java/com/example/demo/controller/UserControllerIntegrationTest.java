package com.example.demo.controller;

import com.example.demo.BaseIntegrationTest;
import com.example.demo.dto.PaymentCardCreateDTO;
import com.example.demo.dto.UserCreateDTO;
import com.example.demo.dto.UserUpdateDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@Transactional
class UserControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private Long createTestUser(String email) throws Exception {
        UserCreateDTO dto = new UserCreateDTO();
        dto.setName("Ivan");
        dto.setSurname("Ivanov");
        dto.setEmail(email);
        dto.setBirthDate(LocalDate.of(1995, 5, 20));

        String response = mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }

    @Test
    @DisplayName("POST /register — успешное создание")
    void createUser_whenValid_shouldReturn201() throws Exception {
        UserCreateDTO dto = new UserCreateDTO();
        dto.setName("New");
        dto.setSurname("User");
        dto.setEmail("new@test.com");
        dto.setBirthDate(LocalDate.of(2000, 1, 1));

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("GET /api/users/{id} — успех (ADMIN)")
    @WithMockUser(roles = "ADMIN")
    void getUserById_whenExists_shouldReturnUser() throws Exception {
        Long userId = createTestUser("get@test.com");

        mockMvc.perform(get("/api/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("get@test.com"));
    }

    @Test
    @DisplayName("GET /api/users/{id} — 404 если нет (ADMIN)")
    @WithMockUser(roles = "ADMIN")
    void getUserById_whenNotExists_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/users/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/users/{id} — обновление (ADMIN)")
    @WithMockUser(roles = "ADMIN")
    void updateUser_shouldUpdateFields() throws Exception {
        Long userId = createTestUser("update@test.com");

        UserUpdateDTO updateDto = new UserUpdateDTO();
        updateDto.setEmail("updated@test.com");
        updateDto.setActive(false);

        mockMvc.perform(put("/api/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@test.com"))
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    @DisplayName("Карты: лимит 5 карт работает корректно")
    @WithMockUser(roles = "ADMIN")
    void cardsLimit_shouldBlockSixthCard() throws Exception {
        Long userId = createTestUser("cards@test.com");

        for (int i = 0; i < 5; i++) {
            PaymentCardCreateDTO cardDto = new PaymentCardCreateDTO();
            cardDto.setNumber("111122223333444" + i);
            cardDto.setHolder("Holder");
            cardDto.setExpirationDate(LocalDate.now().plusYears(1));

            mockMvc.perform(post("/api/users/" + userId + "/cards")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cardDto)))
                    .andExpect(status().isCreated());
        }

        PaymentCardCreateDTO sixthCard = new PaymentCardCreateDTO();
        sixthCard.setNumber("9999999999999999");
        sixthCard.setHolder("Fail Holder");
        sixthCard.setExpirationDate(LocalDate.now().plusYears(1));

        mockMvc.perform(post("/api/users/" + userId + "/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sixthCard)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("DELETE /api/users/{id} — удаление")
    @WithMockUser(roles = "ADMIN")
    void deleteUser_shouldRemoveFromDb() throws Exception {
        Long userId = createTestUser("delete@test.com");

        mockMvc.perform(delete("/api/users/" + userId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/users/" + userId))
                .andExpect(status().isNotFound());
    }
}