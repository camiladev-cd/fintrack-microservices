FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY user-service/pom.xml user-service/
RUN chmod +x mvnw && ./mvnw dependency:go-offline -pl api-gateway -am -q
COPY user-service/src user-service/src
RUN ./mvnw package -pl user-service -am -DskipTests -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN chmod +x mvnw && ./mvnw package -pl api-gateway -am -DskipTests -q
COPY --from=builder /app/user-service/target/*.jar app.jar
RUN chown fintrack:fintrack app.jar
USER fintrack
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]