# syntax=docker/dockerfile:1
FROM openjdk:18-alpine
WORKDIR /app
COPY target/executable ./mtgcompanion
EXPOSE 8080/tcp
EXPOSE 80/tcp
CMD ["./mtgcompanion/bin/web-ui.sh"]