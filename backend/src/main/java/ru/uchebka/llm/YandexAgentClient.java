package ru.uchebka.llm;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.uchebka.config.YandexProperties;

@Component
public class YandexAgentClient implements LlmClient {
    private static final Logger log = LoggerFactory.getLogger(YandexAgentClient.class);

    private final RestClient client;
    private final YandexProperties properties;

    public YandexAgentClient(RestClient yandexRestClient, YandexProperties properties) {
        this.client = yandexRestClient;
        this.properties = properties;
    }

    @Override
    @Retry(name = "yandex")
    @CircuitBreaker(name = "yandex")
    public String generate(String instruction, String material) {
        if (properties.apiKey() == null || properties.apiKey().isBlank()) {
            throw new IllegalStateException("YANDEX_API_KEY is not configured");
        }
        if (properties.promptId() == null || properties.promptId().isBlank()) {
            throw new IllegalStateException("YANDEX_PROMPT_ID is not configured");
        }

        log.info("Calling Yandex Agent; materialLength={}", material.length());

        Map<String, Object> webSearch = Map.of(
                "type", "web_search",
                "filters", Map.of("allowed_domains", List.of()),
                "search_context_size", "medium"
        );
        Map<String, Object> body = Map.of(
                "prompt", Map.of("id", properties.promptId()),
                "input", instruction + "\n\nУЧЕБНЫЙ МАТЕРИАЛ:\n" + material,
                "tools", List.of(webSearch)
        );

        Map<?, ?> response = client.post()
                .uri("/responses")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.apiKey())
                .header("OpenAI-Project", properties.projectId() == null ? "" : properties.projectId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);

        String output = extractOutputText(response);
        if (output == null || output.isBlank()) {
            Object status = response == null ? null : response.get("status");
            Object responseId = response == null ? null : response.get("id");
            log.error("Yandex Responses API returned no text; responseId={}, status={}, keys={}",
                    responseId, status, response == null ? null : response.keySet());
            throw new IllegalStateException(
                    "Yandex Agent returned no text (response status: " + status + ")");
        }

        return output;
    }

    /**
     * The raw Responses API JSON contains output[].content[].text.
     * output_text is a convenience property added by the official SDKs,
     * not a guaranteed field in the HTTP response body.
     */
    private String extractOutputText(Map<?, ?> response) {
        if (response == null) {
            return null;
        }

        Object outputValue = response.get("output");
        if (!(outputValue instanceof List<?> outputItems)) {
            return null;
        }

        StringJoiner text = new StringJoiner("\n");
        for (Object outputItem : outputItems) {
            if (!(outputItem instanceof Map<?, ?> item)) {
                continue;
            }

            Object contentValue = item.get("content");
            if (!(contentValue instanceof List<?> contentItems)) {
                Object refusal = item.get("refusal");
                if (refusal instanceof String refusalText && !refusalText.isBlank()) {
                    text.add(refusalText);
                }
                continue;
            }

            for (Object contentItem : contentItems) {
                if (!(contentItem instanceof Map<?, ?> content)) {
                    continue;
                }

                Object type = content.get("type");
                Object value = content.get("text");
                if ("output_text".equals(type) && value instanceof String outputText
                        && !outputText.isBlank()) {
                    text.add(outputText);
                } else if ("refusal".equals(type) && content.get("refusal") instanceof String refusalText
                        && !refusalText.isBlank()) {
                    text.add(refusalText);
                }
            }
        }

        String result = text.toString().trim();
        return result.isEmpty() ? null : result;
    }
}
