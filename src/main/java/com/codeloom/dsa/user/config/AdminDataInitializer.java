package com.codeloom.dsa.user.config;

import com.codeloom.dsa.user.entity.Role;
import com.codeloom.dsa.user.entity.User;
import com.codeloom.dsa.user.repository.RoleRepository;
import com.codeloom.dsa.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdminDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminDataInitializer(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(new Role("USER")));

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ADMIN")));

        userRepository.findByEmail("admin@example.com").ifPresentOrElse(
                user -> {
                    if (!user.getRoles().contains(adminRole)) {
                        user.addRole(adminRole);
                        userRepository.save(user);
                    }
                },
                () -> {
                    User admin = new User(
                            "admin@example.com",
                            "admin",
                            passwordEncoder.encode("AdminPassword123!")
                    );
                    admin.addRole(userRole);
                    admin.addRole(adminRole);
                    userRepository.save(admin);
                }
        );
    }
}
