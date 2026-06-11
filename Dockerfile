# ─────────────────────────────────────────────
# Stage 1 : Build
# ─────────────────────────────────────────────
FROM gradle:8.5-jdk21 AS build

WORKDIR /app

# Copier uniquement les fichiers de config Gradle en premier (cache layer)
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle

# Télécharger les dépendances sans compiler (optimisation cache Docker)
RUN gradle dependencies --no-daemon || true

# Copier le reste du code source
COPY src ./src

# Build le JAR sans les tests
RUN gradle bootJar --no-daemon -x test

# ─────────────────────────────────────────────
# Stage 2 : Runtime
# ─────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Créer le dossier d'uploads
RUN mkdir -p /app/uploads/logos /app/uploads/animaux /app/uploads/taches

# Copier le JAR depuis le stage build
COPY --from=build /app/build/libs/*.jar app.jar

# Exposer le port
EXPOSE 8080

# Lancement
ENTRYPOINT ["java", "-Xmx400m", "-Xms200m", "-jar", "app.jar"]
