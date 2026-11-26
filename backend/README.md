# Backend

Spring Boot REST API. Yandex Agent access is isolated behind the `LlmClient` interface.

## Configuration

Required environment variables:
- `YANDEX_API_KEY` — Yandex AI Studio API key
- `YANDEX_PROJECT_ID` — Yandex Cloud folder/project identifier used by the supplied integration
- `YANDEX_PROMPT_ID` — prompt ID for the configured agent

Optional:
- `YANDEX_BASE_URL` (default: `https://ai.api.cloud.yandex.net/v1`)
- `FRONTEND_ORIGIN` (default: `http://localhost:5173`)

The API key is never exposed to the browser or committed to the repository.

## Endpoints

- `POST /api/quizzes/generate` — generate a quiz
- `GET /actuator/health` — health check
- `/swagger-ui.html` — OpenAPI UI
