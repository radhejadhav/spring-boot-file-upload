package com.file_upload.controllers;

import com.file_upload.services.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("file")
    public String  uploadFile(@RequestParam("file") MultipartFile file){

        String fileName = this.fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile")
                .path(fileName)
                .toUriString();

        return fileDownloadUri;
    }

    @GetMapping("/file/{fileName:.+}")
    public Resource downloadFile(@PathVariable String fileName, HttpServletRequest request){

        Resource resource = this.fileStorageService.loadFileAsResource(fileName);
        String contentType=null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        }catch (IOException e){

        }
        if(contentType == null){
            contentType = "application/octet-stream";
        }
        return resource;
    }
}
