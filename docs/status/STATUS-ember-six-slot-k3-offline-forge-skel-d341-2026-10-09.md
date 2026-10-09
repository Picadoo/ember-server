# 状态 · D341 续：K3 同部位熔炼 · 闸关离线 Forge/服务骨架（隔离 worktree · 零 live）

**日期：** 2026-10-09（上海时间）  
**上游：** 总控改派 · 闸关离线续施工（≠开闸 ≠装 play）· [`DESIGN-ember-six-slot-k3-same-slot-refine-2026-10-08.md`](../design/DESIGN-ember-six-slot-k3-same-slot-refine-2026-10-08.md) §2 · [`DESIGN-ember-six-slot-k3-build-gate-2026-10-09.md`](../design/DESIGN-ember-six-slot-k3-build-gate-2026-10-09.md) §2.2 · 前序预备 [`STATUS-ember-six-slot-k3-offline-prep-d341-2026-10-09.md`](STATUS-ember-six-slot-k3-offline-prep-d341-2026-10-09.md)（规则+单测 @ `67fdc07c`）  
**裁决：** **闸关离线续施工**；仅 worktree `/workspace/minecraft-wt-d341` · 分支 `feat/d341-k3-refine-offline`；**未**装 play；**未**开 `k3_refine`；**未**碰主仓 live yml

> **观察正文以主仓为准**（`/workspace/minecraft` · `main` · Stage2 三开关/bv62）。本 STATUS 只描述隔离分支骨架，不替代主仓观察/部署记录。

## 人话

在闸关允许的范围内，把「同部位熔炼」从纯规则接到可测的 **preview / commitIntent** 骨架：开关关一律拒；成功时产出「目标 craft=max + 材料销毁」事务意图与审计串；零价；不碰成色/族/阶/强化。仍**不**注册玩家命令、**不**上 TrMenu live、**不**装 play。

## 分支

| 项 | 值 |
|----|-----|
| worktree | `/workspace/minecraft-wt-d341`（**禁止**主仓切分支） |
| 分支名 | `feat/d341-k3-refine-offline` |
| tip | `6aac6397` (`6aac63973bf1afa6b39d0289b316305196540738`) |
| 主仓 `/workspace/minecraft` | 保持 **main**；本号未改其 tracked live yml |

## 接线摘要

| 项 | 口径 |
|----|------|
| 规则 | 仍走 `EmberK3RefineRules.plan`（craft=max · 毁材料 · 零价 · 字节级不碰 q/fam/tier/enh） |
| 服务骨架 | `EmberK3RefineService.preview` / `commitIntent` → `CommitIntent`（TxnItem×2 + audit + Cost.NONE） |
| Forge 钩子 | `EmberForgeService.k3Preview` / `k3CommitIntent` 委托上述服务；**未**挂入 `cmd()` |
| 材料 retire | `RETIRE_MATERIAL = "k3_consumed"` |
| 开关 | `gear.six_slot.k3_refine` 默认 **false**（须 enabled）；关 = 与现网一致 |
| 不动 | set_bonus / ×0.97 / 价表 / 主仓 live `ember-v1*.yml` / play jar / TrMenu |

## 文件清单

| 文件 | 改动 |
|------|------|
| `CoreRpg/.../EmberK3RefineService.java` | **新建** preview/commitIntent 骨架 |
| `CoreRpg/.../EmberK3RefineServiceTest.java` | **新建** 开关关 / 成功熔炼 / 拒绝路径 |
| `CoreRpg/.../EmberForgeService.java` | 加 `k3Preview` / `k3CommitIntent` 委托（不注册命令） |
| `docs/status/STATUS-ember-six-slot-k3-offline-forge-skel-d341-2026-10-09.md` | 本 STATUS |

前序已有（本号未重写规则）：`EmberK3RefineRules` + `EmberK3RefineRulesTest` + `EmberSixSlot.KEY_K3_REFINE`。

## 单测

| 套件 | 结果 |
|------|------|
| `EmberK3RefineServiceTest` | **6/6 PASS** |
| `EmberK3RefineRulesTest` | **13/13 PASS**（回归） |
| 合计 | **19/19 PASS** |

## 确认（钉死）

- [x] 仅在 worktree 动手；主仓未 `checkout` / `reset` / 改 `plugins/CoreRpg/ember-v1*.yml`
- [x] **未**装 play / 热更现网 jar
- [x] **未**开 `k3_refine`
- [x] **未**改 set_bonus / ×0.97 / bv / 价表
- [x] **未**把 worktree resources 拷进主仓 live plugins
- [x] 观察正文以主仓为准

## blocker

无（闸关离线骨架完成）。开闸条件未满足——属预期：等 Stage2 绿出口≥**2026-10-10 17:40 CST** 或总控另签并行；开闸后另号再挂 `commitTxn` + 菜单。
