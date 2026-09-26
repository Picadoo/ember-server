#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
source ./env.sh
JAR="$RUNTIME_DIR/paper-custom.jar"
PIDFILE=server.pid
if [ -f "$PIDFILE" ] && kill -0 "$(cat "$PIDFILE")" 2>/dev/null; then
  echo "Login server already running (pid $(cat "$PIDFILE"))." >&2
  exit 1
fi
mkdir -p logs
echo "Starting LOGIN Paper on :25566"
nohup "$JAVA_HOME/bin/java" -Xms256M -Xmx768M -jar "$JAR" nogui \
  > logs/stdout.log 2>&1 &
echo $! > "$PIDFILE"
echo "PID $(cat "$PIDFILE") — logs: $RUNTIME_DIR/logs/stdout.log"
