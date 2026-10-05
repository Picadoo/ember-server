#!/usr/bin/env bash
# d223-economy-delivery-smoke.sh — CoreRpg 1.65.51 D223 ARCH S2-6: Delivery debit tags + E1 economy yml mirror.
# Plugin load + MySQL×2 + Q01 first clear + economy yml mirror log + festival trail buy (regression).
# FreshQ784. No asset path change → no persist-roundtrip.
set -uo pipefail
C=/workspace/minecraft/scripts/console.sh; BOTD=http://127.0.0.1:8765; T=/workspace/minecraft/mineflayer-tests/tmp-p1
LOG=/workspace/minecraft/server-runtime/logs/latest.log
PASS=0; FAIL=0
ok(){ echo "PASS $1"; PASS=$((PASS+1)); }; bad(){ echo "FAIL $1"; FAIL=$((FAIL+1)); }
curl -sf "$BOTD/list" >/dev/null || { echo "FAIL botd"; exit 1; }
ph(){ $C play "papi parse $1 %corerpg_p1_$2%" 0.5 2>/dev/null | tr -d '\r' | sed 's/§.//g' | grep -v '^\s*$' | tail -1 | sed 's/^.*INFO\]: //'; }
expect(){ local B=$1 K=$2 W=$3 V; V=$(ph "$B" "$K"); echo "  $B $K = $V"; [[ "$V" == *"$W"* ]] && ok "$B $K~$W" || bad "$B $K~$W (got: $V)"; }
expect_not(){ local B=$1 K=$2 W=$3 V; V=$(ph "$B" "$K"); echo "  $B $K = $V"; [[ -n "$V" && "$V" != *"$W"* ]] && ok "$B $K!~$W" || bad "$B $K!~$W (got: $V)"; }
weaken_n(){ local W; W=$($C play "corerpg p1 runs weaken $1 ${2:-}" 0.4 2>/dev/null | tr -d '\r' || true)
  if echo "$W" | grep -q '没有进行中'; then echo -1; return; fi
  local N; N=$(echo "$W" | sed -n 's/.*削弱 \([0-9]*\) 只.*/\1/p' | tail -1); echo "${N:--2}"; }
clear_room(){ local B=$1; for k in $(seq 1 24); do N=$(weaken_n "$B"); [[ "$N" == 0 ]] && return 0; [[ "$N" == -1 ]] && return 1
  $C play "corerpg p1 heal $B" 0.12 >/dev/null || true; $T/fight.sh "$B" 12000 0 32 2500 >/dev/null 2>&1 || true; done; [[ "$(weaken_n "$B")" == 0 ]]; }
settle(){ local B=$1; for i in $(seq 1 30); do N=$(weaken_n "$B"); [[ "$N" == -1 ]] && break; $C play "corerpg p1 heal $B" 0.12 >/dev/null || true; $T/fight.sh "$B" 6000 0 40 1500 >/dev/null 2>&1 || true; sleep 1; done; }
join(){ local B=$1; curl -sf "$BOTD/quit?name=$B" >/dev/null 2>&1 || true; sleep 1
  curl -sf -m 90 "$BOTD/join?name=$B" >/dev/null || { bad "$B join"; return 1; }; sleep 7
  $C play "list" 0.5 | tr -d '\r' | grep -q "$B" && ok "$B online" || { bad "$B online"; return 1; }
  $C play "corerpg stamina set $B 200" 0.5 >/dev/null; }
run_q01(){ local B=$1; $C play "corerpg p1 runs extra none" 0.4 >/dev/null || true; $C play "corerpg p1 runs variety clear" 0.4 >/dev/null || true
  $T/b.sh chat "$B" "/corerpg p1 enter q01" 6000 >/dev/null || true; sleep 6
  $C play "gamemode creative $B" 0.4 >/dev/null
  local si=0; for spot in "0 64 29" "30 64 39" "32 64 69"; do si=$((si+1)); $C play "tp $B $spot" 0.4 >/dev/null; sleep 2; clear_room "$B" && ok "$B room$si" || bad "$B room$si"; done
  $C play "tp $B 0 64 96" 0.4 >/dev/null; sleep 4; settle "$B"; sleep 4; }
quitall(){ for b in "$@"; do curl -sf "$BOTD/quit?name=$b" >/dev/null 2>&1 || true; done; sleep 2; }

grep -q "Enabling CoreRpg v1.65.51" "$LOG" && ok "CoreRpg 1.65.51" || bad "CoreRpg 1.65.51"
grep -q "\[CoreRpg\] \[storage\] MySQL connected" "$LOG" && ok "CoreRpg MySQL" || bad "CoreRpg MySQL"
grep -q "\[CoreGacha\] \[db\] MySQL connected" "$LOG" && ok "CoreGacha MySQL" || bad "CoreGacha MySQL"
grep -q "ember-v1-economy.yml mirrors registry golden" "$LOG" && ok "E1 economy yml mirror" || bad "E1 economy yml mirror"

A=${BOT:-FreshQ784}; N0=$(wc -l < "$LOG")
join $A || exit 1
expect $A q01_state 已解锁; expect_not $A q01_fc 已领取
run_q01 $A
FULL=$(tail -n +"$((N0+1))" "$LOG" | tr -d '\r')
echo "$FULL" | grep -qE "q01.*settle .*\(first clear\)|settle .*first clear" && ok "$A settled-first-clear" || bad "$A settled-first-clear"
expect $A q01_state 已首通; expect $A q01_fc 已领取; expect $A q02_open yes

# C18 festival trail buy (event window still open through 2026-10-08): stub Q04 FC + give 国庆币 + buy trail
$C play "gamemode survival $A" 0.4 >/dev/null; $C play "spawn $A" 0.4 >/dev/null 2>&1 || true; sleep 2
$C play "corerpg p1 runs firstclear $A q04" 0.5 >/dev/null
$C play "corerpg p1 fest reset $A" 0.5 >/dev/null
$C play "corerpg p1 fest coins $A 40" 0.5 >/dev/null
COIN0=$(ph "$A" fest_coins); echo "  fest coins before = $COIN0"
N1=$(wc -l < "$LOG")
R=$($T/b.sh chat "$A" "/corerpg p1 fest buy trail" 4000 | tr -d '\r'); echo "  fest buy: $R" | head -c 400; echo
sleep 2
COIN1=$(ph "$A" fest_coins); echo "  fest coins after = $COIN1"
# trail costs 30 国庆币
if [[ -n "$COIN0" && -n "$COIN1" && "$COIN0" =~ ^[0-9]+$ && "$COIN1" =~ ^[0-9]+$ && $((COIN0 - COIN1)) -eq 30 ]]; then
  ok "$A C18 fest trail spent 30 ($COIN0→$COIN1)"
else
  bad "$A C18 fest trail coins ($COIN0→$COIN1)"
fi
tail -n +"$((N1+1))" "$LOG" | grep -qE "\[P1 fest\] buy .* trail_" && ok "$A C18 fest buy logged" || bad "$A C18 fest buy log"
tail -n +"$((N0+1))" "$LOG" | grep -q "economy grantMat refused" && bad "grantMat refused" || ok "no grantMat refusal"
tail -n +"$((N0+1))" "$LOG" | grep -q "\[P1 pay\] registry refused" && bad "registry refused" || ok "no registry refusal"

FULL=$(tail -n +"$((N0+1))" "$LOG" | tr -d '\r')
echo "$FULL" | grep -qE 'SEVERE|Exception' && bad "$A SEVERE" || ok "$A no-SEVERE"
quitall $A
echo "list=$(curl -sf $BOTD/list)"
echo "RESULT PASS=$PASS FAIL=$FAIL"
[[ "$FAIL" -eq 0 ]]
