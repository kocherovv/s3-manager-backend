package net.example.controller;

import lombok.RequiredArgsConstructor;
import net.example.domain.entity.File;
import net.example.domain.entity.UserDetailsCustom;
import net.example.dto.FileInfoDto;
import net.example.dto.FileRevisionDto;
import net.example.exception.NotFoundException;
import net.example.mapper.FileInfoDtoMapper;
import net.example.mapper.FileMapper;
import net.example.mapper.FileRevisionDtoMapper;
import net.example.service.FileService;
import net.example.service.RevisionService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final RevisionService revisionService;

    private final FileInfoDtoMapper fileInfoDtoMapper;
    private final FileMapper fileMapper;
    private final FileRevisionDtoMapper fileRevisionDtoMapper;


    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR','USER')")
    public List<FileInfoDto> findAll() {

        return fileService.findAll().stream()
            .map(fileInfoDtoMapper::mapFrom).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR','USER')")
    public FileInfoDto findById(@PathVariable("id") Long id) {

        return fileService.findById(id)
            .map(fileInfoDtoMapper::mapFrom)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/{id}/view")
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR','USER')")
    public ResponseEntity<Resource> openById(@AuthenticationPrincipal UserDetailsCustom userDetails,
                                             @PathVariable("id") Long id) {

        var content = fileService.downloadById(userDetails, id);

        return ResponseEntity.ok()
            .contentLength(content.getObjectMetadata().getContentLength())
            .contentType(MediaType.parseMediaType(content.getObjectMetadata().getContentType()))
            .body(new InputStreamResource(content.getObjectContent()));
    }

    @GetMapping(value = "/{id}/download")
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR','USER')")
    public ResponseEntity<Resource> download(@AuthenticationPrincipal UserDetailsCustom userDetailsCustom,
                                             @PathVariable("id") Long id) {

        var content = fileService.downloadById(userDetailsCustom, id);

        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=\"" + content.getKey() + "\"")
            .contentLength(content.getObjectMetadata().getContentLength())
            .contentType(MediaType.parseMediaType(content.getObjectMetadata().getContentType()))
            .body(new InputStreamResource(content.getObjectContent()));
    }

    @GetMapping(value = "/{id}/history")
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR','USER')")
    public List<FileRevisionDto> getHistory(@PathVariable("id") Long id) {

        return revisionService.findFileRevisions(id).stream()
            .map(fileRevisionDtoMapper::mapFrom)
            .toList();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    public FileInfoDto upload(@AuthenticationPrincipal UserDetailsCustom userDetails,
                              @RequestBody MultipartFile multipartFile) {

        return fileInfoDtoMapper.mapFrom(
            fileService.create(
                userDetails,
                fileMapper.mapFrom(multipartFile, userDetails),
                multipartFile));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    public ResponseEntity<?> updateName(@AuthenticationPrincipal UserDetailsCustom userDetails,
                                        @PathVariable("id") Long id,
                                        @RequestBody File file) {
        try {
            file.setId(id);
            fileService.updateName(userDetails, file);

            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    public ResponseEntity<?> delete(@AuthenticationPrincipal UserDetailsCustom userDetailsCustom,
                                    @PathVariable("id") Long id) {

        if (!fileService.deleteById(userDetailsCustom, id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity
            .noContent()
            .build();
    }
}
