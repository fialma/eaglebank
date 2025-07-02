# eaglebank

Requirements:
Maven (to compile)
Docker (for tests and to Run)

http://localhost:8080/swagger-ui/index.html


To Compile:
mvn clean compile install

To Compile without Test:
clean compile install -DskipTests

To Run:
docker compose up

Tear down:
docker compose down

Tear down with deletion of all containers and images
docker compose down --rmi all

