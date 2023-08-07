package net.example.controller;

import lombok.RequiredArgsConstructor;
import net.example.domain.entity.File;
import net.example.dto.FileInfoDto;
import net.example.dto.FileRevisionDto;
import net.example.mapper.FileInfoDtoMapper;
import net.example.mapper.FileRevisionDtoMapper;
import net.example.service.FileService;
import net.example.service.RevisionService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final RevisionService revisionService;

    private final FileInfoDtoMapper fileInfoDtoMapper;
    private final FileRevisionDtoMapper fileRevisionDtoMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR','USER')")
    public List<FileInfoDto> findAll(@RequestParam(required = false) List<Long> ids,
                                     @RequestParam(defaultValue = "0") Long from,
                                     @RequestParam(defaultValue = "10") Long pageSize) {
        return fileService.findAll(ids, from, pageSize).stream()
            .map(fileInfoDtoMapper::mapFrom).toList();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR','USER')")
    public FileInfoDto findById(@PathVariable("id") Long id) {
        return fileInfoDtoMapper.mapFrom(
            fileService.findById(id));
    }

    @GetMapping("/{id}/view")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR','USER')")
    public ResponseEntity<Resource> openById(@PathVariable("id") Long id) {
        var s3Object = fileService.downloadById(id);

        return ResponseEntity.ok()
            .header("Content-Disposition", "inline; filename=\"" + s3Object.getKey() + "\"")
            .contentLength(s3Object.getObjectMetadata().getContentLength())
            .contentType(MediaType.parseMediaType(s3Object.getObjectMetadata().getContentType()))
            .body(new InputStreamResource(s3Object.getObjectContent()));
    }

    @GetMapping(value = "/{id}/download")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR','USER')")
    public ResponseEntity<Resource> download(@PathVariable("id") Long id) {
        var s3Object = fileService.downloadById(id);

        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=\"" + s3Object.getKey() + "\"")
            .contentLength(s3Object.getObjectMetadata().getContentLength())
            .contentType(MediaType.parseMediaType(s3Object.getObjectMetadata().getContentType()))
            .body(new InputStreamResource(s3Object.getObjectContent()));
    }

    @GetMapping(value = "/{id}/history")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR','USER')")
    public List<FileRevisionDto> getHistory(@PathVariable("id") Long id,
                                            @RequestParam(defaultValue = "0") Long from,
                                            @RequestParam(defaultValue = "10") Long pageSize) {
        return revisionService.findFileRevisions(id, from, pageSize).stream()
            .map(fileRevisionDtoMapper::mapFrom)
            .toList();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    public FileInfoDto upload(@RequestBody MultipartFile multipartFile) {
        return fileInfoDtoMapper.mapFrom(
            fileService.create(multipartFile));
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    public FileInfoDto updateName(@PathVariable("id") Long id,
                                  @RequestBody File file) {
        return fileInfoDtoMapper.mapFrom(
            fileService.updateFileName(file, id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('ADMIN','MODERATOR')")
    public boolean delete(@PathVariable("id") Long id) {
        return fileService.deleteById(id);
    }
}
