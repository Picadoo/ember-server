# RESEARCH · 烬斩手感 · SF4 战斗微效用（TIP-Y 后下一档 · 禁 skill_mult）· 2026-10-12

> **状态：** OPEN · TIP-BE · docs-only · DESIGN stage0 前置 · 2026-10-12 23:04 Asia/Shanghai  
> **起因：** TIP-BD CLOSED live 后 soft/开孔文案链已齐；总控离 soft densify（Anvil/topUpTo 仍冻），换轴 **可感变强途径**。TIP-Y 门闩仍有效；本窗盘清 **已 live vs 仍 HOLD**，荐 **SF4 DESIGN stage0**。  
> **上游：** TIP-Y CLOSED docs-only `a838781ed` · RESEARCH [`RESEARCH-ember-skill-feel-post-k-next-2026-10-12.md`](RESEARCH-ember-skill-feel-post-k-next-2026-10-12.md) · DESIGN [`DESIGN-ember-skill-feel-stage2-micro-stage0-2026-10-11.md`](DESIGN-ember-skill-feel-stage2-micro-stage0-2026-10-11.md) · TIP-K CLOSED live `1.65.766-d12335` · D301 余烬招架 LIVE · power-paths [`DESIGN-ember-power-paths-variety-2026-10-12.md`](DESIGN-ember-power-paths-variety-2026-10-12.md) · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **硬禁：** **零抬 skill_mult · 零伤乘 · 零 Affix densify · 零抬表 · 零 glance twin · 零深化 densify · 零 sx20 · 零粒子 densify · 不重开 TIP-K 同构 cue · 零 Anvil densify · 零 topUpTo 扩 soft · 零 soft-latch/CostSoft/pity/Cap/firstsoft/cook/life-buy soft 再叠 · 零 GemHole/enhance+5 开孔文案再叠 · 零开 L13 施工 tip · 零再扫甲闸 · 零扩 stampable · 零 SetChase 六槽 densify**

---

## 0. 一句话

soft/开孔展示族已齐；守招余烬招架 **已 live**（TIP-Y「守招 HOLD」过时）；下一档可感变强 = **SF4 形状×套装战斗微效用**——本窗只 RESEARCH→DESIGN stage0，**无码**，须 p1sim within 才谈施工。

---

## 1. TIP-Y 之后现网校正（只读）

| 层 | TIP-Y 当时口径 | 2026-10-12 校正 | 含义 |
|----|----------------|-----------------|------|
| TIP-F / TIP-K | 入口+战斗身份 cue CLOSED | **仍 CLOSED** | 禁 cue/粒子 densify twin |
| combat-feel 族 | NEAR-SAT 停加厚 | **仍停** | 禁 land/cast/dash 粒子 densify |
| SF1–SF3 | SHIP（命中反馈差 / 命中数 / 套装落点半行） | **仍 SHIP** | 展示层已齐 |
| **守招（Q）** | D212 S0b ❌ HOLD | **过时** → D301 **余烬招架 LIVE**（T0b✅→T1；D476 CD 就绪 cue） | **不再当「须新思路才开」的债**；禁重交旧壁垒表；禁再 densify 招架 CD/flat |
| 灰印 / 聚火 | HOLD | **仍 HOLD** | 须另起重设计 + p1sim；本窗不抢 |
| **SF4 微缓/微疗** | TIP-K D12327 HOLD · 须另 tip + p1sim within | **仍 OPEN 真债** | **本窗采纳** |
| 独立副招 CD | HOLD | **仍 HOLD** | 抬主动份额；禁 |
| skill_mult / 1.5B / CD / 几何盒 | 硬禁 | **仍硬禁** | 假变强 |

**口径钉死：** 「烬斩太素」在 TIP-K 的身份缺口 **已 CLOSED**；守招防御时机键 **已有**（招架）。剩的可感变强 = **形状选择影响战斗微结果**（SF4），不是再刷 ActionBar。

---

## 2. 证据（SF4 为何仍是债）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | Stage2 DESIGN：SF1–SF3 SHIP；**SF4 = 微缓/微疗按形状触发窗 · 高风险 · 须 p1sim within · BANNED 顺手开** | `DESIGN-ember-skill-feel-stage2-micro-stage0-2026-10-11.md` | **规格意向已有 · 门禁未过 · 无 stage0 钉死** |
| E2 | TIP-Y RESEARCH P2：SF4 须 within p1sim 后再 tip；禁无 sim 上战斗效用 | `RESEARCH-ember-skill-feel-post-k-next-2026-10-12.md` §3 | **门闩仍有效** |
| E3 | TIP-K / SF1–3 只做展示反馈；形状切换 **不改变** 缓速/自疗/抗性等战斗微结果 | SkillService / SetRules 只读 | **玩家仍可感「形状只是皮」**（展示已齐、效用未开） |
| E4 | D301 招架 = 防御时机键，**正交** SF4（进攻形状微效用）；禁借号改 PARRY_CD / flat | `EmberParry` · STATUS-D301 | **不抢招架轨** |
| E5 | soft/开孔/假耗/xp-boost/GemHole/enhance+5 链 **已齐**；Anvil soft densify / topUpTo **仍冻** | TIP-AZ…BD | **本窗离开 soft 族** |
| E6 | tip-J：禁新 glance twin；本债 = **战斗微效用规格**，非 PAPI 一眼 | power-paths | 正交可感 |

---

## 3. 错位场景（玩家面）

| 场景 | 真能力 | 现行体感 | 缺口 |
|------|--------|----------|------|
| 换 LINE/FAN/RING 打怪 | 几何命中差 + SF1–3 字面反馈 | 「字变了、怪还是一样死」 | **缺非伤乘微差** |
| 套装推荐形状 | 软推荐 + 同屏半行 | 跟不跟都无所谓战斗结果 | **形状承诺弱** |
| 招架成功 | 反打 flat · CD 28s | 防御侧已有决策 | **进攻侧形状仍素**（正交） |

---

## 4. 下一档门闩（解冻才可开施工 tip）

| 优先 | 条件 | 产出 |
|------|------|------|
| **本窗** | DESIGN stage0 钉 SF4 身份核 + 允许方向 + p1sim within 门禁 | docs-only · **HOLD 施工** |
| P1 | p1sim within PASS（三套装 · 通关率/择优硬线见 DESIGN） | 可报总控显式开工 |
| P2 | 显式开工 → 施工 tip（禁顺手改 skill_mult / CD / 几何盒） | jar |
| 禁 | 无 sim 上战斗效用 · TIP-K cue twin · 粒子 densify · 灰印/聚火夹带 · 招架 densify |

---

## 5. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| **SF4 战斗微效用 DESIGN stage0（docs-only）** | **采纳** | TIP-Y P2 真债；E1–E3；离开 soft/开孔链；**非 densify · 非 Anvil · 非抬表** |
| AnvilRepair Soft 假耗 | **否** | 硬禁 densify 未解冻；旁债仍冻 |
| topUpTo 接 soft | **否** | 扩 soft densify |
| soft-latch / CostSoft / pity / Cap / firstsoft / cook·life-buy / xp-boost / GemHole / enhance+5 再叠 | **否** | 链已齐 / 硬禁 |
| 再开 TIP-K cue / 粒子 densify | **否** | 身份债 CLOSED；reinvent |
| 守招再调 / 改 PARRY_CD·flat | **否** | D301 LIVE；禁 densify |
| 灰印 / 聚火重设计 | **次选旁债** | 须更大重设计；本窗先钉 SF4 |
| L13 施工 tip | **否** | TIP-AI FAIL；PASS≠开工；**HOLD** |
| Affix / 伤乘 / skill_mult / sx20 / 甲闸再扫 / 扩 stampable / SetChase | **否** | 硬禁 |
| 观察停窗 only | **否** | 有可钉 stage0 真债 |

---

## 6. 成熟做法对照（只学分工 · 不学乘区）

| 参照 | 学什么 | 不学什么 |
|------|--------|----------|
| 热血战士 / 方舟三脚架 | 技能形态改 **控制/自保节奏** | 熟练度伤乘 |
| D3 符文 | 换打法标签 | 大倍率数值 |
| PoE 辅助宝石微效用 | 短控/回复挂在技能形态 | 全图伤乘墙 |
| Hades 锤/盾位替 | 同键不同微结果 | 永久 DPS 表抬 |

**给 DESIGN 的约束：** SF4 价值在 **缓/疗/控短窗**，不在伤害乘区；须 p1sim within；禁 skill_mult。

---

## 7. 本 RESEARCH 验收

- [x] 校正 TIP-Y：守招 D301 LIVE；SF4 仍 OPEN  
- [x] 离开 soft/开孔/Anvil densify  
- [x] 选题采纳 SF4 DESIGN stage0；门闩含 p1sim within  
- [x] **零**数值终表、零 jar 指引  
