#!/usr/bin/env bash
# smoke-map.sh qNN : P1 must be runtime-on; P1Fox online & unlocked. Creative + admin kill, walks the book route (no jumping).
K=$1; C=/workspace/minecraft/scripts/console.sh; T=/workspace/minecraft/mineflayer-tests/tmp-p1
ROUTE=$(python3 - "$K" <<'PY'
import sys, json
sys.path.insert(0, '/workspace/ember-p1/tools/p1map')
from book import parse
m = parse()[sys.argv[1]]
R = {r['id']: r for r in m['rooms']}; C = {c['id']: c for c in m['corridors']}
legs = []
def ctr(r): return [(R[r]['x0'] + R[r]['x1']) // 2, (R[r]['z0'] + R[r]['z1']) // 2]
for cid, room in (('C01', 'R1'), ('C12', 'R2'), ('C23', 'R3'), ('C3B', 'RB')):
    pts = [[n[0], n[2]] for n in C[cid]['nodes']] + [ctr(room)]
    legs.append(json.dumps(pts))
print('|'.join(legs))
PY
)
$C play "corerpg p1 heal P1Fox" >/dev/null
$T/b.sh chat P1Fox "/corerpg enter $K" 9000 | tr -d '\n'; echo
$C play "gamemode creative P1Fox" >/dev/null; sleep 1
IFS='|' read -ra L <<< "$ROUTE"
for i in 0 1 2 3; do
  $T/walk.sh P1Fox "${L[$i]}" | tr -d '\n '; echo
  sleep 2
  [ $i -lt 3 ] && { $C play "execute P1Fox ~ ~ ~ kill @e[type=!player,r=50]" >/dev/null; sleep 2; }
done
sleep 3
$T/b.sh eval P1Fox "return bot.chatLog.slice(-9).map(s=>s.replace(/§./g,''))" | tr -d '\n'; echo
$C play "execute P1Fox ~ ~ ~ kill @e[type=!player,r=50]" >/dev/null; sleep 6
$C play "gamemode survival P1Fox" >/dev/null
