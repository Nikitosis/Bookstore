FROM openjdk:8u121-jre-alpine

COPY target/Library-1.0-SNAPSHOT.jar /data/Library-1.0-SNAPSHOT.jar

COPY configuration.yml /data/configuration.yml

#RUN java -jar /data/Library-1.0-SNAPSHOT.jar db migrate /data/configuration.yml

#ENV DB_IP=12.12
#ENV DB_NAME=bookstore
#ENV SERVICE_CLIENTPORT=9000
#ENV SERVICE_ADMINPORT=9001
#ENV LOGGER_SERVICE_IP=12.21
#ENV LOGGER_SERVICE_PORT=9002

EXPOSE 9000 9001

ENTRYPOINT ["java","-jar","/data/Library-1.0-SNAPSHOT.jar","server","/data/configuration.yml"]
