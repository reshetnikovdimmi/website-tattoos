package ru.tattoo.maxsim.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BusinessEventLogger {

    private static final Logger BUSINESS_LOG = LoggerFactory.getLogger("BUSINESS_EVENTS");

    public void logImageUpload(String username, Long imageId, String category) {
        BUSINESS_LOG.info("ImageUpload: user={}, imageId={}, category={}",
                username, imageId, category);
    }

    public void logBestImageFlag(Long imageId, boolean flag, String username) {
        BUSINESS_LOG.info("BestImageFlag: imageId={}, flag={}, user={}",
                imageId, flag, username);
    }
}
