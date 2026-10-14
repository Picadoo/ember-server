# 状态 · TIP-DJ 试炼路径选打菜单化抽测

**裁决：** **PASS** · feat `845a99920` · menu tip `9dabb653e` · tip/批 `dbbc499b0` · jar `1.65.834-d13341.local` · 证据 `/workspace/tmp/tip-dj-trial-path/`  
**范围：** offerPick / 无参 trialpath / 周要约 → 开 `ember_p1_trial_path`（**禁** ConfirmTokens 教 trialpath 为主 UI）· 薄页稳慎/猛开/加压/清空/冒险/工坊真开 · applyAndReply 下一步走选打薄页 · PAPI `trial_path_*` 诚实 · Q06 灰态+亮态 · **零改**伤害/CD表/掉落/体力价 · **零改** `ember_p1_abyss_path` 及其他已齐 path 选打页 · **≠**已齐选打同题复读 · **勾毕 CLOSED live**（总控收口 · 2026-10-14 12:20 Asia/Shanghai）

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | 选稳慎/猛开/加压/清空/冒险真开（菜单 actions 落地） | **PASS** | 1→cautious · 2→aggressive · 3→pressure · C clear · A→冒险 · F→工坊 · E 进本接线 · `10-menu-static` · `40–47` · `57-bright-reopen` |
| 2 | offerPick / 无参 trialpath / 周要约入口开薄页（非 ConfirmTokens 教裸 trialpath） | **PASS** | `offerPick`→`openPathPick` · 无参 trialpath live 开「试炼 · 路径」· nudge/周要约同开薄页 · glance「点试炼路径页」· 无教裸 trialpath · `12-unit-path-pick*` · `32-offer-noarg` · `33-weekly-offer` · `58-no-teach` |
| 3 | PAPI `trial_path_*` 可读；灰态诚实 | **PASS** | path/label/tip/line 诚实 · Q06/pledge 锁→稳慎/猛开/加压/清空灰「需首通 Q06」· 通关亮态「稳慎 · 已选」· apply 下一步 `[试炼路径][冒险][工坊]` · `20/21/30` · `41-apply-next` · `V3_q06_gray` |
| 4 | 零抬伤害/CD表/掉落/体力价 | **PASS** | feat 仅 TrialPath/Command/Papi/薄页/单测 · 零 yml/Mythic 触碰 · top=30 · weekly_cap=3 · event_rate=0.85 · bv123 · `00-feat-zero-raise` · `70-intact` |
| 5 | 零改 `ember_p1_abyss_path` 及其他已齐 path 选打页 | **PASS** | abyss_path md5=`504d837822496466afd221ab20f02912`（TIP-DI 基线）· short=`0ed41104fff4df791d5a7a95faaebd68` · raid/feat/chal 基线未变 · `00-md5` · `V0_abyss_path_untouched` · `V5_*` |
| 6 | 零已齐选打同题复读 | **PASS** | 本窗仅钉 TrialPath；未改 abyss/short/raid/featured/challenge path · 未借 settle · `V5_not_abyss_path_reread` · `V5_not_other_path_reread` |

## 旁注

- Stage2 三开 · daily_kills=2400 · ×0.97 · gate_daily=no · TrMenu **89**（含 `ember_p1_trial_path`）· jar sha256 `91e07896d2fe…` plugins=server-runtime · MySQL connected · bv123
- 单测 `EmberTrialPathPickTest`（6）/ `EmberTrialPathTest` / RunPapi / AbyssPathPick 绿
- 39/39 PASS · tip **勾毕 CLOSED live**（总控收口）
- 选打主 UI = `ember_p1_trial_path`；未教裸 trialpath/pledgepath/counterpath；未复读深渊/短征等选打

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · weekly_cap=3 · rot weekly_cap=3 · top cost=30 · SOFT_TARGET=3 · ×0.97 未改 · bv123 · 零改伤害/CD表/掉落/体力价/abyss_path/short·raid·featured·challenge path/variety/soft-reward · 零 sx20 · 未改玩法数值 · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB

> **测岗抽测：** PASS live · feat `845a99920` · menu tip `9dabb653e` · tip `dbbc499b0` · jar `1.65.834-d13341.local` · **CLOSED live**（总控收口）· 2026-10-14 12:20 Asia/Shanghai
