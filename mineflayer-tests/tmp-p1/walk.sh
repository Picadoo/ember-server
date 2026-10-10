#!/usr/bin/env bash
# walk.sh NAME '[[x,z],...]'
JS="const PTS=$2;
$(cat /workspace/minecraft/mineflayer-tests/tmp-p1/walk.js)"
/workspace/minecraft/mineflayer-tests/tmp-p1/b.sh eval "$1" "$JS"
