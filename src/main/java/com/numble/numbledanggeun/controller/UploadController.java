package com.numble.numbledanggeun.controller;

import com.numble.numbledanggeun.file.FileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RequiredArgsConstructor
@RestController
public class UploadController {

    private final FileStore filestore;

    @GetMapping("/img/{storeFileName}")
    public ResponseEntity<Resource> downloadImage(@RequestParam("folderPath") String folderPath,
                                                 @PathVariable("storeFileName") String storeFileName) throws IOException {

        String fullPath = filestore.getFullPath(folderPath, storeFileName);

        //MIME 타입
        HttpHeaders header = new HttpHeaders();
        header.add("Content-Type", Files.probeContentType(Paths.get(fullPath)));

        return new ResponseEntity<>(new UrlResource("file:" + fullPath), header, HttpStatus.OK);
    }


}
