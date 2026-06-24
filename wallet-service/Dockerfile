FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY wallet-service/pom.xml wallet-service/
RUN ./mvnw dependency:go-offline -pl wallet-service -am -q
COPY wallet-service/src wallet-service/src
RUN ./mvnw package -pl wallet-service -am -DskipTests -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S fintrack && adduser -S fintrack -G fintrack
COPY --from=builder /app/wallet-service/target/*.jar app.jar
RUN chown fintrack:fintrack app.jar
USER fintrack
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]