# 状态 · TIP-DH 短征路径选打菜单化抽测

**裁决：** **PASS** · feat `ec5264ef9` · menu tip `9a81a840e` · tip/批 `fbe2cce5c` · jar `1.65.832-d13339.local` · 证据 `/workspace/tmp/tip-dh-short-path/`  
**范围：** offerPick / 无参 shortpath / 周要约 → 开 `ember_p1_short_path`（**禁** ConfirmTokens 教 shortpath 为主 UI）· 薄页首通追/有奖追/后段追/清空/进下一本/回选页/冒险/工坊真开 · 选页 Icon0 开薄页不弹 ConfirmTokens · applyAndReply 下一步禁裸 enter ConfirmTokens · PAPI `short_path_*` 诚实 · Q01 灰态+亮态 · **零改** `ember_p1_short_settle` / 日表/掉落/体力价/resolve · **≠**路径追同题复读（TIP-CV）· **勿勾 tip CLOSED**（总控收口）

## 必验

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| 1 | offerPick / 无参 shortpath / 周要约 → 开 `/ember_p1_short_path`（禁 ConfirmTokens 教 shortpath 为主 UI） | **PASS** | `offerPick`→`openPathPick` · 无参 shortpath live 开「短征 · 路径追」· nudge/周要约同开薄页 · glance「点路径追页」· 无教裸 shortpath · `12-unit-path-pick*` · `32-offer-noarg` · `33-weekly-offer` · `58-no-teach` |
| 2 | 薄页真开：首通追/有奖追/后段追/清空/进下一本/回选页；选页 Icon0 开薄页不弹 ConfirmTokens | **PASS** | F→fc · D→day · L→late · C clear · E→enter short-path（SX01 真进本）· A→冒险 · G→工坊 · Z→短征选本 · Icon0「路径追」→薄页 · short Open 已清旁路 shortpath · `10-menu-static` · `34-icon0-click` · `40–48` |
| 3 | applyAndReply 下一步禁裸 enter ConfirmTokens；PAPI `short_path_*`；Q01 灰态 | **PASS** | apply 下一步 `[路径追][短征选页][工坊]` · path/label/tip/line/resolve 诚实 · Q01 锁→首通/有奖/后段/进下一本灰「需首通 Q01」· 通关亮态「首通追 · 已选」· `20/21/30` · `41-apply-next` · `V3_q01_gray` · `57-bright-reopen` |
| 4 | 零改 short_settle / 日表/掉落/体力价/resolve；≠路径追同题复读 | **PASS** | short_settle md5=`595236593155e49e1c685b89c8925950`（TIP-CW 基线）未变 · featured/challenge/raid_path md5 基线未变 · short cost=30 · weekly_cap=3 · short target=5 · top=30 · event_rate=0.85 · event_core=1 · affix_shard=2 · bv123 · SOFT_TARGET=3 · resolve 算法未动 · 本票=选打入口接线≠新建路径追页 · `00-feat-zero-raise` · `70-intact` · `90-endstate` |

## 旁注

- Stage2 三开 · daily_kills=2400 · ×0.97 · gate_daily=no · TrMenu **87**（含 `ember_p1_short_path`）· jar sha256 `2d1d760d705d…` plugins=server-runtime · MySQL connected · bv123
- 单测 `EmberShortPathPickTest`（6）/ `EmberShortPathTest`（1）/ SettleNextLoop / Settle / PathChase 绿
- 43/43 PASS · tip **勿勾 CLOSED**（总控收口）
- 选页 Icon0 确开薄页；Open 旁路 `command: corerpg p1 shortpath` 已清

## 纪律确认

six_slot enabled/migrate/set_bonus 仍 true · daily_kills=2400 · weekly_cap=3 · rot weekly_cap=3 · short cost=30 · short target=5 · top cost=30 · SOFT_TARGET=3 · ×0.97 未改 · bv123 · 零改日表/掉落/体力价/resolve/short_settle/featured·challenge·raid path/variety/soft-reward · 零 sx20 · 未改玩法数值 · 未切分支/stash/reset/force-push · 未碰 login/proxy/MariaDB

> **测岗抽测：** PASS live · feat `ec5264ef9` · menu tip `9a81a840e` · tip `fbe2cce5c` · jar `1.65.832-d13339.local` · **勿勾 tip CLOSED**（总控收口）· 2026-10-14 11:45 Asia/Shanghai
