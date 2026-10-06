#!/usr/bin/env bash
# d245-arch-s4-4-smoke.sh — CoreRpg 1.65.70 D245 (ARCH S4-4): item provenance (origin) on new Ember gear.
#   boot: version, MySQL x2, E1 SoT bv58, no FAIL-CLOSED, cr_p1_item.origin column, old rows untouched ('').
#   F1 fresh: starter kit → origin starter|S35|starter|<t> (DB = NBT) · Q01 first clear + repeat → base_item origin
#             q01|S01|<run>|<ledger second> (DB = NBT = cr_p1_reward row) · gear library stash → take keeps it ·
#             forge enhance keeps it · non-OP inspect: valid, no provenance line · inventory = DB after every step.
#   F2 fresh: 8 T1 marks (admin hook) → exchange → origin forge|S28|<rid> · dismantle → undo → take keeps it ·
#             /corerpg p1 give → admin|X03|give · loss/dup check.
#   OLD: a pre-1.65.70 bot (default FreshQ834): rows origin '' + NBT without om, item still valid (inspect), inventory = DB.
# Usage:  F1=FreshQ835 F2=FreshQ836 OLD=FreshQ834 tools/p1map/d245-arch-s4-4-smoke.sh   (bots FreshQ835+)
#         PART="boot old" …   # parts: boot starter q01 lib enh mark admin old restart (default all but restart)
set -uo pipefail
M=/workspace/minecraft; C=$M/scripts/console.sh; BOTD=http://127.0.0.1:8765; T=$M/mineflayer-tests/tmp-p1; RT=$M/server-runtime
LOG=$RT/logs/latest.log; VER=${VER:-1.65.70}
F1=${F1:-FreshQ835}; F2=${F2:-FreshQ836}; OLD=${OLD:-FreshQ834}
PASS=0; FAIL=0; SOFT=0; N_START=$(wc -l < "$LOG"); PART=${PART:-"boot starter q01 lib enh mark admin old"}
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
dbo(){ sql "SELECT origin FROM cr_p1_item WHERE item_uid='$1'"; }
gstate(){ sql "SELECT CONCAT(state,':',rev) FROM cr_p1_item WHERE item_uid='$1'"; }
hasuid(){ evalb "$1" "let n=0; for (const i of bot.inventory.slots) { if (i && JSON.stringify(i.nbt||'').includes('$2')) n++; } return String(n);"; }
# origin keys on the stack carrying uid $2 → "map|src|run|at" ('' when the stack has none, 'nostack' when absent)
nbto(){ evalb "$1" "const g=(o,k)=>{ if(!o||typeof o!=='object') return undefined; if(o[k]&&o[k].value!==undefined&&typeof o[k].value!=='object') return o[k].value; for (const x in o){ const r=g(o[x],k); if(r!==undefined) return r; } return undefined; }; for (const i of bot.inventory.slots) { if (i && JSON.stringify(i.nbt||'').includes('$2')) { const v=['om','os','or','ot'].map(k=>g(i.nbt,k)); return v.every(x=>x===undefined)?'':v.map(x=>x===undefined?'?':String(x)).join('|'); } } return 'nostack';"; }
# lore lines of the stack with uid $2 (joined), to show provenance never reaches lore
lore(){ evalb "$1" "for (const i of bot.inventory.slots) { if (i && JSON.stringify(i.nbt||'').includes('$2')) { try { return i.nbt.value.display.value.Lore.value.value.join(' / ').replace(/\u00a7./g,''); } catch (e) { return 'nolore'; } } } return 'nostack';"; }
# move the stack with uid $2 into the main inventory (slots 9-35: what /corerpg p1 stash stores)
tobag(){ evalb $1 "let k=-1; for(let i=9;i<=44;i++){const x=bot.inventory.slots[i]; if(x&&JSON.stringify(x.nbt||{}).includes('$2')){k=i;break;}} if(k>=36){ let t=-1; for(let j=9;j<=35;j++) if(!bot.inventory.slots[j]){t=j;break;} if(t>=0) await bot.moveSlotItem(k,t); } await wait(600); return String(k);" >/dev/null; }
tohand(){ evalb $1 "bot.setQuickBarSlot(0); await wait(200); let k=-1; for(let i=9;i<=44;i++){const x=bot.inventory.slots[i]; if(x&&JSON.stringify(x.nbt||{}).includes('$2')){k=i;break;}} if(k>=0&&k!==36) await bot.moveSlotItem(k,36); await wait(800); return String(k);" >/dev/null; }
tomain(){ evalb "$1" 'let n=0; for(let i=37;i<=44;i++){const x=bot.inventory.slots[i]; if(!x||!/sword|_axe|_hoe/.test(x.name)) continue; let t=-1; for(let j=9;j<=35;j++) if(!bot.inventory.slots[j]){t=j;break;} if(t<0) break; await bot.moveSlotItem(i,t); n++; await wait(150);} return String(n);' >/dev/null; }
consist(){ local B=$1 U; U=$(uuid "$B")
  local IDS; IDS=$(sql "SELECT item_uid FROM cr_p1_item WHERE owner_uuid='$U' AND state='active'" | tr '\n' ' ')
  evalb "$B" "const ids='$IDS'.trim().split(/\s+/).filter(Boolean); const s=bot.inventory.slots.filter(Boolean).map(i=>JSON.stringify(i.nbt||'')); const p1=s.filter(x=>x.includes('ember_v1')).length; const bad=ids.filter(u=>s.filter(x=>x.includes(u)).length!==1); return ids.length+' rows · '+p1+' P1 stacks · off '+bad.length;"; }
consist_ok(){ local R; R=$(consist "$1"); echo "  [$2] $R"
  [[ "$R" =~ ^([0-9]+)\ rows\ ·\ ([0-9]+)\ P1\ stacks\ ·\ off\ 0$ ]] && [[ "${BASH_REMATCH[1]}" == "${BASH_REMATCH[2]}" ]] && ok "$1 $2: inventory = DB ($R)" || bad "$1 $2: inventory ≠ DB ($R)"; }
# DB origin = NBT origin for uid $2 on bot $1, and matches regex $3
same(){ local D N; D=$(dbo "$2"); N=$(nbto "$1" "$2"); echo "  $2 db=[$D] nbt=[$N]"
  [[ "$D" == "$N" ]] && ok "$4: DB origin = NBT origin" || bad "$4: DB [$D] ≠ NBT [$N]"
  [[ "$D" =~ $3 ]] && ok "$4: origin shape ($D)" || bad "$4: origin [$D] !~ $3"; }

if has boot; then
grep -aq "Enabling CoreRpg v$VER" "$LOG" && ok "CoreRpg $VER" || bad "CoreRpg $VER"
grep -aq "\[CoreRpg\] \[storage\] MySQL connected" "$LOG" && ok "CoreRpg MySQL" || bad "CoreRpg MySQL"
grep -aq "\[CoreGacha\] \[db\] MySQL connected" "$LOG" && ok "CoreGacha MySQL" || bad "CoreGacha MySQL"
grep -aq "ember-v1-economy.yml loaded as amount() SoT (bv58)" "$LOG" && ok "E1 SoT bv58" || bad "E1 SoT bv58"
grep -aq "MySQL tables cr_p1_item .* ready" "$LOG" && ok "cr_p1_item schema ready" || bad "cr_p1_item schema line"
grep -aq "FAIL-CLOSED" "$LOG" && bad "FAIL-CLOSED" || ok "no FAIL-CLOSED"
COL=$(sql "SELECT CONCAT(COLUMN_TYPE,'/',IS_NULLABLE,'/',IFNULL(COLUMN_DEFAULT,'NULL')) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA='ember' AND TABLE_NAME='cr_p1_item' AND COLUMN_NAME='origin'")
[[ "$COL" == "varchar(128)/NO/''" || "$COL" == "varchar(128)/NO/" ]] && ok "cr_p1_item.origin column ($COL)" || bad "origin column [$COL]"
DEPLOY=${DEPLOY:-"2026-10-06 10:07:51"}   # first boot of $VER (the log rotates at every restart)
OLDN=$(sql "SELECT COUNT(*) FROM cr_p1_item WHERE created_at < $(( $(date -d "$DEPLOY" +%s) * 1000 )) AND origin<>''")
[[ "$OLDN" == 0 ]] && ok "rows created before $VER ($DEPLOY) keep origin '' ($OLDN set)" || bad "old rows with origin: $OLDN"
fi
curl -sf "$BOTD/list" >/dev/null || { bad "botd"; exit 1; }

# ---------------------------------------------------------------- Q01 helpers (as d243/d244)
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
    if echo "$R" | grep -aq "正在创建实例"; then sleep 4; [[ "$(weaken_n "$B")" != -1 ]] && break; fi  # entry can be released (未进入实例): retry
    [[ $e == 4 ]] && return 2; sleep 10; done
  $C play "tp $B 0 64 28" 0.3 >/dev/null; sleep 2
  clear_room "$B" || return 3
  $C play "tp $B 30 64 38" 0.3 >/dev/null; sleep 2; clear_room "$B" || return 4
  $C play "tp $B 32 64 68" 0.3 >/dev/null; sleep 2; clear_room "$B" || return 5
  $C play "tp $B 0 64 102" 0.3 >/dev/null; sleep 2; settle "$B"; sleep 3
  $C play "gamemode survival $B" 0.3 >/dev/null; return 0; }
# newest base_item row of a run: uid, and check origin against the ledger row
runitem(){ local U=$1 RUN=$2; sql "SELECT item_uid FROM cr_p1_item WHERE owner_uuid='$U' AND origin LIKE '%|$RUN|%' ORDER BY created_at DESC LIMIT 1"; }

# ---------------------------------------------------------------- F1: starter kit
if has starter; then
[[ -z "$(uuid "$F1")" ]] && ok "$F1 is a fresh name" || bad "$F1 already exists"
N0=$(wc -l < "$LOG"); join "$F1" || exit 1; sleep 6
U1=$(uuid "$F1")
SI=$(sql "SELECT CONCAT(item_uid,' ',slot,'/t',tier,'/',source,' ',origin) FROM cr_p1_item WHERE owner_uuid='$U1' AND state='active' ORDER BY slot"); echo "$SI" | sed 's/^/  /'
[[ $(echo "$SI" | grep -cE "/t0/quest starter\|S35\|starter\|[0-9]+$") == 2 ]] && ok "$F1 2 starter pieces origin starter|S35|starter|<t>" || bad "$F1 starter origins"
for u in $(echo "$SI" | awk '{print $1}'); do same "$F1" "$u" '^starter\|S35\|starter\|[0-9]{10}$' "starter $u"; done
L=$(tail -n +"$((N0+1))" "$LOG" | tr -d '\r')
echo "$L" | grep -aE "economy grant[A-Za-z]* refused" && bad "$F1 grant refused at join" || ok "$F1 no grant refusal at join"
consist_ok "$F1" "after starter kit"
quit "$F1"
fi

# ---------------------------------------------------------------- F1: Q01 first clear + repeat → base_item q01|S01
if has q01 || has lib || has enh; then join "$F1" || exit 1; U1=$(uuid "$F1"); $T/b.sh chat "$F1" "/dungeon-team disband" 800 >/dev/null || true; fi
if has q01; then
BASES=()
for n in 1 2; do
  [[ $n == 2 ]] && sleep 12   # let the first instance close before re-entering
  QN0=$(wc -l < "$LOG"); q01 "$F1"; RC=$?; FULL=$(tail -n +"$((QN0+1))" "$LOG" | tr -d '\r')
  RUN=$(echo "$FULL" | grep -aoE "\[P1 run\] q01-[a-z0-9-]+ settle" | tail -1 | awk '{print $3}')
  [[ "$RC" == 0 && -n "$RUN" ]] && ok "Q01 #$n settled ($RUN)" || { bad "Q01 #$n rc=$RC run=$RUN"; continue; }
  sleep 3
  BI=$(runitem "$U1" "$RUN"); RS=$(sql "SELECT CONCAT(status,' ',FLOOR(created_at/1000)) FROM cr_p1_reward WHERE player_uuid='$U1' AND run_id='$RUN' AND reward_key='base_item'")
  echo "  run $RUN base_item ledger [$RS] item $BI"
  if [[ -n "$BI" ]]; then
    same "$F1" "$BI" "^q01\|S01\|$RUN\|[0-9]{10}$" "Q01 #$n base_item"
    [[ "$(dbo "$BI" | cut -d'|' -f4)" == "$(echo "$RS" | awk '{print $2}')" ]] && ok "Q01 #$n origin time = ledger row created_at" || bad "Q01 #$n origin time ≠ ledger ($RS)"
    [[ "$(sql "SELECT source FROM cr_p1_item WHERE item_uid='$BI'")" == drop ]] && ok "Q01 #$n base_item source drop (unchanged)" || bad "Q01 #$n source"
    BASES+=("$BI")
  else bad "Q01 #$n: no item row with origin run $RUN"; fi
  if [[ $n == 1 ]]; then
    FC=$(sql "SELECT CONCAT(item_uid,' ',origin) FROM cr_p1_item WHERE owner_uuid='$U1' AND origin LIKE 'q01|S06|%'"); echo "  first-clear pick rows: ${FC:-none (choice not taken — needs the pick button)}"
    [[ -n "$FC" ]] && ok "fc choice origin q01|S06" || soft "fc choice piece not picked in this smoke (mapping pinned by EmberProvenanceTest)"
  fi
  echo "$FULL" | grep -aE "economy grant[A-Za-z]* refused" && bad "Q01 #$n grant refused" || ok "no grant refusal Q01 #$n"
  consist_ok "$F1" "after Q01 #$n"
done
fi

# ---------------------------------------------------------------- F1: gear library stash → take keeps origin
if has lib; then
if has q01; then quit "$F1"; join "$F1" || exit 1; sleep 3; fi   # back in the hub (stash is refused inside an instance)
for u in $(sql "SELECT item_uid FROM cr_p1_item WHERE owner_uuid='$U1' AND state='active' AND origin LIKE 'q01|S01|%'"); do tobag "$F1" "$u"; done
declare -A O0S; for u in $(sql "SELECT item_uid FROM cr_p1_item WHERE owner_uuid='$U1' AND origin<>''"); do O0S[$u]=$(dbo "$u"); done
chat "$F1" "/corerpg p1 stash" 2500 | head -3; sleep 2
# the library piece = a stored Q01 drop (stash skips the live loadout: the worn blade / selected charm stay in the bag)
G=$(sql "SELECT item_uid FROM cr_p1_item WHERE owner_uuid='$U1' AND state='stored' AND origin LIKE 'q01|S01|%' ORDER BY created_at LIMIT 1")
[[ -n "$G" ]] && ok "lib: Q01 drop $G stored" || bad "lib: no Q01 drop stored ($(sql "SELECT GROUP_CONCAT(CONCAT(item_uid,':',state)) FROM cr_p1_item WHERE owner_uuid='$U1'"))"
O0=${O0S[$G]:-}; echo "  library piece $G [$O0] $(gstate "$G")"
NST=0; for u in $(sql "SELECT item_uid FROM cr_p1_item WHERE owner_uuid='$U1' AND state='stored'"); do [[ "$(dbo "$u")" == "${O0S[$u]:-}" ]] || NST=$((NST+1)); done
[[ "$NST" == 0 ]] && ok "lib: every stored piece kept its origin" || bad "lib: $NST stored pieces changed origin"
[[ -n "$G" && "$(hasuid "$F1" "$G")" == 0 ]] && ok "lib: no stack left in backpack" || bad "lib: stack still in backpack"
chat "$F1" "/corerpg p1 gearlib take $G" 2500 | grep -E '取出' | head -1; sleep 2
[[ "$(gstate "$G" | cut -d: -f1)" == active ]] && ok "lib: $G taken back (active)" || bad "lib: take ($(gstate "$G"))"
[[ "$(dbo "$G")" == "$O0" && "$(nbto "$F1" "$G")" == "$O0" ]] && ok "lib: stash → take keeps origin (DB + NBT)" || bad "lib: origin after take db=[$(dbo "$G")] nbt=[$(nbto "$F1" "$G")]"
for u in $(sql "SELECT item_uid FROM cr_p1_item WHERE owner_uuid='$U1' AND state='stored'"); do chat "$F1" "/corerpg p1 gearlib take $u" 2000 >/dev/null; done  # the rest of the stash back
sleep 2; consist_ok "$F1" "after stash/take"
fi

# ---------------------------------------------------------------- F1: forge enhance keeps origin; inspect (non-OP) valid, no provenance line
if has enh; then
E=${BASES[1]:-${BASES[0]:-}}; [[ -z "$E" ]] && E=$(sql "SELECT item_uid FROM cr_p1_item WHERE owner_uuid='$U1' AND state='active' AND origin LIKE 'q01|S01|%' ORDER BY created_at DESC LIMIT 1")
[[ "$(gstate "$E" | cut -d: -f1)" == stored ]] && chat "$F1" "/corerpg p1 gearlib take $E" 2000 >/dev/null
O0=$(dbo "$E"); R0=$(gstate "$E"); echo "  enhance piece $E [$O0] $R0"
$C play "corerpg coin give $F1 5000" 0.6 >/dev/null; $C play "ni give $F1 mat_ember_shard 64" 0.6 >/dev/null; $C play "ni give $F1 mat_ember_core_fragment 16" 0.6 >/dev/null; sleep 1
tomain "$F1"; tohand "$F1" "$E"
EN=$(chat "$F1" "/corerpg p1 enhance confirm" 3500); echo "  enhance: $(echo "$EN" | grep -E '强化|成功|失败|不足' | head -2 | tr '\n' ' ')"; sleep 2
R1=$(gstate "$E"); [[ "$R1" != "$R0" ]] && ok "enh: item transaction committed ($R0 → $R1)" || soft "enh: rev unchanged ($R1) — materials/route"
[[ "$(dbo "$E")" == "$O0" && "$(nbto "$F1" "$E")" == "$O0" ]] && ok "enh: origin kept after forge (DB + NBT)" || bad "enh: origin db=[$(dbo "$E")] nbt=[$(nbto "$F1" "$E")]"
LO=$(lore "$F1" "$E"); echo "  lore: $LO"; [[ "$LO" == nolore || "$LO" == nostack ]] && bad "lore not readable ($LO)"; echo "$LO" | grep -qE "来源记录|S01|$(echo "$O0" | cut -d'|' -f3)" && bad "provenance visible in lore" || ok "lore carries no provenance"
tohand "$F1" "$E"; IN=$(chat "$F1" "/corerpg p1 inspect" 2500)
echo "$IN" | grep -q "校验失败" && bad "inspect: new piece fails validation" || ok "inspect: new piece valid"
echo "$IN" | grep -q "来源记录" && bad "non-OP sees the provenance line" || ok "non-OP inspect shows no provenance line"
consist_ok "$F1" "after enhance"
fi
if has q01 || has lib || has enh; then quit "$F1"; fi

# ---------------------------------------------------------------- F2: mark exchange forge|S28 → dismantle → undo → take; admin give X03
if has mark || has admin; then
[[ -z "$(uuid "$F2")" ]] && ok "$F2 is a fresh name" || bad "$F2 already exists"
join "$F2" || exit 1; sleep 4; U2=$(uuid "$F2"); $C play "corerpg coin give $F2 5000" 0.6 >/dev/null
fi
if has mark; then
$C play "corerpg p1 runs marks $F2 1 8" 0.8 | tr -d '\r' | tail -1
MN0=$(wc -l < "$LOG")
chat "$F2" "/corerpg p1 marks exchange scorch blade 1 confirm" 3500 | grep -E '兑换|获得|印记' | head -2; sleep 2
MK=$(sql "SELECT item_uid FROM cr_p1_item WHERE owner_uuid='$U2' AND origin LIKE 'forge|S28|%' ORDER BY created_at DESC LIMIT 1")
[[ -n "$MK" ]] && ok "mark exchange piece $MK" || bad "mark exchange: no forge|S28 row"
if [[ -n "$MK" ]]; then
  same "$F2" "$MK" '^forge\|S28\|[A-Za-z0-9_@:.-]+\|[0-9]{10}$' "mark exchange"
  O0=$(dbo "$MK"); consist_ok "$F2" "after mark exchange"
  tomain "$F2"; tohand "$F2" "$MK"
  TOK=$(evalb "$F2" 'let tok=""; const h=m=>{const j=JSON.stringify(m.json||{}); const x=j.match(/tok:([A-Za-z0-9]+)/); if(x) tok=x[1];}; bot.on("message",h); bot.chat("/corerpg p1 dismantle"); await wait(2200); bot.removeListener("message",h); return tok;')
  [[ -n "$TOK" ]] && ok "dismantle preview token" || bad "dismantle: no token"
  chat "$F2" "/corerpg p1 dismantle confirm tok:$TOK" 3000 | grep -E '分解' | head -1; sleep 2
  [[ "$(gstate "$MK" | cut -d: -f1)" == dismantled ]] && ok "dismantle: $MK dismantled" || bad "dismantle state $(gstate "$MK")"
  [[ "$(dbo "$MK")" == "$O0" ]] && ok "dismantle: origin kept on the dismantled row" || bad "dismantle: origin [$(dbo "$MK")]"
  [[ "$(hasuid "$F2" "$MK")" == 0 ]] && ok "dismantle: stack gone" || bad "dismantle: stack still there"
  chat "$F2" "/corerpg p1 undo $MK" 3500 | grep -E '撤销|扣|胚料' | head -2; sleep 2
  [[ "$(gstate "$MK" | cut -d: -f1)" == stored ]] && ok "undo: $MK back in the library (stored)" || bad "undo state $(gstate "$MK")"
  [[ "$(dbo "$MK")" == "$O0" ]] && ok "undo: origin kept" || bad "undo: origin [$(dbo "$MK")]"
  chat "$F2" "/corerpg p1 gearlib take $MK" 2500 | grep -E '取出' | head -1; sleep 2
  [[ "$(gstate "$MK" | cut -d: -f1)" == active && "$(dbo "$MK")" == "$O0" && "$(nbto "$F2" "$MK")" == "$O0" ]] && ok "dismantle → undo → take keeps origin (DB + NBT)" || bad "after undo+take: $(gstate "$MK") db=[$(dbo "$MK")] nbt=[$(nbto "$F2" "$MK")]"
  [[ "$(sql "SELECT COUNT(*) FROM cr_p1_item WHERE item_uid='$MK'")" == 1 && "$(hasuid "$F2" "$MK")" == 1 ]] && ok "exactly one row and one stack for $MK (no dup)" || bad "dup/loss for $MK"
  consist_ok "$F2" "after dismantle/undo/take"
fi
fi
if has admin; then
$C play "corerpg p1 give scorch charm 1 0 0 0 $F2" 1.0 >/dev/null; sleep 2
AD=$(sql "SELECT item_uid FROM cr_p1_item WHERE owner_uuid='$U2' AND state='active' AND source='admin' ORDER BY created_at DESC LIMIT 1")
[[ -n "$AD" ]] && same "$F2" "$AD" '^admin\|X03\|give\|[0-9]{10}$' "admin give" || bad "admin give: no row"
consist_ok "$F2" "after admin give"
fi
if has mark || has admin; then quit "$F2"; fi

# ---------------------------------------------------------------- OLD: a pre-1.65.70 bot stays valid
if has old; then
UO=$(uuid "$OLD"); [[ -n "$UO" ]] || { bad "$OLD does not exist"; }
if [[ -n "$UO" ]]; then
  join "$OLD" || exit 1; sleep 4
  OI=$(sql "SELECT CONCAT(item_uid,' [',origin,']') FROM cr_p1_item WHERE owner_uuid='$UO' AND state='active'"); echo "$OI" | sed 's/^/  /'
  [[ -n "$OI" && $(echo "$OI" | grep -vc ' \[\]$') == 0 ]] && ok "$OLD: all active rows origin '' (pre-1.65.70)" || bad "$OLD rows: $OI"
  OU=$(echo "$OI" | head -1 | awk '{print $1}')
  [[ "$(nbto "$OLD" "$OU")" == "" ]] && ok "$OLD: NBT has no origin keys" || bad "$OLD: NBT origin [$(nbto "$OLD" "$OU")]"
  tomain "$OLD"; tohand "$OLD" "$OU"; IN=$(chat "$OLD" "/corerpg p1 inspect" 2500)
  echo "$IN" | grep -q "校验失败" && bad "$OLD: old piece fails validation" || ok "$OLD: old piece still valid (inspect)"
  consist_ok "$OLD" "old bot"
  [[ "$(dbo "$OU")" == "" ]] && ok "$OLD: join did not stamp an origin on the old piece" || bad "$OLD: origin stamped [$(dbo "$OU")]"
  quit "$OLD"
fi
fi

# ---------------------------------------------------------------- optional: graceful restart round trip (PART=restart)
if has restart; then
INO=$(stat -c %i "$LOG"); echo "RESTART $(date +%H:%M) d245 smoke: graceful" >> /workspace/COORD-arch-s4-4.txt
SNAP=$(sql "SELECT MD5(GROUP_CONCAT(item_uid,origin ORDER BY item_uid)) FROM cr_p1_item WHERE origin<>''")
$RT/stop.sh >/dev/null; sleep 1; $RT/start.sh >/dev/null; for i in $(seq 1 90); do [ "$(stat -c %i "$LOG")" != "$INO" ] && grep -q 'Done (' "$LOG" && break; sleep 2; done; sleep 5
[[ "$(sql "SELECT MD5(GROUP_CONCAT(item_uid,origin ORDER BY item_uid)) FROM cr_p1_item WHERE origin<>''")" == "$SNAP" ]] && ok "restart: every origin unchanged" || bad "restart: origins changed"
N_START=0
fi

ALL=$(tail -n +"$((N_START+1))" "$LOG" | tr -d '\r')
echo "$ALL" | grep -aE "economy grant[A-Za-z]* refused" && bad "grant refusal during smoke" || ok "no grant refusal during smoke"
BOOT=$(grep -an "Enabling CoreRpg v$VER" "$LOG" | tail -1 | cut -d: -f1)
POST=$(tail -n +"$BOOT" "$LOG" | grep -ac "SEVERE" || true); POST=${POST:-0}
[[ "$POST" == "0" ]] && ok "SEVERE 0 (post-enable)" || bad "SEVERE post-enable=$POST"
echo "==== RESULT PASS $PASS / FAIL $FAIL / SOFT $SOFT ===="
[[ "$FAIL" == "0" ]]
