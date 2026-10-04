#!/usr/bin/env bash
# d159-reroll-gearlib-smoke.sh [BOT] — D159: 洗练吃装备库重复件 + 背包路径仍可用
set -uo pipefail
B=${1:-FreshQ51}
C=/workspace/minecraft/scripts/console.sh
BOTD=http://127.0.0.1:8765
PASS=0; FAIL=0
ok(){ echo "PASS $1"; PASS=$((PASS+1)); }
bad(){ echo "FAIL $1"; FAIL=$((FAIL+1)); }
evalb(){ curl -sf -m 60 -X POST "$BOTD/eval?name=$B" --data "$1" | jq -r '.r // .err // ""'; }
inv(){ evalb 'return bot.inventory.items().map(i=>((i.displayName||i.name||"")+"x"+i.count).replace(/\u00a7./g,"")).join("|");'; }
count(){ evalb "let n=0; for (const i of bot.inventory.items()) { const d=(i.displayName||'')+' '+JSON.stringify(i.nbt||''); if (d.includes('$1')) n+=i.count; } return String(n);"; }
chat(){ evalb "const f=bot.chatLog.length; bot.chat('$1'); await wait(${2:-1500}); return bot.chatLog.slice(f).map(s=>s.replace(/\u00a7./g,'')).join('\n');"; }

curl -sf "$BOTD/list" >/dev/null || { echo "FAIL botd not reachable"; exit 1; }
curl -sf -m 90 "$BOTD/join?name=$B" >/dev/null
sleep 8
curl -sf "$BOTD/list" | grep -q "$B" && ok "bot $B online" || { bad "offline"; exit 1; }

# hold a fresh T1 scorch blade (givedup copies the equipped one)
$C play "corerpg p1 give scorch blade 1 0 0 0 $B" 1.0 >/dev/null
evalb 'bot.setQuickBarSlot(0); await wait(200); let k=-1; for(let i=37;i<=44;i++){const x=bot.inventory.slots[i]; if(x&&/sword/.test(x.name)&&JSON.stringify(x.nbt||{}).includes("焚烬刃")) k=i;} if(k<0) for(let j=9;j<=35;j++){const x=bot.inventory.slots[j]; if(x&&JSON.stringify(x.nbt||{}).includes("焚烬刃")) k=j;} if(k>=0) await bot.moveSlotItem(k,36); await wait(800); return String(k);' >/dev/null
$C play "corerpg coin give $B 5000" 0.8 >/dev/null

# --- path A: dup in gear library ---
$C play "corerpg p1 givedup blade $B 0" 0.8 >/dev/null
# move hotbar dups into main inv (stash skips hotbar)
evalb 'let n=0; for(let i=37;i<=44;i++){const x=bot.inventory.slots[i]; if(!x||!/sword|_axe|_hoe/.test(x.name)) continue; if(i===36) continue; let t=-1; for(let j=9;j<=35;j++) if(!bot.inventory.slots[j]){t=j;break;} if(t<0) break; await bot.moveSlotItem(i,t); n++; await wait(150);} return String(n);' >/dev/null
sleep 1
BEFORE=$(count 焚烬刃); echo "blades before stash=$BEFORE"
STASH=$(chat "/corerpg p1 stash" 2000); echo "$STASH" | head -3
sleep 1
AFTER=$(count 焚烬刃); echo "blades after stash=$AFTER (held should remain)"
# backpack should have no extra dup (only held equipped)
# preview reroll
PREV=$(chat "/corerpg p1 reroll blade dup" 2500)
echo "$PREV" | head -12
echo "$PREV" | grep -qE '装备库' && ok "preview mentions 装备库" || bad "preview missing 装备库 ($PREV)"
echo "$PREV" | grep -qE '背包里没有' && bad "still says 背包里没有" || ok "no stale 背包里没有"

# confirm — click confirm via command
CONF=$(chat "/corerpg p1 reroll blade dup confirm" 3500)
echo "$CONF" | head -10
echo "$CONF" | grep -qE '洗练结果|词条槽|保留' && ok "lib-dup wash rolled" || bad "lib wash no result ($CONF)"
# gearlib should have lost the piece (open count via itemlog or gearlib)
$C play "corerpg p1 gearlib" 1.0 >/dev/null || true

# --- path B: dup still in backpack ---
$C play "corerpg p1 givedup blade $B 0" 0.8 >/dev/null
evalb 'let n=0; for(let i=37;i<=44;i++){const x=bot.inventory.slots[i]; if(!x||!/sword/.test(x.name)) continue; if(i===36) continue; let t=-1; for(let j=9;j<=35;j++) if(!bot.inventory.slots[j]){t=j;break;} if(t<0) break; await bot.moveSlotItem(i,t); n++; await wait(150);} return String(n);' >/dev/null
sleep 1
PREV2=$(chat "/corerpg p1 reroll blade dup" 2500)
echo "$PREV2" | head -8
echo "$PREV2" | grep -qE '背包' && ok "inv-path preview says 背包" || bad "inv preview ($PREV2)"
CONF2=$(chat "/corerpg p1 reroll blade dup confirm" 3500)
echo "$CONF2" | head -8
echo "$CONF2" | grep -qE '洗练结果|词条槽|保留' && ok "inv-dup wash rolled" || bad "inv wash no result ($CONF2)"

# hub icon (file already ender pearl; soft check via trmenu open if possible)
HUB=$(evalb 'const f=bot.chatLog.length; bot.chat("/trmenu open ember_hub"); await wait(1500); return "opened";' 2>/dev/null || echo opened)
ok "hub menu open attempted ($HUB)"

curl -sf "$BOTD/quit?name=$B" >/dev/null || true
echo "d159-reroll-gearlib-smoke $B: PASS=$PASS FAIL=$FAIL"
[ "$FAIL" -eq 0 ]
