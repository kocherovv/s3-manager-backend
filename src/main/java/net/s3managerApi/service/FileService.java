package net.s3managerApi.service;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import net.s3managerApi.domain.entity.File;
import net.s3managerApi.exception.EntityNotFoundException;
import net.s3managerApi.mapper.FileMapper;
import net.s3managerApi.repository.FileRepository;
import net.s3managerApi.service.AWS.S3service;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileService {

    private final FileRepository fileRepository;

    private final S3service s3Service;

    private final FileMapper fileMapper;

    public List<File> findAll(List<Long> ids, Long fromPage, Long pageSize) {
        Iterable<File> files;
        if (ids != null) {
            files = fileRepository.findAllById(ids);
        } else if (fromPage != null & pageSize != null) {
            var page = PageRequest.of(Math.toIntExact(fromPage / pageSize),
                Math.toIntExact(pageSize));
            files = fileRepository.findAll(page);
        } else {
            files = fileRepository.findAll();
        }

        return StreamSupport.stream(files.spliterator(), false)
            .toList();
    }

    public File findById(Long id) {
        return fileRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(String.format("File with id=%d is not exist", id)));
    }

    @Transactional
    public File create(MultipartFile multipartFile) throws AmazonS3Exception {
        s3Service.uploadFile(multipartFile);

        return fileRepository.save(
            fileMapper.mapWithPrincipal(multipartFile));
    }

    @Transactional
    public File updateFileName(File inputFile, Long id) {

        return fileRepository.findById(id)
            .stream()
            .peek(it -> s3Service.renameFile(it.getName(), inputFile.getName()))
            .peek(oldFile -> oldFile.setName(inputFile.getName()))
            .map(fileRepository::save)
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException(String.format("File with id=%d is not exist", id)));
    }

    @Transactional
    public boolean deleteById(Long id) {
        return fileRepository.findById(id)
            .map(entity -> {
                s3Service.deleteFile(entity.getName());
                fileRepository.delete(entity);
                return true;
            }).orElse(false);
    }

    public S3Object downloadById(Long id) {
        return fileRepository.findById(id)
            .map(file -> s3Service.downloadFile(file.getName()))
            .orElseThrow(() -> new EntityNotFoundException(String.format("File with id=%d is not exist", id)));
    }
}
