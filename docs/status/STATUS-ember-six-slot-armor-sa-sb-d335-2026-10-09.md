# 状态 · D335：护甲套装 PAPI 短别名 armor_sa / armor_sb（TrMenu check 兼容）

**日期：** 2026-10-09（上海时间）  
**上游：** tip @ 装服前 `3e803266` · 问题：TrMenu `check papi %corerpg_p1_armor_set_active|busy%` / contains 均不命中（D329/D334 FAIL），`/papi parse` 正常  
**裁决：** **紧急 D335** · 加短别名 · **保留旧键** · 出 jar 装 play · **TrMenu 条件改写由总控另派人**  
**版本：** jar **`1.65.101-d335.local`** · tip 见本 STATUS 交稿 commit · **未改** set_bonus / enabled / migrate / bv62 / 价表 / TrMenu / ×0.97

## 人话

给菜单条件用的 `%corerpg_p1_armor_sa%` / `%corerpg_p1_armor_sb%` 已进 jar 并装上 play，数值与原来的 `armor_set_active` / `armor_set_busy` 完全相同（1/0）。旧键保留兼容。菜单侧改条件总控另派人。

## 改动

| 文件 | 改动 |
|------|------|
| `EmberSixPapi.text` | `armor_sa` ≡ `armor_set_active`；`armor_sb` ≡ `armor_set_busy`（同分支返回） |
| `EmberRunPapi` | **未改路由**：`armor_*` 前缀已进 ARMOR → SixPapi |
| 单测 | sa/sb 与 active/busy 同值；激活 / 进行中 / 未两件套 / 开关关 各盖 |
| TrMenu | **本号未改**（总控另派人改 `check papi %corerpg_p1_armor_sa% == 1` 等） |
| set_bonus / enabled / migrate / bv | **未动**（yml 与备份 diff 空） |

## PAPI

| 键 | 行为 |
|----|------|
| `%corerpg_p1_armor_set_active%` | 四件套激活 → `1`，否则 `0`（旧键保留） |
| `%corerpg_p1_armor_set_busy%` | 两件套已启、四件套未激 → `1`，否则 `0`（旧键保留） |
| `%corerpg_p1_armor_sa%` | **D335 短别名** = `armor_set_active` |
| `%corerpg_p1_armor_sb%` | **D335 短别名** = `armor_set_busy` |

开关 `gear.six_slot.enabled` 关时：四键均返回 `""`（与旧行为一致）。

## 产物

| 项 | 值 |
|----|-----|
| jar | `/workspace/tmp/d335/CoreRpg-1.65.101-d335.local.jar` |
| jar sha256 | `2bd11b903c703163ec201075793b34b53c9d9e04a524a6ac85f2461bee9e850f` |
| plugin.yml（jar 内） | `1.65.101-d335.local`（源码 `plugin.yml` 仍 1.65.97，未提交版本戳） |
| 单测 | `EmberSixSetBonusTest`（9）+ `EmberRunPapiTest`（5）+ `EmberSixRankTest` **PASS**（failures=0） |

## 装服（play）

| 步 | 结果 |
|----|------|
| 备份 | `/workspace/tmp/d335-backup-20261009195542/`（旧 jar sha `4932eaa8…` · `1.65.100-d333.local` + ember-v1.yml） |
| 换档 | `plugins/CoreRpg.jar` ← d335.local（sha 与产物一致 `2bd11b90…`） |
| 停启 | 旧 play **568465** 停 → 新 PID **580488**；日志 `Enabling CoreRpg v1.65.101-d335.local` · `Done (7.634s)` |
| sidecars | login **520591** · proxy / MariaDB **未动** |
| 开关 / bv | set_bonus/enabled/migrate 仍 true · yml 与备份 **diff 空** |
| TrMenu | **本号未 reload / 未改菜单** |

## 验收

| # | 项 | 结果 |
|---|----|------|
| P1 | `armor_sa`/`armor_sb` 路由 → ARMOR | 单测 PASS |
| P2 | sa≡active · sb≡busy（激活/忙/未两件/关） | 单测 PASS |
| P3 | Enabling `1.65.101-d335.local` | 装服 PASS |
| P4 | 旧键兼容 · 零改 TrMenu / 三开关 / bv | 本号未触 PASS |

**实机：** 总控改菜单条件后，测试岗用 `check papi %corerpg_p1_armor_sa% == 1` 等复测装备页护甲入口即可。

## 不动

TrMenu · set_bonus / enabled / migrate · bv62 · 价表 · ×0.97 · K3 · 观察期 Stage1

---

*D335 紧急 · armor_sa/sb 短别名装 play · 旧键兼容 · 菜单条件另派。*
