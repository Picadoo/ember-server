# 状态 · TIP-DG 团本路径选打菜单化抽测

**裁决：** **PASS** · feat `c81c9e7e2` · menu tip `b9b8f688f` · tip/批 `fd80fd696` · jar `1.65.831-d13338.local` · 证据 `/workspace/tmp/tip-dg-raid-path/`  
**范围：** offerPick / 无参 raidpath / 冒险 Open → 开 `ember_p1_raid_path`（**禁**借 `ember_p1_raid_settle` ConfirmTokens 当选打主 UI）· 薄页 R01锈轨/R02霜封/R03断塔/清空/团本大厅/冒险/工坊真开 · applyAndReply 下一步走选打薄页 · PAPI `raid_path_*` lore 诚实 · Q07 灰态+亮态 · **零改** `ember_p1_raid_settle` / `ember_p1_featured_path` / `ember_p1_challenge_path` · **零抬**掉落/体力价/周帽 · **勿勾 tip CLOSED**（总控收口）

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | offerPick / 无参 raidpath / 冒险 Open → 开 `/ember_p1_raid_path`（禁借 settle ConfirmTokens） | **PASS** | `offerPick`→`openPathPick` · 无参 raidpath live 开「团本 · 路径」· adventure Open 接线 `corerpg p1 raidpath` · glance/cue「点团本路径页」· 无教裸 raidpath · 无借 settle · `12-unit-path-pick*` · `32-offer-noarg` · `33-adv-*` · `58-no-teach` |
| 2 | 薄页真开：R01锈轨/R02霜封/R03断塔/清空/冒险/可选大厅·工坊 | **PASS** | 1→r01 · 2→r02 · 3→r03 · C clear · S→团本大厅 · A→冒险 · F→工坊 · live 点击设路径/开菜单 · `10-menu-static` · `40–47` |
| 3 | applyAndReply 下一步走选打薄页；PAPI `raid_path_*`；Q07 灰态 | **PASS** | apply 下一步 `[团本路径][进 R01][工坊]` · path/label/tip/line 诚实 · Q07 锁→R01/R02/R03灰「需首通 Q07」· 全通亮态「R01 锈轨 · 已选」· `20/21/30` · `41-apply-next` · `V3_q07_gray` · `57-bright-reopen` |
| 4 | 零改 raid_settle / featured_path / challenge_path；零抬掉落/体力价/周帽 | **PASS** | raid_settle md5=`f57f9b69f6716bdaa81f1ce17c1696eb`（TIP-CZ 基线）未变 · featured_path md5=`7177b843b24d18b8dc9c7681e29718a6`（TIP-DE 基线）未变 · challenge_path md5=`6a141307446470e13ab8d43f17ba9256`（TIP-DF 基线）未变 · weekly_cap=3 · raid cost=50 · top=30 · rot_cap=3 · short=5 · event_rate=0.85 · event_core=1 · affix_shard=2 · bv123 · 短征/挑战/深渊/图身份 settle md5 未变 · SOFT_TARGET=3 · feat 零 yml/settle/feat_path/chal_path/soft · `00-feat-zero-raise` · `70-intact` · `90-endstate` |

## 旁注

- Stage2 三开 · daily_kills=2400 · ×0.97 · gate_daily=no · TrMenu **87**（含 `ember_p1_raid_path`）· jar sha256 `2e4eab9ab641…` plugins=server-runtime · MySQL connected · bv123
- 单测 `EmberRaidPathPickTest`（6）/ `EmberRaidPathTest`（1）/ settle* 绿
- 38/38 PASS · tip **未勾 CLOSED**（总控收口）
- 选打主 UI 确认为 `ember_p1_raid_path`，未借 `ember_p1_raid_settle`

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · weekly_cap=3 · rot weekly_cap=3 · raid cost=50 · short target=5 · top cost=30 · SOFT_TARGET=3 · ×0.97 未改 · bv123 · 零改掉落/体力价/周帽/raid_settle/featured_path/challenge_path/短征·挑战·深渊·图身份 settle/variety/soft-reward · 零 sx20 · 未改玩法数值 · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB

> **测岗抽测：** PASS live · feat `c81c9e7e2` · menu tip `b9b8f688f` · tip `fd80fd696` · jar `1.65.831-d13338.local` · **勿勾 CLOSED** · 2026-10-14 11:25 Asia/Shanghai
