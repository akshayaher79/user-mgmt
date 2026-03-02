# Build stage
FROM mcr.microsoft.com/openjdk/jdk:17-distroless AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

# Run stage
FROM mcr.microsoft.com/openjdk/jdk:17-distroless
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
