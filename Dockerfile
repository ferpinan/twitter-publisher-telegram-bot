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

RUN mkdir -p /home/twitter-bot/files
RUN mkdir /opt/app
COPY ./target/twitter-bot.jar /opt/app
CMD ["java", "-jar", "/opt/app/twitter-bot.jar"]