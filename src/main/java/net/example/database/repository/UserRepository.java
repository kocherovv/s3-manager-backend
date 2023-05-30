package net.example.database.repository;

import net.example.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends
    JpaRepository<User, Long>,
    RevisionRepository<User, Long, Long> {

    List<User> findAll();

    Optional<User> findById(Long aLong);

    <S extends User> S save(S entity);

    void deleteById(Long aLong);

    void delete(User entity);

    Optional<User> findByName(String username);
}
