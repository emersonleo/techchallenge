# BUILD
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# RUN
FROM eclipse-temurin:21-jre-alpine

RUN mkdir -p /data/h2db

RUN adduser -D -u 1001 chefonline
RUN chown -R chefonline:chefonline /data/h2db
RUN chmod 700 /data/h2db

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

USER chefonline

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]