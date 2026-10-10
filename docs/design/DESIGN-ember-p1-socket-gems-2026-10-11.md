# DESIGN · Ember P1 孔石（Socket Gems）分期规格 · 2026-10-11

> **状态：** 批 A·M PASS · Stage1 已施工（数据 only）· Stage2+ 仍禁战斗/掉落/菜单 / 禁改线上 enhance.yml  
> **研究：** [`RESEARCH-ember-gem-reference-2026-10-11.md`](RESEARCH-ember-gem-reference-2026-10-11.md)  
> **STATUS tip：** [`STATUS-ember-p1-socket-gems-2026-10-11.md`](../status/STATUS-ember-p1-socket-gems-2026-10-11.md)  
> **硬禁：** 无 K3 · 无 AFK 表抬 · 无 sx20 · 无新永久伤乘区 · 签名仍只改打法  
> **流程：** tip → 批 A·M → 再施工（本文件 = tip 内容；批注后才 Stage1+）

---

## 0. 一句话

把旧四孔石（锋/稳/汲/疾）**迁进 P1**：孔位活在 `EmberItemData` 上，攻防进 `EmberFormula` 的 **flat 旁路（有帽）**，吸血/移速走受控层且复用旧帽；**绝不**把 P1 装接回旧 `/corerpg socket` + AttributePlus lore。

---

## 1. 明确不做（NOT）

| 禁 | 原因 |
|----|------|
| 把 `ember_v1_*` 加进 `enhance.yml` `whitelist_ni_ids` / `socket` 表 | 旧系统 P1 下已宣告不适用；与 `EmberUpgradeRules` 双轨 |
| 对 P1 玩家开放 `/corerpg socket` / `enhance`（legacy_gate 白名单放行） | 命令面仍指向 GearLore 旧栈 |
| 孔石写 AP / SX lore `物理伤害` 让 `StatService` 在 P1 结算 | P1 已旁路 legacy stats；再开 = 双结算 |
| 新 `% 伤害` / `more` / 暴击伤乘 / 技能伤乘孔 | 破预算；签名哲学禁止 |
| 孔石抬 AFK 效率表 / K3 / sx20 | 站立禁令 |
| 一上来做装甲四孔、多链、随机属性石 | 范围膨胀；先刃+符 |
| 无 p1sim 帽检验就上数值 | 违反「within range」政策 |

---

## 2. Stage0 — 库存（本文 + RESEARCH，已完成文档侧）

| 项 | 结论 |
|----|------|
| 旧四石 NI | 可复用 ID 与文案心智；数值须重标定 |
| 旧孔解锁 | 刃 2 孔 @+0/+5；符 1 孔 @+3 — **节奏可参考，勿绑旧强化命令** |
| P1 强化 | `EmberItemData.enhance` 0–10 + `EmberUpgradeRules`；与旧 `GearLore` 强化无关 |
| D535 `EmberGemPath` | 粘性自动镶；P1 无孔字段时无效 — Stage4 再接线或新路径 |
| 锻造迁移 | 旧 `EnhanceService.moveEnhance` 搬孔；P1 升阶须定义「孔是否保留 / 石是否退回」 |

**验收（Stage0）：** RESEARCH + 本 DESIGN 入库；无代码。

---

## 3. Stage1 — 数据模型（下一步施工范围 · 仍无结算）

### 3.1 目标

在 `EmberItemData`（及 `cr_p1_item` / NBT `ember_v1`）上增加**可 HMAC 的孔位状态**，不改战斗。

### 3.2 建议字段（草案，批注后锁定）

| 字段 | 含义 |
|------|------|
| `sockMax` | 该件最大孔（刃 2 / 符 1；装甲暂 0） |
| `sockMask` 或 `sockUnlock` | 已解锁孔位 bitmask / 计数（由 enhance 门槛推导亦可，存盘可冗余） |
| `sock0`, `sock1` | 镶入的 gem code（0=空；1=sharp 2=steady 3=drain 4=gale）或短 NI 枚举 |

版本策略（二选一，Stage1 开工前定死）：

- **A（推荐）：** `DATA_VERSION` 升 3，canonical 追加 `sk:<codes>`；旧 v2 读入视为无孔。  
- **B：** 仍 v2，但把孔编码进现有「item keys」旁路 — 更挤，不推荐。

### 3.3 解锁规则（草案）

| 槽 | 孔数 | 解锁 |
|----|------|------|
| blade | 2 | 第 1 孔：enhance ≥ 0（即有装即开）；第 2 孔：enhance ≥ 5 |
| charm | 1 | enhance ≥ 3 |
| armor | 0 | Stage1 **不做** |

升阶（T1→T2→T3）：**保留已镶宝石与解锁状态**（与成色/精工保留同哲学）。分解：仅 `source=drop`；宝石**退回背包**或进邮件（Stage3 定）。

### 3.4 Stage1 明确不做

- 不写 `EmberFormula` 加法  
- 不改掉落  
- 不开菜单  
- 不碰 `enhance.yml` 线上表

### 3.5 验收

- 单测：编解码 / HMAC / 旧 v2 物品仍验证  
- lore 只读展示「孔：空 / 锋利石」（可先占位，真 UI 在 Stage4）

---

## 4. Stage2 — 四石映射进公式 + 预算

### 4.1 映射原则

| 石 | 旧 | P1 目标 | 进哪 |
|----|----|---------|------|
| sharp 锋利 | AP +2 攻 | **flat A**（小额） | `EmberFormula`：`B = A×growth + levelAtk + gemFlatA`（gem 在括号**外**，与等级段同类） |
| steady 稳固 | AP +2 防 | **flat D** | `defense = charmD(tier) + gemFlatD` |
| drain 汲取 | 1% 吸血 | 普攻吸血 % | 受控层（P1 需显式接线）；帽 ≤ **5%**（旧 `caps.life_steal_pct`） |
| gale 疾风 | 2% 移速 | 移速 % | 仅当 P1 `refreshP1` **已支持**或 Stage2 同期加受控层；帽 ≤ **8%** |

禁止：sharp 写成 `×(1+x)` 伤乘；禁止多颗 sharp 叠出「第二把武器」。

### 4.2 功率预算（初稿 · 必须 p1sim 复核）

旧 sharp +2 对 T0 A=12 过强。建议：

| 石 | 建议初值 | 全装硬帽 | 备注 |
|----|----------|----------|------|
| sharp | **+1 A / 石**（非 +2） | 全角色 gemFlatA ≤ **2**（刃两孔都锋利时） | ≈ T0 的 8.3% / T3 的 1.4%（相对裸 A） |
| steady | **+1 D / 石** | gemFlatD ≤ **2** | 相对 T0 D=2 显著、相对 T3 D=14 温和 |
| drain | **1% / 石** | life_steal_pct ≤ **0.05** | 与旧帽一致；P1 普攻才吃 |
| gale | **2% / 石** | move_speed_pct ≤ **0.08** | 与旧帽一致；不影响 B |

p1sim 闸门（Stage2 必过）：

- 相对无孔同装：期望满蓄 DPS / EHP 偏移落在既有「within range」带（与天赋 sidegrade 同级，**远小于**升一阶或 +3 强化）  
- 两孔双锋利不得接近「A 升一阶」的跨度（T2→T3 A 42→70）  
- 吸血不改变 烬斩（D06：技能不吸血）

### 4.3 与签名 / 套装

- 签名：仍只改行为；孔石不加签名乘区  
- 套装被动：不读孔；孔不替代两件套  

### 4.4 验收

- `EmberFormula` + p1sim 导出 diff 表进 `tools/p1sim/out-gem-*.md`  
- 单测：帽夹紧、双锋利、无孔回归 bit-identical（gemFlat=0）

---

## 5. Stage3 — 获取（掉落 / 合成）

| 来源 | 草案 | 禁 |
|------|------|----|
| 工坊 / 兑换 | 碎+骨尘（或胚）合成指定石；币小额手续费 | 不进氪金强制 |
| 分解副产 | 低概率出「未鉴定石」或碎石 | 不抬 AFK 表 |
| 周常 / 精选 | 周箱 1 颗自选 | 不绑 sx20 |
| 副本 | **可选** 挑战/深渊低权掉；主线 Q01–Q07 **不**塞进 MM 表（对齐旧「不进地窟 MM」） | 不替代装备掉落 |

拆卸：免费或小币；石返还（避免永久锁构筑）。`unique_gem_ids: false` 可保留（允许多锋利），但 **flat 帽**仍生效。

---

## 6. Stage4 — UI / 命令 / 粘性路径

| 面 | 草案 |
|----|------|
| 命令 | `/corerpg p1 socket …` 或工坊 TrMenu 页（**新**命令面，不复用 legacy `socket`） |
| 菜单 | 手持 P1 刃/符 → 显示孔 → 点选背包石镶入 |
| D535 | 改为读 `EmberItemData` 空孔；或新 sticky「P1 孔路径」；旧 Enhance 路径保持给遗物 |
| lore | 孔行由 P1 lore builder 写；不写 AP 属性行 |

---

## 7. 分期验收总表

| Stage | 交付 | 代码？ |
|-------|------|--------|
| 0 | RESEARCH + DESIGN + STATUS tip | 否 |
| 1 | EmberItemData 孔字段 + 编解码/HMAC/库表 | 是（无战斗） |
| 2 | 公式 flat + 吸血/移速受控 + p1sim | 是 |
| 3 | 合成/掉落/拆卸经济 | 是 |
| 4 | 菜单/命令/GemPath | 是 |

---

## 8. 批注栏

- [x] 总控批 **A · 方案 M** · 日期 2026-10-11  
- [x] Stage1 版本策略锁定：**A（v3）**  
- [x] Stage2 初值：经 p1sim FAIL→收紧2 PASS · sharp +0.1 帽0.2 T2+ / steady +0.1 帽0.2 / drain1% / gale2% · 接线 2026-10-11  

**默认提案（待批）：方案 M = Stage1 走版本 A（v3）+ 上表初值；Stage2 前必须 p1sim 绿。**
