# STATUS · TIP-CZ W1a 团本结算「下一环」菜单化 · D13331

> 2026-10-14 Asia/Shanghai · 余烬-插件岗 · **CLOSED live** · PASS `64bfa4265` · tip `f7a3b824c` · 总控收口 2026-10-14 08:56 Asia/Shanghai

## 交付
- `EmberRaidLoop.afterWeeklyClear`：额度满/未满两态主钮 → `/ember_p1_raid_settle`（`EmberRaidSettle.SETTLE_NEXT_LOOP_*`）
- 辅钮：冒险 `/ember_p1_adventure` · 工坊 `/ember_p1_forge`（满额）；禁教手打 `raidpath`/`recruit` 主 UI
- `EmberRaidPath`：glance「点下一环选路径」；offerPick / applyAndReply / enterCue / afterClearHit 菜单优先（开薄页；可保留再进 `enter`）
- `EmberRaidSettle`：常量 + weekLeft/weekLine/reenterGrey/settleMap
- PAPI（薄页已依赖）：`%corerpg_p1_raid_settle%/_map/_name/_week_left/_week_line/_reenter_grey%` · `%corerpg_p1_raid_path%` / `_path_label%`（经 `EmberRaidService.papi`）
- 单测：`EmberRaidSettleNextLoopTest` 5/0 · `EmberRaidPathTest` 1/0
- jar：`1.65.824-d13331.local` · sha256 `79488db276ca…` · ECONOMY_BV **123** 未动
- play：live `1.65.824-d13331.local` · storage=mysql · bv123 · MySQL connected
- **零改** weeklyCap / loot / 体力价 / 团本数值 / Cast / Aid / variety / 短征薄页 / soft-reward / ShortPath / TrMenu yml
- **未做** 菜单 W1b（物品岗薄页已并行存在；本窗未改 yml）· **未勾** tip CLOSED

## 落点
- `EmberRaidSettle`（新）
- `EmberRaidLoop`：afterWeeklyClear 两态开薄页
- `EmberRaidPath`：glance / offerPick / applyAndReply / enterCue / afterClearHit
- `EmberRaidService`：papi settle_* / path*
- `pom.xml` / `plugin.yml`：1.65.824-d13331.local

## 零改边界
- weeklyCap / loot / 体力价 / 团本 HP·伤表 / Cast / Aid / variety yaml
- 短征薄页 / soft-reward / ShortPath · TrMenu yml
- balance_version 123 · sx20 · 挑战/深渊 settle

## 下步
- 物品 W1b / 测岗均已齐 · 总控收口 CLOSED

## 测岗抽测（TIP-CZ spot）
- **PASS** · 2026-10-14 08:56 Asia/Shanghai · 证据 `/workspace/tmp/tip-cz-raid-settle/` · 详 `STATUS-ember-raid-settle-next-loop-tip-cz-spot-d13331-2026-10-14.md`
- live jar `1.65.824-d13331.local` · feat `166586585` · tip `f7a3b824c` · 批 `2e12d099e` · 团本结算下一环菜单化全绿

## 续 · 测岗抽测 PASS（2026-10-14 Asia/Shanghai · 余烬-测试）
- PASS live · feat `166586585` · 证据 `/workspace/tmp/tip-cz-raid-settle/` · STATUS `64bfa4265`
- **CLOSED live**（总控收口）· tip `f7a3b824c` · jar `1.65.824-d13331.local`
