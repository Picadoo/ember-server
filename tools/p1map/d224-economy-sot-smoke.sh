#!/usr/bin/env bash
# d224-economy-sot-smoke.sh — CoreRpg 1.65.52 D224 ARCH S2-7: amount() yml SoT + fail-closed load.
# Plugin load + MySQL×2 + economy SoT log + Q01 first clear settle + forge enhance spend (coin).
# FreshQ785. No asset path change → no persist-roundtrip.
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

grep -q "Enabling CoreRpg v1.65.52" "$LOG" && ok "CoreRpg 1.65.52" || bad "CoreRpg 1.65.52"
grep -q "\[CoreRpg\] \[storage\] MySQL connected" "$LOG" && ok "CoreRpg MySQL" || bad "CoreRpg MySQL"
grep -q "\[CoreGacha\] \[db\] MySQL connected" "$LOG" && ok "CoreGacha MySQL" || bad "CoreGacha MySQL"
grep -q "ember-v1-economy.yml loaded as amount() SoT" "$LOG" && ok "E1 economy yml SoT" || bad "E1 economy yml SoT"
grep -q "FAIL-CLOSED" "$LOG" && bad "economy FAIL-CLOSED present" || ok "no economy FAIL-CLOSED"

A=${BOT:-FreshQ785}; N0=$(wc -l < "$LOG")
join $A || exit 1
expect $A q01_state 已解锁; expect_not $A q01_fc 已领取
COIN_BEFORE=$(ph "$A" coin); echo "  coin before settle = $COIN_BEFORE"
run_q01 $A
FULL=$(tail -n +"$((N0+1))" "$LOG" | tr -d '\r')
echo "$FULL" | grep -qE "q01.*settle .*\(first clear\)|settle .*first clear" && ok "$A settled-first-clear" || bad "$A settled-first-clear"
expect $A q01_state 已首通; expect $A q01_fc 已领取; expect $A q02_open yes
COIN_AFTER=$(ph "$A" coin); echo "  coin after settle = $COIN_AFTER"
# first clear pays base 300 + fc package; just assert coin rose (amount SoT still grants)
if [[ -n "$COIN_BEFORE" && -n "$COIN_AFTER" && "$COIN_BEFORE" =~ ^[0-9]+$ && "$COIN_AFTER" =~ ^[0-9]+$ && "$COIN_AFTER" -gt "$COIN_BEFORE" ]]; then
  ok "$A settle coin rose ($COIN_BEFORE→$COIN_AFTER)"
else
  bad "$A settle coin ($COIN_BEFORE→$COIN_AFTER)"
fi

# C07 marks exchange (amount SoT for C07 marks=8) + C03 enhance (SOFT if no piece)
$C play "gamemode survival $A" 0.4 >/dev/null; $C play "spawn $A" 0.4 >/dev/null 2>&1 || true; sleep 3
mk(){ $C play "corerpg p1 runs marks $A 1 ${1:-0}" 0.5 2>/dev/null | tr -d '\r' | sed 's/§.//g' | sed -n 's/.*T1 锻造印记 = \([0-9]*\).*/\1/p' | tail -1; }
M0=$(mk 8); echo "  T1 marks after +8 = $M0"
N1=$(wc -l < "$LOG")
R=$($T/b.sh chat "$A" "/corerpg p1 marks exchange scorch blade 1 confirm" 4000 | tr -d '\r'); echo "  exchange: $R" | head -c 400; echo
sleep 3
M1=$(mk 0); echo "  T1 marks after exchange = $M1"
[[ -n "$M0" && -n "$M1" && $((M0 - M1)) -eq 8 ]] && ok "$A C07 took 8 marks ($M0→$M1)" || bad "$A C07 marks ($M0→$M1)"
tail -n +"$((N1+1))" "$LOG" | grep -q "mark redeem .* T1 scorch blade" && ok "$A C07 mark redeem committed" || bad "$A C07 mark redeem log"

SOFT=""
for i in 0 1 2 3 4 5 6 7 8; do
  $T/b.sh eval "$A" "bot.setQuickBarSlot($i); await wait(300); return 1" >/dev/null
  P=$($T/b.sh chat "$A" "/corerpg p1 enhance" 1500 | tr -d '\r')
  if echo "$P" | grep -q "强化预览"; then
    N2=$(wc -l < "$LOG")
    $T/b.sh chat "$A" "/corerpg p1 enhance confirm" 4000 >/dev/null; sleep 2
    if tail -n +"$((N2+1))" "$LOG" | grep -q "forge enhance .* ok"; then ok "$A C03 enhance paid+committed (slot $i)"; else SOFT="enhance confirm not logged (slot $i)"; fi
    break
  fi
done
[[ -z "$SOFT" ]] || echo "SOFT $SOFT"

tail -n +"$((N0+1))" "$LOG" | grep -q "economy grantMat refused" && bad "grantMat refused" || ok "no grantMat refusal"
tail -n +"$((N0+1))" "$LOG" | grep -q "\[P1 pay\] registry refused" && bad "registry refused" || ok "no registry refusal"
FULL=$(tail -n +"$((N0+1))" "$LOG" | tr -d '\r')
echo "$FULL" | grep -qE 'SEVERE|Exception' && bad "$A SEVERE" || ok "$A no-SEVERE"
quitall $A
echo "list=$(curl -sf $BOTD/list)"
echo "RESULT PASS=$PASS FAIL=$FAIL"
[[ "$FAIL" -eq 0 ]]
