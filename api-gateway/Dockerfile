FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY api-gateway/pom.xml api-gateway/
RUN ./mvnw dependency:go-offline -pl api-gateway -am -q
COPY api-gateway/src api-gateway/src
RUN ./mvnw package -pl api-gateway -am -DskipTests -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S fintrack && adduser -S fintrack -G fintrack
COPY --from=builder /app/api-gateway/target/*.jar app.jar
RUN chown fintrack:fintrack app.jar
USER fintrack
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]