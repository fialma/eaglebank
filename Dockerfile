# FROM maven:3.8.2-jdk-8 # for Java 8
#FROM maven:3.8.5-openjdk-17
#WORKDIR /eaglebank-api
#COPY . .
#RUN mvn clean compile install -DskipTests

#CMD mvn spring-boot:run

FROM openjdk:21-ea-1-jdk-slim
WORKDIR /opt
ENV PORT=8080
EXPOSE 8080

COPY target/eaglebank-api-*.jar /opt/app.jar
# Define JAVA_OPTS
ENV JAVA_OPTS="-Xmx512m -Dspring.profiles.active=dev"

# Use the shell form. The shell will expand $JAVA_OPTS before running 'java'.
CMD java $JAVA_OPTS -jar app.jar