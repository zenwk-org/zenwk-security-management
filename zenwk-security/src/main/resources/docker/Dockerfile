FROM eclipse-temurin:17
WORKDIR /app
COPY build/libs/zenwk-security-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8087
# Agregar opciones para permitir acceso a métricas del sistema
ENV JAVA_OPTS="--add-opens java.base/jdk.internal.platform=ALL-UNNAMED"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]