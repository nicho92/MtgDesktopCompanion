# syntax=docker/dockerfile:1
FROM openjdk:21-jdk
WORKDIR /app
COPY target/executable ./mtgcompanion
EXPOSE 8080/tcp
EXPOSE 80/tcp
CMD ["./mtgcompanion/bin/web-ui.sh"]