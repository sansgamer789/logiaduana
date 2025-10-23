package com.logiaduana.config;

import com.logiaduana.model.Role;
import com.logiaduana.model.User;
import com.logiaduana.repository.RoleRepository;
import com.logiaduana.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository,
                                   RoleRepository roleRepository,
                                   PasswordEncoder passwordEncoder) {
        return args -> {
            if (roleRepository.count() == 0) {
                Role adminRole = new Role("ADMIN");
                Role userRole = new Role("USER");
                roleRepository.save(adminRole);
                roleRepository.save(userRole);
            }

            if (userRepository.findByUsername("admin").isEmpty()) {
                Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();

                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRoles(new HashSet<>(Set.of(adminRole)));

                userRepository.save(admin);
                System.out.println("✅ Usuario admin creado: admin / admin123");
            }
        };
    }
}
