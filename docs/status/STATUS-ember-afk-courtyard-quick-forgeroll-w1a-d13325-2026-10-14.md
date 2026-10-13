# STATUS · TIP-CT W1a 挂机庭「顺手随机锻」afk/forge 返回源 · D13325

> 2026-10-14 03:40 Asia/Shanghai · 余烬-插件岗 · **勿勾 CLOSED**（等 live + 物品 W1b/c/d）

## 交付
- `FORGEROLL_FROM.put("afk", "ember_p1_afk")` + `FORGEROLL_FROM.put("forge", "ember_p1_forge")`
- `/corerpg p1 forgeroll from afk|forge` → 开既有 `ember_p1_forgeroll`；`forgeroll back` → 映射菜单；`menu`/空参 → 开页
- 既有 `<族> <blade|charm> <阶> [floor] [confirm]` **保留**（`cmdForgeRoll` 内 from/back/menu 先行）
- `%corerpg_p1_forgeroll_back%`：from afk → `§7返回挂机庭`；from forge → `§7返回工坊`；否则 `§7关闭`
- 单测：`EmberForgeRollRulesTest.forgerollFromAfkAndForge_mapsToMenus_D13325`
- jar：`1.65.817-d13325.local` · sha256 `0205fd183e49…` · ECONOMY_BV **123** 未动
- play：live `1.65.817-d13325.local` · storage=mysql · bv123 · MySQL connected
- **零改** 印记/胚/骨/币/floor 价 · QUALITY_W · densify/C22 · afk.tiers · daily_kills · event_* · Cast/Aid/variety · 洗练/烙纹/转化
- **未做** 菜单 W1b/c/d（物品岗并行）· 庭内宝石 · densify

## 落点
| 文件 | 改动 |
|------|------|
| `EmberRunService` | FORGEROLL_FROM + FFROM；forgeroll from/back/menu；forgerollPapi back |
| `EmberRunPapi` | `forgeroll_back` → LOADOUT → forgerollPapi |
| `EmberForgeRollRulesTest` | afk/forge→菜单映射单测 |
| `pom.xml` / `plugin.yml` | 1.65.817-d13325.local |

## 零改边界
- EmberForgeRollRules MARKS/BLANKS/COINS/QUALITY_W/floor*
- afk.tiers / daily_kills / Cast / Aid / variety / todayRot / REROLL/BRAND/CONVERT_FROM
- balance_version 123 · sx20 · 第五花料 · densify/C22
- TrMenu（W1b/c/d 另派）

## 下步
- 物品 W1b/c/d 并行 · 测岗抽测等 live
