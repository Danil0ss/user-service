package com.example.demo.controller;

import com.example.demo.BaseIntegrationTest;
import com.example.demo.dto.PaymentCardCreateDTO;
import com.example.demo.dto.UserCreateDTO;
import com.example.demo.dto.UserUpdateDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private static Long createdUserId;

    private void setAdminAuth() {
        var auth = new TestingAuthenticationToken(
                "1",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        auth.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private void clearAuth() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @Order(1)
    @DisplayName("POST /api/users/register — создаёт пользователя (без роли)")
    void createUser_whenValid_shouldReturn201AndUser() throws Exception {
        clearAuth();

        UserCreateDTO dto = new UserCreateDTO();
        dto.setName("Ivan");
        dto.setSurname("Ivanov");
        dto.setEmail("ivan.integration@test.com");
        dto.setBirthDate(LocalDate.of(1995, 5, 20));

        String response = mockMvc.perform(post("/api/users/register")
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
    @DisplayName("GET /api/users/{id} — возвращает пользователя (ADMIN)")
    void getUserById_whenExists_shouldReturn200() throws Exception {
        setAdminAuth();

        mockMvc.perform(get("/api/users/" + createdUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdUserId))
                .andExpect(jsonPath("$.name").value("Ivan"));

        clearAuth();
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/users/{id} — 404 если пользователь не существует (ADMIN)")
    void getUserById_whenNotExists_shouldReturn404() throws Exception {
        setAdminAuth();

        mockMvc.perform(get("/api/users/999999"))
                .andExpect(status().isNotFound());

        clearAuth();
    }

    @Test
    @Order(4)
    @DisplayName("PUT /api/users/{id} — обновляет пользователя (ADMIN)")
    void updateUser_whenValid_shouldReturn200() throws Exception {
        setAdminAuth();

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setEmail("ivan.updated@test.com");
        dto.setActive(false);

        mockMvc.perform(put("/api/users/" + createdUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ivan.updated@test.com"))
                .andExpect(jsonPath("$.active").value(false));

        clearAuth();
    }

    @Test
    @Order(5)
    @DisplayName("POST /api/users/{id}/cards — создаёт 5 карт (ADMIN)")
    void createFiveCards_shouldSucceed() throws Exception {
        setAdminAuth();

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

            mockMvc.perform(post("/api/users/" + createdUserId + "/cards")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cardDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.number").value(uniqueNumbers[i]));
        }

        clearAuth();
    }

    @Test
    @Order(6)
    @DisplayName("POST /api/users/{id}/cards — шестая карта → 422 (лимит) (ADMIN)")
    void createSixthCard_shouldReturn422() throws Exception {
        clearAuth();

        UserCreateDTO userDto = new UserCreateDTO();
        userDto.setName("Test");
        userDto.setSurname("User");
        userDto.setEmail("test@example.com");
        userDto.setBirthDate(LocalDate.now().minusYears(25));

        String userResponse = mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long userId = JsonPath.parse(userResponse).read("$.id", Long.class);

        setAdminAuth();


        for (int i = 0; i < 5; i++) {
            PaymentCardCreateDTO cardDto = new PaymentCardCreateDTO();
            cardDto.setNumber("111122223333444" + i);
            cardDto.setHolder("Test Holder");
            cardDto.setExpirationDate(LocalDate.now().plusYears(2));

            mockMvc.perform(post("/api/users/" + userId + "/cards")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cardDto)))
                    .andExpect(status().isCreated());
        }

        PaymentCardCreateDTO sixthCardDto = new PaymentCardCreateDTO();
        sixthCardDto.setNumber("6666666666666666");
        sixthCardDto.setHolder("Ivan Ivanov");
        sixthCardDto.setExpirationDate(LocalDate.now().plusYears(3));

        mockMvc.perform(post("/api/users/" + userId + "/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sixthCardDto)))
                .andExpect(status().isUnprocessableEntity());

        clearAuth();
    }

    @Test
    @Order(7)
    @DisplayName("GET /api/users/{id}/cards — возвращает список карт (ADMIN)")
    void getCardsByUserId_shouldReturnFiveCards() throws Exception {
        setAdminAuth();

        mockMvc.perform(get("/api/users/" + createdUserId + "/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5));

        clearAuth();
    }

    @Test
    @Order(8)
    @DisplayName("DELETE /api/users/{id} — удаляет пользователя (ADMIN)")
    void deleteUser_shouldReturn204() throws Exception {
        setAdminAuth();

        mockMvc.perform(delete("/api/users/" + createdUserId))
                .andExpect(status().isNoContent());

        clearAuth();
    }

    @Test
    @Order(9)
    @DisplayName("GET /api/users/{id} после удаления — 404 (ADMIN)")
    void getUserAfterDelete_shouldReturn404() throws Exception {
        setAdminAuth();

        mockMvc.perform(get("/api/users/" + createdUserId))
                .andExpect(status().isNotFound());

        clearAuth();
    }
}
