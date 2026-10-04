#!/usr/bin/env bash
# realgear-run.sh NAME qNN|rush BT BE CT CE LV [FAM=burst] — real-play run with progression-appropriate gear (book §3.2 /
# p1sim REF_GEAR): no weaken, no heal, no creative, survival only. Setup (admin, progression state only): first-clear
# flags of the earlier maps, ember level LV, a FAM blade T BT +BE and charm T CT +CE (selected), stamina for one run.
# The bot walks the book route and fights each room until idle with the blade, drinking a hotbar potion below 55 %
# (DRINK=1, like a new player). Prints a one-line RESULT plus the run's chat.
# env MOVE=stand (default; the bot stands and swings, never dodges) | kite (doorway pull, back-pedal / strafe, telegraph
# dodges with a reaction delay — tools/p1map/fight-kite.js; no combat assist either way).
MOVE=${MOVE:-stand}; export MOVE
N=$1; K=$2; BT=$3; BE=$4; CT=$5; CE=$6; LV=$7; FAM=${8:-burst}
M=/workspace/minecraft; C=$M/scripts/console.sh; T=$M/mineflayer-tests/tmp-p1; P=$M/tools/p1map; LOG=$M/server-runtime/logs/latest.log
ev(){ curl -sf -m 120 -X POST "http://127.0.0.1:8765/eval?name=$N" --data "$1" | jq -r '.r // .err // ""'; }
curl -sf -m 90 "http://127.0.0.1:8765/join?name=$N" >/dev/null; sleep 8
ORDER="q01 q02 q03 q04 q05 q06 q07"
for q in $ORDER; do [ "$q" = "$K" ] && break; $C play "corerpg p1 runs firstclear $N $q" 0.4 >/dev/null; done
[ "$K" = rush ] && for q in $ORDER; do $C play "corerpg p1 runs firstclear $N $q" 0.4 >/dev/null; done
[ "$K" != rush ] && [ "$K" != q01 ] && $C play "corerpg p1 runs unlock $N $K" 0.4 >/dev/null   # the quest unlock of the map itself
for i in $(seq 1 60); do L=$(ev 'const n=bot.chatLog.length; bot.chat("/corerpg level"); await wait(900); const m=bot.chatLog.slice(n).join(" ").replace(/\u00a7./g,"").match(/Lv\.(\d+)/); return m?m[1]:"0";'); [ "${L:-0}" -ge "$LV" ] && break; for j in 1 2 3; do $C play "corerpg progress $N raid_clear" 0.3 >/dev/null; done; done
echo "level Lv.$L (want $LV)"
$C play "corerpg stamina set $N 120" 0.4 >/dev/null
# blade → hotbar 0, charm → hotbar 1 then select it (hold + /corerpg p1 charm), back to the blade
$C play "corerpg p1 give $FAM blade $BT 0 0 $BE $N" 0.8 >/dev/null
[ "$CT" -ge 1 ] && $C play "corerpg p1 give $FAM charm $CT 0 0 $CE $N" 0.8 >/dev/null
sleep 1
ev "const want=(s,t)=>{ for(let i=9;i<=44;i++){const x=bot.inventory.slots[i]; if(!x) continue; const j=JSON.stringify(x.nbt||{}); if(j.includes('\"slot\"')||true){ if(j.includes('ember_v1_${FAM}_'+s+'_t'+t)) return i; } } return -1; };
 let b=want('blade',$BT); if(b>=0&&b!==36){ await bot.moveSlotItem(b,36); await wait(400); }
 let c=$CT>=1?want('charm',$CT):-1; if(c>=0&&c!==37){ await bot.moveSlotItem(c,37); await wait(400); }
 if(c>=0){ bot.setQuickBarSlot(1); await wait(300); bot.chat('/corerpg p1 charm'); await wait(1200); }
 bot.setQuickBarSlot(0); await wait(300); return 'blade@'+b+' charm@'+c;" 
ev 'const n=bot.chatLog.length; bot.chat("/corerpg p1 equip"); await wait(1200); return bot.chatLog.slice(n).map(s=>s.replace(/\u00a7./g,"")).join(" | ").slice(0,400);'
START=$(wc -l < $LOG); T0=$(date +%s)
if [ "$K" = rush ]; then
  # rush legs are boss-only arenas (no room DOOR/ROOM frames). MOVE=kite → fight-kite.sh strafe+telegraph dodge; stand keeps old fight.sh.
  ev 'const n=bot.chatLog.length; bot.chat("/corerpg p1 rush go"); await wait(9000); return bot.chatLog.slice(n).map(s=>s.replace(/\u00a7./g,"")).join(" | ").slice(0,500);'
  sleep 3
  for leg in 1 2 3 4; do
    if [ "$MOVE" = kite ]; then
      FJ=$(DRINK=1 MOVE=kite $P/fight-kite.sh $N 150000 0 34 22000 | tr -d "\n ")
      echo "RUSH-LEG $leg $(echo "$FJ" | grep -o "\"ms\":[0-9]*\|\"hits\":[0-9]*\|\"drinks\":[0-9]*\|\"kites\":[0-9]*\|\"dodges\":[0-9]*\|\"casterDodges\":[0-9]*\|\"missed\":[0-9]*\|\"dead\":[a-z]*\|\"minHp\":[0-9.]*\|\"hp\":[0-9.]*\|\"err\".\{0,200\}" | tr "\n" " ")"
      D1=$(echo "$FJ" | grep -o '"drinks":[0-9]*' | cut -d: -f2); RUSH_DRK=$(( ${RUSH_DRK:-0} + ${D1:-0} ))
      echo "$FJ" | grep -q '"dead":true' && break
    else
      DRINK=1 $T/fight.sh $N 150000 0 34 22000 | tr -d "\n " | grep -o "\"ms\":[0-9]*\|\"hits\":[0-9]*\|\"drinks\":[0-9]*\|\"minHp\":[0-9.]*" | tr "\n" " "; echo
    fi
    tail -n +$START $LOG | grep -aq "rush settle\|run .* failed\|state=FAILED\|余烬连战失败\|全员倒下" && break
  done
else
  NR=$($P/newbie-run.sh $N $K 2>&1); echo "$NR"
fi
[ -n "${RUSH_DRK:-}" ] && NR="KITE-SUMMARY died_leg=- drinks=$RUSH_DRK"
sleep 6
DT=$(( $(date +%s) - T0 ))
RL=$(tail -n +$START $LOG | grep -a "\[P1 run\]" | grep -a " $K-\| $N " | sed 's/^.*\[P1 run\] //' | cut -c1-150)
CH=$(ev 'return bot.chatLog.slice(-40).map(s=>s.replace(/\u00a7./g,"")).join("\n");')
DEATHS=$(echo "$CH" | grep -c "^\[队伍\] 你已倒下" || true)
if echo "$RL" | grep -q "rush settle\|settle\b\|settled\|boss killed after"; then R=CLEAR; elif echo "$CH$RL" | grep -q "失败"; then R=FAIL; else R=UNKNOWN; fi
KILLER=$(tail -n +$START $LOG | grep -a "$N was slain by" | head -1 | sed 's/.*was slain by //')
DLEG=$(echo "$NR" | grep -o "died_leg=[0-9-]*\|FAILED/DEAD before leg [0-9]" | head -1 | grep -o "[0-9-]*$")
case "$DLEG" in 0) ROOM=R1;; 1) ROOM=R2;; 2) ROOM=R3;; 3) ROOM=RB;; *) ROOM=-;; esac
DRK=$(echo "$NR" | grep -o "KITE-SUMMARY.*drinks=[0-9]*" | grep -o "[0-9]*$")
[ -z "$DRK" ] && DRK=$(echo "$NR" | grep -o '"drinks":[0-9]*' | cut -d: -f2 | paste -sd+ | bc 2>/dev/null)
echo "RESULT $N $K move=$MOVE gear=${FAM} T$BT+$BE/T$CT+$CE Lv$L result=$R wall=${DT}s deaths=$DEATHS room=$ROOM potions=${DRK:-?} killer=${KILLER:--}"
echo "--- [P1 run] log"; echo "$RL" | tail -25
echo "--- chat (last 40)"; echo "$CH"
curl -sf "http://127.0.0.1:8765/quit?name=$N" >/dev/null
