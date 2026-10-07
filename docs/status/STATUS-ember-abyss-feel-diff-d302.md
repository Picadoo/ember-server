# 状态 · D302：深渊可感差异方案 M（W1a+W1b+W1c）

**日期：** 2026-10-07（上海时间）  
**上游：** 总控批 A · [`DESIGN-ember-abyss-feel-diff-2026-10-07.md`](../design/DESIGN-ember-abyss-feel-diff-2026-10-07.md) 方案 M（`15296141`）  
**版本：** CoreRpg **1.65.92** · `balance_version` **60**（未抬）

## 人话

深渊十层仍像编号楼梯。本窗**零经济 / 零新 Pack**：菜单软三段签 + 进层揭示抽中主线图的 D300 节奏短签——不抬体力/层费/掉率，不新开地图。

## 改了什么

| 窗 | 改动 |
|---|---|
| **W1a** | TrMenu `ember_p1_abyss.yml`：L1–3 **§a浅渊** · L4–7 **§e中渊** · L8–10 **§c深渊**；每格两行段门槛短提示（对齐现行 hp/dmg/quality/fee 叙事，**未改数值**）；I 格加浅/中/深总览两行 |
| **W1b** | 进层（`verifyEntry` 深渊分支）聊天短行：`本层地图：<图名> · 节奏：<D300 短签>`；短签由 `event.after` + 任一门 `door_delay>0` 推导，与冒险/挑战 lore 一致 |
| **W1c** | 材料收束三段：浅石剑 · 中金剑 · 深钻剑（L10 下界之星）+ 深段 `shiny: true` |
| jar | `EmberSessionService.rhythmTag` / `mapHasDoorDelay` / `abyssRhythmRevealText`；版本 **1.65.91 → 1.65.92** |
| 单测 | `EmberSessionServiceTest.abyssRhythmReveal_matchesD300Copy`（七图短签钉死） |

## 不动

- Pack6 / 新深渊地图包 / 重铺 10 层  
- 体力 30 / 层费表 / `fee_mark_coin` / 掉率·成色权重·ALTS / `event_rate`  
- 事件 R/W · 调律 R · 工坊 R · 走廊 W2 · 方案 R Director 旋钮  
- 六槽 / 天赋 / 守招邻域 / 永久乘区  
- bv **60** · login/proxy 未停（仅 play 换 jar）

## 验收

- 静态：十层段划分与 lore 一致；七图节奏短签单测 PASS（`abyssRhythmReveal_matchesD300Copy`）  
- 冒烟（live · **不跑 p1sim**）：
  - 菜单：浅渊 L1–3 / 中渊 L4–7 / 深渊 L8–10 段签名可见
  - L2 进层：`本层地图：锈轨矿道 · 节奏：事件居中 · 门慢半拍`
  - L5 进层：`本层地图：灰烬庭院 · 节奏：事件偏早 · 门慢半拍`
  - L9 进层：`本层地图：断塔回廊 · 节奏：事件偏早`
- play Enabling **1.65.92**；`version CoreRpg` = 1.65.92；TrMenu reload 69 菜单  
- 附：`/corerpg p1 runs abyssbest <玩家> <层>` 运维桩（设 `p2_abyss_best`，冒烟用）  
- 回滚：`/workspace/backup/CoreRpg-1.65.91-pre-d302.jar` + 还原 `ember_p1_abyss.yml`

## 下一窗

- **R**（按段轻 Director）**后置**；仅总控另批 M+R 时开  
- **禁** Pack6 / 新深渊图 / 抬体力掉率 / 六槽 / 天赋续跑 / 事件 R/W / 调律 R  
