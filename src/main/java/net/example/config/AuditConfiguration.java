package net.example.config;

import net.example.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.envers.repository.config.EnableEnversRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@EnableJpaAuditing
@EnableEnversRepositories(basePackageClasses = ApplicationRunner.class)
@Configuration
public class AuditConfiguration {

    @Bean
    public AuditorAware<String> auditorAware() {
        var user = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
            .map(authentication -> (UserDetails) authentication.getPrincipal())
            .map(UserDetails::getUsername);

        if (user.isPresent()) {
            return () -> user;
        } else {
            return () -> Optional.of("system");
        }
    }
}
