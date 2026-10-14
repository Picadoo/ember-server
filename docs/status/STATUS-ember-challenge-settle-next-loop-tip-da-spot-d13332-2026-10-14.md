# 状态 · TIP-DA 挑战本结算「下一环」菜单化抽测

**裁决：** **PASS** · feat `3d218ee83` · menu `b56f20912` · tip/批 `d0f10a344` · jar `1.65.825-d13332.local` · 证据 `/workspace/tmp/tip-da-challenge-settle/`  
**范围：** afterSettle 主钮开 `ember_p1_challenge_settle` · 薄页再挑战 E / 选图 S / 路径 1–3 / 清 C / 冒险 A / 工坊 F / 图装 M 真开 · 灰态诚实（体力&lt;30、未选路径）· PAPI `challenge_settle_*` / `challenge_path*` 诚实 · 主 UI 不教手打 enter challenge/forge/mapfarm · **零改**挑战掉落/体力价/成色/日帽 · **零碰**短征·团本/variety/soft-reward · 深渊未动 · **勾毕 CLOSED live**（总控收口 · 2026-10-14 09:28 Asia/Shanghai）

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | afterSettle 主钮开 `ember_p1_challenge_settle` | **PASS** | 源码 `afterSettle` 有/无路径皆 `SETTLE_NEXT_LOOP_CMD` + D671 首通主钮同开薄页（unit 5/0）· live OpenCommand 无路径/有路径/再开皆进薄页 · challengepath 设路径后聊天主钮 `[下一环]` · `12-unit-*` · `30/32/57` · `retest.log` |
| 2 | 薄页：再挑战 E / 选图 S / 路径 1–3 / 清 C / 冒险 A / 工坊 F / 图装 M 真开；灰态诚实 | **PASS** | E 接线 `enter %settle_map% challenge` · S→`ember_p1_challenge` · A→冒险 · F→工坊 · prefer/easy/follow 设焦点 · C 清空 · M→mapfarm · 体力&lt;30 灰 iron_bars · 未选灰 C · `10-menu-static` · `31–47` · `56` |
| 3 | PAPI `challenge_settle_*` / `challenge_path*` 诚实；主 UI 不教手打 | **PASS** | path=1→偏挑战 · label 硬本·偏挑战 · settle_map 未结算时诚实空（remember 单测+源码）· glance「点下一环选路径」· ConfirmTokens `/ember_p1_challenge_settle` · 无教裸 enter challenge\|forge\|mapfarm · `50/51*` · `58-no-teach` · `V4_no_teach` |
| 4 | 零改挑战掉落/体力价/成色/日帽；零碰短征·团本/variety/soft-reward；深渊未动 | **PASS** | mainline default cost=30 · weekly_cap=3 · raid cost=50 · short=5 · event_rate=0.85 · event_core=1 · affix_shard=2 · bv123 · short/raid settle md5 未变 · SOFT_TARGET=3 · feat 零 yml/短征/团本/soft/深渊 · `00-feat-zero-raise` · `00-cost-reparse` · `70-intact` · `90-endstate` |

## 旁注

- Stage2 三开 · daily_kills=2400 · ×0.97 · gate_daily=no · TrMenu **81**（含 `ember_p1_challenge_settle`）· jar sha256 `7a3cb57896c2…` plugins=server-runtime
- 单测 `EmberChallengeSettleNextLoopTest` 5/0 · `EmberChallengeSettleTest` 4/0
- 38/38 PASS · tip **勾毕 CLOSED live**（总控收口）
- live 挑战通关 settle：本测 Q01 挑战清房未在时限内击杀首领（T3 刃握持/房门节奏 soft）；afterSettle 主钮由 unit 两态常量 + D671 + OpenCommand 薄页 + challengepath 直播 `[下一环]` 覆盖（同 TIP-CZ 周满灰范式）
- rescored：`V0_cost` 主线体力价为 runs 顶栏 `cost: 30`（maps.q01 继承，非块内字段）；`V1` 假阳性「图装下一环」已剔

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · weekly_cap=3 · raid cost=50 · short target=5 · mainline cost=30 · SOFT_TARGET=3 · ×0.97 未改 · bv123 · 零改挑战掉落/体力价/成色/日帽/短征薄页/团本薄页/variety/soft-reward/深渊 · 零 sx20 · 未改玩法数值 · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB

> **测岗抽测：** PASS live · feat `3d218ee83` · menu `b56f20912` · tip `d0f10a344` · jar `1.65.825-d13332.local` · **CLOSED live**（总控收口）· 2026-10-14 09:28 Asia/Shanghai
