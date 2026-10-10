# STATUS · P1 孔石 tip · 2026-10-11

STATUS=**tip · 待批 A·M · 文档 only** · CST 2026-10-11  
TRIGGER=玩家「宝石要想办法弄了」  
RESEARCH=`docs/design/RESEARCH-ember-gem-reference-2026-10-11.md`  
DESIGN=`docs/design/DESIGN-ember-p1-socket-gems-2026-10-11.md`  
COORD=`/workspace/COORD-p1-socket-gems-2026-10-11.txt`

## 摘要

- 旧四石仍在 NI + `enhance.yml`，但 **legacy_gate 拒 socket**，且 `ember_v1_*` 不在旧孔表；P1 战斗不吃 StatService 孔属性。
- 提案：孔进 `EmberItemData` → flat A/D 进公式（帽）→ 吸血/移速受控层 → 再掉落/UI。
- **不做：** 复开旧 `/corerpg socket` 给 P1 装；AP lore 伤乘；新永久伤乘。

## 下一步

1. 批 A·M（或改数值/版本策略）  
2. 再开 Stage1 数据模型（无战斗）  
3. Stage2 前跑 p1sim

## 禁

无 K3 / 无 AFK 表抬 / 无 sx20 / 本 tip **不施工 jar**
