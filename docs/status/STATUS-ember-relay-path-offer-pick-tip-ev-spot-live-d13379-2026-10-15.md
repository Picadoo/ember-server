# 状态 · TIP-EV 传火路径选打 · gameplay/spot LIVE 抽测

> **抽测 PASS · tip 未勾 CLOSED（待总控收口）** · feat `6eec10d81` · tip批 `0acf03feb` · tip原文 `843fbe436` · menu `c9b84f30e` · jar `1.65.872-d13379.local` · sha256 `73b760bcf3de…` · bv **123** 未抬 · TrMenu **127** · MySQL · 2026-10-15 Asia/Shanghai · ancestor TIP-EU live `5d7e02ac9` / CLOSED `556312f27`

**裁决：** **PASS** · 2026-10-15 Asia/Shanghai · feat `6eec10d81` · menu tip `c9b84f30e` · tip/批 `0acf03feb` · tip原文 `843fbe436` · jar `1.65.872-d13379.local` · sha12 `73b760bcf3de` · bv **123** 未抬 · TrMenu **127** · MySQL · 证据 `/workspace/tmp/tip-ev-relay-path/` · **100/100 PASS**  
**范围：** offerPick / 无参 relaypath / 周要约 → 真开 `ember_p1_relay_path`（**禁** ConfirmTokens 教裸 relaypath 主 UI · 硬红门绿）· 薄页自动/提醒/静默/清空/技能组/冒险/工坊真开 · applyAndReply 下一步走选打薄页 · PAPI `relay_path`/`_label`/`_tip`/`_line` 诚实 · Q01 灰态+亮态 · **零改**伤害/CD表/掉落/体力价 · **零改** `ember_p1_breach_path`（md5=`c855310473ce…`）及 alert/stock/hurry/focus/doom/nest/save/sequel/bait 等邻批 *path · **零改** D568 传火「传火」战斗钮语义（仍 `/corerpg p1 relay`）· 「改路径」开薄页 · **零**新建 pace_path · **≠**已齐选打同题复读 · **tip 勿勾 CLOSED**（总控收口）

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | play live CoreRpg `1.65.872-d13379.local` · bv123 | **PASS** | jar sha256 `73b760bcf3de…` · MySQL connected · bv123 · `V0_jar` · `V0_switches_pre` |
| 2 | offerPick / 无参 relaypath / 周要约 → 真开 `/ember_p1_relay_path`（非 ConfirmTokens 教裸 relaypath） | **PASS** | live 开「传火 · 路径」· nudge 同开 · glance「点传火路径页」· **零**教裸 relaypath · `V1_offerPick_noarg_opens_thin` · `V1_weekly_offer_opens_thin` · `V1_hard_red_no_bare_confirm_tokens` · `V4_no_confirm_tokens_bare_relaypath` |
| 3 | 自动/提醒/静默/清空/技能组/冒险可点；PAPI `relay_path*` + q01 灰/亮 | **PASS** | 1→auto · 2→ask · 3→mute · C clear · K→技能组 · A→冒险 · F→工坊 · path/label/tip/line 诚实 · Q01 灰「需首通 Q01」→亮「已选」· `V2_*` · `V3_q01_gray` · `V1_bright_reopen` |
| 4 | 灰态 q01done；局内「传火」战斗钮语义零改；「改路径」开薄页；零改 D568 传火语义 | **PASS** | `[传火]`→`/corerpg p1 relay` · 「改路径」→`PATH_PICK_CMD` · `V4_d568_combat_btn_intact` · `featZeroRaise.d568CombatOk` |
| 5 | 零抬伤害/CD/掉落/体力价；零改 breach_path 等已齐；邻批 *path 零回归；TrMenu 127 · MySQL；相关 PickTest 全绿 | **PASS** | breach_path md5=`c855310473ce6803b18d363747394f9a` · alert md5=`246c8e957820b36747b1142f9413bdfa` · bv123 · `V0_breach_path_untouched` · `V0_alert_path_untouched` · `V4_feat_zero_loot_paths` · RelayPathPick **8/8** · RelayPath 1/1 · BreachPathPick · RunPapi · `V0_unit_mvn` · `V0_trmenu_loaded` |

## 旁注

- Stage2 三开 · daily_kills=2400 · ×0.97 · gate_daily=no · TrMenu **127**（含 `ember_p1_relay_path`）· jar sha256 `73b760bcf3de…` plugins=server-runtime · MySQL · bv123
- 单测 `EmberRelayPathPickTest` 8/8 / `EmberRelayPathTest` 1/1 / BreachPathPick 等邻批绿
- **100/100 PASS** · **tip 勿勾 CLOSED**（总控收口）
- 选打主 UI = `ember_p1_relay_path`；未教裸 relaypath；D568 局内「传火」战斗钮语义零改；未复读裂隙/预警/备药等选打；未建 pace_path

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · weekly_cap=3 · rot weekly_cap=3 · top cost=30 · SOFT_TARGET=3 · ×0.97 未改 · bv123 · 零改伤害/CD表/掉落/体力价/breach_path/alert_path/stock_path/hurry_path/focus_path/doom_path/nest_path/save_path/sequel_path/bait_path 及其他已齐 path · 零改 D568 传火战斗钮 · 零新建 pace_path · 零 sx20 · 未改玩法数值 · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB 配置 · **未勾 tip CLOSED**
