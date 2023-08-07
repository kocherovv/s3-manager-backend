package net.s3managerApi.repository;

import net.s3managerApi.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.Optional;

public interface UserRepository extends
    JpaRepository<User, Long>,
    RevisionRepository<User, Long, Long> {

    Optional<User> findByName(String username);
}
