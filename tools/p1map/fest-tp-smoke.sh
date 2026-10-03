#!/usr/bin/env bash
# fest-tp-smoke.sh NAME [weaken-ratio] : D139 event run without pathing — tp into each room (r1 → r2 → r3 → boss hall),
# admin weaken (ratio 0 = 1 HP; e.g. 0.3 leaves the mobs alive long enough for 烟火迸发 to hit), then the bot fights;
# a still-living mob gets the bot teleported next to it. Assumes the bot is already inside the instance.
N=$1; R=${2:-0}; C=/workspace/minecraft/scripts/console.sh; T=/workspace/minecraft/mineflayer-tests/tmp-p1
for spot in "0 64 34" "-36 64 66" "14 64 100" "0 64 136"; do
  $C play "tp $N $spot" 0.5 >/dev/null; sleep 3
  for k in 1 2 3 4 5; do
    if [ "$R" != "0" ]; then $C play "corerpg p1 runs weaken $N $R" 0.5 | tail -1; else $C play "corerpg p1 runs weaken $N" 0.5 | tail -1; fi
    OUT=$($T/fight.sh $N 20000 0 24 4000 | tr -d '\n ')
    echo "$OUT" | tail -c 200; echo
    LEFT=$(echo "$OUT" | python3 -c 'import sys,json,re
try:
  d=json.loads(sys.stdin.read())["r"]["left"]
  print(re.sub(r"[()]","",d[0][1]).replace(","," ") if d else "")
except Exception: print("")')
    [ -z "$LEFT" ] && break
    $C play "tp $N $LEFT" 0.5 >/dev/null; sleep 1
  done
  sleep 2
done
sleep 4
$T/b.sh eval $N "return bot.chatLog.slice(-14).map(s=>s.replace(/§./g,''))" | tr -d '\n'; echo
