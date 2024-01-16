#FROM eclipse-temurin:17-jdk-alpine
#VOLUME /tmp
#COPY ./build/libs/*.jar client.jar
#ENTRYPOINT ["java","-jar","/client.jar"]
#EXPOSE 8080

#FROM openjdk:17-jdk-slim-buster AS builder
#
#RUN apt-get update -y
#RUN apt-get install -y binutils
#
#WORKDIR /app
#
#COPY . .
#
#RUN ./gradlew build -i --stacktrace
#
#ENTRYPOINT ["java", "-jar", "/app/build/libs/config-server-0.0.1.jar"]

FROM gradle:8.2-jdk17-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build -x test --no-daemon

FROM amazoncorretto:17-alpine-jdk
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar
ENTRYPOINT ["java","-Dspring.profiles.active=prod", "-jar","/app.jar"]