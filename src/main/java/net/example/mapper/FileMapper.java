package net.example.mapper;

import lombok.RequiredArgsConstructor;
import net.example.domain.entity.File;
import net.example.domain.entity.User;
import net.example.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public File mapWithPrincipal(MultipartFile source) {
        return File.builder()
            .name(source.getOriginalFilename())
            .extension(source.getContentType())
            .user(userService.findByName(SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getName())
                .orElse(
                    User.builder()
                    .id(0L)
                    .name("System")
                    .build()))
            .build();
    }
}
