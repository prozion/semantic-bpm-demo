#!/bin/bash

cd ~/work/semantic_bpm_demo/server

user=denis

project_name="semantic_bpm_demo"

for pid in `pgrep -f ${project_name}`
do
  echo "kill ${pid}"
  kill $pid
done

caddy stop

# nohup lein ring server-headless > /dev/null >/var/log/projects/taganrog-history-kb/ring.log 2>&1& echo $! > /var/run/denis/ring.pid

clj -M:stop
nohup clj -M:start

caddy run --config /etc/caddy/Caddyfile

exit 0
