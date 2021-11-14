# pull official base image
#FROM maven:3.8.3-openjdk-17-slim AS builder
#
## set working directory
#WORKDIR /app
#
## Copies everything over to Docker environment
#COPY . /app
#RUN mvn clean install


#Stage 2
#######################################
#FROM openjdk:17-jdk-slim

FROM arm32v7/eclipse-temurin:17.0.1_12-jdk-focal

COPY ./target/twitter-bot.jar /home/twitter-bot.jar

CMD ["java", "-jar", "/home/twitter-bot.jar"]