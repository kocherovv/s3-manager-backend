package net.s3managerApi.mapper;

import net.s3managerApi.domain.entity.User;
import net.s3managerApi.dto.UserReadDto;
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
