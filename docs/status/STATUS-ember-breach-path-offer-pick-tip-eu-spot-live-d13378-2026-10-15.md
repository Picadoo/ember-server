# 状态 · TIP-EU 裂隙路径选打 · gameplay/spot LIVE 抽测

> **抽测 PASS · tip 未勾 CLOSED（待总控收口）** · feat `afa21fbef` · tip批 `521d55ef1` · tip原文 `86cefa16c` · menu `dba5004a2` · jar `1.65.871-d13378.local` · sha256 `6a4fecb70fd8…` · bv **123** 未抬 · TrMenu **126** · MySQL · 2026-10-15 Asia/Shanghai · ancestor TIP-ET live `b8c70fc74` / CLOSED `8b1b37a38`

**裁决：** **PASS** · 2026-10-15 Asia/Shanghai · feat `afa21fbef` · menu tip `dba5004a2` · tip/批 `521d55ef1` · tip原文 `86cefa16c` · jar `1.65.871-d13378.local` · sha12 `6a4fecb70fd8` · bv **123** 未抬 · TrMenu **126** · MySQL · 证据 `/workspace/tmp/tip-eu-breach-path/` · **99/99 PASS**  
**范围：** offerPick / 无参 breachpath / 周要约 → 真开 `ember_p1_breach_path`（**禁** ConfirmTokens 教裸 breachpath 主 UI · 硬红门绿）· 薄页自动/提醒/静默/清空/技能组/冒险/工坊真开 · applyAndReply 下一步走选打薄页 · PAPI `breach_path`/`_label`/`_tip`/`_line` 诚实 · Q01 灰态+亮态 · **零改**伤害/CD表/掉落/体力价 · **零改** `ember_p1_alert_path`（md5=`246c8e957820…`）及 stock/hurry/focus/doom/nest/save/sequel/bait 等邻批 *path · **零改** D569 裂隙「裂隙」战斗钮语义（仍 `/corerpg p1 breach`）· 「改路径」开薄页 · **零**新建 pace_path · **≠**已齐选打同题复读 · **tip 勿勾 CLOSED**（总控收口）

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | play live CoreRpg `1.65.871-d13378.local` · bv123 | **PASS** | jar sha256 `6a4fecb70fd8…` · MySQL connected · bv123 · `V0_jar` · `V0_switches_pre` |
| 2 | offerPick / 无参 breachpath / 周要约 → 真开 `/ember_p1_breach_path`（非 ConfirmTokens 教裸 breachpath） | **PASS** | live 开「裂隙 · 路径」· nudge 同开 · glance「点裂隙路径页」· **零**教裸 breachpath · `V1_offerPick_noarg_opens_thin` · `V1_weekly_offer_opens_thin` · `V1_hard_red_no_bare_confirm_tokens` · `V4_no_confirm_tokens_bare_breachpath` |
| 3 | 自动/提醒/静默/清空/技能组/冒险可点；PAPI `breach_path*` + q01 灰/亮 | **PASS** | 1→auto · 2→ask · 3→mute · C clear · K→技能组 · A→冒险 · F→工坊 · path/label/tip/line 诚实 · Q01 灰「需首通 Q01」→亮「已选」· `V2_*` · `V3_q01_gray` · `V1_bright_reopen` |
| 4 | 灰态 q01done；局内「裂隙」战斗钮语义零改；「改路径」开薄页；零改 D569 裂隙语义 | **PASS** | `[裂隙]`→`/corerpg p1 breach` · 「改路径」→`PATH_PICK_CMD` · `V4_d569_combat_btn_intact` · `featZeroRaise.d569CombatOk` |
| 5 | 零抬伤害/CD/掉落/体力价；零改 alert_path 等已齐；邻批 *path 零回归；TrMenu 126 · MySQL；相关 PickTest 全绿 | **PASS** | alert_path md5=`246c8e957820b36747b1142f9413bdfa` · stock md5=`f7892d762b71f91c80e1266745d02815` · bv123 · `V0_alert_path_untouched` · `V0_stock_path_untouched` · `V4_feat_zero_loot_paths` · BreachPathPick **8/8** · BreachPath 1/1 · AlertPathPick · RunPapi · `V0_unit_mvn` · `V0_trmenu_loaded` |

## 旁注

- Stage2 三开 · daily_kills=2400 · ×0.97 · gate_daily=no · TrMenu **126**（含 `ember_p1_breach_path`）· jar sha256 `6a4fecb70fd8…` plugins=server-runtime · MySQL · bv123
- 单测 `EmberBreachPathPickTest` 8/8 / `EmberBreachPathTest` / AlertPathPick 等邻批绿
- **99/99 PASS** · **tip 勿勾 CLOSED**（总控收口）
- 选打主 UI = `ember_p1_breach_path`；未教裸 breachpath；D569 局内「裂隙」战斗钮语义零改；未复读预警/备药/急清/集火/灾临等选打；未建 pace_path

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · weekly_cap=3 · rot weekly_cap=3 · top cost=30 · SOFT_TARGET=3 · ×0.97 未改 · bv123 · 零改伤害/CD表/掉落/体力价/alert_path/stock_path/hurry_path/focus_path/doom_path/nest_path/save_path/sequel_path/bait_path 及其他已齐 path · 零改 D569 裂隙战斗钮 · 零新建 pace_path · 零 sx20 · 未改玩法数值 · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB 配置 · **未勾 tip CLOSED**
