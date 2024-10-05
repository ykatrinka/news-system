FROM gradle:8.5-jdk21 AS build
COPY --chown=gradle:gradle . /app
WORKDIR /app
RUN gradle build


FROM openjdk:21-slim
COPY --from=build app/comments-service-api/build/libs/*.jar comment-service.jar
ENTRYPOINT ["java","-jar","comment-service.jar"]

#EXPOSE 8082
