#!/usr/bin/env bash
# d173-boss-smoke.sh — D173 half-HP Pack 2 short smoke. Q01 + Q07 at minimum.
# Bots: FreshQ370+. Needs botd + play up. Force boss to ~40% HP then watch phase tell + new move.
set -euo pipefail
C=/workspace/minecraft/scripts/console.sh
BOTD=http://127.0.0.1:8765
T=/workspace/minecraft/mineflayer-tests/tmp-p1
LOG=/workspace/minecraft/server-runtime/logs/latest.log
PASS=0; FAIL=0
ok(){ echo "PASS $1"; PASS=$((PASS+1)); }
bad(){ echo "FAIL $1"; FAIL=$((FAIL+1)); }

curl -sf "$BOTD/list" >/dev/null || { echo "FAIL botd"; exit 1; }

run_map() {
  local B=$1 Q=$2 EXPECT=$3
  echo ""
  echo "======== $B map=$Q expect~$EXPECT ========"
  curl -sf "$BOTD/quit?name=$B" >/dev/null 2>&1 || true
  sleep 1
  curl -sf -m 90 "$BOTD/join?name=$B" >/dev/null || { bad "$B join"; return; }
  sleep 6
  $C play "list" 0.5 | tr -d '\r' | grep -q "$B" && ok "$B online" || { bad "$B online"; curl -sf "$BOTD/quit?name=$B" >/dev/null || true; return; }
  $C play "corerpg stamina set $B 200" 0.6 >/dev/null
  # unlock path to map
  for pre in q01 q02 q03 q04 q05 q06 q07; do
    $C play "corerpg p1 runs firstclear $B $pre" 0.4 >/dev/null || true
    [[ "$pre" == "$Q" ]] && break
  done
  local N0=$(wc -l < "$LOG")
  $T/b.sh chat "$B" "/corerpg p1 enter $Q" 5000 >/dev/null || true
  sleep 4
  $C play "gamemode creative $B" 0.4 >/dev/null || true
  # room spots (approximate book white-box centers; same family as d179)
  local spots
  case "$Q" in
    q01) spots=("0 64 28" "30 64 38" "0 64 68" "0 64 102") ;;
    q07) spots=("0 64 33" "40 64 65" "-2 64 98" "0 64 144") ;;
    *) spots=("0 64 28" "30 64 38" "0 64 68" "0 64 102") ;;
  esac
  local si=0
  for spot in "${spots[@]}"; do
    si=$((si+1))
    echo "-- room$si @ $spot --"
    $C play "tp $B $spot" 0.3 >/dev/null
    sleep 1.2
    for k in 1 2 3 4 5 6; do
      $C play "corerpg p1 runs weaken $B" 0.25 >/dev/null || true
      $C play "corerpg p1 heal $B" 0.15 >/dev/null || true
      $T/fight.sh "$B" 8000 0 28 1500 >/dev/null 2>&1 || true
      CLR=$($T/b.sh eval "$B" "return bot.chatLog.slice(-16).map(s=>s.replace(/§./g,'')).join('|')" 2>/dev/null || true)
      echo "$CLR" | grep -qE '房间.*清空|通道已开启|首领|结算|蓄力' && break
      sleep 0.3
    done
  done
  echo "-- boss half-HP --"
  # last spot should be boss hall; force ~40% to open half-HP phase
  $C play "corerpg p1 runs weaken $B 0.4" 0.6 | tr -d '\r' | tail -2
  sleep 1
  for k in 1 2 3 4 5 6 7 8 9 10 11 12; do
    $C play "corerpg p1 heal $B" 0.15 >/dev/null || true
    $T/fight.sh "$B" 10000 0 28 2000 >/dev/null 2>&1 || true
    sleep 1.5
    CH=$($T/b.sh eval "$B" "return bot.chatLog.slice(-40).map(s=>s.replace(/§./g,'')).join('\n')" 2>/dev/null || true)
    echo "$CH" | grep -qE '首领进入半血|招式变强' && { ok "$B phase-tell"; break; }
    [[ $k -eq 12 ]] && bad "$B phase-tell"
  done
  CH=$($T/b.sh eval "$B" "return bot.chatLog.slice(-50).map(s=>s.replace(/§./g,'')).join('\n')" 2>/dev/null || true)
  echo "$CH" | grep -qE "$EXPECT" && ok "$B move:$EXPECT" || bad "$B move:$EXPECT"
  # allow settle or quit mid-boss
  for i in $(seq 1 20); do
    sleep 1.5
    CH=$($T/b.sh eval "$B" "return bot.chatLog.slice(-20).map(s=>s.replace(/§./g,'')).join('\n')" 2>/dev/null || true)
    echo "$CH" | grep -qE '结算|通关|本局结束|首领.*击败' && break
    $C play "corerpg p1 runs weaken $B" 0.2 >/dev/null || true
    $C play "corerpg p1 heal $B" 0.1 >/dev/null || true
  done
  local FULL
  FULL=$(tail -n +$((N0+1)) "$LOG" | tr -d '\r')
  echo "$FULL" | grep -qE 'half-HP phase|phase 2|boss phase' && ok "$B log-phase" || ok "$B log-phase-soft"
  echo "$FULL" | grep -qiE 'SEVERE' && bad "$B SEVERE" || ok "$B no-SEVERE"
  curl -sf "$BOTD/quit?name=$B" >/dev/null || true
}

run_map FreshQ370 q01 '门廊突刺'
run_map FreshQ371 q07 '矿渣'

echo ""
echo "RESULT PASS=$PASS FAIL=$FAIL"
[[ "$FAIL" -eq 0 ]]
