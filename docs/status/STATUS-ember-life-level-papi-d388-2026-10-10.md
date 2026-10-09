# 状态 · D388：生活等级 PAPI 同屏（批 A·M · P1 插件补键已交 · P2 菜单另号）

**日期：** 2026-10-10（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-life-level-papi-need-design-2026-10-10.md`](STATUS-ember-next-hard-debt-life-level-papi-need-design-2026-10-10.md) · DESIGN [`DESIGN-ember-life-level-papi-2026-10-10.md`](../design/DESIGN-ember-life-level-papi-2026-10-10.md) @ `8305129d`  
**裁决：** **已批 A · 批 M · D388** · **本号交 `%corerpg_life_*%` 最少集 + jar 并装 play** · 菜单挂键 → **另号 P2**  
**版本：** jar **`1.65.103-d388.local`** · tip 见本 STATUS 交稿 commit · **未改** life.yml 价/曲线/daily·weekly · set_bonus / enabled / migrate · bv62 · ×0.97 · K3 · TrMenu `ember_life`

## 人话

生活页缺的等级 / 经验进度 / 今日兑尘与本周孵化剩余占位已进 CoreRpg：`%corerpg_life_level_line%` 等只读真源 `LifeService`/`PlayerData`。play 已热换；菜单挂键仍由另号做。

## 计数约定（钉死）

| 键族 | 约定 |
|------|------|
| 魂尘日顶 | `soul_dust` + `soul_dust_bone` **各** `daily: 2` → **合计顶=4**；已用=`periodCount(life_soul_dust,today)+periodCount(life_soul_dust_bone,today)`；剩=`max(0,4-已用)` |
| 孵化周顶 | `pet_ashling` + `pet_cinder` **各** `weekly: 1` → **合计顶=2**；已用=`periodCount(life_pet_ashling,week)+periodCount(life_pet_cinder,week)`；剩=`max(0,2-已用)` |

常量：`CorePapiLife.SOUL_DAILY_CAP=4` · `HATCH_WEEKLY_CAP=2`。

## 键清单

| Placeholder | 含义 | 空/异常 |
|-------------|------|---------|
| `%corerpg_life_level%` | 当前生活等级 | ≥1（空服务约定 `1`） |
| `%corerpg_life_xp%` | 累计生活经验 | `0` |
| `%corerpg_life_xp_next%` | 下一级累计 xp 门槛；满级 `0` | `0` |
| `%corerpg_life_level_line%` | `生活 Lv.N（经验 x/next）` / `生活 Lv.N（已满级）` | 恒有 |
| `%corerpg_life_soul_daily_left%` | 今日兑尘剩余（合计顶 4） | `0` |
| `%corerpg_life_soul_daily_line%` | `今日兑尘 已用 a/4 · 剩 b` | 恒有 |
| `%corerpg_life_hatch_weekly_left%` | 本周孵化剩余（合计顶 2） | `0` |
| `%corerpg_life_hatch_weekly_line%` | `本周孵化 已用 a/2 · 剩 b` | 恒有 |

路由：`CorePapi.Section.LIFE` → `CorePapiLife`（**不**占 `p1_` / `gate_` / `pet_`；**不**挪用 `ember_level`）。

## 改动

| 文件 | 改动 |
|------|------|
| `CorePapi.java` | `Section.LIFE` + `LIFE` 键集 + route |
| `CorePapiLife.java` | 新建只读 resolve/apply |
| `CoreRpgExpansion.java` | `case LIFE` |
| `LifeService.java` | 仅加 `lifeXpNext()` 只读（满级→0） |
| `CoreRpgPlugin.java` | `getLifeService()` |
| `CorePapiTest` / `CorePapiLifeTest` | 路由 + 中档/满级/顶满约定 |
| TrMenu `ember_life.yml` / `life.yml` / ember-v1* | **未动** |

## 产物

| 项 | 值 |
|----|-----|
| jar | `/workspace/tmp/d388/CoreRpg-1.65.103-d388.local.jar` |
| jar sha256 | `d7b46183e59b4988824c0ca992066bfd0dfc574ca30a679e1a54124e9532d025` |
| plugin.yml（jar 内） | `1.65.103-d388.local`（源码 `plugin.yml` 仍 1.65.97，未提交版本戳） |
| 单测 | `CorePapiTest` + `CorePapiLifeTest` + `CorePapiPetTest` **PASS** |

## 装服（play）

| 步 | 结果 |
|----|------|
| 备份 | `/workspace/tmp/d388-backup-20261010040622/`（旧 jar `1.65.102-d384.local` + ember-v1.yml） |
| 换档 | `plugins/CoreRpg.jar` ← d388.local（sha 与产物一致） |
| 停启 | play 停 → 新 PID **929948**；日志 `Enabling CoreRpg v1.65.103-d388.local` · `Done (7.813s)` |
| sidecars | login **827830** · proxy / MariaDB **未动** |
| 开关 / bv | set_bonus/enabled/migrate 仍 true · bv **62**（runs）· live yml 未改 |
| PAPI | `Successfully registered expansion: corerpg` |

## 验收

| # | 项 | 结果 |
|---|----|------|
| P1 | 路由 `life_*` → LIFE；不抢 p1_/gate_/pet_/ember_level | 单测 PASS |
| P2 | 中档：level_line + 兑尘/孵化剩次文案 | 单测 PASS |
| P3 | 满级 xp_next=0；日/周顶耗尽 left=0 | 单测 PASS |
| P4 | Enabling `1.65.103-d388.local` | 装服 PASS |
| P5 | 零改 life.yml / set_bonus / bv / TrMenu | 本号未触 PASS |

**实机菜单同屏：** 等菜单岗挂键后，有进度号 `/papi parse me %corerpg_life_level_line%` + 开 `ember_life`。

## 下一号

| 号 | 岗 | 指针 |
|----|-----|------|
| **P2** | 菜单岗 | `ember_life` W1a–W1b 挂 `%corerpg_life_*_line%`（DESIGN §2.2） |

## 不动

life.yml 价/曲线/daily·weekly · 关观察 · 开 K3 · ×0.97 / set_bonus / bv · TrMenu · live ember-v1* · 主仓切分支 · 样本 R / Pack6

---

*D388 批 A·M · P1 生活 PAPI 补键装 play · 菜单挂键另号 · ≠改 life.yml ≠关观察。*
