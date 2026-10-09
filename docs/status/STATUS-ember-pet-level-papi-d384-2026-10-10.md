# 状态 · D384：使魔等级 PAPI 同屏（批 A·M · P1 插件补键已交 · 菜单挂键另号）

**日期：** 2026-10-10（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-pet-level-papi-need-design-2026-10-10.md`](STATUS-ember-next-hard-debt-pet-level-papi-need-design-2026-10-10.md) @ `a869c892` · DESIGN [`DESIGN-ember-pet-level-papi-2026-10-10.md`](../design/DESIGN-ember-pet-level-papi-2026-10-10.md)  
**裁决：** **已批 A · 批 M · D384** · **本号交 `%corerpg_pet_*%` 最少集 + 可选键 + jar 并装 play** · 菜单挂键另号  
**版本：** jar **`1.65.102-d384.local`** · tip 见本 STATUS 交稿 commit · **未改** feed.* / set_bonus / enabled / migrate / bv62 / ×0.97 / K3 / TrMenu `ember_pet`

## 人话

使魔页缺的等级 / 满级 / 下一级魂尘占位已进 CoreRpg：`%corerpg_pet_level_line%` 等只读真源 `PetService`/`PlayerData`。play 已热换；菜单挂键仍由另号做。

## 键清单

| Placeholder | 空/无宠 | 有宠 |
|-------------|---------|------|
| `%corerpg_pet_active_id%` | `""` | active id |
| `%corerpg_pet_active_name%` | `未选定` | `PetDef.display` |
| `%corerpg_pet_level%` | `0` | `getPetLevel` |
| `%corerpg_pet_max_level%` | `feed.max_level` | 同左 |
| `%corerpg_pet_level_line%` | `尚无使魔` | `Lv.N/max` 或 `已满级 Lv.N` |
| `%corerpg_pet_feed_cost%` | `0` | `feedCostFor`；满级 `0` |
| `%corerpg_pet_feed_hint%` | `先解锁使魔` | `下一级需要魂尘×C` / `已满级` |
| `%corerpg_pet_power_bonus%`（可选） | `0` | `getPowerBonus` |
| `%corerpg_pet_unlocked_count%`（可选） | `0` | `petsUnlocked.size()` |

路由：`CorePapi.Section.PET` → `CorePapiPet`（**不**占 `p1_` / `gate_`；**不**挪用 `ember_level`）。

## 改动

| 文件 | 改动 |
|------|------|
| `CorePapi.java` | `Section.PET` + `PET` 键集 + route |
| `CorePapiPet.java` | 新建只读 resolve/apply |
| `CoreRpgExpansion.java` | `case PET` |
| `PetService.java` | 仅加 `getFeedMaxLevel()` 只读 getter |
| `CorePapiTest` / `CorePapiPetTest` | 路由 + 有/无宠约定 |
| TrMenu `ember_pet.yml` / `pet.yml` feed.* / ember-v1* | **未动** |

## 产物

| 项 | 值 |
|----|-----|
| jar | `/workspace/tmp/d384/CoreRpg-1.65.102-d384.local.jar` |
| jar sha256 | `e5356b09ee50ac745cfac250346c55d964d475a2105a51017fbc336c0f3739b2` |
| plugin.yml（jar 内） | `1.65.102-d384.local`（源码 `plugin.yml` 仍 1.65.97，未提交版本戳） |
| 单测 | `CorePapiTest` + `CorePapiPetTest` **PASS** |

## 装服（play）

| 步 | 结果 |
|----|------|
| 备份 | `/workspace/tmp/d384-backup-20261010035028/`（旧 jar `1.65.101-d335.local` + ember-v1.yml） |
| 换档 | `plugins/CoreRpg.jar` ← d384.local（sha 与产物一致） |
| 停启 | play 停 → 新 PID **906214**；日志 `Enabling CoreRpg v1.65.102-d384.local` · `Done (8.981s)` |
| sidecars | login **827830** · proxy / MariaDB **未动** |
| 开关 / bv | set_bonus/enabled/migrate 仍 true · bv **62**（runs）· yml 与备份 diff 空 |
| PAPI | `Successfully registered expansion: corerpg`；`papi list` 含 corerpg |

## 验收

| # | 项 | 结果 |
|---|----|------|
| P1 | 路由 `pet_*` → PET；不抢 p1_/gate_/ember_level | 单测 PASS |
| P2 | 无宠：尚无使魔 / 先解锁使魔 / level=0 | 单测 PASS |
| P3 | 有宠 mid：Lv.N/max + 魂尘 hint；满级短切 | 单测 PASS |
| P4 | Enabling `1.65.102-d384.local` | 装服 PASS |
| P5 | 零改 feed.* / set_bonus / bv / TrMenu | 本号未触 PASS |

**实机菜单同屏：** 等菜单岗挂键后，有宠号 `/papi parse me %corerpg_pet_level_line%` + 开 `ember_pet`。

## 下一号

| 号 | 岗 | 指针 |
|----|-----|------|
| **P2** | 菜单岗 | DESIGN §2.2 W1a–W1c；键已在 live 可解析；禁假写固定 Lv |

## 不动

feed.* 公式/max_level/产量 · 关观察 · 开 K3 · ×0.97 / set_bonus / bv · TrMenu · live ember-v1* · 主仓切分支 · 样本 R / Pack6

---

*D384 批 A·M · P1 使魔 PAPI 补键装 play · 菜单挂键另号 · ≠改 feed ≠关观察。*
