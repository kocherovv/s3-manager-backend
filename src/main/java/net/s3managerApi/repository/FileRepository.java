package net.s3managerApi.repository;

import net.s3managerApi.domain.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

public interface FileRepository extends
    JpaRepository<File, Long>,
    RevisionRepository<File, Long, Long> {
}
