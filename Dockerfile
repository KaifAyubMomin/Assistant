# Use the official OpenJDK image as a base
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the jar file from the target directory to the container
COPY target/Users-0.0.1-SNAPSHOT.jar app.jar

# Expose the port that your application will run on
EXPOSE 8083

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
