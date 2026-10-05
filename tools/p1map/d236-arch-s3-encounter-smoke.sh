#!/usr/bin/env bash
# d236-arch-s3-encounter-smoke.sh — CoreRpg 1.65.62 D236: encounter primitives (counterplay + revive adapters).
# Behaviour-preserving: Q01 clear regression (tp rooms like D235) + optional counterplay log.
# Bots FreshQ819+. No asset path → no persist-roundtrip.
set -uo pipefail
C=/workspace/minecraft/scripts/console.sh; BOTD=http://127.0.0.1:8765; T=/workspace/minecraft/mineflayer-tests/tmp-p1
LOG=/workspace/minecraft/server-runtime/logs/latest.log
PASS=0; FAIL=0; SOFT=0
ok(){ echo "PASS $1"; PASS=$((PASS+1)); }; bad(){ echo "FAIL $1"; FAIL=$((FAIL+1)); }; soft(){ echo "SOFT $1"; SOFT=$((SOFT+1)); }
curl -sf "$BOTD/list" >/dev/null || { echo "FAIL botd"; exit 1; }
weaken_n(){ local W; W=$($C play "corerpg p1 runs weaken $1" 0.4 2>/dev/null | tr -d '\r' || true)
  if echo "$W" | grep -q '没有进行中'; then echo -1; return; fi
  local N; N=$(echo "$W" | sed -n 's/.*削弱 \([0-9]*\) 只.*/\1/p' | tail -1); echo "${N:--2}"; }
clear_room(){ local B=$1; local saw=0; for k in $(seq 1 40); do
  N=$(weaken_n "$B"); [[ "$N" == -1 ]] && return 1
  [[ "$N" =~ ^[1-9] ]] && saw=1
  [[ "$saw" == 1 && "$N" == 0 ]] && return 0
  $C play "corerpg p1 heal $B" 0.1 >/dev/null || true
  $T/fight.sh "$B" 14000 0 40 2000 >/dev/null 2>&1 || true
done; [[ "$saw" == 1 && "$(weaken_n "$B")" == 0 ]]; }
settle(){ local B=$1; for i in $(seq 1 50); do N=$(weaken_n "$B"); [[ "$N" == -1 ]] && break
  $C play "corerpg p1 heal $B" 0.1 >/dev/null || true; $T/fight.sh "$B" 10000 0 48 1500 >/dev/null 2>&1 || true; sleep 1; done; }
join(){ local B=$1; curl -sf "$BOTD/quit?name=$B" >/dev/null 2>&1 || true; sleep 1
  curl -sf -m 90 "$BOTD/join?name=$B" >/dev/null || { bad "$B join"; return 1; }; sleep 8
  $C play "list" 0.6 | tr -d '\r' | grep -q "$B" && ok "$B online" || { bad "$B online"; return 1; }
  $C play "corerpg stamina set $B 200" 0.4 >/dev/null; }
quitall(){ for b in "$@"; do curl -sf "$BOTD/quit?name=$b" >/dev/null 2>&1 || true; done; sleep 2; }

grep -aq "Enabling CoreRpg v1.65.62" "$LOG" && ok "CoreRpg 1.65.62" || bad "CoreRpg 1.65.62"
grep -aq "\[CoreRpg\] \[storage\] MySQL connected" "$LOG" && ok "CoreRpg MySQL" || bad "CoreRpg MySQL"
grep -aq "\[CoreGacha\] \[db\] MySQL connected" "$LOG" && ok "CoreGacha MySQL" || bad "CoreGacha MySQL"
grep -aq "ember-v1-economy.yml loaded as amount() SoT (bv58)" "$LOG" && ok "E1 SoT bv58" || bad "E1 SoT bv58"
grep -aq "FAIL-CLOSED" "$LOG" && bad "FAIL-CLOSED" || ok "no FAIL-CLOSED"

F=${F:-FreshQ819}

join $F || exit 1
$T/b.sh chat "$F" "/dungeon-team disband" 800 >/dev/null || true
N2=$(wc -l < "$LOG")
$C play "gamemode creative $F" 0.3 >/dev/null
R=$($T/b.sh chat "$F" "/corerpg p1 enter q01" 12000 | tr -d '\r'); echo "  q01: $(echo "$R" | head -c 320)"; sleep 6
echo "$R" | grep -aq "正在创建实例" && ok "Q01 entering line" || bad "Q01 entering line"

$C play "tp $F 0 64 28" 0.3 >/dev/null; sleep 2; clear_room "$F" && ok "q01 r1" || bad "q01 r1"
$C play "tp $F 30 64 38" 0.3 >/dev/null; sleep 2; clear_room "$F" && ok "q01 r2" || bad "q01 r2"
$C play "tp $F 32 64 68" 0.3 >/dev/null; sleep 2; clear_room "$F" && ok "q01 r3" || bad "q01 r3"
$C play "tp $F 0 64 102" 0.3 >/dev/null; sleep 2
settle "$F"; sleep 4
$C play "gamemode survival $F" 0.3 >/dev/null

FULL2=$(tail -n +"$((N2+1))" "$LOG" | tr -d '\r')
echo "$FULL2" | grep -aqE "\[P1 run\] q01-.* settle" && ok "q01 settle" || bad "q01 settle"
echo "$FULL2" | grep -aE "economy grant(Coin|Mat|Xp|Mark|Badge) refused" && bad "grant refused" || ok "no grant refusal q01"
if echo "$FULL2" | grep -aqE 'whiff stun|wall stun| break '; then
  ok "counterplay log line seen (whiff/wall/break)"
  echo "$FULL2" | grep -aE 'whiff stun|wall stun| break ' | tail -3
else
  soft "no counterplay log this run (positioning; unit tests pin behaviour)"
fi

BOOT=$(rg -n "Enabling CoreRpg v1.65.62" "$LOG" | tail -1 | cut -d: -f1)
if [[ -n "$BOOT" ]]; then
  POST=$(tail -n +"$((BOOT))" "$LOG" | rg -c "SEVERE" || true); POST=${POST:-0}
  [[ "$POST" == "0" ]] && ok "SEVERE 0 (post-enable)" || bad "SEVERE post-enable=$POST"
else
  soft "SEVERE boot marker missing"
fi

quitall $F
echo "==== RESULT PASS $PASS / FAIL $FAIL / SOFT $SOFT ===="
[[ "$FAIL" == "0" ]]
