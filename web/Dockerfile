#FROM maven:3.8.3-openjdk-17 AS MAVEN_BUILD
#COPY ./pom.xml ./pom.xml
#RUN mvn dependency:go-offline -B
#COPY ./src ./src
#RUN mvn package

FROM openjdk:17-alpine
ADD target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]