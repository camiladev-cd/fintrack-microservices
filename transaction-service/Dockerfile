FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY transaction-service/pom.xml transaction-service/
RUN chmod +x mvnw && ./mvnw dependency:go-offline -pl api-gateway -am -q
COPY transaction-service/src transaction-service/src
RUN ./mvnw package -pl transaction-service -am -DskipTests -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN chmod +x mvnw && ./mvnw package -pl api-gateway -am -DskipTests -q
COPY --from=builder /app/transaction-service/target/*.jar app.jar
RUN chown fintrack:fintrack app.jar
USER fintrack
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]