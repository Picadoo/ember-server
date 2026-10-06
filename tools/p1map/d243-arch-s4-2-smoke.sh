#!/usr/bin/env bash
# d243-arch-s4-2-smoke.sh — CoreRpg 1.65.68 D243 (ARCH S4-2): S33 codex stage coin / S34 chest extra item / S35 starter kit
# registered (tags only), S09 per_rule key, givedup src admin (not dismantlable), bundled ember-v1*.yml = live.
#   F1 fresh: starter kit (2 T0 pieces + 5 potions, no refusal) → Q01 regression (settle) → repeat Q01 until codex ≥ 5
#             → /corerpg p1 codex claim pays exactly +200 (tagged, no refusal) → second claim pays nothing.
#   F2 fresh: T1 blade in hand → givedup → DB source admin, dismantle refused, piece kept → reroll eats it as duplicate
#             → inventory ↔ cr_p1_item active rows 1:1 before / after every step (no loss, no duplication).
# Usage:  F1=FreshQ831 F2=FreshQ832 tools/p1map/d243-arch-s4-2-smoke.sh      (bots FreshQ831+)
#         PART="codex" F1=FreshQ831 …   # parts: boot starter q01 codex dup (default all)
set -uo pipefail
M=/workspace/minecraft; C=$M/scripts/console.sh; BOTD=http://127.0.0.1:8765; T=$M/mineflayer-tests/tmp-p1
LOG=$M/server-runtime/logs/latest.log; VER=${VER:-1.65.68}
F1=${F1:-FreshQ831}; F2=${F2:-FreshQ832}; MAXQ=${MAXQ:-6}
PASS=0; FAIL=0; SOFT=0; N_START=$(wc -l < "$LOG"); PART=${PART:-"boot starter q01 codex dup"}
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
grep -aq "ember-v1-economy.yml loaded as amount() SoT (bv58)" "$LOG" && ok "E1 SoT bv58 (S33–S35 + S09.per_rule goldens match yml)" || bad "E1 SoT bv58"
grep -aq "FAIL-CLOSED" "$LOG" && bad "FAIL-CLOSED" || ok "no FAIL-CLOSED"
for f in ember-v1.yml ember-v1-runs.yml ember-v1-economy.yml ember-v1-growth.yml ember-v1-festival.yml; do
  cmp -s "$M/plugins/CoreRpg/$f" <(unzip -p "$M/plugins/CoreRpg.jar" "$f") && ok "bundled $f = live" || bad "bundled $f ≠ live"; done
fi
curl -sf "$BOTD/list" >/dev/null || { bad "botd"; exit 1; }

# ---------------------------------------------------------------- Q01 helpers (as d241)
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
q01(){ local B=$1
  $C play "corerpg stamina set $B 200" 0.4 >/dev/null
  $C play "gamemode creative $B" 0.3 >/dev/null
  local e R; for e in 1 2 3 4; do
    R=$($T/b.sh chat "$B" "/corerpg p1 enter q01" 12000 | tr -d '\r'); sleep 6
    echo "$R" | grep -aq "正在创建实例" && break; [[ $e == 4 ]] && return 2; sleep 10; done
  $C play "tp $B 0 64 28" 0.3 >/dev/null; sleep 2
  clear_room "$B" || return 3
  $C play "tp $B 30 64 38" 0.3 >/dev/null; sleep 2; clear_room "$B" || return 4
  $C play "tp $B 32 64 68" 0.3 >/dev/null; sleep 2; clear_room "$B" || return 5
  $C play "tp $B 0 64 102" 0.3 >/dev/null; sleep 2; settle "$B"; sleep 3
  $C play "gamemode survival $B" 0.3 >/dev/null; return 0; }

# ---------------------------------------------------------------- F1: starter kit + Q01 + codex coin
if has starter; then
[[ -z "$(uuid "$F1")" ]] && ok "$F1 is a fresh name" || bad "$F1 already exists"
N0=$(wc -l < "$LOG"); join "$F1" || exit 1; sleep 6
L=$(tail -n +"$((N0+1))" "$LOG" | tr -d '\r')
echo "$L" | grep -aq "\[P1 supply\] starter $F1 heal potions 5/5" && ok "$F1 starter potions 5/5 (S35.potions)" || bad "$F1 starter potions"
U1=$(uuid "$F1")
ST=$(sql "SELECT reward_key,status FROM cr_p1_reward WHERE player_uuid='$U1' AND run_id='starter' ORDER BY reward_key" | tr '\t\n' ': '); echo "  starter ledger: $ST"
[[ $(echo "$ST" | grep -o "starter_blade\|starter_charm" | sort -u | wc -l) == 2 ]] && ok "$F1 starter ledger rows blade + charm" || bad "$F1 starter ledger ($ST)"
SI=$(sql "SELECT CONCAT(slot,'/t',tier,'/',source) FROM cr_p1_item WHERE owner_uuid='$U1' AND state='active' ORDER BY slot" | tr '\n' ' '); echo "  items: $SI"
[[ $(echo "$SI" | grep -oE "(blade|charm)/t0/" | wc -l) == 2 ]] && ok "$F1 2 T0 starter pieces active" || bad "$F1 starter pieces ($SI)"
echo "$L" | grep -aE "economy grant[A-Za-z]* refused" && bad "$F1 grant refused at join" || ok "$F1 no grant refusal at join"
consist_ok "$F1" "after starter"
quit "$F1"
fi
if has q01 || has codex; then
join "$F1" || exit 1; U1=$(uuid "$F1")          # 2nd session: PAPI-cached
C0=$(coin "$F1"); X0=$(codexn "$F1"); echo "  $F1 coin $C0 · codex $X0"
X=$X0
if has q01; then
QN0=$(wc -l < "$LOG")
q01 "$F1"; RC=$?; FULL=$(tail -n +"$((QN0+1))" "$LOG" | tr -d '\r')
[[ "$RC" == 0 ]] && ok "Q01 enter + 3 rooms" || bad "Q01 path rc=$RC"
echo "$FULL" | grep -aqE "\[P1 run\] q01-.* settle" && ok "Q01 settle (regression)" || bad "Q01 settle"
X=$(codexn "$F1"); echo "  codex after Q01 #1: $X"
fi
if has codex; then
for i in $(seq 2 "$MAXQ"); do [[ "${X:-0}" -ge 5 ]] && break
  q01 "$F1" >/dev/null; X=$(codexn "$F1"); echo "  codex after Q01 #$i: $X"; done
[[ "${X:-0}" -ge 5 ]] && ok "$F1 codex $X ≥ 5 from real drops" || bad "$F1 codex only $X after $MAXQ Q01"
consist_ok "$F1" "after Q01 runs"
CA=$(coin "$F1"); CN0=$(wc -l < "$LOG")
R=$(chat "$F1" "/corerpg p1 codex claim" 3000); sleep 2; CB=$(coin "$F1")
CL=$(tail -n +"$((CN0+1))" "$LOG" | tr -d '\r'); echo "  claim: coin $CA → $CB · $(echo "$CL" | grep -a 'P1 codex' | sed 's/.*\[P1 codex\]/[P1 codex]/' | tr '\n' ' ')"
echo "$CL" | grep -aq "\[P1 codex\] $F1 stage 5 -> coin 200" && ok "S33 codex stage 5 claimed" || bad "codex claim not logged"
[[ "$CA" =~ ^[0-9]+$ && "$CB" =~ ^[0-9]+$ && $((CB - CA)) == 200 ]] && ok "S33 coin +200 exact (golden at5.coin)" || bad "codex coin delta $CA → $CB"
echo "$CL" | grep -aE "economy grant[A-Za-z]* refused" && bad "codex grant refused" || ok "codex grantCoin not refused (tagged S33)"
CS=$(sql "SELECT status FROM cr_p1_reward WHERE player_uuid='$U1' AND run_id='codex' AND reward_key='stage0'"); echo "  ledger codex/stage0: ${CS:-<pruned>}"
R2=$(chat "$F1" "/corerpg p1 codex claim" 2500); sleep 1; CC=$(coin "$F1")
echo "$R2" | grep -q "没有可领取" && [[ "$CC" == "$CB" ]] && ok "second claim pays nothing (once)" || bad "second claim: $R2 coin $CB → $CC"
consist_ok "$F1" "after codex claim"
fi
quit "$F1"
fi

# ---------------------------------------------------------------- F2: givedup → admin, not dismantlable, reroll, no loss/dup
if has dup; then
[[ -z "$(uuid "$F2")" ]] && ok "$F2 is a fresh name" || bad "$F2 already exists"
join "$F2" || exit 1; sleep 4; U2=$(uuid "$F2")
$C play "corerpg p1 give scorch blade 1 0 0 0 $F2" 1.0 >/dev/null; sleep 1
TGT=$(sql "SELECT item_uid FROM cr_p1_item WHERE owner_uuid='$U2' AND state='active' AND tier=1 AND slot='blade' ORDER BY created_at DESC LIMIT 1")
# T1 blade into hotbar slot 0 (inventory 36), held
evalb "$F2" "bot.setQuickBarSlot(0); await wait(200); let k=-1; for (let i=9;i<=44;i++){const x=bot.inventory.slots[i]; if(x&&JSON.stringify(x.nbt||'').includes('$TGT')) k=i;} if(k>=0&&k!==36) await bot.moveSlotItem(k,36); await wait(800); return String(k);" >/dev/null
$C play "corerpg coin give $F2 5000" 0.6 >/dev/null
consist_ok "$F2" "before givedup"
GN0=$(wc -l < "$LOG"); GD=$($C play "corerpg p1 givedup blade $F2 0" 1.0 | tr -d '\r'); echo "  givedup: $(echo "$GD" | tail -1 | cut -c1-160)"; sleep 1
DUP=$(sql "SELECT item_uid FROM cr_p1_item WHERE owner_uuid='$U2' AND state='active' AND item_uid<>'$TGT' AND slot='blade' AND tier=1 ORDER BY created_at DESC LIMIT 1")
[[ -n "$DUP" ]] && ok "givedup created a row ($DUP)" || bad "givedup: no row"
SRC=$(sql "SELECT source FROM cr_p1_item WHERE item_uid='$DUP'"); [[ "$SRC" == admin ]] && ok "G4: givedup source = admin (was drop)" || bad "G4: givedup source=$SRC"
echo "$GD" | grep -q "src=admin" && ok "givedup message says src=admin" || soft "givedup message ($GD)"
consist_ok "$F2" "after givedup"
# dup into hand (swap with hotbar 0), try to dismantle → refused, piece kept
evalb "$F2" "let k=-1; for (let i=9;i<=44;i++){const x=bot.inventory.slots[i]; if(x&&JSON.stringify(x.nbt||'').includes('$DUP')) k=i;} if(k>=0&&k!==36) await bot.moveSlotItem(k,36); await wait(800); return String(k);" >/dev/null
DM=$(chat "$F2" "/corerpg p1 dismantle" 2000); echo "  dismantle: $(echo "$DM" | head -2 | tr '\n' ' ')"
echo "$DM" | grep -q "只有副本里随机掉落的装备能分解" && ok "G4: dismantle refused (测试件)" || bad "G4: dismantle not refused ($DM)"
echo "$DM" | grep -q "确认分解" && bad "dismantle offered a confirm button" || ok "no [确认分解] offered"
[[ "$(sql "SELECT state FROM cr_p1_item WHERE item_uid='$DUP'")" == active ]] && ok "dup row still active after refusal" || bad "dup row state changed"
consist_ok "$F2" "after dismantle refusal"
# target back in hand → reroll eats the admin duplicate
evalb "$F2" "let k=-1; for (let i=9;i<=44;i++){const x=bot.inventory.slots[i]; if(x&&JSON.stringify(x.nbt||'').includes('$TGT')) k=i;} if(k>=0&&k!==36) await bot.moveSlotItem(k,36); await wait(800); return String(k);" >/dev/null
PV=$(chat "$F2" "/corerpg p1 reroll blade dup" 2500); echo "$PV" | head -4
echo "$PV" | grep -q "只有掉落来的件能当重复件\|没有可用的重复件" && bad "reroll preview rejects the admin dup" || ok "reroll preview accepts the admin dup"
RR=$(chat "$F2" "/corerpg p1 reroll blade dup confirm" 3500); echo "$RR" | head -4
echo "$RR" | grep -qE '洗练结果|词条槽|保留' && ok "reroll with admin dup rolled" || bad "reroll no result ($RR)"
sleep 2
DS=$(sql "SELECT state FROM cr_p1_item WHERE item_uid='$DUP'"); TS=$(sql "SELECT state FROM cr_p1_item WHERE item_uid='$TGT'")
[[ "$DS" != active && "$TS" == active ]] && ok "dup consumed ($DS), target kept" || bad "after reroll dup=$DS target=$TS"
consist_ok "$F2" "after reroll"
quit "$F2"; join "$F2" || true; sleep 3; consist_ok "$F2" "after rejoin"; quit "$F2"
fi

ALL=$(tail -n +"$((N_START+1))" "$LOG" | tr -d '\r')
echo "$ALL" | grep -aE "economy grant[A-Za-z]* refused" && bad "grant refusal during smoke" || ok "no grant refusal during smoke"
BOOT=$(grep -an "Enabling CoreRpg v$VER" "$LOG" | tail -1 | cut -d: -f1)
POST=$(tail -n +"$BOOT" "$LOG" | grep -ac "SEVERE" || true); POST=${POST:-0}
[[ "$POST" == "0" ]] && ok "SEVERE 0 (post-enable)" || bad "SEVERE post-enable=$POST"
echo "==== RESULT PASS $PASS / FAIL $FAIL / SOFT $SOFT ===="
[[ "$FAIL" == "0" ]]
