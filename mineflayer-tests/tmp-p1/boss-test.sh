#!/usr/bin/env bash
# boss-test.sh LEADER "M1 M2 .." QMAP [RATIO] : rooms with admin weaken (fast), then the boss WITHOUT weaken (or weakened
# to RATIO × max HP, e.g. 0.6 to see a 50 % phase); bots fight with the blade in hotbar slot 1, admin heal every HEAL_EVERY s (default 1)
# (bots do not dodge; heals do not change the boss's HP). Reads "boss spawned / phase 2 / killed after" from the log.
N=$1; MEM=$2; Q=${3:-q07}; RATIO=$4; C=/workspace/minecraft/scripts/console.sh; T=/workspace/minecraft/mineflayer-tests/tmp-p1
LOG=/workspace/minecraft/server-runtime/logs/stdout.log
ROUTE=$(python3 - $Q <<'PY'
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
for i in 0 1 2; do
  $T/walk.sh $N "${L[$i]}" >/dev/null
  for b in $MEM; do $C play "tp $b $N" >/dev/null; done
  sleep 3
  for k in 1 2 3; do
    $C play "corerpg p1 runs weaken $N" 0.5 >/dev/null
    for b in $MEM; do ($T/fight.sh $b 15000 1 24 3000 >/dev/null 2>&1 &); done
    $T/fight.sh $N 16000 1 24 3000 >/dev/null
  done
  sleep 2
done
START=$(wc -l < $LOG)
$T/walk.sh $N "${L[3]}" >/dev/null
$C play "gamemode survival $N" >/dev/null
for b in $MEM; do $C play "tp $b $N" >/dev/null; done
sleep 4
[ -n "$RATIO" ] && $C play "corerpg p1 runs weaken $N $RATIO" 0.5 | tail -1
for b in $N $MEM; do ($T/fight.sh $b 240000 1 30 15000 >/dev/null 2>&1 &); done
for t in $(seq 1 ${HEAL_LOOPS:-240}); do
  sleep ${HEAL_EVERY:-1}
  for b in $N $MEM; do $C play "corerpg p1 heal $b" 0.1 >/dev/null 2>&1; done
  tail -n +$START $LOG | grep -q "boss killed after" && break
done
tail -n +$START $LOG | grep -a "boss spawned\|phase 2\|killed after\|weaken\|slain" | cut -c1-160
