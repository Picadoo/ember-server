# 状态 · TIP-DF 硬本路径选打菜单化抽测

**裁决：** **PASS** · feat `5927286ca` · menu tip `bebeb7f17` · tip/批 `17d0f7ddf` · jar `1.65.830-d13337.local` · 证据 `/workspace/tmp/tip-df-challenge-path/`  
**范围：** offerPick / 无参 challengepath / 冒险 Open → 开 `ember_p1_challenge_path`（**禁**借 `ember_p1_challenge_settle` ConfirmTokens 当选打主 UI）· 薄页偏挑战/偏普通/随令/清空/冒险/挑战选图/工坊真开 · applyAndReply 下一步走选打薄页 · PAPI `challenge_path_*` lore 诚实 · Q07 灰态+亮态 · **零改** `ember_p1_challenge_settle` / `ember_p1_featured_path` · **零抬**掉落/体力价 · **勾毕 CLOSED live**（总控收口 · 2026-10-14 11:11 Asia/Shanghai）

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | offerPick / 无参 challengepath / 冒险 Open → 开 `/ember_p1_challenge_path`（禁借 settle ConfirmTokens） | **PASS** | `offerPick`→`openPathPick` · 无参 challengepath live 开「硬本 · 路径」· adventure Open 接线 `corerpg p1 challengepath` · glance/cue「点硬本路径页」· 无教裸 challengepath · 无借 settle · `12-unit-path-pick*` · `32-offer-noarg` · `33-adv-*` · `58-no-teach` |
| 2 | 薄页真开：偏挑战/偏普通/随令/清空/冒险/挑战选图/工坊 | **PASS** | 1→prefer · 2→easy · 3→follow · C clear · S→挑战选图 · A→冒险 · F→工坊 · live 点击设路径/开菜单 · `10-menu-static` · `40–47` |
| 3 | applyAndReply 下一步走选打薄页；PAPI `challenge_path_*`；Q07 灰态 | **PASS** | apply 下一步 `[硬本路径][打开冒险][挑战选图]` · path/label/tip/line 诚实 · Q07 锁→偏挑战/偏普通/随令灰「需首通 Q07」· 全通亮态「偏挑战 · 已选」· `20/21/30` · `41-apply-next` · `V3_q07_gray` · `57-bright-reopen` |
| 4 | 零改 challenge_settle / featured_path；零抬掉落/体力价 | **PASS** | chal_settle md5=`1bb2b44f389978e4236055b8c66ae097`（TIP-DA 基线）未变 · featured_path md5=`7177b843b24d18b8dc9c7681e29718a6`（TIP-DE 基线）未变 · top cost=30 · weekly_cap=3 · rot_cap=3 · raid=50 · short=5 · event_rate=0.85 · event_core=1 · affix_shard=2 · bv123 · 短征/团本/深渊/图身份 settle md5 未变 · SOFT_TARGET=3 · feat 零 yml/settle/feat_path/soft · `00-feat-zero-raise` · `70-intact` · `90-endstate` |

## 旁注

- Stage2 三开 · daily_kills=2400 · ×0.97 · gate_daily=no · TrMenu **86**（含 `ember_p1_challenge_path`）· jar sha256 `b34dc2ffa59f…` plugins=server-runtime · MySQL connected · bv123
- 单测 `EmberChallengePathPickTest`（6）/ `EmberChallengePathTest`（2）/ settle* 绿
- 37/37 PASS · tip **勾毕 CLOSED live**（总控收口）
- 选打主 UI 确认为 `ember_p1_challenge_path`，未借 `ember_p1_challenge_settle`

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · weekly_cap=3 · rot weekly_cap=3 · raid cost=50 · short target=5 · top cost=30 · SOFT_TARGET=3 · ×0.97 未改 · bv123 · 零改掉落/体力价/challenge_settle/featured_path/短征·团本·深渊·图身份 settle/variety/soft-reward · 零 sx20 · 未改玩法数值 · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB

> **测岗抽测：** PASS live · feat `5927286ca` · menu tip `bebeb7f17` · tip `17d0f7ddf` · jar `1.65.830-d13337.local` · **CLOSED live**（总控收口）· 2026-10-14 11:11 Asia/Shanghai
