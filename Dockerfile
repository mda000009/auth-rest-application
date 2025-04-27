# Use a Java base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the generated JAR file to the container
COPY target/auth-rest-application-0.0.1-SNAPSHOT.jar app.jar

# Expose the port on which the application runs
EXPOSE 8081

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]