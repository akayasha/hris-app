package com.hris.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hris.dto.request.CreatePegawaiRequest;
import com.hris.entity.Pengguna;
import com.hris.exception.GlobalExceptionHandler;
import com.hris.service.PegawaiService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PegawaiControllerTest {

    @Mock
    private PegawaiService pegawaiService;

    @Mock
    private RoleChecker roleChecker;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(new PegawaiController(pegawaiService, roleChecker))
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
    void tambahPegawaiUsesJsonBodyAndReturnsStandardResponse() throws Exception {
        authenticate("admin@example.com", "ADMIN");
        when(roleChecker.getAdminOrHrd("admin@example.com")).thenReturn(Pengguna.builder()
                .email("admin@example.com")
                .profile("ADMIN")
                .build());
        doNothing().when(pegawaiService).tambahPegawai(any(CreatePegawaiRequest.class));

        CreatePegawaiRequest request = new CreatePegawaiRequest(
                "Budi Santoso",
                "budi@example.com",
                "Jakarta",
                946684800L,
                1,
                5,
                2,
                1,
                1,
                "secret123",
                "secret123"
        );

        mockMvc.perform(post("/pegawai/admin-tambah-pegawai")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Pegawai berhasil ditambahkan."));

        then(roleChecker).should().getAdminOrHrd("admin@example.com");
        then(pegawaiService).should().tambahPegawai(any(CreatePegawaiRequest.class));
    }

    @Test
    void tambahPegawaiValidationReturnsFieldErrors() throws Exception {
        authenticate("admin@example.com", "ADMIN");

        mockMvc.perform(post("/pegawai/admin-tambah-pegawai")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "namaLengkap": "",
                                  "email": "salah-format",
                                  "tempatLahir": "",
                                  "tanggalLahir": null,
                                  "kdJenisKelamin": null,
                                  "kdPendidikan": null,
                                  "kdJabatan": null,
                                  "kdDepartemen": null,
                                  "kdUnitKerja": null,
                                  "password": "123",
                                  "passwordC": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors.namaLengkap").value("namaLengkap wajib diisi"))
                .andExpect(jsonPath("$.errors.email").value("format email tidak valid"))
                .andExpect(jsonPath("$.errors.password").value("password minimal 6 karakter"));
    }

    private void authenticate(String email, String role) {
        User principal = new User(email, "ignored", java.util.List.of(() -> "ROLE_" + role));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities())
        );
    }
}
