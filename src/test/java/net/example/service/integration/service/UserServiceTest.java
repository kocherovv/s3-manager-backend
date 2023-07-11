package net.example.service.integration.service;

import lombok.RequiredArgsConstructor;
import net.example.domain.entity.User;
import net.example.domain.enums.Role;
import net.example.service.UserService;
import net.example.service.integration.IntegrationTestBase;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor
class UserServiceTest extends IntegrationTestBase {

    private final UserService userService;

    @Test
    void findAll() {
        var result = userService.findAll();
        assertEquals(3, result.size());
    }

    @Test
    void findById() {
        var result = userService.findById(1L);

        var expected = Optional.of(User.builder()
            .id(1L)
            .name("DaniilKim")
            .email("kim.dany@yandex.ru")
            .role(Role.USER)
            .password(null)
            .build());

        assertEquals(expected, result);
    }
}