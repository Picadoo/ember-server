#!/usr/bin/env bash
# starter-equip-check.sh FRESHBOT : recheck #1/#4 (D130/D131) live check on a brand-new account.
#  1. 8 T1 marks → T1 blade: it must take the T0 starter's hotbar slot and the starter must go to the BACKPACK (9..35).
#  2. put the starter back on the hotbar (the old trap state), hold the T1 blade, 8 T2 marks → T2 blade:
#     it must end up in the main hand (D120 auto-equip still runs after the starter swap) — log "[P1 equip] auto".
# Prints PASS/FAIL lines. Needs botd (tmp-p1/b.sh) and the console helper.
B=$1; C=/workspace/minecraft/scripts/console.sh; T=/workspace/minecraft/mineflayer-tests/tmp-p1
LOG=/workspace/minecraft/server-runtime/logs/stdout.log
INV="const out={}; for(let i=9;i<45;i++){const s=bot.inventory.slots[i]; if(s&&s.customName&&/刃/.test(s.customName)) out[i]=s.customName.replace(/§./g,'');} out.held=bot.heldItem?bot.heldItem.customName.replace(/§./g,''):null; out.q=bot.quickBarSlot; return out"
$T/b.sh join $B >/dev/null; sleep 12
for q in q01 q02 q03 q04; do $C play "corerpg p1 runs firstclear $B $q" 0.3 >/dev/null; done
$C play "corerpg p1 runs marks $B 1 8" 0.5 >/dev/null
$T/b.sh chat $B "/corerpg p1 marks exchange scorch blade 1 confirm" 3000 | tr -d '\n' | grep -o '结算到账[^"]*\|T1 焚烬刃[^"]*' | head -2
R1=$($T/b.sh eval $B "$INV" | tr -d '\n ')
echo "after T1: $R1"
# window slots 36..44 = hotbar 0..8, 9..35 = backpack
ST=$(echo "$R1" | grep -oE '"[0-9]+":"[^"]*(T0|起始)[^"]*"' | head -1 | grep -o '^"[0-9]*' | tr -d '"')
if [ -n "$ST" ] && [ "$ST" -lt 36 ]; then echo "PASS starter went to the backpack (window slot $ST)"; else echo "FAIL starter not in the backpack ($ST)"; fi
# trap state: starter back to hotbar slot 4 (window 40), hold slot of the T1 blade
$T/b.sh eval $B "await bot.moveSlotItem($ST, 40); const i=[...Array(9).keys()].find(k=>{const s=bot.inventory.slots[36+k]; return s&&s.customName&&/T1/.test(s.customName)}); bot.setQuickBarSlot(i); await wait(500); return i" >/dev/null
echo "trap: $($T/b.sh eval $B "$INV" | tr -d '\n ')"
N0=$(grep -c "\[P1 equip\] auto $B" $LOG)
$C play "corerpg p1 runs marks $B 2 8" 0.5 >/dev/null
$T/b.sh chat $B "/corerpg p1 marks exchange scorch blade 2 confirm" 3000 | tr -d '\n' | grep -o 'T2 焚烬刃[^"]*' | head -1
sleep 1
R2=$($T/b.sh eval $B "$INV" | tr -d '\n ')
echo "after T2: $R2"
echo "$R2" | grep -q '"held":"[^"]*T2' && echo "PASS T2 blade is in the main hand" || echo "FAIL T2 blade not in the main hand"
[ "$(grep -c "\[P1 equip\] auto $B" $LOG)" -gt "$N0" ] && echo "PASS log [P1 equip] auto" || echo "FAIL no auto log"
