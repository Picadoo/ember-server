# 状态 · TIP-DK 誓约路径选打菜单化抽测

**裁决：** **PASS** · feat `42fc81f49` · menu tip `fb95767dd` · tip/批 `6ee232766` · jar `1.65.835-d13342.local` · 证据 `/workspace/tmp/tip-dk-pledge-path/`  
**范围：** offerPick / 无参 pledgepath / 周要约 → 开 `ember_p1_pledge_path`（**禁** ConfirmTokens 教 pledgepath 为主 UI）· 薄页限药/逆行/双挂/清空/誓约页/进阶/冒险/工坊真开 · applyAndReply 下一步走选打薄页 · PAPI `pledge_path_*` 诚实 · Q06 灰态+亮态 · **零改**伤害/CD表/掉落/体力价/誓约徽记表 · **零改** `ember_p1_trial_path` / `ember_p1_pledge` Layout · **≠**已齐选打同题复读 · **勾毕 CLOSED live**（总控收口 · 2026-10-14 12:41 Asia/Shanghai)

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | 选限药/逆行/双挂/清空/誓约页/进阶/冒险真开（菜单 actions 落地） | **PASS** | 1→lean · 2→reverse · 3→both · C clear · P→誓约页 · M→进阶 · A→冒险 · F→工坊 · `10-menu-static` · `40–48` · `57-bright-reopen` |
| 2 | offerPick / 无参 pledgepath / 周要约入口开薄页（非 ConfirmTokens 教裸 pledgepath） | **PASS** | `offerPick`→`openPathPick` · 无参 pledgepath live 开「誓约 · 路径」· nudge/周要约同开薄页 · glance「点誓约路径页」· **零** ConfirmTokens 教裸 pledgepath（硬红门绿）· `12-unit-path-pick*` · `32-offer-noarg` · `33-weekly-offer` · `58-no-teach` · `V1_hard_red_*` |
| 3 | PAPI `pledge_path_*` 可读；灰态诚实 | **PASS** | path/label/tip/line 诚实 · Q06/pledge 锁→限药/逆行/双挂/清空灰「需首通 Q06」· 通关亮态「限药 · 已选」· apply 下一步 `[誓约路径][誓约页][冒险]` · `20/21/30` · `41-apply-next` · `V3_q06_gray` |
| 4 | 零抬伤害/CD表/掉落/体力价/誓约徽记表 | **PASS** | feat 仅 PledgePath/Command/Papi/薄页/单测 · 零 yml/Mythic 触碰 · top=30 · weekly_cap=3 · event_rate=0.85 · bv123 · `00-feat-zero-raise` · `70-intact` |
| 5 | 零改 `ember_p1_trial_path` / `ember_p1_pledge` Layout 主交付 | **PASS** | trial_path md5=`1e5c7ecc53b471300cb6d7c6dc5a90c5`（TIP-DJ 基线）· pledge Layout md5=`aa6abcdd519a3fd141d5a609bdcfa663` 未变 · `00-md5` · `V0_trial_path_untouched` · `V0_pledge_layout_untouched` · `V5_*` |
| 6 | 零已齐选打同题复读 | **PASS** | 本窗仅钉 PledgePath；未改 trial/abyss/short/raid/featured/challenge path · 未借 settle · `V5_not_trial_path_reread` · `V5_not_other_path_reread` |

## 旁注

- Stage2 三开 · daily_kills=2400 · ×0.97 · gate_daily=no · TrMenu **90**（含 `ember_p1_pledge_path`）· jar sha256 `3ab2d2190517…` plugins=server-runtime · MySQL connected · bv123
- 单测 `EmberPledgePathPickTest`（7）/ `EmberPledgePathTest` / RunPapi（5）/ TrialPathPick（6）绿
- 45/45 PASS · tip **勾毕 CLOSED live**（总控收口）
- 选打主 UI = `ember_p1_pledge_path`；未教裸 pledgepath/pledge/modes；未复读试炼/深渊等选打

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · weekly_cap=3 · rot weekly_cap=3 · top cost=30 · SOFT_TARGET=3 · ×0.97 未改 · bv123 · 零改伤害/CD表/掉落/体力价/誓约徽记表/trial_path/pledge Layout/abyss·short·raid·featured·challenge path/variety/soft-reward · 零 sx20 · 未改玩法数值 · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB

> **测岗抽测：** PASS live · feat `42fc81f49` · menu tip `fb95767dd` · tip `6ee232766` · jar `1.65.835-d13342.local` · **CLOSED live**（总控收口）· 2026-10-14 12:41 Asia/Shanghai
