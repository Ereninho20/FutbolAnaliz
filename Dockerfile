FROM eclipse-temurin:21-jre
WORKDIR /app
COPY Futbol.jar /app/Futbol.jar
COPY veriler /app/veriler
CMD ["java", "-Djava.awt.headless=true", "-jar", "Futbol.jar"]
