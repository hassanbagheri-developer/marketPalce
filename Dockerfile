FROM eclipse-temurin:21-jdk
EXPOSE 8080
COPY target/markerPlace-0.0.1-SNAPSHOT.jar markerPlace-0.0.1-SNAPSHOT.jar
COPY src/main/resources/application.yml application.yml
ENTRYPOINT ["java","-jar","/markerPlace-0.0.1-SNAPSHOT.jar"]


