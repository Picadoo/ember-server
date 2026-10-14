# 状态 · TIP-DB 深渊结算「下一环」菜单化抽测

**裁决：** **PASS** · feat `6e22b5d2f` · menu `1d575cf83` · tip/批 `8a3f93564` · jar `1.65.826-d13333.local` · 证据 `/workspace/tmp/tip-db-abyss-settle/`  
**范围：** 深渊 newBest 结算主钮开 `ember_p1_abyss_settle` · 薄页再深渊 E / 选层 S / 冒险 A / 工坊 F / 路径 1–2 / 层目标 T / 清路径 P / 清目标 C 真开 · 灰态诚实（体力&lt;30、can_next/未开层）· PAPI `abyss_settle_*` / `abyss_path*` 诚实 · 主 UI 不教手打 abysstarget/forge · `ember_p1_abyss` Open 不再 tell+command 教 abysstarget · **零改**深渊掉落/体力价/层表/日帽 · **零碰**短征·团本·挑战/variety/soft-reward · legacy evacuate 未动 · **勿勾 tip CLOSED**（总控收口）

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | newBest 结算主钮开 `ember_p1_abyss_settle` | **PASS** | 源码 D675 ConfirmTokens → `SETTLE_NEXT_LOOP_CMD`=/ember_p1_abyss_settle · remember settle 层 · unit 5/0 · live OpenCommand 无路径/有路径/再开皆进薄页 · `12-unit-*` · `30/32/57` · `36-settle` |
| 2 | 薄页：再深渊 E / 选层 S / 冒险 A / 工坊 F / 路径·目标薄格真开；灰态诚实 | **PASS** | E 接线 `abyss next` · S→`ember_p1_abyss` · A→冒险 · F→工坊 · push/farm 设路径 · T abysstarget next · P/C 清空 · can_next&lt;1 灰 dye「未开层/层费」· 体力&lt;30 灰 iron_bars（YAML priority）· `10-menu-static` · `31–47` · `56` |
| 3 | PAPI `abyss_settle_*` / `abyss_path*` 诚实；主 UI 不教手打 | **PASS** | path=1→冲层 · path=2→刷层 · settle_floor 未结算时诚实空 · glance「点下一环设目标」· ConfirmTokens `/ember_p1_abyss_settle` · 无教裸 abysstarget\|forge · `50/51*` · `58-no-teach` |
| 4 | `ember_p1_abyss` Open 不再 tell+command 教 abysstarget | **PASS** | Open tell「点层进本 · 层目标在结算下一环页点设」· 无 command/tell 教 abysstarget（注释假阳已剔）· live OpenChat 同 · `48-abyss-open` · `S1_abyss_open_*` |
| 5 | 零改掉落/体力价/层表/日帽；零碰短征·团本·挑战/variety/soft；evacuate 未动 | **PASS** | top cost=30 · abyss fee 0..480×10 · weekly_cap=3 · raid cost=50 · short=5 · event_rate=0.85 · event_core=1 · affix_shard=2 · bv123 · short/raid/chal settle md5 未变 · SOFT_TARGET=3 · evacuate 仍在 · feat 零 yml/短征/团本/挑战/soft · `00-feat-zero-raise` · `00-cost-reparse` · `70-intact` · `90-endstate` |

## 旁注

- Stage2 三开 · daily_kills=2400 · ×0.97 · gate_daily=no · TrMenu **82**（含 `ember_p1_abyss_settle`）· jar sha256 `1ace1c6d1886…` plugins=server-runtime
- 单测 `EmberAbyssSettleNextLoopTest` 5/0 · `EmberAbyssSettleTest` 3/0
- 41/41 PASS · tip **未勾 CLOSED**（总控收口）
- live 深渊 newBest settle：本测未跑满层通关（can_next 灰因无 settle 层）；afterSettle 主钮由 unit D675 + OpenCommand 薄页 + abysspath/abysstarget 直播「点下一环」覆盖（同 TIP-DA/CZ 范式）
- rescored：`V0_cost_fee`（abyss.tiers fee 嵌套解析 miss）· `S1/V4_abyss_open`（Events 注释含 abysstarget/手打 假阳）· `S1_no_teach`（lore「不手打」）· `V0_end`（同 fee0）

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · weekly_cap=3 · raid cost=50 · short target=5 · top cost=30 · abyss fee0=0 · SOFT_TARGET=3 · ×0.97 未改 · bv123 · 零改深渊掉落/体力价/层表/日帽/短征薄页/团本薄页/挑战薄页/variety/soft-reward · legacy evacuate 未动 · 零 sx20 · 未改玩法数值 · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB

> **测岗抽测：** PASS live · feat `6e22b5d2f` · menu `1d575cf83` · tip `8a3f93564` · jar `1.65.826-d13333.local` · **勿勾 CLOSED**（总控收口）· 2026-10-14 09:49 Asia/Shanghai
