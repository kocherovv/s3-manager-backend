package net.s3managerApi.mapper;

import lombok.RequiredArgsConstructor;
import net.s3managerApi.domain.entity.User;
import net.s3managerApi.dto.UserCreateDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCreateMapper implements Mapper<User, UserCreateDto> {

    private final PasswordEncoder passwordEncoder;

    @Override
    public User mapFrom(UserCreateDto source) {
        return User.builder()
            .password(passwordEncoder.encode(source.getPassword()))
            .name(source.getName())
            .email(source.getEmail())
            .role(source.getRole())
            .build();
    }
}
