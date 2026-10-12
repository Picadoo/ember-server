# RESEARCH · 主线解锁 2c / 六槽结构债（TIP-Z 后 P1 · docs-only）· 2026-10-12

> **状态：** CLOSED · TIP-AA 已批 A·M · docs-only · 2026-10-12 17:39 Asia/Shanghai  
> **对接 tip：** [`TIP-ember-mainline-unlock-2c-research-thin-d12544-d12551-2026-10-12.md`](../tips/TIP-ember-mainline-unlock-2c-research-thin-d12544-d12551-2026-10-12.md)  
> **上游：** TIP-Z CLOSED docs-only `923e031f1` · TIP-L CLOSED live `1.65.767-d12351` · D174 DESIGN（2c 暂缓）· 六槽 Stage1+2 live + D453 绿出口 PASS  
> **硬禁：** 零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin · 零深化 densify · 零 sx20 · 零 skill_mult · 零 ForgeChoice·wear·pity·awkDelta densify · **本窗零 jar / 零插件派单 / 零当薄 densify 施工**

---

## 0. 一句话

「2c 六槽」在 D174 里是**暂缓大包**；现网六槽护甲已**另轨上线**。本窗只盘清：**哪些 2c 条目已过时、哪些仍是真结构债、独立 DESIGN 必须覆盖什么**——**不是**薄窗 densify，也**不是**现在开码。

---

## 1. 「2c 六槽」在既有文档里指什么

| 来源 | 原文口径 | 读法 |
|------|----------|------|
| D174 DESIGN §5 阶段表 | `2c（暂缓）` = **6 槽护甲**（当时 60/10、0.7/0.1、Q01 起步件）+ **防具签名 1 条上限**；须 **W30 在 D174 掉落模型下重跑** | 主线解锁轨上的「甲槽 + 防具签名」打包阶段 |
| D174 §2.1 | 六槽上线后：护符签名改叫「**防具签名**」，可落护符或任意甲；**5 件防具同时只算 1 条** | 行为规则已写死，**未施工** |
| COORD-mainline-unlocks | stages 1/1.5/2a/2b/3 DONE；**2c on hold** | 主线 COORD 从未派 2c 施工 |
| TIP-Z / 观察 RESEARCH | 「解锁 2c 六槽 = DESIGN HOLD；非薄 tip」 | 正确把 2c 标成**大设计**，但未拆「甲槽已 live vs 签名未开」 |
| 装备权威 §1 / §9（D330） | **6 槽已上线观察中**（Stage1 F + Stage2 C）；HOLD 仅未授权项 | 与 D174「2c 暂缓」**字面冲突**——须本窗勘误 |

**勘误结论（本 RESEARCH 钉死）：**

1. **「上六槽护甲」这一半已不在 2c 待办里**——由 D312→D317→T1→Stage2→**D453 绿出口 PASS** 另轨完成（共鸣 F · 四件套 ×0.97 · 三开关开）。  
2. **仍属 2c / 主线解锁结构债的核心** = **防具签名（armor-capable signatures · 5 防具 1 生效）** + 与现网六槽 / 签名经济的 **叠合门禁（含 W30）** + D174 文首「2c 暂缓」口径同步。  
3. 不得把「六槽 honesty / 菜单文案 densify」或「再抬 STAMP」当成 2c 薄施工。

---

## 2. 解锁 DESIGN 现状盘点（D174）

| 阶段 | 内容 | 状态（2026-10-12） |
|------|------|-------------------|
| 1 | Q01–Q03 签名 + 徽记 + 烙印 + 双签名 | **已上线** 1.65.7 |
| 1.5 | 签名图鉴 / 烙印 TrMenu | **已上线** 1.65.10 |
| 2a | Q04–Q06 签名 + 烬斩钩子 | **已上线** 1.65.11 |
| 2b | 残响 / 前哨 / 誓约 | **已上线** 1.65.12–14 |
| D183 / 3 | 烬爆返工；Q07 签名 + 调律 | **已上线** 1.65.17–18 |
| **2c** | 六槽护甲 + 防具签名 + W30 | **标签过时**：甲槽另轨 live；**防具签名仍方案** |
| TIP-L | Q07 charm 偏置 + 追烙农场闭环 | **CLOSED live**；非 2c |

现网签名实现（只读）：`EmberSignature.DEFS` 全部 `slot` = `blade` | `charm`；`mods()` / 烙印文案仍按「刃 1 + 护符 1」；**无 head/chest/legs/boots 签名路径**。六槽 `SLOTS` 已含四甲，但签名系统未接甲。

---

## 3. 结构债 OPEN 表（本窗盘清 · 非施工）

| ID | 债 | 严重度 | 为何不能当薄 tip |
|----|----|--------|------------------|
| S1 | **防具签名规则落地规格**（charm→defense；5 件只算 1；刃仍 1） | P0 结构 | 改 `EmberSignature` / loadout / 烙印目标 / UI；须独立 DESIGN + p1sim |
| S2 | **烙印 / 掉落 stampable 是否扩甲槽**（甲掉签名？仅烙？） | P0 | 动件数/惊喜分布 → W30；禁顺手抬 STAMP_RATE |
| S3 | **生效优先级**（多甲带签时取哪条；与套装/四件套 ×0.97 / 天赋同 tag） | P0 | 战斗组合爆炸；须 42 格 + 可达格 |
| S4 | **W30 / 经济叠合**（live 六槽 F + 签名徽记经济 + 可选甲签） | P0 | D174 原文硬门槛；现 bv≫62，须现行规则复跑 |
| S5 | **D174 / 装备权威 / COORD 口径三角**（「2c 暂缓」vs「六槽已上线」） | P1 docs | 本窗可记指针；正式勘误可进下一 DESIGN 文首 |
| S6 | **L11b / 调律遗留**（D184 HOLD） | P2 旁支 | **不绑 2c**；另 tip + 配对 W30 |
| S7 | 六槽后置：K3 / 档 S / 白板补建模 | 另轨 | **非主线解锁 2c**；绿出口后另号；本窗禁夹带 |
| S8 | TIP-L HOLD 余（枢纽折叠 / 抬 STAMP / Affix / echo cap） | 禁 | densify / 抬表 / 另主题 |

---

## 4. 成熟游戏对照（槽位变化前必读 · 沿用 D174 RESEARCH）

用户规则：改装备槽相关前先调研成熟做法；先分阶计划再码。本窗**不新发明乘区**，只对齐已引用结构：

| 参照 [M#] | 学什么 | 不学什么 |
|-----------|--------|----------|
| D4 力量典籍拓印 [M3] | 效果可烙到新件；本地材料保底 | 无限叠威能数值 |
| 命运 2 本本护甲套 [M2] | 护甲位承载**行为型**套效果 | 大倍率数值套 |
| WoW 首领饰品 [M1] | 招牌贴首领主题；装等随难度 | 饰品墙叠乘 |
| PoE 万神殿 [M10] | **同时生效条数封顶**（1 主 1 次） | 全开 |
| DNF 专属材料升级 [M7] | 回本图刷材料搬效果 | 专属史诗数值膨胀 |
| MH 部位装 [M8] | 甲位有主题技能 | 每怪一套数值碾压 |

**给独立 DESIGN 的直接约束（从对照读出）：**

- 防具签名仍是**效果不是数值**；不加新伤害乘区。  
- **同时生效封顶不变精神**：刃 1 + **防具侧 1**（原「护符 1」扩成「5 防具取 1」），禁止 5 甲各算一条。  
- 保底仍走**徽记烙印**；扩 stampable 到甲必须先过 W30，默认偏保守（「可烙不可掉」可作为 stage0 候选）。  
- 主题仍贴首领；不新开「地图数值套装」。

---

## 5. 独立 DESIGN 必须覆盖（下一 tip · 非本窗）

> 本 RESEARCH **不**代替 DESIGN。下一窗应另开 **独立 DESIGN tip**（建议名：`DESIGN-ember-mainline-unlock-2c-armor-sig-stage0-…`），**HOLD 施工**至总控批 A 且门禁过线。

### 5.1 建议分阶（stage0 大纲 · 仅研究产出）

| 阶 | 内容 | 产出 | 禁 |
|----|------|------|-----|
| **0 · 规格** | 钉 S1–S4：防具签名定义、生效选取、烙印/掉落是否扩甲、与 Stage1 F / Stage2 C 边界 | DESIGN stage0 docs + 门禁表 | 零 jar |
| **1 · 离线门禁** | p1sim：签名单条/组合/天赋排除在「防具 1」规则下；W30（六槽 live 参数 + 签名经济）；团本静态若涉 H | out-*.md；不过 → 停 | 禁抬 STAMP / 伤乘凑线 |
| **2 · 施工 tip** | 仅门禁 PASS + 总控显式开工后：Java/UI 最小切片 | 另 tip 单主题 | 禁与 Affix/glance/深化 densify 同包 |

### 5.2 门禁草案（供 DESIGN 钉死 · 本窗不跑）

| Gate | 意图 |
|------|------|
| G-sig-1 | 5 防具任意子集带签 → **生效恰好 ≤1**；刃规则不变 |
| G-sig-2 | 同 tag / 天赋 excl 与现行一致；不与四件套 ×0.97 叠出新乘区叙事 |
| G-42 | 单条 + 合法组合可达格上偏门槛对齐 D174（不超既有精神） |
| G-W30 | 相对现行六槽+签名基线，极品时间线 ±0.5 周（或 DESIGN 重钉并经总控签） |
| G-drop | 若甲可 stamp：件数/成色输入变化必须显式建模；默认候选 = **仅烙印扩甲、掉落仍只打刃/护符** |

### 5.3 p1sim / 工具现状（只读）

- `mainline.py` SIGS / `EmberSignature` 镜像：刃+护符。  
- `P1SIM_SIX` / gear6：六槽经济已有；**未**与「防具签名 1」合成一键门禁。  
- 独立 DESIGN 须写清：复用还是最小接线；**本窗不改 tools**。

---

## 6. 明确 NON-goals（本 RESEARCH + TIP-AA）

- 不把 2c 当**薄 densify**施工；不开 jar / 不派插件 / 不改 map。  
- 不重开「上六槽」工程（已 live）；不关 `enabled`/`migrate`/`set_bonus`。  
- 不抬 STAMP_RATE / 徽记周池 / Affix / 伤乘 / 永久功率表。  
- 不夹带 K3 / 档 S / 白板补建模 / Pack6 / 天赋灰印 / sx20 / skill_mult / glance twin。  
- 不在本窗写可施工数值终表（数字属下一 DESIGN + sim）。  
- 不把 L11b / 调律 HOLD 绑进 2c 开工令。

---

## 7. 成功标准（本 RESEARCH tip）

- [x] 说清「2c」原义 vs 现网六槽 live 的裂口  
- [x] OPEN 结构债表（S1–S8）可核  
- [x] 独立 DESIGN 覆盖面 + 分阶大纲 + 门禁草案  
- [x] 成熟游戏对照指针（槽位/效果封顶/本地材料）  
- [x] NON-goals 与硬禁对齐 TIP-Z/Y/T  
- [x] **零**施工指引冒充开工令  

---

## 8. 下阶段指针

| 优先 | 动作 |
|------|------|
| **下一 tip** | **独立 DESIGN（stage0）**：防具签名 + 叠合门禁；文首勘误 D174「2c」；**HOLD 施工** |
| 门禁后 | 总控显式批开工 → **另**施工 tip（单主题） |
| P0 并行 | 试玩 TIP-L 下一环（TIP-Z）；与 2c DESIGN **不互堵** |
| P2 | 烬斩门闩仍 TIP-Y；不绑 2c |

---

## 9. 参考路径

- [`DESIGN-ember-mainline-unlocks-2026-10-04.md`](DESIGN-ember-mainline-unlocks-2026-10-04.md) §2.1 / §5  
- [`RESEARCH-ember-mainline-unlocks-2026-10-04.md`](RESEARCH-ember-mainline-unlocks-2026-10-04.md)  
- [`RESEARCH-ember-mainline-content-observe-2026-10-12.md`](RESEARCH-ember-mainline-content-observe-2026-10-12.md)（TIP-Z）  
- [`DESIGN-ember-gear-6slot-stage0-2026-10-04.md`](DESIGN-ember-gear-6slot-stage0-2026-10-04.md) D246  
- [`DESIGN-ember-six-slot-stage1-decision-2026-10-08.md`](DESIGN-ember-six-slot-stage1-decision-2026-10-08.md) D314  
- [`DESIGN-ember-six-slot-armor-catchup-2026-10-08.md`](DESIGN-ember-six-slot-armor-catchup-2026-10-08.md)  
- [`DESIGN-ember-gear-structure-2026-10-06.md`](DESIGN-ember-gear-structure-2026-10-06.md) §1/§9  
- [`STATUS-ember-stage2-green-exit-d453-2026-10-10.md`](../status/STATUS-ember-stage2-green-exit-d453-2026-10-10.md)  
- `/workspace/COORD-mainline-unlocks.txt`
