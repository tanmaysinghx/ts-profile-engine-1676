# ===== Stage 1: Build =====
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY . .
RUN mvn clean package -DskipTests

# ===== Stage 2: Run =====
FROM eclipse-temurin:21-jdk
WORKDIR /app

# This is the file you specified!
COPY --from=builder /app/target/ts-profile-engine-1676-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 1676

ENTRYPOINT ["java", "-jar", "app.jar"]
