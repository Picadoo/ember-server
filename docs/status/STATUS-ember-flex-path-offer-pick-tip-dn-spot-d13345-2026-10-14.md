# 状态 · TIP-DN 轻技路径选打菜单化抽测

**裁决：** **PASS** · feat `53be7080e` · menu tip `55f8f3ea0` · tip/批 `68230fe44` · jar `1.65.838-d13345.local` · 证据 `/workspace/tmp/tip-dn-flex-path/`  
**范围：** offerPick / 无参 flexpath / 周要约 → 开 `ember_p1_flex_path`（**禁** ConfirmTokens 教 flexpath 为主 UI）· 薄页装配踏步/卸下轻技/清空/技能组/冒险/工坊真开 · applyAndReply 下一步走选打薄页 · PAPI `flex_path_*` 诚实 · Q01 灰态+亮态 · **零改**伤害/CD表/掉落/体力价 · **零改** `ember_p1_stride_path` 及其他已齐 path · **零**新建 `ember_p1_step_path` · **≠**已齐选打同题复读 · **勿勾 tip CLOSED**（总控收口 · 2026-10-14 13:36 Asia/Shanghai)

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | 选装配踏步/卸下轻技/清空/技能组/冒险真开（菜单 actions 落地） | **PASS** | 1→on · 2→off · C clear · K→技能组 · A→冒险 · F→工坊 · `10-menu-static` · `40–47` · `57-bright-reopen` |
| 2 | offerPick / 无参 flexpath / 周要约入口开薄页（非 ConfirmTokens 教裸 flexpath） | **PASS** | `offerPick`→`openPathPick` · 无参 flexpath live 开「轻技 · 路径」· nudge/周要约同开薄页 · glance「点轻技路径页」· **零** ConfirmTokens 教裸 flexpath（硬红门绿）· `12-unit-path-pick*` · `32-offer-noarg` · `33-weekly-offer` · `58-no-teach` · `V1_hard_red_*` |
| 3 | PAPI `flex_path_*` 可读；灰态诚实 | **PASS** | path/label/tip/line 诚实 · Q01 锁→装配踏步/卸下轻技/清空灰「需首进/首通 Q01」· 通关亮态「装配踏步 · 已选」· apply 下一步 `[轻技路径][冒险]` · `20/21/30` · `41-apply-next` · `V3_q01_gray` |
| 4 | 零抬伤害/CD表/掉落/体力价 | **PASS** | feat 仅 FlexPath/Command/Papi/薄页/单测 · 零 yml/Mythic 触碰 · top=30 · weekly_cap=3 · event_rate=0.85 · bv123 · `00-feat-zero-raise` · `70-intact` |
| 5 | 零改 `ember_p1_stride_path` 及其他已齐 path；零新建 step_path | **PASS** | stride_path md5=`bca2038c20c6913b536b95a77376e4b6`（TIP-DM 基线）· counter=`6a21af38…` · pledge=`88eacbfe…` · trial=`1e5c7ecc…` · abyss/short/raid/feat/chal 基线未变 · **无** `ember_p1_step_path.yml` · `00-md5` · `V0_stride_path_untouched` · `V0_no_step_path_menu` · `V5_*` |
| 6 | 零已齐选打同题复读 | **PASS** | 本窗仅钉 FlexPath；未改 stride/counter/pledge/trial/abyss/short/raid/featured/challenge path · 未借 settle · 未建 step_path · `V5_not_stride_path_reread` · `V5_not_other_path_reread` |

## 旁注

- Stage2 三开 · daily_kills=2400 · ×0.97 · gate_daily=no · TrMenu **93**（含 `ember_p1_flex_path`）· jar sha256 `ff6548cc0148…` plugins=server-runtime · MySQL connected · bv123
- 单测 `EmberFlexPathPickTest` / `EmberFlexPathTest` / RunPapi / StridePathPick / CounterPathPick / PledgePathPick / TrialPathPick 绿
- 46/46 PASS · tip **勿勾 CLOSED**（总控收口）
- 选打主 UI = `ember_p1_flex_path`；未教裸 flexpath/steppath；未复读步态/破绽/誓约/试炼等选打

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · weekly_cap=3 · rot weekly_cap=3 · top cost=30 · SOFT_TARGET=3 · ×0.97 未改 · bv123 · 零改伤害/CD表/掉落/体力价/stride_path/counter_path/pledge_path/trial_path/abyss·short·raid·featured·challenge path/variety/soft-reward · 零新建 step_path · 零 sx20 · 未改玩法数值 · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB

> **测岗抽测：** PASS live · feat `53be7080e` · menu tip `55f8f3ea0` · tip `68230fe44` · jar `1.65.838-d13345.local` · **勿勾 CLOSED**（总控收口 · 2026-10-14 13:36 Asia/Shanghai)
