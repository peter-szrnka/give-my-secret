#!/bin/sh
export DOCKER_CONTAINER_ID=$(cat /etc/hostname)
exec "$@"