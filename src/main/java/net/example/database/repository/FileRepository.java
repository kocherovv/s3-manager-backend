package net.example.database.repository;

import net.example.domain.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends
    JpaRepository<File, Long>,
    RevisionRepository<File, Long, Long> {

    List<File> findAll();

    List<File> findAllById(Iterable<Long> longs);

    Optional<File> findById(Long aLong);

    <S extends File> S save(S entity);

    void deleteById(Long aLong);

    void delete(File entity);

    void deleteAll();
}
