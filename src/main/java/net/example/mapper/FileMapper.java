package net.example.mapper;

import lombok.RequiredArgsConstructor;
import net.example.domain.entity.File;
import net.example.exception.NotFoundException;
import net.example.model.AppStatusCode;
import net.example.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class FileMapper implements Mapper<File, MultipartFile> {

    private final UserService userService;

    @Override
    public File mapFrom(MultipartFile source) {
        return File.builder()
            .name(source.getName())
            .extension(source.getContentType())
            .build();
    }

    public File mapFrom(MultipartFile source, UserDetails userDetails) {
        return File.builder()
            .name(source.getOriginalFilename())
            .extension(source.getContentType())
            .user(userService.findByName(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException(AppStatusCode.NOT_FOUND_EXCEPTION)))
            .build();
    }
}
