# 状态 · TIP-ER 急清路径选打 · gameplay/spot LIVE 抽测

> **抽测 PASS · tip 未勾 CLOSED（待总控收口）** · feat `f7fc5125f` · tip批 `4e0e9c6d2` · tip原文 `4ebd05496` · menu `5a4e37268` · jar `1.65.868-d13375.local` · sha256 `2ecca4b20404…` · bv **123** 未抬 · TrMenu **123** · MySQL · 2026-10-15 Asia/Shanghai · ancestor TIP-EQ live `c6e933433`

**裁决：** **PASS** · 2026-10-15 Asia/Shanghai · feat `f7fc5125f` · menu tip `5a4e37268` · tip/批 `4e0e9c6d2` · tip原文 `4ebd05496` · jar `1.65.868-d13375.local` · sha12 `2ecca4b20404` · bv **123** 未抬 · TrMenu **123** · MySQL · 证据 `/workspace/tmp/tip-er-hurry-path/` · **96/96 PASS**  
**范围：** offerPick / 无参 hurrypath / 周要约 → 真开 `ember_p1_hurry_path`（**禁** ConfirmTokens 教裸 hurrypath 主 UI · 硬红门绿）· 薄页自动/提醒/静默/清空/技能组/冒险/工坊真开 · applyAndReply 下一步走选打薄页 · PAPI `hurry_path`/`_label`/`_tip`/`_line` 诚实 · Q01 灰态+亮态 · **零改**伤害/CD表/掉落/体力价 · **零改** `ember_p1_focus_path`（md5=`d2bbcb5f…`）及 doom/nest/save/sequel/bait 等邻批 *path · **零改** D598 急清「冲刺」战斗钮语义（仍 `/corerpg p1 hurry`）· 「改路径」开薄页 · **零**新建 pace_path · **≠**已齐选打同题复读 · **tip 勿勾 CLOSED**（总控收口）

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | offerPick / 无参 hurrypath / 周要约 → 真开 `/ember_p1_hurry_path`（非 ConfirmTokens 教裸 hurrypath） | **PASS** | live 开「急清 · 路径」· nudge 同开 · glance「点急清路径页」· **零**教裸 hurrypath · `V1_offerPick_noarg_opens_thin` · `V1_weekly_offer_opens_thin` · `V1_hard_red_no_bare_confirm_tokens` · `V4_no_confirm_tokens_bare_hurrypath` |
| 2 | 自动/提醒/静默/清空/技能组/冒险可点；PAPI `hurry_path*` + q01 灰/亮 | **PASS** | 1→auto · 2→ask · 3→mute · C clear · K→技能组 · A→冒险 · F→工坊 · path/label/tip/line 诚实 · Q01 灰「需首通 Q01」→亮「已选」· `V2_*` · `V3_q01_gray` · `V1_bright_reopen` |
| 3 | 「冲刺」钮零改（仍 `/corerpg p1 hurry`）；「改路径」开薄页；零改 D598 急清语义 | **PASS** | `[冲刺]`→`/corerpg p1 hurry` · 「改路径」→`PATH_PICK_CMD` · `V4_d598_combat_btn_intact` · `featZeroRaise.d598CombatOk` |
| 4 | 零改 focus_path；零抬 bv/伤害/CD/掉落/体力价；邻批 *path 零回归 | **PASS** | focus_path md5=`d2bbcb5f341848986d67a6e30a958cd6` · doom md5=`1e0a488184fbec0e35150b1d29550ce2` · bv123 · `V0_focus_path_untouched` · `V0_doom_path_untouched` · `V4_feat_zero_loot_paths` · `V5_*` |
| 5 | play live jar 868 · TrMenu 123 · MySQL；相关 PickTest 全绿 | **PASS** | jar `1.65.868-d13375.local` sha256 `2ecca4b20404…` · MySQL connected · bv123 · HurryPathPick **8/8** · HurryPath 1/1 · FocusPathPick 等邻批 · RunPapi · `V0_jar` · `V0_unit_mvn` · `V0_trmenu_loaded` |

## 旁注

- Stage2 三开 · daily_kills=2400 · ×0.97 · gate_daily=no · TrMenu **123**（含 `ember_p1_hurry_path`）· jar sha256 `2ecca4b20404…` plugins=server-runtime · MySQL · bv123
- 单测 `EmberHurryPathPickTest` 8/8 / `EmberHurryPathTest` / FocusPathPick / DoomPathPick 等绿
- **96/96 PASS** · **tip 勿勾 CLOSED**（总控收口）
- 选打主 UI = `ember_p1_hurry_path`；未教裸 hurrypath；D598 局内「冲刺」战斗钮语义零改；未复读集火/灾临/卡点等选打；未建 pace_path

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · weekly_cap=3 · rot weekly_cap=3 · top cost=30 · SOFT_TARGET=3 · ×0.97 未改 · bv123 · 零改伤害/CD表/掉落/体力价/focus_path/doom_path/nest_path/save_path/sequel_path/bait_path 及其他已齐 path · 零改 D598 急清战斗钮 · 零新建 pace_path · 零 sx20 · 未改玩法数值 · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB · **未勾 tip CLOSED**
