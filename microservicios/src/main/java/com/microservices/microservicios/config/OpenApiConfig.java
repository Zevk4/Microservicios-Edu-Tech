package com.microservices.microservicios.config; 

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Gestión Level-UP") // Titulo 
                        .description("API RESTful para la gestión de usuarios, productos y carrito de compras en Level-UP.") // Descripción de la API
                        .version("3.5.2") // Versión de la API
                        .contact(new Contact()
                                .name("Equipo de Persona") // Nombre de contacto
                                .email("experson@duocuc.cl")) // Email de contacto
                        );
    }
}