FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# The jar is expected to be built by Jenkins (Maven) before Docker build runs
COPY target/*.jar app.jar

EXPOSE 8086

ENTRYPOINT ["java", "-jar", "app.jar"]
