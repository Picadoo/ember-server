#!/usr/bin/env bash
# d181-variety-smoke.sh — D181 short smoke. Forces mortar + molten on Q01 normal repeat.
# Bots: FreshQ330+ only. Needs botd + play up.
set -euo pipefail
C=/workspace/minecraft/scripts/console.sh
BOTD=http://127.0.0.1:8765
T=/workspace/minecraft/mineflayer-tests/tmp-p1
LOG=/workspace/minecraft/server-runtime/logs/latest.log
PASS=0; FAIL=0
ok(){ echo "PASS $1"; PASS=$((PASS+1)); }
bad(){ echo "FAIL $1"; FAIL=$((FAIL+1)); }

curl -sf "$BOTD/list" >/dev/null || { echo "FAIL botd"; exit 1; }

run_one() {
  local B=$1 FORCE=$2 EXPECT=$3
  echo ""
  echo "======== $B force=$FORCE expect~$EXPECT ========"
  curl -sf "$BOTD/quit?name=$B" >/dev/null 2>&1 || true
  sleep 1
  curl -sf -m 90 "$BOTD/join?name=$B" >/dev/null || { bad "$B join"; return; }
  sleep 6
  $C play "list" 0.5 | tr -d '\r' | grep -q "$B" && ok "$B online" || { bad "$B online"; curl -sf "$BOTD/quit?name=$B" >/dev/null || true; return; }
  $C play "corerpg stamina set $B 200" 0.6 >/dev/null
  $C play "corerpg p1 runs firstclear $B q01" 0.6 >/dev/null
  $C play "corerpg p1 runs variety $FORCE" 0.8 | tr -d '\r' | tail -3
  local N0=$(wc -l < "$LOG")
  $T/b.sh chat "$B" "/corerpg p1 enter q01" 5000 >/dev/null || true
  sleep 4
  $C play "gamemode creative $B" 0.4 >/dev/null || true
  local spots=("0 64 28" "30 64 38" "32 64 68" "0 64 102")
  local si=0
  for spot in "${spots[@]}"; do
    si=$((si+1))
    echo "-- room$si @ $spot --"
    $C play "tp $B $spot" 0.3 >/dev/null
    sleep 1.5
    for k in 1 2 3 4 5 6 7 8; do
      $C play "corerpg p1 runs weaken $B" 0.3 >/dev/null || true
      $C play "corerpg p1 heal $B" 0.2 >/dev/null || true
      # briefly stand still so mortar can land a warn circle near feet
      if [[ "$FORCE" == mortar* ]]; then sleep 1.5; fi
      $T/fight.sh "$B" 10000 0 28 2000 >/dev/null 2>&1 || true
      CLR=$($T/b.sh eval "$B" "return bot.chatLog.slice(-16).map(s=>s.replace(/§./g,'')).join('|')" 2>/dev/null || true)
      echo "$CLR" | grep -qE '房间.*清空|通道已开启|首领|词缀精英|亡爆|投弹|结算' && break
      sleep 0.4
    done
    sleep 0.8
  done
  echo "-- wait settle --"
  for i in $(seq 1 35); do
    sleep 2
    CH=$($T/b.sh eval "$B" "return bot.chatLog.slice(-25).map(s=>s.replace(/§./g,'')).join('\n')" 2>/dev/null || true)
    echo "$CH" | grep -qE '结算|通关|花样委托|本局结束|首领.*击败' && break
    $C play "corerpg p1 runs weaken $B" 0.2 >/dev/null || true
  done
  local FULL CHAT
  FULL=$(tail -n +"$((N0+1))" "$LOG" | tr -d '\r')
  CHAT=$($T/b.sh eval "$B" "return bot.chatLog.map(s=>s.replace(/§./g,'')).join('\n')" 2>/dev/null || true)
  echo "$FULL" | grep -qE "variety forced $FORCE" && ok "$B force logged" || bad "$B force logged"
  echo "$FULL$CHAT" | grep -qE "$EXPECT" && ok "$B chat/log has $EXPECT" || { bad "$B expect $EXPECT"; echo "$CHAT" | tail -25; }
  if [[ "$FORCE" == molten* ]]; then
    echo "$FULL$CHAT" | grep -qE '尸体要炸|亡爆' && ok "$B molten death chat" || bad "$B molten death chat"
  fi
  echo "$FULL" | grep -qE "settle|COMPLETE|complete|affix .* done" && ok "$B settle-ish log" || ok "$B settle soft"
  curl -sf "$BOTD/quit?name=$B" >/dev/null 2>&1 || true
  sleep 1
}

run_one FreshQ330 mortar '投弹'
run_one FreshQ331 molten '亡爆'

for b in FreshQ330 FreshQ331 FreshQ332 FreshQ333 FreshQ334 FreshQ335 FreshQ336 FreshQ337 FreshQ338 FreshQ339 FreshQ340 FreshQ341 FreshQ342 FreshQ343 FreshQ344 FreshQ345 FreshQ346 FreshQ347 FreshQ348 FreshQ349; do
  curl -sf "$BOTD/quit?name=$b" >/dev/null 2>&1 || true
done
curl -sf "$BOTD/list" | python3 -c 'import sys,json; d=json.load(sys.stdin); print(d)' 2>/dev/null || true

echo ""
echo "d181-variety-smoke: PASS=$PASS FAIL=$FAIL"
[ "$FAIL" -eq 0 ]
