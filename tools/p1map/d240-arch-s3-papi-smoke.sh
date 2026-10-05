#!/usr/bin/env bash
# d240-arch-s3-papi-smoke.sh — CoreRpg 1.65.66 D240: PAPI 分节 (EmberRunPapi + CorePapi* sections).
# Behaviour-preserving: every %corerpg_*% / %ember_*% key used by live configs (+ edge keys) must resolve the same
#  (a) across the jar swap: BASE=<pre-deploy snapshot tsv of bot $F1> vs a fresh session of $F1 on 1.65.66;
#  (b) across sessions 2 and 3 of a fresh bot $F2 on 1.65.66 (session 1 registers; PAPI parse needs a cached player).
# Time-ticking keys (online minutes / online activity, skill cooldown) are excluded from the diff. Bots FreshQ824+.
set -uo pipefail
M=/workspace/minecraft; C=$M/scripts/console.sh; BOTD=http://127.0.0.1:8765; LOG=$M/server-runtime/logs/latest.log
SNAP=$M/tools/p1map/papi-snapshot.sh; OUT=${OUT:-/tmp/d240}; mkdir -p "$OUT"
PASS=0; FAIL=0; SOFT=0
ok(){ echo "PASS $1"; PASS=$((PASS+1)); }; bad(){ echo "FAIL $1"; FAIL=$((FAIL+1)); }; soft(){ echo "SOFT $1"; SOFT=$((SOFT+1)); }
TICK='^(corerpg_p1_online_min|corerpg_p1_online_next|corerpg_activity|corerpg_kit_charge|corerpg_skill_charge_ready)\b' # online-minute / cooldown ticks
curl -sf "$BOTD/list" >/dev/null || { echo "FAIL botd"; exit 1; }
join(){ curl -sf "$BOTD/quit?name=$1" >/dev/null 2>&1 || true; sleep 1
  curl -sf -m 90 "$BOTD/join?name=$1" >/dev/null || { bad "$1 join"; return 1; }; sleep 8
  $C play "list" 0.6 | tr -d '\r' | grep -q "$1" && ok "$1 online" || { bad "$1 online"; return 1; }; }
papi_ok(){ for i in $(seq 1 10); do $C play "papi parse $1 %player_name%" 0.5 | tr -d '\r' | grep -q "Failed to find player" || return 0; sleep 2; done; return 1; }
quit(){ curl -sf "$BOTD/quit?name=$1" >/dev/null 2>&1 || true; sleep 2; }
same(){ local a=$1 b=$2 label=$3; local D; D=$(diff <(grep -vE "$TICK" "$a") <(grep -vE "$TICK" "$b"))
  local n; n=$(grep -vcE "$TICK" "$b")
  [[ -z "$D" ]] && ok "$label ($n keys identical)" || { bad "$label"; echo "$D" | head -20; }; }

grep -aq "Enabling CoreRpg v1.65.66" "$LOG" && ok "CoreRpg 1.65.66" || bad "CoreRpg 1.65.66"
grep -aq "\[CoreRpg\] \[storage\] MySQL connected" "$LOG" && ok "CoreRpg MySQL" || bad "CoreRpg MySQL"
grep -aq "\[CoreGacha\] \[db\] MySQL connected" "$LOG" && ok "CoreGacha MySQL" || bad "CoreGacha MySQL"
grep -aq "ember-v1-economy.yml loaded as amount() SoT (bv58)" "$LOG" && ok "E1 SoT bv58" || bad "E1 SoT bv58"
grep -aq "FAIL-CLOSED" "$LOG" && bad "FAIL-CLOSED" || ok "no FAIL-CLOSED"
grep -aq "PAPI expansion registered: ember (ladder)" "$LOG" && ok "PAPI registered" || bad "PAPI registered"

F1=${F1:-FreshQ824}; F2=${F2:-FreshQ825}; BASE=${BASE:-$OUT/pre.tsv}
if [[ -s "$BASE" ]]; then
  join "$F1" && papi_ok "$F1" && { bash "$SNAP" "$F1" "$OUT/post.tsv" >/dev/null; quit "$F1"; same "$BASE" "$OUT/post.tsv" "$F1 1.65.65→1.65.66 PAPI"; }
else soft "no pre-deploy baseline ($BASE)"; fi

# session 1: first-ever join (AuthMe register). `papi parse` cannot name a never-cached player until it has quit once.
join "$F2" || exit 1; quit "$F2"
# session 2
join "$F2" || exit 1
papi_ok "$F2" && ok "$F2 PAPI resolvable (2nd session)" || bad "$F2 PAPI resolvable (2nd session)"
bash "$SNAP" "$F2" "$OUT/s1.tsv" >/dev/null
FR=$($C play "papi parse $F2 %corerpg_p1_failrefund%|%corerpg_p1_q01_state%|%corerpg_stamina_max%|%corerpg_gate_elite%|%ember_power_score%" 0.5 | tr -d '\r' | tail -1)
echo "  live: $(echo "$FR" | head -c 260)"
echo "$FR" | grep -aq "失败退还" && ok "failrefund label (online)" || bad "failrefund label"
echo "$FR" | grep -aqE "\|已解锁\||\|已首通\|" && ok "q01_state map tail" || bad "q01_state map tail"
quit "$F2"
# session 3
join "$F2" || exit 1; papi_ok "$F2" || true
bash "$SNAP" "$F2" "$OUT/s2.tsv" >/dev/null; quit "$F2"
same "$OUT/s1.tsv" "$OUT/s2.tsv" "$F2 session2→session3 PAPI"
U=$(grep -cP '\t%(corerpg|ember)_' "$OUT/s2.tsv"); echo "  unresolved (null → literal) keys: $U"
grep -P '\t%(corerpg|ember)_' "$OUT/s2.tsv" | cut -f1 | tr '\n' ' '; echo
[[ "$U" == "$(grep -cP '\t%(corerpg|ember)_' "${BASE:-$OUT/s1.tsv}" 2>/dev/null || echo "$U")" ]] && ok "null-key set unchanged ($U)" || bad "null-key set changed"

BOOT=$(rg -n "Enabling CoreRpg v1.65.66" "$LOG" | tail -1 | cut -d: -f1)
POST=$(tail -n +"$BOOT" "$LOG" | rg -c "SEVERE" || true); POST=${POST:-0}
[[ "$POST" == "0" ]] && ok "SEVERE 0 (post-enable)" || bad "SEVERE post-enable=$POST"
echo "==== RESULT PASS $PASS / FAIL $FAIL / SOFT $SOFT ===="
[[ "$FAIL" == "0" ]]
