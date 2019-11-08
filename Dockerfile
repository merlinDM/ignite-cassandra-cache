# TODO: Multistage build
#
#FROM gradle:5.6.2-jdk11 as builder
#
#COPY . /application
#
#ENV GRADLE_OPTS="$GRADLE_OPTS -Dorg.gradle.daemon=false"
#
#WORKDIR /application
#
#ENTRYPOINT ["gradle", "clean", "test", "--info"]

FROM openjdk:11 as runner

COPY build/libs/ /application/

COPY build/resources/main/ /application/

WORKDIR /application

ENTRYPOINT ["java", "-jar", "ignite-code-all.jar"]