# 状态 · D391：短征 sx01 键 + S40 经济（批 A·M · 键与发放钩）

**日期：** 2026-10-10（上海时间）  
**上游：** DESIGN [`DESIGN-ember-short-dungeon-p1-reachable-2026-10-10.md`](../design/DESIGN-ember-short-dungeon-p1-reachable-2026-10-10.md) · 总控批 A·M @`234dea63`  
**裁决：** **本号交** `short.sx01` 进本键 + S40 发放钩 + REG/Economy/source-map · **地图/MM/TrMenu 另号** · **未关观察 · 未开 K3 · 未动 ×0.97/set_bonus · 未抬 afk · 未放开 gate_daily**  
**版本：** tip **`9df1444f`** · runs `balance_version` **63** · economy SoT 仍 bv60（仅增 S40 金样，未抬 ECONOMY_BV）

## 人话

玩家面「今天能打的短本」先落了 **进本键** 和 **结算包**：`/corerpg p1 enter sx01`（需本人 Q01 首通、耗体力 30）。每天前 3 次有奖通关发币/碎片/骨尘；生涯首通另加一包；第 4 次起仍可进、**无结算包**。真地图 / Boss Cast / 菜单另派人。

## 交付

| 项 | 钉 |
|----|----|
| 键 | `ember-v1-runs.yml` → `short.sx01` · `p1 enter sx01` · cost **30** · requires **q01** · `daily_reward_cap` **3** · claim **`p1_sx01_day`** |
| 有奖通关 | 币 **80** + 碎片 **4** + 骨尘 **3** |
| 生涯首通 | 另加币 **200** + 碎片 **8** + 胚料 **1**（只挂在有奖通关上） |
| 日帽后 | 第 4+ 次：体力仍可扣 · **无结算包**（账本 `sx_practice`） |
| 经济 | **S40** · `EmberEconomy` + `ember-v1-economy.yml` + REG + `ember-source-map.yml` |
| 发放钩 | `EmberShortRules` / `EmberShortService.applyGrants` → 结算分支（boss 击杀时） |
| 单测 | `EmberShortRulesTest`（门闩 / 有奖 / 首通 / 日帽 / source 标签 / yml） |

## 改动文件

| 文件 | 改动 |
|------|------|
| `EmberShortRules.java` / `EmberShortService.java` | 新建 |
| `EmberRunMaps.java` | `short:` 解析 · `shortExpedition` · byKey/validate |
| `EmberEntryService.java` / `EmberSettleService.java` / `EmberRunService.java` | 进本门闩 / 结算分支 / 接线 |
| `EmberEconomy.java` / `ember-v1-economy.yml` | S40 金样 |
| `ember-v1-runs.yml`（resources + live 字节一致） | `short.sx01` · bv **63** |
| `EmberCounters.java` | `p1_sx01_day` |
| `ember-source-map.yml` / `REG-ember-source-sink-cap-…` | S40 + content sx01 |
| `EmberShortRulesTest` / `EmberEntryServiceTest` / `EmberEconomyTest` / `EmberSourceMapTest` | 覆盖 |

## 本号不做 / 另号

| 项 | 说明 |
|----|------|
| 地图 WE `ember_short_sx01` | 另号；`map_version: pending-map-ticket` |
| MM Boss Cast / DP 房间表 | 另号；DP id 预留 `EmberSx01`（可能需扩 `scope.world_prefixes`，因现前缀 `dungeon_EmberQ0`） |
| TrMenu `ember_p1_short` | 另号 |
| p1sim 薄模型 | **另号补模型**（STATUS 明示） |
| 装 play jar / 热更 | 本号可测离线；是否装服由总控另派 |

## 不动

`afk.tiers` / `daily_kills` · `gate_daily` / 旧日常 · 观察三开关 / bv62 六槽段 · ×0.97 / set_bonus · K3 · 主仓切分支 · 整文件盖 live `ember-v1.yml`

## 验收（本号）

| # | 项 | 结果 |
|---|----|------|
| V-key | `short.sx01` 可 `byKey` · enter 文案含 sx01 | 单测 + 代码 |
| V-S40 | 有奖/首通/帽后发放与设计钉一致 | `EmberShortRulesTest` |
| V-reg | S40 进 Economy / REG / source-map | 已交 |
| V-live | resources ↔ live runs/economy 字节一致 | D243 口径 |
| V2/V3 | 旧日常仍关 · 地图体感 | **另号**（本号未放开 gate、未交地图） |

