# 合批冒烟 · CoreRpg 1.65.23 / 1.65.24 / 1.65.25（2026-10-05 04:23–04:59 CST）

POLICY 01:53（业主：测试攒一堆后再测）。服务器：play PID **1261974**，CoreRpg **1.65.25**（04:21:02 Enabling，CoreRpg + CoreGacha MySQL connected，SEVERE 0）。机器人 FreshQ400+，结束后全部经 botd 退出（list=[]）。

## 结果

| 包 | 脚本 | 机器人 | 结果 | 关键证据 |
|---|---|---|---|---|
| 1.65.23 D173 首领半血 Pack 2 | `tools/p1map/d173-boss-smoke.sh` | FreshQ420（Q01）· FreshQ431（Q07） | **PASS 20 / 0** | `q01-muuaag1k-j06 boss half-HP phase at 40%`；`q07-muuajw8u-ff3 boss half-HP phase at 40%`；全队提示「首领进入半血 · 招式变强」；三房清空 → 首领现身（Q07 boss hp 3120）|
| 1.65.24 D185 奖励精英变招 Pack 2 | `tools/p1map/d185-elite2-smoke.sh` | FreshQ422（Q01，`runs extra elite`）| **PASS 4 / 0** | 聊天：「额外事件：奖励精英「门廊推」/「灰烬扇」· …· 击败 → 碎片 +10 + 核心 +1」（双招名 + 两条提示，奖励 10+1 不变）|
| 1.65.25 D186 周规则 Pack 3 | `tools/p1map/d186-weekly3-smoke.sh` | FreshQ436 / 437 / 438（挑战版，`runs modifier`）| **PASS 12 / 0** | `r1 rule skirmish: ranged×4 (converted x4 hp*0.9 interval*0.85)`；Q03 `rule hexers: caster,caster,caster,heavy (converted x3 interval*1.1)`；Q01 `rule ballista: melee×4`（该房无重甲 → noop，正确）；开局文案「本周规则「散兵/咒潮/重弩」…（奖励不变）」|

合计 **36 PASS / 0 FAIL**（最终轮）；全程 SEVERE 0。

## 过程里修掉的测试脚本问题（非游戏 bug）

| 现象 | 原因 | 修正 |
|---|---|---|
| D173 首轮 Q01「phase-tell FAIL」| 旧脚本把房 1 的「通道已开启」当成后续房间已清，首领根本没刷 | 只认 `runs weaken` 返回「削弱 0 只」为清房 |
| D173 Q07「没有进行中的主线本」| 管理员 `runs firstclear` 不写解锁标记，进 Q07 被拒「未解锁」 | 先 `runs unlock` Q02–Q07 |
| D186 首轮无 forced 日志 | 挑战入口要本人首通 Q07 + T3 提醒确认 | firstclear 全部七图 + `enter … challenge force` |
| fight-kite 首轮未清房 | 同第一条（假清房）；kite 本身可用 | 本批用 `fight.sh`（站桩）+ weaken 计数 |

## Stage C

`python3 tools/p1sim/p2econ.py --mods --weeks 24`（bv48）→ `tools/p1sim/out-p2econ-mods-d186.md`：12 条规则精选挑战通关率差 **−0 ～ +1 点**（skirmish / hexers / ballista 各 120 玩家-周，0 点）；W12 经济列三方案同量级（余烬币中位 58300 / 58815 / 58770）。**within range**。

## 未跑

- persist-roundtrip：三包均无资产路径变化。
