package ru.uchebka.config;
import io.swagger.v3.oas.models.OpenAPI;import io.swagger.v3.oas.models.info.Info;import org.springframework.context.annotation.*;
@Configuration public class OpenApiConfig{@Bean OpenAPI uchebkaOpenApi(){return new OpenAPI().info(new Info().title("Uchebka API").description("API образовательной платформы генерации тестов").version("1.0.0"));}}