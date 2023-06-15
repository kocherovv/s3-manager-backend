package net.example.controller;

import lombok.RequiredArgsConstructor;
import net.example.dto.UserCreateDto;
import net.example.dto.requestDto.JwtResponse;
import net.example.dto.requestDto.LoginRequest;
import net.example.dto.requestDto.SignUpRequest;
import net.example.security.jwt.JwtTokenUtil;
import net.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenUtil jwtTokenUtil;

    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> authUser(@RequestBody LoginRequest loginRequest) {
        try {
            var authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            var jwt = jwtTokenUtil.generateToken((UserDetails) authentication.getPrincipal());

            var userDetails = (UserDetails) authentication.getPrincipal();

            return ResponseEntity.ok(JwtResponse.builder()
                .token(jwt)
                .username(userDetails.getUsername())
                .build());
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/registration")
    public ResponseEntity<?> authUser(@RequestBody SignUpRequest registrationDto) {
        try {
            userService.create(UserCreateDto.builder()
                .email(registrationDto.getEmail())
                .name(registrationDto.getUsername())
                .password(registrationDto.getPassword())
                .build());

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
