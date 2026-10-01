#!/usr/bin/env bash
# Send a console command to a running Ember server and show the log lines it produced.
#   scripts/console.sh <play|login|proxy> "command" [waitSeconds=1.5]
# Uses <runtime>/console.fifo (set up by the start.sh scripts). No RCON, no op needed.
set -euo pipefail
M=/workspace/minecraft
case "${1:-}" in
  play)  DIR=$M/server-runtime ;;
  login) DIR=$M/login-runtime ;;
  proxy) DIR=$M/proxy-runtime ;;
  *) echo "usage: $0 <play|login|proxy> \"command\" [waitSeconds]" >&2; exit 2 ;;
esac
[ -n "${2:-}" ] || { echo "usage: $0 <play|login|proxy> \"command\" [waitSeconds]" >&2; exit 2; }
cd "$DIR"
source "$M/scripts/console-fifo.sh"
LOG=logs/latest.log
before=$(wc -l < "$LOG" 2>/dev/null || echo 0)
ember_fifo_send "${2#/}" || { echo "send failed: is the $1 server running with console.fifo stdin?" >&2; exit 1; }
sleep "${3:-1.5}"
tail -n +"$((before + 1))" "$LOG" 2>/dev/null || true
