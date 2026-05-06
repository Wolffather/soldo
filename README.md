# Soldo — Backend

REST API для системы управления событиями и бронированиями.

## Стек

- **Java 21** / **Spring Boot 3.4.4**
- **PostgreSQL 14** + **Liquibase** (миграции)
- **Hibernate 6.6** / **Spring Data JPA**
- **Spring Security** — все эндпоинты открыты (`permitAll`), защита на уровне сети
- **MapStruct** + **Lombok**
- **SpringDoc OpenAPI** (Swagger UI)

## Модули

| Пакет | Описание |
|-------|----------|
| `booking` | Бронирования, статусы оплаты, counter-кэш сводки, документы |
| `config` | Настройки приложения (название, лейблы) |
| `document` | Шаблоны документов, электронная подпись, загрузка файлов |
| `event` | События (CRUD, статусы DRAFT/PUBLISHED) |
| `user` | Профили участников |
| `widget` | Встраиваемый виджет: конфиг, публичные события, бронирование |
| `shared` | Spring Security, CORS, валидация, обработка ошибок |

## Конфигурация

| Переменная | Описание |
|-----------|----------|
| `DB_URL` | JDBC URL базы данных (`jdbc:postgresql://localhost:5432/soldo_db`) |
| `DB_USERNAME` | Пользователь БД (`app_user`) |
| `DB_PASSWORD` | Пароль БД |
| `APP_PUBLIC_URL` | Публичный URL приложения (`http://localhost:8080`) |
| `UPLOAD_DIR` | Директория загрузок (`./uploads`) |
| `CORS_ALLOWED_ORIGINS` | Список разрешённых origins для admin-panel и других клиентов |
| `SWAGGER_ENABLED` | Включить Swagger UI (`true`) |
| `SWAGGER_USERNAME` | Basic-auth для Swagger (`admin`) |
| `SWAGGER_PASSWORD` | Basic-auth для Swagger |

## Быстрый старт (dev)

```bash
# 1. Поднять PostgreSQL
docker compose up -d

# 2. Запустить приложение
./mvnw spring-boot:run
```

API: `http://localhost:8080`  
Swagger UI: `http://localhost:8080/swagger-ui/index.html`

## Миграции БД

Liquibase, применяются автоматически при старте. Мастер-файл: `src/main/resources/db/changelog/db.changelog-master.xml`.

```
db/changelog/
├── db.changelog-master.xml
└── init/
    ├── 001-init-schema.xml               # все таблицы
    ├── 002-booking-summary-functions.sql # PL/pgSQL counter-кэш
    ├── 003-remove-multitenancy.xml       # удаление мультитенантности
    └── 004-remove-categories.xml         # удаление категорий событий
```

## API

### Публичные (без авторизации)

| Метод | Путь | Описание |
|-------|------|----------|
| `GET` | `/public/widget/config` | Конфигурация виджета (цвета, шрифты, лейблы) |
| `GET` | `/public/widget/events` | Опубликованные предстоящие события |
| `POST` | `/public/widget/booking` | Создать бронирование через виджет |

При бронировании через виджет требуются: имя (`guestName`), телефон (`guestPhone`), email (`guestEmail`). Бронирование автоматически получает статус `CONFIRMED`.

### Административные

| Метод | Путь | Описание |
|-------|------|----------|
| `GET/POST/PUT/DELETE` | `/events/**` | CRUD событий |
| `GET` | `/bookings/event/{id}` | Список бронирований события |
| `GET` | `/bookings/summary` | Сводка по всем событиям |
| `GET` | `/bookings/event/{id}/summary` | Сводка по конкретному событию |
| `POST` | `/bookings/admin` | Создать бронирование вручную |
| `PATCH` | `/bookings/{id}/cancel` | Отменить бронирование |
| `PATCH` | `/bookings/{id}/payment` | Обновить статус оплаты |
| `GET` | `/bookings/stats/monthly-revenue` | Выручка за текущий месяц |
| `GET/POST/PUT/DELETE` | `/admin/documents/**` | Шаблоны документов |
| `GET` | `/admin/bookings/{id}/documents` | Документы бронирования |
| `GET/PUT` | `/admin/widget/**` | Настройки виджета (admin) |

### Статусы бронирования

| Статус | Описание |
|--------|----------|
| `CONFIRMED` | Активное бронирование (присваивается автоматически при создании) |
| `CANCELLED` | Отменено (освобождает место) |

### Статусы оплаты

| Статус | Описание |
|--------|----------|
| `NOT_REQUIRED` | Событие бесплатное |
| `PENDING` | Ожидает оплаты |
| `PARTIALLY_PAID` | Внесена частичная оплата |
| `PAID` | Оплачено полностью |
| `REFUNDED` | Возврат |

## Структура проекта

```
src/main/java/ru/savvy/soldo/
├── booking/
│   ├── controller/     # BookingController, AdminDocumentController
│   ├── dto/            # BookingResponse, AdminBookingRequest, PaymentUpdateRequest, ...
│   ├── model/          # Booking, BookingDocument, EventBookingsSummary, BookingStatus, PaymentStatus
│   ├── repository/     # BookingRepository, BookingDocumentRepository, EventBookingSummaryRepository
│   └── service/        # BookingService, PaymentService, BookingDocumentService
├── config/             # AppConfig — настройки приложения (название, лейблы)
├── document/
│   ├── controller/     # DocumentTemplateController, FileUploadController, FileServeController
│   ├── model/          # DocumentTemplate
│   └── service/        # DocumentTemplateService, FileStorageService
├── event/
│   ├── controller/     # EventController
│   ├── dto/            # EventDTO
│   ├── mapper/         # EventMapper (MapStruct)
│   ├── model/          # Event, EventStatus
│   └── service/        # EventService
├── user/
│   ├── controller/     # ParticipantProfileController
│   ├── model/          # User, ParticipantProfile, UserRole
│   └── service/        # ParticipantProfileService
├── widget/
│   ├── WidgetController.java        # GET /public/widget/**
│   ├── AdminWidgetController.java   # GET/PUT /admin/widget/**
│   ├── dto/            # WidgetBookingRequest, WidgetBookingResponse, WidgetConfigResponse, ...
│   └── model/          # WidgetConfig
└── shared/
    ├── config/         # SecurityConfig (permitAll), OpenApiConfig
    ├── exception/      # GlobalExceptionHandler, NotFoundException, ...
    └── annotation/     # @ValidDateOrder
```

## Counter-кэш бронирований

Таблица `event_bookings_summary` хранит счётчики мест в реальном времени. Обновляется через PL/pgSQL-функции (`onCreateConfirmed`, `onCancelFromConfirmed`, ...), которые вызываются из `EventBookingSummaryRepository` после каждого изменения статуса бронирования. Такой подход исключает `SELECT COUNT(*)` при каждом запросе списка событий.

## Production

```bash
# Из корня проекта
cp .env.example .env
# Заполнить .env
docker compose -f docker-compose.prod.yml --env-file .env up -d --build
```
