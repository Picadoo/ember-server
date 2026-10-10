# 状态 · D447：主线 Q01–Q07 地图审计（对照短征 D443）

**裁决：** **已批 A · 方案 M · D447 · 审计 PASS · 无需重建** · 2026-10-10  
**对照：** D443 短征 sx01–13（当时模板 **MISSING** → 重建）  
**方法：** 磁盘 `plugins/DungeonPlus/map/` + `level.dat` generator + MCA 体量 + safe 点脚下实心块 + `ember-v1-runs.yml` boss `warn`

## 总表

| 本 | 模板目录 | gen | MCA≈ | safe 脚下实心 | boss warn min | 裁定 |
|----|----------|-----|------|---------------|---------------|------|
| q01 | `ember_daily_v1` | default | 2.0MB | ✅ 全点 | 1.0 | OK |
| q02 | `ember_daily_ash_v1` | default | 2.0MB | ✅ | **0.9**（横扫） | OK（Director ActionBar 仍可读；非无预警） |
| q03 | `ember_daily_crypt_v1` | default | 2.0MB | ✅（含 y56 高差） | 1.0 | OK |
| q04 | `ember_daily_tide_v1` | default | 1.2MB | ✅ | 1.0 | OK（体量偏薄但仍非空壳） |
| q05 | `ember_daily_spire_v1` | default | 1.1MB | ✅（含 y72/80） | 1.0 | OK |
| q06 | `ember_daily_frost_v1` | default | 1.1MB | ✅ | 1.2 | OK |
| q07 | `ember_daily_rail_v1` | default | 1.1MB | ✅ | 1.2 | OK |

## 与短征债差异

| | 短征 D443 | 主线 D447 |
|--|-----------|-----------|
| 磁盘 | sx01–13 **MISSING** | Q01–07 **齐全** |
| 壳/平 | 需 Anvil 重建 | 非 flat-void；safe 点有实心地板 |
| Cast | 需 MM message+delay30 | Director `warn` + D304 cast-start ActionBar 已有 |

## 不做（本号）

- **不**重建 worst 2–3（无 FAIL 项；重建有误伤通关动线风险）
- **不**改奖励表 / HP / dmg / Stage2 / AFK / K3
- **不**把 q02 warn 0.9 当紧急抬数值（可读性债可另 tip，非地图壳）

## 可选下债（非本号）

| 债 | 说明 |
|----|------|
| `B-mainline-q-map-visual-pass` | q04–q07 MCA 偏薄：视觉「庭院/塔/霜/轨」独立验收（对照 `design-ember-daily-maps-p4.md` 禁换皮）|
| `B-q02-sweep-warn-readability` | 横扫 warn 0.9→≥1.0 另 COORD（零经济意图需批）|

## Stage2

观察续至 ≥**17:40 CST**；绿出见 `STATUS-ember-stage2-green-exit-checklist-2026-10-10.md`。
