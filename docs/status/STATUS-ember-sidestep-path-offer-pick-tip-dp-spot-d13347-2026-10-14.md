# 状态 · TIP-DP 闪冲路径选打菜单化抽测

**裁决：** **PASS** · feat `adf9efa71` · menu tip `52e6da32b` · tip/批 `116eea312` · jar `1.65.840-d13347.local` · 证据 `/workspace/tmp/tip-dp-sidestep-path/`  
**范围：** offerPick / 无参 sidesteppath / 周要约 → 开 `ember_p1_sidestep_path`（**禁** ConfirmTokens 教 sidesteppath 为主 UI）· 薄页自动/提醒/静默/清空/技能组/冒险/工坊真开 · applyAndReply 下一步走选打薄页 · PAPI `sidestep_path_*` 诚实 · Q01 灰态+亮态 · **零改**伤害/CD表/掉落/体力价 · **零改** `ember_p1_step_path` 及其他已齐 path · **零改** D572 冲锋侧移「闪冲」战斗钮语义 · **零**新建 `ember_p1_pace_path` · **≠**已齐选打同题复读 · tip **未勾 CLOSED**（总控收口）

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | offerPick / 无参 sidesteppath / 周要约 → 真开 `ember_p1_sidestep_path` | **PASS** | `offerPick`→`openPathPick` · 无参 sidesteppath live 开「闪冲 · 路径」· nudge/周要约同开薄页 · glance「点闪冲路径页」· **零** ConfirmTokens 教裸 sidesteppath（硬红门绿）· `12-unit-path-pick*` · `32-offer-noarg` · `33-weekly-offer` · `58-no-teach` · `V1_hard_red_*` |
| 2 | 选 自动/提醒/静默/清空 真写入；技能组/冒险真开 | **PASS** | 1→auto · 2→ask · 3→mute · C clear · K→技能组 · A→冒险 · F→工坊 · `10-menu-static` · `40–47` · `57-bright-reopen` |
| 3 | PAPI `sidestep_path` / `_label` / `_tip`（灰态 q01done）诚实 | **PASS** | path/label/tip/line 诚实 · Q01 锁→自动/提醒/静默/清空灰「需首通 Q01」· 通关亮态「自动 · 已选」· apply 下一步 `[闪冲路径][冒险]` · `20/21/30` · `41-apply-next` · `V3_q01_gray` |
| 4 | 零抬伤害/CD表/掉落/体力价；零改 `ember_p1_step_path`；零改 D572「闪冲」战斗钮语义 | **PASS** | feat 仅 SidestepPath/Command/Papi/薄页/单测 · 零 yml/Mythic 触碰 · top=30 · weekly_cap=3 · event_rate=0.85 · bv123 · step_path md5=`b0a6e911706629676e2a174992a6192f`（TIP-DO 基线）· `[闪冲]`仍→`/corerpg p1 sidestep` · 「改路径」→薄页 · `00-feat-zero-raise` · `70-intact` · `V4_d572_*` |
| 5 | ConfirmTokens 不再教裸 sidesteppath 为主 UI | **PASS** | offer/apply/usage/glance 清裸教 · live 无 ConfirmTokens 教 auto|ask|mute|clear · `V1_hard_red_no_bare_confirm_tokens` · `V4_no_teach_bare_main_ui` · `58-no-teach` |

## 旁注

- Stage2 三开 · daily_kills=2400 · ×0.97 · gate_daily=no · TrMenu **95**（含 `ember_p1_sidestep_path`）· jar sha256 `ed84c0233c55…` plugins=server-runtime · MySQL connected · bv123
- 单测 `EmberSidestepPathPickTest` / `EmberSidestepPathTest` / RunPapi / StepPathPick / FlexPathPick / StridePathPick / CounterPathPick / PledgePathPick / TrialPathPick 绿
- 49/49 PASS · tip **未勾 CLOSED**（总控收口）
- 选打主 UI = `ember_p1_sidestep_path`；未教裸 sidesteppath；D572 局内「闪冲」战斗钮语义零改；未复读身法/轻技/步态/破绽等选打；未建 pace_path

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · weekly_cap=3 · rot weekly_cap=3 · top cost=30 · SOFT_TARGET=3 · ×0.97 未改 · bv123 · 零改伤害/CD表/掉落/体力价/step_path/flex_path/stride_path/counter_path/pledge_path/trial_path/abyss·short·raid·featured·challenge path/variety/soft-reward · 零改 D572 冲锋侧移战斗钮 · 零新建 pace_path · 零 sx20 · 未改玩法数值 · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB

> **测岗抽测：** PASS live · feat `adf9efa71` · menu tip `52e6da32b` · tip `116eea312` · jar `1.65.840-d13347.local` · tip **未勾 CLOSED**（总控收口 · 2026-10-14 14:21 Asia/Shanghai)
