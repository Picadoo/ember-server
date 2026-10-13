# STATUS · TIP-CR W1a 挂机庭「顺手烙纹」afk/forge 返回源 · D13323

> 2026-10-14 03:03 Asia/Shanghai · 余烬-插件岗 · **勿勾 CLOSED**（等 live + 物品 W1b/c/d）

## 交付
- `BRAND_FROM.put("afk", "ember_p1_afk")` + `BRAND_FROM.put("forge", "ember_p1_forge")`
- `/corerpg p1 brand from afk|forge` → 开既有 `ember_p1_brand`；`brand back` → 映射菜单
- `%corerpg_p1_brand_back%`：from afk → `§7返回挂机庭`；from forge → `§7返回工坊`；否则 `§7关闭`
- 单测：`EmberGrowthTest.brandFromAfkAndForge_mapsToMenus_D13323`
- jar：`1.65.815-d13323.local` · sha256 `e7de5a90f3a4…` · ECONOMY_BV **123** 未动
- play：live `1.65.815-d13323.local` · storage=mysql · bv123 · MySQL connected
- **零改** craftShards/定向价/次数 · afk.tiers · daily_kills · Cast/Aid/variety · bv123 · 洗练
- **未做** 庭内宝石 · 菜单 W1b/c/d（物品岗并行）

## 落点
| 文件 | 改动 |
|------|------|
| `EmberGrowthService` | BRAND_FROM + BFROM；brand from/back；brandPapi back |
| `EmberRunPapi` | `brand_back` → GROWTH → brandPapi |
| `EmberGrowthTest` | afk/forge→菜单映射单测 |
| `pom.xml` / `plugin.yml` | 1.65.815-d13323.local |

## 零改边界
- EmberBrandRules craftShards / pinBrands / pinCoins / MAX_CHARGES
- afk.tiers / daily_kills / Cast / Aid / variety / todayRot / REROLL_FROM
- balance_version 123 · sx20 · 第五花料 · 推荐花复读 · 洗练再拧
- TrMenu（W1b/c/d 另派）

## 下步
- 物品 W1b/c/d 已交 `ce2f8c95f` · 测岗已 PASS（见 tip-cr-spot / 总控收口）

## 测岗抽测（TIP-CR spot）
- **PASS** · 2026-10-14 03:11 Asia/Shanghai · 证据 `/workspace/tmp/tip-cr-afk-brand/` · 详 `STATUS-ember-afk-courtyard-quick-brand-tip-cr-spot-d13323-2026-10-14.md`
- live jar `1.65.815-d13323.local` · G→brand · 返回挂机庭/工坊双路径 · 四花+P+D+W/w/L/S 仍在

