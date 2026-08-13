package ru.tattoo.maxsim.model.DTO;



public record SmtpTestResult(
        boolean success,
        String debugLog,
        String errorMessage
) {}
