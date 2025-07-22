package org.example.economily.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.economily.exceptions.ErrorCodes;
import org.example.economily.exceptions.ErrorMessageException;
import org.example.economily.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new ErrorMessageException("user not found", ErrorCodes.BadRequest));
    }
}
