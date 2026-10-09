# 状态 · 下一档硬债选定 · 需策划（装备页「成套进度」Stage2 四件套诚实 · docs-only）

> **上游结案：** Stage2 观察续（绿出口仍 ≥**2026-10-10 17:40 CST**，**勿交关窗**）。D353 绿出口附录续写已落 @ `9f8c2013`（**≠关观察**）。D329–D352 主路径 / 次入口 / help 改名已收。禁 Pack6 / 天赋 / 灰印 / 样本 R / 改 ×0.97 / K3 live / 提前关观察。**硬禁复述：** 附录续写 · 已 PASS 薄抽 · tip 关闭包 · 钉盘 · Pack6/天赋/灰印/样本 R/×0.97/K3 live。**零数值（本稿只 docs）。**

**日期：** 2026-10-09（上海时间）  
**本窗性质：** **只 docs tip + DESIGN** · **零代码 / 零价表 / 零 jar** · 施工另号（可只 TrMenu）  
**硬规格（待批 A · 荐 M）：** [`DESIGN-ember-six-slot-gear-progress-honesty-2026-10-09.md`](../design/DESIGN-ember-six-slot-gear-progress-honesty-2026-10-09.md) · backlog `B-six-slot-gear-progress-honesty`  
**打开理由：** D345 已给冒险「我的进度」补 `%armor_set%`，D346 已给装备页**套装格 `S`** 补四件套行，但同页专用进度书 **`P`「成套进度」** 仍只镜像刃+护符 `set_progress`，并以「主线的终点 = 觉醒 III」收束——**无** `%corerpg_p1_armor_set%`。玩家养成日常点「成套进度」仍像 Stage1 半句。

---

## 0. 局势一句话

套装星诚实了，旁边的「成套进度」书还只讲觉醒终点——观察期可批显示补丁。

---

## 1. 选题

| # | 候选 | 裁决 | 理由 |
|---|------|------|------|
| A | 关观察 / K3 live / 改 ×0.97 / 样本 R / Pack6 / 天赋·灰印 | **否** | 硬禁 / 未满窗 |
| B | 附录续写复述 / 已 PASS 薄抽 / tip 关闭包 / 钉盘 | **否** | 硬禁；D353/D348–D350 已落 |
| C | hub_legacy「三套装 / 不改方块甲」 | **否（后置）** | 非玩家可达（D63/D202；仅 admin `trmenu open`） |
| D | hub 装备格「套装与觉醒」软半行 | **否（本窗）** | 同格已有 armor_set PAPI（D339）；次于进度书缺行 |
| E | 重开 D346 套装格 `S` / D345 adventure | **否** | 已落；本债是**不同图标** |
| **H** | **装备页「成套进度」`P` Stage2 四件套诚实** | **采纳 · 需策划** | 证据见下 |

**菜单扫描（本窗执行，排除 hub_legacy）：**
- `rg` `三套装|后续开放|未上线`（`plugins/TrMenu/menus/` · `!ember_hub_legacy.yml`）→ **可达页零命中**（D351/D352 已清）。  
- `rg` `set_progress`：仅 adventure `K` 与 gear `P`；**adventure 已邻接 armor_set（D345）；gear `P` 无**。  
- `rg` `主线的终点` → 仅 `ember_p1_gear.yml` `P` L135。  
- hub_legacy 仍有「三套装 / 不改方块甲」——**不可达，后置**。

**证据：**
- `plugins/TrMenu/menus/ember_p1_gear.yml` 图标 `P`「成套进度」lore：`%corerpg_p1_set_progress%` +「主线的终点 = 同族 T3 两件 +9（觉醒 III）」——**无** `%corerpg_p1_armor_set%`。  
- `set_progress`（`EmberLoadout.setProgress`）= 刃+护符同族成套（同族/T3/T3+9），**≠** 四件套护甲计数。  
- 对照：同文件套装格 `S`（D346）与 adventure `K`（D345）均已有 `§8护甲四件套：%corerpg_p1_armor_set%`——**成套进度书未对齐**。  
- 「主线的终点 = 觉醒 III」在 Stage2 观察中易被读成**否认**护甲四件套并行轨。

**荐方案 M：** docs-only 钉文案补丁——装备页 `P` 在 `set_progress` 后 **+1 行** `%corerpg_p1_armor_set%`（−3% 纪律对齐 D329/D339/D345/D346：只依赖 PAPI，勿静态谎称）；可选同号软化「主线的终点」为刃护符觉醒终点句 + 四件套并行短注；保留 T3+10/毕业句；零数值；施工另号只 TrMenu；**不**重开 `S` / adventure / hub / help。

**为何仍是硬债（玩家面 / 可施工）：** 「成套进度」是装备页**养成日常触点**；与同页套装格 / 冒险进度已诚实口径分裂；观察期可显示部署；零玩法。

---

## 2. 派单句

见 DESIGN 同 slug · STATUS=待批 A · 荐 M。

---

## 3. 暂不做什么

不交关观察签字；不部署 K3；不改 ×0.97 / set_bonus / bv；不复述附录/薄抽/关闭包/钉盘；不动 hub_legacy；不重开 D345/D346 已收图标；不 stage 脏 runtime（ladder / MythicMobs / p1-six）；本 tip 窗零 live。

---

*选题 H · tip 开 · 待批 A · 荐 M · gear「成套进度」补 armor_set · 勿关窗。*
