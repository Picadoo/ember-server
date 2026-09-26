#!/usr/bin/env bash
# Ember proxy (Waterfall, needs Java 11+). Public :25565 → login 127.0.0.1:25566 → play 127.0.0.1:25567.
set -euo pipefail
cd "$(dirname "$0")"
JAVA="${PROXY_JAVA:-/workspace/minecraft/tools/jdk-21.0.12.1+1/bin/java}"
PIDFILE=proxy.pid
if [ -f "$PIDFILE" ] && kill -0 "$(cat "$PIDFILE")" 2>/dev/null; then
  echo "Proxy already running (pid $(cat "$PIDFILE"))." >&2; exit 1
fi
mkdir -p logs
echo "Starting Waterfall proxy on 0.0.0.0:25565"
nohup "$JAVA" -Xms128M -Xmx384M -jar waterfall.jar < /dev/null > logs/stdout.log 2>&1 &
echo $! > "$PIDFILE"
echo "PID $(cat "$PIDFILE") — logs: $(pwd)/logs/stdout.log"
