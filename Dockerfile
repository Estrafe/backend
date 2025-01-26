# Use a base image with OpenJDK 17 installed
FROM eclipse-temurin:17-jdk-jammy

# Set the working directory in the container
WORKDIR /app

# Copy the packaged Spring Boot application JAR file into the container
COPY target/your-application.jar /app/app.jar

# Expose the port your application will run on
EXPOSE 8080

# Specify the command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]