#!/usr/bin/env bash
# persist-roundtrip.sh — real-play persistence round trips with bots, each with a before/after DB + backpack comparison
# (no loss, no duplication). Restarts the PLAY server (graceful twice, then one kill -9 of the exact Paper PID).
#   a) warehouse deposit → abrupt disconnect → restart → rejoin → withdraw → counts exact
#   b) gear → 装备库 → abrupt disconnect → restart → withdraw; withdraw + disconnect in the same tick (mid-transaction)
#   c) bulk dismantle → undo batch; bulk dismantle → restart → undo batch (inside the window)
#   d) invsnap restore queued for an OFFLINE player → restart → join → applied once; restart again → not applied twice
#   e) gacha 10-pull → disconnect right after the command → restart → wallet / ledger / owned / CoreRpg flags consistent
#   f) materials saved in the .dat → deposit + withdraw + gear stash → kill -9 Paper → start → nothing lost or duplicated
#   g) D162 fault injection (restarts play with CORERPG_TEST_FAULTS=1, then once more without it):
#      before_commit (rolled back) / after_commit (callback held 10 s: disconnect or kill -9 inside that window) /
#      after_deliver (applied + saved, ack skipped) on bulk dismantle, withdraw, undo, forge enhance; asserts conservation
#      (end = start + confirmed gains − confirmed costs) and exactly-once delivery rows
#   h) D172 asset fixes (restarts play with CORERPG_TEST_FAULTS=1, two kill -9 bundles, then once more without it):
#      undo pays its blanks up front — undo batch raced against a forge refine on the same blanks (both orders),
#      before_commit, after_pay + kill -9 · mark redemption — normal, before_commit, after_commit + kill -9, after_pay +
#      kill -9 · affix reroll — normal, before_commit, after_commit + disconnect, instant disconnect, after_pay +
#      disconnect, library-duplicate after_commit + disconnect, after_commit + kill -9, after_pay + kill -9. Asserts
#      conservation (blanks / marks / coins / shards), exactly-once items and exactly one roll per committed reroll.
#      D208: the roll commits in the settling item transaction (cr_p1_item affix / af_pity / reroll_n), no p4_rro_.
# usage: tools/p1map/persist-roundtrip.sh [A_BOT] [D_BOT] [E_BOT] [F_BOT] [G1_BOT] [G2_BOT] [H1_BOT] [H2_BOT] [H3_BOT]
#        (fresh bots every run) · ONLY=g or ONLY=g,h … runs only those phases ("1" = a–f); NOG=1 skips g
#        COORDF=… file that gets the RESTART / KILL9 notes (default /workspace/COORD-rush-retry-persist.txt)
# needs: botd on 127.0.0.1:8765 (mineflayer-tests/tmp-p1/botd.js), sudo mysql (read-only queries), no real players online.
set -uo pipefail
BA=${1:-FreshQ52}; BD=${2:-FreshQ53}; BE=${3:-FreshG07}; BF=${4:-FreshQ54}; BG1=${5:-FreshQ61}; BG2=${6:-FreshQ62}
BH1=${7:-FreshQ109}; BH2=${8:-FreshQ110}; BH3=${9:-FreshQ111}; COORDF=${COORDF:-/workspace/COORD-rush-retry-persist.txt}
want(){ [ -z "${ONLY:-}" ] || [[ ",$ONLY," == *",$1,"* ]]; }
M=/workspace/minecraft; C=$M/scripts/console.sh; RT=$M/server-runtime; BOTD=http://127.0.0.1:8765
PASS=0; FAIL=0; RES=()
ok(){ echo "PASS $1"; PASS=$((PASS+1)); RES+=("PASS $1"); }
bad(){ echo "FAIL $1"; FAIL=$((FAIL+1)); RES+=("FAIL $1"); }
chk(){ if [ "$2" = "$3" ]; then ok "$1 ($2)"; else bad "$1 (want $3, got $2)"; fi; }
sql(){ sudo -n mysql -N -B ember -e "$1" 2>/dev/null; }
uuid(){ sql "SELECT uuid FROM cr_players WHERE name='$1' ORDER BY updated_at DESC LIMIT 1"; }
evalb(){ curl -sf -m 60 -X POST "$BOTD/eval?name=$1" --data "$2" | jq -r '.r // .err // ""'; }
join(){ curl -sf -m 90 "$BOTD/join?name=$1" >/dev/null; sleep 6; curl -sf "$BOTD/list" | grep -q "\"$1\"" || { sleep 6; curl -sf -m 90 "$BOTD/join?name=$1" >/dev/null; sleep 6; }; }
online(){ curl -sf "$BOTD/list" | grep -q "\"$1\""; }
quit(){ curl -sf "$BOTD/quit?name=$1" >/dev/null || true; }
# abrupt: destroy the TCP socket (no disconnect packet) — like a crashed client / pulled cable
abrupt(){ evalb "$1" 'setTimeout(()=>{ try { bot._client.socket.destroy() } catch (e) {} }, 0); return "cut";' >/dev/null; sleep 4; }
chat(){ evalb "$1" "const f=bot.chatLog.length; bot.chat($(jq -Rn --arg s "$2" '$s')); await wait(${3:-1500}); return bot.chatLog.slice(f).map(s=>s.replace(/\u00a7./g,'')).join('\n');"; }
# backpack count of items whose name/NBT contains $2 (display name, e.g. 余烬碎片)
count(){ evalb "$1" "let n=0; for (const i of bot.inventory.items()) { const d=(i.displayName||'')+' '+JSON.stringify(i.nbt||''); if (d.includes('$2')) n+=i.count; } return String(n);"; }
hasuid(){ evalb "$1" "let n=0; for (const i of bot.inventory.slots) { if (i && JSON.stringify(i.nbt||'').includes('$2')) n++; } return String(n);"; }
vault(){ sql "SELECT slots_json FROM cr_warehouse WHERE uuid='$1'" | python3 -c "import sys,json; t=sys.stdin.read().strip(); d=json.loads(t) if t and t!='NULL' else []; print(sum(int(x.get('amount',0)) for x in d if x.get('niId')=='$2'))"; }
gstate(){ sql "SELECT CONCAT(state,':',rev) FROM cr_p1_item WHERE item_uid='$1'"; }
tomain(){ evalb "$1" 'let n=0; for(let i=37;i<=44;i++){const x=bot.inventory.slots[i]; if(!x||!/sword|_axe|_hoe/.test(x.name)) continue; let t=-1; for(let j=9;j<=35;j++) if(!bot.inventory.slots[j]){t=j;break;} if(t<0) break; await bot.moveSlotItem(i,t); n++; await wait(150);} return String(n);' >/dev/null; }
# latest.log is rotated at startup: wait for a NEW file (other inode than before the stop) that says Done
waitdone(){ for i in $(seq 1 90); do [ "$(stat -c %i $RT/logs/latest.log 2>/dev/null)" != "$1" ] && grep -q 'Done (' $RT/logs/latest.log 2>/dev/null && return 0; sleep 2; done; return 1; }
logcheck(){ grep -q '\[CoreRpg\] \[storage\] MySQL connected' $RT/logs/latest.log && grep -q '\[CoreGacha\] \[db\] MySQL connected' $RT/logs/latest.log && ok "$1: MySQL connected (CoreRpg + CoreGacha)" || bad "$1: MySQL line missing"
  local sv; sv=$(grep -c SEVERE $RT/logs/latest.log); [ "$sv" = 0 ] && ok "$1: no SEVERE" || { bad "$1: $sv SEVERE"; grep SEVERE $RT/logs/latest.log | head -3; }; }
restart(){ echo "--- graceful restart ($1) $(date +%H:%M:%S)"; echo "RESTART $(date +%H:%M) persist-roundtrip: graceful ($1)" >> $COORDF
  local ino; ino=$(stat -c %i $RT/logs/latest.log); $RT/stop.sh >/dev/null; sleep 1; $RT/start.sh >/dev/null; sleep 3; waitdone "$ino" || bad "server did not finish starting"; sleep 5; logcheck "restart $1"; }
killhard(){ local pid; pid=$(cat $RT/server.pid)
  # only the play server's Paper JVM: its pid from server-runtime/server.pid, a java process whose cwd is server-runtime
  [ "$(readlink /proc/$pid/cwd)" = "$RT" ] && tr '\0' ' ' < /proc/$pid/cmdline | grep -q 'paper.*\.jar' || { bad "kill -9: pid $pid is not the play Paper JVM"; return 1; }
  echo "--- kill -9 $pid (play Paper) $(date +%H:%M:%S)"; echo "KILL9 $(date +%H:%M) persist-roundtrip: kill -9 play Paper pid $pid" >> $COORDF
  local ino; ino=$(stat -c %i $RT/logs/latest.log); kill -9 "$pid"; for i in $(seq 1 20); do kill -0 "$pid" 2>/dev/null || break; sleep 0.5; done
  rm -f $RT/server.pid; sleep 2; $RT/start.sh >/dev/null; sleep 3; waitdone "$ino" || bad "server did not finish starting after kill -9"; sleep 5; logcheck "after kill -9"; }

bulkrun(){ # $1 bot, $2 label → dismantles all library T1 standard pieces of the filter, prints n
  local out tok; out=$(evalb $1 'const f=bot.chatLog.length; let tok=""; const h=m=>{const j=JSON.stringify(m.json||{}); const x=j.match(/tok:([A-Za-z0-9]+)/); if(x) tok=x[1];}; bot.on("message",h); bot.chat("/corerpg p1 gearlib bulk"); await wait(2200); bot.removeListener("message",h); return "TOK="+tok;')
  tok=$(echo "$out" | head -1 | sed 's/^TOK=//'); [ -n "$tok" ] || { echo "no-token"; return; }
  chat $1 "/corerpg p1 gearlib bulk confirm tok:$tok" 4000 | grep -oE '已分解 [0-9]+ 件' | grep -oE '[0-9]+' | head -1; }
mkdups(){ for q in 0 0 0; do $C play "corerpg p1 givedup blade $1 $q" 0.5 >/dev/null; done; sleep 1; tomain $1; chat $1 "/corerpg p1 stash" 2000 >/dev/null; }

curl -sf "$BOTD/list" >/dev/null || { echo "FAIL botd not running"; exit 1; }
[ "$(curl -sf "$BOTD/list")" = "[]" ] || echo "note: other bots online: $(curl -sf "$BOTD/list")"

if want 1; then
echo "=================== phase 1 (before restart #1)"
for b in $BA $BD $BE; do join $b; online $b || { bad "$b online"; exit 1; }; done
UA=$(uuid $BA); UD=$(uuid $BD); UE=$(uuid $BE); echo "uuids $BA=$UA $BD=$UD $BE=$UE"

echo "=== a) warehouse deposit → abrupt disconnect"
$C play "ni give $BA mat_ember_shard 40" 0.6 >/dev/null; $C play "ni give $BA mat_ember_core_fragment 15" 0.6 >/dev/null; sleep 1
A_V0S=$(vault $UA mat_ember_shard); A_V0C=$(vault $UA mat_ember_core_fragment); A_B0S=$(count $BA 余烬碎片); A_B0C=$(count $BA 核心碎片)
echo "before: vault shard=$A_V0S core=$A_V0C · backpack shard=$A_B0S core=$A_B0C"
chat $BA "/corerpg p1 stash" 1200 | grep -E '存入|仓库' | head -2

echo "=== b) gear → 装备库 (2 pieces)"
for i in 1 2; do $C play "corerpg p1 give scorch blade 1 0 0 0 $BA" 0.8 >/dev/null; done; sleep 1; tomain $BA
G_BEFORE=$(sql "SELECT GROUP_CONCAT(item_uid) FROM cr_p1_item WHERE owner_uuid='$UA' AND state='stored'")
chat $BA "/corerpg p1 stash" 2000 | grep -E '装备库|存入' | head -2
UIDS=$(sql "SELECT item_uid FROM cr_p1_item WHERE owner_uuid='$UA' AND state='stored' ORDER BY updated_at DESC LIMIT 2" | tr '\n' ' '); read -r G1 G2 <<< "$UIDS"
echo "stored: $G1 $G2"; [ -n "${G2:-}" ] && ok "b: 2 pieces stored" || bad "b: stash did not store 2 pieces ($UIDS)"
# a) cut the connection ~0.3 s after a second deposit (the 2 s vault flush is still pending), then b) is disconnected too
$C play "ni give $BA mat_ember_shard 7" 0.6 >/dev/null; sleep 0.5
evalb $BA 'bot.chat("/corerpg p1 stash"); await wait(300); setTimeout(()=>{ try { bot._client.socket.destroy() } catch (e) {} }, 0); return "cut";' >/dev/null; sleep 5
online $BA && bad "a: bot still online after socket cut" || ok "a: abrupt disconnect"
A_V1S=$(vault $UA mat_ember_shard); A_V1C=$(vault $UA mat_ember_core_fragment)
chk "a: vault shard after abrupt quit (DB)" "$A_V1S" "$((A_V0S + A_B0S + 7))"; chk "a: vault core after abrupt quit (DB)" "$A_V1C" "$((A_V0C + A_B0C))"

echo "=== c) bulk dismantle → undo batch (no restart)"
join $BA
$C play "corerpg p1 give scorch blade 1 0 0 0 $BA" 1.0 >/dev/null
evalb $BA 'bot.setQuickBarSlot(0); await wait(200); let k=-1; for(let i=9;i<=44;i++){const x=bot.inventory.slots[i]; if(x&&/sword/.test(x.name)&&i!==36&&JSON.stringify(x.nbt||{}).includes("焚烬刃")) {k=i;break;}} if(k>=0) await bot.moveSlotItem(k,36); await wait(800); return String(k);' >/dev/null
mkdups $BA
C_ST0=$(sql "SELECT COUNT(*) FROM cr_p1_item WHERE owner_uuid='$UA' AND state='stored'"); C_BL0=$(vault $UA mat_ember_v1_blank)
N1=$(bulkrun $BA c1); echo "dismantled $N1 · stored before=$C_ST0 blanks before=$C_BL0 → after $(sql "SELECT COUNT(*) FROM cr_p1_item WHERE owner_uuid='$UA' AND state='stored'") / $(vault $UA mat_ember_v1_blank)"
[[ "$N1" =~ ^[0-9]+$ ]] && [ "$N1" -gt 0 ] && ok "c: bulk dismantled $N1" || bad "c: bulk dismantle ($N1)"
chat $BA "/corerpg p1 undo batch" 5000 | grep -E '整批|撤销' | head -2
chk "c: stored count back after undo batch" "$(sql "SELECT COUNT(*) FROM cr_p1_item WHERE owner_uuid='$UA' AND state='stored'")" "$C_ST0"
chk "c: blanks back after undo batch" "$(vault $UA mat_ember_v1_blank)" "$C_BL0"
echo "=== c2) bulk dismantle → (restart #1) → undo"
C2_ST0=$C_ST0; C2_BL0=$C_BL0
N2=$(bulkrun $BA c2); [[ "$N2" =~ ^[0-9]+$ ]] && [ "$N2" -gt 0 ] && ok "c2: bulk dismantled $N2 before restart" || bad "c2: bulk dismantle ($N2)"
C2_BL1=$(vault $UA mat_ember_v1_blank); sleep 3; quit $BA

echo "=== d) invsnap restore queued for an offline player"
$C play "ni give $BD mat_ember_core_fragment 12" 0.6 >/dev/null; sleep 1
D_SID=$($C play "corerpg invsnap take $BD" 1.5 | tr -d '\r' | grep -oE '快照 #[0-9]+' | head -1 | grep -oE '[0-9]+')
$C play "clear $BD" 1.0 >/dev/null; sleep 1; D_C0=$(count $BD 核心碎片); echo "snapshot #$D_SID · core after clear=$D_C0"
quit $BD; sleep 3
$C play "corerpg invsnap restore $BD $D_SID" 2.0 | grep -E 'invsnap|排队|下次|queued' | sed 's/^.*INFO\]: //' | head -2
grep -q "$UD" $M/plugins/CoreRpg/invsnap-pending.yml && ok "d: restore queued in invsnap-pending.yml" || bad "d: not queued"

echo "=== e) gacha 10-pull → disconnect right after the command"
$C play "gacha admin give-tickets $BE 10" 1.0 >/dev/null; sleep 1
E_W0=$(sql "SELECT CONCAT(tickets,':',shards) FROM gacha_wallet WHERE uuid='$UE'"); E_P0=$(sql "SELECT COUNT(*) FROM gacha_pull WHERE uuid='$UE'"); E_O0=$(sql "SELECT COUNT(*) FROM gacha_owned WHERE uuid='$UE'")
echo "before: wallet $E_W0 pulls=$E_P0 owned=$E_O0"
evalb $BE 'bot.chat("/gacha pull standard 10"); setTimeout(()=>{ try { bot._client.socket.destroy() } catch (e) {} }, 30); return "cut";' >/dev/null; sleep 5
gcheck(){ # $1 label → consistency of wallet / ledger / pulls / owned / CoreRpg flags
  local w lt p o b; w=$(sql "SELECT CONCAT(tickets,':',shards) FROM gacha_wallet WHERE uuid='$UE'")
  lt=$(sql "SELECT CONCAT(tickets_after,':',shards_after) FROM gacha_ledger WHERE uuid='$UE' ORDER BY id DESC LIMIT 1")
  p=$(sql "SELECT COUNT(*) FROM gacha_pull WHERE uuid='$UE'"); o=$(sql "SELECT COUNT(*) FROM gacha_owned WHERE uuid='$UE'")
  echo "$1: wallet $w ledger-after $lt pulls=$p owned=$o"
  chk "e $1: wallet = last ledger row" "$w" "$lt"
  local t0=${E_W0%%:*}; local t1=${w%%:*}
  if [ "$p" = "$((E_P0 + 10))" ]; then chk "e $1: 10 tickets spent for 10 pulls" "$t1" "$((t0 - 10))"
    local bt nd; bt=$(sql "SELECT batch_id FROM gacha_pull WHERE uuid='$UE' ORDER BY pull_id DESC LIMIT 1")
    nd=$(sql "SELECT COUNT(*) FROM gacha_pull WHERE uuid='$UE' AND dup=0 AND batch_id='$bt'")
    chk "e $1: owned grew by the non-duplicate pulls" "$o" "$((E_O0 + nd))"
    chk "e $1: ledger has exactly one pull row for the batch" "$(sql "SELECT COUNT(*) FROM gacha_ledger WHERE uuid='$UE' AND reason='pull' AND ref='$bt'")" "1"
    # CoreRpg-side cosmetics (kind: corerpg in gacha.yml) must carry the CoreRpg owned flag p2_cosbuy_<ref>
    local data miss=0 it ref; data=$(sql "SELECT data FROM cr_players WHERE uuid='$UE'")
    for it in $(sql "SELECT item FROM gacha_owned WHERE uuid='$UE' AND batch_id='$bt'"); do
      ref=$(grep -E "^  $it: .*kind: corerpg" $M/plugins/CoreGacha/gacha.yml | grep -oE 'ref: [a-z0-9_]+' | cut -d' ' -f2)
      [ -n "$ref" ] && ! grep -q "p2_cosbuy_$ref" <<< "$data" && { miss=$((miss+1)); echo "  missing CoreRpg flag for $it ($ref)"; }
    done
    chk "e $1: CoreRpg cosmetic flags for the batch's corerpg items" "$miss" "0"
  elif [ "$p" = "$E_P0" ]; then chk "e $1: no pull → no tickets spent" "$t1" "$t0"; else bad "e $1: pulls $p (neither +0 nor +10)"; fi; }
gcheck "before restart"

echo "=================== restart #1"
restart 1
echo "=================== phase 2 (after restart #1)"
join $BA; join $BD; join $BE; sleep 3
echo "=== a) withdraw after restart"
chk "a: deposited shards not back in backpack" "$(count $BA 余烬碎片)" "0"
chat $BA "/corerpg p1 vault take mat_ember_shard $((A_B0S + 7))" 1500 | grep -E '取出|背包' | head -1
chat $BA "/corerpg p1 vault take mat_ember_core_fragment $A_B0C" 1500 >/dev/null; sleep 3
chk "a: backpack shard after withdraw" "$(count $BA 余烬碎片)" "$((A_B0S + 7))"; chk "a: backpack core after withdraw" "$(count $BA 核心碎片)" "$A_B0C"
chk "a: vault shard back to before" "$(vault $UA mat_ember_shard)" "$A_V0S"; chk "a: vault core back to before" "$(vault $UA mat_ember_core_fragment)" "$A_V0C"
echo "=== c2) undo after restart (inside the window)"
chat $BA "/corerpg p1 undo batch" 5000 | grep -E '整批|撤销|没有' | head -2
chk "c2: stored count back after restart + undo" "$(sql "SELECT COUNT(*) FROM cr_p1_item WHERE owner_uuid='$UA' AND state='stored'")" "$C2_ST0"
chk "c2: blanks back after restart + undo" "$(vault $UA mat_ember_v1_blank)" "$C2_BL0"
echo "=== b) withdraw after restart; withdraw + disconnect in the same tick"
chk "b: $G1 still stored after restart" "$(gstate $G1 | cut -d: -f1)" "stored"
chat $BA "/corerpg p1 gearlib take $G1" 2500 | grep -E '取出' | head -1
chk "b: $G1 active after withdraw" "$(gstate $G1 | cut -d: -f1)" "active"; chk "b: $G1 stack in backpack (1)" "$(hasuid $BA $G1)" "1"
evalb $BA "bot.chat('/corerpg p1 gearlib take $G2'); setTimeout(()=>{ try { bot._client.socket.destroy() } catch (e) {} }, 0); return 'cut';" >/dev/null; sleep 5
B_ST2=$(gstate $G2); echo "mid-transaction piece $G2 → $B_ST2"
join $BA; sleep 2; B_H2=$(hasuid $BA $G2)
if [ "${B_ST2%%:*}" = "stored" ]; then chk "b: mid-txn piece back in library, no stack" "$B_H2" "0"; else chk "b: mid-txn piece active → stack must be in backpack" "$B_H2" "1"; fi
echo "=== d) queued restore applied once"
sleep 3; D_C1=$(count $BD 核心碎片); chk "d: queued restore applied at join (core)" "$D_C1" "12"
grep -q "$UD" $M/plugins/CoreRpg/invsnap-pending.yml && bad "d: still queued after apply" || ok "d: queue entry cleared"
echo "=== e) after restart"
gcheck "after restart"
$C play "gacha admin inspect $BE" 1.2 | grep -E '券|光屑' | sed 's/^.*INFO\]: //' | head -2
B2_ST=$(gstate $G2); B2_H=$(hasuid $BA $G2)
quit $BA; quit $BD; quit $BE; sleep 3

echo "=================== restart #2"
restart 2
join $BA; join $BD; sleep 3
D_C2=$(count $BD 核心碎片); chk "d: not applied twice after a 2nd restart (core)" "$D_C2" "12"
B3_ST=$(gstate $G2); B3_H=$(hasuid $BA $G2); chk "b: mid-txn piece state stable over restart" "${B3_ST%%:*}" "${B2_ST%%:*}"
chk "b: mid-txn piece stack stable over restart" "$B3_H" "$B2_H"
chk "b: $G1 stack still once in backpack" "$(hasuid $BA $G1)" "1"
quit $BA; quit $BD; sleep 2

echo "=================== f) kill -9 while online after deposits"
join $BF; UF=$(uuid $BF); echo "uuid $BF=$UF"
$C play "ni give $BF mat_ember_shard 50" 0.6 >/dev/null; $C play "ni give $BF mat_ember_core_fragment 20" 0.6 >/dev/null
$C play "corerpg p1 give scorch blade 1 0 0 0 $BF" 0.8 >/dev/null; sleep 1; tomain $BF
$C play "save-all" 3.0 >/dev/null   # the materials + blade are now in the saved .dat (like a player who had them > 5 min)
F_V0S=$(vault $UF mat_ember_shard); F_V0C=$(vault $UF mat_ember_core_fragment)
chat $BF "/corerpg p1 stash" 2500 | grep -E '存入|装备库' | head -2
FG=$(sql "SELECT item_uid FROM cr_p1_item WHERE owner_uuid='$UF' AND state='stored' ORDER BY updated_at DESC LIMIT 1")
sleep 1; chat $BF "/corerpg p1 vault take mat_ember_shard 10" 1500 >/dev/null
sleep 4   # past the documented 2 s warehouse flush window
F_VS=$(vault $UF mat_ember_shard); F_VC=$(vault $UF mat_ember_core_fragment); F_BS=$(count $BF 余烬碎片); F_BC=$(count $BF 核心碎片)
echo "before kill: vault shard=$F_VS core=$F_VC · backpack shard=$F_BS core=$F_BC · gear $FG $(gstate $FG)"
killhard
# the proxy moved the still-connected bot to the fallback server: drop that session, then a real rejoin of play
quit $BF; sleep 4; join $BF; sleep 3
grep -q "$BF.*logged in" $RT/logs/latest.log && ok "f: $BF really rejoined the restarted play server" || bad "f: $BF not logged in to play after kill -9"
G_VS=$(vault $UF mat_ember_shard); G_VC=$(vault $UF mat_ember_core_fragment); G_BS=$(count $BF 余烬碎片); G_BC=$(count $BF 核心碎片)
echo "after kill -9: vault shard=$G_VS core=$G_VC · backpack shard=$G_BS core=$G_BC · gear $(gstate $FG) stacks=$(hasuid $BF $FG)"
chk "f: shards total (vault + backpack) unchanged" "$((G_VS + G_BS))" "$((F_VS + F_BS))"
chk "f: core total (vault + backpack) unchanged" "$((G_VC + G_BC))" "$((F_VC + F_BC))"
chk "f: withdrawn 10 shards in backpack (not lost)" "$G_BS" "$F_BS"
chk "f: stashed blade: no stack left in backpack" "$(hasuid $BF $FG)" "0"
chk "f: stashed blade still stored" "$(gstate $FG | cut -d: -f1)" "stored"
quit $BF
fi

# ---- helpers for g / h
coin(){ sql "SELECT data FROM cr_players WHERE uuid='$1'" | grep -oE '(^|\\n)coin: [0-9]+' | grep -oE '[0-9]+$' | head -1; }
# snap BOT UUID → "blank shard core coin" totals over warehouse + backpack (+ coin)
snap(){ echo "$(( $(vault $2 mat_ember_v1_blank) + $(count $1 胚料) )) $(( $(vault $2 mat_ember_shard) + $(count $1 余烬碎片) )) $(( $(vault $2 mat_ember_core_fragment) + $(count $1 核心碎片) )) $(coin $2)"; }
pend(){ sql "SELECT COUNT(*) FROM cr_p1_delivery WHERE owner_uuid='$1' AND status IN ('pending','hold')"; }
dstat(){ sql "SELECT GROUP_CONCAT(status ORDER BY id) FROM cr_p1_delivery WHERE owner_uuid='$1' AND request_id LIKE '$2%'"; }
arm(){ $C play "corerpg p1 fault $1 $2" 0.8 | grep -q "fault armed" && ok "g: armed $2 for $1" || bad "g: could not arm $2 for $1"; }
disarm(){ $C play "corerpg p1 fault $1 clear" 0.5 >/dev/null; }
rejoin(){ quit $1; sleep 3; join $1; sleep 6; }
newblade(){ # $1 bot $2 uuid → uid of a freshly given T1 blade, moved to hotbar 0 and selected
  $C play "corerpg p1 give scorch blade 1 0 0 0 $1" 1.0 >/dev/null; sleep 1
  local u; u=$(sql "SELECT item_uid FROM cr_p1_item WHERE owner_uuid='$2' AND state='active' ORDER BY created_at DESC LIMIT 1")
  evalb $1 "bot.setQuickBarSlot(0); await wait(200); let k=-1; for(let i=9;i<=44;i++){const x=bot.inventory.slots[i]; if(x&&JSON.stringify(x.nbt||{}).includes('$u')){k=i;break;}} if(k>=0&&k!==36) await bot.moveSlotItem(k,36); await wait(600); return String(k);" >/dev/null
  echo "$u"; }
stashone(){ # $1 bot $2 uuid → uid of a T1 blade put into the library
  $C play "corerpg p1 give scorch blade 1 0 0 0 $1" 1.0 >/dev/null; sleep 1; tomain $1
  local u; u=$(sql "SELECT item_uid FROM cr_p1_item WHERE owner_uuid='$2' AND state='active' ORDER BY created_at DESC LIMIT 1")
  chat $1 "/corerpg p1 stash" 2500 >/dev/null; echo "$u"; }
if [ "${NOG:-}" != 1 ] && want g; then
echo "=================== g) fault injection (D162)"
export CORERPG_TEST_FAULTS=1; restart g-faults-on; unset CORERPG_TEST_FAULTS
join $BG1; join $BG2; UG1=$(uuid $BG1); UG2=$(uuid $BG2); echo "uuids $BG1=$UG1 $BG2=$UG2"
online $BG1 && online $BG2 || { bad "g: bots online"; }
for b in $BG1 $BG2; do $C play "ni give $b mat_ember_shard 60" 0.5 >/dev/null; $C play "ni give $b mat_ember_core_fragment 30" 0.5 >/dev/null; $C play "corerpg coin give $b 3000" 0.5 >/dev/null; done; sleep 1
chat $BG1 "/corerpg p1 stash" 1500 >/dev/null; chat $BG2 "/corerpg p1 stash" 1500 >/dev/null; sleep 3

echo "=== g1) bulk dismantle · after_commit (first callback held 10 s) · abrupt disconnect in the window → blanks at the next join, once"
newblade $BG1 $UG1 >/dev/null; mkdups $BG1; S0=$(snap $BG1 $UG1); T0=$(( $(date +%s) * 1000 )); echo "start: $S0 · stored $(sql "SELECT COUNT(*) FROM cr_p1_item WHERE owner_uuid='$UG1' AND state='stored'")"
arm $BG1 after_commit; bulkrun $BG1 g1 >/dev/null
OWED=$(sql "SELECT COALESCE(SUM(amount),0) FROM cr_p1_delivery WHERE owner_uuid='$UG1' AND request_id LIKE 'glibdis:%' AND created_at>=$T0")
NDIS=$(sql "SELECT COUNT(*) FROM cr_p1_txn WHERE owner_uuid='$UG1' AND kind='glibdis' AND created_at>=$T0")
echo "dismantled $NDIS, blanks owed $OWED (rows: $(dstat $UG1 glibdis:))"
[ "$NDIS" -gt 0 ] && ok "g1: $NDIS pieces dismantled, $OWED blanks owed in delivery rows" || bad "g1: nothing dismantled"
chk "g1: one delivery row per dismantle" "$(sql "SELECT COUNT(*) FROM cr_p1_delivery WHERE owner_uuid='$UG1' AND request_id LIKE 'glibdis:%' AND created_at>=$T0")" "$NDIS"
abrupt $BG1; sleep 8; join $BG1; sleep 6; disarm $BG1
chk "g1: blanks = start + owed (exactly once)" "$(snap $BG1 $UG1 | cut -d' ' -f1)" "$(( $(echo $S0 | cut -d' ' -f1) + OWED ))"
chk "g1: no pending delivery left" "$(pend $UG1)" "0"
rejoin $BG1; chk "g1: blanks unchanged after another rejoin" "$(snap $BG1 $UG1 | cut -d' ' -f1)" "$(( $(echo $S0 | cut -d' ' -f1) + OWED ))"
UNDO_UID=$(sql "SELECT uid_a FROM cr_p1_txn WHERE owner_uuid='$UG1' AND kind='glibdis' AND created_at>=$T0 ORDER BY created_at DESC LIMIT 1")

echo "=== g2) withdraw · before_commit → rolled back, nothing owed"
P1=$(stashone $BG1 $UG1); echo "stored $P1 $(gstate $P1)"
arm $BG1 before_commit; chat $BG1 "/corerpg p1 gearlib take $P1" 2500 | grep -E '取出' | head -1; disarm $BG1
chk "g2: piece still stored" "$(gstate $P1 | cut -d: -f1)" "stored"; chk "g2: no stack" "$(hasuid $BG1 $P1)" "0"
chk "g2: no delivery row" "$(sql "SELECT COUNT(*) FROM cr_p1_delivery WHERE request_id LIKE 'unstash:$P1:%'")" "0"
abrupt $BG1; join $BG1; sleep 6; chk "g2: still stored, no stack after disconnect + rejoin" "$(gstate $P1 | cut -d: -f1):$(hasuid $BG1 $P1)" "stored:0"

echo "=== g3) withdraw · after_commit (callback held 10 s) · abrupt disconnect in the window → stack at the next join, once"
arm $BG1 after_commit; chat $BG1 "/corerpg p1 gearlib take $P1" 1500 >/dev/null
chk "g3: row active after commit" "$(gstate $P1 | cut -d: -f1)" "active"; chk "g3: callback not run yet → no stack" "$(hasuid $BG1 $P1)" "0"
chk "g3: delivery row pending" "$(dstat $UG1 unstash:$P1)" "pending"
abrupt $BG1; sleep 8; join $BG1; sleep 6; disarm $BG1
chk "g3: stack delivered at join (1)" "$(hasuid $BG1 $P1)" "1"; chk "g3: delivery row delivered" "$(dstat $UG1 unstash:$P1)" "delivered"
rejoin $BG1; chk "g3: still exactly 1 stack after another rejoin" "$(hasuid $BG1 $P1)" "1"

echo "=== g4) undo · after_commit (callback held 10 s) · abrupt disconnect → blanks taken UP FRONT exactly once (D172), hold void"
if [ -n "$UNDO_UID" ]; then
  S4=$(snap $BG1 $UG1); Y=$(sql "SELECT amount FROM cr_p1_delivery WHERE owner_uuid='$UG1' AND request_id LIKE 'glibdis:$UNDO_UID:%'")
  arm $BG1 after_commit; chat $BG1 "/corerpg p1 undo $UNDO_UID" 3000 | grep -E '撤销|扣' | head -2
  chk "g4: blanks taken before the commit callback (start − $Y)" "$(snap $BG1 $UG1 | cut -d' ' -f1)" "$(( $(echo $S4 | cut -d' ' -f1) - Y ))"
  chk "g4: no debit delivery row any more (D172)" "$(sql "SELECT COUNT(*) FROM cr_p1_delivery WHERE request_id LIKE 'undo:$UNDO_UID:%'")" "0"
  abrupt $BG1; sleep 8; join $BG1; sleep 6; disarm $BG1
  chk "g4: piece back in library" "$(gstate $UNDO_UID | cut -d: -f1)" "stored"
  chk "g4: after rejoin blanks still start − $Y (not taken twice, not refunded)" "$(snap $BG1 $UG1 | cut -d' ' -f1)" "$(( $(echo $S4 | cut -d' ' -f1) - Y ))"
  chk "g4: refund hold voided by the commit" "$(dstat $UG1 refund:undo:$UNDO_UID)" "void"
else bad "g4: no dismantled piece to undo"; fi

echo "=== g5a) forge enhance · before_commit → hold released → refund once (materials + coins conserved)"
E2=$(newblade $BG2 $UG2); S5=$(snap $BG2 $UG2); R5=$(gstate $E2); echo "blade $E2 $R5 · start $S5"
arm $BG2 before_commit; chat $BG2 "/corerpg p1 enhance confirm" 3000 | grep -E '未完成|退回|到账|强化' | head -3; sleep 4; disarm $BG2
chk "g5a: item unchanged" "$(gstate $E2)" "$R5"
chk "g5a: totals back to start (refund delivered once)" "$(snap $BG2 $UG2)" "$S5"
chk "g5a: refund rows delivered" "$(dstat $UG2 refund:enh:$E2 | tr ',' '\n' | sort -u | tr '\n' ' ')" "delivered "

echo "=== kill -9 bundle: g6 withdraw after_deliver (BG2) · then inside 10 s: g3k withdraw after_commit (BG1) + g5b enhance after_commit (BG2) → kill -9"
P3=$(stashone $BG2 $UG2); P2=$(stashone $BG1 $UG1)
evalb $BG2 "bot.setQuickBarSlot(0); await wait(200); let k=-1; for(let i=9;i<=44;i++){const x=bot.inventory.slots[i]; if(x&&JSON.stringify(x.nbt||{}).includes('$E2')){k=i;break;}} if(k>=0&&k!==36) await bot.moveSlotItem(k,36); await wait(500); return String(k);" >/dev/null
arm $BG2 after_deliver; chat $BG2 "/corerpg p1 gearlib take $P3" 3000 >/dev/null; sleep 2; disarm $BG2
chk "g6: stack handed out before the kill" "$(hasuid $BG2 $P3)" "1"; chk "g6: ack skipped → pending" "$(dstat $UG2 unstash:$P3)" "pending"
S5B=$(snap $BG2 $UG2); R5B=$(gstate $E2)
arm $BG1 after_commit; arm $BG2 after_commit
evalb $BG1 "bot.chat('/corerpg p1 gearlib take $P2'); return 'x';" >/dev/null; evalb $BG2 "bot.chat('/corerpg p1 enhance confirm'); return 'x';" >/dev/null; sleep 2
chk "g3k: committed, callback not run → no stack before the kill" "$(gstate $P2 | cut -d: -f1):$(hasuid $BG1 $P2)" "active:0"
R5C=$(gstate $E2); echo "enhance: $R5B → $R5C (callback held)"
COST=$(sql "SELECT cost_json FROM cr_p1_txn WHERE request_id='enh:$E2:${R5B##*:}'"); echo "enhance cost $COST"
killhard
quit $BG1; quit $BG2; sleep 4; join $BG1; join $BG2; sleep 8
chk "g3k: stack delivered once after kill -9" "$(hasuid $BG1 $P2)" "1"; chk "g3k: row delivered" "$(dstat $UG1 unstash:$P2)" "delivered"
chk "g6: still exactly one stack after kill -9 (marker/uid → ack only)" "$(hasuid $BG2 $P3)" "1"; chk "g6: row delivered" "$(dstat $UG2 unstash:$P3)" "delivered"
if [ -n "$COST" ]; then
  cs=$(echo "$COST" | jq -r '.shard'); cc=$(echo "$COST" | jq -r '.core'); cb=$(echo "$COST" | jq -r '.blank'); co=$(echo "$COST" | jq -r '.coin')
  read -r b0 s0 c0 m0 <<< "$S5B"
  chk "g5b: enhance committed → cost paid exactly once" "$(snap $BG2 $UG2)" "$((b0 - cb)) $((s0 - cs)) $((c0 - cc)) $((m0 - co))"
  chk "g5b: refund hold of the committed attempt voided, none left on hold" "$(sql "SELECT CONCAT(SUM(status='hold'),':',SUM(status='void')) FROM cr_p1_delivery WHERE request_id='refund:enh:$E2:${R5B##*:}'")" "0:2"
else chk "g5b: enhance not committed → totals unchanged" "$(snap $BG2 $UG2)" "$S5B"; fi
chk "g5a: before_commit result stable over kill -9 (rev)" "$(gstate $E2 | cut -d: -f2)" "${R5C##*:}"
chk "g: no pending / hold delivery rows left ($BG1)" "$(pend $UG1)" "0"; chk "g: no pending / hold delivery rows left ($BG2)" "$(pend $UG2)" "0"
rejoin $BG1; rejoin $BG2
# P1 went back into the library with the 一键存入 of stashone (state stored, no stack) — either way exactly one copy
P1S="$(gstate $P1 | cut -d: -f1):$(hasuid $BG1 $P1)"; [ "$P1S" = "active:1" ] || [ "$P1S" = "stored:0" ] && ok "g: $P1 exactly one copy ($P1S)" || bad "g: $P1 copies ($P1S)"
chk "g: stacks stable after a further rejoin" "$(hasuid $BG1 $P2):$(hasuid $BG2 $P3)" "1:1"
quit $BG1; quit $BG2
echo "=================== back to normal (no fault hooks)"
restart g-faults-off
$C play "corerpg p1 fault $BG1 after_commit" 0.8 | grep -q "测试环境" && ok "g: fault hook refused without CORERPG_TEST_FAULTS" || bad "g: fault hook still active"
fi

if want h; then
echo "=================== h) D172 asset fixes: undo pays up front (X5) · mark redemption (X1/X4) · reroll (X15)"
# p1 counters live in cr_players.data as "key@all: N"
ctr(){ local v; v=$(sql "SELECT data FROM cr_players WHERE uuid='$1'" | grep -oE "$2@all: -?[0-9]+" | grep -oE -- '-?[0-9]+$' | head -1); echo "${v:-0}"; }
blanks(){ snap $1 $2 | cut -d' ' -f1; }
bone(){ echo $(( $(vault $2 mat_ember_bone_dust) + $(count $1 骨尘) )); }
craft(){ sql "SELECT craft FROM cr_p1_item WHERE item_uid='$1'"; }
nstored(){ sql "SELECT COUNT(*) FROM cr_p1_item WHERE owner_uuid='$1' AND state='stored'"; }
holds(){ sql "SELECT COUNT(*) FROM cr_p1_delivery WHERE owner_uuid='$1' AND status='hold'"; }
# lines of all play logs written since the phase started (latest.log + the rotated .gz of this phase)
hlogs(){ { for f in $(find $RT/logs -name '*.log.gz' -newer /tmp/persist-h.start 2>/dev/null); do zcat "$f"; done; cat $RT/logs/latest.log; } 2>/dev/null; }
ndel(){ sql "SELECT COUNT(*) FROM cr_p1_delivery WHERE owner_uuid='$1' AND request_id LIKE '$2%' AND status='delivered'"; }
nhold(){ sql "SELECT COUNT(*) FROM cr_p1_delivery WHERE owner_uuid='$1' AND request_id LIKE '$2%' AND status='hold'"; }
# kill -9 that comes back up WITH the test fault hooks (killhard starts play from this shell's environment)
killfaults(){ export CORERPG_TEST_FAULTS=1; killhard; unset CORERPG_TEST_FAULTS; }
lastdis(){ sql "SELECT t.uid_a FROM cr_p1_txn t JOIN cr_p1_item i ON i.item_uid=t.uid_a WHERE t.owner_uuid='$1' AND t.kind='glibdis' AND i.state='dismantled' ORDER BY t.created_at DESC LIMIT 1"; }
touch /tmp/persist-h.start
export CORERPG_TEST_FAULTS=1; restart h-faults-on; unset CORERPG_TEST_FAULTS
join $BH1; join $BH2; join $BH3; UH1=$(uuid $BH1); UH2=$(uuid $BH2); UH3=$(uuid $BH3); echo "uuids $BH1=$UH1 $BH2=$UH2 $BH3=$UH3"
online $BH1 && online $BH2 && online $BH3 || bad "h: bots online"
$C play "ni give $BH1 mat_ember_bone_dust 30" 0.5 >/dev/null; $C play "corerpg coin give $BH1 5000" 0.5 >/dev/null
$C play "ni give $BH2 mat_ember_shard 400" 0.5 >/dev/null; $C play "corerpg coin give $BH2 8000" 0.5 >/dev/null
$C play "corerpg p1 runs marks $BH3 1 40" 0.8 >/dev/null; sleep 1
chat $BH1 "/corerpg p1 stash" 1500 >/dev/null; chat $BH2 "/corerpg p1 stash" 1500 >/dev/null; sleep 3

# ---------------------------------------------------------------- h1: undo vs a forge refine on the same blanks
RACE=()
race(){ # $1 label $2 order: undo | refine (both fired in the same tick) | window (undo armed after_pay → refine fired in the
  # 10 s between the undo's payment and its commit, the exact pre-1.65.5 hole) → asserts exactly one wins and blanks / bone / coins are conserved
  local H N need tot b0 bo0 c0 st0 out
  H=$(newblade $BH1 $UH1); [ "$(sql "SELECT COUNT(*) FROM cr_p1_item i JOIN cr_p1_txn t ON t.uid_a=i.item_uid WHERE t.owner_uuid='$UH1' AND t.kind='glibdis' AND i.state='dismantled' AND t.created_at>=$(( ($(date +%s) - 300) * 1000 ))")" -gt 0 ] || { mkdups $BH1; bulkrun $BH1 $1 >/dev/null; sleep 2; }
  N=$(sql "SELECT COUNT(*) FROM cr_p1_item i JOIN cr_p1_txn t ON t.uid_a=i.item_uid WHERE t.owner_uuid='$UH1' AND t.kind='glibdis' AND i.state='dismantled' AND t.created_at>=$(( ($(date +%s) - 300) * 1000 ))")
  need=$(( N > 3 ? N : 3 )); tot=$(blanks $BH1 $UH1); [ "$tot" -lt "$need" ] && { $C play "ni give $BH1 mat_ember_v1_blank $((need - tot))" 0.6 >/dev/null; sleep 1; chat $BH1 "/corerpg p1 stash" 1500 >/dev/null; sleep 2; }
  b0=$(blanks $BH1 $UH1); bo0=$(bone $BH1 $UH1); c0=$(coin $UH1); st0=$(nstored $UH1)
  echo "$1: blade $H craft $(craft $H) · $N dismantled pieces in the batch (undo needs $N) · refine needs 3 · blanks $b0 · bone $bo0 · coin $c0"
  [ "$b0" -lt "$((N + 3))" ] && ok "$1: blanks ($b0) cover either op alone, not both" || bad "$1: race not tight (blanks $b0, need $N + 3)"
  if [ "$2" = window ]; then arm $BH1 after_pay
    out=$(evalb $BH1 'const f=bot.chatLog.length; bot.chat("/corerpg p1 undo batch"); await wait(2500); bot.chat("/corerpg p1 refine confirm"); await wait(12000); return bot.chatLog.slice(f).map(s=>s.replace(/\u00a7./g,"")).join("\n");'); disarm $BH1
  elif [ "$2" = undo ]; then out=$(evalb $BH1 'const f=bot.chatLog.length; bot.chat("/corerpg p1 undo batch"); bot.chat("/corerpg p1 refine confirm"); await wait(6000); return bot.chatLog.slice(f).map(s=>s.replace(/\u00a7./g,"")).join("\n");')
  else out=$(evalb $BH1 'const f=bot.chatLog.length; bot.chat("/corerpg p1 refine confirm"); bot.chat("/corerpg p1 undo batch"); await wait(6000); return bot.chatLog.slice(f).map(s=>s.replace(/\u00a7./g,"")).join("\n");'); fi
  echo "$out" | grep -E '撤销|胚料|精工|不够|不足|扣' | head -4 | sed 's/^/    /'
  sleep 3; wonU=$(( $(nstored $UH1) - st0 == N ? 1 : 0 )); wonR=$(( $(craft $H) > 0 ? 1 : 0 ))
  RACE+=("$1 $2: undo=$wonU refine=$wonR"); echo "$1: undo won=$wonU (stored $st0→$(nstored $UH1)) · refine won=$wonR (craft $(craft $H))"
  chk "$1: exactly one of undo / refine went through" "$((wonU + wonR))" "1"
  chk "$1: blanks = start − cost of the winner" "$(blanks $BH1 $UH1)" "$(( b0 - wonU * N - wonR * 3 ))"
  chk "$1: bone dust = start − refine cost if refine won" "$(bone $BH1 $UH1)" "$(( bo0 - wonR * 5 ))"
  chk "$1: coins = start − refine cost if refine won" "$(coin $UH1)" "$(( c0 - wonR * 300 ))"
  chk "$1: no hold / pending delivery left" "$(pend $UH1)" "0"; }
echo "=== h1a) undo batch + forge refine fired in the same tick, undo first"
race h1a undo
echo "=== h1b) the same, refine first"
race h1b refine
echo "=== h1x) undo batch paid, commit held 10 s (after_pay) → refine spends the same blanks inside the window"
race h1x window
chk "h1x: the undo (paid first) is the one that went through" "$wonU" "1"
chk "h1: no 'debit short' (the old post-hoc debit) anywhere in the log" "$(hlogs | grep -c 'debit short')" "0"
chk "h1: no undo debit delivery rows (D172)" "$(sql "SELECT COUNT(*) FROM cr_p1_delivery WHERE owner_uuid='$UH1' AND request_id LIKE 'undo:%'")" "0"

echo "=== h1c) undo · before_commit → rolled back: blanks refunded once, piece stays dismantled"
[ -n "$(lastdis $UH1)" ] || { mkdups $BH1; bulkrun $BH1 h1c >/dev/null; sleep 2; }
UU=$(lastdis $UH1); [ "$(blanks $BH1 $UH1)" -ge 1 ] || { $C play "ni give $BH1 mat_ember_v1_blank 2" 0.6 >/dev/null; sleep 1; }
B1=$(blanks $BH1 $UH1); R1=$(gstate $UU); D1=$(ndel $UH1 refund:undo:$UU); echo "piece $UU $R1 · blanks $B1"
arm $BH1 before_commit; chat $BH1 "/corerpg p1 undo $UU" 3000 | grep -E '撤销|退|扣' | head -2; sleep 4; disarm $BH1
chk "h1c: piece unchanged (dismantled)" "$(gstate $UU)" "$R1"
chk "h1c: blanks back to start (refund delivered once)" "$(blanks $BH1 $UH1)" "$B1"
chk "h1c: refund delivered once, no hold left" "$(ndel $UH1 refund:undo:$UU):$(nhold $UH1 refund:undo:$UU)" "$((D1 + 1)):0"
chk "h1c: no undo txn for this dismantle (rid undo:<uid>:<rev>)" "$(sql "SELECT COUNT(*) FROM cr_p1_txn WHERE request_id='undo:$UU:${R1##*:}'")" "0"

# ---------------------------------------------------------------- h2: mark redemption
redeemed(){ sql "SELECT COUNT(*) FROM cr_p1_txn WHERE owner_uuid='$UH3' AND kind='mark_redeem'"; }
newest(){ sql "SELECT uid_a FROM cr_p1_txn WHERE owner_uuid='$UH3' AND kind='mark_redeem' ORDER BY created_at DESC LIMIT 1"; }
X="/corerpg p1 marks exchange scorch blade 1 confirm"
echo "=== h2a) redeem 8 T1 marks → one blade, ledger row + mark counter in one transaction"
M0=$(ctr $UH3 p1_mark_t1); K0=$(redeemed); echo "marks $M0 · redemptions $K0"
chat $BH3 "$X" 3000 | grep -E '兑换|印记' | head -2; sleep 2; G=$(newest)
chk "h2a: marks −8" "$(ctr $UH3 p1_mark_t1)" "$((M0 - 8))"; chk "h2a: one mark_redeem txn" "$(redeemed)" "$((K0 + 1))"
chk "h2a: the blade is in the backpack once" "$(hasuid $BH3 $G)" "1"; chk "h2a: gear delivery row delivered" "$(sql "SELECT GROUP_CONCAT(status) FROM cr_p1_delivery WHERE owner_uuid='$UH3' AND kind='gear' AND item='$G'")" "delivered"
chk "h2a: item row active" "$(gstate $G | cut -d: -f1)" "active"; chk "h2a: no hold left" "$(holds $UH3)" "0"
echo "=== h2b) redeem · before_commit → marks refunded once, no item, no txn"
M0=$(ctr $UH3 p1_mark_t1); K0=$(redeemed); D3=$(ndel $UH3 refund:redeem:)
arm $BH3 before_commit; chat $BH3 "$X" 3000 | grep -E '兑换|印记|退' | head -2; sleep 4; disarm $BH3
chk "h2b: marks unchanged after the refund" "$(ctr $UH3 p1_mark_t1)" "$M0"; chk "h2b: no new txn" "$(redeemed)" "$K0"
chk "h2b: one mark refund row delivered (kind mark)" "$(( $(ndel $UH3 refund:redeem:) - D3 )):$(sql "SELECT kind FROM cr_p1_delivery WHERE owner_uuid='$UH3' AND request_id LIKE 'refund:redeem:%' AND status='delivered' ORDER BY id DESC LIMIT 1")" "1:mark"
chk "h2b: no pending / hold" "$(pend $UH3)" "0"

# ---------------------------------------------------------------- h3: affix reroll on the durable payment path
RB=$(newblade $BH2 $UH2); echo "reroll blade $RB (T1, empty affix slot)"
read -r _ SH0 _ CO0 <<< "$(snap $BH2 $UH2)"; echo "start shards $SH0 coin $CO0"
RR="/corerpg p1 reroll blade shard confirm"
kshard(){ sql "SELECT COUNT(*) FROM cr_p1_txn WHERE owner_uuid='$UH2' AND request_id LIKE 'afx:$RB:%' AND uid_a='$RB'"; }
lastrid(){ sql "SELECT request_id FROM cr_p1_txn WHERE owner_uuid='$UH2' AND request_id LIKE 'afx:$RB:%' ORDER BY created_at DESC LIMIT 1"; }
kall(){ sql "SELECT COUNT(*) FROM cr_p1_txn WHERE owner_uuid='$UH2' AND request_id LIKE 'afx:$RB:%'"; }
# D208 (ARCH S1-4): the roll commits INSIDE the settling item transaction (affix / af_pity / reroll_n on cr_p1_item,
# data_version 2); request ids are afx:<uid>:<n>:<base36 ms> (never reused); no p4_rro_ "owed roll" counter any more
rolls(){ hlogs | grep -E "reroll $RB afx:$RB:[0-9]+:[0-9a-z]+ .* → " | grep -oE "afx:$RB:[0-9]+:[0-9a-z]+" | sort | uniq -c | awk '{print $1}' | sort -u | tr '\n' ' '; }
nrolls(){ hlogs | grep -E "reroll $RB afx:$RB:[0-9]+:[0-9a-z]+ .* → " | wc -l; }
icol(){ sql "SELECT $2 FROM cr_p1_item WHERE item_uid='$1'"; }
srev(){ evalb "$1" "const f=(o)=>{ if(!o||typeof o!=='object') return null; if(o.rev&&o.rev.value!==undefined) return o.rev.value; for (const k in o){ const r=f(o[k]); if(r!==null) return r; } return null; }; for (const i of bot.inventory.slots) { if (i && JSON.stringify(i.nbt||'').includes('$2')) return String(f(i.nbt)); } return 'none';"; }
LOSTR=0 # roll log lines lost with a kill -9 between COMMIT and the callback (the roll itself is on the item row)
rrcheck(){ # conservation over every reroll of the phase
  local ks ka; ks=$(kshard); ka=$(kall); read -r _ sh _ co <<< "$(snap $BH2 $UH2)"
  chk "$1: coins = start − 300 × committed rerolls ($ka)" "$co" "$(( CO0 - 300 * ka ))"
  chk "$1: shards = start − 40 × committed shard rerolls ($ks)" "$sh" "$(( SH0 - 40 * ks ))"
  chk "$1: exactly one roll per committed reroll (logged + $LOSTR lost to kill -9)" "$(( $(nrolls) + LOSTR ))" "$ka"
  [ "$ka" = 0 ] || [ "$(nrolls)" = 0 ] || chk "$1: no rid rolled twice" "$(rolls)" "1 "
  chk "$1: item reroll_n = committed rerolls (on cr_p1_item, data_version 2)" "$(icol $RB reroll_n):$(icol $RB data_version)" "$ka:2"
  chk "$1: no legacy counters written (p4_af_/p4_afp_/p4_rrn_/p4_rro_)" "$(ctr $UH2 p4_af_$RB):$(ctr $UH2 p4_afp_$RB):$(ctr $UH2 p4_rrn_$RB):$(ctr $UH2 p4_rro_$RB)" "0:0:0:0"
  chk "$1: no hold / pending" "$(pend $UH2)" "0"; }
echo "=== h3a) reroll (shard) normal → paid once, rolled once, affix installed in the empty slot"
chat $BH2 "$RR" 3000 | grep -E '洗练|词条' | head -2; sleep 2
[ "$(icol $RB affix)" != 0 ] && ok "h3a: affix installed on the item row ($(icol $RB affix))" || bad "h3a: no affix on the item row after a reroll"
chk "h3a: backpack stack rev = DB rev" "$(srev $BH2 $RB)" "$(gstate $RB | cut -d: -f2)"
rrcheck h3a
echo "=== h3b) reroll · before_commit → cost refunded once, nothing rolled"
AF=$(icol $RB affix); arm $BH2 before_commit; chat $BH2 "$RR" 3000 | grep -E '洗练|退' | head -2; sleep 5; disarm $BH2
chk "h3b: affix unchanged" "$(icol $RB affix)" "$AF"; rrcheck h3b
echo "=== h3c) reroll · after_commit (callback held 10 s) · abrupt disconnect → the roll is already on the item row"
K0=$(kall); arm $BH2 after_commit; chat $BH2 "$RR" 1500 >/dev/null
chk "h3c: committed with the payment (reroll_n advanced before the callback)" "$(kall):$(icol $RB reroll_n)" "$((K0 + 1)):$((K0 + 1))"
abrupt $BH2; sleep 8; join $BH2; sleep 10; disarm $BH2
chk "h3c: the roll logged once (callback ran, owner offline or back)" "$(hlogs | grep -cE "reroll $RB $(lastrid) ")" "1"
chk "h3c: stack rev = DB rev after rejoin (resync)" "$(srev $BH2 $RB)" "$(gstate $RB | cut -d: -f2)"; rrcheck h3c
echo "=== h3d) reroll + socket destroyed in the same tick → nothing or everything"
evalb $BH2 "bot.chat('$RR'); setTimeout(()=>{ try { bot._client.socket.destroy() } catch (e) {} }, 0); return 'cut';" >/dev/null; sleep 12; join $BH2; sleep 10
rrcheck h3d
echo "=== h3e) reroll · after_pay (settling txn starts 10 s late) · abrupt disconnect → commit while offline, roll at join"
arm $BH2 after_pay; K0=$(kall); chat $BH2 "$RR" 1500 >/dev/null; abrupt $BH2; sleep 12; join $BH2; sleep 10; disarm $BH2
chk "h3e: committed while offline" "$(kall)" "$((K0 + 1))"; rrcheck h3e
echo "=== h3f) reroll with a library duplicate · after_commit · abrupt disconnect"
mkdups $BH2; sleep 1; K0=$(kall); KS0=$(kshard)
arm $BH2 after_commit; chat $BH2 "/corerpg p1 reroll blade dup confirm" 2000 >/dev/null; abrupt $BH2; sleep 8; join $BH2; sleep 10; disarm $BH2
DU=$(sql "SELECT uid_a FROM cr_p1_txn WHERE owner_uuid='$UH2' AND request_id LIKE 'afx:$RB:%' AND uid_a<>'$RB' ORDER BY created_at DESC LIMIT 1")
chk "h3f: one dup reroll committed (no shards)" "$(kall):$(kshard)" "$((K0 + 1)):$KS0"
chk "h3f: the duplicate was retired once" "$(gstate $DU | cut -d: -f1)" "dismantled"; rrcheck h3f

# ---------------------------------------------------------------- kill -9 bundles
echo "=== kill -9 bundle 1: h1d undo after_pay (BH1) · h2c redeem after_commit (BH3) · h3g reroll after_commit (BH2)"
[ -n "$(lastdis $UH1)" ] || { mkdups $BH1; bulkrun $BH1 h1d >/dev/null; sleep 2; }
UD1=$(lastdis $UH1); [ "$(blanks $BH1 $UH1)" -ge 1 ] || { $C play "ni give $BH1 mat_ember_v1_blank 2" 0.6 >/dev/null; sleep 1; }
B1=$(blanks $BH1 $UH1); R1=$(gstate $UD1); D1=$(ndel $UH1 refund:undo:$UD1); M0=$(ctr $UH3 p1_mark_t1); K3=$(redeemed); K2=$(kall)
arm $BH1 after_pay; arm $BH3 after_commit; arm $BH2 after_commit
evalb $BH1 "bot.chat('/corerpg p1 undo $UD1'); return 'x';" >/dev/null; evalb $BH3 "bot.chat('$X'); return 'x';" >/dev/null; evalb $BH2 "bot.chat('$RR'); return 'x';" >/dev/null; sleep 3
chk "h1d: blanks paid before the kill" "$(blanks $BH1 $UH1)" "$((B1 - 1))"
chk "h2c: marks paid + txn committed before the kill" "$(ctr $UH3 p1_mark_t1):$(redeemed)" "$((M0 - 8)):$((K3 + 1))"; G=$(newest)
chk "h2c: callback held → no stack yet" "$(hasuid $BH3 $G)" "0"
chk "h3g: committed with the roll before the kill (reroll_n on the row)" "$(kall):$(icol $RB reroll_n)" "$((K2 + 1)):$((K2 + 1))"
killfaults
quit $BH1; quit $BH2; quit $BH3; sleep 4; join $BH1; join $BH2; join $BH3; sleep 12
chk "h1d: piece still dismantled (undo never committed)" "$(gstate $UD1)" "$R1"; chk "h1d: blanks refunded once" "$(blanks $BH1 $UH1)" "$B1"
chk "h1d: refund hold reconciled → delivered once, no hold left" "$(ndel $UH1 refund:undo:$UD1):$(nhold $UH1 refund:undo:$UD1)" "$((D1 + 1)):0"
chk "h2c: marks −8 exactly once" "$(ctr $UH3 p1_mark_t1)" "$((M0 - 8))"; chk "h2c: blade delivered once at join" "$(hasuid $BH3 $G)" "1"
chk "h2c: hold void, gear row delivered" "$(dstat $UH3 "refund:$(sql "SELECT request_id FROM cr_p1_txn WHERE owner_uuid='$UH3' AND kind='mark_redeem' ORDER BY created_at DESC LIMIT 1")"):$(sql "SELECT status FROM cr_p1_delivery WHERE kind='gear' AND item='$G'")" "void:delivered"
LOSTR=$(( LOSTR + 1 - $(hlogs | grep -cE "reroll $RB $(lastrid) ") ))
chk "h3g: stack rev = DB rev after kill -9 + join (resync, roll kept)" "$(srev $BH2 $RB)" "$(gstate $RB | cut -d: -f2)"; rrcheck h3g

echo "=== kill -9 bundle 2: h2d redeem after_pay (BH3) · h3h reroll after_pay (BH2)"
M0=$(ctr $UH3 p1_mark_t1); K3=$(redeemed); K2=$(kall); read -r _ SHB _ COB <<< "$(snap $BH2 $UH2)"
arm $BH3 after_pay; arm $BH2 after_pay
evalb $BH3 "bot.chat('$X'); return 'x';" >/dev/null; evalb $BH2 "bot.chat('$RR'); return 'x';" >/dev/null; sleep 3
chk "h2d: marks paid before the kill (no txn yet)" "$(ctr $UH3 p1_mark_t1):$(redeemed)" "$((M0 - 8)):$K3"
chk "h3h: reroll paid before the kill (no txn yet)" "$(snap $BH2 $UH2 | cut -d' ' -f2,4):$(kall)" "$((SHB - 40)) $((COB - 300)):$K2"
killfaults
quit $BH2; quit $BH3; sleep 4; join $BH2; join $BH3; sleep 14
chk "h2d: marks refunded once" "$(ctr $UH3 p1_mark_t1)" "$M0"; chk "h2d: no redemption" "$(redeemed)" "$K3"
chk "h2d: no new gear row" "$(sql "SELECT COUNT(*) FROM cr_p1_delivery WHERE owner_uuid='$UH3' AND kind='gear'")" "$K3"
chk "h3h: no reroll committed" "$(kall)" "$K2"; rrcheck h3h
rejoin $BH1; rejoin $BH2; rejoin $BH3
echo "=== h) stable after another rejoin"
rrcheck h-final; chk "h-final: marks stable" "$(ctr $UH3 p1_mark_t1)" "$M0"
for u in $UH1 $UH2 $UH3; do chk "h-final: no pending / hold rows ($u)" "$(pend $u)" "0"; done
chk "h-final: every redemption stack exactly once" "$(for g in $(sql "SELECT uid_a FROM cr_p1_txn WHERE owner_uuid='$UH3' AND kind='mark_redeem'"); do hasuid $BH3 $g; done | sort -u | tr '\n' ' ')" "1 "
chk "h-final: marks = 40 − 8 × redemptions" "$(ctr $UH3 p1_mark_t1)" "$(( 40 - 8 * $(redeemed) ))"
chk "h-final: no 'debit short' in the log" "$(hlogs | grep -c 'debit short')" "0"
quit $BH1; quit $BH2; quit $BH3
echo "=================== back to normal (no fault hooks)"
restart h-faults-off
$C play "corerpg p1 fault $BH1 after_pay" 0.8 | grep -q "测试环境" && ok "h: fault hook refused without CORERPG_TEST_FAULTS" || bad "h: fault hook still active"
tr '\0' '\n' < /proc/$(cat $RT/server.pid)/environ | grep -q '^CORERPG_TEST_FAULTS=' && bad "h: CORERPG_TEST_FAULTS in the play JVM environment" || ok "h: CORERPG_TEST_FAULTS not in the play JVM environment"
printf '  race %s\n' "${RACE[@]}"
fi

echo; printf '%s\n' "${RES[@]}" | grep FAIL || true
echo "persist-roundtrip ${ONLY:-all}: PASS=$PASS FAIL=$FAIL"
[ "$FAIL" -eq 0 ]
