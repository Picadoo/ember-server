#!/usr/bin/env bash
# One-command restore of an Ember MySQL dump (ember + authme), scratch first.
#
#   scripts/db-restore.sh latest                 dry run: load the newest verified hourly dump into scratch databases
#   scripts/db-restore.sh <file.sql[.gz]>        dry run with that dump (db-auto/hourly|daily, /workspace/backup/*.sql, …)
#   scripts/db-restore.sh <dump> --live          dry run, then (after typed confirmation) restore into the LIVE databases
#   options: --db ember|authme|both (default both) · --yes (no prompt; needs --live) · --keep (keep scratch DBs)
#            --force-running (allow --live while the play server is up — normally refused)
#
# Dry run = gzip test → load into `ember_restore_chk` / `authme_restore_chk` → per-table exact row counts, scratch vs
# live, with the difference → scratch DBs dropped (unless --keep). Nothing live is touched.
# Live = refuses while the play server (server-runtime/server.pid) runs, unless --force-running (CoreRpg caches player
# data and would write its memory back over the restore) → a fresh safety dump (scripts/db-dump.sh pre-restore) →
# load the dump into the live databases (the dump's DROP TABLE / CREATE TABLE replace each table it contains; tables
# that are not in the dump are left alone and listed) → counts again. Start the server afterwards and check
# 'MySQL connected' in server-runtime/logs/latest.log.
set -uo pipefail
umask 077
SELF_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SELF_DIR/lib-ember-db.sh"
M="$EMBER_RUNTIME"
ROOT="${EMBER_BACKUP_ROOT:-/workspace/backup/db-auto}"

DUMP=""; LIVE=0; YES=0; KEEP=0; FORCE=0; WHICH=both
while [ $# -gt 0 ]; do
  case "$1" in
    --live) LIVE=1 ;; --yes) YES=1 ;; --keep) KEEP=1 ;; --force-running) FORCE=1 ;;
    --db) WHICH="${2:-}"; shift ;;
    -h|--help) sed -n '2,19p' "$0"; exit 0 ;;
    *) DUMP="$1" ;;
  esac
  shift
done
case "$WHICH" in ember) DBS="ember" ;; authme) DBS="authme" ;; both) DBS="ember authme" ;; *) echo "--db ember|authme|both" >&2; exit 2 ;; esac
if [ "$DUMP" = latest ] || [ -z "$DUMP" ]; then
  DUMP=$(ls -1 "$ROOT"/hourly/db-*.sql.gz 2>/dev/null | sort | tail -n 1)
  [ -n "$DUMP" ] || { echo "no dumps in $ROOT/hourly" >&2; exit 1; }
fi
[ -r "$DUMP" ] || { echo "cannot read $DUMP" >&2; exit 1; }
say() { printf '\033[1m%s\033[0m\n' "$*"; }
cat_dump() { case "$DUMP" in *.gz) zcat "$DUMP" ;; *) cat "$DUMP" ;; esac; }

say "== dump: $DUMP ($(du -h "$DUMP" | cut -f1), $(date -r "$DUMP" '+%F %T'))"
case "$DUMP" in *.gz) gzip -t "$DUMP" || { echo "gzip test FAILED" >&2; exit 1; } ;; esac
for d in $DBS; do
  [ "$(cat_dump | grep -c "^USE \`$d\`;")" -gt 0 ] || { echo "dump has no database $d (was it made with --databases ember authme?)" >&2; exit 1; }
done
ember_db_init || exit 1

# keep only the sections of the chosen databases and rename them (sed on the two header lines of each section)
filtered() { # $1 = suffix ("" = live names)
  local sfx="$1"
  cat_dump | awk -v want=" $DBS " -v sfx="$sfx" '
    /^-- Current Database: `/ { db=$0; sub(/^-- Current Database: `/,"",db); sub(/`.*/,"",db); keep=(index(want," " db " ")>0) }
    /^CREATE DATABASE / || /^USE `/ { if (keep && sfx!="") gsub("`" db "`", "`" db sfx "`") }
    { if (keep || db=="") print }'
}

# ---------------------------------------------------------------- dry run
for d in $DBS; do ember_mysql -e "DROP DATABASE IF EXISTS \`${d}_restore_chk\`"; done
say "== loading into scratch: $(for d in $DBS; do printf '%s_restore_chk ' "$d"; done)"
if ! filtered _restore_chk | ember_mysql; then echo "scratch load FAILED" >&2; exit 1; fi

compare() { # $1 scratch-or-live-copy suffix → prints table / live / dump / diff
  local d t a b total=0 bad=0
  for d in $DBS; do
    printf '%-28s %10s %10s %8s\n' "$d.<table>" "live" "dump" "diff"
    declare -A L=() S=()
    while read -r t n; do L[$t]=$n; done < <(ember_table_counts "$d")
    while read -r t n; do S[$t]=$n; done < <(ember_table_counts "${d}$1")
    for t in $(printf '%s\n' "${!L[@]}" "${!S[@]}" | sort -u); do
      a=${L[$t]:--}; b=${S[$t]:--}
      if [ "$a" = - ] || [ "$b" = - ]; then df="only-$([ "$a" = - ] && echo dump || echo live)"; bad=$((bad+1))
      else df=$((b - a)); [ "$df" = 0 ] && df=0 || df=$(printf '%+d' "$df"); fi
      printf '  %-26s %10s %10s %8s\n' "$t" "$a" "$b" "$df"
    done
    unset L S
  done
}
say "== row counts: live vs dump (dump − live)"
compare _restore_chk
if [ "$KEEP" = 1 ]; then say "== scratch kept: $(for d in $DBS; do printf '%s_restore_chk ' "$d"; done)"
else for d in $DBS; do ember_mysql -e "DROP DATABASE IF EXISTS \`${d}_restore_chk\`"; done; say "== scratch dropped (dry run done, live untouched)"; fi
[ "$LIVE" = 1 ] || exit 0

# ---------------------------------------------------------------- live
PIDF="$M/server-runtime/server.pid"
if [ -f "$PIDF" ] && kill -0 "$(cat "$PIDF")" 2>/dev/null && [ "$FORCE" = 0 ]; then
  echo "The play server is running (pid $(cat "$PIDF")): stop it first (server-runtime/stop.sh), or pass --force-running." >&2
  exit 1
fi
if [ "$YES" = 0 ]; then
  printf '\nThis REPLACES the live tables of [%s] with the dump above.\nType RESTORE to continue: ' "$DBS"
  read -r ans; [ "$ans" = RESTORE ] || { echo "aborted, live untouched"; exit 1; }
fi
say "== safety dump of the current live databases"
"$SELF_DIR/db-dump.sh" pre-restore || { echo "safety dump FAILED, not restoring" >&2; exit 1; }
say "== restoring into live: $DBS"
if ! filtered "" | ember_mysql; then echo "LIVE LOAD FAILED — the safety dump above is the way back (scripts/db-restore.sh <it> --live)" >&2; exit 1; fi
say "== live row counts after restore"
for d in $DBS; do ember_table_counts "$d" | sed "s/^/  $d./"; done
say "== done. Start the server and check 'MySQL connected' in server-runtime/logs/latest.log"
