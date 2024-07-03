package ru.tattoo.maxsim.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public interface SaveImg {
    default void saveImg(MultipartFile fileImport, String UPLOAD_DIRECTORY) throws IOException {
        Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY , fileImport.getOriginalFilename());
        Files.write(fileNameAndPath, fileImport.getBytes());
    }
}
