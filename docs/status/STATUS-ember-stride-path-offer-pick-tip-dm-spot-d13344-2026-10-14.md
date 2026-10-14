# 状态 · TIP-DM 步态路径选打菜单化抽测

**裁决：** **PASS** · feat `3daefff99` · menu tip `e16208193` · tip/批 `d2441d9f8` · jar `1.65.837-d13344.local` · 证据 `/workspace/tmp/tip-dm-stride-path/`  
**范围：** offerPick / 无参 stridepath / 周要约 → 开 `ember_p1_stride_path`（**禁** ConfirmTokens 教 stridepath 为主 UI）· 薄页突进踏/后撤踏/卸轻技/清空/进本/冒险/工坊真开 · applyAndReply 下一步走选打薄页 · PAPI `stride_path_*` 诚实 · Q05 灰态+亮态 · **零改**伤害/CD表/掉落/体力价 · **零改** `ember_p1_counter_path` / `ember_p1_pledge_path` / `ember_p1_trial_path` 及其他已齐 path · **≠**已齐选打同题复读 · **勾毕 CLOSED live**（总控收口 · 2026-10-14 13:19 Asia/Shanghai)

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | 选突进踏/后撤踏/卸轻技/清空/进本/冒险真开（菜单 actions 落地） | **PASS** | 1→push · 2→bail · 3→bare · C clear · E 进本真开 Q05 · A→冒险 · F→工坊 · `10-menu-static` · `40–47` · `57-bright-reopen` |
| 2 | offerPick / 无参 stridepath / 周要约入口开薄页（非 ConfirmTokens 教裸 stridepath） | **PASS** | `offerPick`→`openPathPick` · 无参 stridepath live 开「步态 · 路径」· nudge/周要约同开薄页 · glance「点步态路径页」· **零** ConfirmTokens 教裸 stridepath（硬红门绿）· `12-unit-path-pick*` · `32-offer-noarg` · `33-weekly-offer` · `58-no-teach` · `V1_hard_red_*` |
| 3 | PAPI `stride_path_*` 可读；灰态诚实 | **PASS** | path/label/tip/line 诚实 · Q05 锁→突进踏/后撤踏/卸轻技/清空灰「需 Q01+Q05」· 通关亮态「突进踏 · 已选」· apply 下一步 `[步态路径][冒险]` · `20/21/30` · `41-apply-next` · `V3_q05_gray` |
| 4 | 零抬伤害/CD表/掉落/体力价 | **PASS** | feat 仅 StridePath/Command/Papi/薄页/单测 · 零 yml/Mythic 触碰 · top=30 · weekly_cap=3 · event_rate=0.85 · bv123 · `00-feat-zero-raise` · `70-intact` |
| 5 | 零改 `ember_p1_counter_path` 及其他已齐 path | **PASS** | counter_path md5=`6a21af38f1476bed711bc135fcc66227`（TIP-DL 基线）· pledge_path=`88eacbfea5ccf4453a1c508de0903f59` · trial_path=`1e5c7ecc53b471300cb6d7c6dc5a90c5` · abyss/short/raid/feat/chal 基线未变 · `00-md5` · `V0_counter_path_untouched` · `V5_*` |
| 6 | 零已齐选打同题复读 | **PASS** | 本窗仅钉 StridePath；未改 counter/pledge/trial/abyss/short/raid/featured/challenge path · 未借 settle · `V5_not_counter_path_reread` · `V5_not_other_path_reread` |

## 旁注

- Stage2 三开 · daily_kills=2400 · ×0.97 · gate_daily=no · TrMenu **92**（含 `ember_p1_stride_path`）· jar sha256 `9c2f45172a51…` plugins=server-runtime · MySQL connected · bv123
- 单测 `EmberStridePathPickTest`（7）/ `EmberStridePathTest` / RunPapi / CounterPathPick / PledgePathPick / TrialPathPick 绿
- 46/46 PASS · tip **勾毕 CLOSED live**（总控收口）
- 选打主 UI = `ember_p1_stride_path`；未教裸 stridepath/enter/flexpath/steppath；未复读破绽/誓约/试炼等选打

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · weekly_cap=3 · rot weekly_cap=3 · top cost=30 · SOFT_TARGET=3 · ×0.97 未改 · bv123 · 零改伤害/CD表/掉落/体力价/counter_path/pledge_path/trial_path/abyss·short·raid·featured·challenge path/variety/soft-reward · 零 sx20 · 未改玩法数值 · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB

> **测岗抽测：** PASS live · feat `3daefff99` · menu tip `e16208193` · tip `d2441d9f8` · jar `1.65.837-d13344.local` · **勾毕 CLOSED live**（总控收口 · 2026-10-14 13:19 Asia/Shanghai)
