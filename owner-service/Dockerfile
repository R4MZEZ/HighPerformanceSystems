#FROM eclipse-temurin:17-jdk-alpine
#VOLUME /tmp
#COPY ./build/libs/*.jar client.jar
#ENTRYPOINT ["java","-jar","/client.jar"]
#EXPOSE 8080

FROM openjdk:17-jdk-slim-buster AS builder

RUN apt-get update -y
RUN apt-get install -y binutils

WORKDIR /app

COPY . .

RUN ./gradlew build -x test -i --stacktrace

ENTRYPOINT ["java", "-jar", "/app/build/libs/owner-service-0.0.1.jar"]