FROM openjdk:8-jdk-alpine
LABEL maintainer="themars@gmail.com"

EXPOSE 8080
# Timezone 보정
RUN apk add tzdata
RUN cp /usr/share/zoneinfo/Asia/Seoul /etc/localtime
RUN echo "Asia/Seoul" > /etc/timezone
RUN apk add curl

ARG JAR_FILE=./build/libs/api-skeleton.jar
ADD ${JAR_FILE} /api-skeleton.jar

ENTRYPOINT java \
${heapOpt} \
-Djava.security.egd=file:/dev/./urandom \
-Dspring.profiles.active="${env}" \
-jar /api-skeleton.jar

