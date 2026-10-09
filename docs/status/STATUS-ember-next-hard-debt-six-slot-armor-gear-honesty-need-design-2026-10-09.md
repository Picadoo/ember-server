# 状态 · 下一档硬债选定 · 需策划（六槽装备页护甲入口诚实 · docs-only）

> **旁注（已关 · 批 A · 批 M · D329）：** tip `6d8b6565` 已关；总控已批 A·M；施工 STATUS [`STATUS-ember-six-slot-armor-gear-honesty-d329-2026-10-09.md`](STATUS-ember-six-slot-armor-gear-honesty-d329-2026-10-09.md)。

> **上游结案：** Stage2 档 C 线上 bv62 · `set_bonus=true`（live `6e734dc7`）；观察关窗批 M 执行中（D326 · 绿出口不早于 **2026-10-10 17:40 CST** · 必抽已 PASS `54638e00`）；K3 已批 M、T0‴ 产物在汇总（**本窗不抢签 K3 施工**）。护甲页「后续开放」**线上已清**（D325 三态）。样本 R 全表不得开；天赋/灰印 HOLD；Pack6 硬禁；禁改 ×0.97 / 拧 set_bonus / 观察期部署 K3。

**日期：** 2026-10-09（上海时间）  
**本窗性质：** **只 docs tip + 硬规格** · **未改** yml / jar / 菜单 · 服务器保持 up · **零 live**  

> **交班旁注（D329 后）：** 本债已落地 @ `7f1b165e`。下一档 tip → [`STATUS-ember-next-hard-debt-six-slot-authority-ops-align-need-design-2026-10-09.md`](STATUS-ember-next-hard-debt-six-slot-authority-ops-align-need-design-2026-10-09.md)。
**硬规格（已批 A · 批 M · D329）：** [`DESIGN-ember-six-slot-armor-gear-honesty-2026-10-09.md`](../design/DESIGN-ember-six-slot-armor-gear-honesty-2026-10-09.md) · backlog `B-six-slot-armor-gear-honesty`  
**打开理由：** 观察期要持续推进薄结构债；护甲页已诚实，但**装备页护甲入口**仍不提四件套进度 / −3%，玩家从 `/ember`→装备 看不到套装态——零数值、可只改 TrMenu/PAPI 镜像，观察期可批 A，施工另号。

---

## 0. 局势一句话

Stage2 观察中、K3 施工勿抢。下一档若去开 R / Pack6 / 改 ×0.97 / 部署 K3 = 假活。护甲页「后续开放」已清，**不要再做那个**。改采纳 **装备页护甲入口诚实（docs-only）**。

---

## 1. 选题（裁决）

| # | 候选 | 裁决 | 理由 |
|---|------|------|------|
| A | 样本 R 全表 / Pack6 / 天赋 / 灰印 / 改 ×0.97 / 部署 K3 | **否** | 硬禁 / HOLD / 观察期禁 / 勿抢 K3 施工签 |
| B | 权威文档勘误（gear-structure §1/§9 仍写六槽未上线） | **否（本窗主交付）** | 真缺口，但用户催进度下 **装备页入口不诚实** 更贴玩家面；勘误可旁注后续 |
| C | D12-② 掉落甲分解验收清单 | **否（本窗主交付）** | 可 docs，但属测验收残余，非每日路径诚实 |
| **H** | **装备页护甲入口诚实** | **采纳 · 需策划** | 证据：`ember_p1_gear` 护甲格 lore 只写 F/成色精工 +「打开护甲页」，**无** `%corerpg_p1_armor_set%` / −3%；护甲页候选已有 `armor_*_fam` 套装态，装备入口无镜像 |

**荐方案 M：** 定文案补丁规格——装备页护甲入口加 1～2 行套装态（PAPI 镜像现网）；可选候选对比已有 fam 行保持；零改 ×0.97 / 零改触发规则；施工另号、可只 TrMenu（+必要时 PAPI 短签）；观察期可部署（显示 only）。

---

## 2. 一句话问题

四件套已上线，但玩家在装备页护甲入口仍看不到「进行中 / 已激活 / 受伤 −3%」——要在**不改数值、不改开关**前提下把入口文案补诚实。

---

## 3. 派单句（已交 DESIGN）

见同日 [`DESIGN-ember-six-slot-armor-gear-honesty-2026-10-09.md`](../design/DESIGN-ember-six-slot-armor-gear-honesty-2026-10-09.md) · **已批 A · 批 M · D329**（原荐 M）。

---

## 4. 施工岗暂不做什么

- 不改 ×0.97 / set_bonus / enabled / migrate / bv / jar  
- 不部署 K3 · 不开样本 R · Pack6 · 天赋 · 灰印  
- 不改 F 跟随 / 护符 ×1.0 / 价表 / 掉率 / 体力  
- 本 tip 窗 **零 live**；批 A 后菜单施工**另号**

---

## 5. 刚结指针

- D325 live · `6e734dc7` · bv62 · set_bonus · 护甲页三态已无「后续开放」  
- D326 观察 · 批 M · 必抽 PASS · 绿出口另签  
- D327 K3 · 批 M · T0‴ 汇总中 · **勿抢施工签**  
- 证据文件：`plugins/TrMenu/menus/ember_p1_gear.yml`（护甲格 lore）· `ember_p1_armor.yml`（已诚实三态）· `EmberSixPapi`（`armor_set` / `armor_*_fam`）

---

*零部署 · 选题 H · 荐 M · 禁偷开 R / K3 施工 / 改 ×0.97。*
