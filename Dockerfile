# FROM maven:3.8.2-jdk-8 # for Java 8
FROM maven:3.8.5-openjdk-17

WORKDIR /eaglebank-api
COPY . .
RUN mvn clean compile install -DskipTests

CMD mvn spring-boot:run