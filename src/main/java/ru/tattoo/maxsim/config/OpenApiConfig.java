package ru.tattoo.maxsim.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Wallet Service System Api",
                description = "Wallet Service", version = "1.0.0",
                contact = @Contact(
                        name = "Дмитрий",
                        email = "reshetnikov.dim.mi@yandex.ru"
                )
        )
)
public class OpenApiConfig {

}
