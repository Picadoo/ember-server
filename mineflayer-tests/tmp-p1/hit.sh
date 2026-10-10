#!/usr/bin/env bash
# hit.sh NAME HITS TARGET_SUBSTR [gapMs=900] [slot=0]
JS="const HITS=$2, TGT=$(node -e 'console.log(JSON.stringify(process.argv[1]))' "$3"), GAP=${4:-900}, SLOT=${5:-0};
$(cat /workspace/minecraft/mineflayer-tests/tmp-p1/hit.js)"
/workspace/minecraft/mineflayer-tests/tmp-p1/b.sh eval "$1" "$JS"
