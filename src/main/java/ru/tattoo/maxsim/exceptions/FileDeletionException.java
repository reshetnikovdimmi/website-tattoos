package ru.tattoo.maxsim.exceptions;

import java.io.IOException;

public class FileDeletionException extends RuntimeException{
    public FileDeletionException(String message, IOException e) {
        super(message);
    }
}
