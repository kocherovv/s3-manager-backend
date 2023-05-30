package net.example.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.history.RevisionMetadata;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Builder
@Data
public class FileRevisionDto {

    private final Long id;

    private final String name;

    private final String extension;

    private final LocalDateTime createdAt;

    private final String createdBy;

    private final String modifiedBy;

    private final Long rev;

    private final RevisionMetadata.RevisionType revType;

    private final LocalDateTime revDate;
}
