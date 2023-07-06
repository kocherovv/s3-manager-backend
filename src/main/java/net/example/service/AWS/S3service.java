package net.example.service.AWS;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

        s3Client.putObject(
            bucketName,
            fileName,
            multipartFile.getInputStream(),
            metadata);
    }

    public S3Object downloadFile(String fileName) {
        return s3Client.getObject(bucketName, fileName);
    }

    public void deleteFile(String fileName) {
        s3Client.deleteObject(bucketName, fileName);
    }

    public void renameFile(String oldName, String newName) {

        s3Client.copyObject(
            bucketName,
            oldName,
            bucketName,
            newName);

        s3Client.deleteObject(bucketName, oldName);
    }
}
