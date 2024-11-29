# Use the official Maven image to build the project
FROM maven:3.8.2-openjdk-17-slim as build

WORKDIR /app

# Copy the pom.xml and install dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the project
COPY . .

# Package the application
RUN mvn clean package -DskipTests

# Use the official OpenJDK image to run the application
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build target/*-runner.jar app.jar
CMD ["java", "-jar", "app.jar"]
