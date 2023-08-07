package net.s3managerApi.mapper;

import lombok.RequiredArgsConstructor;
import net.s3managerApi.domain.entity.File;
import net.s3managerApi.domain.entity.User;
import net.s3managerApi.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class FileMapper implements Mapper<File, MultipartFile> {
    private final UserRepository userRepository;

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
            .user(userRepository.findByName(SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getName())
                .orElse(
                    User.builder()
                        .name("System")
                        .build()))
            .build();
    }
}
