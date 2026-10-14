# 状态 · TIP-DD 精选结算「下一环」菜单化抽测

**裁决：** **PASS** · feat `b07074070` · menu tip `7a098dda6`（feat `8b99da98e`）· tip/批 `b7160ee19` · jar `1.65.828-d13335.local` · 证据 `/workspace/tmp/tip-dd-featured-settle/`  
**范围：** afterSettle / D794 周帽满 → 开 `ember_p1_featured_settle` · 薄页再进精选 E / 换精选路径 1–2·C / 换硬本路径 PQR·K / 冒险 A / 工坊 F / 图装追 M 真开 · 灰态诚实（周帽满、未选路径）· PAPI `featured_settle_*` lore 诚实 · 主 UI 不教手打 enter/featurepath/forge/mapfarm · **零抬**掉落/体力价/周帽数值 · **零改**已齐 settle 薄页（短征·团本·挑战·深渊·图身份）· tip **勿勾 CLOSED**（总控收口）

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | afterSettle / D794 周帽满 → 开 `/ember_p1_featured_settle`（非 ConfirmTokens 教 enter/featurepath/forge/mapfarm 主 UI） | **PASS** | 源码 `afterSettle` 有/无路径皆 `SETTLE_NEXT_LOOP_CMD` + D794 周帽满同开薄页（unit NextLoop 6/0）· live OpenCommand 无路径/再开皆进薄页 · ConfirmTokens `/ember_p1_featured_settle` · 无教裸 enter\|featurepath\|forge\|mapfarm · `12-unit-*` · `30/57` · `58-no-teach` |
| 2 | 薄页真开：再进精选 / 换精选路径 / 换硬本路径 / 冒险 / 工坊 / 图装追 | **PASS** | E 接线 enter map[+challenge] · 1/2→featurepath challenge/normal · C clear · PQR→challengepath prefer/easy/follow · K clear · A→冒险 · F→工坊 · M→mapfarm · live 点击开菜单/发指令 · `10-menu-static` · `31/40/41/43/44/45/46` |
| 3 | PAPI `featured_settle_*` lore 诚实；周帽满/未选路径灰态 | **PASS** | 未 remember：path=0/cap=0/未选/周帽未满 空扩诚实 · E 灰「先选路径」gray dye · C 灰「未选」· 周帽满灰态静态 `featured_settle_cap>0` + D794 `remember(capFull)` · `20/30/51*-rescored` · `56-cap-gray-*` · `V3_*` |
| 4 | 零抬掉落/体力价/周帽数值；零改已齐 settle 薄页 | **PASS** | top cost=30 · weekly_cap=3 · rot_cap=3 · raid cost=50 · short=5 · event_rate=0.85 · event_core=1 · affix_shard=2 · bv123 · short/raid/chal/abyss/identity settle md5 未变 · SOFT_TARGET=3 · feat 零 yml/短征/团本/挑战/深渊/图身份/soft · `00-feat-zero-raise` · `70-intact` · `90-endstate` |

## 旁注

- Stage2 三开 · daily_kills=2400 · ×0.97 · gate_daily=no · TrMenu **84**（含 `ember_p1_featured_settle`）· jar sha256 `642f02476144…` plugins=server-runtime · MySQL connected · 本周精选 **q03**
- 单测 `EmberFeaturedSettleNextLoopTest` 6/0 · `EmberFeaturedSettleTest` 4/0
- 39/39 PASS · tip **勿勾 CLOSED**（总控收口）
- live 精选挑战 settle：本测 Q03 挑战遇 T3 刃 ConfirmTokens（「通关率」假阳性已剔）；afterSettle 主钮由 unit afterSettle 两态 + D794 + OpenCommand 薄页覆盖（同 TIP-DA/DC 范式）
- rescored：`V1_afterSettle` 假阳性「通关率会高很多」已剔 · `V2_papi_*` 空扩诚实（未 remember）

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · weekly_cap=3 · rot weekly_cap=3 · raid cost=50 · short target=5 · top cost=30 · SOFT_TARGET=3 · ×0.97 未改 · bv123 · 零改掉落/体力价/周帽/短征薄页/团本薄页/挑战薄页/深渊薄页/图身份薄页/variety/soft-reward · 零 sx20 · 未改玩法数值 · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB

> **测岗抽测：** PASS live · feat `b07074070` · menu tip `7a098dda6` · tip `b7160ee19` · jar `1.65.828-d13335.local` · **勿勾 CLOSED**（总控收口）· 2026-10-14 10:36 Asia/Shanghai
