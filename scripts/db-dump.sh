#!/usr/bin/env bash
# Dump the ember + authme databases into a timestamped file (default /workspace/backup/).
# Usage: scripts/db-dump.sh [label] [outdir]
# Auth: root over the unix socket (sudo) if available, otherwise the CoreRpg config credentials
# passed through a 0600 temp defaults-file. The password is never printed or put on a command line.
set -euo pipefail
umask 077   # dumps contain password hashes
LABEL="${1:-manual}"
OUT="${2:-/workspace/backup}"
M="${EMBER_RUNTIME:-/workspace/minecraft}"
TS="$(date +%Y%m%d-%H%M%S)"
mkdir -p "$OUT"
FILE="$OUT/db-ember-authme-$TS-$LABEL.sql"
DUMP=(mysqldump --single-transaction --routines --triggers --databases ember authme)
if [ -z "${DB_DUMP_NO_SUDO:-}" ] && sudo -n true 2>/dev/null && sudo -n mysql -uroot -e 'SELECT 1' >/dev/null 2>&1; then
  sudo -n "${DUMP[@]}" -uroot >"$FILE.part"
else
  CFG="$M/plugins/CoreRpg/config.yml"
  get() { awk -v k="$1" '/^mysql:/{m=1;next} m&&/^[^ ]/{m=0} m&&$1==k":"{sub(/^[^:]*:[ ]*/,"");gsub(/^["'\'']|["'\'']$/,"");print;exit}' "$CFG"; }
  TMP="$(mktemp)"; chmod 600 "$TMP"; trap 'rm -f "$TMP"' EXIT
  printf '[client]\nhost=%s\nport=%s\nuser=%s\npassword="%s"\n' "$(get host)" "$(get port)" "$(get username)" "$(get password)" >"$TMP"
  mysqldump --defaults-extra-file="$TMP" --single-transaction --routines --triggers --databases ember authme >"$FILE.part"
fi
mv "$FILE.part" "$FILE"
echo "dump: $FILE ($(du -h "$FILE" | cut -f1), $(grep -c '^INSERT INTO' "$FILE") insert statements)"
