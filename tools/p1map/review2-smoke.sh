#!/usr/bin/env bash
# review2-smoke.sh [BOT] — CoreRpg 1.64.1 invsnap ledger net-out (deposit / redeem after snapshot, real loss, gear library),
# bulk dismantle confirmation (quality counts, invested kept back) + batch undo. Fresh bot each run.
set -uo pipefail
B=${1:-FreshQ50}
C=/workspace/minecraft/scripts/console.sh
BOTD=http://127.0.0.1:8765
PASS=0; FAIL=0
ok(){ echo "PASS $1"; PASS=$((PASS+1)); }
bad(){ echo "FAIL $1"; FAIL=$((FAIL+1)); }
evalb(){ curl -sf -m 60 -X POST "$BOTD/eval?name=$B" --data "$1" | jq -r '.r // .err // ""'; }
inv(){ evalb 'return bot.inventory.items().map(i=>((i.displayName||i.name||"")+"x"+i.count).replace(/\u00a7./g,"")).join("|");'; }
count(){ evalb "let n=0; for (const i of bot.inventory.items()) { const d=(i.displayName||'')+' '+JSON.stringify(i.nbt||''); if (d.includes('$1')) n+=i.count; } return String(n);"; }
chat(){ evalb "const f=bot.chatLog.length; bot.chat('$1'); await wait(${2:-1500}); return bot.chatLog.slice(f).map(s=>s.replace(/\u00a7./g,'')).join('\n');"; }
snapid(){ tr -d '\r' | grep -oE '快照 #[0-9]+' | head -1 | grep -oE '[0-9]+'; }

curl -sf "$BOTD/list" >/dev/null || { echo "FAIL botd"; exit 1; }
curl -sf -m 90 "$BOTD/join?name=$B" >/dev/null
sleep 8
curl -sf "$BOTD/list" | grep -q "$B" && ok "bot $B online" || { bad "offline"; exit 1; }

echo "=== A1 deposit + redeem after snapshot → no dupe (30 shards + 2 tickets) ==="
$C play "ni give $B mat_ember_shard 30" 0.6 >/dev/null
$C play "ni give $B ember_gacha_ticket 2" 0.6 >/dev/null
sleep 1
S1=$(count 余烬碎片); T1=$(count 扭蛋券); echo "backpack before: shards=$S1 tickets=$T1"
SID=$($C play "corerpg invsnap take $B" 1.2 | snapid); [ -n "$SID" ] && ok "take #$SID" || bad "no snap id"
chat "/corerpg p1 stash" 1500 | grep -E '存入' | head -2
chat "/gacha redeem" 1800 | grep -E '存入' | head -2
sleep 3   # vault log flush (2 s buckets)
PV=$($C play "corerpg invsnap preview $B $SID" 2.5); echo "$PV" | grep -E '预览|快照 [0-9]+ →|跳过|装备' | sed 's/^.*INFO\]: //'
echo "$PV" | grep -qE '余烬碎片.*快照 30 → 恢复 0' && ok "preview: shards 30 → 0 (stash netted)" || bad "preview shards line"
echo "$PV" | grep -qE '扭蛋券.*快照 2 → 恢复 0.*redeem' && ok "preview: tickets 2 → 0 (redeem ledger)" || bad "preview tickets line"
RS=$($C play "corerpg invsnap restore $B $SID" 3.0); PRE=$(echo "$RS" | tr -d '\r' | grep -oE '已存为 #[0-9]+' | grep -oE '[0-9]+' | head -1)
sleep 1
S2=$(count 余烬碎片); T2=$(count 扭蛋券); echo "backpack after restore: shards=$S2 tickets=$T2"
[ "$S2" = "0" ] && [ "$T2" = "0" ] && ok "no duplicate in backpack after restore" || bad "dupe: shards=$S2 tickets=$T2"
$C play "gacha admin inspect $B" 1.2 | grep -E '券|tickets' | head -2 | sed 's/^.*INFO\]: //'

echo "=== A2 real loss (nothing deposited) → restored ==="
$C play "ni give $B mat_ember_core_fragment 12" 0.6 >/dev/null
sleep 1
SID2=$($C play "corerpg invsnap take $B" 1.2 | snapid); [ -n "$SID2" ] && ok "take #$SID2" || bad "no snap id 2"
$C play "clear $B" 1.0 >/dev/null
sleep 1
L0=$(count 核心碎片); echo "after clear: core=$L0"
RS2=$($C play "corerpg invsnap restore $B $SID2" 3.0); echo "$RS2" | grep -E '核心碎片' | sed 's/^.*INFO\]: //' | head -2
sleep 1
L1=$(count 核心碎片); [ "$L1" = "12" ] && ok "lost materials restored (12)" || bad "core after restore=$L1"

# hotbar → main inventory (depositAll only takes slots 9-35, i.e. not the hotbar); keeps the held slot 36
tomain(){ evalb 'let n=0; for(let i=37;i<=44;i++){const x=bot.inventory.slots[i]; if(!x||!/sword|_axe|_hoe/.test(x.name)) continue; let t=-1; for(let j=9;j<=35;j++) if(!bot.inventory.slots[j]){t=j;break;} if(t<0) break; await bot.moveSlotItem(i,t); n++; await wait(150);} return String(n);' >/dev/null; }
echo "=== A3 gear moved into gear library after snapshot → skipped ==="
$C play "corerpg p1 give scorch blade 1 0 0 0 $B" 1.0 >/dev/null
sleep 1; tomain
SID3=$($C play "corerpg invsnap take $B" 1.2 | snapid)
chat "/corerpg p1 stash" 1800 >/dev/null
G0=$(count 焚烬刃)
RS3=$($C play "corerpg invsnap restore $B $SID3" 3.0); echo "$RS3" | grep -E '已在装备库|跳过' | sed 's/^.*INFO\]: //' | head -3
sleep 1
G1=$(count 焚烬刃); echo "blades before=$G0 after=$G1"
echo "$RS3" | grep -q '已在装备库' && [ "$G1" = "$G0" ] && ok "gear-library piece skipped, no copy" || bad "gear library dupe? before=$G0 after=$G1"

echo "=== B bulk confirmation + batch undo ==="
# givedup copies the *equipped* T1+ blade → hold a fresh T1 scorch blade in hotbar slot 0 first
$C play "corerpg p1 give scorch blade 1 0 0 0 $B" 1.0 >/dev/null
evalb 'bot.setQuickBarSlot(0); await wait(200); let k=-1; for(let i=37;i<=44;i++){const x=bot.inventory.slots[i]; if(x&&/sword/.test(x.name)&&JSON.stringify(x.nbt||{}).includes("焚烬刃")) k=i;} if(k<0) for(let j=9;j<=35;j++){const x=bot.inventory.slots[j]; if(x&&JSON.stringify(x.nbt||{}).includes("焚烬刃")) k=j;} if(k>=0) await bot.moveSlotItem(k,36); await wait(800); return String(k);' >/dev/null
for q in 0 0 0 1 3; do $C play "corerpg p1 givedup blade $B $q" 0.5 >/dev/null; tomain; done
chat "/corerpg p1 stash" 1800 >/dev/null
BULK=$(evalb 'const f=bot.chatLog.length; let tok=""; const h=m=>{const j=JSON.stringify(m.json||{}); const x=j.match(/tok:([A-Za-z0-9]+)/); if(x) tok=x[1];}; bot.on("message",h); bot.chat("/corerpg p1 gearlib bulk"); await wait(2200); bot.removeListener("message",h); return "TOK="+tok+"\n"+bot.chatLog.slice(f).map(s=>s.replace(/\u00a7./g,"")).join("\n");')
echo "$BULK" | head -16
echo "$BULK" | grep -qE '按成色：' && ok "confirm lists counts by quality" || bad "no quality counts"
echo "$BULK" | grep -qE '不会分解 [0-9]+ 件' && echo "$BULK" | grep -qE '✖ .*成色极品' && ok "极品 kept back and marked" || bad "极品 not marked"
TAKE=$(echo "$BULK" | sed -n '/会分解：/,$p' | grep -E '· T' || true)
echo "$TAKE" | grep -qE '成色极品|成色卓越' && bad "invested in take" || ok "take list has no 极品/卓越"
TOK=$(echo "$BULK" | head -1 | sed 's/^TOK=//')
N=$(echo "$BULK" | grep -oE '：[0-9]+ 件 → 胚料' | head -1 | grep -oE '[0-9]+')
if [ -n "$TOK" ]; then
  DONE=$(chat "/corerpg p1 gearlib bulk confirm tok:$TOK" 4000); echo "$DONE" | grep -E '已分解|撤销' | head -3
  echo "$DONE" | grep -qE '已分解 [0-9]+ 件' && ok "bulk dismantled $N" || bad "bulk confirm failed"
  U=$(chat "/corerpg p1 undo" 2000); echo "$U" | grep -E '共|这一批' | head -3
  echo "$U" | grep -qE '批量分解的一批' && ok "undo list offers batch undo" || bad "no batch button"
  UB=$(chat "/corerpg p1 undo batch" 5000); echo "$UB" | grep -E '整批|撤销' | head -2
  echo "$UB" | grep -qE '已整批撤销 [0-9]+ 件' && ok "batch undo" || bad "batch undo failed"
else bad "no confirm token (botd chatLog has no click events?)"; fi

[ -n "${PRE:-}" ] && echo "(A1 undo would be: corerpg invsnap restore $B $PRE)"
curl -sf "$BOTD/quit?name=$B" >/dev/null || true
echo "review2-smoke $B: PASS=$PASS FAIL=$FAIL"
[ "$FAIL" -eq 0 ]
