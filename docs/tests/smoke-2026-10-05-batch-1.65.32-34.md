# 合批冒烟 · CoreRpg 1.65.32 / 1.65.33 / 1.65.34（2026-10-05 11:46–12:30 CST）

POLICY 01:53（业主：测试攒一堆后再测）。服务器：play PID **1527320**，CoreRpg **1.65.34**（11:41 Enabling，CoreRpg + CoreGacha MySQL connected，全程 SEVERE 0、无 Exception）。机器人 FreshQ700–712，结束后全部经 botd 退出（list=[]），`runs list` = 没有进行中的主线本。脚本：`tools/p1map/batch3234-smoke.sh`（PART=g/f/r）+ `b3234-whiff.sh`（团本落空破绽定点测，tools/ 本地）。routine-1145 跑完 G/F/R01 后在 R03 中途中断（脚本 `$5` 在 `set --` 后失效），routine-1216 修好脚本接着跑完 R03/R02。

## 结果

| 包 | 机器人 | 结果 | 关键证据（latest.log / 聊天）|
|---|---|---|---|
| 1.65.32 D194 半血招冷却重锚 · Q07 | FreshQ700（创造，不出手）| **PASS 7 / 0** | 两次「蓄力「炉心聚爆」」相隔 **25.3 s**（上批同项约 7 s）|
| 1.65.32 D194 · Q06 | FreshQ701（同上）| **PASS 7 / 0** | 两次「蓄力「霜潮汲取」」相隔 **25.0 s** |
| 1.65.34 D196 词缀 旋光 | FreshQ702（Q01 `arcane:r1`）| **PASS 12 / 0** | `r1 affix arcane on melee` → `arcane ccw hit=0` → `arcane cw hit=1`（方向交替、每次施放只判一次）→ `affix arcane done`；结算「余烬碎片」|
| 1.65.34 D196 词缀 火链 | FreshQ703（Q01 `firechain:r1`）| **PASS 10 / 0 · SOFT 1** | `affix firechain on melee` → `firechain link melee`（开房约 2 s）→ `affix firechain done`；结算正常。SOFT：只连了一次（被连的怪先死前精英已被削弱击杀，没看到重连），重连逻辑有单元测试覆盖 |
| 1.65.33 D195 团本破绽 · R01 冲撞撞墙 | FreshQ704–706（3 人队，创造；首领前 4 格临时石墙）| **PASS 9 / 0** | `wall stun 冲撞 1.5s`（12:11:05）+ 预警「让它撞上墙会晕」+「撞墙！」；12:11:09 正常结算 |
| 1.65.33 D195 · R03 斧刃横扫落空 | FreshQ707–709（3 人站首领前 2 格 → 收到「全员躲开它会踉跄」预警立刻全队移出 14 格）| **PASS 13 / 0** | 第 3 次施放 `whiff stun 斧刃横扫 0.5s`（12:26:57）+「落空！」；结算正常 |
| 1.65.33 D195 · R02 半血砸地落空 | FreshQ710–712（削到 45% 后同上）| **PASS 13 / 0** | 首次半血「砸地」即 `whiff stun 砸地 0.5s`（12:30:03）+「落空！」；结算正常 |

合计有效 **71 PASS / 0 FAIL / 1 SOFT**；全程 SEVERE 0。

## 说明

- 过程中出现过的 FAIL 都是**测试脚本问题**，不是游戏问题：① R01 结算判断最初看「实例是否已关」，实例要等几秒才关，日志里 12:11:09 已 `settle`，改成查 settle 行；② `rraid` 里 `set -- $A` 覆盖了第 5 个参数（招式名），R03 崩溃；③ 接管时复用已开的 R03 实例，房间早已清完，`clear_room` 判定失效；④ 连打 3 个团本后体力不足（需 50），R02 进不去。最后两项改用 `b3234-whiff.sh` 新号重跑。
- 创造模式机器人靠风筝躲招时没能稳定触发团本落空（招式确实上了「全员躲开」预警，但没有全员躲开），所以改为定点：站在首领前触发「有人在范围内」→ 一收到预警就把全队移出范围。这与 D192 在 Q01 的实测是同一段通用代码（`whiffs(armed, landed==0)`）。
- D194：两张图半血后两次门槛招间隔都 ≥ 25 s，上批观察到的「第二招只隔约 7 秒」已消失。
- persist-roundtrip：三包均未改资产路径，不需要。
- Stage C：D194 / D195（raidwin MAX_ABS_DPP 2.5）/ D196（affixpack5 Part A 0.8 / p2econ 3.0）的 p1sim 关卡见各自发布记录，within range。
- 未测试计数归零；下一包（1.65.35）开始新一批。
