# STATUS · TIP-CJ W1a 挂机庭「顺手推荐花」聚合 PAPI · D13315

> 2026-10-14 00:35 Asia/Shanghai · 余烬-插件岗

## 交付
- `%corerpg_p1_afk_rec_flower%` / `_name` / `_kind`（enhance|upgrade|refine|quality|empty）/ `_ok`（非空→1；TrMenu 灰态单键）
- `/corerpg p1 enter afk-rec-flower`（及 `afk_rec_flower` / `rec-flower`）→ **仅预览**既有 `enhance` / `upgrade` / `refine craft` / `refine quality`；**禁** confirm / 静默扣
- 优先序钉死：enhance（`recipe_gap_enhance1==0` 且 enhanceCheck ok）→ upgrade（非护甲·闸过·lack 空）→ refine → quality → empty
- 护甲手持：跳过 upgrade/refine/quality；强化也不可 → empty；`_ok=0` 诚实 tell
- 单测：`EmberAfkRecFlowerTest` 四态 kind / 护甲跳过 / empty / ok·优先序；`EmberRunPapiTest` 路由四键（`afk_rec_flower*`→LOADOUT，先于 `afk_`→GROWTH）
- jar：`1.65.811-d13315.local` · sha256 `edbb347028fd…` · ECONOMY_BV **123** 未动 · **零改** UpgradeRules / afk.tiers / daily_kills / 价
- play：live `1.65.811-d13315.local` · storage=mysql · bv123 · MySQL connected

## 键清单
| 键 | 口径 |
|----|------|
| `afk_rec_flower` | 短行总览（`§a推荐·强化+1` / `§a推荐·升阶` / … / `§8暂无仓够花`） |
| `afk_rec_flower_name` | 显示名（强化/升阶/精工/成色；无→空） |
| `afk_rec_flower_kind` | enhance\|upgrade\|refine\|quality\|empty |
| `afk_rec_flower_ok` | 非空→1 否则 0（TrMenu 单条件灰） |
| enter `afk-rec-flower` | 派发既有预览命令（非 confirm）；空→诚实拒 |

## 落点
- `EmberAfkRecFlower.pick` / `ok` / `name` / `line` / `Pick`
- `EmberRunPapi.route`（`afk_rec_flower*` 先于 `afk_`→GROWTH）+ `loadout` → `afkRecFlower` / `resolveAfkRecFlower`
- `EmberRunService` case enter 别名 → `forge.cmd` 预览（无 go/confirm）

## 零改边界
- UpgradeRules / afk.tiers / daily_kills / 价表
- 第五花料命令 / 一键静默 confirm
- balance_version 123 / SoftPity / Stage2
- 菜单（物品 W1b/c 另派）· **不撤** E/e/U/u/R/r/Q/q

## 下步
- 物品 W1b/c（庭内 P 格 + Open 半行）等键
