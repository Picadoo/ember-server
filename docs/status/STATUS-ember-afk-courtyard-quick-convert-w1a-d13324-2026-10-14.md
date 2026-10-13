# STATUS · TIP-CS W1a 挂机庭「顺手转化」afk/forge 返回源 · D13324

> 2026-10-14 03:23 Asia/Shanghai · 余烬-插件岗 · **勿勾 CLOSED**（等 live + 物品 W1b/c/d）

## 交付
- `CONVERT_FROM.put("afk", "ember_p1_afk")` + `CONVERT_FROM.put("forge", "ember_p1_forge")`
- `/corerpg p1 convert from afk|forge` → 开既有 `ember_p1_convert`；`convert back` → 映射菜单
- `%corerpg_p1_convert_back%`：from afk → `§7返回挂机庭`；from forge → `§7返回工坊`；否则 `§7关闭`
- 单测：`EmberGrowthTest.convertFromAfkAndForge_mapsToMenus_D13324`
- jar：`1.65.816-d13324.local` · sha256 `18f4e64a0249…` · ECONOMY_BV **123** 未动
- play：live `1.65.816-d13324.local` · storage=mysql · bv123 · MySQL connected
- **零改** convert 周帽/价/族表 · afk.tiers · daily_kills · event_* · 伤表 · Cast/Aid/variety · 洗练/烙纹
- **未做** 菜单 W1b/c/d（物品岗并行）· 庭内宝石/随机锻

## 落点
| 文件 | 改动 |
|------|------|
| `EmberGrowthService` | CONVERT_FROM + CFROM；convert from/back；convertPapi back |
| `EmberRunPapi` | `convert_back` → GROWTH → convertPapi（不撞 convert_path） |
| `EmberGrowthTest` | afk/forge→菜单映射单测 |
| `pom.xml` / `plugin.yml` | 1.65.816-d13324.local |

## 零改边界
- EmberConvertRules WEEKLY_CAP / blanks / coins / QUALITY_CAP / 族表
- afk.tiers / daily_kills / Cast / Aid / variety / todayRot / REROLL_FROM / BRAND_FROM
- balance_version 123 · sx20 · 第五花料 · 推荐花复读 · 洗练/烙纹再拧
- TrMenu（W1b/c/d 另派）

## 下步
- 物品 W1b/c/d 并行 · 测岗抽测等 live

## 测岗抽测（TIP-CS spot）
- **PASS** · 2026-10-14 03:30 Asia/Shanghai · 证据 `/workspace/tmp/tip-cs-afk-convert/` · 详 `STATUS-ember-afk-courtyard-quick-convert-tip-cs-spot-d13324-2026-10-14.md`
- live jar `1.65.816-d13324.local` · C→convert · 返回挂机庭/工坊双路径 · EeUuRrQqPDWwLlGgSsCV 仍在

