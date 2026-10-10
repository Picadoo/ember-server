#!/usr/bin/env bash
# arena.sh NAME [x z]  → world 120,151,118 platform, survival, full heal
C="/workspace/minecraft/scripts/console.sh play"
N=$1; X=${2:-120}; Z=${3:-118}
$C "mvtp $N world" 1.5 >/dev/null
$C "tp $N $X 151 $Z" 1 >/dev/null
$C "gamemode 0 $N" 0.3 >/dev/null
$C "effect $N minecraft:instant_health 1 5" 0.3 >/dev/null
$C "effect $N minecraft:saturation 1 10" 0.3 >/dev/null
$C "corerpg p1 heal $N" 0.3 >/dev/null
