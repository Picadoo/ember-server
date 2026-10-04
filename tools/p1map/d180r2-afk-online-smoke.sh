#!/usr/bin/env bash
# d180r2-afk-online-smoke.sh [BOT] — D180 rev 2: minutes in ember_afk count as online time while auto-combat runs,
# even with no player input for longer than idle_minutes (5). ~9 minutes.
# Q01 first clear → /corerpg afk 1 (enter T1, auto-combat) → no input for 7.5 min → counted minutes +6 or more →
# settles logged (fighting) → claim the 15-min tier → once only.
set -uo pipefail
B=${1:-FreshQ300}
C=/workspace/minecraft/scripts/console.sh
BOTD=http://127.0.0.1:8765
T=/workspace/minecraft/mineflayer-tests/tmp-p1
LOG=/workspace/minecraft/server-runtime/logs/latest.log
PASS=0; FAIL=0
ok(){ echo "PASS $1"; PASS=$((PASS+1)); }
bad(){ echo "FAIL $1"; FAIL=$((FAIL+1)); }
ev(){ $T/b.sh eval "$B" "$1" 2>/dev/null; }
chatsince(){ ev "const n=bot.chatLog.length; bot.chat('$1'); await wait(${2:-1800}); return bot.chatLog.slice(n).map(s=>s.replace(/§./g,'')).join(' / ');"; }
mins(){ $C play "corerpg p1 online test $B 0" 1.2 | tr -d '\r' | grep -oE '今日有效在线 = [0-9]+' | grep -oE '[0-9]+$' | tail -1; }

curl -sf "$BOTD/list" >/dev/null || { echo "FAIL botd"; exit 1; }
curl -sf "$BOTD/quit?name=$B" >/dev/null 2>&1 || true; sleep 1
curl -sf -m 90 "$BOTD/join?name=$B" >/dev/null || { bad "join"; exit 1; }
sleep 7
$C play "list" 0.5 | tr -d '\r' | grep -q "$B" && ok "$B online" || { bad "$B online"; exit 1; }
$C play "corerpg p1 runs firstclear $B q01" 0.6 >/dev/null
$C play "effect $B minecraft:resistance 900 4 true" 0.4 >/dev/null   # test only: the fresh bot must not die during the wait
$C play "effect $B minecraft:regeneration 900 2 true" 0.4 >/dev/null
M0=$(mins); echo "counted minutes before: $M0"
N0=$(wc -l < "$LOG")
R=$(chatsince '/corerpg afk 1' 6000); echo "$R" | cut -c1-240
W=$(ev "return bot.game.dimension + ' @ ' + bot.entity.position.toString()"); echo "$W" | tr -d '\n' | cut -c1-120; echo
echo "waiting 450 s with no input…"
sleep 450
M1=$(mins); echo "counted minutes after: $M1"
SET=$(tail -n +"$((N0+1))" "$LOG" | grep -a "\[P1 afk\] $B settle" | wc -l); echo "settle lines: $SET"
[ "$SET" -ge 1 ] && ok "auto-combat ran in ember_afk ($SET settles)" || bad "no AFK settles for $B"
[ -n "$M0" ] && [ -n "$M1" ] && [ $((M1-M0)) -ge 6 ] && ok "ember_afk minutes counted with no input for 7.5 min (+$((M1-M0)))" || bad "minutes $M0 → $M1"
R=$(chatsince '/corerpg p1 online claim' 2500); echo "$R" | cut -c1-200
echo "$R" | grep -q '15 分钟' && ok "15-min tier claimed" || bad "claim ($R)"
R=$(chatsince '/corerpg p1 online claim' 1800); echo "$R" | grep -qv '15 分钟：' && ok "15-min tier not twice" || bad "twice ($R)"
grep -a "SEVERE" "$LOG" | tail -n +1 | wc -l | grep -qx 0 && ok "no SEVERE" || bad "SEVERE in log"
curl -sf "$BOTD/quit?name=$B" >/dev/null 2>&1 || true
echo "RESULT $PASS pass / $FAIL fail"
