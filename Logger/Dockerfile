FROM openjdk:8u121-jre-alpine

COPY target/Logger-1.0-SNAPSHOT.jar /data/Logger-1.0-SNAPSHOT.jar

COPY configuration.yml /data/configuration.yml

#ENV DB_NAME=bookstore
#ENV SERVICE_CLIENTPORT=9002
#ENV SERVICE_ADMINPORT=9003

EXPOSE 9002 9003

ENTRYPOINT ["java","-jar","/data/Logger-1.0-SNAPSHOT.jar","server","/data/configuration.yml"]
