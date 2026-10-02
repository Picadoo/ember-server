#!/usr/bin/env bash
# fight.sh NAME [maxMs=60000] [slot=0] [radius=24] [stopIdleMs=6000]   (env DRINK=1: drink a hotbar heal potion below 55% like a new player would)
JS="const MAXMS=${2:-60000}, SLOT=${3:-0}, RADIUS=${4:-24}, STOPIDLE=${5:-6000}, SKIP='木桩', DRINK=${DRINK:-0};
$(cat /workspace/minecraft/mineflayer-tests/tmp-p1/fight.js)"
/workspace/minecraft/mineflayer-tests/tmp-p1/b.sh eval "$1" "$JS"
