#!/usr/bin/env bash
# newbie-run.sh NAME qNN [mode] : a real (no weaken, survival) run as a new player would do it; bot walks the book route,
# fights each room until idle with the hotbar-1 blade; prints chat at the end. mode "" = normal, "challenge" = challenge.
# env MOVE=stand (default: walk to each room centre, stand and swing — tmp-p1/fight.sh) | kite (stop just inside the room
# entry, pull the melee to the doorway, back-pedal / strafe / dodge telegraphs — tools/p1map/fight-kite.sh; see fight-kite.js).
# kite + ENTRY=center: walk to the room centre like the stand bot (no doorway pull); REACT / MISS / HOLDMS / REACH pass through.
N=$1; K=$2; MODE=$3; C=/workspace/minecraft/scripts/console.sh; T=/workspace/minecraft/mineflayer-tests/tmp-p1
MOVE=${MOVE:-stand}; P=$(cd "$(dirname "$0")" && pwd)
ROUTE=$(python3 - "$K" <<'PY'
import sys, json
sys.path.insert(0, '/workspace/ember-p1/tools/p1map')
from book import parse
m = parse()[sys.argv[1]]
R = {r['id']: r for r in m['rooms']}; C = {c['id']: c for c in m['corridors']}
legs = []
def ctr(r): return [(R[r]['x0'] + R[r]['x1']) // 2, (R[r]['z0'] + R[r]['z1']) // 2]
import os
kite = os.environ.get('MOVE') == 'kite'
if kite:  # room boxes = the live CoreRpg room triggers / boss area (read only)
    import yaml
    cfg = yaml.safe_load(open('/workspace/minecraft/plugins/CoreRpg/ember-v1-runs.yml'))['maps'][sys.argv[1]]
    box = {'R1': cfg['rooms']['r1']['trigger'], 'R2': cfg['rooms']['r2']['trigger'], 'R3': cfg['rooms']['r3']['trigger'], 'RB': cfg['boss']['area']}
prev = None
for cid, room in (('C01', 'R1'), ('C12', 'R2'), ('C23', 'R3'), ('C3B', 'RB')):
    nodes = [[n[0], n[2]] for n in C[cid]['nodes']]
    if not kite:
        legs.append(json.dumps(nodes + [ctr(room)]))
        continue
    # kite: cross the last room via its centre, stop 2 blocks inside the new room's entry (= triggers it), door = entry node
    (ax, az), (bx, bz) = nodes[-2], nodes[-1]
    l = max(1e-6, ((bx - ax) ** 2 + (bz - az) ** 2) ** 0.5)
    inside = [round(bx + (bx - ax) / l * 2), round(bz + (bz - az) / l * 2)]
    b = box[room]
    if os.environ.get('ENTRY') == 'center':  # sloppy variant: walk into the room centre like the stand bot, no doorway
        legs.append(json.dumps(([ctr(prev)] if prev else []) + nodes + [ctr(room)]) + ';null;' + json.dumps([b[0], b[2], b[3], b[5]]))
    else:
        legs.append(json.dumps(([ctr(prev)] if prev else []) + nodes + [inside]) + ';' + json.dumps([bx, bz]) + ';' + json.dumps([b[0], b[2], b[3], b[5]]))
    prev = room
print('|'.join(legs))
PY
)
ENT=$($T/b.sh chat $N "/corerpg enter $K $MODE" 9000 | tr -d '\n' | sed 's/§.//g'); echo "$ENT" | tail -c 300; echo
# 2026-10-04: a refused entry (locked map, missing charm prompt …) used to walk / fight in the hub for 6 min
echo "$ENT" | grep -q "本开始\|开始 ·" || { echo "ENTER FAILED: $(echo "$ENT" | tail -c 200)"; exit 3; }
IFS='|' read -ra L <<< "$ROUTE"
DIED=-; DRK=0
for i in 0 1 2 3; do
  if [ "$MOVE" = kite ]; then IFS=';' read -r WALK DOOR ROOM <<< "${L[$i]}"; else WALK=${L[$i]}; fi
  $T/walk.sh $N "$WALK" | tr -d '\n ' | tail -c 80; echo
  $T/b.sh eval $N "return bot.chatLog.slice(-8).join('')" | grep -q "本局失败\|阵亡\|你已倒下" && { echo "FAILED/DEAD before leg $i"; DIED=$i; break; }
  if [ "$MOVE" = kite ]; then
    sleep 0.5
    FJ=$(DRINK=1 MOVE=kite DOOR="$DOOR" ROOM="$ROOM" $P/fight-kite.sh $N 120000 0 24 6000 | tr -d "\n ")
    echo "LEG $i $(echo "$FJ" | grep -o "\"ms\":[0-9]*\|\"hits\":[0-9]*\|\"drinks\":[0-9]*\|\"kites\":[0-9]*\|\"dodges\":[0-9]*\|\"casterDodges\":[0-9]*\|\"missed\":[0-9]*\|\"dead\":[a-z]*\|\"room\":\"[^\"]*\"\|\"minHp\":[0-9.]*\|\"hp\":[0-9.]*\|\"err\".\{0,200\}" | tr "\n" " ")"
    D1=$(echo "$FJ" | grep -o '"drinks":[0-9]*' | cut -d: -f2); DRK=$((DRK + ${D1:-0}))
    echo "$FJ" | grep -q '"dead":true' && { DIED=$i; break; }
  else
    sleep 2
    DRINK=1 $T/fight.sh $N 90000 0 24 6000 | tr -d "\n " | grep -o "\"ms\":[0-9]*\|\"hits\":[0-9]*\|\"drinks\":[0-9]*\|\"minHp\":[0-9.]*\|\"hp\":[0-9.]*" | tr "\n" " "; echo
  fi
done
[ "$MOVE" = kite ] && echo "KITE-SUMMARY died_leg=$DIED drinks=$DRK"
sleep 4
$T/b.sh eval $N "return bot.chatLog.slice(-14).map(s=>s.replace(/§./g,''))" | tr -d '\n'; echo
