package com.codeloom.dsa.auth.service;

import com.codeloom.dsa.auth.dto.RegisterRequest;
import com.codeloom.dsa.auth.dto.RegisterResponse;
import com.codeloom.dsa.user.dto.CurrentUserResponse;
import com.codeloom.dsa.user.entity.Role;
import com.codeloom.dsa.user.entity.User;
import com.codeloom.dsa.user.repository.RoleRepository;
import com.codeloom.dsa.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() ->
                        new IllegalStateException(
                                "USER role not found"
                        )
                );

        String passwordHash =
                passwordEncoder.encode(request.password());

        User user = new User(
                request.email(),
                request.username(),
                passwordHash
        );

        user.addRole(userRole);

        User savedUser = userRepository.save(user);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getUsername()
        );
    }

    @Transactional(readOnly = true)
    public CurrentUserResponse getCurrentUser(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );

        Set<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(java.util.stream.Collectors.toSet());

        return new CurrentUserResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                roles
        );
    }
}