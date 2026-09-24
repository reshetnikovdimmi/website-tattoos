package ru.tattoo.maxsim.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AdditionalResourceWebConfiguration implements WebMvcConfigurer {

    private final StorageProperties props;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(props.getUploadDir()).toAbsolutePath().normalize();

        try {
            Files.createDirectories(uploadPath);
            log.info("Serving {}/** from {}", props.getUrlPrefix(), uploadPath);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create upload dir: " + uploadPath, e);
        }

        registry.addResourceHandler(props.getUrlPrefix() + "/**")
            .addResourceLocations("file:" + uploadPath + "/")
            .setCachePeriod(props.getCacheSeconds());

        registry.addResourceHandler("/static/**")
            .addResourceLocations("classpath:/static/")
            .setCachePeriod(props.getCacheSeconds());
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
