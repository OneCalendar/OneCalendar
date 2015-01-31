#!/bin/sh

# docker run -i                -t         -p       9000                          --name onecalendar_play --link onecalendar_db:onecalendar_db  -v      $(pwd):/docker:rw onecalendar_play
docker run --interactive=true --tty=true --publish=9000:9000 --publish=9876:9876 --name onecalendar_play --link onecalendar_db:onecalendar_db --volume=$(pwd):/docker:rw onecalendar/play