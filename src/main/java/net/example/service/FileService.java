package net.example.service;

import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import net.example.database.repository.FileRepository;
import net.example.domain.entity.File;
import net.example.service.AWS.S3Service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileService {

    private final FileRepository fileRepository;

    private final S3Service s3Service;

    public List<File> findAll() {
        return fileRepository.findAll();
    }

    public Optional<File> findById(Long id) {
        return fileRepository.findById(id);
    }

    @Transactional
    public File create(UserDetails user, File entity, MultipartFile multipartFile) {
        s3Service.uploadFile(
            user,
            entity.getName(),
            entity.getExtension(),
            multipartFile);

        return fileRepository.save(entity);
    }

    @Transactional
    public Optional<File> updateName(UserDetails user,
                                     File changedFile) {

        var entity = fileRepository.findById(changedFile.getId());

        entity.ifPresent(file -> s3Service.renameFile(
            user,
            file.getName(),
            changedFile.getName()));

        return entity.map(oldFile -> buildFile(changedFile, oldFile))
            .map(fileRepository::saveAndFlush);
    }

    @Transactional
    public boolean deleteById(UserDetails user, Long id) {
        return fileRepository.findById(id)
            .map(entity -> {
                s3Service.deleteFile(user, entity.getName());
                fileRepository.delete(entity);
                return true;
            })
            .orElse(false);
    }

    public Optional<S3Object> downloadById(UserDetails user, Long id) {
        return fileRepository.findById(id)
            .map(file -> s3Service.downloadFile(user, file.getName()));
    }

    private File buildFile(File file, File entity) {
        entity.setName(file.getName());

        return entity;
    }
}
