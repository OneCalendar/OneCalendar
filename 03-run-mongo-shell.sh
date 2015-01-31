#!/bin/sh

# Runs an instance of the same server container to act as a client.
# see https://github.com/dockerfile/mongodb#run-mongo
docker run --interactive=true --tty=true --rm --link=onecalendar_db:onecalendar_db --name="onecalendar_mongo" dockerfile/mongodb '/bin/bash' -c '/usr/bin/mongo --host $ONECALENDAR_DB_PORT_27017_TCP_ADDR OneCalendar'
