
# Use an official OpenJDK 21 image
FROM eclipse-temurin:21-jdk-jammy

# Optional: Reduce JVM memory usage (can be customized later)
ENV JAVA_OPTS=""

# Set working directory
WORKDIR /app

# Copy the JAR file built by Gradle
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# Expose port (for local documentation; Render ignores it)
EXPOSE 8080

# Run the JAR
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
