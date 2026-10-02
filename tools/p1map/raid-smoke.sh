#!/usr/bin/env bash
# raid-smoke.sh LEADER "M1 M2 .." : P2-5 r01 (Q07 map); leader walks the book route, members tp along; per room weaken -> all bots fight
N=$1; MEM=$2; C=/workspace/minecraft/scripts/console.sh; T=/workspace/minecraft/mineflayer-tests/tmp-p1
ROUTE=$(python3 - q07 <<'PY'
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
for b in $N $MEM; do $C play "corerpg p1 heal $b" >/dev/null; done
$C play "gamemode creative $N" >/dev/null; sleep 1
IFS='|' read -ra L <<< "$ROUTE"
for i in 0 1 2 3; do
  $T/walk.sh $N "${L[$i]}" | tr -d '\n ' | tail -c 80; echo
  for b in $MEM; do $C play "tp $b $N" >/dev/null; done
  sleep 3
  for k in 1 2 3; do
    $C play "corerpg p1 runs weaken $N" 0.5 | tail -1 | grep -o "削弱.*\|没有.*"
    for b in $MEM; do ($T/fight.sh $b 20000 0 24 4000 >/dev/null 2>&1 &); done
    $T/fight.sh $N 22000 0 24 4000 | tr -d '\n ' | grep -o '"hits":[0-9]*'
  done
  sleep 2
done
sleep 4
for b in $N $MEM; do echo "== $b"; $T/b.sh eval $b "return bot.chatLog.slice(-6).map(s=>s.replace(/§./g,''))" | tr -d '\n'; echo; done
$C play "gamemode survival $N" >/dev/null
