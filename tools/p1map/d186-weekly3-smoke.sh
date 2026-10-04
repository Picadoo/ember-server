#!/usr/bin/env bash
# d186-weekly3-smoke.sh — D186 Weekly Mod Pack 3: force skirmish (Q01) / hexers (Q03) / ballista (Q01) on challenge runs, expect
# the forced-rule + remap log lines. Bots need every first clear (challenge opens on the own Q07 clear). Names = $1 $2 $3.
set -euo pipefail
C=/workspace/minecraft/scripts/console.sh
BOTD=http://127.0.0.1:8765
T=/workspace/minecraft/mineflayer-tests/tmp-p1
LOG=/workspace/minecraft/server-runtime/logs/latest.log
PASS=0; FAIL=0
ok(){ echo "PASS $1"; PASS=$((PASS+1)); }
bad(){ echo "FAIL $1"; FAIL=$((FAIL+1)); }

run_mod() {
  local B=$1 MOD=$2 MAP=${3:-q01}
  echo ""
  echo "======== $B mod=$MOD map=$MAP ========"
  curl -sf "$BOTD/quit?name=$B" >/dev/null 2>&1 || true
  sleep 1
  curl -sf -m 90 "$BOTD/join?name=$B" >/dev/null || { bad join; return; }
  sleep 7
  $C play "list" 0.5 | tr -d '\r' | grep -q "$B" && ok online || { bad online; return; }
  $C play "corerpg stamina set $B 200" 0.4 >/dev/null
  for pre in q01 q02 q03 q04 q05 q06 q07; do
    $C play "corerpg p1 runs firstclear $B $pre" 0.3 >/dev/null || true
  done
  for pre in q02 q03 q04 q05 q06 q07; do
    $C play "corerpg p1 runs unlock $B $pre" 0.25 >/dev/null || true
  done
  $C play "corerpg p1 runs modifier $MOD" 0.5 | tr -d '\r' | tail -1
  local N0=$(wc -l < "$LOG")
  $T/b.sh chat "$B" "/corerpg p1 enter $MAP challenge force" 6000 2>/dev/null | tr -d "\r" | tail -c 300 || true; echo
  sleep 5
  $C play "gamemode creative $B" 0.3 >/dev/null || true
  case "$MAP" in
    q03) $C play "tp $B 0 64 30" 0.3 >/dev/null ;;
    *) $C play "tp $B 0 64 28" 0.3 >/dev/null ;;
  esac
  sleep 2
  # trigger room + a bit of combat
  for k in 1 2 3; do
    $C play "corerpg p1 runs weaken $B" 0.25 >/dev/null || true
    $T/fight.sh "$B" 5000 0 28 1200 >/dev/null 2>&1 || true
  done
  FULL=$(tail -n +$((N0+1)) "$LOG" | tr -d '\r')
  echo "$FULL" | grep -qE "modifier forced $MOD" && ok force-log || bad force-log
  echo "$FULL" | grep -qE "rule $MOD" && ok rule-log || bad rule-log
  echo "$FULL" | grep -E "rule $MOD" | head -2
  echo "$FULL" | grep -qiE 'SEVERE' && bad SEVERE || ok no-SEVERE
  curl -sf "$BOTD/quit?name=$B" >/dev/null || true
  $C play "corerpg p1 runs modifier clear" 0.3 >/dev/null || true
}

run_mod "${1:-FreshQ436}" skirmish q01
run_mod "${2:-FreshQ437}" hexers q03
run_mod "${3:-FreshQ438}" ballista q01
echo "RESULT PASS=$PASS FAIL=$FAIL"
[[ "$FAIL" -eq 0 ]]
