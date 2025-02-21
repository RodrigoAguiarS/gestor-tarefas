# Usa a imagem do JDK 21 para compilar e rodar a aplicação
FROM eclipse-temurin:21-jdk AS builder

# Define o diretório de trabalho
WORKDIR /app

# Copia os arquivos do projeto
COPY . .

# Dá permissão de execução ao Gradle Wrapper
RUN chmod +x gradlew

# Executa o build do projeto dentro do container
RUN ./gradlew clean build --no-daemon --stacktrace

# Usa uma nova imagem para rodar a aplicação, reduzindo o tamanho final
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copia apenas o JAR gerado na etapa anterior
COPY --from=builder /app/build/libs/*.jar app.jar

# Expõe a porta 8080
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]