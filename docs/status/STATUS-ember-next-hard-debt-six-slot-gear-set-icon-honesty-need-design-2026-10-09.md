# 状态 · 下一档硬债选定 · 需策划（装备页套装格 Stage2 四件套诚实 · docs-only）

> **已关 · 批 A·M · D346 已落地** · 施工 STATUS [`STATUS-ember-six-slot-gear-set-icon-honesty-d346-2026-10-09.md`](STATUS-ember-six-slot-gear-set-icon-honesty-d346-2026-10-09.md) · DESIGN 方案 M · gear 套装格 `S` +2 行 armor_set · **≠关观察** · 不抢 K3 · 不抢 D345 薄抽。

> **上游结案：** Stage2 观察续（绿出口仍 ≥**2026-10-10 17:40 CST**，**勿交关窗**）。D345 adventure 诚实已施工 @ `49f401b1`（测试薄抽中——**本窗勿抢 D345 薄抽文件/清单**）。D329–D344 / hub·help / 防冲 OPS / 闸页 / 绿出口模板已落。禁 Pack6 / 天赋 / 灰印 / 改 ×0.97 / K3 live / 提前关观察 / 拧 set_bonus / 动 F / 样本 R。**零 live 玩法大改（本稿只 docs）。**

**日期：** 2026-10-09（上海时间）  
**本窗性质：** **只 docs tip + DESIGN** · **零代码 / 零价表 / 零 jar 玩法** · 施工另号（可只 TrMenu）  
**硬规格（已批 A · 批 M · D346 已落地）：** [`DESIGN-ember-six-slot-gear-set-icon-honesty-2026-10-09.md`](../design/DESIGN-ember-six-slot-gear-set-icon-honesty-2026-10-09.md) · backlog `B-six-slot-gear-set-icon-honesty`  
**打开理由：** D329 已修装备页**护甲入口**三态，但同页 **套装格 `S`（下界之星）** 仍只讲刃+护符族觉醒档与族被动，**零** `%armor_set%` / 四件套 −3% 指针——玩家在装备页点「套装」仍像 Stage1。与 D345 adventure 进度格**不同菜单缺口**。

---

## 0. 局势一句话

护甲入口诚实了，同页「套装」星还在讲半句觉醒——观察期可批显示补丁，施工另号。

---

## 1. 选题

| # | 候选 | 裁决 | 理由 |
|---|------|------|------|
| A | 关观察 / K3 live / 改 ×0.97 / 样本 R / Pack6 / 天赋 / 灰印 | **否** | 硬禁 / 未满窗 |
| B | 重复 D329–D345（含 adventure 诚实施工） | **否** | 已落 / D345 薄抽中勿抢 |
| C | D345 薄抽验收清单 | **否（本窗）** | 测试正在薄抽；勿与活轨冲突；交清单易抢文件 |
| D | hub_legacy「不改方块甲」/ 仅 awaken | **否** | legacy 非现行主路径（多次否） |
| E | 过时 tip 批量关闭包 | **否（本窗主交付）** | 维护薄债；次于玩家面残留 |
| F | 团本 lore「三套装不变」软抛光 | **否（本窗）** | 语义可解；次于装备页套装格 |
| **H** | **装备页套装格 `S` Stage2 四件套诚实** | **采纳 · 需策划** | 证据见下 |

**证据：**
- `plugins/TrMenu/menus/ember_p1_gear.yml` 图标 `S`：name=`§d套装：%corerpg_p1_awaken%`；lore=族被动 + 觉醒 I/II/III——**无** `%corerpg_p1_armor_set%`，**无**四件套 / −3% 短句。  
- 同文件护甲入口 `M`（D329/D336）已有三态 `%armor_set%`——**套装格未对齐**。  
- 对照：`ember_set.yml`（D331）/ hub（D339）/ adventure `K`（D345）均已有四件套镜像或短指针。  
- `set_progress` / 觉醒说明仍正确（刃护符轨）；缺口是**缺第二层**叙事。

**荐方案 M：** docs-only 钉文案补丁——装备页 `S` 在觉醒块末 **+1～2 行**：`%corerpg_p1_armor_set%` + 可选「详情→护甲页」；保留族被动与觉醒档；−3% 只跟 PAPI 已激活分支（勿静态谎称）；零数值；施工另号只 TrMenu；**不**重开护甲入口三态 / D345 薄抽。

**为何仍是硬债：** 装备页「套装」是**养成日常触点**；与同页护甲入口 / 套装页 / hub 已诚实口径分裂；观察期可显示部署。

---

## 2. 派单句

见 DESIGN 同 slug · STATUS=已批 A · 批 M · D346 已落地。

---

## 3. 暂不做什么

不交关观察签字；不部署 K3；不改 ×0.97 / set_bonus / bv；不抢 D345 薄抽 STATUS/证据目录；不重开 D329 护甲入口主轨；不 stage 脏 runtime；本 tip 窗零 live。

---

*选题 H · **tip 已关 · 批 A·M · D346** · gear 套装格已补 armor_set · 勿关观察窗 · 勿抢 D345 薄抽。*
