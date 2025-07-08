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
                        .title("API de Gestión Edu-Tech") // Titulo 
                        .description("API RESTful para la gestión de usuarios, roles, cursos y evaluaciones en la plataforma Edu-Tech.") // Descripción de la API
                        .version("3.2.2") // Versión de la API
                        .contact(new Contact()
                                .name("Equipo de X") // Nombre de contacto
                                .email("equipox2319@duocuc.cl")) // Email de contacto
                        );
    }
}