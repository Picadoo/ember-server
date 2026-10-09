# 状态 · 下一档硬债选定 · 需策划（冒险页「我的进度」Stage2 四件套诚实 · docs-only）

> **已关 · 批 A·M · D345 已落地** · 施工 STATUS [`STATUS-ember-six-slot-adventure-set-honesty-d345-2026-10-09.md`](STATUS-ember-six-slot-adventure-set-honesty-d345-2026-10-09.md) · DESIGN 方案 M · adventure「我的进度」+1 行 armor_set · **≠关观察** · 不抢 K3。

> **上游结案：** Stage2 观察续（绿出口仍 ≥**2026-10-10 17:40 CST**，**勿交关窗**）。D344 live-yml-protect 已批 A·M 并落 OPS @ `6af090f8`（**已含**「主工作区禁切分支 / K3 用独立 worktree / skip-worktree」——本窗复核通过，**无需旁注补行**）。D329–D343 / hub·help D339–D340 / 闸页 / 绿出口模板 / 现态复跑 / 防冲 OPS 已落。禁 Pack6 / 天赋 / 灰印 / 改 ×0.97 / K3 live / 提前关观察 / 拧 set_bonus / 动 F / 样本 R。**零 live 玩法大改（本稿只 docs）。**

**日期：** 2026-10-09（上海时间）  
**本窗性质：** **只 docs tip + DESIGN** · **零代码 / 零价表 / 零 jar 玩法** · 施工另号（可只 TrMenu）  
**硬规格（已批 A · 批 M · D345 已落地）：** [`DESIGN-ember-six-slot-adventure-set-honesty-2026-10-09.md`](../design/DESIGN-ember-six-slot-adventure-set-honesty-2026-10-09.md) · backlog `B-six-slot-adventure-set-honesty`  
**打开理由：** D339 已修 hub/help，但 **冒险页 `ember_p1_adventure`「我的进度」** 仍只镜像族觉醒（`%awaken%` / `awaken_next` / 刃护符 `set_progress`），**无** `%corerpg_p1_armor_set%`——玩家从进本主路径仍看不到 Stage2 四件套态。D342/D344 tip 曾点名「adventure 仅 awaken」并因防冲事故后置；现防冲已落，升为本窗主交付。

---

## 0. 局势一句话

主菜单诚实了，进本前「我的进度」还在讲 Stage1 半句——观察期可批显示补丁规格，施工另号。

---

## 1. 选题

| # | 候选 | 裁决 | 理由 |
|---|------|------|------|
| A | 关观察 / K3 live / 改 ×0.97 / 样本 R / Pack6 / 天赋 / 灰印 | **否** | 硬禁 / 未满窗 |
| B | 重复 D329–D344（含防冲 OPS、闸页、绿出口模板、hub/help） | **否** | 已落 |
| C | D344 OPS 漏写禁切分支 → 旁注补行 | **否** | 本窗复核 OPS §1.5 / DESIGN 已含禁主仓切分支 + worktree + skip-worktree |
| D | 团本 lore「战斗规则和三套装不变」软抛光 | **否（本窗主交付）** | 语义可解为「规则未改」；次于进度格缺 armor_set |
| E | 图录 help 捷径「怎么玩/三套装」半行 | **否（本窗）** | 次入口；help 正文已 D339 诚实 |
| **H** | **冒险页「我的进度」Stage2 四件套诚实** | **采纳 · 需策划** | 证据见下 |

**证据：**
- `plugins/TrMenu/menus/ember_p1_adventure.yml` 图标 `K`「我的进度」lore：`§7套装：%corerpg_p1_awaken%` + `awaken_next` + `set_progress`——**无** `%corerpg_p1_armor_set%`。  
- `set_progress`（`EmberLoadout.setProgress`）= 刃+护符同族成套进度（同族/T3/T3+9），**不是**四件套护甲计数。  
- 对照：`ember_hub.yml` 装备格已有 `§8护甲四件套：%corerpg_p1_armor_set%`（D339）；`armor_set` PAPI（`EmberSixPapi`）已诚实三态（未同族 / n/2 / 已激活含 −3%）。  
- 上游 tip 明确后置：`STATUS-ember-next-hard-debt-six-slot-live-yml-protect-…` 候选 C；`…-observe-state-check-…` 候选 D「adventure 页仅 awaken 无 armor_set」。  
- D344 复核：`OPS-ember-six-slot-migration.md` §1.5 表与 skip-worktree 段已写「禁主仓切分支」「K3 离线只用独立 worktree」；live 两文件现态 `git ls-files -v` 为 `S`（skip-worktree 已设）。

**荐方案 M：** docs-only 钉文案补丁——冒险页「我的进度」在觉醒行后 **+1 行** `%corerpg_p1_armor_set%`（−3% 纪律对齐 D329/D339：只依赖 PAPI，勿静态谎称未激活也 −3%）；零数值；施工另号只 TrMenu；可选同号旁扫 challenge/modes 无对称进度格则不动。

**为何仍是硬债（非纯软文档）：** 冒险页是**进本第一触点**；与 hub/set/armor 已诚实页**口径分裂**；观察期可显示部署；零玩法。

---

## 2. 派单句

见 DESIGN 同 slug · STATUS=待批 A · 荐 M。

---

## 3. 暂不做什么

不交关观察签字；不部署 K3；不改 ×0.97 / set_bonus / bv；不重开 D339 hub/help；不 stage 脏 runtime（ladder / MythicMobs）；本 tip 窗零 live。

---

*选题 H · **tip 已关 · 批 A·M · D345** · adventure 进度格已补 armor_set · 勿关观察窗。*
