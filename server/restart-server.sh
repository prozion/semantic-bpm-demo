#!/bin/bash

project_name="semantic_bpm_demo"

cd "~/work/$project_name/server"

user=denis

for pid in `pgrep -f ${project_name}`
do
  echo "kill ${pid}"
  kill $pid
done

caddy stop

# nohup lein ring server-headless > /dev/null >/var/log/projects/taganrog-history-kb/ring.log 2>&1& echo $! > /var/run/denis/ring.pid

# clj -M:stop

caddy run --config /etc/caddy/Caddyfile

nohup clj -M:start

exit 0
