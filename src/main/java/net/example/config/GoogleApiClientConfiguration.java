package net.example.config;


import com.google.api.client.googleapis.apache.v2.GoogleApacheHttpTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Configuration
@RequiredArgsConstructor
public class GoogleApiClientConfiguration {

    private final Environment environment;
    private final List<String> SCOPES = Arrays.asList("email", "profile");

    @Bean
    public GoogleAuthorizationCodeFlow authorizationCodeFlow() throws GeneralSecurityException, IOException {

        return new GoogleAuthorizationCodeFlow(
            GoogleApacheHttpTransport.newTrustedTransport(),
            GsonFactory.getDefaultInstance(),
            Objects.requireNonNull(environment.getProperty("GOOGLE_CLIENT_SECRET")),
            Objects.requireNonNull(environment.getProperty("GOOGLE_CLIENT_ID")),
            SCOPES);
    }

    @Bean
    public GoogleIdTokenVerifier googleIdTokenVerifier() throws GeneralSecurityException, IOException {
        return new GoogleIdTokenVerifier.Builder(
            authorizationCodeFlow().getTransport(),
            authorizationCodeFlow().getJsonFactory())
            .setAudience(Collections.singletonList(environment.getProperty("GOOGLE_CLIENT_ID")))
            .build();
    }
}
