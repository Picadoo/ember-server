#!/usr/bin/env bash
set -uo pipefail
C=/workspace/minecraft/scripts/console.sh
BOTD=http://127.0.0.1:8765
T=/workspace/minecraft/mineflayer-tests/tmp-p1
LOG=/workspace/minecraft/server-runtime/logs/latest.log
OUT=/workspace/tmp/d446-sx-map
mkdir -p "$OUT"
PASS=0;FAIL=0;SOFT=0
ok(){ echo PASS "$1"; PASS=$((PASS+1));}; bad(){ echo FAIL "$1"; FAIL=$((FAIL+1));}; soft(){ echo SOFT "$1"; SOFT=$((SOFT+1));}
weaken_n(){ local W; W=$($C play "corerpg p1 runs weaken $1" 0.3 2>/dev/null | tr -d '\r' || true)
  echo "$W" | grep -q '没有进行中' && { echo -1; return; }
  echo "$W" | sed -n 's/.*削弱 \([0-9]*\) 只.*/\1/p' | tail -1 | awk '{print ($0==""?-2:$0)}'; }
clear_room(){ local B=$1; for k in $(seq 1 24); do N=$(weaken_n "$B"); [[ "$N" == 0 ]] && return 0; [[ "$N" == -1 ]] && return 1
  $C play "corerpg p1 heal $B" 0.05 >/dev/null || true; $T/fight.sh "$B" 10000 0 32 1500 >/dev/null 2>&1 || true; done
  [[ "$(weaken_n "$B")" == 0 ]]; }
settle(){ local B=$1; for i in $(seq 1 45); do N=$(weaken_n "$B"); [[ "$N" == -1 ]] && break
  $C play "corerpg p1 heal $B" 0.05 >/dev/null || true; $T/fight.sh "$B" 7000 0 40 1500 >/dev/null 2>&1 || true; sleep 0.6; done; }
join(){ local B=$1; curl -sf "$BOTD/quit?name=$B" >/dev/null 2>&1 || true; sleep 2
  curl -sf -m 90 "$BOTD/join?name=$B" >/dev/null || { bad "$B join"; return 1; }; sleep 7
  $C play "list" 0.3 | tr -d '\r' | grep -q "$B" && ok "$B online" || { bad "$B online"; return 1; }
  $C play "corerpg stamina set $B 200" 0.3 >/dev/null; }
smoke(){
  local KEY=$1 A=$2 CAST=$3; shift 3; local SPOTS=("$@")
  echo; echo "======== $KEY ========"
  join "$A" || return 1
  $C play "corerpg p1 runs firstclear $A q01" 0.4 >/dev/null; sleep 2
  N1=$(wc -l < "$LOG")
  $C play "corerpg p1 runs extra none" 0.2 >/dev/null || true
  $T/b.sh chat "$A" "/corerpg p1 enter $KEY" 8000 >/dev/null || true
  sleep 8
  ENT=$(tail -n +"$((N1+1))" "$LOG" | tr -d '\r')
  echo "$ENT" | grep -q "bound to dungeon_Ember" && ok "$KEY bound" || soft "$KEY bound"
  echo "$ENT" | grep -q '未进入实例' && bad "$KEY early-release" || ok "$KEY stayed"
  $C play "gamemode creative $A" 0.3 >/dev/null
  si=0
  for spot in "${SPOTS[@]}"; do
    si=$((si+1)); $C play "tp $A $spot" 0.35 >/dev/null; sleep 2
    clear_room "$A" && ok "$KEY room$si" || soft "$KEY room$si"
  done
  $C play "tp $A 0 64 102" 0.4 >/dev/null; sleep 10
  CASTLOG=$(tail -n +"$((N1+1))" "$LOG" | tr -d '\r')
  BOT=$($T/b.sh log "$A" 0 2>/dev/null | tr -d '\r' | sed 's/§.//g' || true)
  echo "$CASTLOG$BOT" | grep -E "$CAST" | head -5 | tee "$OUT/$KEY-cast.txt"
  echo "$CASTLOG$BOT" | grep -qE "$CAST" && ok "$KEY Cast" || soft "$KEY Cast"
  settle "$A"; sleep 2
  FULL=$(tail -n +"$((N1+1))" "$LOG" | tr -d '\r')
  echo "$FULL" | grep -E "short settle|r2 |r3 " | head -12 | tee "$OUT/$KEY-settle.txt"
  echo "$FULL" | grep -qi 'short settle' && ok "$KEY settle" || soft "$KEY settle"
  curl -sf "$BOTD/quit?name=$A" >/dev/null 2>&1 || true; sleep 3
}
smoke sx10 FreshQ966 '闸心压浪|【密封闸】' "0 64 29" "30 64 30" "30 64 41" "30 64 52" "32 64 69"
smoke sx11 FreshQ967 '镜面扫线|【合镜】' "0 64 29" "20 64 36" "36 64 46" "30 64 50" "32 64 69"
smoke sx12 FreshQ968 '枢扫斩|【环枢】' "0 64 29" "18 64 32" "40 64 32" "30 64 40" "32 64 69"
smoke sx13 FreshQ969 '衡扫斩|【衡梁】' "0 64 29" "29 64 34" "29 64 40" "29 64 46" "32 64 69"
echo "PASS=$PASS FAIL=$FAIL SOFT=$SOFT" | tee "$OUT/summary.txt"
[[ "$FAIL" -eq 0 ]]
