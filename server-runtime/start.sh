#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
source ./env.sh
source /workspace/minecraft/scripts/console-fifo.sh

MODE="${1:-auto}"
case "$MODE" in
  stock) JAR="$STOCK_JAR" ;;
  custom)
    JAR="${CUSTOM_JAR:-$RUNTIME_DIR/paper-custom.jar}"
    if [ ! -f "$JAR" ]; then
      echo "paper-custom.jar missing. Copy rebuilt Paper jar to $RUNTIME_DIR/paper-custom.jar" >&2
      exit 1
    fi
    ;;
  rebuilt)
    if [ -z "${REBUILT_JAR}" ] || [ ! -f "$REBUILT_JAR" ]; then
      echo "No rebuilt jar found. Build Paper first." >&2
      exit 1
    fi
    JAR="$REBUILT_JAR"
    ;;
  auto|*)
    if [ -f "${CUSTOM_JAR:-}" ]; then
      JAR="$CUSTOM_JAR"; MODE=custom
    elif [ -n "${REBUILT_JAR}" ] && [ -f "$REBUILT_JAR" ]; then
      JAR="$REBUILT_JAR"; MODE=rebuilt
    else
      JAR="$STOCK_JAR"; MODE=stock
    fi
    ;;
esac

if [ ! -f "$JAR" ]; then
  echo "Jar not found: $JAR" >&2
  exit 1
fi

if [ ! -e plugins ]; then
  ln -sfn /workspace/minecraft/plugins plugins
fi

mkdir -p logs
PIDFILE=server.pid
if [ -f "$PIDFILE" ] && kill -0 "$(cat "$PIDFILE")" 2>/dev/null; then
  echo "Server already running (pid $(cat "$PIDFILE")). Use ./stop.sh first." >&2
  exit 1
fi

# hourly verified backup of MySQL + player files (idempotent; see scripts/ember-backup-loop.sh)
/workspace/minecraft/scripts/ember-backup-loop.sh start >/dev/null 2>&1 || echo "WARN: backup loop did not start" >&2

echo "Starting Paper ($MODE): $JAR"
echo "  127.0.0.1:25567 (behind proxy :25565, bungeecord=true) plugins=/workspace/minecraft/plugins"
ember_fifo_prepare   # stdin = console.fifo (kept open by a holder) → scripts/console.sh play "cmd"
nohup "$JAVA_HOME/bin/java" -Xms512M -Xmx1536M -jar "$JAR" nogui \
  < console.fifo > logs/stdout.log 2>&1 &
echo $! > "$PIDFILE"
echo "PID $(cat "$PIDFILE") — logs: $RUNTIME_DIR/logs/stdout.log"
echo "Console: /workspace/minecraft/scripts/console.sh play \"cmd\"  ·  Stop with: $RUNTIME_DIR/stop.sh"
