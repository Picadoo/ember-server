# 状态 · TIP-ES 备药路径选打 · gameplay/spot LIVE 抽测

> **抽测 PASS · tip 未勾 CLOSED（待总控收口）** · feat `e1b2553d4` · tip批 `2ab321047` · tip原文 `bd67d9a25` · menu `ca133c931` · jar `1.65.869-d13376.local` · sha256 `5cb2dac68ae4…` · bv **123** 未抬 · TrMenu **124** · MySQL · 2026-10-15 Asia/Shanghai · ancestor TIP-ER live `dfe0e4724` / CLOSED `dc6838c45`

**裁决：** **PASS** · 2026-10-15 Asia/Shanghai · feat `e1b2553d4` · menu tip `ca133c931` · tip/批 `2ab321047` · tip原文 `bd67d9a25` · jar `1.65.869-d13376.local` · sha12 `5cb2dac68ae4` · bv **123** 未抬 · TrMenu **124** · MySQL · 证据 `/workspace/tmp/tip-es-stock-path/` · **97/97 PASS**  
**范围：** offerPick / 无参 stockpath / 周要约 → 真开 `ember_p1_stock_path`（**禁** ConfirmTokens 教裸 stockpath 主 UI · 硬红门绿）· 薄页自动/提醒/静默/清空/技能组/冒险/工坊真开 · applyAndReply 下一步走选打薄页 · PAPI `stock_path`/`_label`/`_tip`/`_line` 诚实 · Q01 灰态+亮态 · **零改**伤害/CD表/掉落/体力价 · **零改** `ember_p1_hurry_path`（md5=`6bd68b7ffe…`）及 focus/doom/nest/save/sequel/bait 等邻批 *path · **零改** D599 备药「核对」战斗钮语义（仍 `/corerpg p1 stock`）· 「改路径」开薄页 · **零**新建 pace_path · **≠**已齐选打同题复读 · **tip 勿勾 CLOSED**（总控收口）

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | offerPick / 无参 stockpath / 周要约 → 真开 `/ember_p1_stock_path`（非 ConfirmTokens 教裸 stockpath） | **PASS** | live 开「备药 · 路径」· nudge 同开 · glance「点备药路径页」· **零**教裸 stockpath · `V1_offerPick_noarg_opens_thin` · `V1_weekly_offer_opens_thin` · `V1_hard_red_no_bare_confirm_tokens` · `V4_no_confirm_tokens_bare_stockpath` |
| 2 | 自动/提醒/静默/清空/技能组/冒险可点；PAPI `stock_path*` + q01 灰/亮 | **PASS** | 1→auto · 2→ask · 3→mute · C clear · K→技能组 · A→冒险 · F→工坊 · path/label/tip/line 诚实 · Q01 灰「需首通 Q01」→亮「已选」· `V2_*` · `V3_q01_gray` · `V1_bright_reopen` |
| 3 | 「核对」钮零改（仍 `/corerpg p1 stock`）；「改路径」开薄页；零改 D599 备药语义 | **PASS** | `[核对]`→`/corerpg p1 stock` · 「改路径」→`PATH_PICK_CMD` · `V4_d599_combat_btn_intact` · `featZeroRaise.d599CombatOk` |
| 4 | 零改 hurry_path；零抬 bv/伤害/CD/掉落/体力价；邻批 *path 零回归 | **PASS** | hurry_path md5=`6bd68b7ffe5ffea5ba7d726413c74f54` · focus md5=`d2bbcb5f341848986d67a6e30a958cd6` · bv123 · `V0_hurry_path_untouched` · `V0_focus_path_untouched` · `V4_feat_zero_loot_paths` · `V5_*` |
| 5 | play live jar 869 · TrMenu 124 · MySQL；相关 PickTest 全绿 | **PASS** | jar `1.65.869-d13376.local` sha256 `5cb2dac68ae4…` · MySQL connected · bv123 · StockPathPick **8/8** · StockPath 1/1 · HurryPathPick 等邻批 · RunPapi · `V0_jar` · `V0_unit_mvn` · `V0_trmenu_loaded` |

## 旁注

- Stage2 三开 · daily_kills=2400 · ×0.97 · gate_daily=no · TrMenu **124**（含 `ember_p1_stock_path`）· jar sha256 `5cb2dac68ae4…` plugins=server-runtime · MySQL · bv123
- 单测 `EmberStockPathPickTest` 8/8 / `EmberStockPathTest` / HurryPathPick / FocusPathPick 等绿
- **97/97 PASS** · **tip 勿勾 CLOSED**（总控收口）
- 选打主 UI = `ember_p1_stock_path`；未教裸 stockpath；D599 局内「核对」战斗钮语义零改；未复读急清/集火/灾临等选打；未建 pace_path

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · weekly_cap=3 · rot weekly_cap=3 · top cost=30 · SOFT_TARGET=3 · ×0.97 未改 · bv123 · 零改伤害/CD表/掉落/体力价/hurry_path/focus_path/doom_path/nest_path/save_path/sequel_path/bait_path 及其他已齐 path · 零改 D599 备药战斗钮 · 零新建 pace_path · 零 sx20 · 未改玩法数值 · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB · **未勾 tip CLOSED**
