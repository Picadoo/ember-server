# 状态 · TIP-DO 身法路径选打菜单化抽测

**裁决：** **PASS** · feat `bb9accd40` · menu tip `a8c288f57` · tip/批 `057b6b380` · jar `1.65.839-d13346.local` · 证据 `/workspace/tmp/tip-do-step-path/`  
**范围：** offerPick / 无参 steppath / 周要约 → 开 `ember_p1_step_path`（**禁** ConfirmTokens 教 steppath 为主 UI）· 薄页前冲/后撤/清空/进本/技能组/冒险/工坊真开 · applyAndReply 下一步走选打薄页 · PAPI `step_path_*` 诚实 · Q05 灰态+亮态 · **零改**伤害/CD表/掉落/体力价 · **零改** `ember_p1_flex_path` 及其他已齐 path · **零**新建 `ember_p1_sidestep_path` · **≠**已齐选打同题复读 · **tip 未勾 CLOSED**（总控收口）

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | 选前冲/后撤/清空/进本/技能组/冒险真开（菜单 actions 落地） | **PASS** | 1→forward · 2→back · C clear · E→进本 · K→技能组 · A→冒险 · F→工坊 · `10-menu-static` · `40–47` · `57-bright-reopen` |
| 2 | offerPick / 无参 steppath / 周要约入口开薄页（非 ConfirmTokens 教裸 steppath） | **PASS** | `offerPick`→`openPathPick` · 无参 steppath live 开「身法 · 路径」· nudge/周要约同开薄页 · glance「点身法路径页」· **零** ConfirmTokens 教裸 steppath（硬红门绿）· `12-unit-path-pick*` · `32-offer-noarg` · `33-weekly-offer` · `58-no-teach` · `V1_hard_red_*` |
| 3 | PAPI `step_path_*` 可读；灰态诚实 | **PASS** | path/label/tip/line 诚实 · Q05 锁→前冲/后撤/清空灰「需首通 Q05」· 通关亮态「前冲 · 已选」· apply 下一步 `[身法路径][冒险]` · `20/21/30` · `41-apply-next` · `V3_q05_gray` |
| 4 | 零抬伤害/CD表/掉落/体力价 | **PASS** | feat 仅 StepPath/Command/Papi/薄页/单测 · 零 yml/Mythic 触碰 · top=30 · weekly_cap=3 · event_rate=0.85 · bv123 · `00-feat-zero-raise` · `70-intact` |
| 5 | 零改 `ember_p1_flex_path` 及其他已齐 path；零新建 sidestep_path | **PASS** | flex_path md5=`cd03e07682336da05a45f85fe3c1e053`（TIP-DN 基线）· stride=`bca2038c…` · counter=`6a21af38…` · pledge=`88eacbfe…` · trial=`1e5c7ecc…` · abyss/short/raid/feat/chal 基线未变 · **无** `ember_p1_sidestep_path.yml` · `00-md5` · `V0_flex_path_untouched` · `V0_no_sidestep_path_menu` · `V5_*` |
| 6 | 零已齐选打同题复读 | **PASS** | 本窗仅钉 StepPath；未改 flex/stride/counter/pledge/trial/abyss/short/raid/featured/challenge path · 未借 settle · 未建 sidestep_path · `V5_not_flex_path_reread` · `V5_not_other_path_reread` |

## 旁注

- Stage2 三开 · daily_kills=2400 · ×0.97 · gate_daily=no · TrMenu **94**（含 `ember_p1_step_path`）· jar sha256 `d35731caef43…` plugins=server-runtime · MySQL connected · bv123
- 单测 `EmberStepPathPickTest` / `EmberStepPathTest` / RunPapi / FlexPathPick / StridePathPick / CounterPathPick / PledgePathPick / TrialPathPick 绿
- 47/47 PASS · **tip 未勾 CLOSED**（总控收口）
- 选打主 UI = `ember_p1_step_path`；未教裸 steppath/enter；未复读轻技/步态/破绽/誓约/试炼等选打

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · weekly_cap=3 · rot weekly_cap=3 · top cost=30 · SOFT_TARGET=3 · ×0.97 未改 · bv123 · 零改伤害/CD表/掉落/体力价/flex_path/stride_path/counter_path/pledge_path/trial_path/abyss·short·raid·featured·challenge path/variety/soft-reward · 零新建 sidestep_path · 零 sx20 · 未改玩法数值 · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB

> **测岗抽测：** PASS live · feat `bb9accd40` · menu tip `a8c288f57` · tip `057b6b380` · jar `1.65.839-d13346.local` · **tip 未勾 CLOSED**（总控收口 · 2026-10-14 Asia/Shanghai)
