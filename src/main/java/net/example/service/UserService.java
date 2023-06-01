package net.example.service;

import lombok.RequiredArgsConstructor;
import net.example.database.repository.UserRepository;
import net.example.domain.entity.User;
import net.example.dto.UserCreateDto;
import net.example.mapper.UserCreateMapper;
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

    private final UserCreateMapper userCreateMapper;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public User create(UserCreateDto user) {

        return userRepository.save(userCreateMapper.mapFrom(user));
    }

    @Transactional
    public Optional<User> update(User changedUser) {
        return userRepository.findById(changedUser.getId())
            .map(entity -> buildUser(changedUser, entity))
            .map(userRepository::saveAndFlush);
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

    public Optional<User> findByName(String name) {
        return userRepository.findByName(name);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByName(username)
            .map(user -> new org.springframework.security.core.userdetails.User(
                user.getName(),
                user.getPassword(),
                Collections.singleton(user.getRole())
            )).orElseThrow(() -> new UsernameNotFoundException("Not found User:" + username));
    }
}
