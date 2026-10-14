# 状态 · TIP-DE 精选路径选打菜单化抽测

**裁决：** **PASS** · feat `1465d7622` · menu tip `c161fff0d` · tip/批 `c72e8a964` · jar `1.65.829-d13336.local` · 证据 `/workspace/tmp/tip-de-featured-path/`  
**范围：** offerPick / 无参 featurepath / 冒险 Open → 开 `ember_p1_featured_path` · 薄页挑战/普通/清空/进本周精选/冒险/工坊真开 · applyAndReply 下一步走薄页 · PAPI `featured_path_*` lore 诚实 · Q01/Q07 灰态+亮态 · 主 UI 不教手打 featurepath/enter · **零改** `ember_p1_featured_settle` · **零抬**掉落/体力价/周帽 · **勾毕 CLOSED live**（总控收口 · 2026-10-14 10:56 Asia/Shanghai）

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | offerPick / 无参 featurepath / 冒险 Open → 开 `/ember_p1_featured_path`（非 ConfirmTokens 教 featurepath/enter 主 UI） | **PASS** | `offerPick`→`openPathPick` · 无参 featurepath live 开薄页 · adventure Open 接线 `corerpg p1 featurepath` · glance/cue「点精选路径页」· 无教裸 featurepath/enter · `12-unit-path-pick*` · `32-offer-noarg` · `33-adv-*` · `58-no-teach` |
| 2 | 薄页真开：挑战/普通/清空/进本周精选/冒险/工坊 | **PASS** | 1→featurepath challenge · 2→normal · C clear · E→enter featured_key[+challenge] · A→冒险 · F→工坊 · live 点击设路径/开菜单 · `10-menu-static` · `40–46` |
| 3 | applyAndReply 下一步走薄页；PAPI `featured_path_*` lore 诚实；Q01/Q07 灰态 | **PASS** | apply 下一步 PATH_PICK/冒险/工坊 · path/label/tip/line 诚实 · Q01 锁灰（挑战/普通/进本）· Q01 开+Q07 锁→挑战灰「需首通 Q07」· 全通亮态 · 未选路径 E 灰 · `20/21/30/31` · `41-apply-next` · `V3_*` |
| 4 | 零改 `ember_p1_featured_settle`；零抬掉落/体力价/周帽 | **PASS** | settle md5=`5ba371c1064ee68c6f153a46d3e145ba`（TIP-DD 基线）未变 · top cost=30 · weekly_cap=3 · rot_cap=3 · raid=50 · short=5 · event_rate=0.85 · event_core=1 · affix_shard=2 · bv123 · 短征/团本/挑战/深渊/图身份 settle md5 未变 · SOFT_TARGET=3 · feat 零 yml/settle/soft · `00-feat-zero-raise` · `70-intact` · `90-endstate` |

## 旁注

- Stage2 三开 · daily_kills=2400 · ×0.97 · gate_daily=no · TrMenu **85**（含 `ember_p1_featured_path`）· jar sha256 `05551d1620bb…` plugins=server-runtime · MySQL connected · bv123
- 单测 `EmberFeaturedPathPickTest` / `EmberFeaturedPathTest` / settle* 绿
- 37/37 PASS · tip **勾毕 CLOSED live**（总控收口）
- rescored：`V4_unit_path_pick_clean.glance_no_bare` 假阳性（注释「禁教手打 /corerpg p1 featurepath」≠教指令）已剔；unit glance 断言已绿

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · weekly_cap=3 · rot weekly_cap=3 · raid cost=50 · short target=5 · top cost=30 · SOFT_TARGET=3 · ×0.97 未改 · bv123 · 零改掉落/体力价/周帽/featured_settle/短征·团本·挑战·深渊·图身份 settle/variety/soft-reward · 零 sx20 · 未改玩法数值 · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB

> **测岗抽测：** PASS live · feat `1465d7622` · menu tip `c161fff0d` · tip `c72e8a964` · jar `1.65.829-d13336.local` · **CLOSED live**（总控收口）· 2026-10-14 10:57 Asia/Shanghai
