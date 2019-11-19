FROM openjdk:8u121-jre-alpine

COPY target/Library-1.0-SNAPSHOT.jar /data/Library-1.0-SNAPSHOT.jar

COPY configuration.yml /data/configuration.yml

EXPOSE 9000 9001

ENTRYPOINT ["java","-jar","/data/Library-1.0-SNAPSHOT.jar","server","/data/configuration.yml"]
