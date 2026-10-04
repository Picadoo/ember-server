#!/usr/bin/env bash
# d157-smoke.sh [BOT] — InvSnap currency strip + bulk dismantle skip invested + hub L after-end click.
set -euo pipefail
B=${1:-FreshQ49}
C=/workspace/minecraft/scripts/console.sh
BOTD=http://127.0.0.1:8765
PASS=0; FAIL=0
ok(){ echo "PASS $1"; PASS=$((PASS+1)); }
bad(){ echo "FAIL $1"; FAIL=$((FAIL+1)); }
evalb(){ curl -sf -X POST "$BOTD/eval?name=$B" --data "$1"; }

curl -sf "$BOTD/list" >/dev/null || { echo "FAIL botd"; exit 1; }
curl -sf "$BOTD/quit?name=$B" >/dev/null 2>&1 || true
curl -sf -m 90 "$BOTD/join?name=$B" >/dev/null
sleep 8
curl -sf "$BOTD/list" | grep -q "$B" && ok "bot online" || { bad "offline"; exit 1; }

echo "=== A invsnap strip ==="
$C play "ni give $B mat_ember_shard 30" 0.5 >/dev/null
$C play "ni give $B ember_gacha_ticket 2" 0.5 >/dev/null
SNAP=$($C play "corerpg invsnap take $B" 1.0)
SID=$(echo "$SNAP" | tr -d '\r' | grep -oE '#[0-9]+' | head -1 | tr -d '#')
[ -n "$SID" ] && ok "take #$SID" || bad "no snap id"
evalb 'bot.chat("/corerpg p1 stash"); await wait(1200); bot.chat("/gacha redeem"); await wait(1200); return "ok";' >/dev/null
REST=$($C play "corerpg invsnap restore $B $SID" 2.0)
echo "$REST" | grep -qE '跳过|材料仓' && ok "restore skip msg" || bad "no skip msg"
INV=$(evalb 'return bot.inventory.items().map(i=>((i.displayName||i.name||"")+ "x"+i.count).replace(/\u00a7./g,"")).join("|");')
echo "$INV" | grep -qiE '碎片|扭蛋券' && bad "dupe in backpack" || ok "no currency dupe in backpack"
PRE=$(echo "$REST" | tr -d '\r' | grep -oE '已存为 #[0-9]+' | grep -oE '[0-9]+' | head -1 || true)
[ -n "$PRE" ] && $C play "corerpg invsnap restore $B $PRE" 1.0 >/dev/null && ok "undo #$PRE" || ok "no undo needed"

echo "=== B bulk invested skip ==="
$C play "corerpg p1 give scorch blade 1 0 0 0 $B" 0.8 >/dev/null
evalb 'const it=bot.inventory.items().find(x=>(x.displayName||"").includes("刃")||(x.name||"").includes("sword")); if(it){await bot.equip(it,"hand"); await wait(600);} return "eq";' >/dev/null
$C play "corerpg p1 givedup blade $B 0" 0.5 >/dev/null
$C play "corerpg p1 givedup blade $B 0" 0.5 >/dev/null
$C play "corerpg p1 givedup blade $B 3" 0.5 >/dev/null
$C play "corerpg p1 givedup blade $B 2" 0.5 >/dev/null
evalb 'bot.chat("/corerpg p1 stash"); await wait(1500); return "s";' >/dev/null
BULK=$(evalb 'const f=bot.chatLog.length; bot.chat("/corerpg p1 gearlib bulk"); await wait(2000); return bot.chatLog.slice(f).map(s=>s.replace(/\u00a7./g,"")).join("\n");')
echo "$BULK" | grep -qE '有投入' && ok "bulk mentions invested skip" || bad "no invested text"
echo "$BULK" | grep -qE '成色标准' && ok "plain listed" || bad "plain missing"
TAKE=$(echo "$BULK" | grep -E '· T' || true)
echo "$TAKE" | grep -qE '成色极品|成色卓越|\+[1-9]|精工[1-9]' && bad "invested in take list" || ok "invested not in take list"

echo "=== C hub L after-end ==="
cp -a plugins/CoreRpg/ember-v1-festival.yml /workspace/backup/ember-v1-festival.yml.pre-d157-smoke
python3 -c 'import re,pathlib;p=pathlib.Path("plugins/CoreRpg/ember-v1-festival.yml");p.write_text(re.sub(r"end:\s*\".*?\"","end: \"2026-10-04T00:00:00+08:00\"",p.read_text(),1))'
$C play "corerpg p1 fest reload" 0.8 >/dev/null
$C play "trmenu reload" 1.0 >/dev/null
sleep 1
$C play "trmenu open ember_hub $B" 0.8 >/dev/null
CLICK=$(evalb 'await wait(400); const t1=bot.currentWindow?(bot.currentWindow.title||""):"none"; try{await bot.clickWindow(0,0,0)}catch(e){}; await wait(2000); const t2=bot.currentWindow?(bot.currentWindow.title||""):"none"; return JSON.stringify({t1:t1.replace(/\u00a7./g,""),t2:t2.replace(/\u00a7./g,"")});')
echo "$CLICK" | grep -qE '烟火庙会|国庆' && ok "hub L opened fest after-end" || bad "hub L click failed $CLICK"
python3 -c 'import re,pathlib;p=pathlib.Path("plugins/CoreRpg/ember-v1-festival.yml");p.write_text(re.sub(r"end:\s*\".*?\"","end: \"2026-10-08T00:00:00+08:00\"",p.read_text(),1))'
$C play "corerpg p1 fest reload" 0.8 >/dev/null
$C play "trmenu reload" 0.8 >/dev/null
ok "fest end restored"

rg -q '繁花烟火' plugins/CoreGacha/gacha.yml && ok "gacha rename live" || bad "rename missing"
curl -sf "$BOTD/quit?name=$B" >/dev/null || true
echo "d157-smoke $B: PASS=$PASS FAIL=$FAIL"
[ "$FAIL" -eq 0 ]
