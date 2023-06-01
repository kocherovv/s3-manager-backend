package net.example.config;

import lombok.RequiredArgsConstructor;
import net.example.dto.UserCreateDto;
import net.example.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Set;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final UserService userService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(urlConfig -> urlConfig
                .anyRequest().permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .deleteCookies("JSESSIONID"))
            .formLogin(login -> login
                .defaultSuccessUrl("/api/v1/files"))
            .oauth2Login(config -> config
                .defaultSuccessUrl("/api/v1/files")
                .userInfoEndpoint(userInfo -> userInfo
                    .oidcUserService(oidcUserService())));

        return http.build();
    }

    private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        return userRequest -> {
            String name = userRequest.getIdToken().getClaim("name");
            String email = userRequest.getIdToken().getClaim("email");
            String standardPassword = userRequest.getIdToken().getClaim("at_hash");

            if (userService.findByName(name).isEmpty()) {

                userService.create(UserCreateDto.builder()
                    .email(email)
                    .name(name)
                    .password(standardPassword)
                    .build());
            }

            var userDetails = userService.loadUserByUsername(name);

            var oidcUser = new DefaultOidcUser(userDetails.getAuthorities(), userRequest.getIdToken());

            Set<Method> userDetailsMethods = Set.of(UserDetails.class.getMethods());

            return (OidcUser) Proxy.newProxyInstance(SecurityConfiguration.class.getClassLoader(),
                new Class[]{UserDetails.class, OidcUser.class},
                ((proxy, method, args) -> userDetailsMethods.contains(method)
                    ? method.invoke(userDetails, args)
                    : method.invoke(oidcUser, args)));
        };
    }
}
