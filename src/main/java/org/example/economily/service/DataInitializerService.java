package org.example.economily.service;

import org.example.economily.entity.User;
import org.example.economily.entity.UserRole;
import org.example.economily.repository.UserRepository;
import org.example.economily.repository.UserRoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializerService implements CommandLineRunner {

    private final UserRepository userRepository;
    private final UserRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail("admin@example.com").isEmpty()) {
            Set<UserRole> roles = new HashSet<>();

            roleRepository.findByName("ADMIN").ifPresent(roles::add);
            roleRepository.findByName("SUPER_ADMIN").ifPresent(roles::add);


            User admin = User.builder()
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(roles)
                    .build();

            userRepository.save(admin);
        }
    }

}
