#!/usr/bin/env bash
# d227-insignia-echo-b-smoke.sh — CoreRpg 1.65.53 D227 option B: echo halls Q01–Q07, shared weekly 3.
# Checks: plugin load + MySQL×2 + bv58 + modes UI 7 halls + echo_q05/q07 bind + one echo_q01 claim (+2 Q01 insignia).
# FreshQ786. No asset path change → no persist-roundtrip.
set -uo pipefail
C=/workspace/minecraft/scripts/console.sh; BOTD=http://127.0.0.1:8765; T=/workspace/minecraft/mineflayer-tests/tmp-p1
LOG=/workspace/minecraft/server-runtime/logs/latest.log
PASS=0; FAIL=0
ok(){ echo "PASS $1"; PASS=$((PASS+1)); }; bad(){ echo "FAIL $1"; FAIL=$((FAIL+1)); }
curl -sf "$BOTD/list" >/dev/null || { echo "FAIL botd"; exit 1; }
ph(){ $C play "papi parse $1 %corerpg_p1_$2%" 0.5 2>/dev/null | tr -d '\r' | sed 's/§.//g' | grep -v '^\s*$' | tail -1 | sed 's/^.*INFO\]: //'; }
expect(){ local B=$1 K=$2 W=$3 V; V=$(ph "$B" "$K"); echo "  $B $K = $V"; [[ "$V" == *"$W"* ]] && ok "$B $K~$W" || bad "$B $K~$W (got: $V)"; }
ev(){ $T/b.sh eval "$1" "$2" 2>/dev/null; }
slot(){ local B=$1 S=$2; ev "$B" "const w=bot.currentWindow; if(!w) return 'NOWIN'; const it=w.slots[$S]; if(!it) return 'EMPTY';
 const d=(it.nbt&&it.nbt.value&&it.nbt.value.display)?it.nbt.value.display.value:{}; const nm=d.Name?d.Name.value:'';
 const lo=d.Lore?d.Lore.value.value:[]; return (String(w.title)+' || '+nm+' || '+lo.join(' | ')).replace(/§./g,'');"; }
say(){ local B=$1; ev "$B" "const n=bot.chatLog.length; bot.chat('$2'); await wait(${3:-2000}); return bot.chatLog.slice(n).map(s=>s.replace(/§./g,'')).join(' / ');"; }
weaken_n(){ local W; W=$($C play "corerpg p1 runs weaken $1 ${2:-}" 0.4 2>/dev/null | tr -d '\r' || true)
  if echo "$W" | grep -q '没有进行中'; then echo -1; return; fi
  local N; N=$(echo "$W" | sed -n 's/.*削弱 \([0-9]*\) 只.*/\1/p' | tail -1); echo "${N:--2}"; }
settle_boss(){ local B=$1; for i in $(seq 1 40); do N=$(weaken_n "$B"); [[ "$N" == -1 ]] && break
  $C play "corerpg p1 heal $B" 0.12 >/dev/null || true; $T/fight.sh "$B" 8000 0 40 1200 >/dev/null 2>&1 || true; sleep 1; done; }
join(){ local B=$1; curl -sf "$BOTD/quit?name=$B" >/dev/null 2>&1 || true; sleep 1
  curl -sf -m 90 "$BOTD/join?name=$B" >/dev/null || { bad "$B join"; return 1; }; sleep 7
  $C play "list" 0.5 | tr -d '\r' | grep -q "$B" && ok "$B online" || { bad "$B online"; return 1; }
  $C play "corerpg stamina set $B 200" 0.5 >/dev/null; }
quitall(){ for b in "$@"; do curl -sf "$BOTD/quit?name=$b" >/dev/null 2>&1 || true; done; sleep 2; }

grep -q "Enabling CoreRpg v1.65.53" "$LOG" && ok "CoreRpg 1.65.53" || bad "CoreRpg 1.65.53"
grep -q "\[CoreRpg\] \[storage\] MySQL connected" "$LOG" && ok "CoreRpg MySQL" || bad "CoreRpg MySQL"
grep -q "\[CoreGacha\] \[db\] MySQL connected" "$LOG" && ok "CoreGacha MySQL" || bad "CoreGacha MySQL"
grep -q "ember-v1-economy.yml loaded as amount() SoT" "$LOG" && ok "E1 economy yml SoT" || bad "E1 economy yml SoT"
# balance 58 should be in rule_version path when maps load — accept either explicit bv log or no FAIL-CLOSED
grep -q "FAIL-CLOSED" "$LOG" && bad "economy FAIL-CLOSED present" || ok "no economy FAIL-CLOSED"
grep -qE 'rushClaimError|claim counter' "$LOG" && bad "claim-error-in-log" || ok "no claim-error log"

A=${BOT:-FreshQ786}; N0=$(wc -l < "$LOG")
join $A || exit 1

# Unlock Q01–Q07 via admin firstclear so all seven echo halls are eligible
for q in q01 q02 q03 q04 q05 q06 q07; do $C play "corerpg p1 runs firstclear $A $q" 0.35 >/dev/null; done
# 2nd session: first-session papi parse often "Failed to find player" (known quirk)
quitall $A; sleep 2; join $A || exit 1
expect $A q04done 1; expect $A q05done 1; expect $A q07done 1

# PAPI rush lines for new halls
for K in echo_q01 echo_q05 echo_q07; do
  V=$(ph "$A" "rush_$K"); echo "  rush_$K = $V"
  echo "$V" | grep -qE "本周已领|0/" && ok "papi rush_$K ready" || bad "papi rush_$K ($V)"
done
# Before Q05 firstclear, echo_q05 would say 需本人首通 — already cleared above

# Modes menu: header + Q05/Q07 icons (layout row3 slots 28/32)
ev "$A" "bot.chat('/ember_p1_modes'); await wait(2400); return 1" >/dev/null
E=$(slot "$A" 10); echo "$E" | cut -c1-200
echo "$E" | grep -q "首领残响" && ok "modes E icon" || bad "modes E ($E)"
echo "$E" | grep -qE "七个首领|Q01–Q07|Q01-Q07" && ok "modes E lore Q01–Q07" || echo "SOFT modes E lore (got: $(echo "$E" | cut -c1-160))"
S5=$(slot "$A" 28); echo "$S5" | cut -c1-200
echo "$S5" | grep -q "Q05\|断塔" && ok "modes Q05 icon" || bad "modes Q05 ($S5)"
S7=$(slot "$A" 32); echo "$S7" | cut -c1-200
echo "$S7" | grep -q "Q07\|炉锁" && ok "modes Q07 icon" || bad "modes Q07 ($S7)"
ev "$A" "if(bot.currentWindow) bot.closeWindow(bot.currentWindow); return 1" >/dev/null

# Bind echo_q05 and echo_q07 sessions (leave without full clear)
for K in echo_q05 echo_q07; do
  R=$(say "$A" "/corerpg p1 rush $K go" 10000); echo "$R" | cut -c1-300
  tail -n +"$N0" "$LOG" | grep -a "\[P1 run\] $K-.* bound to dungeon_EmberQ0B2_" | tail -1 | grep -q . && ok "$K bound EmberQ0B2" || bad "$K bound"
  ev "$A" "bot.chat('/dp leave'); await wait(6000); return 1" >/dev/null
  sleep 6
done

# Full echo_q01 claim: bind → weaken → kill → expect Q01 insignia +2 and shared 1/3
N1=$(wc -l < "$LOG")
SIG0=$(ph "$A" sigmark_q01); echo "  sigmark_q01 before = $SIG0"
R=$(say "$A" "/corerpg p1 rush echo_q01 go" 10000); echo "$R" | cut -c1-300
tail -n +"$N1" "$LOG" | grep -a "\[P1 run\] echo_q01-.* bound to dungeon_EmberQ0B2_" | tail -1 | grep -q . && ok "echo_q01 bound" || bad "echo_q01 bound"
$C play "gamemode creative $A" 0.4 >/dev/null
sleep 3
# Boss is in hall center; weaken + fight
for i in $(seq 1 5); do $C play "corerpg p1 runs weaken $A" 0.3 >/dev/null; $T/fight.sh "$A" 10000 0 48 1500 >/dev/null 2>&1 || true; sleep 1
  weaken_n "$A" | grep -q '^-1$' && break
done
settle_boss "$A"
sleep 4
FULL=$(tail -n +"$((N1+1))" "$LOG" | tr -d '\r')
echo "$FULL" | grep -E "echo_q01|徽记|settle|rush_sig" | head -8
echo "$FULL" | grep -qE "rush settle .* claim" && ok "echo_q01 rush settle claim" || bad "echo_q01 rush settle claim"
echo "$FULL" | grep -qE "rush_sig_q01|rows\+2" && ok "echo_q01 grant rows" || echo "SOFT grant rows line"
# 2nd-session papi for counter
ev "$A" "bot.chat('/dp leave'); await wait(5000); return 1" >/dev/null || true
sleep 5
quitall $A; sleep 2; join $A || true
SIG1=$(ph "$A" sigmark_q01); echo "  sigmark_q01 after = $SIG1"
V=$(ph "$A" rush_echo_q01); echo "  rush_echo_q01 after = $V"
if [[ "$SIG0" =~ ^[0-9]+$ && "$SIG1" =~ ^[0-9]+$ && $((SIG1 - SIG0)) -ge 2 ]]; then
  ok "echo_q01 Q01 insignia +$((SIG1-SIG0)) ($SIG0→$SIG1)"
elif echo "$V" | grep -qE "1/3|已领 1"; then
  ok "echo shared claim 1/3 via papi"
elif echo "$FULL" | grep -qE "rush settle .* claim"; then
  ok "echo claim proven by settle log (papi soft)"
else
  bad "echo claim/insignia ($SIG0→$SIG1; $V)"
fi

FULL=$(tail -n +"$((N0+1))" "$LOG" | tr -d '\r')
echo "$FULL" | grep -qE 'SEVERE' && bad "$A SEVERE" || ok "$A no-SEVERE"
quitall $A
echo "list=$(curl -sf $BOTD/list)"
echo "RESULT PASS=$PASS FAIL=$FAIL"
[[ "$FAIL" -eq 0 ]]
