# pull official base image
FROM maven:3.8.3-openjdk-17-slim AS builder

# set working directory
WORKDIR /app

# Copies everything over to Docker environment
COPY . /app
RUN mvn clean install


#Stage 2
#######################################
#FROM openjdk:17-jdk-slim

FROM eclipse-temurin:latest@shasha256:46f35cd099e25ed0d9f8ad4c33e8ad436e34f3b889e112d38ed000f24256ff8d

RUN mkdir /home/videos && touch /home/videos/testfile.html
COPY --from=builder /app/target/twitter-bot.jar /home/twitter-bot.jar

CMD ["java", "-jar", "/home/twitter-bot.jar"]