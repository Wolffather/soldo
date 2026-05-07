# Soldo — Backend

REST API для системы управления событиями и бронированиями.

## Стек

| Технология | Версия | Роль |
|-----------|--------|------|
| Java | 21 | Язык |
| Spring Boot | 3.4.4 | Фреймворк |
| Spring Security | 6 | CORS, базовая фильтрация (все запросы `permitAll`, защита на уровне сети) |
| PostgreSQL | 14 | База данных |
| Liquibase | — | Миграции схемы |
| Spring Data JPA / Hibernate | 6.6 | ORM |
| Spring Mail | — | Отправка документов как email-вложений |
| MapStruct + Lombok | — | Маппинг и бойлерплейт |
| SpringDoc OpenAPI | — | Swagger UI |

## Модули

| Пакет | Описание |
|-------|----------|
| `booking` | Бронирования, статусы оплаты, сводка мест, документы бронирований |
| `config` | Настройки приложения (название, контактные данные) |
| `document` | Шаблоны документов, загрузка и хранение файлов |
| `event` | Мероприятия (CRUD, статусы, ценовые варианты) |
| `widget` | Встраиваемый виджет: конфигурация, публичные события, создание бронирований |
| `shared` | Security, CORS, обработка ошибок, email, настройки приложения |

## Быстрый старт

```bash
# Поднять PostgreSQL
docker run -d --name soldo-pg \
  -e POSTGRES_DB=soldo_db -e POSTGRES_USER=soldo -e POSTGRES_PASSWORD=soldo \
  -p 5432:5432 postgres:14

# Запустить приложение
./mvnw spring-boot:run
```

API: `http://localhost:8080`  
Swagger UI: `http://localhost:8080/swagger-ui/index.html`

## Конфигурация

Все параметры передаются через переменные окружения (с дефолтами для локальной разработки).

| Переменная | Дефолт | Описание |
|-----------|--------|----------|
| `DB_URL` | `jdbc:postgresql://localhost:5432/soldo_db` | JDBC URL |
| `DB_USERNAME` | `app_user` | Пользователь БД |
| `DB_PASSWORD` | `app_pass` | Пароль БД |
| `APP_PUBLIC_URL` | `http://localhost:8080` | Публичный URL (используется в ссылках и CORS) |
| `UPLOAD_DIR` | `./uploads` | Директория хранения загруженных файлов |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:*` | Разрешённые origins для CORS |
| `SWAGGER_ENABLED` | `false` | Включить Swagger UI |
| `SWAGGER_USERNAME` | `admin` | Basic-auth для Swagger |
| `SWAGGER_PASSWORD` | `admin` | Basic-auth для Swagger |

### Настройки email (через панель администратора)

SMTP-параметры хранятся в таблице `app_settings` и настраиваются через раздел «Настройки» в панели администратора, без перезапуска сервера.

| Ключ | Описание |
|------|----------|
| `mail.enabled` | Включить отправку писем (`true` / `false`) |
| `mail.host` | SMTP-сервер (например, `smtp.yandex.ru`) |
| `mail.port` | Порт (обычно `587` для STARTTLS) |
| `mail.username` | Логин |
| `mail.password` | App Password (не основной пароль аккаунта) |
| `mail.from` | Адрес отправителя |

## API

### Публичные (виджет, без авторизации)

| Метод | Путь | Описание |
|-------|------|----------|
| `GET` | `/public/widget/config` | Конфигурация виджета (цвета, шрифты, тексты) |
| `GET` | `/public/widget/events` | Опубликованные предстоящие события |
| `POST` | `/public/widget/booking` | Создать бронирование через виджет |

**Поля запроса на бронирование:** `eventId`, `guestName`, `guestPhone`, `guestEmail`, `notes`, `priceOptionId` (опционально).

При создании бронирование автоматически считается подтверждённым. Если у события есть цена — статус оплаты устанавливается `PENDING`, срок — 7 дней. Если у события есть привязанные документы — они отправляются участнику по email как вложения.

### Статические файлы

| Метод | Путь | Описание |
|-------|------|----------|
| `GET` | `/files/{filename}` | Скачать загруженный файл (документ) |

### Административные

| Метод | Путь | Описание |
|-------|------|----------|
| `GET` | `/events` | Список событий (фильтрация, пагинация) |
| `GET` | `/events/{id}` | Детали события |
| `POST` | `/events` | Создать событие |
| `PUT` | `/events/{id}` | Обновить событие |
| `DELETE` | `/events/{id}` | Удалить событие |
| `GET` | `/bookings/event/{id}` | Бронирования события (пагинация) |
| `GET` | `/bookings/summary` | Сводка мест по всем событиям |
| `GET` | `/bookings/event/{id}/summary` | Сводка мест по конкретному событию |
| `POST` | `/bookings/admin` | Создать бронирование вручную (администратор) |
| `DELETE` | `/bookings/{id}` | Удалить бронирование |
| `PATCH` | `/bookings/{id}/payment` | Обновить статус оплаты |
| `POST` | `/bookings/{id}/send-documents` | Повторно отправить документы участнику |
| `GET` | `/bookings/stats/monthly-revenue` | Выручка за текущий месяц |
| `GET` | `/admin/documents` | Список шаблонов документов |
| `POST` | `/admin/documents` | Создать шаблон |
| `PUT` | `/admin/documents/{id}` | Обновить шаблон |
| `DELETE` | `/admin/documents/{id}` | Удалить шаблон |
| `POST` | `/admin/documents/upload` | Загрузить файл шаблона |
| `GET` | `/admin/bookings/{id}/documents` | Документы конкретного бронирования |
| `GET` | `/admin/widget` | Текущая конфигурация виджета |
| `PUT` | `/admin/widget` | Обновить конфигурацию виджета |
| `GET` | `/admin/settings` | Настройки приложения |
| `PUT` | `/admin/settings` | Обновить настройки |

### Статусы оплаты

| Статус | Описание |
|--------|----------|
| `NOT_REQUIRED` | Событие бесплатное |
| `PENDING` | Ожидает оплаты |
| `PARTIALLY_PAID` | Внесена частичная оплата |
| `PAID` | Оплачено полностью |
| `REFUNDED` | Возврат средств |

## Миграции БД

Liquibase применяется автоматически при старте. Мастер-файл: `src/main/resources/db/changelog/db.changelog-master.xml`.

```
db/changelog/init/
├── 001-init-schema.xml           # основные таблицы
├── 002-booking-summary-functions.sql  # (устаревшие триггеры, оставлены для совместимости)
├── 003-remove-multitenancy.xml   # удаление мультитенантности
├── 004-remove-categories.xml     # удаление категорий
├── 005-document-email.xml        # таблицы документов и email-истории
├── 006-app-settings.xml          # таблица настроек приложения
├── 007-event-price-options.xml   # ценовые варианты событий
├── 008-simplify-schema.xml       # упрощение схемы
└── 009-refresh-summary.xml       # пересчёт сводки из реальных данных
```

## Сводка мест (event_bookings_summary)

Таблица хранит счётчики: `total_bookings`, `confirmed_bookings`, `cancelled_bookings`, `num_of_participants` (свободные места).

После каждого изменения (создание или удаление бронирования, изменение `max_participants` события) вызывается `refreshSummary` — единый SQL-запрос, пересчитывающий все счётчики из реальных данных:

```sql
UPDATE event_bookings_summary SET
    confirmed_bookings = (SELECT COUNT(*) FROM bookings WHERE event_id = :id AND cancelled = false),
    num_of_participants = GREATEST(0, max_participants - confirmed_bookings),
    ...
WHERE event_id = :id
```

Это исключает `SELECT COUNT(*)` при каждом запросе событий и гарантирует консистентность данных.

## Структура проекта

```
src/main/java/ru/savvy/soldo/
├── booking/
│   ├── controller/   # BookingController, AdminDocumentController
│   ├── dto/          # BookingResponse, AdminBookingRequest, PaymentUpdateRequest, BookingSummaryResponse
│   ├── model/        # Booking, BookingDocument, EventBookingsSummary, PaymentStatus
│   ├── repository/   # BookingRepository, BookingDocumentRepository, EventBookingSummaryRepository
│   └── service/      # BookingService, BookingServiceImpl, PaymentService, BookingDocumentService
├── config/           # AppConfig — настройки приложения
├── document/
│   ├── controller/   # DocumentTemplateController, FileUploadController, FileServeController
│   ├── model/        # DocumentTemplate
│   ├── repository/   # DocumentTemplateRepository
│   └── service/      # DocumentTemplateService, FileStorageService
├── event/
│   ├── controller/   # EventController
│   ├── dto/          # EventDTO, EventPriceOptionDTO
│   ├── mapper/       # EventMapper (MapStruct)
│   ├── model/        # Event, EventStatus, EventPriceOption
│   └── service/      # EventService, EventServiceImpl
├── widget/
│   ├── WidgetController.java       # GET /public/widget/**
│   ├── AdminWidgetController.java  # GET/PUT /admin/widget/**
│   ├── WidgetServiceImpl.java
│   ├── dto/          # WidgetBookingRequest, WidgetBookingResponse, WidgetConfigResponse, WidgetEventResponse
│   └── model/        # WidgetConfig
└── shared/
    ├── config/       # SecurityConfig (CORS, permitAll), OpenApiConfig
    ├── email/        # EmailService, EmailServiceImpl (отправка документов как вложений)
    ├── exception/    # GlobalExceptionHandler, NotFoundException, IllegalOperationException
    └── settings/     # AppSettingsService (настройки из БД)
```
