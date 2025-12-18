FROM eclipse-temurin:21-jdk
EXPOSE 8080
COPY target/marketPlace-0.0.1-SNAPSHOT.jar marketPlace-0.0.1-SNAPSHOT.jar
COPY src/main/resources/application.yml application.yml
ENTRYPOINT ["java","-jar","/marketPlace-0.0.1-SNAPSHOT.jar"]


