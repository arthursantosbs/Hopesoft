FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY backend/target/hopesoft-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENV SERVER_PORT=8080
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/hopesoft_db
ENV SPRING_DATASOURCE_USERNAME=hopesoft_user
ENV SPRING_DATASOURCE_PASSWORD=hopesoft123
ENV APP_JWT_SECRET=hopesoft-secret-key-change-in-production
ENV APP_JWT_EXPIRATION_MS=86400000
ENV APP_SEED_ENABLED=true

HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:8080/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
