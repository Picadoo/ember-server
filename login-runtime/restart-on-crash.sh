#!/usr/bin/env bash
# D12195: Paper restart-script wrapper for login (mirror of play; port 25566).
set -euo pipefail
cd "$(dirname "$0")"
LOG=/tmp/ember-login-restart.log
PORT=25566
PIDFILE=server.pid
ROOT="$(pwd)"

(
  ts() { date '+%Y-%m-%d %H:%M:%S %z'; }
  cd "$ROOT"
  echo "$(ts) restart-on-crash: invoked (login port ${PORT})" >>"$LOG"
  deadline=$((SECONDS + 120))
  while (( SECONDS < deadline )); do
    alive=0
    if [[ -f $PIDFILE ]]; then
      old=$(cat "$PIDFILE" 2>/dev/null || true)
      if [[ -n "$old" ]] && kill -0 "$old" 2>/dev/null; then
        alive=1
      fi
    fi
    port_busy=0
    if ss -lntH 2>/dev/null | grep -qE ":${PORT}([[:space:]]|$)"; then
      port_busy=1
    elif (echo >/dev/tcp/127.0.0.1/"${PORT}") >/dev/null 2>&1; then
      port_busy=1
    fi
    if (( alive == 0 && port_busy == 0 )); then
      break
    fi
    echo "$(ts) waiting: pid_alive=$alive port_busy=$port_busy" >>"$LOG"
    sleep 2
  done
  rm -f "$PIDFILE"
  echo "$(ts) starting ./start.sh auto" >>"$LOG"
  ./start.sh auto >>"$LOG" 2>&1
  echo "$(ts) start.sh exit=$?" >>"$LOG"
) >/dev/null 2>&1 &
disown || true
exit 0
