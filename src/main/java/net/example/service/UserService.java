package net.example.service;

import lombok.RequiredArgsConstructor;
import net.example.domain.entity.User;
import net.example.domain.entity.UserDetailsCustom;
import net.example.dto.UserCreateDto;
import net.example.dto.UserReadDto;
import net.example.exception.NotFoundException;
import net.example.mapper.UserCreateMapper;
import net.example.mapper.UserReadMapper;
import net.example.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final UserReadMapper userReadMapper;

    private final UserCreateMapper userCreateMapper;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
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
            .orElseThrow(NotFoundException::new);
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

    public Optional<User> findByName(String name) {
        return userRepository.findByName(name);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByName(username)
            .map(user -> new UserDetailsCustom(
                user.getName(),
                Collections.singleton(user.getRole())
            )).orElseThrow(() -> new UsernameNotFoundException("Not found User:" + username));
    }

    private User buildUser(User user, User entity) {
        entity.setName(user.getName());
        entity.setEmail(user.getEmail());
        entity.setRole(user.getRole());

        return entity;
    }
}
