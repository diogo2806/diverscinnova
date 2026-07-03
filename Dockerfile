# --- Stage 1: compile Tailwind CSS ---
FROM node:20-alpine AS css
WORKDIR /web
COPY package.json package-lock.json ./
RUN npm ci
COPY tailwind.config.js ./
COPY src/input.css ./src/input.css
COPY index.html ./
RUN npm run build

# --- Stage 2: build the Spring Boot jar ---
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
# Resolve dependencies first for better layer caching
COPY pom.xml ./
RUN mvn -q -B dependency:go-offline
# App sources + static site (index.html at root, CSS from stage 1)
COPY src ./src
COPY index.html ./index.html
COPY --from=css /web/assets ./assets
RUN mvn -q -B clean package -DskipTests

# --- Stage 3: runtime ---
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app
ENV JAVA_OPTS=""
COPY --from=build /app/target/diverscinnova-site.jar app.jar

# EasyPanel proxies to this port
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=25s --retries=3 \
  CMD wget -q --spider http://localhost:8080/ || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
