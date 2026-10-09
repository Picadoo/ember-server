# 状态 · D341：K3 同部位熔炼 · 闸关离线预备（隔离分支 · 零 live）

**日期：** 2026-10-09（上海时间）  
**上游：** 总控 D341 批 A·M · [`DESIGN-ember-six-slot-k3-build-gate-2026-10-09.md`](../design/DESIGN-ember-six-slot-k3-build-gate-2026-10-09.md) §2.2 允许离线预备 · [`DESIGN-ember-six-slot-k3-same-slot-refine-2026-10-08.md`](../design/DESIGN-ember-six-slot-k3-same-slot-refine-2026-10-08.md) §2 + §5.2 · 分支基线 `origin/main` @ `60090a7a`（含 D341 闸页 `e04c79be`）  
**裁决：** **闸关离线预备**（≠开闸 ≠部署）；代码+单测在 **feature 分支**；**未** merge 进 main 玩法 jar 路径以外的 live；**未装 play**；**未开** `k3_refine`

## 人话

闸还关着，但按 D341 §2.2 允许在隔离分支把 K3 熔炼规则和单测先写好。开关默认关；关着时行为与现网一致（一律拒绝）。开闸仍等 Stage2 绿出口≥**2026-10-10 17:40 CST** 或总控另签并行——本号**不开闸、不装服、不改线上 yml**。

## 分支

| 项 | 值 |
|----|-----|
| 分支名 | `feat/d341-k3-refine-offline` |
| 基线 | `60090a7a`（origin/main；含 D341 闸页 `e04c79be`） |
| tip | `e650ed7a` (`e650ed7a6515c000471e671cb2cce3142929771d`) |
| main | **未** merge 本号代码（代码留在 feature 分支） |

## 实现摘要

| 项 | 口径 |
|----|------|
| 开关 | `gear.six_slot.k3_refine` · 代码默认 **false** · 须 `gear.six_slot.enabled` |
| 规则 | `craft' = max(目标, 材料)`；材料销毁+审计串；零价；不碰 quality/family/tier/enhance/affix/签名 |
| 拒绝 | 开关关 / 非甲 / 异部位 / 不提升 / 穿着材料 / 未绑定 / v1 / 自身作材料 |
| K0 | 工坊 `ARMOR_REFUSE` **保留**；K3 是独立纯规则入口（Bukkit 提交 / 菜单后置） |
| 不动 | set_bonus / ×0.97 / F / 护符×1.0 / 价表 / 线上 `ember-v1*.yml` / play jar |

## 文件清单

| 文件 | 改动 |
|------|------|
| `CoreRpg/.../EmberSixSlot.java` | `KEY_K3_REFINE` · `k3RefineEnabled()` · `testK3Refine` |
| `CoreRpg/.../EmberK3RefineRules.java` | **新建** 纯规则 `plan` / `preview` + 文案常量 + 审计串 |
| `CoreRpg/.../EmberK3RefineRulesTest.java` | **新建** K3-1…K3-6 覆盖 |
| `docs/status/STATUS-ember-six-slot-k3-offline-prep-d341-2026-10-09.md` | 本 STATUS |

**未做（闸关故意后置）：** Bukkit `EmberForgeService` 提交事务、TrMenu 熔炼页、admin 命令接线、装 play、改运行时 yml。

## 单测

| 套件 | 结果 |
|------|------|
| `EmberK3RefineRulesTest` | **13/13 PASS** |
| 抽查 `EmberSixSetBonusTest` / `EmberSixSlotTest` / `EmberUpgradeRulesTest` | **全 PASS**（本号未改其语义） |

覆盖要点：开关关无效果；只改 craft；材料销毁标记+审计；异部位/不提升/穿着材料拒绝；零扣费且 `refineCost` 表未改；掉落阶不变 → `setProgress` 不变。

## 确认（钉死）

- [x] **未**装 play / 热更现网 jar  
- [x] **未**改线上 `plugins/CoreRpg/ember-v1*.yml`  
- [x] **未**将 `k3_refine` 设为 true（代码默认 false；资源 yml **未**写入该键）  
- [x] **未**改 set_bonus / ×0.97 / bv / 价表  
- [x] **未** stash/reset/强推  

## 开闸下一步（非本号）

1. Stage2 绿出口已签 **或** 总控另签「观察期并行施工 · K3」  
2. 另号：Bukkit 提交 + 菜单 + 测服冒烟（K3-7/K3-8）  
3. 部署另签；开关仍建议默认关，测服再开  

## blocker

无（离线预备完成）。开闸条件未满足——属预期，非本号 blocker。
