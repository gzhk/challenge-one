FROM frolvlad/alpine-oraclejdk8:slim
ADD challenge-one-0.0.1-SNAPSHOT.jar app.jar
RUN sh -c 'touch /app.jar'
RUN mkdir /tmp/GFT
ENTRYPOINT ["sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -jar /app.jar"]
