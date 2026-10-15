# 状态 · TIP-EP 灾临路径选打 · 正式 gameplay/spot LIVE 抽测

> **正式 live PASS（独立裁决）** · feat `274c3299f` · tip批 `1064e9b7f` · tip原文 `5b629e8af` · menu `635d5b670` · jar `1.65.866-d13373.local` · sha256 `86840c762fc7…` · docs CLOSED 早间静态 `616563d47`（自写 PASS 忽略，以本窗裁决为准）· **未再改 tip CLOSED 文案** · 2026-10-15 Asia/Shanghai · ancestor TIP-EO CLOSED live `375570391`

**裁决：** **PASS** · 2026-10-15 Asia/Shanghai · feat `274c3299f` · menu tip `635d5b670` · tip/批 `1064e9b7f` · tip原文 `5b629e8af` · jar `1.65.866-d13373.local` · sha12 `86840c762fc7` · bv **123** 未抬 · TrMenu **121** · MySQL · 证据 `/workspace/tmp/tip-ep-doom-path/` · **94/94 PASS**  
**范围：** offerPick / 无参 doompath / 周要约 → 真开 `ember_p1_doom_path`（**禁** ConfirmTokens 教裸 doompath 主 UI · 硬红门绿）· 薄页自动/提醒/静默/清空/技能组/冒险/工坊真开 · applyAndReply 下一步走选打薄页 · PAPI `doom_path`/`_label`/`_tip`/`_line` 诚实 · Q01 灰态+亮态 · **零改**伤害/CD表/掉落/体力价 · **零改** `ember_p1_nest_path`（md5=`7a69bcd8…`）及 save/sequel/bait 等邻批 *path · **零改** D596 奔赴「奔赴」战斗钮语义（仍 `/corerpg p1 doom`）· 「改路径」开薄页 · **零**新建 pace_path · **≠**已齐选打同题复读 · **tip CLOSED 文案未改**（除非 FAIL；本窗 PASS）

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | offerPick / 无参 doompath / 周要约 → 真开 `/ember_p1_doom_path`（非 ConfirmTokens 教裸 doompath） | **PASS** | live 开「灾临 · 路径」· nudge 同开 · glance「点灾临路径页」· **零**教裸 doompath · `V1_offerPick_noarg_opens_thin` · `V1_weekly_offer_opens_thin` · `V1_hard_red_no_bare_confirm_tokens` · `V4_no_confirm_tokens_bare_doompath` |
| 2 | 自动/提醒/静默/清空/技能组/冒险可点；PAPI `doom_path*` + q01 灰/亮 | **PASS** | 1→auto · 2→ask · 3→mute · C clear · K→技能组 · A→冒险 · F→工坊 · path/label/tip/line 诚实 · Q01 灰「需首通 Q01」→亮「已选」· `V2_*` · `V3_q01_gray` · `V1_bright_reopen` |
| 3 | 局内「奔赴」仍 `/corerpg p1 doom`；「改路径」开薄页；零改 D596 奔赴语义 | **PASS** | `[奔赴]`→`/corerpg p1 doom` · 「改路径」→`PATH_PICK_CMD` · `V4_d596_combat_btn_intact` · `featZeroRaise.d596CombatOk` |
| 4 | 零改 nest_path；零抬 bv/伤害/CD/掉落/体力价；邻批 *path 零回归 | **PASS** | nest_path md5=`7a69bcd8a3bab01a9832020f4712de91` · bv123 · `V0_nest_path_untouched` · `V4_feat_zero_loot_paths` · `V5_*` |
| 5 | play live jar 866 · TrMenu 121 · MySQL；`EmberDoomPathPickTest` 全绿 | **PASS** | jar `1.65.866-d13373.local` sha256 `86840c762fc7…` · MySQL connected · bv123 · DoomPathPick **8/8** · DoomPath 1/1 · NestPathPick 等邻批 · RunPapi · `V0_jar` · `V0_unit_mvn` · `V0_trmenu_loaded` |

## 旁注

- Stage2 三开 · daily_kills=2400 · ×0.97 · gate_daily=no · TrMenu **121**（含 `ember_p1_doom_path`）· jar sha256 `86840c762fc7…` plugins=server-runtime · MySQL · bv123
- 单测 `EmberDoomPathPickTest` 8/8 / `EmberDoomPathTest` / NestPathPick / SavePathPick 等绿
- **94/94 PASS** · tip 已 CLOSED（早间静态 `616563d47`）· **本窗未改 tip 文案** · 独立 STATUS 交正式 live 裁决
- 选打主 UI = `ember_p1_doom_path`；未教裸 doompath；D596 局内「奔赴」战斗钮语义零改；未复读卡点/终救等选打；未建 pace_path

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · weekly_cap=3 · rot weekly_cap=3 · top cost=30 · SOFT_TARGET=3 · ×0.97 未改 · bv123 · 零改伤害/CD表/掉落/体力价/nest_path/save_path/sequel_path/bait_path 及其他已齐 path · 零改 D596 奔赴战斗钮 · 零新建 pace_path · 零 sx20 · 未改玩法数值 · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB · **未改 tip CLOSED 文案**
