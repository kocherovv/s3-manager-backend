package net.example.service.AWS;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3service {

    private final AmazonS3 s3Client;

    @Value("${spring.application.bucket.name}")
    private String bucketName;

    @SneakyThrows
    public void uploadFile(String fileName,
                           String extension,
                           MultipartFile multipartFile) {

        var metadata = new ObjectMetadata();
        metadata.setContentType(extension);
        metadata.setContentLength(multipartFile.getSize());

        fileName = buildS3FileName(fileName);

        s3Client.putObject(
            bucketName,
            fileName,
            multipartFile.getInputStream(),
            metadata);
    }

    @SneakyThrows
    public S3Object downloadFile(String fileName) {
        return s3Client.getObject(bucketName, buildS3FileName(fileName));
    }

    public void deleteFile(String fileName) {
        s3Client.deleteObject(bucketName, buildS3FileName(fileName));
    }

    public void renameFile(String oldName, String newName) {

        var oldS3Name = buildS3FileName(oldName);
        var newS3Name = buildS3FileName(newName);

        s3Client.copyObject(
            bucketName,
            oldS3Name,
            bucketName,
            newS3Name);

        s3Client.deleteObject(bucketName, oldS3Name);
    }

    private String buildS3FileName(String fileName) {
        var principal = (UserDetails) SecurityContextHolder.getContext()
            .getAuthentication()
            .getPrincipal();

        return principal.getUsername().replaceAll("\\s", "") + "/" + fileName;
    }
}
