FROM eclipse-temurin:21.0.7_6-jdk AS builder
LABEL authors="alpha-learn"

WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY ./src ./src
RUN ./mvnw clean install

FROM eclipse-temurin:21.0.7_6-jre-ubi9-minimal AS final
WORKDIR /app
COPY --from=builder /app/target/*.jar /app/*.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/*.jar"]

