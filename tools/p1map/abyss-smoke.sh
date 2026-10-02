#!/usr/bin/env bash
# abyss-smoke.sh NAME TIER : P2-2 segment; reads the seeded map from chat; per room admin weaken → bot kills (real settlement)
N=$1; TI=$2; C=/workspace/minecraft/scripts/console.sh; T=/workspace/minecraft/mineflayer-tests/tmp-p1
$C play "corerpg p1 heal $N" >/dev/null
OUT=$($T/b.sh chat $N "/corerpg p1 abyss $TI" 9000 | tr -d '\n')
echo "$OUT" | tail -c 600; echo
K=$(echo "$OUT" | grep -o "→ Q0[1-7]" | head -1 | tr -d '→ Q' ); K="q0${K#0}"
[ "$K" = "q0" ] && { echo "no map"; exit 1; }
echo "MAP=$K"
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
$C play "gamemode creative $N" >/dev/null; sleep 1
IFS='|' read -ra L <<< "$ROUTE"
for i in 0 1 2 3; do
  $T/walk.sh $N "${L[$i]}" | tr -d '\n ' | tail -c 80; echo
  sleep 3
  for k in 1 2 3; do
    $C play "corerpg p1 runs weaken $N" 0.5 | tail -1 | grep -o "削弱.*\|没有.*"
    $T/fight.sh $N 25000 0 24 4000 | tr -d '\n ' | grep -o '"hits":[0-9]*'
  done
  sleep 2
done
sleep 4
$T/b.sh eval $N "return bot.chatLog.slice(-10).map(s=>s.replace(/§./g,''))" | tr -d '\n'; echo
$C play "gamemode survival $N" >/dev/null
