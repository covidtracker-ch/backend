FROM openjdk:8-jdk-alpine
VOLUME /tmp
EXPOSE 8080
COPY target/app.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Xmx160m","-XX:MaxRAM=190m","-Dspring.profiles.active=prod","-jar","/app.jar"]