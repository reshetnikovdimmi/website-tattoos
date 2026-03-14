package ru.tattoo.maxsim.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url("http://localhost:" + serverPort).description("Локальный сервер")
                ))
                .info(new Info()
                        .title("Tattoo Studio Maxsim API")
                        .description("API для управления тату-студией Maxsim")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Maxsim Studio")
                                .email("info@maxsim.ru")
                                .url("https://maxsim.ru"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")));
    }
}