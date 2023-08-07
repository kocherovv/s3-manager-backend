package net.s3managerApi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.s3managerApi.dto.UserCreateDto;
import net.s3managerApi.dto.UserReadDto;
import net.s3managerApi.dto.requestDto.JwtResponse;
import net.s3managerApi.dto.requestDto.LoginRequest;
import net.s3managerApi.dto.requestDto.SignUpRequest;
import net.s3managerApi.service.AuthService;
import net.s3managerApi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public JwtResponse authUser(@RequestBody LoginRequest loginRequest) {
        var authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
            loginRequest.getUsername(),
            loginRequest.getPassword()));

        log.info("POST /api/v1/auth success authUser: {}", loginRequest.getUsername());

        return authService.getToken(authentication);
    }

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public UserReadDto registrationUser(@RequestBody SignUpRequest registrationDto) {
        log.info("POST /api/v1/auth/registration newUser: {}", registrationDto.getUsername());

        return userService.create(UserCreateDto.builder()
            .email(registrationDto.getEmail())
            .name(registrationDto.getUsername())
            .password(registrationDto.getPassword())
            .build());
    }
}
