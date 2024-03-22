# 2 stage docking
# First container
FROM openjdk:21-jdk-bullseye AS builder


ARG APP_DIR=/app
WORKDIR ${APP_DIR}


COPY pom.xml .
COPY src src
COPY mvnw .
COPY .mvn .mvn

# Compile to get jar file
# Change mod a+x (gives permission to access mvnw file)
RUN chmod a+x /app/mvnw
RUN ./mvnw package -Dmaven.test.skip=true

# Run container 2
FROM openjdk:21-jdk-bullseye

ARG APP_DIR=/app_run
WORKDIR ${APP_DIR}

# COPY from container above filepath filename.jar
# Need to give the full path!
COPY --from=builder /app/target/day17.weather.cached-0.0.1-SNAPSHOT.jar .


ENV WEATHER_API_KEY=
ENV PORT=8080

EXPOSE ${PORT}

HEALTHCHECK --interval=30s --timeout=5s --start-period=5s --retries=3 CMD curl http://localhost:${PORT}/health || exit 1

# Runs from weather.jar not day17-weather
ENTRYPOINT SERVER_PORT=${PORT} java -jar day17.weather.cached-0.0.1-SNAPSHOT.jar