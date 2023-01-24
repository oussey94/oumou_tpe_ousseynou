# Première étape : Compiler le projet Spring Boot
FROM maven:3.6.3-openjdk-11 as build
COPY src /app/src
COPY pom.xml /app
RUN mvn -f /app/pom.xml clean package -DskipTests
# Deuxième étape : Créer une image en copiant seulement l'artefact JAR généré dans la première étape
FROM openjdk:11-jre
COPY --from=build /app/target/*.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
