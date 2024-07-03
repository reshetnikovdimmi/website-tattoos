package ru.tattoo.maxsim.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public interface DeleteImg {

   default void deleteImg(String name, String UPLOAD_DIRECTORY) throws IOException {
       Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, name);
       Files.delete(fileNameAndPath);
   }
}
