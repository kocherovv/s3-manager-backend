package net.example.service;

import lombok.RequiredArgsConstructor;
import net.example.database.repository.FileRepository;
import net.example.database.repository.UserRepository;
import net.example.domain.entity.File;
import net.example.domain.entity.User;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RevisionService {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    public Optional<Revision<Long, File>> findFileLastChangeRevision(Long id) {
        return fileRepository.findLastChangeRevision(id);
    }

    public Revisions<Long, File> findFileRevisions(Long id) {
        return fileRepository.findRevisions(id);
    }

    public Optional<Revision<Long, User>> findUserLastChangeRevision(Long id) {
        return userRepository.findLastChangeRevision(id);
    }

    public Revisions<Long, User> findUserRevisions(Long id) {
        return userRepository.findRevisions(id);
    }

}
