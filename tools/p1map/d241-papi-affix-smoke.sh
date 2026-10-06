#!/usr/bin/env bash
# d241-papi-affix-smoke.sh — CoreRpg 1.65.67 D241 (ARCH S3-12).
#   Job A: %ember_daily_left% / %ember_weekly_left% now resolve (were literal): = stamina / daily cost, = weekly free credit.
#   Job B: affix behaviour primitives (behaviour-preserving) — Q01 regression + every affix forced once on a Q01 repeat run.
# Usage:  MODE=pre  F1=FreshQ828 tools/p1map/d241-papi-affix-smoke.sh   # before the jar swap: register F1 + baseline snapshot
#         MODE=post F1=FreshQ828 F2=FreshQ829 F3=FreshQ830 tools/p1map/d241-papi-affix-smoke.sh
#         MODE=affix F3=FreshQ831 AFFIXES="blazing shield" tools/p1map/d241-papi-affix-smoke.sh   # affix sweep only
# Bots FreshQ828+. Time-ticking keys excluded from diffs (as D240).
set -uo pipefail
M=/workspace/minecraft; C=$M/scripts/console.sh; BOTD=http://127.0.0.1:8765; T=$M/mineflayer-tests/tmp-p1
LOG=$M/server-runtime/logs/latest.log; SNAP=$M/tools/p1map/papi-snapshot.sh; OUT=${OUT:-/tmp/d241}; mkdir -p "$OUT"
MODE=${MODE:-post}; F1=${F1:-FreshQ828}; F2=${F2:-FreshQ829}; F3=${F3:-FreshQ830}; VER=${VER:-1.65.67}
AFFIXES=${AFFIXES:-"blazing split shield regen charge frost mortar molten venom jailer arcane firechain"}
PASS=0; FAIL=0; SOFT=0
ok(){ echo "PASS $1"; PASS=$((PASS+1)); }; bad(){ echo "FAIL $1"; FAIL=$((FAIL+1)); }; soft(){ echo "SOFT $1"; SOFT=$((SOFT+1)); }
TICK='^(corerpg_p1_online_min|corerpg_p1_online_next|corerpg_activity|corerpg_kit_charge|corerpg_skill_charge_ready)\b'
curl -sf "$BOTD/list" >/dev/null || { echo "FAIL botd"; exit 1; }
join(){ curl -sf "$BOTD/quit?name=$1" >/dev/null 2>&1 || true; sleep 1
  curl -sf -m 90 "$BOTD/join?name=$1" >/dev/null || { bad "$1 join"; return 1; }; sleep 8
  $C play "list" 0.6 | tr -d '\r' | grep -q "$1" && ok "$1 online" || { bad "$1 online"; return 1; }; }
papi_ok(){ for i in $(seq 1 10); do $C play "papi parse $1 %player_name%" 0.5 | tr -d '\r' | grep -q "Failed to find player" || return 0; sleep 2; done; return 1; }
quit(){ curl -sf "$BOTD/quit?name=$1" >/dev/null 2>&1 || true; sleep 2; }
papi(){ $C play "papi parse $1 $2" 0.6 | tr -d '\r' | grep -a "⟦" | tail -1; }
# value of ⟦k⟧ in a papi line
pv(){ echo "$1" | sed -n "s/.*⟦$2⟧\([^⟦]*\).*/\1/p" | sed 's/[[:space:]]*$//'; }
counts(){ papi "$1" "⟦st⟧%corerpg_stamina%⟦cost⟧%corerpg_stamina_cost_daily%⟦dl⟧%ember_daily_left%⟦cw⟧%corerpg_stamina_credit_weekly%⟦wl⟧%ember_weekly_left%⟦END⟧"; }

if [[ "$MODE" == pre ]]; then
  join "$F1" || exit 1; quit "$F1"                      # session 1 registers (PAPI needs a cached player)
  join "$F1" || exit 1; papi_ok "$F1" && ok "$F1 PAPI resolvable" || bad "$F1 PAPI resolvable"
  bash "$SNAP" "$F1" "$OUT/pre.tsv" >/dev/null
  L=$(counts "$F1"); echo "  pre: $L"
  [[ "$(pv "$L" dl)" == "%ember_daily_left%" && "$(pv "$L" wl)" == "%ember_weekly_left%" ]] && ok "pre: both keys literal (unprovided)" || bad "pre literal"
  quit "$F1"; echo "==== PRE PASS $PASS / FAIL $FAIL / SOFT $SOFT ===="; [[ "$FAIL" == 0 ]]; exit $?
fi

if [[ "$MODE" == post ]]; then
grep -aq "Enabling CoreRpg v$VER" "$LOG" && ok "CoreRpg $VER" || bad "CoreRpg $VER"
grep -aq "\[CoreRpg\] \[storage\] MySQL connected" "$LOG" && ok "CoreRpg MySQL" || bad "CoreRpg MySQL"
grep -aq "\[CoreGacha\] \[db\] MySQL connected" "$LOG" && ok "CoreGacha MySQL" || bad "CoreGacha MySQL"
grep -aq "ember-v1-economy.yml loaded as amount() SoT (bv58)" "$LOG" && ok "E1 SoT bv58" || bad "E1 SoT bv58"
grep -aq "FAIL-CLOSED" "$LOG" && bad "FAIL-CLOSED" || ok "no FAIL-CLOSED"
grep -aq "PAPI expansion registered: ember (ladder)" "$LOG" && ok "PAPI registered" || bad "PAPI registered"

# ---- A1: F1 across the jar swap — only the two keys change (literal → number)
if [[ -s "$OUT/pre.tsv" ]]; then
  join "$F1" && papi_ok "$F1" && {
    bash "$SNAP" "$F1" "$OUT/post.tsv" >/dev/null
    D=$(diff <(grep -vE "$TICK" "$OUT/pre.tsv") <(grep -vE "$TICK" "$OUT/post.tsv") | grep -E '^[<>]' | cut -c3- | cut -f1 | sort -u | tr '\n' ' ')
    echo "  changed keys: $D"
    [[ "$D" == "ember_daily_left ember_weekly_left " ]] && ok "$F1 cross-jar: only the 2 keys changed ($(grep -vcE "$TICK" "$OUT/post.tsv") keys)" || bad "$F1 cross-jar diff: $D"
    L=$(counts "$F1"); echo "  post: $L"
    ST=$(pv "$L" st); CO=$(pv "$L" cost); DL=$(pv "$L" dl); CW=$(pv "$L" cw); WL=$(pv "$L" wl)
    [[ "$DL" =~ ^[0-9]+$ && "$DL" == "$((ST / CO))" ]] && ok "$F1 daily_left=$DL = $ST/$CO" || bad "$F1 daily_left=$DL (st $ST cost $CO)"
    [[ "$WL" =~ ^[0-9]+$ && "$WL" == "$CW" ]] && ok "$F1 weekly_left=$WL = credit_weekly" || bad "$F1 weekly_left=$WL credit $CW"
    $C play "corerpg stamina set $F1 59" 0.5 >/dev/null; L=$(counts "$F1")
    [[ "$(pv "$L" dl)" == 1 ]] && ok "$F1 stamina 59 → daily_left 1" || bad "$F1 stamina 59 → $(pv "$L" dl)"
    $C play "corerpg stamina set $F1 29" 0.5 >/dev/null; L=$(counts "$F1")
    [[ "$(pv "$L" dl)" == 0 ]] && ok "$F1 stamina 29 → daily_left 0" || bad "$F1 stamina 29 → $(pv "$L" dl)"
    $C play "corerpg stamina set $F1 200" 0.5 >/dev/null
    quit "$F1"; }
else soft "no pre-deploy baseline"; fi

# ---- A2: fresh bot F2 — 2nd-session PAPI for the two keys, 2nd→3rd session identical
join "$F2" || exit 1; quit "$F2"
join "$F2" || exit 1
papi_ok "$F2" && ok "$F2 PAPI resolvable (2nd session)" || bad "$F2 PAPI resolvable (2nd session)"
L=$(counts "$F2"); echo "  $F2 s2: $L"
[[ "$(pv "$L" dl)" == 3 ]] && ok "$F2 fresh daily_left 3 (90/30)" || bad "$F2 daily_left $(pv "$L" dl)"
[[ "$(pv "$L" wl)" == 1 ]] && ok "$F2 fresh weekly_left 1" || bad "$F2 weekly_left $(pv "$L" wl)"
bash "$SNAP" "$F2" "$OUT/s2.tsv" >/dev/null; quit "$F2"
join "$F2" || exit 1; papi_ok "$F2" || true
bash "$SNAP" "$F2" "$OUT/s3.tsv" >/dev/null; quit "$F2"
D=$(diff <(grep -vE "$TICK" "$OUT/s2.tsv") <(grep -vE "$TICK" "$OUT/s3.tsv"))
[[ -z "$D" ]] && ok "$F2 session2→3 PAPI identical ($(grep -vcE "$TICK" "$OUT/s3.tsv") keys)" || { bad "$F2 session2→3"; echo "$D" | head; }
U=$(grep -P '\t%(corerpg|ember)_' "$OUT/s3.tsv" | cut -f1 | tr '\n' ' '); echo "  unresolved keys: $U"
echo "$U" | grep -q "ember_daily_left\|ember_weekly_left" && bad "daily/weekly_left still unresolved" || ok "daily/weekly_left resolved"
fi

# ---- B: Q01 regression + every affix forced once (F3)
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
q01(){ local B=$1 LINGER=${2:-0}
  $C play "corerpg stamina set $B 200" 0.4 >/dev/null
  $C play "gamemode creative $B" 0.3 >/dev/null
  # right after a settle the bot may still stand in the instance being torn down → enter is ignored; retry
  local e R; for e in 1 2 3 4; do
    R=$($T/b.sh chat "$B" "/corerpg p1 enter q01" 12000 | tr -d '\r'); sleep 6
    echo "$R" | grep -aq "正在创建实例" && break; [[ $e == 4 ]] && return 2; sleep 10; done
  $C play "tp $B 0 64 28" 0.3 >/dev/null; sleep 2
  if [[ "$LINGER" -gt 0 ]]; then   # let the r1 affix elite act on a living target (survival + heals)
    $C play "gamemode survival $B" 0.3 >/dev/null
    for h in $(seq 1 "$LINGER"); do $C play "corerpg p1 heal $B" 0.2 >/dev/null || true; sleep 0.8; done
    $C play "gamemode creative $B" 0.3 >/dev/null; fi
  clear_room "$B" || return 3
  $C play "tp $B 30 64 38" 0.3 >/dev/null; sleep 2; clear_room "$B" || return 4
  $C play "tp $B 32 64 68" 0.3 >/dev/null; sleep 2; clear_room "$B" || return 5
  $C play "tp $B 0 64 102" 0.3 >/dev/null; sleep 2; settle "$B"; sleep 3
  $C play "gamemode survival $B" 0.3 >/dev/null; return 0; }

join "$F3" || exit 1
$T/b.sh chat "$F3" "/dungeon-team disband" 800 >/dev/null || true
if [[ "$MODE" == post ]]; then
N0=$(wc -l < "$LOG"); q01 "$F3" 0; RC=$?
FULL=$(tail -n +"$((N0+1))" "$LOG" | tr -d '\r')
[[ "$RC" == 0 ]] && ok "Q01 enter + 3 rooms" || bad "Q01 path rc=$RC"
echo "$FULL" | grep -aqE "\[P1 run\] q01-.* settle" && ok "Q01 settle" || bad "Q01 settle"
echo "$FULL" | grep -aE "economy grant(Coin|Mat|Xp|Mark|Badge) refused" && bad "grant refused" || ok "no grant refusal q01"
fi
$C play "corerpg p1 runs firstclear $F3 q01" 0.6 >/dev/null
for A in $AFFIXES; do
  $C play "corerpg p1 runs variety $A:r1" 0.8 >/dev/null
  N0=$(wc -l < "$LOG"); q01 "$F3" 10; RC=$?
  FULL=$(tail -n +"$((N0+1))" "$LOG" | tr -d '\r')
  CH=$($T/b.sh eval "$F3" "return bot.chatLog.slice(-60).map(s=>s.replace(/§./g,'')).join('\n')" 2>/dev/null || true)
  echo "$FULL" | grep -aqE "affix $A on " && ok "affix $A promoted" || bad "affix $A promoted (rc=$RC)"
  echo "$FULL" | grep -aqE "affix $A done" && ok "affix $A defeated" || soft "affix $A done not logged (rc=$RC)"
  case $A in
    venom) echo "$FULL" | grep -aqE 'venom [+x] hit=' && ok "venom cast" || soft "venom no cast seen";;
    jailer) echo "$FULL" | grep -aqE 'jailer rooted=' && ok "jailer cast" || soft "jailer no cast seen";;
    arcane) echo "$FULL" | grep -aqE 'arcane c?cw hit=' && ok "arcane cast" || soft "arcane no cast seen";;
    firechain) echo "$FULL" | grep -aqE 'firechain link' && ok "firechain link" || soft "firechain no link seen";;
    molten) echo "$FULL" | grep -aqE 'molten scheduled' && ok "molten scheduled" || soft "molten not scheduled";;
    split) echo "$CH" | grep -aq '「分裂」' && ok "split adds" || soft "split chat not seen";;
    regen) echo "$CH" | grep -aqE '「再生」' && ok "regen channel" || soft "regen chat not seen";;
  esac
  echo "$FULL" | grep -aqE "\[P1 run\] q01-.* settle" && ok "affix $A run settled" || soft "affix $A settle not seen (rc=$RC)"
  echo "$FULL" | grep -aqE 'SEVERE|Exception' && bad "affix $A SEVERE/Exception" || true
done
quit "$F3"

BOOT=$(rg -n "Enabling CoreRpg v$VER" "$LOG" | tail -1 | cut -d: -f1)
POST=$(tail -n +"$BOOT" "$LOG" | rg -c "SEVERE" || true); POST=${POST:-0}
[[ "$POST" == "0" ]] && ok "SEVERE 0 (post-enable)" || bad "SEVERE post-enable=$POST"
echo "==== RESULT PASS $PASS / FAIL $FAIL / SOFT $SOFT ===="
[[ "$FAIL" == "0" ]]
