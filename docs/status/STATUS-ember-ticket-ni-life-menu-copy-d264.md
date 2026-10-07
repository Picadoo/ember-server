# 状态 · D264：旧票 NI lore 生活菜单按钮名假指引

**日期：** 2026-10-07（上海时间）  
**上游：** D263（`88d90e1c`）  
**范围：** 仅 `plugins/NeigeItems/Items/ember-dungeon-tickets.yml` 五行兑换指引

## 债证（修前）

| 位置 | 原文 | 为何假 |
|---|---|---|
| L10/22/34/46/58（日/周/深渊/团/精英票） | `生活菜单「旧票兑换」` | 生活菜单实际按钮名是 `旧票 → 余烬体力`（`ember_life.yml` L125）；玩家悬停旧票按「旧票兑换」找不到对应格 |

## 改后

五处统一为：`生活菜单「旧票 → 余烬体力」`  
零改兑换数值 / NI id / 命令。

## 验收

- rg：`ember-dungeon-tickets.yml` 无「旧票兑换」
- NI 热更：`neigeitems reload`（或等价）；未重启 play

## 旁扫未并入

- `crystal_ember_enchant` 等炉/渔 NI lore 首行仍印英文 id（管理噪音，非假指引句）
- hub「每次 30 体力」写死（与现行 cash 一致，非假）
