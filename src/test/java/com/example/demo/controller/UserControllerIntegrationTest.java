package com.example.demo.controller;

import com.example.demo.BaseIntegrationTest;
import com.example.demo.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private static Long createdUserId;

    @Test
    @Order(1)
    @DisplayName("POST /users — создаёт пользователя")
    void createUser_whenValid_shouldReturn201AndUser() throws Exception {
        UserCreateDTO dto = new UserCreateDTO();
        dto.setName("Ivan");
        dto.setSurname("Ivanov");
        dto.setEmail("ivan.integration@test.com");
        dto.setBirthDate(LocalDate.of(1995, 5, 20));

        String response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Ivan"))
                .andReturn().getResponse().getContentAsString();

        createdUserId = objectMapper.readTree(response).get("id").asLong();
    }

    @Test
    @Order(2)
    @DisplayName("GET /users/{id} — возвращает пользователя")
    void getUserById_whenExists_shouldReturn200() throws Exception {
        mockMvc.perform(get("/users/" + createdUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdUserId))
                .andExpect(jsonPath("$.name").value("Ivan"));
    }

    @Test
    @Order(3)
    @DisplayName("GET /users/{id} — 404 если пользователь не существует")
    void getUserById_whenNotExists_shouldReturn404() throws Exception {
        mockMvc.perform(get("/users/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(4)
    @DisplayName("PUT /users/{id} — обновляет пользователя")
    void updateUser_whenValid_shouldReturn200() throws Exception {
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setEmail("ivan.updated@test.com");
        dto.setActive(false);

        mockMvc.perform(put("/users/" + createdUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ivan.updated@test.com"))
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    @Order(5)
    @DisplayName("POST /users/{id}/cards — создаёт 5 карт")
    void createFiveCards_shouldSucceed() throws Exception {
        String[] uniqueNumbers = {
                "4111111111111111",
                "4222222222222222",
                "4333333333333333",
                "4444444444444444",
                "5555555555555555"
        };

        for (int i = 0; i < 5; i++) {
            PaymentCardCreateDTO cardDto = new PaymentCardCreateDTO();
            cardDto.setNumber(uniqueNumbers[i]);
            cardDto.setHolder("Ivan Ivanov");
            cardDto.setExpirationDate(LocalDate.now().plusYears(3));

            mockMvc.perform(post("/users/" + createdUserId + "/cards")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cardDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.number").value(uniqueNumbers[i]));
        }
    }

    @Test
    @Order(6)
    @DisplayName("POST /users/{id}/cards — шестая карта → 400 (лимит)")
    void createSixthCard_shouldReturn400() throws Exception {
        // Сначала создаем пользователя
        UserCreateDTO userDto = new UserCreateDTO();
        userDto.setName("Test");
        userDto.setSurname("User");
        userDto.setEmail("test@example.com");
        userDto.setBirthDate(LocalDate.now().minusYears(25));

        String userResponse = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long userId = JsonPath.parse(userResponse).read("$.id", Long.class);


        for (int i = 0; i < 5; i++) {
            PaymentCardCreateDTO cardDto = new PaymentCardCreateDTO();
            cardDto.setNumber("111122223333444" + i);
            cardDto.setHolder("Test Holder");
            cardDto.setExpirationDate(LocalDate.now().plusYears(2));

            mockMvc.perform(post("/users/" + userId + "/cards")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cardDto)))
                    .andExpect(status().isCreated());
        }


        PaymentCardCreateDTO sixthCardDto = new PaymentCardCreateDTO();
        sixthCardDto.setNumber("6666666666666666");
        sixthCardDto.setHolder("Ivan Ivanov");
        sixthCardDto.setExpirationDate(LocalDate.now().plusYears(3));

        mockMvc.perform(post("/users/" + userId + "/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sixthCardDto)))
                .andExpect(status().isUnprocessableEntity());
    }
    @Test
    @Order(7)
    @DisplayName("GET /users/{id}/cards — возвращает список карт")
    void getCardsByUserId_shouldReturnFourCards() throws Exception {
        mockMvc.perform(get("/users/" + createdUserId + "/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5));
    }

    @Test
    @Order(8)
    @DisplayName("DELETE /users/{id} — удаляет пользователя")
    void deleteUser_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/users/" + createdUserId))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(9)
    @DisplayName("GET /users/{id} после удаления — 404")
    void getUserAfterDelete_shouldReturn404() throws Exception {
        mockMvc.perform(get("/users/" + createdUserId))
                .andExpect(status().isNotFound());
    }
}