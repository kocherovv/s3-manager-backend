package net.example.config;

import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiAuthConfiguration {

    @Value("${oauth2.authorizationUrl}")
    private String authorizationUrl;

    @Value("${oauth2.tokenUrl}")
    private String tokenUrl;

    @Bean
    public SecurityScheme oauth2() {
        return new SecurityScheme()
            .name("oauth2")
            .type(SecurityScheme.Type.OAUTH2)
            .flows(new OAuthFlows().authorizationCode(
                new OAuthFlow().authorizationUrl(authorizationUrl).tokenUrl(tokenUrl)
            ));
    }
}
