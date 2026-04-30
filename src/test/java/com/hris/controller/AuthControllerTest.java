package com.hris.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hris.dto.request.InitDataRequest;
import com.hris.dto.request.LoginRequest;
import com.hris.exception.GlobalExceptionHandler;
import com.hris.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(authService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void loginReturnsStandardResponse() throws Exception {
        Map<String, Object> response = Map.of(
                "hasil", Map.of(
                        "token", "jwt-token",
                        "info", Map.of("profile", "ADMIN")
                )
        );
        given(authService.login(any(LoginRequest.class))).willReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(
                                "admin@example.com", "secret123", "ADMIN"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login berhasil."))
                .andExpect(jsonPath("$.data.hasil.token").value("jwt-token"))
                .andExpect(jsonPath("$.errors").doesNotExist());
    }

    @Test
    void initDataValidationReturnsFieldErrors() throws Exception {
        mockMvc.perform(post("/api/auth/init-data")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new InitDataRequest("", ""))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Validasi request gagal."))
                .andExpect(jsonPath("$.errors.namaAdmin").value("namaAdmin wajib diisi"))
                .andExpect(jsonPath("$.errors.perusahaan").value("perusahaan wajib diisi"));
    }
}
