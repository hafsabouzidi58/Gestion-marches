# --- Étape 1 : Construction du JAR ---
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

# Copier les fichiers de configuration Maven et le code source
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY src/ src/

# Rendre le wrapper exécutable et compiler le projet en ignorant les tests
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# --- Étape 2 : Exécution de l'application ---
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copier le fichier JAR généré depuis l'étape de build
COPY --from=build /app/target/*.jar app.jar

# Exposer le port de l'application Spring Boot
EXPOSE 8081

# Commande pour lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]