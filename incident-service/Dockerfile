# Etapa 1: compilación

FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /workspace

# Copiamos primero los ficheros necesarios para descargar dependencias
# y aprovechar la caché de Docker.
COPY .mvn .mvn
COPY mvnw pom.xml ./

RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

# Copiamos el código fuente.
COPY src src

# Generamos el JAR.
# Los tests se ejecutarán de forma independiente en CI.
RUN ./mvnw clean package -DskipTests -B


# Etapa 2: ejecución

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Usuario sin privilegios.
RUN addgroup -S cloudmaint && \
    adduser -S cloudmaint -G cloudmaint

# Copiamos únicamente el JAR generado.
COPY --from=build \
    /workspace/target/incident-service-0.0.1-SNAPSHOT.jar \
    app.jar

USER cloudmaint

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]