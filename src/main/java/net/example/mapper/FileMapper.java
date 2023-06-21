package net.example.mapper;

import lombok.RequiredArgsConstructor;
import net.example.domain.entity.File;
import net.example.domain.entity.UserDetailsCustom;
import net.example.exception.NotFoundException;
import net.example.model.AppStatusCode;
import net.example.service.UserService;
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

    public File mapFrom(MultipartFile source, UserDetailsCustom userDetailsCustom) {
        return File.builder()
            .name(source.getOriginalFilename())
            .extension(source.getContentType())
            .user(userService.findByName(userDetailsCustom.getUsername())
                .orElseThrow(() -> new NotFoundException(AppStatusCode.NOT_FOUND_EXCEPTION)))
            .build();
    }
}
