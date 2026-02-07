
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .

COPY reddit-common ./reddit-common
COPY discovery-server ./discovery-server
COPY notification-service ./notification-service
COPY post-service ./post-service
COPY feed-service ./feed-service
COPY user-service ./user-service
COPY auth-service ./auth-service
COPY api-gateway ./api-gateway
COPY vote-service ./vote-service
COPY karma-service ./karma-service
COPY comment-service ./comment-service


RUN mvn clean install -DskipTests


FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/api-gateway/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java","-jar","app.jar"]
