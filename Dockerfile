# Build with :
# docker build -t onecalendar/dev .
# Run with :
# docker run -i -t -p 9000:9000 -v $(pwd):/docker:rw onecalendar/dev <sha1>

FROM ubuntu:14.04

RUN apt-get update
RUN apt-get install -y wget unzip mongodb npm nodejs openjdk-7-jdk
RUN wget "http://downloads.typesafe.com/play/2.2.3/play-2.2.3.zip" -O /tmp/play.zip
RUN unzip /tmp/play.zip -d /
ENV PATH $PATH:/play-2.2.3

EXPOSE 9000

WORKDIR /docker

ENTRYPOINT ["/bin/bash"]