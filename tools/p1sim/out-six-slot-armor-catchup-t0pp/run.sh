#!/bin/bash
# D316 T0″ pipeline (resume-safe: each step skipped if its output exists). NPROC 2, nice.
set -u
cd /workspace/minecraft/tools/p1sim
export P1SIM_CRN=1 NPROC=2
D=/workspace/d316
F=$(python3 -c "import json;print(json.dumps(json.load(open('$D/arms.json'))['F']))")
E=$(python3 -c "import json;print(json.dumps(json.load(open('$D/arms.json'))['E']))")
A=$(python3 -c "import json;print(json.dumps(json.load(open('$D/arms.json'))['t0p_A']))")
step(){ out=$1; shift; if [ -s "$out" ]; then echo "skip $out"; return; fi; echo "$(date +%H:%M:%S) start $out"; "$@" > "$out.tmp" 2> "$out.err" && mv "$out.tmp" "$out"; echo "$(date +%H:%M:%S) end $out rc=$?"; }
step $D/g8-raid-A.md nice -n 15 python3 six_t0.py raid "$A" $D/raid-A.json --trials 1000 --procs 2
step $D/g8-raid-F.md nice -n 15 python3 six_t0.py raid "$F" $D/raid-F.json --trials 1000 --procs 2
step $D/g8-raid-E.md nice -n 15 python3 six_t0.py raid "$E" $D/raid-E.json --trials 1000 --procs 2
step $D/g56-F.txt nice -n 15 python3 six_t0.py g56 "$F"
step $D/g56-E.txt nice -n 15 python3 six_t0.py g56 "$E"
step $D/prog-a.log nice -n 15 python3 gear6.py prog "{\"F\":$F,\"E\":$E}" 4000 $D/fe-a.pkl
GEAR6_SEED0=100000 step $D/prog-b.log env GEAR6_SEED0=100000 nice -n 15 python3 gear6.py prog "{\"F\":$F,\"E\":$E}" 4000 $D/fe-b.pkl
step $D/g8-afk-F.md nice -n 15 python3 six_t0.py afk "$F" 1200 $D/afk-F.json
step $D/g8-afk-E.md nice -n 15 python3 six_t0.py afk "$E" 1200 $D/afk-E.json
for nm in F E; do for mode in base rot; do step $D/w30-$nm-$mode.txt nice -n 15 python3 $D/w30.py $nm $mode; done; done
step $D/p2-F.md env P1SIM_SIX="$F" nice -n 15 python3 p2econ.py --players 300 --weeks 12 --dodge 0.5 --abyss --raid --goals --every-week
step $D/p2-E.md env P1SIM_SIX="$E" nice -n 15 python3 p2econ.py --players 300 --weeks 12 --dodge 0.5 --abyss --raid --goals --every-week
echo "$(date +%H:%M:%S) ALL DONE"
