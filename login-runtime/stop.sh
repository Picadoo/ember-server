#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
PIDFILE=server.pid
if [ ! -f "$PIDFILE" ]; then echo "not running"; exit 0; fi
PID=$(cat "$PIDFILE")
if kill -0 "$PID" 2>/dev/null; then
  if [ -w "/proc/$PID/fd/0" ]; then printf 'stop\n' > "/proc/$PID/fd/0"; fi
  for i in $(seq 1 30); do kill -0 "$PID" 2>/dev/null || break; sleep 1; done
  kill -9 "$PID" 2>/dev/null || true
fi
rm -f "$PIDFILE"
echo "Login stopped."
