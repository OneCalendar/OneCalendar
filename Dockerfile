# Build with:
# docker build .

FROM ubuntu:14.04

RUN apt-get update
RUN apt-get install -y wget unzip npm nodejs openjdk-7-jdk
RUN update-alternatives --install /usr/bin/node node /usr/bin/nodejs 0
RUN wget "http://downloads.typesafe.com/play/2.2.3/play-2.2.3.zip" -O /tmp/play.zip
RUN unzip /tmp/play.zip -d /
ENV PATH $PATH:/play-2.2.3

EXPOSE 9000 9876

WORKDIR /docker

ENTRYPOINT /bin/bash
