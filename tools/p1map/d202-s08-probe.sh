#!/usr/bin/env bash
# D202 ARCH S0-8 live probe (docs only): can a fresh non-OP bot open legacy TrMenu menus or Multiverse-teleport?
set -uo pipefail
B=${B:-FreshQ736}
C=/workspace/minecraft/scripts/console.sh
BOTD=http://127.0.0.1:8765
T=/workspace/minecraft/mineflayer-tests/tmp-p1
LOG=/workspace/minecraft/server-runtime/logs/latest.log
say(){ $T/b.sh chat "$B" "$1" "${2:-2500}" | tr -d '\r'; }
curl -sf "$BOTD/quit?name=$B" >/dev/null 2>&1; sleep 1
curl -sf -m 90 "$BOTD/join?name=$B" >/dev/null || { echo "join fail"; exit 1; }; sleep 7
N0=$(($(wc -l < "$LOG")+1))
for CMD in "/trmenu open ember_hub_legacy" "/trmenu open ember_daily" "/trmenu open ember_arena" "/trmenu open ember_shop" "/trmenu open ember_calamity" "/trmenu open ember_guild" "/trmenu" "/mvtp ember_event" "/mv tp ember_event" "/mvtp world" "/mv list" "/tp $B 0 100 0" "/spawn" "/dp start EmberDaily" "/dungeonplus start EmberDaily" "/mm" "/ni" "/lp" "/warp"; do
  R=$(say "$CMD" 2200); echo "== $CMD -> $(echo "$R" | tr '\n' ' ' | cut -c1-220)"
done
echo "--- curl world:"; curl -s "$BOTD/state?name=$B" 2>/dev/null | head -c 400; echo
echo "--- log:"; tail -n +"$N0" "$LOG" | tr -d '\r' | grep -iE "$B|SEVERE" | cut -c1-220 | head -40
curl -sf "$BOTD/quit?name=$B" >/dev/null 2>&1
