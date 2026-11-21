# Etapa 1: compilación
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /workspace

# Copiamos primero los archivos necesarios para descargar dependencias.
# Esto permite aprovechar mejor la caché de Docker.
COPY .mvn .mvn
COPY mvnw pom.xml ./

RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

# Copiamos el código fuente.
COPY src src

# Generamos el JAR. Los tests se ejecutarán por separado en CI.
RUN ./mvnw clean package -DskipTests -B


# Etapa 2: ejecución
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Creamos un usuario sin privilegios.
RUN addgroup -S cloudmaint && \
    adduser -S cloudmaint -G cloudmaint

# Copiamos únicamente el JAR generado.
COPY --from=build \
    /workspace/target/asset-service-0.0.1-SNAPSHOT.jar \
    app.jar

# La aplicación se ejecuta como usuario no root.
USER cloudmaint

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]