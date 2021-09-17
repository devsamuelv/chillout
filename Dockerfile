FROM adoptopenjdk/openjdk8

WORKDIR /chillout

COPY target/chillout-1.0-SNAPSHOT.jar /chillout

ARG TOKEN=null

RUN java -jar chillout-1.0-SNAPSHOT.jar