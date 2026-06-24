FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY transaction-service/pom.xml transaction-service/
RUN ./mvnw dependency:go-offline -pl transaction-service -am -q
COPY transaction-service/src transaction-service/src
RUN ./mvnw package -pl transaction-service -am -DskipTests -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S fintrack && adduser -S fintrack -G fintrack
COPY --from=builder /app/transaction-service/target/*.jar app.jar
RUN chown fintrack:fintrack app.jar
USER fintrack
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]