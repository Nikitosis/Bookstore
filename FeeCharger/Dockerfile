FROM openjdk:8u121-jre-alpine

COPY target/FeeCharger-1.0-SNAPSHOT.jar /data/FeeCharger-1.0-SNAPSHOT.jar

COPY configuration.yml /data/configuration.yml

EXPOSE 9006 9007

ENTRYPOINT ["java","-jar","/data/FeeCharger-1.0-SNAPSHOT.jar","server","/data/configuration.yml"]
