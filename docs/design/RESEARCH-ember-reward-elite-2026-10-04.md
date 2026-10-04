# 研究：奖励精英（Extra.ELITE）该怎么「变花样」· 2026-10-04

> 只读研究，服务设计稿 `DESIGN-ember-reward-elite-twists-2026-10-04.md`（D182）。
> 不改代码 / 不跑 sim。对照对象：Diablo 3 Champion/Rare 词缀、PoE 稀有怪、WoW 大秘境「少叠可读」、本服已有 D138/D176/D181 词缀精英与 D140/D173 首领招式形状库。

## 1. 现网事实（只读）

| 项 | 现网 |
|---|---|
| 事件 | `EmberRunRules.Extra`：NONE 70% / TREASURE 15% / **ELITE 10%** / CHEST 5%（`EXTRA_WEIGHTS`） |
| 怪 | 每图 `roles.elite` → `EmberQ0xElite`（金盔甲僵尸壳），**无 Skills / 无导演招式** |
| 奖励 | 击败结算 `ELITE_SHARD=10` 碎片 + `ELITE_CORE=1` 核心（与词缀精英 `affix_shard=2` 分开） |
| 出现模式 | `rollExtra` 对主线局通用（首通也有）；**不是** D138 的「仅已首通普通版」闸门 |
| 对比 | 词缀精英 = 提升房内一只普通怪 + 随机词缀；奖励精英 = 额外刷一只专用 MM |

结论：奖励精英现在是「金皮木桩换材料」。词缀池已经扩到 Pack 3（设计 D181）；奖励精英这条线还空着，正适合做**每图一个固定轻招**（地图身份），而不是再叠一套随机词缀。

## 2. 参考游戏取舍

| 来源 | 机制 | 采用？ | 原因 |
|---|---|---|---|
| D3 Champion 共享词缀、Rare 领袖+小怪 | 包概念 | **部分** | 本服 Extra.ELITE 是**单只**，不做小怪包（刷图密度 / 门线风险） |
| D3 Mortar / Molten / Arcane | 预警地面效果 | **形状原则** | 已用于词缀 Pack 3；奖励精英改用**每图固定招**，避免与 mortar/molten 随机池撞车感 |
| D3 Jailer / Vortex / Waller / Reflect / Teleporter | 控场 / 墙 / 反伤 / 瞬移 | **否** | 与 D171/D181 否决表一致：软锁、落差、公式对账、leash |
| D3「低级精英词缀更少」 | 早期图压力轻 | **是** | Q01–Q03 招更轻、every 更长；Q04–Q07 略紧但仍 light |
| PoE 稀有三缀叠乘 | 多缀 | **否** | 一局一只、一招；禁止局内 atk/HP 乘区 |
| WoW M+ 少叠可读 | 原则 | **是** | warn ≥1.2s；与词缀精英同房时靠 every 错开 |
| 国服 1.12「精英点名砸地」 | 可读原型 | **是** | circle / line / cone / charge 全在现有导演形状库 |

## 3. 与词缀精英（D138+）的边界

| | 词缀精英 | 奖励精英（本包） |
|---|---|---|
| 闸门 | 仅已首通普通版 | Extra 权重，首通也可出 |
| 身份 | 随机词缀名（炽热/投弹…） | **图名奖励精英** + 固定招名 |
| 奖励 | `affix_shard`（2） | `extra_elite_shard/core`（10+1）不变 |
| 扩展方向 | 随机池扩包（Pack 2/3） | **每图 1 条固定轻招** |

## 4. 实现落点（给实现窗，本窗不动）

- 导演：`EmberRunDirector` 在 Extra.ELITE 刷出后，给该 Tracked 挂一张与首领相同的轻招表（`circle`/`cone`/`line`/`charge` + `light` + warn），数据可进 `ember-v1-runs.yml` 的 `roles.elite` 旁或独立 `elite_twists:`。
- MythicMobs：`EmberQ0xElite` 可继续无 Skills（招式走导演，与首领一致），避免双通道。
- 门禁：`tools/p1sim` 对「有/无奖励精英招」做少量格子 Δ 通关率（目标 ≤2pp，硬顶 3pp）；**不改**碎片/核心数量。
