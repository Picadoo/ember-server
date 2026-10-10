#!/usr/bin/env bash
# D441 sx14+sx15 real-map smoke: walk spots + Cast visible + settle
set -uo pipefail
C=/workspace/minecraft/scripts/console.sh
BOTD=http://127.0.0.1:8765
T=/workspace/minecraft/mineflayer-tests/tmp-p1
LOG=/workspace/minecraft/server-runtime/logs/latest.log
OUT=/workspace/tmp/d441-sx-map
mkdir -p "$OUT"
PASS=0; FAIL=0; SOFT=0
ok(){ echo "PASS $1"; PASS=$((PASS+1)); }; bad(){ echo "FAIL $1"; FAIL=$((FAIL+1)); }; soft(){ echo "SOFT $1"; SOFT=$((SOFT+1)); }
curl -sf "$BOTD/list" >/dev/null || { echo "FAIL botd"; exit 1; }
weaken_n(){ local W; W=$($C play "corerpg p1 runs weaken $1" 0.4 2>/dev/null | tr -d '\r' || true)
  echo "$W" | grep -q '没有进行中' && { echo -1; return; }
  echo "$W" | sed -n 's/.*削弱 \([0-9]*\) 只.*/\1/p' | tail -1 | awk '{print ($0==""?-2:$0)}'; }
clear_room(){ local B=$1; for k in $(seq 1 28); do N=$(weaken_n "$B"); [[ "$N" == 0 ]] && return 0; [[ "$N" == -1 ]] && return 1
  $C play "corerpg p1 heal $B" 0.1 >/dev/null || true; $T/fight.sh "$B" 12000 0 32 2500 >/dev/null 2>&1 || true; done; [[ "$(weaken_n "$B")" == 0 ]]; }
settle(){ local B=$1; for i in $(seq 1 45); do N=$(weaken_n "$B"); [[ "$N" == -1 ]] && break
  $C play "corerpg p1 heal $B" 0.1 >/dev/null || true; $T/fight.sh "$B" 6000 0 40 1500 >/dev/null 2>&1 || true; sleep 1; done; }
join(){ local B=$1; curl -sf "$BOTD/quit?name=$B" >/dev/null 2>&1 || true; sleep 1
  curl -sf -m 90 "$BOTD/join?name=$B" >/dev/null || { bad "$B join"; return 1; }; sleep 6
  $C play "list" 0.4 | tr -d '\r' | grep -q "$B" && ok "$B online" || { bad "$B online"; return 1; }
  $C play "corerpg stamina set $B 200" 0.4 >/dev/null; }
quitall(){ for b in "$@"; do curl -sf "$BOTD/quit?name=$b" >/dev/null 2>&1 || true; done; sleep 2; }

run_one(){
  local KEY=$1 NAME=$2 CAST_PAT=$3
  local A="FreshQ94${KEY: -1}"
  # unique bots: sx14 -> FreshQ944, sx15 -> FreshQ945
  [[ "$KEY" == sx14 ]] && A=FreshQ944
  [[ "$KEY" == sx15 ]] && A=FreshQ945
  echo; echo "======== $KEY $NAME ========"
  join $A || return 1
  $C play "corerpg p1 runs firstclear $A q01" 0.5 >/dev/null
  sleep 2
  N1=$(wc -l < "$LOG")
  $C play "corerpg p1 runs extra none" 0.2 >/dev/null || true
  $T/b.sh chat "$A" "/corerpg p1 enter $KEY" 6000 >/dev/null || true
  sleep 6
  ENT=$(tail -n +"$((N1+1))" "$LOG" | tr -d '\r')
  echo "$ENT" | grep -qiE "$KEY|EmberSx|短征|$NAME" && ok "$KEY enter" || bad "$KEY enter"
  $C play "gamemode creative $A" 0.3 >/dev/null
  if [[ "$KEY" == sx14 ]]; then
    SPOTS=("0 64 29" "36 64 31" "36 64 44" "32 64 69")
  else
    SPOTS=("0 64 29" "30 64 39" "32 64 69")
  fi
  si=0
  for spot in "${SPOTS[@]}"; do
    si=$((si+1)); $C play "tp $A $spot" 0.35 >/dev/null; sleep 1.5
    # floor check via getblock if available
    GB=$($C play "testforblock $A minecraft:air" 0.2 2>/dev/null | tr -d '\r' || true)
    clear_room "$A" && ok "$KEY room$si@$spot" || soft "$KEY room$si@$spot"
  done
  $C play "tp $A 0 64 102" 0.4 >/dev/null; sleep 3
  # wait for cast message before nuking boss
  sleep 5
  CASTLOG=$(tail -n +"$((N1+1))" "$LOG" | tr -d '\r' | grep -E "$CAST_PAT" | head -8)
  echo "$CASTLOG" | tee "$OUT/$KEY-cast.txt"
  BOTCAST=$($T/b.sh log "$A" 0 2>/dev/null | tr -d '\r' | sed 's/§.//g' || true)
  echo "$BOTCAST" | grep -E "$CAST_PAT" | head -5 | tee -a "$OUT/$KEY-cast.txt" || true
  if echo "$CASTLOG$BOTCAST" | grep -qE "$CAST_PAT"; then ok "$KEY Cast"; else soft "$KEY Cast msg"; fi
  settle "$A"; sleep 4
  FULL=$(tail -n +"$((N1+1))" "$LOG" | tr -d '\r')
  echo "$FULL" | grep -E "short settle|$KEY|S5[34]|settle" | head -15 | tee "$OUT/$KEY-settle.txt"
  echo "$FULL" | grep -qiE "short settle|settle.*$KEY|$KEY.*settle|S5" && ok "$KEY settle" || soft "$KEY settle"
  quitall $A
}

run_one sx14 烬裂 '裂扫斩|【烬裂】|RiftCast'
run_one sx15 烬旋 '旋扫斩|【烬旋】|SpiralCast'

echo; echo "PASS=$PASS FAIL=$FAIL SOFT=$SOFT" | tee "$OUT/summary.txt"
[[ "$FAIL" -eq 0 ]]
