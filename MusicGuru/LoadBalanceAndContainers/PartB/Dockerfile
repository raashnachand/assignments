FROM openjdk:8
COPY *.java /usr/src/MGS/
COPY musicdata.txt /usr/src/MGS/
WORKDIR /usr/src/MGS
RUN javac MusicGuruServer.java
EXPOSE 5000
CMD ["java", "MusicGuruServer", "5000"]
