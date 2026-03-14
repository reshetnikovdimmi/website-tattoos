package ru.tattoo.maxsim.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.modelmapper.ModelMapper;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class AdditionalResourceWebConfiguration implements WebMvcConfigurer {

    @Value("${upload.directory:/app/img/images}")
    private String uploadDirectory;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Используем путь из конфигурации
        Path uploadPath = Paths.get(uploadDirectory).toAbsolutePath().normalize();

        // Создаем директорию если её нет
        try {
            java.nio.file.Files.createDirectories(uploadPath);
            System.out.println("📁 Раздача статики из: " + uploadPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Раздаем загруженные изображения
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + uploadPath + "/");

        // Раздаем статические файлы (CSS, JS)
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}