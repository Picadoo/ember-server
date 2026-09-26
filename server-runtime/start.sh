#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
source ./env.sh

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

echo "Starting Paper ($MODE): $JAR"
echo "  port=25565 online-mode=false plugins=/workspace/minecraft/plugins"
nohup "$JAVA_HOME/bin/java" -Xms512M -Xmx1536M -jar "$JAR" nogui \
  > logs/stdout.log 2>&1 &
echo $! > "$PIDFILE"
echo "PID $(cat "$PIDFILE") — logs: $RUNTIME_DIR/logs/stdout.log"
echo "Stop with: $RUNTIME_DIR/stop.sh"
