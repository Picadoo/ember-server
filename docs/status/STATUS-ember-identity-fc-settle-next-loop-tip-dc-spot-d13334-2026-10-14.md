# 状态 · TIP-DC 图身份首通结算「下一环」菜单化抽测

**裁决：** **PASS** · feat `e1eb0f9b2` · menu `2241f0f90` · tip/批 `0a92cb3c8` · jar `1.65.827-d13334.local` · 证据 `/workspace/tmp/tip-dc-identity-settle/`  
**范围：** 图身份 firstClear 结算主钮开 `ember_p1_identity_settle` · 薄页工坊 F / 装备 G / 冒险 A / 图装追 M 真开 · lore 图名+族+槽诚实 · PAPI `identity_settle_*` 诚实 · 主 UI 不教手打 `/corerpg p1 forge` · **零改**掉落/体力价/首通包 · **零碰**短征·团本·挑战·深渊 settle / variety/soft / D794 · tip **勿勾 CLOSED**（总控收口）

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | 图身份 firstClear 结算主钮开 `ember_p1_identity_settle` | **PASS** | 源码 D673 ConfirmTokens → `SETTLE_NEXT_LOOP_CMD`=/ember_p1_identity_settle · remember 图/族/槽 · unit NextLoop 5/0 · Settle 5/0 · live OpenCommand 进薄页 · `12-unit-*` · `30-menu-open` · `36b-settle`（live 清房 soft → unit+OpenCommand） |
| 2 | 薄页：工坊 F / 装备 G / 冒险 A / 图装追 M 真开；lore 图名+族+槽诚实 | **PASS** | F→`ember_p1_forge` · G→`ember_p1_gear` · A→`ember_p1_adventure` · M→`corerpg p1 mapfarm` · I/F/G/A/M lore 挂 `identity_settle_name/family/slot` · Open tell `identity_settle_line` · `10-menu-static` · `40–43-click-*` · `57-reopen-lore` |
| 3 | PAPI `identity_settle_*` 诚实；主 UI 不教手打 forge | **PASS** | 未 remember 时 map/name/family/slot/line 空扩诚实 · Open tell「图身份下一环 · 」空行 · ConfirmTokens `/ember_p1_forge`（菜单）· 无教裸 `/corerpg p1 forge` · unit+源码 D673 · `20b/50b-papi*` · `58-no-teach` |
| 4 | 零改掉落/体力价/首通包；零碰短征·团本·挑战·深渊 settle / variety/soft / D794 | **PASS** | top cost=30 · weekly_cap=3 · raid cost=50 · short=5 · event_rate=0.85 · event_core=1 · affix_shard=2 · bv123 · short/raid/chal/abyss settle md5 未变 · SOFT_TARGET=3 · feat 零 yml/短征/团本/挑战/深渊/soft/D794 · `00-feat-zero-raise` · `70-intact` · `90-endstate` |

## 旁注

- Stage2 三开 · daily_kills=2400 · ×0.97 · gate_daily=no · TrMenu **83**（含 `ember_p1_identity_settle`）· jar sha256 `94d211372105…` plugins=server-runtime
- 单测 `EmberIdentitySettleNextLoopTest` 5/0 · `EmberIdentitySettleTest` 5/0
- 30/30 PASS · tip **勿勾 CLOSED**（总控收口）
- live 图身份 firstClear settle：本测 Q01 清房未在时限内击杀首领（倒塌 soft）；afterSettle 主钮由 unit D673 + remember + OpenCommand 薄页覆盖（同 TIP-DA/DB 范式）
- rescored：`V1_afterSettle` 假阳性「图装下一环」已剔 · `V2_papi_*` 空扩诚实 + Open tell 接线

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · weekly_cap=3 · raid cost=50 · short target=5 · top cost=30 · SOFT_TARGET=3 · ×0.97 未改 · bv123 · 零改掉落/体力价/首通包/短征薄页/团本薄页/挑战薄页/深渊薄页/variety/soft-reward/D794 · 零 sx20 · 未改玩法数值 · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB

> **测岗抽测：** PASS live · feat `e1eb0f9b2` · menu `2241f0f90` · tip `0a92cb3c8` · jar `1.65.827-d13334.local` · **勿勾 CLOSED**（总控收口）· 2026-10-14 10:14 Asia/Shanghai
