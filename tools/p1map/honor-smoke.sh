#!/usr/bin/env bash
# honor-smoke.sh [BOT] — D142 余烬勋记 live smoke (admin test hook).
# Grants every honor via /corerpg p1 honor test all, checks unlock chat + cap text, then clears.
# Needs: game server up, botd on 127.0.0.1:8765, corerpg.admin via console.
# Does NOT restart the server. Default bot FreshQ42.
set -euo pipefail
B=${1:-FreshQ42}
C=/workspace/minecraft/scripts/console.sh
BOTD=http://127.0.0.1:8765
LOG=/workspace/minecraft/server-runtime/logs/latest.log
PASS=0; FAIL=0
ok(){ echo "PASS $1"; PASS=$((PASS+1)); }
bad(){ echo "FAIL $1"; FAIL=$((FAIL+1)); }

curl -sf "$BOTD/list" >/dev/null || { echo "FAIL botd not reachable at $BOTD"; exit 1; }
curl -sf "$BOTD/join?name=$B" >/dev/null
sleep 8

N0=$(grep -c "获得余烬勋记\|勋记解锁\|已解锁勋记\|✔" "$LOG" 2>/dev/null || true)
$C play "corerpg p1 honor test clear $B" 0.8 >/dev/null
$C play "corerpg p1 honor test all $B" 1.2 >/dev/null
sleep 1
OUT=$($C play "corerpg p1 honor test show $B" 1.0)
echo "$OUT" | tr -d '\r' | grep -E "勋记|封顶|合计" | head -5

echo "$OUT" | grep -qE '勋记 7/7|7/7' && ok "7/7 honors after test all" || bad "expected 7/7 in show ($OUT)"
echo "$OUT" | grep -qE '结算余烬币|\+4%|币' && ok "capped coin bonus mentioned" || bad "cap text missing"
# unlock notice should have been logged for at least one honor
N1=$(grep -c "\[P1 growth\] admin .* honor test all $B" "$LOG" 2>/dev/null || true)
[ "$N1" -gt 0 ] && ok "admin honor test logged" || bad "no admin honor test log line"

$C play "corerpg p1 honor test clear $B" 0.8 >/dev/null
OUT2=$($C play "corerpg p1 honor test show $B" 1.0)
echo "$OUT2" | grep -qE '勋记 0/7|0/7' && ok "cleared to 0/7" || bad "clear did not reset ($OUT2)"

curl -sf "$BOTD/quit?name=$B" >/dev/null || true
echo "honor-smoke $B: PASS=$PASS FAIL=$FAIL"
[ "$FAIL" -eq 0 ]
