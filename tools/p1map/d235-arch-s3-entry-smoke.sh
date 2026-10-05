#!/usr/bin/env bash
# d235-arch-s3-entry-smoke.sh — CoreRpg 1.65.61 D235: EmberEntryService extract smoke.
# Entry gates (unlock / challenge / rush / raid party size / abyss / stamina / variant), D96 + D104 readiness,
# forced weekly rule on a Q01 normal run, Q01 enter + settle. Gate wording pinned by EmberEntryServiceTest.
# Bots FreshQ813+. No asset path → no persist-roundtrip.
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
say(){ local B=$1; local MSG=$2; local W=${3:-4000}; $T/b.sh chat "$B" "$MSG" "$W" 2>/dev/null | tr -d '\r'; }
fc_all(){ for q in q01 q02 q03 q04 q05 q06 q07; do $C play "corerpg p1 runs firstclear $1 $q" 0.2 >/dev/null; done
  for q in q02 q03 q04 q05 q06 q07; do $C play "corerpg p1 runs unlock $1 $q" 0.2 >/dev/null; done; }
has(){ echo "$2" | grep -aqE "$3" && ok "$1" || bad "$1"; }

grep -aq "Enabling CoreRpg v1.65.61" "$LOG" && ok "CoreRpg 1.65.61" || bad "CoreRpg 1.65.61"
grep -aq "\[CoreRpg\] \[storage\] MySQL connected" "$LOG" && ok "CoreRpg MySQL" || bad "CoreRpg MySQL"
grep -aq "\[CoreGacha\] \[db\] MySQL connected" "$LOG" && ok "CoreGacha MySQL" || bad "CoreGacha MySQL"
grep -aq "ember-v1-economy.yml loaded as amount() SoT (bv58)" "$LOG" && ok "E1 SoT bv58" || bad "E1 SoT bv58"
grep -aq "FAIL-CLOSED" "$LOG" && bad "FAIL-CLOSED" || ok "no FAIL-CLOSED"

F=${F:-FreshQ813}; V=${V:-FreshQ814}; W=${W:-FreshQ815}

# ---- fresh character: per-mode gate lines (no first clears)
join $F || exit 1
$T/b.sh chat "$F" "/dungeon-team disband" 800 >/dev/null || true
R=$(say "$F" "/corerpg p1 enter q02" 4000); echo "  q02 fresh: $(echo "$R" | head -c 240)"
has "unlock gate (q02 未解锁 需先首通 Q01)" "$R" "$F 未解锁（需先首通 Q01 "
R=$(say "$F" "/corerpg p1 enter q01 challenge" 4000); echo "  challenge fresh: $(echo "$R" | head -c 240)"
has "challenge gate (未开放挑战版 Q07)" "$R" "$F 未开放挑战版（需本人首通 Q07）"
R=$(say "$F" "/corerpg p1 enter rush" 4000); echo "  rush fresh: $(echo "$R" | head -c 240)"
has "rush gate (未开放余烬连战 Q07) via EmberRushService" "$R" "$F 未开放.*（需本人首通 Q07）"
R=$(say "$F" "/corerpg p1 enter rush challenge" 4000); echo "  rush challenge: $(echo "$R" | head -c 240)"
has "variant gate (没有挑战 / 深渊版本)" "$R" "没有挑战 / 深渊版本"
R=$(say "$F" "/corerpg p1 enter r01" 4000); echo "  r01 solo: $(echo "$R" | head -c 300)"
has "raid party size gate (人数 3～5，当前 1)" "$R" "人数 3～5，当前 1"
has "raid problem via EmberRaidService (Q07)" "$R" "Q07"
R=$(say "$F" "/corerpg p1 abyss 1" 4000); echo "  abyss fresh: $(echo "$R" | head -c 300)"
has "abyss gate (未开放深渊 / 需本人首通 Q07)" "$R" "未开放深渊|需本人首通 Q07"
$C play "corerpg stamina set $F 10" 0.4 >/dev/null
R=$(say "$F" "/corerpg p1 enter q01" 4000); echo "  stamina low: $(echo "$R" | head -c 240)"
has "stamina gate (体力不足 需 30 当前 10)" "$R" "$F 体力不足（需 30，当前 10）"
$C play "corerpg stamina set $F 200" 0.4 >/dev/null
quitall $F

# ---- readiness: D96 (Q02 first attempt without T1) and D104 (challenge without T3)
join $W || exit 1
$C play "corerpg p1 runs firstclear $W q01" 0.2 >/dev/null; $C play "corerpg p1 runs unlock $W q02" 0.2 >/dev/null
$T/b.sh chat "$W" "/dungeon-team disband" 800 >/dev/null || true
R=$(say "$W" "/corerpg p1 enter q02" 4500); echo "  q02 D96: $(echo "$R" | head -c 360)"
has "D96 T1 readiness warn (Q02 首通推荐)" "$R" "Q02 首通推荐：T1 刃 \+ T1 护符"
has "D96 [仍然进入] button" "$R" "仍然进入"
quitall $W
join $V || exit 1
fc_all $V
$T/b.sh chat "$V" "/dungeon-team disband" 800 >/dev/null || true
R=$(say "$V" "/corerpg p1 enter q01 challenge" 4500); echo "  challenge D104: $(echo "$R" | head -c 300)"
has "D104 T3 readiness warn (挑战版按 T3 装备来调)" "$R" "挑战版按 T3 装备来调"
has "D104 [仍然进入] button" "$R" "仍然进入"
quitall $V

# ---- Q01 enter with a forced weekly rule (applyWeeklyRule + forceModifier hook) + settle
join $F || exit 1
N2=$(wc -l < "$LOG")
R=$($C play "corerpg p1 runs modifier lean" 0.5 | tr -d '\r'); echo "  force: $(echo "$R" | tail -1 | head -c 200)"
has "admin forced rule set (lean)" "$R" "= lean"
$C play "gamemode creative $F" 0.3 >/dev/null
R=$($T/b.sh chat "$F" "/corerpg p1 enter q01" 12000 | tr -d '\r'); echo "  q01: $(echo "$R" | head -c 320)"; sleep 6
has "Q01 entering line (正在创建实例 / 已预留体力 30)" "$R" "Q01 .*正在创建实例……（已预留体力 30）"
FULL=$(tail -n +"$((N2+1))" "$LOG" | tr -d '\r')
has "Q01 forced rule logged (admin test, normal run)" "$FULL" "modifier forced lean \(admin test, normal run\)"
$C play "tp $F 0 64 28" 0.3 >/dev/null; sleep 2; clear_room "$F" && ok "q01 r1" || bad "q01 r1"
$C play "tp $F 30 64 38" 0.3 >/dev/null; sleep 2; clear_room "$F" && ok "q01 r2" || bad "q01 r2"
$C play "tp $F 32 64 68" 0.3 >/dev/null; sleep 2; clear_room "$F" && ok "q01 r3" || bad "q01 r3"
$C play "tp $F 0 64 102" 0.3 >/dev/null; sleep 2
settle "$F"; sleep 4
$C play "gamemode survival $F" 0.3 >/dev/null
FULL2=$(tail -n +"$((N2+1))" "$LOG" | tr -d '\r')
echo "$FULL2" | grep -aqE "\[P1 run\] q01-.* settle" && ok "q01 settle" || bad "q01 settle"
echo "$FULL2" | grep -aE "economy grant(Coin|Mat|Xp|Mark|Badge) refused" && bad "grant refused" || ok "no grant refusal q01"
quitall $F

BOOT=$(rg -n "Enabling CoreRpg v1.65.61" "$LOG" | tail -1 | cut -d: -f1)
if [[ -n "$BOOT" ]]; then
  POST=$(tail -n +"$((BOOT))" "$LOG" | rg -c "SEVERE" || true); POST=${POST:-0}
  [[ "$POST" == "0" ]] && ok "SEVERE 0 (post-enable)" || bad "SEVERE post-enable=$POST"
else
  soft "SEVERE boot marker missing"
fi

echo "==== RESULT PASS $PASS / FAIL $FAIL / SOFT $SOFT ===="
[[ "$FAIL" == "0" ]]
