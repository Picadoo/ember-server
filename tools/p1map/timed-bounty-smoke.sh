#!/usr/bin/env bash
# timed-bounty-smoke.sh [BOT] — D144 花样委托·限时清房 live smoke.
# Admin firstclear Q01 → force variety event:r1 → normal Q01 enter → weaken+kill rooms → settle.
# Needs: game server up, botd on 127.0.0.1:8765. Does NOT restart. Default bot FreshQ43.
set -euo pipefail
B=${1:-FreshQ43}
C=/workspace/minecraft/scripts/console.sh
BOTD=http://127.0.0.1:8765
LOG=/workspace/minecraft/server-runtime/logs/latest.log
T=/workspace/minecraft/mineflayer-tests/tmp-p1
PASS=0; FAIL=0
ok(){ echo "PASS $1"; PASS=$((PASS+1)); }
bad(){ echo "FAIL $1"; FAIL=$((FAIL+1)); }

curl -sf "$BOTD/list" >/dev/null || { echo "FAIL botd not reachable at $BOTD"; exit 1; }

# quit leftover if any
curl -sf "$BOTD/quit?name=$B" >/dev/null 2>&1 || true
sleep 1

echo "== join $B =="
JOIN=$(curl -sf -m 90 "$BOTD/join?name=$B" || true)
echo "$JOIN" | head -c 200; echo
sleep 8

# confirm online
ONLINE=$($C play "list" 0.6 | tr -d '\r' || true)
echo "$ONLINE" | grep -q "$B" && ok "bot online" || { bad "bot not online ($ONLINE)"; curl -sf "$BOTD/quit?name=$B" >/dev/null || true; echo "timed-bounty-smoke $B: PASS=$PASS FAIL=$FAIL"; exit 1; }

# stamina + firstclear + force timed variety on r1
$C play "corerpg stamina set $B 120" 0.8 | tr -d '\r' | tail -3
$C play "corerpg p1 runs firstclear $B q01" 0.8 | tr -d '\r' | tail -3
$C play "corerpg p1 runs variety event:r1" 0.8 | tr -d '\r' | tail -5

N0=$(wc -l < "$LOG")
CHAT0=$($T/b.sh eval "$B" "return bot.chatLog.length" | python3 -c 'import sys,json;print(json.load(sys.stdin).get("r",0))' 2>/dev/null || echo 0)

echo "== enter q01 =="
$T/b.sh chat "$B" "/corerpg p1 enter q01" 6000 | tr -d '\r' | sed 's/§.//g' | tail -c 500; echo
sleep 4

# creative for safe TP pathing (combat still via attack + weaken)
$C play "gamemode creative $B" 0.5 >/dev/null || true
sleep 1

# Q01 safe spots: r1 (timed!) → r2 → r3 → boss
# Timed room is forced to r1: clear it ASAP after trigger.
for spot in "0 64 28" "30 64 38" "32 64 68" "0 64 102"; do
  echo "== room @ $spot =="
  $C play "tp $B $spot" 0.4 >/dev/null
  sleep 2
  # for first spot (r1 timed), hammer weaken+fight hard and fast
  for k in 1 2 3 4 5 6; do
    $C play "corerpg p1 runs weaken $B" 0.4 | tr -d '\r' | tail -1
    $C play "corerpg p1 heal $B" 0.3 >/dev/null || true
    OUT=$($T/fight.sh "$B" 12000 0 28 2500 2>/dev/null | tr -d '\n ' || true)
    echo "  fight$k: $(echo "$OUT" | tail -c 160)"
    # stop early if no mobs left nearby
    LEFT=$(echo "$OUT" | python3 -c 'import sys,json,re
try:
  d=json.loads(sys.stdin.read())["r"]
  print(d.get("left") or d.get("hits",""))
except Exception:
  print("")' 2>/dev/null || true)
    # check if room cleared chat arrived
    CLR=$($T/b.sh eval "$B" "return bot.chatLog.slice(-8).map(s=>s.replace(/§./g,'')).join('|')" 2>/dev/null || true)
    echo "$CLR" | grep -qE '限时清房完成|房间.*清空|通道已开启|首领' && break
    sleep 0.5
  done
  sleep 1
done

# wait for settlement
echo "== wait settle =="
for i in $(seq 1 40); do
  sleep 2
  CH=$($T/b.sh eval "$B" "return bot.chatLog.slice(-20).map(s=>s.replace(/§./g,'')).join('\n')" 2>/dev/null || true)
  echo "$CH" | grep -qE 'settle|通关|结算|花样委托|每日委托|首领.*击败|本局结束' && break
  # keep boss weakened if still fighting
  $C play "corerpg p1 runs weaken $B" 0.3 >/dev/null || true
  $C play "corerpg p1 heal $B" 0.2 >/dev/null || true
  $T/fight.sh "$B" 8000 0 32 2000 >/dev/null 2>&1 || true
done

sleep 3
$C play "gamemode survival $B" 0.4 >/dev/null || true

echo "== evidence =="
CHAT=$($T/b.sh eval "$B" "return bot.chatLog.slice($CHAT0).map(s=>s.replace(/§./g,''))" 2>/dev/null || true)
echo "$CHAT" | python3 -c 'import sys,json
try:
  d=json.load(sys.stdin); r=d.get("r",d)
  if isinstance(r,list):
    for x in r: print(x)
  else: print(r)
except Exception as e:
  print(sys.stdin.read() if False else open("/dev/stdin").read() if False else "")
' 2>/dev/null || echo "$CHAT"
echo "--- console since enter ---"
tail -n +"$((N0+1))" "$LOG" | tr -d '\r' | grep -E "FreshQ43|variety|限时|event|settle|P1 run|花样|bounty|vbounty|first_clear|forced" | head -60

# assertions from bot chat + console
FULL=$(echo "$CHAT"; tail -n +"$((N0+1))" "$LOG")
echo "$FULL" | grep -qE '限时清房完成|限时清房.*完成' && ok "限时清房完成" || bad "no 限时清房完成"
echo "$FULL" | grep -qE '花样委托.*(限时清房|完成)|限时清房 1/1' && ok "花样委托 timed progress" || bad "no 花样委托 timed line"
echo "$FULL" | grep -qE 'variety forced event' && ok "admin variety force logged" || bad "no variety forced log"
echo "$FULL" | grep -qE "settle .*FreshQ43|settle .*$B|settle [0-9a-f-]+ " && ok "settle logged" || {
  # settle log uses UUID not name
  echo "$FULL" | grep -qE '\[P1 run\].*settle' && ok "settle logged" || bad "no settle"
}

curl -sf "$BOTD/quit?name=$B" >/dev/null || true
echo "timed-bounty-smoke $B: PASS=$PASS FAIL=$FAIL"
[ "$FAIL" -eq 0 ]
