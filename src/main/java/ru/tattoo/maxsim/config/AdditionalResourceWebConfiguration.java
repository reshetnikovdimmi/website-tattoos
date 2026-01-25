package ru.tattoo.maxsim.config;


import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;


@Configuration
public class AdditionalResourceWebConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDirectory = Paths.get("" +
                "/app/img/images");  // Путь внутри контейнера

        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:", uploadDirectory.toUri().toString());  // Обратите внимание на prefix "file:"
    }
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
