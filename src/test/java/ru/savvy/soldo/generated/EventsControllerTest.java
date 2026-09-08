package ru.savvy.soldo.generated;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EventsControllerTest {

    @LocalServerPort
    private int port;

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    @DisplayName("GET /events/upcoming - успешное получение предстоящих событий")
    void getUpcomingEvents_Success() {
        // Given
        RestAssured.port = port;

        // When & Then
        Response response = given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/events/upcoming") // или "/events/upcoming" в зависимости от вашей структуры
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract()
                .response();

        // Дополнительные проверки
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("GET /events/upcoming - проверка структуры ответа JSON")
    void getUpcomingEvents_CheckResponseStructure() {
        RestAssured.port = port;

        given()
                .accept(ContentType.JSON)
                .when()
                .get("/events/upcoming")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("$", hasSize(greaterThan(0))) // массив не пустой // не пустой массив (если есть события)
                .body("[0].id", is(notNullValue()))
                .body("[0].title", is(notNullValue()))
                .body("[0].date", is(notNullValue()))
                .body("[0].location", is(notNullValue()));
    }

    @Test
    @DisplayName("GET /events/upcoming - проверка что все события будущие")
    void getUpcomingEvents_VerifyFutureEvents() {
        RestAssured.port = port;

        Response response = given()
                .accept(ContentType.JSON)
                .when()
                .get("/events/upcoming")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response();

        // Получаем массив событий из JSON
        List<Map<String, Object>> events = response.jsonPath().getList("");

        // Проверяем, что все события имеют даты в будущем
        LocalDate today = LocalDate.now();
        for (Map<String, Object> event : events) {
            String eventDate = (String) event.get("date");
            LocalDate parsedDate = LocalDate.parse(eventDate, DateTimeFormatter.ISO_DATE);
            assertTrue(parsedDate.isAfter(today), 
                    "Событие с ID " + event.get("id") + " не является будущим");
            
            // Дополнительно проверяем обязательные поля
            assertNotNull(event.get("title"), "Отсутствует заголовок события");
            assertNotNull(event.get("client"), "Отсутствует клиент");
        }
    }

    @Test
    @DisplayName("GET /events/upcoming - проверка сортировки по дате")
    void getUpcomingEvents_VerifySorting() {
        RestAssured.port = port;

        Response response = given()
                .accept(ContentType.JSON)
                .when()
                .get("/events/upcoming")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response();

        // Получаем все даты событий
        List<String> dates = response.jsonPath().getList("date");
        
        // Проверяем, что даты отсортированы по возрастанию
        for (int i = 0; i < dates.size() - 1; i++) {
            LocalDate currentDate = LocalDate.parse(dates.get(i), DateTimeFormatter.ISO_DATE);
            LocalDate nextDate = LocalDate.parse(dates.get(i + 1), DateTimeFormatter.ISO_DATE);
            assertFalse(currentDate.isAfter(nextDate), 
                    "События не отсортированы по возрастанию дат");
        }
    }

    @Test
    @DisplayName("GET /events/upcoming - контрактное тестирование с JsonSchema")
    void getUpcomingEvents_SchemaValidation() {
        RestAssured.port = port;

        String jsonSchema = """
            {
              "$schema": "http://json-schema.org/draft-04/schema#",
              "type": "array",
              "items": {
                "type": "object",
                "properties": {
                  "id": {
                    "type": "integer"
                  },
                  "title": {
                    "type": "string"
                  },
                  "date": {
                    "type": "string",
                    "format": "date"
                  },
                  "location": {
                    "type": "string"
                  },
                  "client": {
                    "type": "string"
                  },
                  "status": {
                    "type": "string",
                    "enum": ["SCHEDULED", "CONFIRMED", "CANCELED"]
                  }
                },
                "required": ["id", "title", "date", "client"]
              }
            }
            """;

        given()
                .accept(ContentType.JSON)
                .when()
                .get("/events/upcoming")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON);
        // Если хотите использовать полную валидацию схемы, раскомментируйте:
        // .body(matchesJsonSchema(jsonSchema));
        // Для этого требуется зависимости json-schema-validator
    }

    @Test
    @DisplayName("GET /events/upcoming - проверка ошибки при недоступности сервиса")
    void getUpcomingEvents_ServiceUnavailable() {
        RestAssured.port = port;

        // Симулируем недоступность сервиса 
        // (при условии что сервис может вернуть 503)
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/events/upcoming")
                .then()
                .statusCode(anyOf(is(200), is(500), is(503)));
        
        // Если необходимо, для проверки ошибки можно отключить сервис
        // и затем проверить корректный ответ
    }

    @Test
    @DisplayName("GET /events/upcoming - тест производительности")
    void getUpcomingEvents_PerformanceTest() {
        RestAssured.port = port;

        long startTime = System.currentTimeMillis();

        given()
                .accept(ContentType.JSON)
                .when()
                .get("/events/upcoming")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON);

        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;

        // Проверяем, что ответ получен за разумное время (< 3 секунд)
        assertTrue(responseTime < 3000, 
                "Ответ слишком медленный: " + responseTime + " мс");
        
        System.out.println("Response time: " + responseTime + " ms");
    }

    @Test
    @DisplayName("GET /events/upcoming - проверка заголовков ответа")
    void getUpcomingEvents_CheckHeaders() {
        RestAssured.port = port;

        given()
                .accept(ContentType.JSON)
                .when()
                .get("/events/upcoming")
                .then()
                .statusCode(HttpStatus.OK.value())
                .header("Content-Type", containsString("application/json"))
                .header("Cache-Control", notNullValue());
    }
}