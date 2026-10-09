# 状态 · D333：工坊手持甲菜单诚实（批 A·M · TrMenu + held_is_armor PAPI）

**日期：** 2026-10-09（上海时间）  
**上游：** tip @ `5564ea55` · DESIGN [`DESIGN-ember-six-slot-forge-armor-honesty-2026-10-09.md`](../design/DESIGN-ember-six-slot-forge-armor-honesty-2026-10-09.md) · TrMenu 落地 `ee5217d6`  
**裁决：** **已批 A · 批 M · D333** · TrMenu 已落地 · **本号交 `%corerpg_p1_held_is_armor%` + jar 并装 play**  
**版本：** jar **`1.65.100-d333.local`** · tip 见本 STATUS 交稿 commit · **未改** set_bonus / enabled / migrate / bv62 / 价表 / armorDismantleTenths / ×0.97 / K3 / TrMenu

## 人话

手持甲打开工坊时，养成格灰显分支依赖的 `%corerpg_p1_held_is_armor%` 已进 jar 并装上 play：主手可信 P1 甲 → `1`，否则 `0`。菜单条件（总控已写）现在会真正灰显。

## 改动

| 文件 | 改动 |
|------|------|
| `EmberSixPapi.heldIsArmor` | 只读：`d != null && d.isArmor()` → `"1"` / `"0"` |
| `EmberRunPapi` | LOADOUT 键 `held_is_armor` → `heldTrusted` 后调 `heldIsArmor`（与 held_* 同区） |
| 单测 | 路由 → LOADOUT；甲→1、刃/护符/null→0 |
| TrMenu `ember_p1_forge.yml` | **本号未改**（总控 `ee5217d6`） |
| set_bonus / ×0.97 / bv / 价表 / armorDismantleTenths | **未动** |

## PAPI

| 键 | 行为 |
|----|------|
| `%corerpg_p1_held_is_armor%` | 主手可信 P1 甲 → `1`；空手 / 非 P1 / 刃 / 护符 / 不可信 → `0` |

## 产物

| 项 | 值 |
|----|-----|
| jar | `/workspace/tmp/d333/CoreRpg-1.65.100-d333.local.jar` |
| jar sha256 | `4932eaa8274385df5448243124ce2394d05a5625645026f61a0cbefc9a0b9c45` |
| plugin.yml（jar 内） | `1.65.100-d333.local`（源码 `plugin.yml` 仍 1.65.97，未提交版本戳） |
| 单测 | `EmberRunPapiTest` + `EmberSixRankTest#heldIsArmorFlag` **PASS** |

## 装服（play）

| 步 | 结果 |
|----|------|
| 备份 | `/workspace/tmp/d333-backup-20261009194751/`（旧 jar sha `4c273c5a…` · `1.65.99-d325.local` + ember-v1.yml） |
| 换档 | `plugins/CoreRpg.jar` ← d333.local（sha 与产物一致） |
| 停启 | 旧 play **520664** 停 → 新 PID **568465**；日志 `Enabling CoreRpg v1.65.100-d333.local` · `Done (7.020s)` |
| sidecars | login **520591** · proxy **47617** · MariaDB **未动** |
| 开关 / bv | set_bonus/enabled/migrate 仍 true · bv **62** · yml 与备份 diff 空 |
| trmenu | `trmenu reload` → `良好 \| 70 个菜单已加载 (113 ms)` |
| 在线 | 装服前踢测试号 D332Dis / D329Retest（短重启） |

## 验收

| # | 项 | 结果 |
|---|----|------|
| P1 | 路由 `held_is_armor` → LOADOUT | 单测 PASS |
| P2 | 甲→1 · 刃/护符/null→0 | 单测 PASS |
| P3 | Enabling `1.65.100-d333.local` | 装服 PASS |
| P4 | 零改价 / set_bonus / bv / TrMenu | 本号未触 PASS |

**实机菜单灰显：** 测试岗用手持甲开 `ember_p1_forge` 看养成格灰显即可。

## 不动

×0.97 · set_bonus / enabled / migrate · bv · 价表 / armorDismantleTenths · TrMenu · K3 / D332 测试轨 · ember_set · 样本 R / Pack6

---

*D333 批 A·M · TrMenu `ee5217d6` + held_is_armor PAPI 装 play · 观察期显示 only。*
