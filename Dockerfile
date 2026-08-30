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

# Stage 2: Запуск — здесь только лёгкая Java для выполнения
FROM eclipse-temurin:21-jre-alpine

# Устанавливаем curl, чтобы Spring Boot мог проверить health
RUN apk add --no-cache curl

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