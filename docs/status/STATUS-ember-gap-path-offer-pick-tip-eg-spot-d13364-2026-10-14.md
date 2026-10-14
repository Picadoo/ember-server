# 状态 · TIP-EG 收招路径选打菜单化抽测

> **抽测 PASS · tip 未勾 CLOSED（待总控收口）** · feat `c9d2b431f` · tip批 `98f341015` · menu `ce0ac0d97` · jar `1.65.857-d13364.local` · 2026-10-14 Asia/Shanghai · ancestor TIP-EF CLOSED live `80b2ce3d2`

**裁决：** **PASS** · 2026-10-14 Asia/Shanghai · feat `c9d2b431f` · menu tip `ce0ac0d97` · tip/批 `98f341015` · jar `1.65.857-d13364.local` · sha12 `67d23267c903` · 证据 `/workspace/tmp/tip-eg-gap-path/`  
**范围：** offerPick / 无参 gappath / 周要约 → 开 `ember_p1_gap_path`（**禁** ConfirmTokens 教 gappath 为主 UI）· 薄页自动/提醒/静默/清空/技能组/冒险/工坊真开 · applyAndReply 下一步走选打薄页 · PAPI `gap_path_*` 诚实 · Q01 灰态+亮态 · **零改**伤害/CD表/掉落/体力价 · **零改** `ember_p1_phase_path` / wave / soak / shell / clone / press / cut / rush / clean / streak / blaze / spin / snap / veil / free 及其他已齐 path · **零改** D587 收招「抓空窗」战斗钮语义 · **零**新建 `ember_p1_pace_path` · **≠**已齐选打同题复读 · **tip 勿勾 CLOSED**（总控收口）

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | 静态：菜单文件存在；零改 `ember_p1_phase_path` / wave / soak / shell / clone / press / cut / rush / clean / streak / blaze / spin / snap / veil / free 及其他已齐 path | **PASS** | gap_path md5=`9602bae7717dcf218dc651f67a528a24` · phase_path md5=`b37b4dec11bfa8217d84403f9b13d4cd`（TIP-EF 基线）· wave=`0e1e18187b8687028d4ba51c13b83931` · soak/shell/clone/press/cut/rush/clean/streak/blaze/spin/snap/veil/free/dodge/sidestep/step/flex/stride/counter/pledge/trial 基线未变 · `10-menu-static` · `00-md5` · `V0_phase_path_untouched` · `V5_*` |
| 2 | jar/bv：live 1.65.857-d13364 · ECONOMY_BV 123 未抬 | **PASS** | jar sha256 `67d23267c903…` plugins=server-runtime · bv123 · `00-jar` · `V0_jar` · `V0_switches_pre` |
| 3 | 单测：`EmberGapPathPickTest` 等绿 | **PASS** | GapPathPick **8/8** · GapPath · PhasePathPick · PhasePath · Wave/Soak/Shell/Clone/Press/Cut/Rush/Clean/Streak/Blaze/Spin/Snap/Veil/Free/Dodge/Sidestep/Step/Flex/Stride/Counter/Pledge/Trial/RunPapi · `12-unit-*` · `V4_unit_path_pick_gap` · `V0_unit_mvn` |
| 4 | PAPI：q01 未通灰态 / 已通亮；`%corerpg_p1_gap_path%` / `_label` / `_tip` | **PASS** | path/label/tip/line 诚实 · Q01 锁→自动/提醒/静默/清空灰「需首通 Q01」· 通关亮态 · `20/21/30` · `V3_q01_gray` · `V2_papi_*` |
| 5 | offerPick / 无参 gappath / 周要约 → 真开 `/ember_p1_gap_path`（非 ConfirmTokens 教裸 gappath） | **PASS** | `offerPick`→`openPathPick` · 无参 gappath live 开「收招 · 路径」· nudge/周要约同开薄页 · glance「点收招路径页」· **零** ConfirmTokens 教裸 gappath（硬红门绿）· `12-unit-path-pick*` · `32-offer-noarg` · `33-weekly-offer` · `58-no-teach` · `V1_hard_red_*` |
| 6 | 点选 auto/ask/mute/clear + 技能组/冒险真开 | **PASS** | 1→auto · 2→ask · 3→mute · C clear · K→技能组 · A→冒险 · F→工坊 · `40–47` · `57-bright-reopen` |
| 7 | 局内「抓空窗」战斗钮仍 `/corerpg p1 gap`；「改路径」开薄页 | **PASS** | `[抓空窗]`仍→`/corerpg p1 gap` · 「改路径」→`/ember_p1_gap_path` · `V4_d587_combat_btn_intact` · `12-unit-path-pick` |
| 8 | 零抬伤害/CD表/掉落/体力价；零改 D587 收招语义 | **PASS** | feat 仅 GapPath/Command/Papi/薄页/单测 · 零 yml/Mythic 触碰 · top=30 · weekly_cap=3 · event_rate=0.85 · bv123 · `00-feat-zero-raise` · `70-intact` · `V5_*` |

## 旁注

- Stage2 三开 · daily_kills=2400 · ×0.97 · gate_daily=no · TrMenu **112**（含 `ember_p1_gap_path`）· jar sha256 `67d23267c903…` plugins=server-runtime · MySQL connected · bv123
- 单测 `EmberGapPathPickTest` 8/8 / `EmberGapPathTest` / PhasePathPick 等绿
- **82/82 PASS** · **tip 勿勾 CLOSED**（总控收口）
- 选打主 UI = `ember_p1_gap_path`；未教裸 gappath；D587 局内「抓空窗」战斗钮语义零改；未复读半血/增援等选打；未建 pace_path

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · weekly_cap=3 · rot weekly_cap=3 · top cost=30 · SOFT_TARGET=3 · ×0.97 未改 · bv123 · 零改伤害/CD表/掉落/体力价/phase_path/wave_path/soak_path/shell_path/clone_path/press_path/cut_path/rush_path/clean_path/streak_path/blaze_path/spin_path/snap_path/veil_path/free_path/dodge_path/sidestep_path/step_path/flex_path/stride_path/counter_path/pledge_path/trial_path/abyss·short·raid·featured·challenge path/variety/soft-reward · 零改 D587 收招战斗钮 · 零新建 pace_path · 零 sx20 · 未改玩法数值 · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB

> **测岗抽测：** PASS live · feat `c9d2b431f` · menu tip `ce0ac0d97` · tip `98f341015` · jar `1.65.857-d13364.local` · **勿勾 CLOSED**（总控收口）
