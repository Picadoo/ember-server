# 状态 · D303：周本/团本段感方案 M（W1a+W1b+W1c+W1d）

**日期：** 2026-10-08（上海时间）  
**上游：** 总控批 A · [`DESIGN-ember-weekly-raid-feel-diff-2026-10-08.md`](../design/DESIGN-ember-weekly-raid-feel-diff-2026-10-08.md) 方案 M（`ce3b0e5c`）  
**版本：** CoreRpg **1.65.93** · `balance_version` **60**（未抬）

## 人话

团本 R01–R03 机制已厚，入口与本间仍像同骨架换 Boss。本窗**零经济 / 零新 Pack**：入口名片+本间短签 + 进本/段真实机制揭示 + 周规则身份钉——不抬体力/掉率，不新开地图。

## 改了什么

| 窗 | 改动 |
|---|---|
| **W1a** | TrMenu `ember_p1_adventure.yml` 团本三格：状态行下加 `§d名片` + `§b本间`（R01 冲撞撞墙破绽 / 半血增援；R02 半血砸地破绽 / 半血转阶段；R03 烬核分摊 / 全队靠拢）；**未改** cost/cap/HP |
| **W1b** | 进本揭示 `本局：<名> · 名片：…`；房清软段 `本间：<label> 已清 · 三房推进`；首领厅名片钉；半血/增援按本钉 D195（R01 四角加怪 / R02 砸地接横扫）；烬核沿用既有分摊预警（≤1） |
| **W1c** | 「本周短目标」本周规则旁半行：`周规则＝精选挑战 remix · 团本不受影响` |
| **W1d** | 材料已铁/钻/金胸甲对齐三本；R03 金胸甲加 `shiny: true` |
| jar | `EmberRaidService` cardTag / enterReveal / roomClearCue / bossCue / halfHpCue；`EmberRunService` 房清/Boss 分支；`EmberRunDirector` 半血/增援；版本 **1.65.92 → 1.65.93** |
| 单测 | `EmberRaidServiceTest.enterReveal_matchesMenuCard` · `softSegmentCues_matchD195Anchors` |

## 不动

- Pack6 / 新团本·周本地图包 / 重铺 R01–R03  
- 体力 50 / weekly_cap 3 / cap_group / 掉落 / HP·伤  
- 方案 R Director（door_delay / event.after 分本）  
- 天赋 / 灰印 HOLD / 守招 / 事件 R/W / 调律 R / 深渊 R / 走廊 W2  
- 六槽 / 永久乘区  
- bv **60** · login/proxy 未停（仅 play 换 jar）

## 验收

- 静态：三本短签与 D195 / 房间 label 一致；单测 PASS  
- 冒烟（live · **不跑 p1sim**）：
  - 菜单静态：R01/R02/R03 名片+本间短签；本周短目标「周规则＝精选挑战 remix · 团本不受影响」；R03 shiny
  - R01 进本：`本局：锈轨矿道·团 · 名片：冲撞撞墙`
  - R02 进本：`本局：霜封哨所·团 · 名片：半血砸地`（补测，过 DP 5s 冷却）
  - R03 进本：`本局：断塔回廊·团 · 名片：烬核分摊`
  - 3 人 dungeon-team；进本揭示即 ≥1 本内段线索（房清/半血/首领厅钩已落 jar，冒烟未打满本）
- play Enabling **1.65.93**；`version CoreRpg` = 1.65.93；TrMenu reload 69 菜单  
- 回滚：`/workspace/backup/CoreRpg-1.65.92-pre-d303.jar` + 还原 `ember_p1_adventure.yml`

## 下一窗

- **R**（按本轻 Director）**后置**；仅总控另批 M+R 时开  
- **禁** Pack6 / 新团本图 / 抬体力掉率 / 六槽 / 天赋续跑 / 灰印续跑 / 事件 R/W / 调律 R / 深渊 R  
- **下一档硬债 tip（需策划）：** [`STATUS-ember-next-hard-debt-boss-telegraph-need-design-2026-10-08.md`](STATUS-ember-next-hard-debt-boss-telegraph-need-design-2026-10-08.md) · Boss 预警诚实 / 相位线索可读  
