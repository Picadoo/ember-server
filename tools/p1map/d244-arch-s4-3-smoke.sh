#!/usr/bin/env bash
# d244-arch-s4-3-smoke.sh — CoreRpg 1.65.69 D244 (ARCH S4-3): affix export table (sim side, plugin behaviour unchanged)
# + G10 item-level register rows S36/S37/S38/C19 (tags only, nothing routes to them, no amount change).
#   boot: version, MySQL x2, E1 SoT bv58, no FAIL-CLOSED, bundled ember-v1*.yml = live, bundled runs.yml variety = affix-table.json inputs.
#   F1 fresh: starter kit (2 T0 + 5 potions, no refusal) → Q01 regression (settle) → firstclear → forced affix repeat runs
#             (AFFIXES, default "venom arcane"; their cadence is what changed in p1sim) promote + cast + settle → variety clear.
#   F2 fresh: CoreGacha welcome ticket path untouched (join, no refusal) — S37 is a tag, not a route.
# Usage:  F1=FreshQ833 F2=FreshQ834 tools/p1map/d244-arch-s4-3-smoke.sh      (bots FreshQ833+)
#         PART="affix" F1=FreshQ833 …   # parts: boot starter q01 affix gacha (default all)
set -uo pipefail
M=/workspace/minecraft; C=$M/scripts/console.sh; BOTD=http://127.0.0.1:8765; T=$M/mineflayer-tests/tmp-p1
LOG=$M/server-runtime/logs/latest.log; VER=${VER:-1.65.69}
F1=${F1:-FreshQ833}; F2=${F2:-FreshQ834}; AFFIXES=${AFFIXES:-"venom arcane"}
PASS=0; FAIL=0; SOFT=0; N_START=$(wc -l < "$LOG"); PART=${PART:-"boot starter q01 affix gacha"}
has(){ [[ " $PART " == *" $1 "* ]]; }
ok(){ echo "PASS $1"; PASS=$((PASS+1)); }; bad(){ echo "FAIL $1"; FAIL=$((FAIL+1)); }; soft(){ echo "SOFT $1"; SOFT=$((SOFT+1)); }
sql(){ sudo -n mysql -N -B ember -e "$1" 2>/dev/null; }
uuid(){ sql "SELECT uuid FROM cr_players WHERE name='$1'" | head -1; }
evalb(){ curl -sf -m 60 -X POST "$BOTD/eval?name=$1" --data "$2" | jq -r '.r // .err // ""'; }
chat(){ evalb "$1" "const f=bot.chatLog.length; bot.chat($(jq -Rn --arg s "$2" '$s')); await wait(${3:-1500}); return bot.chatLog.slice(f).map(s=>s.replace(/\u00a7./g,'')).join('\n');"; }
join(){ curl -sf "$BOTD/quit?name=$1" >/dev/null 2>&1 || true; sleep 1
  curl -sf -m 90 "$BOTD/join?name=$1" >/dev/null || { bad "$1 join"; return 1; }; sleep 8
  $C play "list" 0.6 | tr -d '\r' | grep -q "$1" && ok "$1 online" || { bad "$1 online"; return 1; }; }
quit(){ curl -sf "$BOTD/quit?name=$1" >/dev/null 2>&1 || true; sleep 2; }
papi(){ $C play "papi parse $1 $2" 0.6 | tr -d '\r' | grep -a "⟦" | tail -1 | sed 's/.*⟦v⟧//; s/⟦END⟧.*//'; }
coin(){ local v; for i in 1 2 3 4 5; do v=$(papi "$1" "⟦v⟧%corerpg_coin%⟦END⟧"); [[ "$v" =~ ^[0-9]+$ ]] && { echo "$v"; return; }; sleep 2; done; echo "?"; }
# codex entries = distinct p1_codex_<family>_<slot>_t<n>@all counters in the saved player row (stage claims excluded)
codexn(){ local U; U=$(uuid "$1"); sql "SELECT data FROM cr_players WHERE uuid='$U'" | grep -oE 'p1_codex_[a-z]+_(blade|charm)_t[0-9]@all: [1-9]' | sort -u | wc -l; }
# loss/dup: every active cr_p1_item row of the owner sits in exactly one inventory slot, and no P1 stack is unknown to the DB
consist(){ local B=$1 U; U=$(uuid "$B")
  local IDS; IDS=$(sql "SELECT item_uid FROM cr_p1_item WHERE owner_uuid='$U' AND state='active'" | tr '\n' ' ')
  evalb "$B" "const ids='$IDS'.trim().split(/\s+/).filter(Boolean); const s=bot.inventory.slots.filter(Boolean).map(i=>JSON.stringify(i.nbt||'')); const p1=s.filter(x=>x.includes('ember_v1')).length; const bad=ids.filter(u=>s.filter(x=>x.includes(u)).length!==1); return ids.length+' rows · '+p1+' P1 stacks · off '+bad.length;"; }
consist_ok(){ local R; R=$(consist "$1"); echo "  [$2] $R"
  [[ "$R" =~ ^([0-9]+)\ rows\ ·\ ([0-9]+)\ P1\ stacks\ ·\ off\ 0$ ]] && [[ "${BASH_REMATCH[1]}" == "${BASH_REMATCH[2]}" ]] && ok "$1 $2: inventory = DB ($R)" || bad "$1 $2: inventory ≠ DB ($R)"; }

if has boot; then
grep -aq "Enabling CoreRpg v$VER" "$LOG" && ok "CoreRpg $VER" || bad "CoreRpg $VER"
grep -aq "\[CoreRpg\] \[storage\] MySQL connected" "$LOG" && ok "CoreRpg MySQL" || bad "CoreRpg MySQL"
grep -aq "\[CoreGacha\] \[db\] MySQL connected" "$LOG" && ok "CoreGacha MySQL" || bad "CoreGacha MySQL"
grep -aq "ember-v1-economy.yml loaded as amount() SoT (bv58)" "$LOG" && ok "E1 SoT bv58 (goldens match yml; S36–S38/C19 carry no golden)" || bad "E1 SoT bv58"
grep -aq "FAIL-CLOSED" "$LOG" && bad "FAIL-CLOSED" || ok "no FAIL-CLOSED"
for f in ember-v1.yml ember-v1-runs.yml ember-v1-economy.yml ember-v1-growth.yml ember-v1-festival.yml; do
  cmp -s "$M/plugins/CoreRpg/$f" <(unzip -p "$M/plugins/CoreRpg.jar" "$f") && ok "bundled $f = live" || bad "bundled $f ≠ live"; done
DR=$(cd "$M/tools/p1sim" && python3 -c "import rules,affixtable; print(rules.affix_table_drift(affixtable.table(), rules.data('runs')) or 'ok')" 2>&1 | tail -1)
[[ "$DR" == ok ]] && ok "affix-table.json inputs = live runs.yml variety" || bad "affix table drift: $DR"
fi
curl -sf "$BOTD/list" >/dev/null || { bad "botd"; exit 1; }

# ---------------------------------------------------------------- Q01 helpers (as d241/d243)
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

# ---------------------------------------------------------------- F1: starter kit + Q01 + forced affixes
if has starter; then
[[ -z "$(uuid "$F1")" ]] && ok "$F1 is a fresh name" || bad "$F1 already exists"
N0=$(wc -l < "$LOG"); join "$F1" || exit 1; sleep 6
L=$(tail -n +"$((N0+1))" "$LOG" | tr -d '\r')
echo "$L" | grep -aq "\[P1 supply\] starter $F1 heal potions 5/5" && ok "$F1 starter potions 5/5 (S35.potions)" || bad "$F1 starter potions"
U1=$(uuid "$F1")
SI=$(sql "SELECT CONCAT(slot,'/t',tier,'/',source) FROM cr_p1_item WHERE owner_uuid='$U1' AND state='active' ORDER BY slot" | tr '\n' ' '); echo "  items: $SI"
[[ $(echo "$SI" | grep -oE "(blade|charm)/t0/" | wc -l) == 2 ]] && ok "$F1 2 T0 starter pieces active" || bad "$F1 starter pieces ($SI)"
echo "$L" | grep -aE "economy grant[A-Za-z]* refused" && bad "$F1 grant refused at join" || ok "$F1 no grant refusal at join"
quit "$F1"
fi
if has q01 || has affix; then
join "$F1" || exit 1; U1=$(uuid "$F1")
$T/b.sh chat "$F1" "/dungeon-team disband" 800 >/dev/null || true
if has q01; then
QN0=$(wc -l < "$LOG")
q01 "$F1"; RC=$?; FULL=$(tail -n +"$((QN0+1))" "$LOG" | tr -d '\r')
[[ "$RC" == 0 ]] && ok "Q01 enter + 3 rooms" || bad "Q01 path rc=$RC"
echo "$FULL" | grep -aqE "\[P1 run\] q01-.* settle" && ok "Q01 settle (regression)" || bad "Q01 settle"
echo "$FULL" | grep -aE "economy grant[A-Za-z]* refused" && bad "Q01 grant refused" || ok "no grant refusal Q01"
fi
if has affix; then
$C play "corerpg p1 runs firstclear $F1 q01" 0.6 >/dev/null
for A in $AFFIXES; do
  $C play "corerpg p1 runs variety $A:r1" 0.8 >/dev/null
  N0=$(wc -l < "$LOG"); q01 "$F1" 14; RC=$?
  FULL=$(tail -n +"$((N0+1))" "$LOG" | tr -d '\r')
  echo "$FULL" | grep -aqE "affix $A on " && ok "affix $A promoted" || bad "affix $A promoted (rc=$RC)"
  echo "$FULL" | grep -aqE "affix $A done" && ok "affix $A defeated" || soft "affix $A done not logged (rc=$RC)"
  case $A in
    venom) echo "$FULL" | grep -aqE 'venom [+x] hit=' && ok "venom cast" || soft "venom no cast seen";;
    jailer) echo "$FULL" | grep -aqE 'jailer rooted=' && ok "jailer cast" || soft "jailer no cast seen";;
    arcane) echo "$FULL" | grep -aqE 'arcane c?cw hit=' && ok "arcane cast" || soft "arcane no cast seen";;
    firechain) echo "$FULL" | grep -aqE 'firechain link' && ok "firechain link" || soft "firechain no link seen";;
  esac
  echo "$FULL" | grep -aqE "\[P1 run\] q01-.* settle" && ok "affix $A run settled" || soft "affix $A settle not seen (rc=$RC)"
  echo "$FULL" | grep -aqE 'SEVERE|Exception' && bad "affix $A SEVERE/Exception" || true
done
$C play "corerpg p1 runs variety clear" 0.8 >/dev/null && ok "variety override cleared" || soft "variety clear"
fi
quit "$F1"
fi

# ---------------------------------------------------------------- F2: CoreGacha welcome path untouched (S37 = tag only)
if has gacha; then
[[ -z "$(uuid "$F2")" ]] && ok "$F2 is a fresh name" || bad "$F2 already exists"
N0=$(wc -l < "$LOG"); join "$F2" || exit 1; sleep 6
L=$(tail -n +"$((N0+1))" "$LOG" | tr -d '\r')
echo "$L" | grep -a "CoreGacha" | tail -3 | cut -c1-200
echo "$L" | grep -aE "economy grant[A-Za-z]* refused" && bad "$F2 grant refused at join" || ok "$F2 no grant refusal at join"
echo "$L" | grep -aqE "\[CoreGacha\].*(SEVERE|Exception)" && bad "$F2 CoreGacha error" || ok "$F2 CoreGacha join clean"
quit "$F2"
fi

ALL=$(tail -n +"$((N_START+1))" "$LOG" | tr -d '\r')
echo "$ALL" | grep -aE "economy grant[A-Za-z]* refused" && bad "grant refusal during smoke" || ok "no grant refusal during smoke"
BOOT=$(grep -an "Enabling CoreRpg v$VER" "$LOG" | tail -1 | cut -d: -f1)
POST=$(tail -n +"$BOOT" "$LOG" | grep -ac "SEVERE" || true); POST=${POST:-0}
[[ "$POST" == "0" ]] && ok "SEVERE 0 (post-enable)" || bad "SEVERE post-enable=$POST"
echo "==== RESULT PASS $PASS / FAIL $FAIL / SOFT $SOFT ===="
[[ "$FAIL" == "0" ]]
