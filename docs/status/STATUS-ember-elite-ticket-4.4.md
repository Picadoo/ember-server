# STATUS · 4.4 余烬精英票（物品岗）

- 时间：2026-09-27（Asia/Shanghai）
- 执行：余烬-物品
- 依据：`docs/design/design-stage4-elite-weekly.md` §5.1；总控派活 4.4
- 未改：MM / DP / Paper / CoreRpg；通关箱物料未新建（沿用现成 fragment/shard/bone_dust/stable_charm/crystal）

## 文件

`plugins/NeigeItems/Items/ember-dungeon-tickets.yml` → 新增 **`ticket_ember_elite`**

## 定义

| 项 | 值 |
|----|-----|
| NI ID | `ticket_ember_elite` |
| 显示名 | `&c余烬精英票` |
| material | PAPER（与日/周/深渊/团本票一致） |
| lore | 每周 1 张 · 进本即扣 · **不退还** · 持有上限 1 · **需余烬 Lv.40**（玩家可读，无管理命令） |

## 持有上限

NI **无**背包持有上限 YAML 键（与现有票一致）。物品层已在 lore 写明「持有上限 1」；周发 / 扣次 / 硬顶由插件岗接线。

## 给物（测试）

```
/ni give <玩家> ticket_ember_elite 1
/ni reload
```

## 请插件

周发 1、进本扣次、已通关本周拒进、Lv.40 软门 — 接 `ticket_ember_elite`。
