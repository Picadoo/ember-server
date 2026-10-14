# 状态 · TIP-DI 深渊路径选打菜单化抽测

**裁决：** **PASS** · feat `724f53462` · menu tip `27541e2cb` · tip/批 `39e38c8b1` · jar `1.65.833-d13340.local` · 证据 `/workspace/tmp/tip-di-abyss-path/`  
**范围：** offerPick / 无参 abysspath / 周要约 → 开 `ember_p1_abyss_path`（**禁** ConfirmTokens 教 abysspath 为主 UI）· 薄页冲层/刷层/清空/深渊表/冒险/工坊真开 · applyAndReply 下一步走选打薄页 · PAPI `abyss_path_*` 诚实 · Q07 灰态+亮态 · **零改** `ember_p1_abyss_settle` / 层费/强度/掉落/体力价 · **≠**短征选打同题复读（TIP-DH）· **勿勾 tip CLOSED**（总控收口）

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | offerPick / 无参 abysspath / 周要约 → 开 `/ember_p1_abyss_path`（禁 ConfirmTokens 教 abysspath 为主 UI） | **PASS** | `offerPick`→`openPathPick` · 无参 abysspath live 开「深渊 · 路径」· nudge/周要约同开薄页 · glance「点深渊路径页」· 无教裸 abysspath · `12-unit-path-pick*` · `32-offer-noarg` · `33-weekly-offer` · `58-no-teach` |
| 2 | 薄页真开：冲层/刷层/清空/深渊表/冒险/可选工坊 | **PASS** | 1→push · 2→farm · C clear · S→`ember_p1_abyss` · A→冒险 · F→工坊 · Z close · `10-menu-static` · `40–47` |
| 3 | applyAndReply 下一步走选打薄页；PAPI `abyss_path_*`；Q07 灰态 | **PASS** | apply 下一步 `[深渊路径][深渊表][冒险]` · path/label/tip/line 诚实 · Q07 锁→冲层/刷层/清空灰「需首通 Q07」· 通关亮态「冲层 · 已选」· `20/21/30` · `41-apply-next` · `V3_q07_gray` · `57-bright-reopen` |
| 4 | 零改 abyss_settle / 层费/强度/掉落/体力价；零短征选打同题复读 | **PASS** | abyss_settle md5=`c31a4dd82173b5cfbc3864039ae82152`（TIP-DB 基线）未变 · short_path md5=`0ed41104fff4df791d5a7a95faaebd68`（TIP-DH 基线）未变 · fee 表 0/60/…/480 · fee_mark=200 · hp1=1.00 · dmg10=1.42 · weekly_cap=3 · top=30 · event_rate=0.85 · bv123 · `00-feat-zero-raise` · `00-abyss-fees` · `70-intact` · `90-endstate` |

## 旁注

- Stage2 三开 · daily_kills=2400 · ×0.97 · gate_daily=no · TrMenu **88**（含 `ember_p1_abyss_path`）· jar sha256 `9e9754e7de9f…` plugins=server-runtime · MySQL connected · bv123
- 单测 `EmberAbyssPathPickTest` / `EmberAbyssPathTest` / SettleNextLoop / RunPapi 绿
- 39/39 PASS · tip **未勾 CLOSED**（总控收口）
- 选打主 UI = `ember_p1_abyss_path`；未借 settle ConfirmTokens；未复读短征选打

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · weekly_cap=3 · rot weekly_cap=3 · top cost=30 · SOFT_TARGET=3 · ×0.97 未改 · bv123 · 零改层费/强度/掉落/体力价/层表/abyss_settle/short_path/raid·featured·challenge path/variety/soft-reward · 零 sx20 · 未改玩法数值 · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB

> **测岗抽测：** PASS live · feat `724f53462` · menu tip `27541e2cb` · tip `39e38c8b1` · jar `1.65.833-d13340.local` · **勿勾 CLOSED** · 2026-10-14 12:00 Asia/Shanghai
