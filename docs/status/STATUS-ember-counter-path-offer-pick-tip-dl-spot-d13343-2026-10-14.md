# 状态 · TIP-DL 破绽路径选打菜单化抽测

**裁决：** **PASS** · feat `2b7617600` · menu tip `c2b3b37bd` · tip/批 `7490a03eb` · jar `1.65.836-d13343.local` · 证据 `/workspace/tmp/tip-dl-counter-path/`  
**范围：** offerPick / 无参 counterpath / 周要约 → 开 `ember_p1_counter_path`（**禁** ConfirmTokens 教 counterpath 为主 UI）· 薄页撞墙/落空/破招/清空/进本/冒险/工坊真开 · applyAndReply 下一步走选打薄页 · PAPI `counter_path_*` 诚实 · Q02 灰态+亮态 · **零改**伤害/CD表/掉落/体力价/破绽眩晕加秒表(+0.12s) · **零改** `ember_p1_pledge_path` / `ember_p1_trial_path` 及其他已齐 path · **≠**已齐选打同题复读 · **勿勾 tip CLOSED**（总控收口）· 2026-10-14 12:58 Asia/Shanghai

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | 选撞墙/落空/破招/清空/进本/冒险真开（菜单 actions 落地） | **PASS** | 1→wall · 2→whiff · 3→break · C clear · E 进本接线 · A→冒险 · F→工坊 · `10-menu-static` · `40–47` · `57-bright-reopen` |
| 2 | offerPick / 无参 counterpath / 周要约入口开薄页（非 ConfirmTokens 教裸 counterpath） | **PASS** | `offerPick`→`openPathPick` · 无参 counterpath live 开「破绽 · 路径」· 周要约 unit+noarg 同开薄页 · glance「点破绽路径页」· **零** ConfirmTokens 教裸 counterpath（硬红门绿）· `12-unit-path-pick*` · `32-offer-noarg` · `33-weekly-offer` · `58-no-teach` · `V1_hard_red_*` |
| 3 | PAPI `counter_path_*` 可读；灰态诚实 | **PASS** | path/label/tip/line 诚实 · Q02 锁→撞墙/落空/破招/清空灰「需首进/首通 Q02」· 通关亮态「撞墙 · 已选」· apply 下一步 `[破绽路径][冒险]` · `20/21/30` · `41-apply-next` · `V3_q02_gray` |
| 4 | 零抬伤害/CD表/掉落/体力价/破绽眩晕加秒表 | **PASS** | feat 仅 CounterPath/Command/Papi/薄页/单测 · 零 yml/Mythic 触碰 · preferredStunBonusSec **仍 +0.12s** · top=30 · weekly_cap=3 · event_rate=0.85 · bv123 · `00-feat-zero-raise` · `70-intact` |
| 5 | 零改 `ember_p1_pledge_path` 及其他已齐 path | **PASS** | pledge_path md5=`88eacbfea5ccf4453a1c508de0903f59`（TIP-DK 基线）· trial_path md5=`1e5c7ecc53b471300cb6d7c6dc5a90c5` · abyss/short/raid/feat/chal 基线未变 · `00-md5` · `V0_pledge_path_untouched` · `V0_trial_path_untouched` · `V5_*` |
| 6 | 零已齐选打同题复读 | **PASS** | 本窗仅钉 CounterPath；未改 pledge/trial/abyss/short/raid/featured/challenge path · 未借 settle · `V5_not_pledge_path_reread` · `V5_not_other_path_reread` |

## 旁注

- Stage2 三开 · daily_kills=2400 · ×0.97 · gate_daily=no · TrMenu **91**（含 `ember_p1_counter_path`）· jar sha256 `8961daa00067…` plugins=server-runtime · MySQL connected · bv123
- 单测 `EmberCounterPathPickTest`（8）/ `EmberCounterPathTest` / RunPapi 绿
- 44/44 PASS · tip **勿勾 CLOSED**（总控收口）
- 选打主 UI = `ember_p1_counter_path`；未教裸 counterpath/enter；未复读誓约/试炼/深渊等选打

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · weekly_cap=3 · rot weekly_cap=3 · top cost=30 · SOFT_TARGET=3 · ×0.97 未改 · bv123 · 零改伤害/CD表/掉落/体力价/破绽眩晕加秒表(+0.12s)/pledge_path/trial_path/abyss·short·raid·featured·challenge path/variety/soft-reward · 零 sx20 · 未改玩法数值 · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB

> **测岗抽测：** PASS live · feat `2b7617600` · menu tip `c2b3b37bd` · tip `7490a03eb` · jar `1.65.836-d13343.local` · **勿勾 CLOSED**（总控收口）· 2026-10-14 12:58 Asia/Shanghai
