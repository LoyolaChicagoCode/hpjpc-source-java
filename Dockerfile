FROM openjdk:11
COPY . /usr/src/hpjpc-source-java
WORKDIR /usr/src/hpjpc-source-java
RUN ./gradlew compileJava jar
