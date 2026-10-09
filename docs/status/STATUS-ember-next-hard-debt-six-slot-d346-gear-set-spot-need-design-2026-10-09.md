# 状态 · 下一档硬债选定 · 需策划（D346 装备页套装格落地薄抽验收清单 · docs-only）

> **上游结案：** Stage2 观察续（绿出口仍 ≥**2026-10-10 17:40 CST**，**勿交关窗**）。D346 gear 套装格诚实 **已施工** @ `d51b28a2`（静态 G1/G3/G4 PASS · `trmenu reload` 已做）；D345 adventure 薄抽 **已 PASS** @ `a9238b55`；D347 旁路 docs 勘误已落 @ `6e81d3fa`。禁 Pack6 / 天赋 / 灰印 / 改 ×0.97 / K3 live / 提前关观察 / 拧 set_bonus / 动 F / 样本 R。**零改菜单/开关（本稿只 docs checklist）。**

**日期：** 2026-10-09（上海时间）  
**本窗性质：** **只 docs tip + DESIGN** · **零改 yml/jar/开关** · 测另号  
**硬规格（待批 A · 荐 M）：** [`DESIGN-ember-six-slot-d346-gear-set-spot-2026-10-09.md`](../design/DESIGN-ember-six-slot-d346-gear-set-spot-2026-10-09.md) · backlog `B-six-slot-d346-gear-set-spot`  
**打开理由：** D346 DESIGN §2.3 / 施工 STATUS 写明薄抽 **G2**（打开装备页套装格可见）交测试另号；落地 STATUS 仅交**静态**。与 D339→D340、D345 spot 同形缺口——需可执行活窗清单关清。

---

## 0. 局势一句话

套装格文案已上菜单，静态已绿；**玩家面窗口是否真显 armor_set**尚未按清单抽——docs 补测单即可，不重开 D346 文案窗。

---

## 1. 选题

| # | 候选 | 裁决 | 理由 |
|---|------|------|------|
| A | 关观察 / K3 live / 改 ×0.97 / 样本 R / Pack6 / 天赋 / 灰印 | **否** | 硬禁 / 未满窗 |
| B | 重复 D329–D347（含 D346 文案施工、D347 docs） | **否** | 已落 |
| C | hub_legacy 缺 armor_set /「不改方块甲」 | **否（后置）** | **证据：非玩家可达**——无 Bindings 命令；Title 标明旧模式；D63/D202 普通玩家 `/trmenu open ember_hub_legacy` = no permission；仅管理员查旧壳 |
| D | 过时 tip 关闭包（k3-refine / observe-close 头旁注） | **否（本窗主交付）** | 维护软债；次于刚落地 D346 活窗关清 |
| E | D345 再抽 / 重开 adventure | **否** | 薄抽已 PASS |
| **H** | **D346 装备页套装格落地薄抽清单** | **采纳 · 需策划** | 证据见下 |

**证据：**
- [`DESIGN-ember-six-slot-gear-set-icon-honesty-2026-10-09.md`](../design/DESIGN-ember-six-slot-gear-set-icon-honesty-2026-10-09.md) §2.3：G2「打开装备页套装格」；绿出口 G1–G4。  
- [`STATUS-ember-six-slot-gear-set-icon-honesty-d346-2026-10-09.md`](STATUS-ember-six-slot-gear-set-icon-honesty-d346-2026-10-09.md)：仅静态 G1/G3/G4；旁注「薄抽 G2 交测试另号」；**无** live 窗口 dump。  
- 现网 `ember_p1_gear.yml` `S`：已有 `%corerpg_p1_armor_set%` + 护甲页详情行（施工 `d51b28a2`）。  
- 范式：D340 hub/help 薄抽清单；D345 adventure spot PASS。  
- hub_legacy：文件头「不对玩家开放」· 无命令绑定 · AUDIT/D202 证实非 OP 不可 open → **本窗后置，不做文案债**。

**荐方案 M：** docs-only 薄抽 checklist（S0–S5）；测号打开装备页套装格；禁改 yml；测另号（≠ D345 adventure 号）。

**为何仍是硬债：** 显示债「落地」未闭合到玩家面可见性；绿出口前菜单诚实增量旁注需要 live 证据指针。

---

## 2. 派单句

见 DESIGN 同 slug · STATUS=待批 A · 荐 M。

---

## 3. 暂不做什么

不重开 D346 文案；不改开关/bv/×0.97；不提前关观察；不部署 K3；不动 hub_legacy；本 tip 窗不跑实机、不 stage 脏 runtime。

---

*选题 H · tip 开 · 待批 A · 荐 M · D346 薄抽清单 · 勿关窗 · hub_legacy 后置。*
