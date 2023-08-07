package net.s3managerApi.controller;

import lombok.RequiredArgsConstructor;
import net.s3managerApi.domain.entity.User;
import net.s3managerApi.dto.UserCreateDto;
import net.s3managerApi.dto.UserReadDto;
import net.s3managerApi.mapper.UserReadMapper;
import net.s3managerApi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final UserReadMapper userReadMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    public List<UserReadDto> findAll(@RequestParam(required = false) List<Long> ids,
                                     @RequestParam(defaultValue = "0") Long from,
                                     @RequestParam(defaultValue = "10") Long pageSize) {
        return userService.findAll(ids, from, pageSize).stream()
            .map(userReadMapper::mapFrom)
            .toList();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    public UserReadDto findById(@PathVariable("id") Long id) {
        return userService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public UserReadDto create(@RequestBody UserCreateDto userCreateDto) {
        return userService.create(userCreateDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    public UserReadDto update(@PathVariable("id") User user) {
        return userReadMapper.mapFrom(userService.update(user));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    public boolean delete(@PathVariable("id") Long id) {
        return userService.deleteById(id);
    }
}
