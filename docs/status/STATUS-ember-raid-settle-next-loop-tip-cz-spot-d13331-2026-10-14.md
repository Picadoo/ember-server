# 状态 · TIP-CZ 团本结算「下一环」菜单化抽测

**裁决：** **PASS** · feat `166586585` · menu `f7a3b824c` · tip/批 `2e12d099e` · jar `1.65.824-d13331.local` · 证据 `/workspace/tmp/tip-cz-raid-settle/`  
**范围：** 周清后下一环主钮开 `ember_p1_raid_settle`（满/未满皆进）· 薄页再进 E / 路径 1–3 / 冒险 A / 工坊 F / 招募 R / 清路径 C 真开 · 灰态诚实（周满/未选/体力）· PAPI `raid_settle_*` / `raid_path*` 诚实 · 主 UI 不教手打 `/corerpg p1 raidpath|recruit|enter` · **零改** weeklyCap/loot/体力价 · **零碰** 短征/variety/soft-reward · tip **勿勾 CLOSED**（总控收口）

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | 周清后下一环主钮开 `ember_p1_raid_settle`（满/未满皆进） | **PASS** | 源码 `afterWeeklyClear` 两态皆 `SETTLE_NEXT_LOOP_CMD`（unit 5/0 · `12-unit-*`）· live OpenCommand 无路径/有路径/再开皆进薄页 · `30/32/57` · `V1_both_states_enter_thin` |
| 2 | 薄页：再进 E / 路径 1–3 / 冒险 A / 工坊 F / 招募 R / 清路径 C 真开；灰态诚实 | **PASS** | E 点进发 enter（solo 拒人数 3～5 诚实）· R01/R02/R03 设焦点 · C 清空 · A→冒险 · F→工坊 · R→招募挂牌 · 未选灰 E/R/C · 周满灰 YAML `week_left<=0` · 体力灰 YAML `stamina_blocked_raid` · `31–46` · `56/57` · `10-menu-static` |
| 3 | PAPI `raid_settle_*` / `raid_path*` 诚实；主 UI 不教手打 | **PASS** | path=1→r01 / label R01 锈轨 · week_line 0/3 · week_left=3 · reenter_grey=0 · glance「点下一环选路径」· ConfirmTokens 主钮 `/ember_p1_raid_settle` · 无教裸 raidpath|recruit · `50-papi` · `58-no-teach` · `V4_no_teach` |
| 4 | 零改 weeklyCap/loot/体力价；零碰短征/variety/soft-reward | **PASS** | live weekly_cap=3 · cost=50 · short=5 · event_rate=0.85 · event_core=1 · affix_shard=2 · bv123 · short_settle md5 未变 · SOFT_TARGET=3 · feat 零 yml/Cast/Aid/soft · `00-feat-zero-raise` · `70-intact` · `90-endstate` |

## 旁注

- Stage2 三开 · daily_kills=2400 · ×0.97 · gate_daily=no · TrMenu **80**（含 `ember_p1_raid_settle`）· jar sha256 `79488db276ca…` plugins=server-runtime
- 单测 `EmberRaidSettleNextLoopTest` 5/0 · `EmberRaidPathTest` 1/0
- 35/35 PASS · tip **未勾 CLOSED**（总控收口）
- 历史 WE/raid 壳 PARTIAL 不挡绿；本票必测项真绿
- 满周 live 灰：本测周 0/3 未刷满；满态入口由 unit 两态常量 + YAML `week_left<=0` 灰态 + 薄页可再开覆盖（同 TIP-CW 日满灰范式）
- rescored：`V4_feat_zero` 误匹配 unit `assertFalse(weeklyCap=4)`；`V5_soft` 常量在 `EmberShortDayDivChase`（非 SoftClaim 字面）

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · weekly_cap=3 · raid cost=50 · short target=5 · SOFT_TARGET=3 · ×0.97 未改 · bv123 · 零改 weeklyCap/loot/体力价/短征薄页/variety/soft-reward/Cast/Aid · 零 sx20 · 未改玩法数值 · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB

> **测岗抽测：** PASS live · feat `166586585` · menu `f7a3b824c` · tip `2e12d099e` · jar `1.65.824-d13331.local` · **勿勾 CLOSED** · 2026-10-14 08:54 Asia/Shanghai
