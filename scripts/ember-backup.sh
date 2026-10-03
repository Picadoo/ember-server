#!/usr/bin/env bash
# One verified backup pass of Ember player data (run hourly by scripts/ember-backup-loop.sh; safe to run by hand).
#   1. MySQL ember + authme → gzip dump        $ROOT/hourly/db-<ts>.sql.gz
#      verify: gunzip -t, CREATE TABLE count == live base-table count, cr_p1_item rows in the dump ≈ live COUNT(*)
#   2. vanilla player files → tar.gz           $ROOT/files-hourly/files-<ts>.tar.gz
#      (every world's playerdata/stats/advancements on the play + login servers = inventory, armor, offhand, ender
#       chest; CoreRpg players/ p1-runs/ snapshots/ invsnap-pending.yml; AuthMe data (not its config); DungeonPlus / TrMenu sqlite)
#      verify: tar -tzf lists the archive and it holds ≥ 1 playerdata .dat
#   3. first verified pair of each day → $ROOT/daily/ and $ROOT/files-daily/, plus a second copy outside the repo
#      ($COPY, default /home/box/ember-db-backups/{daily,files-daily})
#   4. retention: 48 hourly, 30 daily (each directory)
# Log: $ROOT/backup.log (one line per step, no secrets). Exit 0 = everything verified.
set -uo pipefail
umask 077   # dumps hold password hashes; tarballs hold player files
M="${EMBER_RUNTIME:-/workspace/minecraft}"
ROOT="${EMBER_BACKUP_ROOT:-/workspace/backup/db-auto}"
COPY="${EMBER_BACKUP_COPY:-/home/box/ember-db-backups}"
KEEP_HOURLY="${EMBER_KEEP_HOURLY:-48}"
KEEP_DAILY="${EMBER_KEEP_DAILY:-30}"
SELF_DIR="$(cd "$(dirname "$0")" && pwd)"
# shellcheck source=lib-ember-db.sh
source "$SELF_DIR/lib-ember-db.sh"

mkdir -p "$ROOT"/{hourly,daily,files-hourly,files-daily} "$COPY"/{daily,files-daily}
LOG="$ROOT/backup.log"
log() { printf '%s %s\n' "$(date '+%Y-%m-%d %H:%M:%S')" "$*" | tee -a "$LOG"; }

exec 9>"$ROOT/.lock"
flock -n 9 || { log "SKIP another backup pass is running"; exit 0; }

TS="$(date +%Y%m%d-%H%M%S)"; DAY="${TS%%-*}"
rc=0

# ---------------------------------------------------------------- 1. database
db_ok=0
DB="$ROOT/hourly/db-$TS.sql.gz"
if ember_db_init; then
  live_tables=$(ember_mysql -N -B -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema IN ('ember','authme') AND table_type='BASE TABLE'" 2>/dev/null || echo "?")
  live_items=$(ember_mysql -N -B -e "SELECT COUNT(*) FROM ember.cr_p1_item" 2>/dev/null || echo "?")
  ember_dump --single-transaction --routines --triggers --databases ember authme 2>>"$LOG" | gzip -6 >"$DB.part"
  st=("${PIPESTATUS[@]}")
  if [ "${st[0]}" = 0 ] && [ "${st[1]}" = 0 ] && gzip -t "$DB.part" 2>>"$LOG"; then
    dump_tables=$(zcat "$DB.part" | grep -c '^CREATE TABLE')
    # rows of cr_p1_item in the dump: MariaDB writes one "(…)," line per row under each INSERT (older mysqldump: one
    # long line with "),(" separators) — handle both
    dump_items=$(zcat "$DB.part" | awk '/^INSERT INTO `cr_p1_item`/{on=1; if ($0 ~ /VALUES \(/) n+=gsub(/\),\(/,"")+1; if ($0 ~ /;$/) on=0; next}
      on && /^\(/{n++} on && /;$/{on=0} END{print n+0}')
    if [[ "$live_items" =~ ^[0-9]+$ ]] && [[ "$dump_items" =~ ^[0-9]+$ ]]; then diff=$(( dump_items - live_items )); else diff=999; fi
    if [ "$dump_tables" = "$live_tables" ] && [ "$dump_tables" -gt 0 ] && [ "${diff#-}" -le 20 ] && [ "${dump_items:-0}" -gt 0 ]; then
      mv "$DB.part" "$DB"; db_ok=1
      log "OK   db    $(basename "$DB") $(du -h "$DB" | cut -f1) tables=$dump_tables/$live_tables cr_p1_item dump=$dump_items live=$live_items"
    else
      mv "$DB.part" "$DB.bad"; rc=1
      log "FAIL db    $(basename "$DB").bad sanity: tables dump=$dump_tables live=$live_tables cr_p1_item dump=$dump_items live=$live_items"
    fi
  else
    mv "$DB.part" "$DB.bad" 2>/dev/null; rc=1
    log "FAIL db    dump exit=${st[0]} gzip=${st[1]} (MySQL down or gzip error) → $(basename "$DB").bad"
  fi
else
  rc=1; log "FAIL db    no credentials"
fi

# ---------------------------------------------------------------- 2. player files
files_ok=0
FT="$ROOT/files-hourly/files-$TS.tar.gz"
LIST="$(mktemp)"
(
  cd "$M" || exit 1
  for rt in server-runtime login-runtime; do
    for w in "$rt"/*/; do
      for sub in playerdata stats advancements; do [ -d "$w$sub" ] && echo "$w$sub"; done
    done
  done
  for p in plugins/CoreRpg/players plugins/CoreRpg/p1-runs plugins/CoreRpg/snapshots plugins/CoreRpg/invsnap-pending.yml \
           plugins/DungeonPlus/data.db plugins/TrMenu/data.db plugins/TrMenu/data.db-wal plugins/TrMenu/global.db plugins/TrMenu/global.db-wal \
           login-runtime/plugins/AuthMe/authme.db login-runtime/plugins/AuthMe/playerdata; do
    [ -e "$p" ] && echo "$p"
  done
) >"$LIST"
if [ -s "$LIST" ]; then
  tar -C "$M" --warning=no-file-changed --exclude='*/config.yml' -czf "$FT.part" -T "$LIST" 2>>"$LOG"; trc=$?
  # tar exit 1 = "some files changed while being read" (live server): fine if the archive verifies
  if [ "$trc" -le 1 ] && entries=$(tar -tzf "$FT.part" 2>>"$LOG" | wc -l) && [ "$entries" -gt 0 ]; then
    dats=$(tar -tzf "$FT.part" | grep -c 'playerdata/.*\.dat$')
    if [ "$dats" -gt 0 ]; then
      mv "$FT.part" "$FT"; files_ok=1
      log "OK   files $(basename "$FT") $(du -h "$FT" | cut -f1) entries=$entries playerdata_dat=$dats"
    else
      mv "$FT.part" "$FT.bad"; rc=1; log "FAIL files no playerdata .dat in archive"
    fi
  else
    mv "$FT.part" "$FT.bad" 2>/dev/null; rc=1; log "FAIL files tar exit=$trc"
  fi
else
  rc=1; log "FAIL files nothing to archive under $M"
fi
rm -f "$LIST"

# ---------------------------------------------------------------- 3. dailies (+ copy outside the repo)
if [ "$db_ok" = 1 ] && [ ! -e "$ROOT/daily/db-$DAY.sql.gz" ]; then
  cp -p "$DB" "$ROOT/daily/db-$DAY.sql.gz" && cp -p "$DB" "$COPY/daily/db-$DAY.sql.gz" \
    && gzip -t "$COPY/daily/db-$DAY.sql.gz" && log "OK   daily db-$DAY.sql.gz (+ copy in $COPY/daily)" || { rc=1; log "FAIL daily db copy"; }
fi
if [ "$files_ok" = 1 ] && [ ! -e "$ROOT/files-daily/files-$DAY.tar.gz" ]; then
  cp -p "$FT" "$ROOT/files-daily/files-$DAY.tar.gz" && cp -p "$FT" "$COPY/files-daily/files-$DAY.tar.gz" \
    && tar -tzf "$COPY/files-daily/files-$DAY.tar.gz" >/dev/null && log "OK   daily files-$DAY.tar.gz (+ copy in $COPY/files-daily)" || { rc=1; log "FAIL daily files copy"; }
fi

# ---------------------------------------------------------------- 4. retention
prune() { # dir keep pattern
  local n; n=$(find "$1" -maxdepth 1 -type f -name "$3" | wc -l)
  [ "$n" -le "$2" ] && return 0
  find "$1" -maxdepth 1 -type f -name "$3" -printf '%f\n' | sort | head -n "$((n - $2))" | while read -r f; do rm -f -- "$1/$f"; done
}
prune "$ROOT/hourly" "$KEEP_HOURLY" 'db-*.sql.gz'
prune "$ROOT/files-hourly" "$KEEP_HOURLY" 'files-*.tar.gz'
prune "$ROOT/daily" "$KEEP_DAILY" 'db-*.sql.gz'
prune "$ROOT/files-daily" "$KEEP_DAILY" 'files-*.tar.gz'
prune "$COPY/daily" "$KEEP_DAILY" 'db-*.sql.gz'
prune "$COPY/files-daily" "$KEEP_DAILY" 'files-*.tar.gz'
find "$ROOT/hourly" "$ROOT/files-hourly" -maxdepth 1 -name '*.bad' -mtime +2 -delete 2>/dev/null

[ "$rc" = 0 ] && date +%s >"$ROOT/last-ok"
log "END  pass $TS rc=$rc"
exit "$rc"
