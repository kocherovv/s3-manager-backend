package net.s3managerApi.mapper;

import net.s3managerApi.domain.entity.File;
import net.s3managerApi.dto.FileInfoDto;
import org.springframework.stereotype.Component;

@Component
public class FileInfoDtoMapper implements Mapper<FileInfoDto, File> {

    @Override
    public FileInfoDto mapFrom(File source) {
        return FileInfoDto.builder()
            .id(source.getId())
            .userId(source.getUser().getId())
            .name(source.getName())
            .extension(source.getExtension())
            .modifiedAt(source.getModifiedAt())
            .createdAt(source.getCreatedAt())
            .build();
    }
}
