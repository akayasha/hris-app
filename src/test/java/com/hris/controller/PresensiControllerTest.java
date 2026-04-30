package com.hris.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hris.dto.request.SubmitAbsensiRequest;
import com.hris.exception.GlobalExceptionHandler;
import com.hris.service.PresensiService;
import com.hris.util.RoleChecker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PresensiControllerTest {

    @Mock
    private PresensiService presensiService;

    @Mock
    private RoleChecker roleChecker;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(new PresensiController(presensiService, roleChecker))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void checkInReturnsStandardResponse() throws Exception {
        authenticate("pegawai@example.com", "PEGAWAI");
        given(presensiService.checkIn("pegawai@example.com")).willReturn(Map.of("jamMasuk", "08:00:00"));

        mockMvc.perform(get("/presensi/in"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Check-in berhasil."))
                .andExpect(jsonPath("$.data.jamMasuk").value("08:00:00"));
    }

    @Test
    void submitAbsensiUsesJsonBody() throws Exception {
        authenticate("pegawai@example.com", "PEGAWAI");
        doNothing().when(presensiService).submitAbsensi(eq("pegawai@example.com"), any(SubmitAbsensiRequest.class));

        mockMvc.perform(post("/presensi/abseni")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SubmitAbsensiRequest(1714435200000L, 2))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Keterangan absensi berhasil dicatat."));

        then(presensiService).should().submitAbsensi(eq("pegawai@example.com"), any(SubmitAbsensiRequest.class));
    }

    private void authenticate(String email, String role) {
        User principal = new User(email, "ignored", java.util.List.of(() -> "ROLE_" + role));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities())
        );
    }
}
