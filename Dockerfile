FROM openjdk:21-slim-buster as pom_dependency_cache
COPY pom.xml ./mvnw ./
COPY ./.mvn/ ./.mvn/
RUN ./mvnw dependency:resolve

FROM pom_dependency_cache AS base
WORKDIR /app
COPY --from=pom_dependency_cache /root/.m2/ /root/.m2/
COPY . .
RUN ./mvnw -DskipTests clean package spring-boot:repackage

# split to 2 different dockerfiles, one for building code, second for running code in different environments
# dockerfile for building put on ci/cd
# building zostaje tutaj do uruchomienia apki

FROM ubuntu/jre:21-24.04_stable AS prod
WORKDIR /app
COPY --from=base ./app/target/*.jar ./app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "./app/app.jar"]

FROM base AS dev
WORKDIR /app
COPY --from=base ./app/target/*.jar ./app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "./app/app.jar"]

# possible improvements, setting up profile, start command, naming, exposing port, better caching?
# ideal: copy jar as an artefact of CI