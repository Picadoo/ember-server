#!/usr/bin/env bash
# d219-backstep-smoke.sh — CoreRpg 1.65.49 D219 后撤步 / 身法方向
# FreshQ780: hub kit + dir toggle 前冲/后撤 + flex cast names + CD fill
# FreshQ781: Q05+焚烬 → 火痕步 / 火痕·后撤 toggle
# FreshQ782: Q01 first-clear regression
set -uo pipefail
C=/workspace/minecraft/scripts/console.sh; BOTD=http://127.0.0.1:8765; T=/workspace/minecraft/mineflayer-tests/tmp-p1
LOG=/workspace/minecraft/server-runtime/logs/latest.log
PASS=0; FAIL=0
ok(){ echo "PASS $1"; PASS=$((PASS+1)); }; bad(){ echo "FAIL $1"; FAIL=$((FAIL+1)); }
curl -sf "$BOTD/list" >/dev/null || { echo "FAIL botd"; exit 1; }
ph(){ $C play "papi parse $1 %corerpg_$2%" 0.5 2>/dev/null | tr -d '\r' | sed 's/§.//g' | grep -v '^\s*$' | tail -1 | sed 's/^.*INFO\]: //'; }
expect(){ local B=$1 K=$2 W=$3 V; V=$(ph "$B" "$K"); echo "  $B $K = $V"; [[ "$V" == *"$W"* ]] && ok "$B $K~$W" || bad "$B $K~$W (got: $V)"; }
ev(){ $T/b.sh eval "$1" "$2" 2>/dev/null; }
say(){ local B=$1; shift; ev "$B" "const n=bot.chatLog.length; bot.chat('$1'); await wait(${2:-2000}); return bot.chatLog.slice(n).map(s=>s.replace(/§./g,'')).join(' / ');"; }
weaken_n(){ local W; W=$($C play "corerpg p1 runs weaken $1 ${2:-}" 0.4 2>/dev/null | tr -d '\r' || true)
  if echo "$W" | grep -q '没有进行中'; then echo -1; return; fi
  local N; N=$(echo "$W" | sed -n 's/.*削弱 \([0-9]*\) 只.*/\1/p' | tail -1); echo "${N:--2}"; }
clear_room(){ local B=$1; for k in $(seq 1 24); do N=$(weaken_n "$B"); [[ "$N" == 0 ]] && return 0; [[ "$N" == -1 ]] && return 1
  $C play "corerpg p1 heal $B" 0.12 >/dev/null || true; $T/fight.sh "$B" 12000 0 32 2500 >/dev/null 2>&1 || true; done; [[ "$(weaken_n "$B")" == 0 ]]; }
settle(){ local B=$1; for i in $(seq 1 30); do N=$(weaken_n "$B"); [[ "$N" == -1 ]] && break; $C play "corerpg p1 heal $B" 0.12 >/dev/null || true; $T/fight.sh "$B" 6000 0 40 1500 >/dev/null 2>&1 || true; sleep 1; done; }
join(){ local B=$1; curl -sf "$BOTD/quit?name=$B" >/dev/null 2>&1 || true; sleep 1
  curl -sf -m 90 "$BOTD/join?name=$B" >/dev/null || { bad "$B join"; return 1; }; sleep 7
  $C play "list" 0.5 | tr -d '\r' | grep -q "$B" && ok "$B online" || { bad "$B online"; return 1; }
  $C play "corerpg stamina set $B 200" 0.5 >/dev/null; }
run_q01(){ local B=$1; $C play "corerpg p1 runs extra none" 0.4 >/dev/null || true; $C play "corerpg p1 runs variety clear" 0.4 >/dev/null || true
  $T/b.sh chat "$B" "/corerpg p1 enter q01" 6000 >/dev/null || true; sleep 6
  $C play "gamemode creative $B" 0.4 >/dev/null
  local si=0; for spot in "0 64 29" "30 64 39" "32 64 69"; do si=$((si+1)); $C play "tp $B $spot" 0.4 >/dev/null; sleep 2; clear_room "$B" && ok "$B room$si" || bad "$B room$si"; done
  $C play "tp $B 0 64 96" 0.4 >/dev/null; sleep 4; settle "$B"; sleep 4; }
quitall(){ for b in "$@"; do curl -sf "$BOTD/quit?name=$b" >/dev/null 2>&1 || true; done; sleep 2; }
equip_scorch(){ local B=$1
  $C play "corerpg p1 give scorch blade 2 0 0 4 $B" 0.8 >/dev/null
  $C play "corerpg p1 give scorch charm 2 0 0 4 $B" 0.8 >/dev/null; sleep 1
  ev "$B" "for(const k of ['blade','charm']){ let c=-1; for(let i=9;i<=44;i++){const x=bot.inventory.slots[i]; if(x&&JSON.stringify(x.nbt||{}).includes('ember_v1_scorch_'+k+'_t2')) c=i;}
   const to=k==='blade'?36:37; if(c>=0&&c!==to){ await bot.moveSlotItem(c,to); await wait(400);} }
   bot.setQuickBarSlot(1); await wait(300); bot.chat('/corerpg p1 charm'); await wait(1200); bot.setQuickBarSlot(0); await wait(500); return 1" >/dev/null
}

grep -q "Enabling CoreRpg v1.65.49" "$LOG" && ok "CoreRpg 1.65.49" || bad "CoreRpg 1.65.49"
grep -q "\[CoreRpg\] \[storage\] MySQL connected" "$LOG" && ok "CoreRpg MySQL" || bad "CoreRpg MySQL"
grep -q "\[CoreGacha\] \[db\] MySQL connected" "$LOG" && ok "CoreGacha MySQL" || bad "CoreGacha MySQL"
SEVERE=$(grep -c 'SEVERE' "$LOG" 2>/dev/null || echo 0)
[[ "$SEVERE" == "0" ]] && ok "SEVERE 0" || bad "SEVERE=$SEVERE"

echo "======== FreshQ780 hub kit + dir toggle ========"
A=FreshQ780; join $A || exit 1
$C play "corerpg flex equip flex_ember_step $A" 0.5 >/dev/null || true
R=$(say "$A" "/corerpg skill kit" 2000); echo "kit: $R" | cut -c1-400
echo "$R" | grep -q "身法方向" && ok "$A kit mentions 身法方向" || bad "$A kit mentions 身法方向"
echo "$R" | grep -q "前冲" && ok "$A kit mentions 前冲" || bad "$A kit mentions 前冲"
expect $A kit_step_dir 前冲
expect $A kit_step 踏步
R=$(say "$A" "/corerpg skill dir back" 2000); echo "dir back: $R"
echo "$R" | grep -q "后撤" && ok "$A dir→后撤" || bad "$A dir→后撤 ($R)"
echo "$R" | grep -q "冷却转满" && ok "$A dir fills CD" || bad "$A dir fills CD ($R)"
expect $A kit_step_dir 后撤
expect $A kit_step 后撤步
# CD should block cast briefly
R=$(say "$A" "/corerpg flex cast" 2000); echo "cast on CD: $R"
echo "$R" | grep -qE "冷却|后撤" && ok "$A cast after dir (CD or 后撤)" || bad "$A cast after dir ($R)"
# wait CD then cast 后撤步
sleep 15
R=$(say "$A" "/corerpg flex cast" 2500); echo "cast back: $R"
echo "$R" | grep -q "后撤步" && ok "$A flex=后撤步" || bad "$A flex=后撤步 ($R)"
R=$(say "$A" "/corerpg skill dir forward" 2000); echo "dir fwd: $R"
expect $A kit_step_dir 前冲
expect $A kit_step 踏步
quitall $A

echo "======== FreshQ781 Q05+焚烬 → 火痕·后撤 ========"
B=FreshQ781; join $B || exit 1
for q in q01 q02 q03 q04 q05; do $C play "corerpg p1 runs firstclear $B $q" 0.4 >/dev/null; done
$C play "corerpg flex equip flex_ember_step $B" 0.5 >/dev/null || true
equip_scorch $B
sleep 1
expect $B kit_step_unlock yes
expect $B kit_step 火痕步
R=$(say "$B" "/corerpg skill dir back" 2000); echo "dir: $R"
expect $B kit_step 火痕·后撤
expect $B kit_step_dir 后撤
sleep 15
R=$(say "$B" "/corerpg flex cast" 2500); echo "cast: $R"
echo "$R" | grep -q "火痕·后撤" && ok "$B flex=火痕·后撤" || bad "$B flex=火痕·后撤 ($R)"
echo "$R" | grep -q "起跳点燃" && ok "$B tip 起跳点燃" || bad "$B tip 起跳点燃 ($R)"
R=$(say "$B" "/corerpg skill dir forward" 1500); echo "fwd: $R"
expect $B kit_step 火痕步
quitall $B

echo "======== FreshQ782 Q01 first-clear regression ========"
Cbot=FreshQ782; join $Cbot || exit 1
$C play "corerpg flex equip flex_ember_step $Cbot" 0.5 >/dev/null || true
run_q01 $Cbot
# settle message check
sleep 2
R=$($C play "corerpg p1 runs info $Cbot" 0.5 2>/dev/null | tr -d '\r' | tail -5)
echo "info: $R"
expect $Cbot kit_step_dir 前冲
quitall $Cbot

echo "======== SUMMARY PASS=$PASS FAIL=$FAIL ========"
[[ "$FAIL" -eq 0 ]] && exit 0 || exit 1
