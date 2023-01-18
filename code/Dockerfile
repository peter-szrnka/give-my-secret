FROM adoptopenjdk/openjdk11:latest
LABEL maintainer="Peter Szrnka"

ARG JAR_FILE=gms-backend/target/gms-app.jar

RUN echo "Let's copy the JAR file: $JAR_FILE"
COPY $JAR_FILE gms-app.jar

ENTRYPOINT ["java","-Dspring.profiles.active=${SPRING_PROFILE}","-jar","gms-app.jar"]

EXPOSE 8080
EXPOSE 8443