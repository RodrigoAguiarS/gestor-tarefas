# Usa a imagem do JDK 21 para compilar a aplicação
FROM eclipse-temurin:21-jdk AS builder

# Define o diretório de trabalho
WORKDIR /app

# Copia os arquivos do projeto
COPY . .

# Dá permissão de execução ao Gradle Wrapper (caso ainda não tenha sido feito)
RUN chmod +x gradlew

# Executa o build do projeto dentro do container
RUN ./gradlew clean build --no-daemon --stacktrace

# Usa uma nova imagem com o JRE 21 para rodar a aplicação
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copia o JAR gerado na etapa anterior para o novo container
COPY --from=builder /app/build/libs/*.jar app.jar

# Expõe a porta 8080
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
