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
| W30 p2econ 300×12 | **D248 关账**：标题深渊 **5.11** 周（相对 D244 的 5.31 = −0.20）；六路线全在 ±0.5 内。见 `STATUS-ember-affix-d248.md` |

原始表：`tools/p1sim/out-affix-d247.md`。

## 不变

- **不改** CoreRpg / 线上词缀数值 / `variety` yml / `balance_version`（仍 58）/ 发版
- 服未停；无 jar 部署

## 下一刀

- ~~全量 W30~~ → **D248 关账**
- 六槽护甲施工仍**不开**（要等明确开工令）
- 下一薄窗候选：自检 3 条老红灯 / 文案注释
