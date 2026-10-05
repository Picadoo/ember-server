#!/usr/bin/env bash
# d228-economy-insignia-badge-smoke.sh — CoreRpg 1.65.54 D228 ARCH S2-8: insignia / badge / weekly-mark grants via EmberEconomy.
# Checks: plugin load + MySQL×2 + bv58 SoT; Q01 first clear (S07 fc_sigmark +3), Q01 repeat (S08 sig_mark +1),
# echo_q01 claim (S18 rush_sig_q01 +2), weekly goal (S19 badge +15), festival exchange (S27 badge +2);
# no "economy grant* refused" / "untagged mark|insignia row" lines; SEVERE 0. FreshQ790 (FreshQ788 killed attempt; FreshQ789 run 1 S07/S19/S27 PASS, repeat-run step script bug). No asset path change → no persist-roundtrip.
set -uo pipefail
C=/workspace/minecraft/scripts/console.sh; BOTD=http://127.0.0.1:8765; T=/workspace/minecraft/mineflayer-tests/tmp-p1
LOG=/workspace/minecraft/server-runtime/logs/latest.log
PASS=0; FAIL=0
ok(){ echo "PASS $1"; PASS=$((PASS+1)); }; bad(){ echo "FAIL $1"; FAIL=$((FAIL+1)); }
curl -sf "$BOTD/list" >/dev/null || { echo "FAIL botd"; exit 1; }
ph(){ $C play "papi parse $1 %corerpg_p1_$2%" 0.5 2>/dev/null | tr -d '\r' | sed 's/§.//g' | grep -v '^\s*$' | tail -1 | sed 's/^.*INFO\]: //'; }
num(){ local V; V=$(ph "$1" "$2"); [[ "$V" =~ ^[0-9]+$ ]] && echo "$V" || echo "NA($V)"; }
sigq(){ local V; V=$($C play "corerpg p1 sig test show $1" 0.6 2>/dev/null | tr -d '\r' | grep -a "徽记 q01=" | tail -1 | sed -n "s/.* $2=\([0-9]*\) .*/\1/p"); [[ "$V" =~ ^[0-9]+$ ]] && echo "$V" || echo "NA"; }
delta(){ local L=$1 A0=$2 A1=$3 W=$4; if [[ "$A0" =~ ^[0-9]+$ && "$A1" =~ ^[0-9]+$ && $((A1 - A0)) -eq $W ]]; then ok "$L +$W ($A0→$A1)"; else bad "$L want +$W ($A0→$A1)"; fi; }
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
  $C play "tp $B 0 64 96" 0.4 >/dev/null; sleep 4; settle "$B"; sleep 4
  $C play "gamemode survival $B" 0.4 >/dev/null; $T/b.sh chat "$B" "/dp leave" 5000 >/dev/null 2>&1 || true; sleep 8; }
quitall(){ for b in "$@"; do curl -sf "$BOTD/quit?name=$b" >/dev/null 2>&1 || true; done; sleep 2; }

grep -aq "Enabling CoreRpg v1.65.54" "$LOG" && ok "CoreRpg 1.65.54" || bad "CoreRpg 1.65.54"
grep -aq "\[CoreRpg\] \[storage\] MySQL connected" "$LOG" && ok "CoreRpg MySQL" || bad "CoreRpg MySQL"
grep -aq "\[CoreGacha\] \[db\] MySQL connected" "$LOG" && ok "CoreGacha MySQL" || bad "CoreGacha MySQL"
grep -aq "ember-v1-economy.yml loaded as amount() SoT (bv58)" "$LOG" && ok "E1 economy yml SoT bv58" || bad "E1 economy yml SoT bv58"
grep -aq "FAIL-CLOSED" "$LOG" && bad "economy FAIL-CLOSED present" || ok "no economy FAIL-CLOSED"

A=${BOT:-FreshQ790}; N0=$(wc -l < "$LOG")
join $A || exit 1
quitall $A; join $A || exit 1   # 2nd session: first-session papi parse quirk
I0=$(sigq $A q01); echo "  sigmark_q01 start = $I0"

# S07: Q01 first clear → fc_sigmark +3
run_q01 $A
tail -n +"$((N0+1))" "$LOG" | tr -d '\r' | grep -aqE "settle .*first clear" && ok "$A Q01 first clear settled" || bad "$A Q01 first clear settled"
I1=$(sigq $A q01); delta "S07 fc_sigmark q01" "$I0" "$I1" 3

# S08: Q01 repeat normal → sig_mark +1 (no pledge picked; featured-week rot_mark S11 may also pay a T1 mark)
N1=$(wc -l < "$LOG")
run_q01 $A
tail -n +"$((N1+1))" "$LOG" | tr -d '\r' | grep -aqE "\[P1 run\] q01-.* settle" && ok "$A Q01 repeat settled" || bad "$A Q01 repeat settled"
I2=$(sigq $A q01); delta "S08 sig_mark q01" "$I1" "$I2" 1

# S19: weekly goal badge (needs own Q07 flag) — admin goal hook → EmberSeason.addGoal → grantBadge S19
for q in q04 q07; do $C play "corerpg p1 runs firstclear $A $q" 0.35 >/dev/null; done
B0=$(num $A badges); echo "  badges start = $B0"
$C play "corerpg p1 runs season goal $A bounty 3" 0.6 >/dev/null
B1=$(num $A badges); delta "S19 weekly goal badge" "$B0" "$B1" 15

# S27: festival exchange 10 国庆币 → 2 余烬徽 (window open through 2026-10-08)
$C play "corerpg p1 fest reset $A" 0.5 >/dev/null
$C play "corerpg p1 fest coins $A 10" 0.5 >/dev/null
R=$($T/b.sh chat "$A" "/corerpg p1 fest buy badge 2" 4000 | tr -d '\r'); echo "  fest exchange: $(echo "$R" | head -c 300)"
sleep 2
B2=$(num $A badges); delta "S27 fest exchange badge" "$B1" "$B2" 2

# S18: echo_q01 claim → rush_sig_q01 +2
N2=$(wc -l < "$LOG")
R=$($T/b.sh chat "$A" "/corerpg p1 rush echo_q01 go" 10000 | tr -d '\r'); echo "  echo: $(echo "$R" | head -c 200)"
sleep 4
tail -n +"$((N2+1))" "$LOG" | grep -a "\[P1 run\] echo_q01-.* bound to dungeon_EmberQ0B2_" | tail -1 | grep -q . && ok "echo_q01 bound" || bad "echo_q01 bound"
$C play "gamemode creative $A" 0.4 >/dev/null; sleep 3
for i in $(seq 1 5); do $C play "corerpg p1 runs weaken $A" 0.3 >/dev/null; $T/fight.sh "$A" 10000 0 48 1500 >/dev/null 2>&1 || true; sleep 1
  weaken_n "$A" | grep -q '^-1$' && break; done
settle "$A"; sleep 4
tail -n +"$((N2+1))" "$LOG" | tr -d '\r' | grep -aqE "rush settle .* claim" && ok "echo_q01 rush settle claim" || bad "echo_q01 rush settle claim"
$T/b.sh chat "$A" "/dp leave" 5000 >/dev/null 2>&1 || true; sleep 5
I3=$(sigq $A q01); delta "S18 echo rush_sig_q01" "$I2" "$I3" 2

FULL=$(tail -n +"$((N0+1))" "$LOG" | tr -d '\r')
echo "$FULL" | grep -aE "economy grant(Insignia|Badge|Mark) refused" && bad "grant refused line" || ok "no grantInsignia/Badge/Mark refusal"
echo "$FULL" | grep -aE "untagged (mark|insignia) row .*$A" && bad "untagged row for $A" || ok "no untagged mark/insignia row"
echo "$FULL" | grep -aqE 'SEVERE' && bad "$A SEVERE" || ok "$A no-SEVERE"
quitall $A
echo "list=$(curl -sf $BOTD/list)"
echo "RESULT PASS=$PASS FAIL=$FAIL"
[[ "$FAIL" -eq 0 ]]
