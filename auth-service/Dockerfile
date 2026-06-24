FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S fintrack && adduser -S fintrack -G fintrack
COPY auth-service/target/*.jar app.jar
RUN chown fintrack:fintrack app.jar
USER fintrack
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]