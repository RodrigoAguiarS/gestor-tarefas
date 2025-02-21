
FROM eclipse-temurin:17-jdk

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia o arquivo JAR gerado pelo Gradle
COPY build/libs/*.jar app.jar

# Expõe a porta 8080
EXPOSE 8080

# Comando para rodar a aplicação
CMD ["java", "-jar", "app.jar"]