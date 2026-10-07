# 状态 · D293：炽愈 S2 施工（sustain_hp 1.12→1.00）

**日期：** 2026-10-07（上海时间）  
**上游：** 总控批 A · 2 槽模拟 [`STATUS-ember-sustain-s2-2slot-sim-2026-10-07.md`](STATUS-ember-sustain-s2-2slot-sim-2026-10-07.md) · CoreRpg **1.65.84** · `balance_version` **59**

## 人话

炽愈成套生命加成从 ×1.12 收到 ×1.00，对齐模拟窗 S2：主线相对另两套的通关优势收到约 +2.5 / +7.5 pp，团本 R01 仍不坏。

## 改了什么

| 处 | 改动 |
|---|---|
| `ember-v1.yml` 双路径 | `sustain_hp_mult: 1.12 → 1.00` |
| `ember-v1-runs.yml` 双路径 | `balance_version` **58 → 59**（注释 D293） |
| `EmberTables` / `EmberMode` | 书默认 / 缺键默认同步 1.00 |
| 单测 Loadout / Formula / SetEnvelope | 炽愈 H/EHP 期望随 1.00 |
| `ember_help` / `ember_p1_gear` | 套装说明「生命 ×1.12」→「×1.00」 |
| 版本 | **1.65.83 → 1.65.84** |

## 不动

- 六槽 · 天赋一排 · Pack · 其它套装常数（回复%、ICD、每 N 次等）
- 焚烬 / 烬爆 数值

## 验收

- `rg 'sustain_hp_mult:' plugins/CoreRpg/ember-v1.yml` → `1.00`
- `rg 'balance_version:' plugins/CoreRpg/ember-v1-runs.yml` → `59`
- play Enabling **1.65.84**；`trmenu reload`
- 单测 `EmberLoadoutTest` / `EmberFormulaTest` / `EmberSetEnvelopeTest` PASS
