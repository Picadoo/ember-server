#!/usr/bin/env bash
# Supervised hourly backup loop (the box has no cron). Runs scripts/ember-backup.sh once an hour, retries a failed
# pass after 10 minutes, and catches up immediately after a box restart when the last good pass is older than 1 hour.
#   scripts/ember-backup-loop.sh start    start in the background unless already running (idempotent; called by
#                                         server-runtime/start.sh, login-runtime/start.sh and scripts/ember-up.sh)
#   scripts/ember-backup-loop.sh stop     stop it (exact pid from the pid file)
#   scripts/ember-backup-loop.sh status   running? + last log lines
#   scripts/ember-backup-loop.sh now      one pass in the foreground (same as scripts/ember-backup.sh)
# Files: $ROOT/loop.pid, $ROOT/loop.log (loop events), $ROOT/backup.log (pass results), $ROOT/last-ok, $ROOT/last-try
set -uo pipefail
SELF="$(cd "$(dirname "$0")" && pwd)/$(basename "$0")"
DIR="$(dirname "$SELF")"
ROOT="${EMBER_BACKUP_ROOT:-/workspace/backup/db-auto}"
PERIOD="${EMBER_BACKUP_PERIOD:-3600}"
RETRY="${EMBER_BACKUP_RETRY:-600}"
PIDF="$ROOT/loop.pid"
mkdir -p "$ROOT"; chmod 700 "$ROOT" 2>/dev/null

alive() {
  local pid; [ -f "$PIDF" ] || return 1; pid=$(cat "$PIDF" 2>/dev/null)
  [ -n "$pid" ] && tr '\0' ' ' <"/proc/$pid/cmdline" 2>/dev/null | grep -q 'ember-backup-loop.sh run'
}

case "${1:-status}" in
  start)
    if alive; then echo "backup loop already running (pid $(cat "$PIDF"))"; exit 0; fi
    nohup setsid bash "$SELF" run >>"$ROOT/loop.log" 2>&1 </dev/null &
    echo $! >"$PIDF"
    echo "backup loop started (pid $!) → $ROOT/backup.log"
    ;;
  stop)
    if alive; then pid=$(cat "$PIDF"); kill "$pid" && rm -f "$PIDF" && echo "stopped pid $pid"; else echo "not running"; fi
    ;;
  status)
    if alive; then echo "running (pid $(cat "$PIDF"))"; else echo "NOT running"; fi
    [ -f "$ROOT/last-ok" ] && echo "last ok: $(date -d "@$(cat "$ROOT/last-ok")" '+%Y-%m-%d %H:%M:%S %Z')"
    tail -n 6 "$ROOT/backup.log" 2>/dev/null
    ;;
  now)
    exec "$DIR/ember-backup.sh"
    ;;
  run)
    echo "$(date '+%F %T') loop start pid $$ period ${PERIOD}s"
    trap 'echo "$(date "+%F %T") loop stop pid $$"; exit 0' TERM INT
    while true; do
      now=$(date +%s)
      ok=$(cat "$ROOT/last-ok" 2>/dev/null || echo 0)
      try=$(cat "$ROOT/last-try" 2>/dev/null || echo 0)
      if [ $((now - ok)) -ge "$PERIOD" ] && [ $((now - try)) -ge "$RETRY" ]; then
        echo "$now" >"$ROOT/last-try"
        if "$DIR/ember-backup.sh" >/dev/null 2>&1; then echo "$(date '+%F %T') pass ok"
        else echo "$(date '+%F %T') pass FAILED (see backup.log; retry in ${RETRY}s)"; fi
      fi
      sleep 60 & wait $!
    done
    ;;
  *) echo "usage: $0 start|stop|status|now" >&2; exit 2 ;;
esac
