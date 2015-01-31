#!/bin/sh

docker rm onecalendar_db

# See https://github.com/dockerfile/mongodb#run-mongod-w-persistentshared-directory
docker pull dockerfile/mongodb
docker run --detach=true --volume=$(pwd)/data/db:/data/db --name onecalendar_db dockerfile/mongodb
