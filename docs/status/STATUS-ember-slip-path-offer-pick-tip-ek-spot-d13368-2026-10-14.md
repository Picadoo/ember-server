# 状态 · TIP-EK 闪招路径选打菜单化抽测

> **抽测 PASS · tip 未勾 CLOSED（待总控收口）** · feat `606eea48f` · tip批 `5119931a6` · menu `dbd986adb` · jar `1.65.861-d13368.local` · 2026-10-14 Asia/Shanghai · ancestor TIP-EJ CLOSED live `9a81d121a`

**裁决：** **PASS** · 2026-10-14 Asia/Shanghai · feat `606eea48f` · menu tip `dbd986adb` · tip/批 `5119931a6` · jar `1.65.861-d13368.local` · sha12 `2ebd751e4375` · 证据 `/workspace/tmp/tip-ek-slip-path/`  
**范围：** offerPick / 无参 slippath / 周要约 → 开 `ember_p1_slip_path`（**禁** ConfirmTokens 教 slippath 为主 UI）· 薄页自动/提醒/静默/清空/技能组/冒险/工坊真开 · applyAndReply 下一步走选打薄页 · PAPI `slip_path_*` 诚实 · Q01 灰态+亮态 · **零改**伤害/CD表/掉落/体力价 · **零改** `ember_p1_crash_path` / bolt / shift / gap / phase / wave / soak / shell / clone / press / cut / rush / clean / streak / blaze / spin / snap / veil / free 及其他已齐 path · **零改** D591 闪招战斗钮语义 · **零**新建 `ember_p1_pace_path` · **≠**已齐选打同题复读 · **tip 勿勾 CLOSED**（总控收口）

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | 静态：菜单文件存在；零改 `ember_p1_crash_path` / bolt / shift / gap / phase / wave / soak / shell / clone / press / cut / rush / clean / streak / blaze / spin / snap / veil / free 及其他已齐 path | **PASS** | slip_path md5=`fb2ee0212dfb91dbc9435ba5bb52706f` · crash_path md5=`010e4b96a72283556916b086ce2cf0a1`（TIP-EJ 基线）· bolt=`0ff6058fccfd32d0f48c628bca74c557` · 其余基线未变 · `10-menu-static` · `V0_crash_path_untouched` · `V5_*` |
| 2 | jar/bv：live 1.65.861-d13368 · ECONOMY_BV 123 未抬 | **PASS** | jar sha256 `2ebd751e4375…` plugins=server-runtime · bv123 · `00-jar` · `V0_jar` · `V0_switches_pre` |
| 3 | 单测：`EmberSlipPathPickTest` 等绿 | **PASS** | SlipPathPick **8/8** · SlipPath 1 · CrashPathPick 8 · RunPapi 5 · `12-unit-*` · `V4_unit_path_pick` · `V0_unit_mvn` |
| 4 | PAPI：q01 未通灰态 / 已通亮；`%corerpg_p1_slip_path%` / `_label` / `_tip` / `_line` | **PASS** | path/label/tip/line 诚实 · Q01 锁→自动/提醒/静默/清空灰「需首通 Q01」· 通关亮态 · `20/21/30` · `V3_q01_gray` · `V2_papi_*` |
| 5 | offerPick / 无参 slippath / 周要约 → 真开 `/ember_p1_slip_path`（非 ConfirmTokens 教裸 slippath） | **PASS** | `offerPick`→`openPathPick` · 无参 slippath live 开「闪招 · 路径」· nudge/周要约同开薄页 · glance「点闪招路径页」· **零** ConfirmTokens 教裸 slippath（硬红门绿）· `12-unit-path-pick*` · `32-offer-noarg` · `33-weekly-offer` · `58-no-teach` · `V1_hard_red_*` |
| 6 | 点选 auto/ask/mute/clear + 技能组/冒险真开 | **PASS** | 1→auto · 2→ask · 3→mute · C clear · K→技能组 · A→冒险 · F→工坊 · `40–47` · `57-bright-reopen` |
| 7 | 局内「闪招」战斗钮仍 `/corerpg p1 slip`；「改路径」开薄页 | **PASS** | `[闪招]`仍→`/corerpg p1 slip` · 「改路径」→`/ember_p1_slip_path` · `V4_d591_combat_btn_intact` · `12-unit-path-pick` |
| 8 | 零抬伤害/CD表/掉落/体力价；零改 D591 闪招语义 | **PASS** | feat 仅 SlipPath/Command/Papi/薄页/单测 · 零 yml/Mythic 触碰 · top=30 · weekly_cap=3 · event_rate=0.85 · bv123 · `00-feat-zero-raise` · `70-intact` · `V5_*` |

## 旁注

- Stage2 三开 · daily_kills=2400 · ×0.97 · gate_daily=no · TrMenu **116**（含 `ember_p1_slip_path`）· jar sha256 `2ebd751e4375…` plugins=server-runtime · MySQL connected · bv123
- 单测 `EmberSlipPathPickTest` 8/8 / `EmberSlipPathTest` / CrashPathPick 等绿
- **88/88 PASS** · **tip 勿勾 CLOSED**（总控收口）
- 选打主 UI = `ember_p1_slip_path`；未教裸 slippath；D591 局内「闪招」战斗钮语义零改；未复读撞墙/闪线等选打；未建 pace_path

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · weekly_cap=3 · rot weekly_cap=3 · top cost=30 · SOFT_TARGET=3 · ×0.97 未改 · bv123 · 零改伤害/CD表/掉落/体力价/crash_path/bolt_path/shift_path/gap_path/phase_path/wave_path/soak_path/shell_path/clone_path/press_path/cut_path/rush_path/clean_path/streak_path/blaze_path/spin_path/snap_path/veil_path/free_path/dodge_path/sidestep_path/step_path/flex_path/stride_path/counter_path/pledge_path/trial_path/abyss·short·raid·featured·challenge path/variety/soft-reward · 零改 D591 闪招战斗钮 · 零新建 pace_path · 零 sx20 · 未改玩法数值 · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB
