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

ARG DOCKER_ARG_JAR_NAME
ENV JAR_NAME $DOCKER_ARG_JAR_NAME

COPY . /application/

WORKDIR /application

#RUN echo `pwd`/$JAR_NAME && chmod +x $JAR_NAME && ls -lah  && echo "$(java -jar $JAR_NAME)"

CMD java -jar $JAR_NAME