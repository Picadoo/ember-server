# 状态 · TIP-DR 解锢路径选打菜单化抽测

**裁决：** **PASS** · 2026-10-14 14:55 Asia/Shanghai · feat `446b2d907` · menu tip `b1f0db650` · tip/批 `66bfe1203` · jar `1.65.842-d13349.local` · 证据 `/workspace/tmp/tip-dr-free-path/`  
**范围：** offerPick / 无参 freepath / 周要约 → 开 `ember_p1_free_path`（**禁** ConfirmTokens 教 freepath 为主 UI）· 薄页自动/提醒/静默/清空/技能组/冒险/工坊真开 · applyAndReply 下一步走选打薄页 · PAPI `free_path_*` 诚实 · Q01 灰态+亮态 · **零改**伤害/CD表/掉落/体力价 · **零改** `ember_p1_dodge_path` 及其他已齐 path · **零改** D570 禁锢解锢「解锢」战斗钮语义 · **零**新建 `ember_p1_pace_path` · **≠**已齐选打同题复读 · **勿勾 tip CLOSED**（总控收口）

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | 静态：菜单文件存在；零改 `ember_p1_dodge_path` 及其他已齐 path | **PASS** | free_path md5=`8674c12a94f5ae9c3300647790155110` · dodge_path md5=`11744d794c38c10a27f14aa874a3f593`（TIP-DQ 基线）· sidestep/step/flex/stride/counter/pledge/trial 基线未变 · `10-menu-static` · `00-md5` · `V0_dodge_path_untouched` · `V5_*` |
| 2 | jar/bv：live 1.65.842-d13349 · ECONOMY_BV 123 未抬 | **PASS** | jar sha256 `5013fc78d220…` plugins=server-runtime · bv123 · `00-jar` · `V0_jar` · `V0_switches_pre` |
| 3 | 单测：`EmberFreePathPickTest` 等绿 | **PASS** | FreePathPick/FreePath/DodgePathPick/Sidestep/Step/Flex/Stride/Counter/Pledge/Trial/RunPapi · `12-unit-*` · `V0_unit_mvn` · `V4_unit_path_pick_clean` |
| 4 | PAPI：q01 未通灰态 / 已通亮；`%corerpg_p1_free_path%` / `_label` / `_tip` | **PASS** | path/label/tip/line 诚实 · Q01 锁→自动/提醒/静默/清空灰「需首通 Q01」· 通关亮态「自动 · 已选」· `20/21/30` · `V3_q01_gray` · `V2_papi_*` |
| 5 | offerPick / 无参 freepath / 周要约 → 真开 `/ember_p1_free_path`（非 ConfirmTokens 教裸 freepath） | **PASS** | `offerPick`→`openPathPick` · 无参 freepath live 开「解锢 · 路径」· nudge/周要约同开薄页 · glance「点解锢路径页」· **零** ConfirmTokens 教裸 freepath（硬红门绿）· `12-unit-path-pick*` · `32-offer-noarg` · `33-weekly-offer` · `58-no-teach` · `V1_hard_red_*` |
| 6 | 点选 auto/ask/mute/clear + 技能组/冒险真开 | **PASS** | 1→auto · 2→ask · 3→mute · C clear · K→技能组 · A→冒险 · F→工坊 · `40–47` · `57-bright-reopen` |
| 7 | 局内「解锢」战斗钮仍 `/corerpg p1 free`；「改路径」开薄页 | **PASS** | `[解锢]`仍→`/corerpg p1 free` · 「改路径」→`/ember_p1_free_path` · `V4_d570_combat_btn_intact` · `12-unit-path-pick` |
| 8 | 零抬伤害/CD表/掉落/体力价；零改 D570 禁锢解锢语义 | **PASS** | feat 仅 FreePath/Command/Papi/薄页/单测 · 零 yml/Mythic 触碰 · top=30 · weekly_cap=3 · event_rate=0.85 · bv123 · `00-feat-zero-raise` · `70-intact` · `V5_*` |

## 旁注

- Stage2 三开 · daily_kills=2400 · ×0.97 · gate_daily=no · TrMenu **97**（含 `ember_p1_free_path`）· jar sha256 `5013fc78d220…` plugins=server-runtime · MySQL connected · bv123
- 单测 `EmberFreePathPickTest` / `EmberFreePathTest` / DodgePathPick / SidestepPathPick / StepPathPick / FlexPathPick / StridePathPick / CounterPathPick / PledgePathPick / TrialPathPick / RunPapi 绿
- 53/53 PASS · **tip 未勾 CLOSED**（总控收口）
- 选打主 UI = `ember_p1_free_path`；未教裸 freepath；D570 局内「解锢」战斗钮语义零改；未复读闪榴/闪冲/身法/轻技/步态等选打；未建 pace_path

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · weekly_cap=3 · rot weekly_cap=3 · top cost=30 · SOFT_TARGET=3 · ×0.97 未改 · bv123 · 零改伤害/CD表/掉落/体力价/dodge_path/sidestep_path/step_path/flex_path/stride_path/counter_path/pledge_path/trial_path/abyss·short·raid·featured·challenge path/variety/soft-reward · 零改 D570 禁锢解锢战斗钮 · 零新建 pace_path · 零 sx20 · 未改玩法数值 · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB

> **测岗抽测：** PASS live · feat `446b2d907` · menu tip `b1f0db650` · tip `66bfe1203` · jar `1.65.842-d13349.local` · **勿勾 tip CLOSED**（总控收口）
