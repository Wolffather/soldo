# syntax=docker/dockerfile:1.6
#
# Production-сборка Soldo backend.
# Используется BuildKit cache mount для Maven — повторная сборка занимает
# ~30 секунд вместо 5+ минут скачивания зависимостей.

# ─── Stage 1: build ───────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /workspace

# Копируем wrapper и pom.xml отдельно — при неизменённом pom'е cache mount
# переиспользуется, даже если код поменялся.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw

# Копируем исходники и собираем. Cache mount хранит ~/.m2 между сборками.
COPY src/ src/
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -B -DskipTests package \
 && mkdir -p target/extracted \
 && java -Djarmode=layertools -jar target/*.jar extract --destination target/extracted

# ─── Stage 2: runtime ─────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

# Не-рутовый пользователь + директория для загрузок
RUN addgroup -S soldo && adduser -S soldo -G soldo \
 && mkdir -p /app/uploads \
 && chown -R soldo:soldo /app

WORKDIR /app
USER soldo

# Слои Spring Boot в порядке от "редко меняющихся" к "часто меняющимся":
# это даёт максимальный cache hit на rebuild.
COPY --from=build --chown=soldo:soldo /workspace/target/extracted/dependencies/          ./
COPY --from=build --chown=soldo:soldo /workspace/target/extracted/spring-boot-loader/    ./
COPY --from=build --chown=soldo:soldo /workspace/target/extracted/snapshot-dependencies/ ./
COPY --from=build --chown=soldo:soldo /workspace/target/extracted/application/           ./

EXPOSE 8080

# Опции JVM можно переопределить через JAVA_OPTS в docker-compose
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
