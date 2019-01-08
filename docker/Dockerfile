FROM registry.gs.youyuwo.com/base/docker-springboot-pinpoint:latest

COPY cardequity-promotion-biz/target/cardequity-promotion-biz-1.0.0-SNAPSHOT.jar /app.jar

ENV PATH="${JAVA_HOME}/bin:${PATH}"

EXPOSE 8080

CMD ["java","-jar","app.jar"]


