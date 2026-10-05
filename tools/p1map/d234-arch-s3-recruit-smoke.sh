#!/usr/bin/env bash
# d234-arch-s3-recruit-smoke.sh — CoreRpg 1.65.60 D234: EmberRecruitService extract smoke.
# Recruit board empty/list/post/papi + join path if feasible + Q01 settle. Board/cooldown/label pinned by unit tests.
# Bots FreshQ810+. No asset path → no persist-roundtrip.
set -uo pipefail
C=/workspace/minecraft/scripts/console.sh; BOTD=http://127.0.0.1:8765; T=/workspace/minecraft/mineflayer-tests/tmp-p1
LOG=/workspace/minecraft/server-runtime/logs/latest.log
PASS=0; FAIL=0; SOFT=0
ok(){ echo "PASS $1"; PASS=$((PASS+1)); }; bad(){ echo "FAIL $1"; FAIL=$((FAIL+1)); }; soft(){ echo "SOFT $1"; SOFT=$((SOFT+1)); }
curl -sf "$BOTD/list" >/dev/null || { echo "FAIL botd"; exit 1; }
chat(){ $T/b.sh eval "$1" "return bot.chatLog.slice(-${2:-60}).map(s=>s.replace(/§./g,'')).join('\n')" 2>/dev/null || true; }
weaken_n(){ local W; W=$($C play "corerpg p1 runs weaken $1" 0.4 2>/dev/null | tr -d '\r' || true)
  if echo "$W" | grep -q '没有进行中'; then echo -1; return; fi
  local N; N=$(echo "$W" | sed -n 's/.*削弱 \([0-9]*\) 只.*/\1/p' | tail -1); echo "${N:--2}"; }
clear_room(){ local B=$1; local saw=0; for k in $(seq 1 40); do
  N=$(weaken_n "$B"); [[ "$N" == -1 ]] && return 1
  [[ "$N" =~ ^[1-9] ]] && saw=1
  [[ "$saw" == 1 && "$N" == 0 ]] && return 0
  $C play "corerpg p1 heal $B" 0.1 >/dev/null || true
  $T/fight.sh "$B" 14000 0 40 2000 >/dev/null 2>&1 || true
done; [[ "$saw" == 1 && "$(weaken_n "$B")" == 0 ]]; }
settle(){ local B=$1; for i in $(seq 1 50); do N=$(weaken_n "$B"); [[ "$N" == -1 ]] && break
  $C play "corerpg p1 heal $B" 0.1 >/dev/null || true; $T/fight.sh "$B" 10000 0 48 1500 >/dev/null 2>&1 || true; sleep 1; done; }
join(){ local B=$1; curl -sf "$BOTD/quit?name=$B" >/dev/null 2>&1 || true; sleep 1
  curl -sf -m 90 "$BOTD/join?name=$B" >/dev/null || { bad "$B join"; return 1; }; sleep 8
  $C play "list" 0.6 | tr -d '\r' | grep -q "$B" && ok "$B online" || { bad "$B online"; return 1; }
  $C play "corerpg stamina set $B 200" 0.4 >/dev/null; }
quitall(){ for b in "$@"; do curl -sf "$BOTD/quit?name=$b" >/dev/null 2>&1 || true; done; sleep 2; }
say(){ local B=$1; local MSG=$2; local W=${3:-4000}; $T/b.sh chat "$B" "$MSG" "$W" 2>/dev/null | tr -d '\r'; }
fc_all(){ for q in q01 q02 q03 q04 q05 q06 q07; do $C play "corerpg p1 runs firstclear $1 $q" 0.2 >/dev/null; done
  for q in q02 q03 q04 q05 q06 q07; do $C play "corerpg p1 runs unlock $1 $q" 0.2 >/dev/null; done; }

grep -aq "Enabling CoreRpg v1.65.60" "$LOG" && ok "CoreRpg 1.65.60" || bad "CoreRpg 1.65.60"
grep -aq "\[CoreRpg\] \[storage\] MySQL connected" "$LOG" && ok "CoreRpg MySQL" || bad "CoreRpg MySQL"
grep -aq "\[CoreGacha\] \[db\] MySQL connected" "$LOG" && ok "CoreGacha MySQL" || bad "CoreGacha MySQL"
grep -aq "ember-v1-economy.yml loaded as amount() SoT (bv58)" "$LOG" && ok "E1 SoT bv58" || bad "E1 SoT bv58"
grep -aq "FAIL-CLOSED" "$LOG" && bad "FAIL-CLOSED" || ok "no FAIL-CLOSED"

L=${L:-FreshQ810}; M=${M:-FreshQ811}; Q=${Q:-FreshQ812}

# ---- recruit board: empty → post → list → papi → join
for b in $L $M; do join $b || exit 1; fc_all $b; done
for b in $L $M; do $T/b.sh chat "$b" "/dungeon-team disband" 800 >/dev/null || true; done
sleep 1

R=$(say "$L" "/corerpg p1 recruit list" 4000); echo "  empty list: $(echo "$R" | head -c 280)"
echo "$R" | grep -aq "现在没有团本在招人" && ok "recruit empty list" || bad "recruit empty list"

$T/b.sh chat "$L" "/dungeon-team create" 1200 >/dev/null || true
R=$(say "$L" "/corerpg p1 recruit r01" 6000); echo "  post r01: $(echo "$R" | head -c 420)"
echo "$R" | grep -aqE "已向 .* 位已首通 Q07|现在没有其他已首通 Q07" && ok "recruit post (cmd)" || bad "recruit post (cmd)"
echo "$R" | grep -aq "招募挂在冒险页团本图标上 10 分钟" && ok "recruit post TTL note" || soft "recruit post TTL note"

# receiver should see the call (if progress matched)
CHM=$(chat "$M" 30); echo "  M chat: $(echo "$CHM" | grep -aE '招|申请入队|锈轨' | tail -3 | tr '\n' ' ' | head -c 360)"
echo "$CHM" | grep -aqE "招 .*锈轨矿道·团|申请入队" && ok "recruit broadcast to peer" || soft "recruit broadcast to peer (alone/timing)"

R=$(say "$L" "/corerpg p1 recruit list" 4500); echo "  board: $(echo "$R" | head -c 360)"
echo "$R" | grep -aq "团本招募板" && ok "recruit board header" || bad "recruit board header"
echo "$R" | grep -aqE "R01 · $L · |你的招募" && ok "recruit board own entry" || bad "recruit board own entry"

PP=$($C play "papi parse $L %corerpg_p1_recruits%" 0.8 | tr -d '\r' | tail -3); echo "  papi recruits: $PP"
echo "$PP" | grep -aqE "R01 · $L|暂无（右键发一个）" && ok "recruit papi label" || soft "recruit papi label"

# join path: M applies to L via recruit join → DP request
R=$(say "$M" "/corerpg p1 recruit join $L" 5000); echo "  join: $(echo "$R" | head -c 280)"
# DP may print its own request line; we only assert the command did not error with our reds
if echo "$R" | grep -aqE "队长不在线|你已经在一支队伍里了"; then
  soft "recruit join blocked ($(echo "$R" | tr '\n' ' ' | head -c 120))"
else
  ok "recruit join dispatched"
fi

# cooldown: immediate re-post should refuse
R=$(say "$L" "/corerpg p1 recruit r01" 3500); echo "  cooldown: $(echo "$R" | head -c 200)"
echo "$R" | grep -aq "招募 60 秒内只能发一次" && ok "recruit cooldown" || soft "recruit cooldown"

$T/b.sh chat "$L" "/dungeon-team disband" 800 >/dev/null || true
quitall $L $M

# ---- Q01 normal settle regression
join $Q || exit 1
N2=$(wc -l < "$LOG")
$C play "gamemode creative $Q" 0.3 >/dev/null
R=$($T/b.sh chat "$Q" "/corerpg p1 enter q01" 12000 | tr -d '\r'); echo "  q01: $(echo "$R" | head -c 280)"; sleep 6
$C play "tp $Q 0 64 28" 0.3 >/dev/null; sleep 2; clear_room "$Q" && ok "q01 r1" || bad "q01 r1"
$C play "tp $Q 30 64 38" 0.3 >/dev/null; sleep 2; clear_room "$Q" && ok "q01 r2" || bad "q01 r2"
$C play "tp $Q 32 64 68" 0.3 >/dev/null; sleep 2; clear_room "$Q" && ok "q01 r3" || bad "q01 r3"
$C play "tp $Q 0 64 102" 0.3 >/dev/null; sleep 2
settle "$Q"; sleep 4
$C play "gamemode survival $Q" 0.3 >/dev/null
FULL2=$(tail -n +"$((N2+1))" "$LOG" | tr -d '\r')
echo "$FULL2" | grep -aqE "\[P1 run\] q01-.* settle" && ok "q01 settle" || bad "q01 settle"
echo "$FULL2" | grep -aE "economy grant(Coin|Mat|Xp|Mark|Badge) refused" && bad "grant refused" || ok "no grant refusal q01"
quitall $Q

SEV=$(rg -c "SEVERE" "$LOG" 2>/dev/null || echo 0)
# only count SEVERE after our marker if possible — full-file is fine for short window deploys
echo "SEVERE lines in latest.log (grep count): $SEV"
rg -a "SEVERE" "$LOG" 2>/dev/null | tail -5 || true
# prefer post-enable SEVERE for this boot
BOOT=$(rg -n "Enabling CoreRpg v1.65.60" "$LOG" | tail -1 | cut -d: -f1)
if [[ -n "$BOOT" ]]; then
  POST=$(tail -n +"$((BOOT))" "$LOG" | rg -c "SEVERE" || true); POST=${POST:-0}
  [[ "$POST" == "0" ]] && ok "SEVERE 0 (post-enable)" || bad "SEVERE post-enable=$POST"
else
  soft "SEVERE boot marker missing"
fi

echo "==== RESULT PASS $PASS / FAIL $FAIL / SOFT $SOFT ===="
[[ "$FAIL" == "0" ]]
