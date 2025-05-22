FROM eclipse-temurin:21.0.7_6-jdk
LABEL authors="alpha-learn"

WORKDIR /app

EXPOSE 8080
ENTRYPOINT ["top", "-b"]

