package net.example.config;

import lombok.RequiredArgsConstructor;
import net.example.domain.entity.User;
import net.example.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Set;

import static net.example.domain.enums.Role.USER;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final UserService userService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(urlConfig -> urlConfig
                .requestMatchers("/login", "/user/registration", "/swagger-ui*").permitAll()
                .anyRequest().authenticated()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .deleteCookies("JSESSIONID"))
            .formLogin(login -> login
                .loginPage("/login")
                .defaultSuccessUrl("/api/v1/files"))
            .oauth2Login(config -> config
                .loginPage("/login")
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

                userService.create(User.builder()
                    .email(email)
                    .name(name)
                    .role(USER)
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
