#!/usr/bin/env bash
# Graceful stop: `stop` via console.fifo (saves worlds), SIGTERM/SIGKILL only as fallback.
set -euo pipefail
cd "$(dirname "$0")"
source /workspace/minecraft/scripts/console-fifo.sh
PIDFILE=server.pid
if [ ! -f "$PIDFILE" ]; then
  echo "No pidfile."
  ember_fifo_release
  exit 0
fi
PID=$(cat "$PIDFILE")
if kill -0 "$PID" 2>/dev/null; then
  echo "Stopping pid $PID..."
  ember_graceful_stop "$PID" stop 90
  echo "Stopped."
else
  echo "Process $PID not running."
fi
rm -f "$PIDFILE"
ember_fifo_release
