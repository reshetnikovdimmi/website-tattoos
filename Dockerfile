FROM eclipse-temurin:17-jdk-alpine
MAINTAINER DOCKER
COPY target/maxsim-0.0.1-SNAPSHOT.jar maxsim-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/maxsim-0.0.1-SNAPSHOT.jar"]