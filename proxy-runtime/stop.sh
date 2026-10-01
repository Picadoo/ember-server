#!/usr/bin/env bash
# Graceful stop: Waterfall `end` via console.fifo, SIGTERM/SIGKILL only as fallback.
set -euo pipefail
cd "$(dirname "$0")"
source /workspace/minecraft/scripts/console-fifo.sh
PIDFILE=proxy.pid
if [ ! -f "$PIDFILE" ]; then echo "Proxy not running."; ember_fifo_release; exit 0; fi
PID=$(cat "$PIDFILE")
if kill -0 "$PID" 2>/dev/null; then
  ember_graceful_stop "$PID" end 30
fi
rm -f "$PIDFILE"; ember_fifo_release; echo "Proxy stopped."
