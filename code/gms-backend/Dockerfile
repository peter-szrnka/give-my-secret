# Deprecated file
FROM eclipse-temurin:17-jdk
LABEL maintainer="Peter Szrnka"

ARG JAR_FILE=target/gms-app.jar
COPY ${JAR_FILE} gms-app.jar

ENTRYPOINT ["java","-Dspring.profiles.active=${SPRING_PROFILE}","-jar","gms-app.jar"]

EXPOSE 8080
EXPOSE 8443
