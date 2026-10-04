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
# usage: tools/p1map/persist-roundtrip.sh [A_BOT] [D_BOT] [E_BOT] [F_BOT] [G1_BOT] [G2_BOT]   (fresh bots every run)
#        ONLY=g … runs phase g alone; NOG=1 skips it
# needs: botd on 127.0.0.1:8765 (mineflayer-tests/tmp-p1/botd.js), sudo mysql (read-only queries), no real players online.
set -uo pipefail
BA=${1:-FreshQ52}; BD=${2:-FreshQ53}; BE=${3:-FreshG07}; BF=${4:-FreshQ54}; BG1=${5:-FreshQ61}; BG2=${6:-FreshQ62}
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
restart(){ echo "--- graceful restart ($1) $(date +%H:%M:%S)"; echo "RESTART $(date +%H:%M) persist-roundtrip: graceful ($1)" >> /workspace/COORD-rush-retry-persist.txt
  local ino; ino=$(stat -c %i $RT/logs/latest.log); $RT/stop.sh >/dev/null; sleep 1; $RT/start.sh >/dev/null; sleep 3; waitdone "$ino" || bad "server did not finish starting"; sleep 5; logcheck "restart $1"; }
killhard(){ local pid; pid=$(cat $RT/server.pid)
  # only the play server's Paper JVM: its pid from server-runtime/server.pid, a java process whose cwd is server-runtime
  [ "$(readlink /proc/$pid/cwd)" = "$RT" ] && tr '\0' ' ' < /proc/$pid/cmdline | grep -q 'paper.*\.jar' || { bad "kill -9: pid $pid is not the play Paper JVM"; return 1; }
  echo "--- kill -9 $pid (play Paper) $(date +%H:%M:%S)"; echo "KILL9 $(date +%H:%M) persist-roundtrip: kill -9 play Paper pid $pid" >> /workspace/COORD-rush-retry-persist.txt
  local ino; ino=$(stat -c %i $RT/logs/latest.log); kill -9 "$pid"; for i in $(seq 1 20); do kill -0 "$pid" 2>/dev/null || break; sleep 0.5; done
  rm -f $RT/server.pid; sleep 2; $RT/start.sh >/dev/null; sleep 3; waitdone "$ino" || bad "server did not finish starting after kill -9"; sleep 5; logcheck "after kill -9"; }

bulkrun(){ # $1 bot, $2 label → dismantles all library T1 standard pieces of the filter, prints n
  local out tok; out=$(evalb $1 'const f=bot.chatLog.length; let tok=""; const h=m=>{const j=JSON.stringify(m.json||{}); const x=j.match(/tok:([A-Za-z0-9]+)/); if(x) tok=x[1];}; bot.on("message",h); bot.chat("/corerpg p1 gearlib bulk"); await wait(2200); bot.removeListener("message",h); return "TOK="+tok;')
  tok=$(echo "$out" | head -1 | sed 's/^TOK=//'); [ -n "$tok" ] || { echo "no-token"; return; }
  chat $1 "/corerpg p1 gearlib bulk confirm tok:$tok" 4000 | grep -oE '已分解 [0-9]+ 件' | grep -oE '[0-9]+' | head -1; }
mkdups(){ for q in 0 0 0; do $C play "corerpg p1 givedup blade $1 $q" 0.5 >/dev/null; done; sleep 1; tomain $1; chat $1 "/corerpg p1 stash" 2000 >/dev/null; }

curl -sf "$BOTD/list" >/dev/null || { echo "FAIL botd not running"; exit 1; }
[ "$(curl -sf "$BOTD/list")" = "[]" ] || echo "note: other bots online: $(curl -sf "$BOTD/list")"

if [ "${ONLY:-}" != g ]; then
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

if [ "${NOG:-}" != 1 ]; then
echo "=================== g) fault injection (D162)"
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

echo "=== g4) undo · after_deliver (applied + saved, ack skipped) · abrupt disconnect → ack only, debit once"
if [ -n "$UNDO_UID" ]; then
  S4=$(snap $BG1 $UG1); Y=$(sql "SELECT amount FROM cr_p1_delivery WHERE owner_uuid='$UG1' AND request_id LIKE 'glibdis:$UNDO_UID:%'")
  arm $BG1 after_deliver; chat $BG1 "/corerpg p1 undo $UNDO_UID" 3000 | grep -E '撤销|扣回' | head -2; sleep 2; disarm $BG1
  chk "g4: piece back in library" "$(gstate $UNDO_UID | cut -d: -f1)" "stored"
  chk "g4: blanks debited once (start − $Y)" "$(snap $BG1 $UG1 | cut -d' ' -f1)" "$(( $(echo $S4 | cut -d' ' -f1) - Y ))"
  chk "g4: ack skipped → row still pending" "$(dstat $UG1 undo:$UNDO_UID)" "pending"
  abrupt $BG1; join $BG1; sleep 6
  chk "g4: after rejoin blanks still start − $Y (not debited twice)" "$(snap $BG1 $UG1 | cut -d' ' -f1)" "$(( $(echo $S4 | cut -d' ' -f1) - Y ))"
  chk "g4: row delivered by the marker (ack only)" "$(dstat $UG1 undo:$UNDO_UID)" "delivered"
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

echo; printf '%s\n' "${RES[@]}" | grep FAIL || true
echo "persist-roundtrip $BA/$BD/$BE/$BF: PASS=$PASS FAIL=$FAIL"
[ "$FAIL" -eq 0 ]
