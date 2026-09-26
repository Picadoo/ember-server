#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
PIDFILE=proxy.pid
if [ ! -f "$PIDFILE" ]; then echo "Proxy not running."; exit 0; fi
PID=$(cat "$PIDFILE")
if kill -0 "$PID" 2>/dev/null; then
  kill "$PID" || true
  for i in $(seq 1 20); do kill -0 "$PID" 2>/dev/null || break; sleep 1; done
  kill -9 "$PID" 2>/dev/null || true
fi
rm -f "$PIDFILE"; echo "Proxy stopped."
