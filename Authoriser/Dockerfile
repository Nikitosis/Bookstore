FROM openjdk:8u121-jre-alpine

COPY target/Authoriser-1.0-SNAPSHOT.jar /data/Authoriser-1.0-SNAPSHOT.jar

COPY configuration.yml /data/configuration.yml

#ENV DB_IP=12.10.2
#ENV DB_USERNAME=admin
#ENV DB_PASSWORD=12345678
#ENV DB_NAME=bookstore
#ENV SERVICE_CLIENTPORT=9004
#ENV SERVICE_ADMINPORT=9005

EXPOSE 9004 9005

ENTRYPOINT ["java","-jar","/data/Authoriser-1.0-SNAPSHOT.jar","server","/data/configuration.yml"]
