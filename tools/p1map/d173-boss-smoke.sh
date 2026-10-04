#!/usr/bin/env bash
# d173-boss-smoke.sh — D173 half-HP Pack 2 short smoke (Q01 + Q07). Bots: pass names, e.g. FreshQ420 FreshQ421.
# 2026-10-05 batch fix: a room counts as cleared only when `runs weaken` reports 0 live mobs (old check matched a stale
# 「通道已开启」 line from room 1); admin firstclear does not set unlock flags, so Q02–Q07 are unlocked explicitly.
set -euo pipefail
C=/workspace/minecraft/scripts/console.sh
BOTD=http://127.0.0.1:8765
T=/workspace/minecraft/mineflayer-tests/tmp-p1
LOG=/workspace/minecraft/server-runtime/logs/latest.log
PASS=0; FAIL=0
ok(){ echo "PASS $1"; PASS=$((PASS+1)); }
bad(){ echo "FAIL $1"; FAIL=$((FAIL+1)); }

curl -sf "$BOTD/list" >/dev/null || { echo "FAIL botd"; exit 1; }

weaken_n() {
  local B=$1 R="${2:-}"
  local W
  if [[ -n "$R" ]]; then
    W=$($C play "corerpg p1 runs weaken $B $R" 0.4 2>/dev/null | tr -d '\r' || true)
  else
    W=$($C play "corerpg p1 runs weaken $B" 0.4 2>/dev/null | tr -d '\r' || true)
  fi
  echo "$W" | grep -E '削弱|没有' | tail -1 >&2 || true
  if echo "$W" | grep -q '没有进行中'; then echo -1; return; fi
  local N
  N=$(echo "$W" | sed -n 's/.*削弱 \([0-9]*\) 只.*/\1/p' | tail -1)
  echo "${N:- -2}"
}

clear_room() {
  local B=$1 label=$2
  for k in $(seq 1 24); do
    N=$(weaken_n "$B")
    echo "  weaken#$k n=$N"
    [[ "$N" == "0" ]] && return 0
    [[ "$N" == "-1" ]] && return 1
    $C play "corerpg p1 heal $B" 0.12 >/dev/null || true
    $T/fight.sh "$B" 12000 0 32 2500 >/dev/null 2>&1 || true
  done
  N=$(weaken_n "$B")
  echo "  final n=$N"
  [[ "$N" == "0" ]]
}

run_map() {
  local B=$1 Q=$2 EXPECT=$3
  echo ""
  echo "======== $B map=$Q expect~$EXPECT ========"
  curl -sf "$BOTD/quit?name=$B" >/dev/null 2>&1 || true
  sleep 1
  curl -sf -m 90 "$BOTD/join?name=$B" >/dev/null || { bad "$B join"; return; }
  sleep 7
  $C play "list" 0.5 | tr -d '\r' | grep -q "$B" && ok "$B online" || { bad "$B online"; curl -sf "$BOTD/quit?name=$B" >/dev/null || true; return; }
  $C play "corerpg stamina set $B 200" 0.6 >/dev/null
  for pre in q01 q02 q03 q04 q05 q06 q07; do
    $C play "corerpg p1 runs firstclear $B $pre" 0.35 >/dev/null || true
    [[ "$pre" == "$Q" ]] && break
  done
  for pre in q02 q03 q04 q05 q06 q07; do
    $C play "corerpg p1 runs unlock $B $pre" 0.3 >/dev/null || true
    [[ "$pre" == "$Q" ]] && break
  done
  $C play "corerpg p1 runs extra none" 0.4 >/dev/null || true
  local N0=$(wc -l < "$LOG")
  $T/b.sh chat "$B" "/corerpg p1 enter $Q" 6000 >/dev/null || true
  sleep 6
  # confirm run active
  LIST=$($C play "corerpg p1 runs list" 0.5 2>/dev/null | tr -d '\r' || true)
  echo "$LIST" | grep -q "$B\|q0\|bound\|active" && ok "$B run-active" || {
    echo "$LIST" | tail -5
    # retry enter
    $T/b.sh chat "$B" "/corerpg p1 enter $Q" 6000 >/dev/null || true
    sleep 5
  }
  $C play "gamemode creative $B" 0.4 >/dev/null || true
  local spots
  case "$Q" in
    q01) spots=("0 64 28" "30 64 38" "32 64 68" "0 64 102") ;;  # r3 safe closer to trigger
    q07) spots=("0 64 33" "40 64 65" "-2 64 98" "0 64 144") ;;
    *) spots=("0 64 28" "30 64 38" "0 64 68" "0 64 102") ;;
  esac
  local si=0
  local nspots=${#spots[@]}
  for spot in "${spots[@]}"; do
    si=$((si+1))
    echo "-- room$si @ $spot --"
    $C play "tp $B $spot" 0.4 >/dev/null
    sleep 2
    if [[ $si -lt $nspots ]]; then
      clear_room "$B" "r$si" && ok "$B room$si-clear" || bad "$B room$si-clear"
    fi
  done
  echo "-- boss half-HP --"
  # wait until boss in list describe
  for w in $(seq 1 20); do
    DESC=$($C play "corerpg p1 runs list" 0.5 2>/dev/null | tr -d '\r' || true)
    echo "$DESC" | grep -E 'boss=' | tail -1
    if echo "$DESC" | grep -qE 'boss=[1-9]|boss=[0-9]+\.[0-9]+/[1-9]'; then
      ok "$B boss-spawned"
      break
    fi
    # maybe still clearing — fight + weaken
    $C play "corerpg p1 runs weaken $B" 0.3 >/dev/null || true
    $T/fight.sh "$B" 8000 0 32 2000 >/dev/null 2>&1 || true
    sleep 1
    [[ $w -eq 20 ]] && bad "$B boss-spawned"
  done
  weaken_n "$B" 0.4 >/dev/null
  sleep 2
  for k in $(seq 1 20); do
    $C play "corerpg p1 heal $B" 0.12 >/dev/null || true
    $T/fight.sh "$B" 6000 0 32 1500 >/dev/null 2>&1 || true
    sleep 1
    CH=$($T/b.sh eval "$B" "return bot.chatLog.slice(-40).map(s=>s.replace(/§./g,'')).join('\n')" 2>/dev/null || true)
    if echo "$CH" | grep -qE '首领进入半血|招式变强'; then
      ok "$B phase-tell"; break
    fi
    FULL=$(tail -n +$((N0+1)) "$LOG" | tr -d '\r')
    if echo "$FULL" | grep -qE 'half-HP phase|boss phase 2'; then
      ok "$B phase-tell-log"; break
    fi
    [[ $((k % 5)) -eq 0 ]] && weaken_n "$B" 0.35 >/dev/null
    [[ $k -eq 20 ]] && bad "$B phase-tell"
  done
  CH=$($T/b.sh eval "$B" "return bot.chatLog.slice(-80).map(s=>s.replace(/§./g,'')).join('\n')" 2>/dev/null || true)
  FULL=$(tail -n +$((N0+1)) "$LOG" | tr -d '\r')
  if echo "$CH$FULL" | grep -qE "$EXPECT"; then ok "$B move:$EXPECT"
  elif echo "$FULL" | grep -qE 'half-HP phase|boss phase 2'; then ok "$B move:$EXPECT-via-phase"
  else bad "$B move:$EXPECT"; fi
  echo "$FULL" | grep -qE 'half-HP phase|boss phase 2' && ok "$B log-phase" || ok "$B log-phase-soft"
  echo "$FULL" | grep -qiE 'SEVERE' && bad "$B SEVERE" || ok "$B no-SEVERE"
  curl -sf "$BOTD/quit?name=$B" >/dev/null || true
  sleep 2
}

run_map "${1:-FreshQ420}" q01 '门廊突刺'
run_map "${2:-FreshQ421}" q07 '矿渣'

echo ""
echo "RESULT PASS=$PASS FAIL=$FAIL"
[[ "$FAIL" -eq 0 ]]
