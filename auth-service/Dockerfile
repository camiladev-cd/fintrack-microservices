FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY auth-service/pom.xml auth-service/
RUN chmod +x mvnw && ./mvnw dependency:go-offline -pl api-gateway -am -q
COPY auth-service/src auth-service/src
RUN ./mvnw package -pl auth-service -am -DskipTests -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN chmod +x mvnw && ./mvnw package -pl api-gateway -am -DskipTests -q
COPY --from=builder /app/auth-service/target/*.jar app.jar
RUN chown fintrack:fintrack app.jar
USER fintrack
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]