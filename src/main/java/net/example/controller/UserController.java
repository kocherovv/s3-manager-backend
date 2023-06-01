package net.example.controller;

import lombok.RequiredArgsConstructor;
import net.example.domain.entity.User;
import net.example.domain.enums.Role;
import net.example.dto.UserCreateDto;
import net.example.dto.UserReadDto;
import net.example.mapper.UserReadMapper;
import net.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final UserReadMapper userReadMapper;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    public List<UserReadDto> findAll() {

        return userService.findAll().stream()
            .map(userReadMapper::mapFrom)
            .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    public UserReadDto findById(@PathVariable("id") Long id) {

        return userService.findById(id)
            .map(userReadMapper::mapFrom)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String create(@RequestBody UserCreateDto userCreateDto) {
        userService.create(userCreateDto);

        return "redirect:/login";
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    public String update(@PathVariable("id") User user) {

        return userService.update(user)
            .map(it -> "redirect:/users/{id}")
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String delete(@PathVariable("id") Long id,
                         @AuthenticationPrincipal UserDetails userDetails) {

        var currentUser = userService.findByName(userDetails.getUsername())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));

        if (currentUser.getRole() == Role.ADMIN) {
            if (!userService.deleteById(id)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }

            return "redirect:/users";
        } else if (Objects.equals(currentUser.getId(), id)) {
            if (!userService.deleteById(id)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }

            return "redirect:/logout";
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
}
