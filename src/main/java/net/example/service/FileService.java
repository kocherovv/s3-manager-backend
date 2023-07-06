package net.example.service;

import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import net.example.domain.entity.File;
import net.example.exception.NotFoundException;
import net.example.repository.FileRepository;
import net.example.service.AWS.S3service;
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

    private final S3service s3Service;

    public List<File> findAll() {
        return fileRepository.findAll();
    }

    public Optional<File> findById(Long id) {
        return fileRepository.findById(id);
    }

    @Transactional
    public File create(File entity, MultipartFile multipartFile) {
            s3Service.uploadFile(
                entity.getName(),
                entity.getExtension(),
                multipartFile);

            return fileRepository.save(entity);
    }

    @Transactional
    public File updateName(File changedFile) {

        return fileRepository.findById(changedFile.getId())
            .stream()
            .peek(it -> s3Service.renameFile(it.getName(), changedFile.getName()))
            .map(oldFile -> buildFile(changedFile, oldFile))
            .map(fileRepository::save)
            .findFirst()
            .orElseThrow(NotFoundException::new);
    }

    @Transactional
    public boolean deleteById(Long id) {
        return fileRepository.findById(id)
            .map(entity -> {
                s3Service.deleteFile(entity.getName());
                fileRepository.delete(entity);
                return true;
            })
            .orElse(false);
    }

    public S3Object downloadById(Long id) {
        return fileRepository.findById(id)
            .map(file -> s3Service.downloadFile(file.getName()))
            .orElseThrow(NotFoundException::new);
    }

    private File buildFile(File changedFile, File oldFile) {
        oldFile.setName(changedFile.getName());

        return oldFile;
    }
}
