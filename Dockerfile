FROM openjdk:8-jre
ARG JAR_FILE=target/swipe-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} swipe-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/swipe-0.0.1-SNAPSHOT.jar"]