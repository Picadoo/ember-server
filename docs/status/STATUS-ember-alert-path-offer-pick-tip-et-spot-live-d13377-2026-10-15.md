# 状态 · TIP-ET 预警路径选打 · gameplay/spot LIVE 抽测

> **抽测 PASS · tip 未勾 CLOSED（待总控收口）** · feat `0517e92ed` · tip批 `52edcaf29` · tip原文 `1a7ecb8ef` · menu `1248a512b` · jar `1.65.870-d13377.local` · sha256 `233c766ef218…` · bv **123** 未抬 · TrMenu **125** · MySQL · 2026-10-15 Asia/Shanghai · ancestor TIP-ES live `1201f5d52` / CLOSED `24dc2ff5b`

**裁决：** **PASS** · 2026-10-15 Asia/Shanghai · feat `0517e92ed` · menu tip `1248a512b` · tip/批 `52edcaf29` · tip原文 `1a7ecb8ef` · jar `1.65.870-d13377.local` · sha12 `233c766ef218` · bv **123** 未抬 · TrMenu **125** · MySQL · 证据 `/workspace/tmp/tip-et-alert-path/` · **98/98 PASS**  
**范围：** offerPick / 无参 alertpath / 周要约 → 真开 `ember_p1_alert_path`（**禁** ConfirmTokens 教裸 alertpath 主 UI · 硬红门绿）· 薄页自动/提醒/静默/清空/技能组/冒险/工坊真开 · applyAndReply 下一步走选打薄页 · PAPI `alert_path`/`_label`/`_tip`/`_line` 诚实 · Q01 灰态+亮态 · **零改**伤害/CD表/掉落/体力价 · **零改** `ember_p1_stock_path`（md5=`f7892d762b71…`）及 hurry/focus/doom/nest/save/sequel/bait 等邻批 *path · **零改** D600 预警「就位」战斗钮语义（仍 `/corerpg p1 alert`）· 「改路径」开薄页 · **零**新建 pace_path · **≠**已齐选打同题复读 · **tip 勿勾 CLOSED**（总控收口）

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | offerPick / 无参 alertpath / 周要约 → 真开 `/ember_p1_alert_path`（非 ConfirmTokens 教裸 alertpath） | **PASS** | live 开「预警 · 路径」· nudge 同开 · glance「点预警路径页」· **零**教裸 alertpath · `V1_offerPick_noarg_opens_thin` · `V1_weekly_offer_opens_thin` · `V1_hard_red_no_bare_confirm_tokens` · `V4_no_confirm_tokens_bare_alertpath` |
| 2 | 自动/提醒/静默/清空/技能组/冒险可点；PAPI `alert_path*` + q01 灰/亮 | **PASS** | 1→auto · 2→ask · 3→mute · C clear · K→技能组 · A→冒险 · F→工坊 · path/label/tip/line 诚实 · Q01 灰「需首通 Q01」→亮「已选」· `V2_*` · `V3_q01_gray` · `V1_bright_reopen` |
| 3 | 「就位」钮零改（仍 `/corerpg p1 alert`）；「改路径」开薄页；零改 D600 预警语义 | **PASS** | `[就位]`→`/corerpg p1 alert` · 「改路径」→`PATH_PICK_CMD` · `V4_d600_combat_btn_intact` · `featZeroRaise.d600CombatOk` |
| 4 | 零改 stock_path；零抬 bv/伤害/CD/掉落/体力价；邻批 *path 零回归 | **PASS** | stock_path md5=`f7892d762b71f91c80e1266745d02815` · hurry md5=`6bd68b7ffe5ffea5ba7d726413c74f54` · bv123 · `V0_stock_path_untouched` · `V0_hurry_path_untouched` · `V4_feat_zero_loot_paths` · `V5_*` |
| 5 | play live jar 870 · TrMenu 125 · MySQL；相关 PickTest 全绿 | **PASS** | jar `1.65.870-d13377.local` sha256 `233c766ef218…` · MySQL connected · bv123 · AlertPathPick **8/8** · AlertPath 1/1 · StockPathPick 等邻批 · RunPapi · `V0_jar` · `V0_unit_mvn` · `V0_trmenu_loaded` |

## 旁注

- Stage2 三开 · daily_kills=2400 · ×0.97 · gate_daily=no · TrMenu **125**（含 `ember_p1_alert_path`）· jar sha256 `233c766ef218…` plugins=server-runtime · MySQL · bv123
- 单测 `EmberAlertPathPickTest` 8/8 / `EmberAlertPathTest` / StockPathPick / HurryPathPick 等绿
- **98/98 PASS** · **tip 勿勾 CLOSED**（总控收口）
- 选打主 UI = `ember_p1_alert_path`；未教裸 alertpath；D600 局内「就位」战斗钮语义零改；未复读备药/急清/集火/灾临等选打；未建 pace_path

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · weekly_cap=3 · rot weekly_cap=3 · top cost=30 · SOFT_TARGET=3 · ×0.97 未改 · bv123 · 零改伤害/CD表/掉落/体力价/stock_path/hurry_path/focus_path/doom_path/nest_path/save_path/sequel_path/bait_path 及其他已齐 path · 零改 D600 预警战斗钮 · 零新建 pace_path · 零 sx20 · 未改玩法数值 · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB · **未勾 tip CLOSED**
