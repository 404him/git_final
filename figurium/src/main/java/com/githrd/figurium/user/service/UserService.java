package com.githrd.figurium.user.service;

import com.githrd.figurium.user.entity.User;
import com.githrd.figurium.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);

    }
}
