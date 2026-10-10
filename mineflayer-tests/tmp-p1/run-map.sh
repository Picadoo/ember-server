#!/usr/bin/env bash
# run-map.sh NAME SLOT "x y z" "x y z" ...  (tp to each room point, fight until idle)
N=$1; S=$2; shift 2
for P in "$@"; do
  /workspace/minecraft/scripts/console.sh play "tp $N $P" 0.5 >/dev/null; sleep 1.5
  /workspace/minecraft/mineflayer-tests/tmp-p1/fight.sh $N 120000 $S 22 5000 | tr -d '\n '; echo
done
