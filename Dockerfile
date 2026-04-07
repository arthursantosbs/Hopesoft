# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copiar arquivos do projeto
COPY pom.xml .
COPY src ./src

# Build da aplicação
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiar JAR do stage anterior
COPY --from=builder /app/target/hopesoft-0.0.1-SNAPSHOT.jar app.jar

# Expor porta
EXPOSE 8080

# Variáveis de ambiente padrão
ENV SERVER_PORT=8080
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/hopesoft_db
ENV SPRING_DATASOURCE_USERNAME=hopesoft_user
ENV SPRING_DATASOURCE_PASSWORD=hopesoft123
ENV APP_JWT_SECRET=hopesoft-secret-key-change-in-production
ENV APP_JWT_EXPIRATION_MS=86400000

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:8080/auth/me || exit 1

# Executar aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]

