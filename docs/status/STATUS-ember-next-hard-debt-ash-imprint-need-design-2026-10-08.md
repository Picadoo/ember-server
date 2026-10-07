# 状态 · 下一档硬债选定 · 需策划（灰印副招枢轴 / 成长横向第二技能身份）

> **上游结案：** D302 深渊可感差异方案 **M 已上线**（CoreRpg **1.65.92** · `c6196b43`）；D301 守招·余烬招架 **已上线**（1.65.91 · `78dec2ae`）；天赋换机制 **HOLD**；事件 R/W / 调律 R / 工坊 R / 走廊 W2 均后置等人。见 [`STATUS-ember-abyss-feel-diff-d302.md`](STATUS-ember-abyss-feel-diff-d302.md)。

**日期：** 2026-10-08（上海时间）  
**上游：** D302 深渊感已收 · D301 招架已收 · 天赋 HOLD · 事件 R/W / 调律 R / 工坊 R / 走廊 W2 等人 · 深渊 R（Director）刚上 M，等人感  
**本窗性质：** **只 docs tip + 派单文** · **未改** yml / jar / 菜单 · 服务器保持 up  
**打开理由：** 招架落地后，技能装仍缺一条清晰的**副招进攻/控制身份**；旧灰印在 skill-kit 已删（白送或没人用）；守招稿方案 R 曾指向灰印作另一横向——本窗正式打开**灰印枢轴硬设计**（非 Pack6、非六槽）。

---

## 1. 选题（七选一 · 本窗裁决）

| # | 候选 | 裁决 | 理由 |
|---|------|------|------|
| A | 房间事件方案 **R/W**（软保底 / timed 权重） | **否** | D298 tip：须先攒 1～2 周真人样本；硬禁未解除 |
| B | 签名调律方案 **R**（每局 prepare 锁定） | **否** | 同上；绑遥测周样本，本窗不开 |
| C | 走廊 **W2**（房压/精英位轻排）/ 工坊方案 **R** | **否** | 均后置等人；非本窗硬债 |
| D | 天赋续跑（T0'''）/ 守招邻域再调 / 纯 lore | **否** | 天赋 HOLD 硬禁；招架刚上线等人；纯 lore 薄非难 |
| E | Pack6 / 六槽 / 副手方案 B | **否** | 硬禁 / 搁置 |
| F | 深渊方案 **R**（按段轻 Director 旋钮） | **否** | D302 刚上 M；等人感样本；R 后置，本窗不抢 |
| **G** | **灰印副招枢轴（成长横向 · 非 Pack6）** | **采纳 · 需策划** | skill-kit 已删旧灰印；招架后装仍缺清晰副招进攻/控制身份；守招稿 R 曾备选灰印——本窗另起规格，禁照搬旧 cand、禁永久伤税 / *d 系数 |

**对齐：** skill-kit §2～§3 灰印/聚火删除 · 守招枢轴稿方案 R 指针 · D301 成长横向（招架）已收 · 加固债 #2 仍薄于「刃+符+招架」之外 · ARCH 禁 Pack6/六槽。

---

## 2. 一句话问题

招架已有；技能装仍缺一条清晰的**副招进攻/控制身份**——旧灰印已删，须重设计手感（CD/窗/标记类），**禁**永久伤税与 *d 系数堆通关。

---

## 3. 建议派单文（给策划 · 可贴总控）

```
【派单·硬设计待批A】灰印副招枢轴（成长横向第二技能身份）
优先级：D302 后下一档体验硬债 · 非施工窗 · 对齐 skill-kit 已删灰印 + 守招稿方案 R 备选指针 · 非 Pack6
禁：六槽·Pack6·天赋续跑（HOLD）·守招邻域再调（等人）·抬体力/掉率/event_rate/ALTS·事件R/W·调律R·工坊R·走廊W2夹带·深渊R夹带·永久伤税/*d系数·照搬旧灰印cand·纯lore
目标：一条可装配副招（进攻/控制身份清晰）+ 与烬斩/烬突/招架分工可读；过线手感，不靠永久乘区

请出 docs/design/DESIGN-ember-ash-imprint-pivot-YYYY-MM-DD.md，文首 STATUS=待批 A，含：

0. 证据：skill-kit 旧灰印/聚火为何删（白送或没人用；指针 DESIGN-ember-skill-kit-2026-10-06.md §2/§3）；守招稿 R 备选灰印指针（DESIGN-ember-guard-skill-pivot-2026-10-07.md）；招架后装仍缺副招进攻/控制身份；禁照搬旧 kit_mark_* cand
1. 玩家感知目标（≤3 条）：例——装上后有明确「点一下有用」的进攻/控制侧移；与烬斩爆发 / 烬突位移 / 招架短窗分工可读；不是白送通关也不是永远不按
2. 方案表（至少 2 选 1 荐）：
   - 方案 M（荐）：菜单/装配壳 + 轻手感（短签、装配位、与真实 CD/窗一致的可读提示）；零经济、零永久乘；机制可后置薄桩
   - 方案 R：机制重设计（自有 CD/窗/标记或短推迟等）+ 回滚；写清钩点、与共享充能旧结构为何不同；若动战斗压须 p1sim
   - 方案 W（否决默认）：永久伤税 / *d 系数 / 六槽 / Pack6 / 照搬旧灰印 cand —— 写清否决理由（硬禁 / skill-kit 已证中间带空）
3. 不动清单与门禁：体力/掉落/event_rate/ALTS；六槽；Pack6；天赋轨；守招邻域；永久乘区 / 永久伤税 / *d；事件 R/W · 调律 R · 工坊 R · 走廊 W2 · 深渊 R 不夹带。若动战斗压须 p1sim 通关率门禁 42±2
4. 批注勾选：批 M / 批 R / 批 M+R / 驳回改派（其它）

交付：只 docs；批准前禁改 YAML/Java。
```

---

## 4. 施工岗暂不做什么

- **不**开事件 R/W · **不**开调律 R · **不**开走廊 W2 / 工坊 R · **不**开深渊 R  
- **不**开天赋 T0''' / 守招邻域再调（等人）  
- **不**扩 Pack6 / 六槽 · **不**永久伤税 / *d 系数 / 新永久乘区  
- **不**照搬 skill-kit 已删灰印 `kit_mark_*` cand  
- **不**写假「已施工」报告  
- **不**动 ECONOMY_BV / bv（本 tip 零部署）

有硬规格且总控批 A 后，另派施工号（建议 **D303+**；若方案含战斗压旋钮先 T0 再 T1）。

---

## 5. 备选（本窗不采纳 · 记档）

| 备选 | 为何不抢本窗 |
|------|----------------|
| 事件 R/W · 调律 R | D298 后须 1～2 周真人样本；证据未满 |
| 走廊 W2 · 工坊 R | 后置等人；非本窗硬债 |
| 深渊 R（Director） | D302 刚上 M；等人感 |
| 天赋 T0''' / 守招邻域再调 | HOLD / 刚上线等人 |
| 纯 lore / 破绽薄修 | 薄施工，非难活 |
| Pack6 / 六槽 | 硬禁 / 搁置 |
| 照搬旧灰印 cand / 永久伤税 | skill-kit 已证；硬禁永久乘 |

---

## 6. 刚结指针

- D302 · [`STATUS-ember-abyss-feel-diff-d302.md`](STATUS-ember-abyss-feel-diff-d302.md) · CoreRpg **1.65.92** · `c6196b43`  
- D301 · [`STATUS-ember-guard-skill-parry-d301.md`](STATUS-ember-guard-skill-parry-d301.md) · CoreRpg **1.65.91** · `78dec2ae`  
- 选题 F 旧 tip（深渊层感）· [`STATUS-ember-next-hard-debt-abyss-feel-need-design-2026-10-07.md`](STATUS-ember-next-hard-debt-abyss-feel-need-design-2026-10-07.md) · **已收为 D302**  
- skill-kit 灰印删除 · [`DESIGN-ember-skill-kit-2026-10-06.md`](../design/DESIGN-ember-skill-kit-2026-10-06.md) §2～§3  
- 守招枢轴 R 备选灰印 · [`DESIGN-ember-guard-skill-pivot-2026-10-07.md`](../design/DESIGN-ember-guard-skill-pivot-2026-10-07.md)  
- 天赋 HOLD · [`STATUS-ember-talent-row1-mech-t0dprime-2026-10-07.md`](STATUS-ember-talent-row1-mech-t0dprime-2026-10-07.md)  
- 加固债总览 · [`DESIGN-ember-playfeel-hardening-2026-10-07.md`](../design/DESIGN-ember-playfeel-hardening-2026-10-07.md)

---

*服务器：proxy/login/play 保持 up；本 tip 零部署。*
