package net.s3managerApi.service.AWS;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3service {

    private final AmazonS3 s3Client;

    @Value("${spring.application.bucket.name}")
    private String bucketName;

    @SneakyThrows(IOException.class)
    public void uploadFile(MultipartFile multipartFile) {
        if (s3Client.doesObjectExist(bucketName, multipartFile.getOriginalFilename())) {
            throw new AmazonS3Exception("File with the same name already exist in the bucket");
        }

        var metadata = new ObjectMetadata();
        metadata.setContentType(multipartFile.getContentType());
        metadata.setContentLength(multipartFile.getSize());

        s3Client.putObject(
            bucketName,
            multipartFile.getOriginalFilename(),
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
