package com.file_upload.services;

import com.file_upload.configurations.FileStorageProperties;
import com.file_upload.exceptions.FileNotFoundException;
import com.file_upload.exceptions.FileStorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

//    @Autowired
    private String fileStorageProperties = "./upload";

    public FileStorageService() {
        this.fileStorageLocation = Paths.get(this.fileStorageProperties).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        }catch (Exception e){
            throw new FileStorageException("Could not create the directory");
        }
    }

    public String storeFile(MultipartFile file){

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (fileName.contains("..")){
                throw new FileStorageException("File Name Contain Invalid Path");
            }
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(),targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        }catch (IOException e){
            throw new FileStorageException("Could Not Store File");
        }
    }
    public Resource loadFileAsResource(String fileName){
        try {
            Path filePath =this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            }else {
                throw new FileNotFoundException("File Not Found");
            }
        }catch (MalformedURLException e){
            throw new FileNotFoundException("file Not Found");
        }
    }
}
