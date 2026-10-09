# 状态 · D343：Stage2 配置覆盖恢复（six_slot 三 true + bv62 钉进 git）

**日期：** 2026-10-09（上海时间）  
**上游：** D342 预检红停报 [`STATUS-ember-six-slot-observe-state-check-d342-precheck-RED-2026-10-09.md`](STATUS-ember-six-slot-observe-state-check-d342-precheck-RED-2026-10-09.md) · D325 live bv62 · jar `1.65.101-d335.local`  
**裁决：** **已恢复并钉死 tracked yml** · **≠关观察** · **≠开 K3 live**  
**版本：** live `plugins/CoreRpg/ember-v1.yml` + `ember-v1-runs.yml` · `CoreRpg/src/main/resources/` 同文 · STATUS/旁注

## 人话

约 **21:00 / 21:03 CST**，主工作区在 `feat/d341-k3-refine-offline` ↔ `main` 之间 **checkout 切分支**，把 **git 跟踪的** `plugins/CoreRpg/ember-v1.yml` / `ember-v1-runs.yml` 盖回旧默认：无 `gear.six_slot`（缺省 false）、`balance_version: 60`。D342 只读预检抓到 **R1+R2 红**。总控/执行已从 `d335-backup` 恢复三 true，并把 bv 拉回 **62**；本号把 **live + src/resources 同文钉进 main**，避免再被 checkout 冲掉。

## 事件时间线

| 时点 (CST) | 事 |
|------------|----|
| ~20:59 | D342 预检开跑：当时三开关仍 true |
| ~21:00:03 | 第一次覆盖：live 与 src 同文无 `six_slot`、bv→60（疑 checkout） |
| ~21:02 | 总控从 `/workspace/tmp/d335-backup-20261009195542/ember-v1.yml` 恢复；坏文件备份 `d343-restore-20261009210239/` |
| ~21:03 | 再次被 checkout 冲掉（同根因） |
| ~21:04+ | 再恢复；插件恢复副本 `d343-plugin-restore-20261009210456/`（NOTE：d335 yml + bv62 runs） |
| 本号 | 核对失败后再钉；**commit+push** live+src 同文；D342 红停报另存；观察续 |

## 根因

1. **`plugins/CoreRpg/ember-v1.yml` / `ember-v1-runs.yml` 被 git 跟踪**，且 main 历史上 **无** `gear.six_slot`、bv 停在 **60**。  
2. 插件岗在 **主工作区** `checkout` / `switch` **feat/d341 ↔ main** 时，git 按分支树重写工作区 → **直接覆盖 live 真源**。  
3. 非「reload 从 jar 资源吐出」单一路径；**分支切换本身就会盖 live**。src/resources 旧默认无 six_slot 只会在「用资源覆盖 live」时加重，但本夜主因是 **tracked plugins 路径 + 主仓切分支**。

## 恢复步骤（已做）

1. 自 `d335-backup` / `d343-plugin-restore` 恢复 `ember-v1.yml` → `gear.six_slot.enabled/migrate/set_bonus: true`（md5 **`48b963229dd261d0036cc8e7e65c3f8e`**）。  
2. `ember-v1-runs.yml` → **`balance_version: 62`**，注释补 61/62 史。  
3. **同步** `CoreRpg/src/main/resources/ember-v1.yml` 与 `ember-v1-runs.yml` 与 live **同文**（防再冲）。  
4. **commit + push origin main**（本 STATUS 同号）。  
5. 需要时 `/corerpg reload` 使内存与盘一致（二次 reload 不得再冲文件）。

## 现态核对（签字时）

| 项 | 值 |
|----|-----|
| `gear.six_slot.enabled` | **true** |
| `gear.six_slot.migrate` | **true** |
| `gear.six_slot.set_bonus` | **true** |
| `balance_version` | **62** |
| ember-v1.yml md5 | **48b963229dd261d0036cc8e7e65c3f8e** |
| jar | **1.65.101-d335.local**（观察期真源；未本号换 jar） |
| k3_refine | **关 / 未开 live** |
| Stage2 观察 | **续** · 绿出口不早于 **2026-10-10 17:40 CST** · **本号 ≠ 关观察** |

## 纪律（即日起）

| 禁 / 须 | 口径 |
|---------|------|
| **禁止** | 在 **`/workspace/minecraft` 主工作区** 对含 live 跟踪 yml 的树做 `checkout` / `switch` 来回切 `feat/*` ↔ `main`（会盖掉 Stage2 真源） |
| **须** | K3 / 插件离线预备 **只用独立 worktree**（或裸仓 clone），**勿**占用主工作区切分支 |
| **须** | 改 `ember-v1.yml` / runs 涉及开关或 bv 时，**live 与 `src/main/resources` 同文进 git**，避免「仅线上」再被分支树打回 |
| **禁** | 插件岗覆盖 live 开关真值；提前关观察；开 K3 live；动 Pack6 / 天赋 / 灰印 / 样本 R / ×0.97 |

## 后续

- 测试岗：**重跑** D342 R0–R10 只读（满窗前仍只预检；**禁止**签观察结束）。  
- 插件岗：K3 继续 **独立 worktree** 离线预备；**零 live**。  
- 观察日历：D338 绿出口模板不变；满窗 ≥**2026-10-10 17:40 CST** 另号签字。

## 改动（本号）

| 文件 | 改动 |
|------|------|
| `plugins/CoreRpg/ember-v1.yml` | 钉入 six_slot 三 true |
| `plugins/CoreRpg/ember-v1-runs.yml` | 钉入 bv62 + 61/62 注释 |
| `CoreRpg/src/main/resources/ember-v1.yml` | 与 live 同文 |
| `CoreRpg/src/main/resources/ember-v1-runs.yml` | 与 live 同文 |
| 本 STATUS | 事件 / 根因 / 恢复 / 纪律 |
| D342 批 A STATUS | 保持 docs-only；红停报改链 precheck-RED |
| `…-d342-precheck-RED-…` | 红停报另存（勿丢证据） |

## 不动

关 Stage2 观察 · 关 Stage1 · 开 K3 live · 换 ×0.97 · Pack6 / 天赋 / 灰印 / 样本 R · 强推 · stash/reset/checkout -- . · 提交 ladder.yml / active-mobs.json

---

*D343 · 跟踪 yml + 主仓 checkout 覆盖 → 恢复三 true+bv62 并钉进 main · ≠关观察 · K3 只用独立 worktree。*
