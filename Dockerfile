# Usa a imagem do JDK 17 para compilar e rodar a aplicação
FROM eclipse-temurin:17-jdk AS builder

# Define o diretório de trabalho
WORKDIR /app

# Copia os arquivos do projeto
COPY . .

# Executa o build do projeto dentro do container
RUN ./gradlew clean build --no-daemon

# Usa uma nova imagem para rodar a aplicação, reduzindo o tamanho final
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copia apenas o JAR gerado na etapa anterior
COPY --from=builder /app/build/libs/*.jar app.jar

# Expõe a porta 8080
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
