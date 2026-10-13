# STATUS · TIP-CQ W1a 挂机庭「顺手洗练」afk 返回源 · D13322

> 2026-10-14 02:46 Asia/Shanghai · 余烬-插件岗 · **勿勾 CLOSED**（等 live + 物品 W1b/c）

## 交付
- `REROLL_FROM.put("afk", "ember_p1_afk")`（对齐 gear/hub/forge）
- 洗练页 `reroll_back` 文案：`from afk` → `§7返回挂机庭`（对齐 forge→工坊）
- `/corerpg p1 reroll from afk` → 开既有 `ember_p1_reroll`；`back` → `ember_p1_afk`
- 单测：`EmberGrowthTest.rerollFromAfk_mapsToAfkMenu_D13322`
- jar：`1.65.814-d13322.local` · sha256 `fe7bb5ac8e3b…` · ECONOMY_BV **123** 未动
- play：live `1.65.814-d13322.local` · storage=mysql · bv123 · MySQL connected
- **零改** reroll 价/pity/pool · afk.tiers · daily_kills · Cast/Aid/variety · bv123
- **未做** 菜单 W1b/c（物品岗并行）

## 落点
| 文件 | 改动 |
|------|------|
| `EmberGrowthService` | REROLL_FROM + afk；`rerollPapi back` 返回挂机庭 |
| `EmberGrowthTest` | afk→ember_p1_afk 映射单测 |
| `pom.xml` / `plugin.yml` | 1.65.814-d13322.local |

## 零改边界
- reroll 价 / pity / pool / lock / soft
- afk.tiers / daily_kills / Cast / Aid / variety / todayRot
- balance_version 123 · sx20 · 第五花料 · 推荐花复读
- TrMenu（W1b/c 另派）

## 下步
- 物品 W1b/c 已交 `6804ed53a` · 测岗已 PASS（见 tip-cq-spot / 总控收口）

## 测岗抽测（TIP-CQ spot）
- **PASS** · 2026-10-14 02:53 Asia/Shanghai · 证据 `/workspace/tmp/tip-cq-afk-reroll/` · 详 `STATUS-ember-afk-courtyard-quick-reroll-tip-cq-spot-d13322-2026-10-14.md`
- live jar `1.65.814-d13322.local` · L→reroll · 返回挂机庭 · 四花+P+D+W/w/S 仍在
