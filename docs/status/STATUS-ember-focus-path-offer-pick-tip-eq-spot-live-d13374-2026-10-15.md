# 状态 · TIP-EQ 集火路径选打 · gameplay/spot LIVE 抽测

> **抽测 PASS · tip 未勾 CLOSED（待总控收口）** · feat `755be1f29` · tip批 `453590e06` · tip原文 `f1c8949e0` · menu `9d92087d5` · jar `1.65.867-d13374.local` · sha256 `ba87b157f12c…` · bv **123** 未抬 · TrMenu **122** · MySQL · 2026-10-15 Asia/Shanghai · ancestor TIP-EP live `00b50ba36`

**裁决：** **PASS** · 2026-10-15 Asia/Shanghai · feat `755be1f29` · menu tip `9d92087d5` · tip/批 `453590e06` · tip原文 `f1c8949e0` · jar `1.65.867-d13374.local` · sha12 `ba87b157f12c` · bv **123** 未抬 · TrMenu **122** · MySQL · 证据 `/workspace/tmp/tip-eq-focus-path/` · **95/95 PASS**  
**范围：** offerPick / 无参 focuspath / 周要约 → 真开 `ember_p1_focus_path`（**禁** ConfirmTokens 教裸 focuspath 主 UI · 硬红门绿）· 薄页自动/提醒/静默/清空/技能组/冒险/工坊真开 · applyAndReply 下一步走选打薄页 · PAPI `focus_path`/`_label`/`_tip`/`_line` 诚实 · Q01 灰态+亮态 · **零改**伤害/CD表/掉落/体力价 · **零改** `ember_p1_doom_path`（md5=`1e0a4881…`）及 nest/save/sequel/bait 等邻批 *path · **零改** D597 集火「集火」战斗钮语义（仍 `/corerpg p1 focus`）· 「改路径」开薄页 · **零**新建 pace_path · **≠**已齐选打同题复读 · **tip 勿勾 CLOSED**（总控收口）

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | offerPick / 无参 focuspath / 周要约 → 真开 `/ember_p1_focus_path`（非 ConfirmTokens 教裸 focuspath） | **PASS** | live 开「集火 · 路径」· nudge 同开 · glance「点集火路径页」· **零**教裸 focuspath · `V1_offerPick_noarg_opens_thin` · `V1_weekly_offer_opens_thin` · `V1_hard_red_no_bare_confirm_tokens` · `V4_no_confirm_tokens_bare_focuspath` |
| 2 | 自动/提醒/静默/清空/技能组/冒险可点；PAPI `focus_path*` + q01 灰/亮 | **PASS** | 1→auto · 2→ask · 3→mute · C clear · K→技能组 · A→冒险 · F→工坊 · path/label/tip/line 诚实 · Q01 灰「需首通 Q01」→亮「已选」· `V2_*` · `V3_q01_gray` · `V1_bright_reopen` |
| 3 | 「集火」钮零改（仍 `/corerpg p1 focus`）；「改路径」开薄页；零改 D597 集火语义 | **PASS** | `[集火]`→`/corerpg p1 focus` · 「改路径」→`PATH_PICK_CMD` · `V4_d597_combat_btn_intact` · `featZeroRaise.d597CombatOk` |
| 4 | 零改 doom_path；零抬 bv/伤害/CD/掉落/体力价；邻批 *path 零回归 | **PASS** | doom_path md5=`1e0a488184fbec0e35150b1d29550ce2` · bv123 · `V0_doom_path_untouched` · `V4_feat_zero_loot_paths` · `V5_*` |
| 5 | play live jar 867 · TrMenu 122 · MySQL；相关 PickTest 全绿 | **PASS** | jar `1.65.867-d13374.local` sha256 `ba87b157f12c…` · MySQL connected · bv123 · FocusPathPick **8/8** · FocusPath 1/1 · DoomPathPick 等邻批 · RunPapi · `V0_jar` · `V0_unit_mvn` · `V0_trmenu_loaded` |

## 旁注

- Stage2 三开 · daily_kills=2400 · ×0.97 · gate_daily=no · TrMenu **122**（含 `ember_p1_focus_path`）· jar sha256 `ba87b157f12c…` plugins=server-runtime · MySQL · bv123
- 单测 `EmberFocusPathPickTest` 8/8 / `EmberFocusPathTest` / DoomPathPick / NestPathPick 等绿
- **95/95 PASS** · **tip 勿勾 CLOSED**（总控收口）
- 选打主 UI = `ember_p1_focus_path`；未教裸 focuspath；D597 局内「集火」战斗钮语义零改；未复读灾临/卡点等选打；未建 pace_path

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · weekly_cap=3 · rot weekly_cap=3 · top cost=30 · SOFT_TARGET=3 · ×0.97 未改 · bv123 · 零改伤害/CD表/掉落/体力价/doom_path/nest_path/save_path/sequel_path/bait_path 及其他已齐 path · 零改 D597 集火战斗钮 · 零新建 pace_path · 零 sx20 · 未改玩法数值 · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB · **未勾 tip CLOSED**
