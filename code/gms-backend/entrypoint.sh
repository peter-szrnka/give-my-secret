#!/bin/sh
export CONTAINER_ID=$(cat /proc/self/cgroup | grep 'pids:' | sed 's/^.*\///')
exec "$@"