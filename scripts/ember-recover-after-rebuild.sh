#!/usr/bin/env bash
# Bring the Ember stack back after the box was rebuilt (system disk reset, /workspace kept). Idempotent: every step
# checks first and does nothing when it is already in place, so it is safe to run on a healthy box.
#
#   scripts/ember-recover-after-rebuild.sh            full run: MariaDB (install/config/start/restore) → stack
#   scripts/ember-recover-after-rebuild.sh --db-only  stop after MariaDB (do not start login/play/proxy)
#   scripts/ember-recover-after-rebuild.sh --check    print what would be done, change nothing
#
# Steps
#   1. mariadb-server missing                → sudo apt-get install mariadb-server mariadb-client
#   2. conf.d override                       → /etc/mysql/mariadb.conf.d/99-ember-datadir.cnf: datadir=$EMBER_MYSQL_DATADIR
#   3. datadir                               → chown mysql:mysql (the mysql uid can change between rebuilds);
#                                              empty/missing → mariadb-install-db
#   4. MariaDB not answering                 → mysqld_safe --user=mysql in the background (setsid nohup)
#   5. app user `ember`@127.0.0.1/localhost  → CREATE USER IF NOT EXISTS with the password from plugins/CoreRpg/config.yml
#                                              (an existing user is never altered; password never printed / never in argv)
#   6. ember or authme DB missing / empty    → scripts/db-restore.sh latest --live --yes (newest verified hourly dump)
#   7. login / play / proxy not all running  → scripts/ember-up.sh (setsid nohup; start.sh skips what already runs)
# No auto-start hook is installed; run this by hand after a rebuild. Needs passwordless sudo.
set -uo pipefail
M="${EMBER_RUNTIME:-/workspace/minecraft}"
DATADIR="${EMBER_MYSQL_DATADIR:-/workspace/mysql-data}"
CNF=/etc/mysql/mariadb.conf.d/99-ember-datadir.cnf
CHECK=0; DBONLY=0
for a in "$@"; do
  case "$a" in
    --check) CHECK=1 ;; --db-only) DBONLY=1 ;;
    -h|--help) sed -n '2,21p' "$0"; exit 0 ;;
    *) echo "unknown option $a" >&2; exit 2 ;;
  esac
done
say()  { printf '[recover] %s\n' "$*"; }
act()  { if [ "$CHECK" = 1 ]; then say "WOULD: $*"; return 1; fi; say "DO: $*"; return 0; }
die()  { say "FAIL: $*"; exit 1; }
sudo -n true 2>/dev/null || die "passwordless sudo is required"
db_up() { sudo -n mariadb -uroot -N -B -e 'SELECT 1' >/dev/null 2>&1; }
rootsql() { sudo -n mariadb -uroot -N -B "$@"; }

# 1. package
if [ -x /usr/sbin/mariadbd ]; then say "ok: mariadb-server installed"
elif act "apt-get install mariadb-server mariadb-client"; then
  sudo -n apt-get update -qq || die "apt-get update"
  sudo -n env DEBIAN_FRONTEND=noninteractive apt-get install -y -qq mariadb-server mariadb-client || die "apt-get install"
fi

# 2. datadir override
WANT=$(printf '# Ember: keep MariaDB data on the persistent /workspace volume (box rebuilds wipe /var/lib/mysql).\n# Written by scripts/ember-recover-after-rebuild.sh - safe to rewrite.\n[mysqld]\ndatadir = %s\n' "$DATADIR")
if [ -f "$CNF" ] && [ "$(cat "$CNF")" = "$WANT" ]; then say "ok: $CNF -> $DATADIR"
elif act "write $CNF (datadir = $DATADIR)"; then
  if db_up && [ "$(rootsql -e 'SELECT @@datadir')" != "$DATADIR/" ]; then
    die "MariaDB is running on another datadir; stop it (sudo mariadb-admin -uroot shutdown) and copy the data first"
  fi
  printf '%s\n' "$WANT" | sudo -n tee "$CNF" >/dev/null
fi

# 3. datadir ownership / initialisation
if sudo -n test -d "$DATADIR/mysql"; then
  if [ "$(sudo -n stat -c %U "$DATADIR")" = mysql ] && [ -z "$(sudo -n find "$DATADIR" ! -user mysql -print -quit)" ]; then
    say "ok: $DATADIR present, owned by mysql"
  elif act "chown -R mysql:mysql $DATADIR"; then sudo -n chown -R mysql:mysql "$DATADIR"; fi
elif act "initialise empty datadir $DATADIR (mariadb-install-db)"; then
  sudo -n mkdir -p "$DATADIR" && sudo -n chown mysql:mysql "$DATADIR" && sudo -n chmod 750 "$DATADIR"
  sudo -n mariadb-install-db --user=mysql --datadir="$DATADIR" >/tmp/ember-mariadb-install-db.log 2>&1 || die "mariadb-install-db (see /tmp/ember-mariadb-install-db.log)"
fi

# 4. server
if db_up; then say "ok: MariaDB answering (datadir $(rootsql -e 'SELECT @@datadir'))"
elif act "start mysqld_safe --user=mysql"; then
  sudo -n mkdir -p /run/mysqld && sudo -n chown mysql:mysql /run/mysqld
  (sudo -n setsid nohup mysqld_safe --user=mysql >/tmp/mysqld_safe.log 2>&1 </dev/null &)
  for _ in $(seq 1 60); do db_up && break; sleep 1; done
  db_up || die "MariaDB did not come up (see /tmp/mysqld_safe.log, $DATADIR/*.err)"
  [ "$(rootsql -e 'SELECT @@datadir')" = "$DATADIR/" ] || die "MariaDB started on $(rootsql -e 'SELECT @@datadir'), expected $DATADIR/"
  say "ok: MariaDB up on $DATADIR"
fi

if [ "$CHECK" = 1 ] && ! db_up; then say "check: MariaDB down - DB/user/restore checks skipped"; else
  # 5. app user (password from the CoreRpg config the servers actually use; never printed)
  CFG="$M/plugins/CoreRpg/config.yml"
  get() { awk -v k="$1" '/^mysql:/{m=1;next} m&&/^[^ ]/{m=0} m&&$1==k":"{sub(/^[^:]*:[ ]*/,"");gsub(/^["'\'']|["'\'']$/,"");print;exit}' "$CFG"; }
  U=$(get username); U=${U:-ember}
  NEED=$(rootsql -e "SELECT 2-COUNT(*) FROM mysql.user WHERE user='$U' AND host IN ('127.0.0.1','localhost')")
  if [ "$NEED" = 0 ]; then say "ok: user $U@127.0.0.1/localhost exists (left unchanged)"
  elif act "create user $U@127.0.0.1/localhost + grants on ember/authme"; then
    P=$(get password); [ -n "$P" ] || die "no mysql.password in $CFG"
    P=${P//\\/\\\\}; P=${P//\'/\\\'}
    rootsql <<SQL || die "create user"
CREATE DATABASE IF NOT EXISTS ember CHARACTER SET utf8mb4; CREATE DATABASE IF NOT EXISTS authme CHARACTER SET utf8mb4;
CREATE USER IF NOT EXISTS '$U'@'127.0.0.1' IDENTIFIED BY '$P'; CREATE USER IF NOT EXISTS '$U'@'localhost' IDENTIFIED BY '$P';
GRANT ALL ON ember.* TO '$U'@'127.0.0.1'; GRANT ALL ON authme.* TO '$U'@'127.0.0.1';
GRANT ALL ON ember.* TO '$U'@'localhost'; GRANT ALL ON authme.* TO '$U'@'localhost'; FLUSH PRIVILEGES;
SQL
    unset P
  fi

  # 6. data
  T_EMBER=$(rootsql -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='ember' AND table_name='cr_players'")
  T_AUTH=$(rootsql -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='authme' AND table_name='authme'")
  if [ "$T_EMBER" = 1 ] && [ "$T_AUTH" = 1 ]; then
    say "ok: data present (ember.cr_players=$(rootsql -e 'SELECT COUNT(*) FROM ember.cr_players') authme.authme=$(rootsql -e 'SELECT COUNT(*) FROM authme.authme'))"
  else
    LATEST=$(ls -1 /workspace/backup/db-auto/hourly/db-*.sql.gz 2>/dev/null | sort | tail -n 1)
    [ -n "$LATEST" ] || die "ember/authme missing and no hourly dump in /workspace/backup/db-auto/hourly"
    if act "restore $LATEST into live ember+authme (scripts/db-restore.sh latest --live --yes)"; then
      [ -f "$M/server-runtime/server.pid" ] && kill -0 "$(cat "$M/server-runtime/server.pid")" 2>/dev/null \
        && die "play server is running on an empty DB; stop it (scripts/ember-down.sh) and re-run"
      "$M/scripts/db-restore.sh" latest --live --yes || die "db-restore failed"
    fi
  fi
fi

# 7. stack
[ "$DBONLY" = 1 ] && { say "done (--db-only)"; exit 0; }
alive() { [ -f "$1" ] && kill -0 "$(cat "$1")" 2>/dev/null; }
if alive "$M/login-runtime/server.pid" && alive "$M/server-runtime/server.pid" && alive "$M/proxy-runtime/proxy.pid"; then
  say "ok: login/play/proxy already running"
elif act "start stack (scripts/ember-up.sh via setsid nohup; log /tmp/ember-up.log)"; then
  (cd "$M" && setsid nohup scripts/ember-up.sh >/tmp/ember-up.log 2>&1 </dev/null &)
  for _ in $(seq 1 90); do
    ss -ltn 2>/dev/null | grep -q ':25567 ' && ss -ltn | grep -q ':25566 ' && ss -ltn | grep -q ':25565 ' && break; sleep 2
  done
  for p in 25565 25566 25567 3306; do ss -ltn | grep -q ":$p " && say "port $p listening" || say "WARN: port $p not listening yet"; done
  say "check: grep -E 'Done \\(|MySQL connected' $M/server-runtime/logs/latest.log ; grep 'MySQL setup finished' $M/login-runtime/logs/latest.log"
fi
say "done"
