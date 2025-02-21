# Usa a imagem do JDK 17
FROM eclipse-temurin:17-jdk

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia o arquivo JAR gerado pelo Gradle
COPY build/libs/gestortarefas-0.0.1-SNAPSHOT.jar app.jar

# Garante que o arquivo JAR seja copiado corretamente
RUN ls -l app.jar

# Expõe a porta da aplicação
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]