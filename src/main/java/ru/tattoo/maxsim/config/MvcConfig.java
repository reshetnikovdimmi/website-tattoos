package ru.tattoo.maxsim.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    final String TEMPLATE_PREFIX = "/WEB-INF/views/";

    public void addViewControllers(ViewControllerRegistry registry) {

        registry.addViewController("/Index").setViewName("Index");
        registry.addViewController("/").setViewName("Index");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(
                        "/fonts/**",
                        "/img/**",
                        "/css/**",
                        "/js/**")
                .addResourceLocations(
                        TEMPLATE_PREFIX + "fonts/",
                        TEMPLATE_PREFIX + "img/",
                        TEMPLATE_PREFIX + "css/",
                        TEMPLATE_PREFIX + "js/");
    }
}