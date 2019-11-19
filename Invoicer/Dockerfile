FROM openjdk:8u121-jre-alpine

COPY target/Invoicer-1.0-SNAPSHOT.jar /data/Invoicer-1.0-SNAPSHOT.jar

COPY configuration.yml /data/configuration.yml

EXPOSE 9006 9007

ENTRYPOINT ["java","-jar","/data/Invoicer-1.0-SNAPSHOT.jar","server","/data/configuration.yml"]
