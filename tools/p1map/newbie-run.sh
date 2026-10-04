#!/usr/bin/env bash
# newbie-run.sh NAME qNN [mode] : a real (no weaken, survival) run as a new player would do it; bot walks the book route,
# fights each room until idle with the hotbar-1 blade; prints chat at the end. mode "" = normal, "challenge" = challenge.
N=$1; K=$2; MODE=$3; C=/workspace/minecraft/scripts/console.sh; T=/workspace/minecraft/mineflayer-tests/tmp-p1
ROUTE=$(python3 - "$K" <<'PY'
import sys, json
sys.path.insert(0, '/workspace/ember-p1/tools/p1map')
from book import parse
m = parse()[sys.argv[1]]
R = {r['id']: r for r in m['rooms']}; C = {c['id']: c for c in m['corridors']}
legs = []
def ctr(r): return [(R[r]['x0'] + R[r]['x1']) // 2, (R[r]['z0'] + R[r]['z1']) // 2]
for cid, room in (('C01', 'R1'), ('C12', 'R2'), ('C23', 'R3'), ('C3B', 'RB')):
    legs.append(json.dumps([[n[0], n[2]] for n in C[cid]['nodes']] + [ctr(room)]))
print('|'.join(legs))
PY
)
ENT=$($T/b.sh chat $N "/corerpg enter $K $MODE" 9000 | tr -d '\n' | sed 's/§.//g'); echo "$ENT" | tail -c 300; echo
# 2026-10-04: a refused entry (locked map, missing charm prompt …) used to walk / fight in the hub for 6 min
echo "$ENT" | grep -q "本开始\|开始 ·" || { echo "ENTER FAILED: $(echo "$ENT" | tail -c 200)"; exit 3; }
IFS='|' read -ra L <<< "$ROUTE"
for i in 0 1 2 3; do
  $T/walk.sh $N "${L[$i]}" | tr -d '\n ' | tail -c 80; echo
  $T/b.sh eval $N "return bot.chatLog.slice(-8).join('')" | grep -q "本局失败\|阵亡" && { echo "FAILED/DEAD before leg $i"; break; }
  sleep 2
  DRINK=1 $T/fight.sh $N 90000 0 24 6000 | tr -d "\n " | grep -o "\"ms\":[0-9]*\|\"hits\":[0-9]*\|\"drinks\":[0-9]*\|\"minHp\":[0-9.]*\|\"hp\":[0-9.]*" | tr "\n" " "; echo
done
sleep 4
$T/b.sh eval $N "return bot.chatLog.slice(-14).map(s=>s.replace(/§./g,''))" | tr -d '\n'; echo
