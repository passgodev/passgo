FROM openjdk:21-slim-buster as pom_dependency_cache
COPY pom.xml ./mvnw ./
COPY ./.mvn/ ./.mvn/
RUN ./mvnw dependency:resolve

FROM pom_dependency_cache AS base
WORKDIR /app
COPY --from=pom_dependency_cache /root/.m2/ /root/.m2/
COPY . .
RUN ./mvnw -DskipTests clean package spring-boot:repackage

FROM ubuntu/jre:21-24.04_stable AS prod
WORKDIR /app
COPY --from=base ./app/target/*.jar ./app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "./app/app.jar"]

# possible improvements, setting up profile, start command, naming, exposing port, better caching?
# ideal: copy jar as an artefact of CI