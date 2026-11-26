package ru.uchebka.config;
import java.time.Duration;import org.springframework.boot.context.properties.ConfigurationProperties;
@ConfigurationProperties(prefix="yandex") public record YandexProperties(String baseUrl,String apiKey,String projectId,String promptId,Duration timeout) {}