# Etapa 1: Construcción (Usamos Maven con Java 21)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Producción (Usamos un entorno ligero de Java 21)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copiamos el archivo .jar generado en la etapa anterior
COPY --from=build /app/target/*.jar app.jar
# Exponemos el puerto
EXPOSE 8080
# Comando para arrancar Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]