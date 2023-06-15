package net.example.mapper;

import net.example.domain.entity.File;
import net.example.dto.FileRevisionDto;
import org.springframework.data.history.Revision;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class FileRevisionDtoMapper implements Mapper<FileRevisionDto, Revision<Long, File>> {

    @Override
    public FileRevisionDto mapFrom(Revision<Long, File> source) {

        var file = source.getEntity();
        var metadata = source.getMetadata();

        return FileRevisionDto.builder()
            .id(file.getId())
            .name(file.getName())
            .extension(file.getExtension())
            .createdAt(file.getCreatedAt())
            .createdBy(file.getCreatedBy())
            .modifiedBy(file.getModifiedBy())
            .rev(metadata.getRequiredRevisionNumber())
            .revDate(metadata.getRevisionInstant()
                .map(it -> LocalDateTime.ofInstant(it, ZoneId.systemDefault()))
                .orElse(null))
            .revType(metadata.getRevisionType())
            .build();
    }
}
