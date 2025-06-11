FROM eclipse-temurin:21-jre-alpine
LABEL maintainer="Peter Szrnka"

ARG JAR_FILE=target/gms-app.jar
COPY ${JAR_FILE} gms-app.jar

COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh", "java","-jar","gms-app.jar"]

EXPOSE 8080
EXPOSE 8443
