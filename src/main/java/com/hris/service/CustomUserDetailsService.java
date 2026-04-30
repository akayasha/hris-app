package com.hris.service;

import com.hris.repository.PenggunaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final PenggunaRepository penggunaRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return penggunaRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Pengguna tidak ditemukan: " + email));
    }
}
