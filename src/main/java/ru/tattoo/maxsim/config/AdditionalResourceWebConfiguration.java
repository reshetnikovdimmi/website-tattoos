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
        Path uploadDirectory = Paths.get(System.getProperty("user.dir"), "img", "images");

        registry.addResourceHandler("/images/**").addResourceLocations(uploadDirectory.toUri().toString());
    }
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
