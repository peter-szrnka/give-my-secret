FROM adoptopenjdk/openjdk11:latest
LABEL maintainer="Peter Szrnka"

ARG JAR_FILE=target/gms-backend.jar
COPY ${JAR_FILE} gms-backend.jar

ENTRYPOINT ["java","-Dspring.profiles.active=${SPRING_PROFILE}","-jar","gms-backend.jar"]

EXPOSE 8080
EXPOSE 8443