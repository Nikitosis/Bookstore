FROM openjdk:8u121-jre-alpine

COPY target/MailSender-1.0-SNAPSHOT.jar /data/MailSender-1.0-SNAPSHOT.jar

COPY configuration.yml /data/configuration.yml

EXPOSE 9008 9009

ENTRYPOINT ["java","-jar","/data/MailSender-1.0-SNAPSHOT.jar","server","/data/configuration.yml"]
