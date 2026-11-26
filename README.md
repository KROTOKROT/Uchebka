# Uchebka

Образовательная платформа для создания и прохождения тестов по учебным материалам с помощью Yandex AI Studio Agent.

## Стек

- Frontend: React, TypeScript, Vite, Nginx
- Backend: Java 21, Spring Boot 3, Maven
- AI: Yandex AI Studio Responses API (OpenAI-compatible)
- Web search: встроенный инструмент Yandex Agent `web_search`
- Resilience: Resilience4j
- API docs: Springdoc OpenAPI
- Observability: Actuator + Micrometer
- Deployment: Docker, Docker Compose

## Запуск через Docker (Windows)

Требуется установленный и запущенный Docker Desktop с поддержкой Docker Compose.

1. Откройте файл `.env.example` и вставьте свой Yandex AI Studio API key вместо `replace-me` в строке `YANDEX_API_KEY`. ID проекта и новый ID агента уже указаны.
2. В корне проекта создайте рабочий файл `.env`:

   PowerShell:
   ```powershell
   Copy-Item .env.example .env
   ```

   CMD:
   ```cmd
   copy .env.example .env
   ```

3. Соберите и запустите приложение:

   ```powershell
   docker compose up --build -d
   ```

Docker Compose автоматически читает параметры из файла `.env` в корне проекта. Не коммитьте `.env`: он содержит секретный API key. Файл `.env` исключён через `.gitignore`, шаблон `.env.example` хранится в репозитории.

В конфигурации по умолчанию используется агент `fvt27hjfedvl5f9qs3lk` и проект `b1gfj3v8keh3qobuh6rv`. Чтобы сменить агента, измените `YANDEX_PROMPT_ID` в `.env`.

Backend обращается к Yandex Responses API по адресу `https://ai.api.cloud.yandex.net/v1/responses`. Это официальный hostname Yandex AI Studio; адрес без `.net` не существует и приводит к DNS-ошибке. Docker Compose явно задаёт правильный адрес для backend, поэтому устаревшее значение `YANDEX_BASE_URL` в ранее созданном локальном файле `.env` не сможет его переопределить. Backend передаёт `OpenAI-Project` и включает инструмент `web_search` с контекстом поиска `medium`. Секретный ключ передаётся только backend-контейнеру и не встраивается во frontend.

После запуска:

- Приложение: http://localhost
- Swagger UI: http://localhost/swagger-ui.html
- OpenAPI JSON: http://localhost/v3/api-docs
- Backend Actuator: http://localhost/actuator/health

Остановка:

```powershell
docker compose down
```

Просмотр логов:

```powershell
docker compose logs -f
```

Для повторной сборки после изменения кода:

```powershell
docker compose up --build -d
```

По умолчанию frontend доступен на порту 80. Если порт занят, измените `APP_PORT` в `.env`, например на `8081`, и откройте http://localhost:8081.

## Локальный запуск без Docker

Создайте `.env` из `.env.example` и заполните `YANDEX_API_KEY`.

### Backend (PowerShell)

```powershell
cd backend
mvn spring-boot:run
```

Swagger UI: http://localhost:8080/swagger-ui.html

### Frontend

В другом терминале:

```powershell
cd frontend
npm install
npm run dev
```

Приложение: http://localhost:5173

Никогда не публикуйте API key и не добавляйте файл `.env` в Git.
