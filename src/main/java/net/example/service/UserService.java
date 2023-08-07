package net.example.service;

import lombok.RequiredArgsConstructor;
import net.example.domain.entity.User;
import net.example.dto.UserCreateDto;
import net.example.dto.UserReadDto;
import net.example.exception.EntityNotFoundException;
import net.example.mapper.UserCreateMapper;
import net.example.mapper.UserReadMapper;
import net.example.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    private final UserReadMapper userReadMapper;

    private final UserCreateMapper userCreateMapper;

    public List<User> findAll(List<Long> ids, Long fromPage, Long pageSize) {
        Iterable<User> users;

        if (ids != null) {
            users = userRepository.findAllById(ids);
        } else if (fromPage != null & pageSize != null) {
            var page = PageRequest.of(Math.toIntExact(fromPage / pageSize),
                Math.toIntExact(pageSize));
            users = userRepository.findAll(page);
        } else {
            users = userRepository.findAll();
        }

        return StreamSupport.stream(users.spliterator(), false)
            .toList();
    }

    public UserReadDto findById(Long id) {
        return userRepository.findById(id)
            .map(userReadMapper::mapFrom)
            .orElseThrow(
                () -> new EntityNotFoundException(String.format("User with id=%d is not exist", id)));
    }

    @Transactional
    public UserReadDto create(UserCreateDto user) {
        return userReadMapper.mapFrom(
            userRepository.save(
                userCreateMapper.mapFrom(user)));
    }

    @Transactional
    public User update(User changedUser) {
        return userRepository.findById(changedUser.getId())
            .map(entity -> buildUser(changedUser, entity))
            .map(userRepository::save)
            .orElseThrow(() -> new EntityNotFoundException(
                String.format("User with id=%d is not exist", changedUser.getId())));
    }

    @Transactional
    public boolean deleteById(Long id) {
        return userRepository.findById(id)
            .map(entity -> {
                userRepository.delete(entity);
                return true;
            })
            .orElse(false);
    }

    private User buildUser(User user, User entity) {
        entity.setName(user.getName());
        entity.setEmail(user.getEmail());
        entity.setRole(user.getRole());
        return entity;
    }
}
