#!/usr/bin/env bash
# d180-signin-smoke.sh [BOT] — D180 每日签到 + 在线时长 with clicks only (a few minutes).
# join hint → hub icon (slot 27) → sign page → click today's cell (n=1, slot 10) → paid once → click again / reconnect → no double →
# make-up refused (<60 min) → admin +60 counted min → claim 15/30/60 by click → make-up by click → reconnect → no re-claim →
# legacy /corerpg sign / activity / bounty claim pay no coin.
set -uo pipefail
B=${1:-FreshQ170}
C=/workspace/minecraft/scripts/console.sh
BOTD=http://127.0.0.1:8765
T=/workspace/minecraft/mineflayer-tests/tmp-p1
LOG=/workspace/minecraft/server-runtime/logs/latest.log
PASS=0; FAIL=0
ok(){ echo "PASS $1"; PASS=$((PASS+1)); }
bad(){ echo "FAIL $1"; FAIL=$((FAIL+1)); }
ev(){ $T/b.sh eval "$B" "$1" 2>/dev/null; }
slot(){ ev "const w=bot.currentWindow; if(!w) return 'NOWIN'; const it=w.slots[$1]; if(!it) return 'EMPTY';
 const d=(it.nbt&&it.nbt.value&&it.nbt.value.display)?it.nbt.value.display.value:{}; const nm=d.Name?d.Name.value:'';
 const lo=d.Lore?d.Lore.value.value:[]; return (String(w.title)+' || '+nm+' || '+lo.join(' | ')+' || x'+it.count).replace(/§./g,'');"; }
click(){ ev "const w=bot.currentWindow; if(!w) return 'NOWIN'; const n=bot.chatLog.length; try { await bot.clickWindow($1,0,${2:-0}); } catch(e) {} await wait(${3:-2200});
 return bot.chatLog.slice(n).map(s=>s.replace(/§./g,'')).join(' / ');"; }
chatsince(){ ev "const n=bot.chatLog.length; bot.chat('$1'); await wait(${2:-1800}); return bot.chatLog.slice(n).map(s=>s.replace(/§./g,'')).join(' / ');"; }
coin(){ $C play "papi parse $B CN=%corerpg_coin%" 0.8 | tr -d '\r' | grep -oE 'CN=[0-9]+' | tail -1 | cut -d= -f2; }
openhub(){ ev "if(bot.currentWindow) bot.closeWindow(bot.currentWindow); await wait(300); bot.chat('/ember'); await wait(2200); return 1" >/dev/null; }
joinbot(){ curl -sf -m 90 "$BOTD/join?name=$B" >/dev/null || { bad "join"; exit 1; }; sleep 7; }

curl -sf "$BOTD/list" >/dev/null || { echo "FAIL botd"; exit 1; }
curl -sf "$BOTD/quit?name=$B" >/dev/null 2>&1 || true; sleep 1
joinbot
$C play "list" 0.5 | tr -d '\r' | grep -q "$B" && ok "$B online" || { bad "$B online"; exit 1; }
H=$(ev "return bot.chatLog.map(s=>s.replace(/§./g,'')).filter(s=>s.includes('[签到]')).join(' / ')"); echo "$H" | cut -c1-200
echo "$H" | grep -q '今天还没签到' && ok "join hint (not signed)" || bad "join hint ($H)"

C0=$(coin); echo "coin before $C0"
# 1) hub icon → page
openhub
S=$(slot 27); echo "$S" | cut -c1-200
echo "$S" | grep -q '签到 · 在线' && ok "hub icon 签到 · 在线" || bad "hub icon ($S)"
click 27 0 2600 >/dev/null
S=$(slot 10); echo "$S" | cut -c1-200
echo "$S" | grep -q '第 1 次 · 今天可签' && echo "$S" | grep -q '余烬币 20' && ok "cell 1 claimable + reward from config" || bad "cell 1 ($S)"
S=$(slot 16); echo "$S" | grep -q '首领徽记' && ok "cell 7 shows insignia" || bad "cell 7 ($S)"
R=$(click 10 0 2600); echo "$R" | cut -c1-200
echo "$R" | grep -q '签到成功 · 本月第 1 次' && ok "click → signed n=1" || bad "sign click ($R)"
C1=$(coin); echo "coin after $C1"
[ -n "$C1" ] && [ -n "$C0" ] && [ $((C1-C0)) -eq 20 ] && ok "+20 coin" || bad "coin delta ($C0 → $C1)"
openhub; click 27 0 2600 >/dev/null
S=$(slot 10); echo "$S" | grep -q '已领' && ok "cell 1 now 已领" || bad "cell 1 after ($S)"
R=$(click 10 0 1800); echo "$R" | grep -qv '签到成功' && ok "second click: no second sign" || bad "double ($R)"
R=$(chatsince '/corerpg p1 sign claim'); echo "$R" | grep -q '今天已经签到' && ok "command again: already signed" || bad "again ($R)"
# 2) make-up refused before 60 counted minutes
S=$(slot 46); echo "$S" | cut -c1-160; echo "$S" | grep -q '分钟后可补签' && ok "make-up shows 60-min rule" || bad "makeup icon ($S)"
# 3) reconnect → still signed, coin unchanged
ev "if(bot.currentWindow) bot.closeWindow(bot.currentWindow); return 1" >/dev/null
curl -sf "$BOTD/quit?name=$B" >/dev/null 2>&1; sleep 3; joinbot
R=$(chatsince '/corerpg p1 sign claim'); echo "$R" | grep -q '今天已经签到' && ok "after reconnect: already signed" || bad "reconnect ($R)"
C2=$(coin); [ "$C2" = "$C1" ] && ok "coin unchanged after reconnect" || bad "coin after reconnect ($C1 → $C2)"
# 4) online: +60 counted min (admin test hook), claim by click
$C play "corerpg p1 online test $B 60" 0.6 >/dev/null
openhub; click 27 0 2600 >/dev/null
S=$(slot 48); echo "$S" | grep -q '15 分钟 · 可领取' && ok "15-min tier claimable" || bad "15 ($S)"
S=$(slot 51); echo "$S" | grep -qv '可领取' && ok "120-min tier not yet" || bad "120 ($S)"
R=$(click 48 0 2400); echo "$R" | grep -q '15 分钟：余烬币 10' && ok "claim 15 by click" || bad "claim 15 ($R)"
R=$(click 48 0 1800); echo "$R" | grep -qv '15 分钟：余烬币' && ok "15 not twice" || bad "15 twice ($R)"
R=$(click 7 0 2600); echo "$R" | grep -q '30 分钟' && echo "$R" | grep -q '60 分钟' && ok "claim-all 30 + 60" || bad "claim all ($R)"
# 5) make-up by click (today signed + 60 min; Oct 1–3 missed)
S=$(slot 46); echo "$S" | grep -q '可补签' && ok "make-up available" || bad "makeup avail ($S)"
R=$(click 46 0 2600); echo "$R" | cut -c1-200; echo "$R" | grep -q '补签 .* 1 日 · 本月第 2 次' && ok "make-up fills the 1st as n=2" || bad "makeup ($R)"
R=$(click 46 0 1800); echo "$R" | grep -qv '本月第 3 次' && ok "make-up once a day" || bad "makeup twice ($R)"
# 6) reconnect → no re-claim of online tiers
ev "if(bot.currentWindow) bot.closeWindow(bot.currentWindow); return 1" >/dev/null
curl -sf "$BOTD/quit?name=$B" >/dev/null 2>&1; sleep 3; joinbot
R=$(chatsince '/corerpg p1 online claim'); echo "$R" | grep -qE '还没到|都领完' && ok "after reconnect: nothing to re-claim" || bad "online reconnect ($R)"
# 7) legacy commands pay no coin
C3=$(coin)
chatsince '/corerpg sign' >/dev/null; chatsince '/corerpg activity claim' >/dev/null; chatsince '/corerpg bounty claim' >/dev/null
ev "if(bot.currentWindow) bot.closeWindow(bot.currentWindow); return 1" >/dev/null
C4=$(coin); [ "$C3" = "$C4" ] && ok "legacy sign/activity/bounty: coin unchanged ($C4)" || bad "legacy paid ($C3 → $C4)"
grep -a "\[P1 sign\] $B sign .*n=1" "$LOG" | tail -1 | grep -q . && ok "log: sign n=1" || bad "log sign"
grep -a "\[P1 delivery\]\|P1 run\] undecodable" "$LOG" | grep -a "$B" | grep -qi "undecodable" && bad "undecodable ledger row" || ok "no undecodable rows"
curl -sf "$BOTD/quit?name=$B" >/dev/null 2>&1 || true
echo "RESULT $PASS pass / $FAIL fail"
