package net.example.service;

import lombok.RequiredArgsConstructor;
import net.example.domain.entity.File;
import net.example.repository.FileRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.history.Revision;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RevisionService {

    private final FileRepository fileRepository;

    public Page<Revision<Long, File>> findFileRevisions(Long fileId, Long fromPage, Long pageSize) {
        var page = PageRequest.of(Math.toIntExact(fromPage / pageSize),
            Math.toIntExact(pageSize));

        return fileRepository.findRevisions(fileId, page);
    }
}
