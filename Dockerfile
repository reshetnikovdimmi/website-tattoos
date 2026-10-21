# Stage 1: Сборка — здесь компилируем jar
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Сначала копируем только pom.xml и качаем зависимости
# Если pom не менялся — Docker использует кэш и не качает всё заново
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Теперь копируем исходники и собираем jar без тестов
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Запуск — здесь только лёгкая Java для выполнения.
# База Ubuntu, а не Alpine: нужен настоящий клиент MySQL (Oracle).
# MySQL 8 по умолчанию использует caching_sha2_password, а MariaDB-клиент из Alpine
# не содержит этот плагин — mysqldump падает с
# "Plugin caching_sha2_password could not be loaded".
FROM eclipse-temurin:21-jre-jammy

# curl — healthcheck, mysql-client-8.0 — mysqldump для бэкапов
RUN apt-get update \
    && DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends \
        curl \
        mysql-client-8.0 \
    && rm -rf /var/lib/apt/lists/* \
    && mysqldump --version

WORKDIR /app

# Берём готовый jar из Stage 1 (builder) и кладём сюда
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

# Ограничиваем память Java, чтобы не съела всю RAM сервера
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseContainerSupport"

# Docker будет стучаться по этому адресу и проверять, живо ли приложение
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]