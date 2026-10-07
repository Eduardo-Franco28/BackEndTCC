FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B
#Aqui ele cria o .jar com tudo já

FROM eclipse-temurin:25-jre
#Pega o java menor só pra rodar mesmo
WORKDIR /app
COPY --from=build /app/target/*.jar /app.jar
#Só precisa do .jar pra rodar, não precisa chamar as dependências também
EXPOSE 8080
CMD ["java","-jar","app.jar"]