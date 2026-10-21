# Etapa 1: Build
# Usa uma imagem Maven com Java 25 para compilar o projeto
FROM maven:3.9.16-eclipse-temurin-25 AS build

# Define o diretório de trabalho dentro do container
WORKDIR /opt/app

# Copia o código-fonte completo para dentro do container
COPY . .

# Executa o build do projeto, gerando o JAR em /opt/app/target/
RUN mvn clean package -DskipTests

# Etapa 2: Runtime
# Usa uma imagem leve do Java 25 para rodar o app
FROM eclipse-temurin:25-jre
LABEL authors="renan" \
        MAINTAINER="Renan" \
        application="ms-pagamento" \
        version="1.0"

WORKDIR /opt/app

# Copia o JAR gerado na etapa de build para a imagem final
COPY --from=build /opt/app/target/*.jar /opt/app/app.jar

# Expoe a porta 8080
EXPOSE 8080

# Define o comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]