# STATUS · B0.1 票务 NI 对齐审计（物品岗）

- 时间：2026-09-27（Asia/Shanghai）
- 执行：余烬-物品
- 依据：`docs/design-ember-content-backlog.md` B0.1；总控派活「票扣次绑显示名→NI id」
- 未改：票价/次数数值（日 3 / 周 1 / 深 1 / 团 1 / 精英 1）；Paper / MM；CoreRpg Java（交插件岗）

## 结论（一句话）

**给票/计数已走 NI ID；进本扣次仍绑 DP 显示名（原生限制）→ 标债务给插件岗。** 物品岗已去掉玩家可见的裸 `ticket_ember_*` lore/文案。

---

## 已对齐（走 NI ID / ItemManager）

| 区域 | 路径/点 | 说明 |
|------|---------|------|
| CoreRpg 发放 | `plugins/CoreRpg/cash.yml` | `ticket_ni_id: ticket_ember_*`（日/周/深/团/精英） |
| CoreRpg 邮件/进度/任务 | `mail.yml` / `progress.yml` / `quest.yml` | 奖励键为 NI id |
| CoreRpg 仓库/拍卖白名单 | `warehouse.yml` / `auction.yml` | NI id 列表 |
| CoreRpg 运行时 | `NiBridge` + `TicketGrantService` / `EliteService` | `countInInventory` / `give` 按 NI id；精英门控按 `ticket_ember_elite` |
| NI 定义 | `plugins/NeigeItems/Items/ember-dungeon-tickets.yml` | 五票 ID 稳定；**已删** lore 首行裸 ID |
| 通关给物 | DP `option.yml` `$command ni give …` | 已用 NI id（非扣票） |

同步副本：`server-runtime/plugins/...` 与 `plugins/...` 已对齐（cmp 一致）。

---

## 本次已改（物品岗）

| 文件 | 改动 |
|------|------|
| `plugins/NeigeItems/Items/ember-dungeon-tickets.yml`（+ server-runtime） | 去掉五票 lore 中玩家可见的 `&7ticket_ember_*` 行；注释标明 DP 债务 |
| `TrMenu/menus/ember_{hub,daily,weekly,abyss,raid,shop}.yml` | 玩家 lore/tell 去掉裸 `ticket_ember_*`，改中文显示名 |
| `DungeonPlus/dungeon/Ember{Daily,Weekly,Abyss,Raid,EliteWeekly}/option.yml` | **仅**清理无票提示文案中的裸 ID；**未改** `<item:显示名>` 扣次（不能） |

未改平衡数值。

---

## 未改债务（需插件岗 · CoreRpg）

| 点 | 现状 | 建议 |
|----|------|------|
| DP 进本扣次 | 五本 `option.yml` 仍 `$js-condition … <item:余烬*票 *1 *true>` | DP `<item:>` **只认去色显示名**，不能写 NI id。建议：CoreRpg 统一 `consumeExact(player, ticket_ni_id, 1)` 后再 `dp start`，并**移除** DP `<item:>` 条件（或改成只检等级/人数） |
| `EliteService.cmdStart` | 已按 NI id **检票**，但仍 `dp start` 交给 DP **再按显示名扣** | 同上一行：插件内 `consumeExact(TICKET_NI,1)` 后启动，DP 不再扣 |
| `corerpg ticket consume <id>` | 尚无统一消费命令 | backlog 建议项；日/周/深/团入口可复用 |
| TrMenu `ember_raid.yml` L94 | 管理向 lore 仍写 `/ni give … ticket_ember_raid` | 可留（管理/测试）；若要彻底清玩家面可再藏 |

债务文件内已有 DEBT 注释：`DungeonPlus/README-ember-dungeons.md`、各 `option.yml` 头、`ember-dungeon-tickets.yml` 头。

---

## 验收建议

```
/ni reload
/trmenu reload
/dp reload
```

1. `/ni give <玩家> ticket_ember_daily 1`（及 weekly/abyss/raid/elite）→ 物品 lore **无**裸 `ticket_ember_`  
2. 改显示名颜色后（未做）：五本扣票仍依赖显示名 → **预期仍脆**，直至插件改 NI consume  
3. 无票进本 → 拒绝文案说人话、无裸 ID  
4. 数值次数不变

## 请总控

把「DP 显示名扣次 → CoreRpg NiBridge consume + 去 `<item:>`」派给 **余烬-插件**；物品岗 B0.1 文案侧已交卷。
