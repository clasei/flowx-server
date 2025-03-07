# FROM openjdk:17-jdk-slim
# WORKDIR /app
# COPY target/*.jar app.jar
# EXPOSE 8080
# CMD ["java", "-jar", "app.jar"]


# # in case it fails... tell Render to compile the code automatically

FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]