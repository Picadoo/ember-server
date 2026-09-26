# STATUS · 余烬誓约 + 天赋（菜单壳 / YAML 契约 / 插件落地）

**日期：** 2026-09-12（Asia/Shanghai）  
**规格：** `DESIGN-ember-covenant-talent.md`（承接总纲 §2）  
**本岗：** 设计 + TrMenu 壳 + CoreRpg YAML stub + **插件岗命令/存档/jar 1.3.0**

---

## 已落地

| 产物 | 路径 |
|------|------|
| 设计规格 | `DESIGN-ember-covenant-talent.md` |
| Hub 入口 | `plugins/TrMenu/menus/ember_hub.yml` — 誓约/天赋打开子菜单（**已去「即将点燃」**） |
| 誓约菜单 | `plugins/TrMenu/menus/ember_covenant.yml` |
| 天赋菜单 | `plugins/TrMenu/menus/ember_talent.yml` |
| YAML stub | `plugins/CoreRpg/covenant.yml` · `talent.yml`（含 `ember_level` / `starting_talent_points`） |
| 源码资源镜像 | `CoreRpg/src/main/resources/covenant.yml` · `talent.yml` |
| 插件实现 | `CovenantService` · `TalentService` · PlayerData 字段 · PAPI · `/corerpg covenant|talent|cash` |
| NI 券 stub | `plugins/NeigeItems/Items/ember-covenant-talent.yml` |
| 部署 jar | `plugins/CoreRpg.jar` **1.3.0**（~82KB）· `_golden` 已同步 |

### 菜单行为

- 选誓约：`tell` + `/corerpg covenant set blaze|ash|warden`（命令已实装，需**重启 Paper** 后生效）
- 天赋：`tell` + `/corerpg talent` / `unlock` / `reset`
- 返回：`menu: ember_hub`

### 契约摘要

| 项 | 默认 |
|----|------|
| 誓约 ID | `blaze` 烬刃 · `ash` 灰行 · `warden` 守墓 |
| 首次选定 | 免费 |
| 洗约 | 晶钻 **80** 或 `mat_ember_covenant_reset`；**清空天赋**（点数退回） |
| 天赋硬顶 | `max_spendable_points: 20` |
| 洗点 | 日免 **1**；额外晶钻 **25** 或 `mat_ember_talent_reset` |
| 播种 | `earned==0` → `min(20, max(5, (ember_level-level_points_from+1)*points_per_level))`；默认 ember_level=10 → **9** 点可用测 |
| 命令前缀 | `/corerpg covenant …` · `/corerpg talent …` · `/corerpg cash give`（admin） |
| 聊天前缀 | `[誓约]` / `[天赋]`（设计 §5.1） |
| PAPI | `%corerpg_covenant%` `%corerpg_talent_points%` `%corerpg_talent_spent%` |

---

## 插件岗交接清单

- [x] 实现子命令：`covenant` / `covenant set` / `covenant reset` / `talent` / `talent info` / `talent unlock` / `talent reset` / `talent grant`（admin）
- [x] 读取 `covenant.yml` + `talent.yml`；挂到现有 `/corerpg reload`
- [x] 玩家 YAML 字段：`covenant`、`covenantChosenAt`、`talentPointsEarned`、`talentPointsSpent`、`talentNodes`、`talentFreeResetDate`、`talentFreeResetsUsed`、`crystalCash`、`emberLevel`
- [x] 聊天前缀对齐设计 §5.1（Bot 可断言）
- [x] 权限：`corerpg.covenant` / `corerpg.talent`（default true） / `corerpg.admin`
- [x] PAPI：`%corerpg_covenant%` `%corerpg_talent_points%` `%corerpg_talent_spent%`
- [x] NI stub：`mat_ember_covenant_reset` / `mat_ember_talent_reset`（缺定义时仍可用 crystalCash，日志 warn once）
- [x] **部署新 jar** `plugins/CoreRpg.jar` 1.3.0；**重启 Paper 后生效**（新类不可热重载）
- [x] **未覆盖**无关模块；保留 coin/sign/activity/bounty/enhance/socket/abyss/calamity

---

## 菜单岗 / 测试岗

- [ ] TrMenu reload 后 `/ember` → 誓约/天赋无「即将点燃」
- [ ] 打开子菜单、返回 hub
- [ ] **重启后** Bot/手工：`covenant set blaze` 免费；再 set 扣费；talent unlock / reset

---

## 未改

Paper · MythicMobs 释放 · AttributePlus 实伤 · enhance.yml 内容

---

## 阻塞

**需重启 Paper** 加载 CoreRpg 1.3.0 后命令才可用。菜单壳可先 TrMenu reload。
