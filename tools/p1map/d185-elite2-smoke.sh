#!/usr/bin/env bash
# d185-elite2-smoke.sh — D185 奖励精英变招 Pack 2: force Extra.ELITE on Q01, expect the dual-name tell 门廊推/灰烬扇. Bot name = $1.
set -euo pipefail
C=/workspace/minecraft/scripts/console.sh
BOTD=http://127.0.0.1:8765
T=/workspace/minecraft/mineflayer-tests/tmp-p1
LOG=/workspace/minecraft/server-runtime/logs/latest.log
PASS=0; FAIL=0
ok(){ echo "PASS $1"; PASS=$((PASS+1)); }
bad(){ echo "FAIL $1"; FAIL=$((FAIL+1)); }
B=${1:-FreshQ422}

curl -sf "$BOTD/quit?name=$B" >/dev/null 2>&1 || true
sleep 1
curl -sf -m 90 "$BOTD/join?name=$B" >/dev/null || { echo FAIL join; exit 1; }
sleep 7
$C play "list" 0.5 | tr -d '\r' | grep -q "$B" && ok online || { bad online; exit 1; }
$C play "corerpg stamina set $B 200" 0.5 >/dev/null
$C play "corerpg p1 runs firstclear $B q01" 0.4 >/dev/null
$C play "corerpg p1 runs extra elite" 0.5 | tr -d '\r' | tail -1
N0=$(wc -l < "$LOG")
$T/b.sh chat "$B" "/corerpg p1 enter q01" 6000 >/dev/null || true
sleep 5
$C play "gamemode creative $B" 0.3 >/dev/null || true
for spot in "0 64 28" "30 64 38" "32 64 68" "0 64 102"; do
  echo "-- $spot --"
  $C play "tp $B $spot" 0.3 >/dev/null
  sleep 1.5
  for k in $(seq 1 12); do
    W=$($C play "corerpg p1 runs weaken $B" 0.3 2>/dev/null | tr -d '\r' || true)
    N=$(echo "$W" | sed -n 's/.*削弱 \([0-9]*\) 只.*/\1/p' | tail -1)
    $C play "corerpg p1 heal $B" 0.1 >/dev/null || true
    $T/fight.sh "$B" 10000 0 32 2000 >/dev/null 2>&1 || true
    CH=$($T/b.sh eval "$B" "return bot.chatLog.slice(-30).map(s=>s.replace(/§./g,'')).join('\n')" 2>/dev/null || true)
    if echo "$CH" | grep -qE '灰烬扇'; then
      echo "$CH" | grep -E '灰烬扇|奖励精英' | head -3
      break 2
    fi
    [[ "${N:-1}" == "0" ]] && break
  done
done
CH=$($T/b.sh eval "$B" "return bot.chatLog.slice(-80).map(s=>s.replace(/§./g,'')).join('\n')" 2>/dev/null || true)
echo "$CH" | grep -qE '奖励精英' && ok elite-tell || bad elite-tell
echo "$CH" | grep -qE '灰烬扇' && ok ashfan-alt || bad ashfan-alt
FULL=$(tail -n +$((N0+1)) "$LOG" | tr -d '\r')
echo "$FULL" | grep -qiE 'SEVERE' && bad SEVERE || ok no-SEVERE
curl -sf "$BOTD/quit?name=$B" >/dev/null || true
$C play "corerpg p1 runs extra clear" 0.3 >/dev/null || true
echo "RESULT PASS=$PASS FAIL=$FAIL"
[[ "$FAIL" -eq 0 ]]
