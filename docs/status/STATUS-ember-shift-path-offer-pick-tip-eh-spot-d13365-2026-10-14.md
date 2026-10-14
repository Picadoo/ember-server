# 状态 · TIP-EH 侧安路径选打菜单化抽测

> **抽测 PASS · tip 未勾 CLOSED（待总控收口）** · feat `65ae19a37` · tip批 `b42ce4a52` · menu `f2c690343` · jar `1.65.858-d13365.local` · 2026-10-14 Asia/Shanghai · ancestor TIP-EG CLOSED live `10d06ab27`

**裁决：** **PASS** · 2026-10-14 Asia/Shanghai · feat `65ae19a37` · menu tip `f2c690343` · tip/批 `b42ce4a52` · jar `1.65.858-d13365.local` · sha12 `6283171354d1` · 证据 `/workspace/tmp/tip-eh-shift-path/`  
**范围：** offerPick / 无参 shiftpath / 周要约 → 开 `ember_p1_shift_path`（**禁** ConfirmTokens 教 shiftpath 为主 UI）· 薄页自动/提醒/静默/清空/技能组/冒险/工坊真开 · applyAndReply 下一步走选打薄页 · PAPI `shift_path_*` 诚实 · Q01 灰态+亮态 · **零改**伤害/CD表/掉落/体力价 · **零改** `ember_p1_gap_path` / phase / wave / soak / shell / clone / press / cut / rush / clean / streak / blaze / spin / snap / veil / free 及其他已齐 path · **零改** D588 侧安「左侧」战斗钮语义 · **零**新建 `ember_p1_pace_path` · **≠**已齐选打同题复读 · **tip 勿勾 CLOSED**（总控收口）

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | 静态：菜单文件存在；零改 `ember_p1_gap_path` / phase / wave / soak / shell / clone / press / cut / rush / clean / streak / blaze / spin / snap / veil / free 及其他已齐 path | **PASS** | shift_path md5=`eec992760b0892ec9d2df3958420185e` · gap_path md5=`9602bae7717dcf218dc651f67a528a24`（TIP-EG 基线）· phase=`b37b4dec11bfa8217d84403f9b13d4cd` · wave/soak/shell/clone/press/cut/rush/clean/streak/blaze/spin/snap/veil/free/dodge/sidestep/step/flex/stride/counter/pledge/trial 基线未变 · `10-menu-static` · `00-md5` · `V0_gap_path_untouched` · `V5_*` |
| 2 | jar/bv：live 1.65.858-d13365 · ECONOMY_BV 123 未抬 | **PASS** | jar sha256 `6283171354d1…` plugins=server-runtime · bv123 · `00-jar` · `V0_jar` · `V0_switches_pre` |
| 3 | 单测：`EmberShiftPathPickTest` 等绿 | **PASS** | ShiftPathPick **8/8** · ShiftPath · GapPathPick · GapPath · Wave/Soak/Shell/Clone/Press/Cut/Rush/Clean/Streak/Blaze/Spin/Snap/Veil/Free/Dodge/Sidestep/Step/Flex/Stride/Counter/Pledge/Trial/RunPapi · `12-unit-*` · `V4_unit_path_pick_shift` · `V0_unit_mvn` |
| 4 | PAPI：q01 未通灰态 / 已通亮；`%corerpg_p1_shift_path%` / `_label` / `_tip` | **PASS** | path/label/tip/line 诚实 · Q01 锁→自动/提醒/静默/清空灰「需首通 Q01」· 通关亮态 · `20/21/30` · `V3_q01_gray` · `V2_papi_*` |
| 5 | offerPick / 无参 shiftpath / 周要约 → 真开 `/ember_p1_shift_path`（非 ConfirmTokens 教裸 shiftpath） | **PASS** | `offerPick`→`openPathPick` · 无参 shiftpath live 开「侧安 · 路径」· nudge/周要约同开薄页 · glance「点侧安路径页」· **零** ConfirmTokens 教裸 shiftpath（硬红门绿）· `12-unit-path-pick*` · `32-offer-noarg` · `33-weekly-offer` · `58-no-teach` · `V1_hard_red_*` |
| 6 | 点选 auto/ask/mute/clear + 技能组/冒险真开 | **PASS** | 1→auto · 2→ask · 3→mute · C clear · K→技能组 · A→冒险 · F→工坊 · `40–47` · `57-bright-reopen` |
| 7 | 局内「左侧」战斗钮仍 `/corerpg p1 shift`；「改路径」开薄页 | **PASS** | `[左侧]`仍→`/corerpg p1 shift` · 「改路径」→`/ember_p1_shift_path` · `V4_d588_combat_btn_intact` · `12-unit-path-pick` |
| 8 | 零抬伤害/CD表/掉落/体力价；零改 D588 侧安语义 | **PASS** | feat 仅 ShiftPath/Command/Papi/薄页/单测 · 零 yml/Mythic 触碰 · top=30 · weekly_cap=3 · event_rate=0.85 · bv123 · `00-feat-zero-raise` · `70-intact` · `V5_*` |

## 旁注

- Stage2 三开 · daily_kills=2400 · ×0.97 · gate_daily=no · TrMenu **113**（含 `ember_p1_shift_path`）· jar sha256 `6283171354d1…` plugins=server-runtime · MySQL connected · bv123
- 单测 `EmberShiftPathPickTest` 8/8 / `EmberShiftPathTest` / GapPathPick 等绿
- **84/84 PASS** · **tip 勿勾 CLOSED**（总控收口）
- 选打主 UI = `ember_p1_shift_path`；未教裸 shiftpath；D588 局内「左侧」战斗钮语义零改；未复读收招/半血等选打；未建 pace_path

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · weekly_cap=3 · rot weekly_cap=3 · top cost=30 · SOFT_TARGET=3 · ×0.97 未改 · bv123 · 零改伤害/CD表/掉落/体力价/gap_path/phase_path/wave_path/soak_path/shell_path/clone_path/press_path/cut_path/rush_path/clean_path/streak_path/blaze_path/spin_path/snap_path/veil_path/free_path/dodge_path/sidestep_path/step_path/flex_path/stride_path/counter_path/pledge_path/trial_path/abyss·short·raid·featured·challenge path/variety/soft-reward · 零改 D588 侧安战斗钮 · 零新建 pace_path · 零 sx20 · 未改玩法数值 · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB
