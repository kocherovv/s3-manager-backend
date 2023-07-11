package net.example.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import lombok.RequiredArgsConstructor;
import net.example.dto.UserCreateDto;
import net.example.dto.requestDto.JwtResponse;
import net.example.security.jwt.JwtTokenUtil;
import net.example.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/api/v1/oauth2/google")
@RequiredArgsConstructor
public class Oauth2Google {
    private final GoogleAuthorizationCodeFlow flow;
    private final GoogleIdTokenVerifier googleIdTokenVerifier;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;

    @Value("${spring.security.oauth2.google.redirect-url}")
    private final String callBackUrl;

    @GetMapping
    public String authorizeWithGoogle() {
        var authorizationUrl = flow.newAuthorizationUrl()
            .setRedirectUri(callBackUrl);

        return authorizationUrl.build();
    }

    @GetMapping("/callback")
    public ResponseEntity<?> handleCallback(@RequestParam("code") String authorizationCode) throws IOException, GeneralSecurityException {
        var tokenRequest = flow.newTokenRequest(authorizationCode).setRedirectUri(callBackUrl);
        var tokenResponse = tokenRequest.execute();
        var googleTokenResponse = flow.createAndStoreCredential(tokenResponse, null);

        var googleIdToken = googleIdTokenVerifier.verify(googleTokenResponse.getAccessToken());

        if (googleIdToken != null) {
            var payload = googleIdToken.getPayload();
            var email = payload.getEmail();
            var username = email.substring(0, email.indexOf('@'));

            if (userService.findByName(username).isEmpty()) {
                userService.create(UserCreateDto.builder()
                    .name(username)
                    .email(email)
                    .build());
            }
            var userDetails = userService.loadUserByUsername(username);

            return ResponseEntity.ok(JwtResponse.builder()
                .token(jwtTokenUtil.generateToken(userDetails))
                .username(userDetails.getUsername())
                .build());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
