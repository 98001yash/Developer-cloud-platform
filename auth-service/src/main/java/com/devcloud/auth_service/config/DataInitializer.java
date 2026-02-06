package com.devcloud.auth_service.config;


import com.devcloud.auth_service.entity.Role;
import com.devcloud.auth_service.enums.RoleName;
import com.devcloud.auth_service.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {


    private final RoleRepository roleRepository;

    @Bean
    CommandLineRunner initRoles() {
        return args -> {
            for (RoleName roleName : RoleName.values()) {
                roleRepository.findByName(roleName.name())
                        .orElseGet(() ->
                                roleRepository.save(
                                        Role.builder()
                                                .name(roleName.name())
                                                .build()
                                )
                        );
            }
        };
    }
}
