#!/bin/bash
set -u
cd /workspace/minecraft/tools/p1sim
export P1SIM_CRN=1 NPROC=2
D=/workspace/d316
F=$(python3 -c "import json;print(json.dumps(json.load(open('$D/arms.json'))['F']))")
step(){ out=$1; shift; if [ -s "$out" ]; then echo "skip $out"; return; fi; echo "$(date +%H:%M:%S) start $out"; "$@" > "$out.tmp" 2> "$out.err"; rc=$?; [ $rc -eq 0 ] && mv "$out.tmp" "$out"; echo "$(date +%H:%M:%S) end $out rc=$rc"; }
step $D/w30-F-base.txt nice -n 15 python3 $D/w30.py F base
step $D/w30-F-rot.txt nice -n 15 python3 $D/w30.py F rot
step $D/p2-F.md env P1SIM_SIX="$F" nice -n 15 python3 p2econ.py --players 300 --weeks 12 --dodge 0.5 --abyss --raid --goals --every-week
echo "$(date +%H:%M:%S) ALL DONE"
