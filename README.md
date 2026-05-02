# Soldo — Backend

Сервис управления бронированиями для организаторов мероприятий. Multi-tenant SaaS с изоляцией данных на уровне `tenant_id`.

## Стек

- **Java 21** / **Spring Boot 3.4.4**
- **PostgreSQL 14** + **Liquibase** (миграции)
- **Hibernate 6.6** с `@Filter` для мультитенантности
- **Spring Security** + JWT
- **MapStruct** + **Lombok**
- **SpringDoc OpenAPI** (Swagger UI)

## Модули

| Пакет | Описание |
|-------|----------|
| `auth` | Аутентификация — JWT, логин/пароль, регистрация |
| `tenant` | Мультитенантность, конфигурация, подписки |
| `user` | Управление пользователями и профилями участников |
| `event` | События, категории, форматы |
| `booking` | Бронирования, статусы, сводка (counter-кэш) |
| `document` | Шаблоны документов, электронная подпись |
| `notification` | Уведомления с планировщиком |
| `inquiry` | Обратная связь / заявки |
| `widget` | Встраиваемый виджет бронирования |
| `content` | Управление контентом (сайт, команда, галерея) |
| `onboarding` | Онбординг новых тенантов |

## Конфигурация

Вся конфигурация через переменные окружения. Файл `application.yaml` содержит дефолты для локальной разработки — для запуска на `localhost` достаточно поднять PostgreSQL с дефолтными кредами.

| Переменная | По умолчанию | Описание |
|-----------|-------------|----------|
| `DB_URL` | `jdbc:postgresql://localhost:5432/soldo_db` | JDBC URL базы данных |
| `DB_USERNAME` | `app_user` | Пользователь БД |
| `DB_PASSWORD` | `app_pass` | Пароль БД |
| `JWT_SECRET` | dev-заглушка | Секрет для подписи JWT (в проде — длинная случайная строка) |
| `JWT_EXPIRATION_MS` | `86400000` (24ч) | Время жизни JWT |
| `APP_PUBLIC_URL` | `http://localhost:8080` | Публичный URL приложения |
| `UPLOAD_DIR` | `./uploads` | Директория загрузок |
| `SWAGGER_ENABLED` | `true` | Включить Swagger UI |
| `SWAGGER_USERNAME` | `admin` | Basic-auth для Swagger |
| `SWAGGER_PASSWORD` | `admin` | Basic-auth для Swagger |
| `LOG_LEVEL_APP` | `DEBUG` | Уровень логирования приложения |
| `LOG_LEVEL_SQL` | `DEBUG` | Уровень логирования SQL |

## Быстрый старт (dev)

```bash
# 1. Поднять PostgreSQL
docker compose up -d

# 2. Запустить приложение (дефолтов достаточно для localhost)
./mvnw spring-boot:run
```

Если нужны нестандартные значения — передайте через env:
```bash
DB_URL=jdbc:postgresql://myhost:5432/mydb DB_USERNAME=me DB_PASSWORD=secret ./mvnw spring-boot:run
```

API будет доступно на `http://localhost:8080`.  
Swagger UI: `http://localhost:8080/swagger-ui/index.html`.

## Миграции БД

Используется Liquibase. Мастер-файл: `src/main/resources/db/changelog/db.changelog-master.xml`.

Миграции применяются автоматически при старте приложения. Структура:

```
db/changelog/
├── db.changelog-master.xml          # точка входа
└── init/
    ├── 001-init-schema.xml          # все таблицы
    └── 002-booking-summary-functions.sql  # PL/pgSQL функции счётчиков
```

## Архитектура мультитенантности

Shared database, shared schema. Все сущности содержат `tenant_id`. Изоляция через Hibernate `@Filter("tenantFilter")`, который устанавливается автоматически через `TenantContext`.

Глобальные сущности без `tenant_id`: `SiteSettings`, `TeamMember`, `GalleryItem`.

## Production

См. [DEPLOYMENT.md](../DEPLOYMENT.md) в корне проекта — инструкция по развёртыванию на VPS (Docker + Caddy + auto-TLS).

```bash
# Из корня проекта
cp .env.example .env
# Заполнить .env
docker compose -f docker-compose.prod.yml --env-file .env up -d --build
```

## API

Основные группы эндпоинтов:

| Путь | Описание |
|------|----------|
| `POST /auth/login` | Аутентификация, получение JWT |
| `POST /auth/register` | Регистрация пользователя |
| `/admin/events/**` | CRUD событий |
| `/admin/bookings/**` | CRUD бронирований |
| `/admin/users/**` | Управление пользователями |
| `/admin/tenant/**` | Настройки тенанта |
| `/admin/documents/**` | Шаблоны и документы |
| `/admin/notifications/**` | Уведомления |
| `/public/widget/**` | Публичный виджет (без авторизации) |

## Структура проекта

```
src/main/java/ru/savvy/soldo/
├── auth/           # Контроллеры, DTO, модели аутентификации
├── booking/        # Бронирования, документы, сводка
├── content/        # Контент сайта
├── document/       # Документы и шаблоны
├── event/          # События и категории
├── inquiry/        # Обратная связь
├── notification/   # Уведомления и планировщик
├── onboarding/     # Онбординг
├── tenant/         # Мультитенантность
├── user/           # Пользователи и профили
├── widget/         # Виджет бронирования
└── shared/         # Конфигурация, безопасность, утилиты
```
