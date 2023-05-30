package net.example.mapper;

import net.example.domain.entity.User;
import net.example.dto.UserReadDto;
import org.springframework.stereotype.Component;

@Component
public class UserReadMapper implements Mapper<UserReadDto, User> {

    @Override
    public UserReadDto mapFrom(User source) {
        return UserReadDto.builder()
            .id(source.getId())
            .name(source.getName())
            .email(source.getEmail())
            .role(source.getRole())
            .build();
    }
}
