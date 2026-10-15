# 状态 · TIP-EW 护灯路径选打 · gameplay/spot LIVE 抽测

> **抽测 PASS · tip 未勾 CLOSED（待总控收口）** · feat `113e9f7f0` · tip批 `ead4c7ee4` · tip原文 `4dbde88e7` · menu `b4a3273eb` · jar `1.65.873-d13380.local` · sha256 `b9cda161d210…` · bv **123** 未抬 · TrMenu **128** · MySQL · 2026-10-15 Asia/Shanghai · ancestor TIP-EV live `ffd10969f` / CLOSED `4580b5583`

**裁决：** **PASS** · 2026-10-15 Asia/Shanghai · feat `113e9f7f0` · menu tip `b4a3273eb` · tip/批 `ead4c7ee4` · tip原文 `4dbde88e7` · jar `1.65.873-d13380.local` · sha12 `b9cda161d210` · bv **123** 未抬 · TrMenu **128** · MySQL · 证据 `/workspace/tmp/tip-ew-beacon-path/` · **101/101 PASS**  
**范围：** offerPick / 无参 beaconpath / 周要约 → 真开 `ember_p1_beacon_path`（**禁** ConfirmTokens 教裸 beaconpath 主 UI · 硬红门绿）· 薄页自动/提醒/静默/清空/技能组/冒险/工坊真开 · applyAndReply 下一步走选打薄页 · PAPI `beacon_path`/`_label`/`_tip`/`_line` 诚实 · Q01 灰态+亮态 · **零改**伤害/CD表/掉落/体力价 · **零改** `ember_p1_relay_path`（md5=`8d857ed8065a…`）及 breach/alert/stock/hurry/focus/doom/nest/save/sequel/bait 等邻批 *path · **零改** D567 护灯「护灯」战斗钮语义（仍 `/corerpg p1 beacon`）· 「改路径」开薄页 · **零**新建 pace_path · **≠**已齐选打同题复读 · **tip 勿勾 CLOSED**（总控收口）

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | play live CoreRpg `1.65.873-d13380.local` · bv123 | **PASS** | jar sha256 `b9cda161d210…` · MySQL connected · bv123 · `V0_jar` · `V0_switches_pre` |
| 2 | offerPick / 无参 beaconpath / 周要约 → 真开 `/ember_p1_beacon_path`（非 ConfirmTokens 教裸 beaconpath） | **PASS** | live 开「护灯 · 路径」· nudge 同开 · glance「点护灯路径页」· **零**教裸 beaconpath · `V1_offerPick_noarg_opens_thin` · `V1_weekly_offer_opens_thin` · `V1_hard_red_no_bare_confirm_tokens` · `V4_no_confirm_tokens_bare_beaconpath` |
| 3 | 自动/提醒/静默/清空/技能组/冒险可点；PAPI `beacon_path*` + q01 灰/亮 | **PASS** | 1→auto · 2→ask · 3→mute · C clear · K→技能组 · A→冒险 · F→工坊 · path/label/tip/line 诚实 · Q01 灰「需首通 Q01」→亮「已选」· `V2_*` · `V3_q01_gray` · `V1_bright_reopen` |
| 4 | 灰态 q01done；局内「护灯」战斗钮语义零改；「改路径」开薄页；零改 D567 护灯语义 | **PASS** | `[护灯]`→`/corerpg p1 beacon` · 「改路径」→`PATH_PICK_CMD` · `V4_d567_combat_btn_intact` · `featZeroRaise.d567CombatOk` |
| 5 | 零抬伤害/CD/掉落/体力价；零改 relay_path 等已齐；邻批 *path 零回归；TrMenu 128 · MySQL；相关 PickTest 全绿 | **PASS** | relay_path md5=`8d857ed8065a499997a349e74f3faa27` · breach md5=`c855310473ce6803b18d363747394f9a` · bv123 · `V0_relay_path_untouched` · `V0_breach_path_untouched` · `V4_feat_zero_loot_paths` · BeaconPathPick **8/8** · BeaconPath 1/1 · RunPapi · `V0_unit_mvn` · `V0_trmenu_loaded` |

## 旁注

- Stage2 三开 · daily_kills=2400 · ×0.97 · gate_daily=no · TrMenu **128**（含 `ember_p1_beacon_path`）· jar sha256 `b9cda161d210…` plugins=server-runtime · MySQL · bv123
- 单测 `EmberBeaconPathPickTest` 8/8 / `EmberBeaconPathTest` 1/1 / 邻批 PickTest 绿
- **101/101 PASS** · **tip 勿勾 CLOSED**（总控收口）
- 选打主 UI = `ember_p1_beacon_path`；未教裸 beaconpath；D567 局内「护灯」战斗钮语义零改；未复读传火/裂隙/预警等选打；未建 pace_path

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · weekly_cap=3 · rot weekly_cap=3 · top cost=30 · SOFT_TARGET=3 · ×0.97 未改 · bv123 · 零改伤害/CD表/掉落/体力价/relay_path/breach_path/alert_path/stock_path/hurry_path/focus_path/doom_path/nest_path/save_path/sequel_path/bait_path 及其他已齐 path · 零改 D567 护灯战斗钮 · 零新建 pace_path · 零 sx20 · 未改玩法数值 · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB 配置 · **未勾 tip CLOSED**
