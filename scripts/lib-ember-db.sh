#!/usr/bin/env bash
# Shared MySQL access for the Ember backup / restore scripts (sourced, not run).
# Auth: root over the unix socket via `sudo -n` when available, otherwise the CoreRpg config credentials written to a
# 0600 temp defaults-file. The password is never printed, logged or put on a command line.
#   ember_db_init        → sets EMBER_DB_MODE (sudo|cfg) and, for cfg, EMBER_DB_DEFAULTS (temp file, removed on exit)
#   ember_mysql  [args]  → mysql client with the chosen auth (use -N -B for scripts)
#   ember_dump   [args]  → mysqldump with the chosen auth
EMBER_RUNTIME="${EMBER_RUNTIME:-/workspace/minecraft}"
EMBER_DB_MODE=""
EMBER_DB_DEFAULTS=""

ember_db_init() {
  [ -n "$EMBER_DB_MODE" ] && return 0
  if [ -z "${DB_DUMP_NO_SUDO:-}" ] && sudo -n true 2>/dev/null && sudo -n mysql -uroot -e 'SELECT 1' >/dev/null 2>&1; then
    EMBER_DB_MODE=sudo
    return 0
  fi
  local cfg="$EMBER_RUNTIME/plugins/CoreRpg/config.yml"
  [ -r "$cfg" ] || { echo "no DB credentials: sudo root unavailable and $cfg unreadable" >&2; return 1; }
  _ember_get() { awk -v k="$1" '/^mysql:/{m=1;next} m&&/^[^ ]/{m=0} m&&$1==k":"{sub(/^[^:]*:[ ]*/,"");gsub(/^["'\'']|["'\'']$/,"");print;exit}' "$cfg"; }
  EMBER_DB_DEFAULTS="$(mktemp)"; chmod 600 "$EMBER_DB_DEFAULTS"
  trap 'rm -f "$EMBER_DB_DEFAULTS"' EXIT
  printf '[client]\nhost=%s\nport=%s\nuser=%s\npassword="%s"\n' "$(_ember_get host)" "$(_ember_get port)" \
    "$(_ember_get username)" "$(_ember_get password)" >"$EMBER_DB_DEFAULTS"
  EMBER_DB_MODE=cfg
}

ember_mysql() {
  ember_db_init || return 1
  if [ "$EMBER_DB_MODE" = sudo ]; then sudo -n mysql -uroot "$@"; else mysql --defaults-extra-file="$EMBER_DB_DEFAULTS" "$@"; fi
}

ember_dump() {
  ember_db_init || return 1
  if [ "$EMBER_DB_MODE" = sudo ]; then sudo -n mysqldump -uroot "$@"; else mysqldump --defaults-extra-file="$EMBER_DB_DEFAULTS" "$@"; fi
}

# "table rows" for every base table of a schema, exact COUNT(*) (information_schema.table_rows is an estimate).
ember_table_counts() {
  local db="$1" t
  for t in $(ember_mysql -N -B -e "SELECT table_name FROM information_schema.tables WHERE table_schema='$db' AND table_type='BASE TABLE' ORDER BY table_name"); do
    printf '%s %s\n' "$t" "$(ember_mysql -N -B -e "SELECT COUNT(*) FROM \`$db\`.\`$t\`")"
  done
}
