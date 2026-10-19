package com.thebrian31.coworking;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thebrian31.coworking.dto.LoginRequest;
import com.thebrian31.coworking.dto.ReservationRequest;
import com.thebrian31.coworking.dto.SpaceRequest;
import com.thebrian31.coworking.dto.UserRegistrationRequest;
import com.thebrian31.coworking.entity.RoleCode;
import com.thebrian31.coworking.entity.SpaceType;
import com.thebrian31.coworking.entity.User;
import com.thebrian31.coworking.repository.RoleRepository;
import com.thebrian31.coworking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class ReservationFlowIntegrationTest {

    private static final String ADMIN_EMAIL = "admin@coworking.test";
    private static final String ADMIN_PASSWORD = "admin-pass-123";

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void ensureAdmin() {
        if (userRepository.existsByEmail(ADMIN_EMAIL)) {
            return;
        }
        User admin = new User();
        admin.setFullName("Administrador");
        admin.setEmail(ADMIN_EMAIL);
        admin.setPasswordHash(passwordEncoder.encode(ADMIN_PASSWORD));
        admin.setEnabled(true);
        admin.setRole(roleRepository.findByCode(RoleCode.ADMIN).orElseThrow());
        userRepository.saveAndFlush(admin);
    }

    @Test
    void overlappingReservationIsRejected() throws Exception {
        String adminToken = login(ADMIN_EMAIL, ADMIN_PASSWORD);
        long spaceId = createSpace(adminToken);
        String userToken = registerAndLogin();
        OffsetDateTime start = OffsetDateTime.parse("2030-06-01T10:00:00Z");

        createReservation(userToken, spaceId, start, start.plusHours(1));

        mockMvc.perform(post("/api/reservations")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReservationRequest(
                                spaceId,
                                start.plusMinutes(30),
                                start.plusHours(2)
                        ))))
                .andExpect(status().isConflict());
    }

    @Test
    void confirmReservationApprovesPayment() throws Exception {
        String adminToken = login(ADMIN_EMAIL, ADMIN_PASSWORD);
        long spaceId = createSpace(adminToken);
        String userToken = registerAndLogin();
        OffsetDateTime start = OffsetDateTime.parse("2030-07-01T10:00:00Z");
        long reservationId = createReservation(userToken, spaceId, start, start.plusHours(1));

        mockMvc.perform(post("/api/reservations/{id}/confirm", reservationId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    private String registerAndLogin() throws Exception {
        String email = "user-" + UUID.randomUUID() + "@coworking.test";
        String password = "user-pass-123";
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRegistrationRequest(
                                "Usuario de prueba",
                                email,
                                password
                        ))))
                .andExpect(status().isCreated());
        return login(email, password);
    }

    private long createSpace(String adminToken) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/spaces")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SpaceRequest(
                                "Sala " + UUID.randomUUID(),
                                SpaceType.WORK,
                                4,
                                "Piso 1",
                                new BigDecimal("25.00"),
                                false
                        ))))
                .andExpect(status().isCreated())
                .andReturn();
        return readTree(result).get("id").asLong();
    }

    private long createReservation(String token, long spaceId, OffsetDateTime start, OffsetDateTime end) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/reservations")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReservationRequest(spaceId, start, end))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn();
        return readTree(result).get("id").asLong();
    }

    private String login(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(email, password))))
                .andExpect(status().isOk())
                .andReturn();
        return readTree(result).get("accessToken").asText();
    }

    private JsonNode readTree(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }
}
