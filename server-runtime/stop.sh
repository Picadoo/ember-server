#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
PIDFILE=server.pid
if [ ! -f "$PIDFILE" ]; then
  echo "No pidfile."
  exit 0
fi
PID=$(cat "$PIDFILE")
if kill -0 "$PID" 2>/dev/null; then
  echo "Stopping pid $PID..."
  kill "$PID" || true
  for i in $(seq 1 40); do
    kill -0 "$PID" 2>/dev/null || break
    sleep 1
  done
  if kill -0 "$PID" 2>/dev/null; then
    echo "Force kill..."
    kill -9 "$PID" || true
  fi
  echo "Stopped."
else
  echo "Process $PID not running."
fi
rm -f "$PIDFILE"
