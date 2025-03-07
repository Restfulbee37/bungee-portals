#!/bin/bash

docker compose --profile minecraft down
docker system prune --force

docker compose --profile build up && \

docker cp build_mc_mod:/java-mod/build/libs/ mods/ && \

docker compose --profile minecraft up -d