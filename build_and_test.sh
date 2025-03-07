#!/bin/bash

docker compose --profile minecraft down
docker container prune --force
docker image rm bungee-portals-java-mod:latest
docker system prune --force

docker compose --profile build up && \
docker cp build_mc_mod:/java-mod/build/libs/java-mod-1.0.0.jar mods/java-mod-1.0.0.jar && \
docker compose --profile minecraft up