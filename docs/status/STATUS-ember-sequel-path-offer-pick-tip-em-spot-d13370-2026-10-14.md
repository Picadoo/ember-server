# 状态 · TIP-EM 续招路径选打菜单化抽测

> **抽测 PASS · tip 未勾 CLOSED（待总控收口）** · feat `3f08561be` · tip批 `878ae0e5b` · menu `ec0cc64e1` · jar `1.65.863-d13370.local` · 2026-10-14 Asia/Shanghai · ancestor TIP-EL CLOSED live `f9291ddaf`

**裁决：** **PASS** · 2026-10-14 Asia/Shanghai · feat `3f08561be` · menu tip `ec0cc64e1` · tip/批 `878ae0e5b` · tip原文 `604cf1fdb` · docs pin `16b4f54b7` · jar `1.65.863-d13370.local` · sha12 `2b60aa134e09` · 证据 `/workspace/tmp/tip-em-sequel-path/`  
**范围：** offerPick / 无参 sequelpath / 周要约 → 开 `ember_p1_sequel_path`（**禁** ConfirmTokens 教 sequelpath 为主 UI）· 薄页自动/提醒/静默/清空/技能组/冒险/工坊真开 · applyAndReply 下一步走选打薄页 · PAPI `sequel_path_*` 诚实 · Q01 灰态+亮态 · **零改**伤害/CD表/掉落/体力价 · **零改** `ember_p1_bait_path` / slip / crash / bolt / shift / gap / phase / wave / soak / shell / clone / press / cut / rush / clean / streak / blaze / spin / snap / veil / free 及其他已齐 path · **零改** D593 续招「预站」战斗钮语义 · **零**新建 `ember_p1_pace_path` · **≠**已齐选打同题复读 · **tip 勿勾 CLOSED**（总控收口）

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | 静态：菜单 `ember_p1_sequel_path` 存在；零改 `ember_p1_bait_path` / slip / crash / bolt 及其他已齐 path | **PASS** | sequel_path md5=`6ed0772b55a5de68066aec1583d6d1ab` · bait_path md5=`3cf993a7840cb8551fbd4929df029d58`（TIP-EL 基线）· slip=`fb2ee0212dfb91dbc9435ba5bb52706f` · crash=`010e4b96a72283556916b086ce2cf0a1` · bolt=`0ff6058fccfd32d0f48c628bca74c557` · `V0_bait_path_untouched` · `V5_*` |
| 2 | jar/bv：live 1.65.863-d13370 · ECONOMY_BV 123 未抬 | **PASS** | jar sha256 `2b60aa134e09…` plugins=server-runtime · bv123 · `00-jar` · `V0_jar` · `V0_switches_pre` |
| 3 | 单测：`EmberSequelPathPickTest` 等绿 | **PASS** | SequelPathPick **8/8** · SequelPath 1 · BaitPathPick 8 · SlipPathPick 等 · RunPapi · `12-unit-*` · `V4_unit_path_pick` · `V0_unit_mvn` |
| 4 | PAPI：q01 未通灰态 / 已通亮；`%corerpg_p1_sequel_path%` / `_label` / `_tip` / `_line` | **PASS** | path/label/tip/line 诚实 · Q01 锁→自动/提醒/静默/清空灰「需首通 Q01」· 通关亮态 · `20/21/30` · `V3_q01_gray` · `V2_papi_*` |
| 5 | offerPick / 无参 sequelpath / 周要约 → 真开 `/ember_p1_sequel_path`（非 ConfirmTokens 教裸 sequelpath） | **PASS** | `offerPick`→`openPathPick` · 无参 sequelpath live 开「续招 · 路径」· nudge/周要约同开薄页 · glance「点续招路径页」· **零** ConfirmTokens 教裸 sequelpath（硬红门绿）· `12-unit-path-pick*` · `32-offer-noarg` · `33-weekly-offer` · `58-no-teach` · `V1_hard_red_*` |
| 6 | 点选 auto/ask/mute/clear + 技能组/冒险真开 | **PASS** | 1→auto · 2→ask · 3→mute · C clear · K→技能组 · A→冒险 · F→工坊 · `40–47` · `57-bright-reopen` |
| 7 | 局内「预站」战斗钮仍 `/corerpg p1 sequel`；「改路径」开薄页 | **PASS** | `[预站]`仍→`/corerpg p1 sequel` · 「改路径」→`/ember_p1_sequel_path` · `V4_d593_combat_btn_intact` · `12-unit-path-pick` |
| 8 | 零抬伤害/CD表/掉落/体力价；零改 D593 续招语义 | **PASS** | feat 仅 SequelPath/Command/Papi/薄页/单测 · 零 yml/Mythic 触碰 · top=30 · weekly_cap=3 · event_rate=0.85 · bv123 · `00-feat-zero-raise` · `70-intact` · `V5_*` |

## 旁注

- Stage2 三开 · daily_kills=2400 · ×0.97 · gate_daily=no · TrMenu **118**（含 `ember_p1_sequel_path`）· jar sha256 `2b60aa134e09…` plugins=server-runtime · MySQL connected · bv123
- 单测 `EmberSequelPathPickTest` 8/8 / `EmberSequelPathTest` / BaitPathPick 等绿
- **90/90 PASS** · **tip 勿勾 CLOSED**（总控收口）
- 选打主 UI = `ember_p1_sequel_path`；未教裸 sequelpath；D593 局内「预站」战斗钮语义零改；未复读诱空/闪招等选打；未建 pace_path

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · weekly_cap=3 · rot weekly_cap=3 · top cost=30 · SOFT_TARGET=3 · ×0.97 未改 · bv123 · 零改伤害/CD表/掉落/体力价/bait_path/slip_path/crash_path/bolt_path/shift_path/gap_path/phase_path/wave_path/soak_path/shell_path/clone_path/press_path/cut_path/rush_path/clean_path/streak_path/blaze_path/spin_path/snap_path/veil_path/free_path/dodge_path/sidestep_path/step_path/flex_path/stride_path/counter_path/pledge_path/trial_path/abyss·short·raid·featured·challenge path/variety/soft-reward · 零改 D593 续招预站战斗钮 · 零新建 pace_path · 零 sx20 · 未改玩法数值 · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB
