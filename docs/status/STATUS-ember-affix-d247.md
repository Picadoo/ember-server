# STATUS · D247 p1sim 建模 5 个未覆盖词缀（离线，无发版）

**日期：** 2026-10-07 Asia/Shanghai · **上游：** main `35dfc5fb`（D246）/ CoreRpg **1.65.70** / bv**58**（不变）

## 做了什么

把 `affix-table.json` 里此前「导出但未建模」的 5 个词缀接入 `tools/p1sim`（ARCH S4「可选下一刀」）：

| 词缀 | 族 | sim 模型 |
|---|---|---|
| charge 冲锋 | STRIP | PERIODIC 预警打击（同炽热路径，读表 `first_hit_s` / `period_s` / `dmg_atk`） |
| mortar 投弹 | CIRCLE | 同上 |
| frost 凝霜 | AURA | 存活期间 dodge × `1/(1+FROST_DODGE_PEN·amp)`（`FROST_DODGE_PEN=0.05`；原版缓速只改移速，作轻量闪避时序代理） |
| regen 再生 | CHANNEL | 读条窗结束：窗内受伤 ≥ `interrupt_maxhp·max` 则打断，否则回 `heal_maxhp·max` |
| molten 亡爆 | DEATH_BLAST | 死后 `hit_after_death_s` 下一记预警圈；无坐标 → `MOLTEN_EXPOSURE=1/3` 才仍受威胁（其余视为已退开） |

门禁基线：affixpack5 / D247 门改用哨兵 **`_plain`**（晋升精英、无战斗压力）。旧基线 mortar 在未建模时等价于普通精英；D247 起 mortar 有圈伤，不能再当基线。

## Gate

| Gate | 结果 |
|---|---|
| selfcheck D247 7 条 | **PASS**（仓库原有 3 个已知 FAIL 不变：图录预警文案、gear6 `default_rng`、signin 直读） |
| `affix-d247.py --n 150`（42 格 ×5 新词缀 vs `_plain`，帽 3.0 pp） | **WITHIN RANGE** · MAX_ABS_DPP **2.9**（q07 书参考 dodge 0.5 · charge） |
| `affixpack5.py --n 150`（旋光/火链 vs `_plain`） | **WITHIN RANGE** · MAX_ABS_DPP **2.9** · 压力 −毒十字 **1.8** |
| W30 p2econ | **室门优先已过**；全量 300 人因本机墙钟延后。spot `n=40` 最早 **4.67** 周（D244 `n=300` 为 5.31；样本量不同，不据此宣称翻转） |

原始表：`tools/p1sim/out-affix-d247.md`。

## 不变

- **不改** CoreRpg / 线上词缀数值 / `variety` yml / `balance_version`（仍 58）/ 发版
- 服未停；无 jar 部署

## 下一刀

- Stage 1（6 槽护甲只有属性）仍待总控 / 服主**显式开工**（D246 前置已满足）
- 可选：把 D247 的 exposure / frost 代理换成有坐标的闪避模型（另开窗）
