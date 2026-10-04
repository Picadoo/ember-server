#!/usr/bin/env bash
# fight-kite.sh NAME [maxMs=60000] [slot=0] [radius=24] [stopIdleMs=6000]
# env: MOVE=stand|kite (default stand = the old stand-and-swing bot), DRINK=1 (potion at 55 %), DOOR='[x,z]', ROOM='[x0,z0,x1,z1]',
#      REACT=350 (ms), MISS=0.15, HOLDMS=6000, REACH=3.0. Body: tools/p1map/fight-kite.js, run in the local botd (127.0.0.1:8765).
JS="const MAXMS=${2:-60000}, SLOT=${3:-0}, RADIUS=${4:-24}, STOPIDLE=${5:-6000}, SKIP='木桩', DRINK=${DRINK:-0}, MOVE='${MOVE:-stand}',
 DOOR=${DOOR:-null}, ROOM=${ROOM:-null}, REACT=${REACT:-350}, MISS=${MISS:-0.15}, HOLDMS=${HOLDMS:-6000}, REACH=${REACH:-3.0};
$(cat "$(dirname "$0")/fight-kite.js")"
curl -s -X POST --data-binary "$JS" "http://127.0.0.1:8765/eval?name=$1"; echo
