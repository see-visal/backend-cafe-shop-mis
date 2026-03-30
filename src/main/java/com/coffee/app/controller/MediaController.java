package com.coffee.app.controller;

import com.coffee.app.service.FileStorageService;
import java.time.Duration;
import lombok.Generated;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/media"})
public class MediaController {
   private final FileStorageService fileStorageService;

   @GetMapping({"/storage/image"})
   public ResponseEntity<byte[]> getStorageImage(@RequestParam String path) {
      FileStorageService.StoredFile storedFile = this.fileStorageService.downloadFile(path);
      MediaType mediaType = MediaType.parseMediaType(storedFile.contentType() != null ? storedFile.contentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE);
      return ResponseEntity.ok()
         .cacheControl(CacheControl.maxAge(Duration.ofHours(1)).cachePublic())
         .contentType(mediaType)
         .body(storedFile.content());
   }

   @Generated
   public MediaController(final FileStorageService fileStorageService) {
      this.fileStorageService = fileStorageService;
   }
}
