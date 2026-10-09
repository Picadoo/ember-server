# 余烬 · D330 后旁路设计文档 Stage2 诚实勘误（决策页）

STATUS=**待批 A**（观察期 docs-only 落字 · 零玩法 · 零 TrMenu · **≠关观察** · **不**抢 K3 · **不**抢 D345/D346 薄抽 · **不**改 ×0.97）· 2026-10-09 · 上游 tip `STATUS-ember-next-hard-debt-six-slot-docs-sidepath-honesty-need-design-2026-10-09.md`

> **一句话：** D330 收了 gear-structure/OPS，旁路稿仍写「不改方块甲 / 日后 4 件」，offhand pilot 还**假称** ember_set 有该句。荐 **方案 M**：旁路勘误清单；批后另号落字。

### 0. 证据

| # | 文件 | 过时 / 假引用 | 线上事实 |
|---|------|---------------|----------|
| E1 | `DESIGN-ember-rpg-systems.md` §3.3 | 「日后 4 件（甲）再扩；本期先做 2 件，**不改方块甲**」 | Stage1 F + Stage2 四件套已上线观察（bv62） |
| E2 | `DESIGN-ember-character-storage.md` §2 | 「日后 4 件再扩；**不改方块甲**」· 本期仅 2 件 | 同上；护甲页/装备页已诚实 |
| E3 | `design-ember-offhand-slot-pilot.md` 团戒行 | 称 `ember_set` **明文**「日后 4 件（甲/饰品）再扩」 | D331 后 `ember_set` 为同袍 + P1 觉醒/四件套 −3%；**无**该句 |
| E4 | `DESIGN-ember-offhand-budget-decision-2026-10-07.md` §8 | 证据表「gear-structure…六槽未上线」 | D330 已改 gear-structure 正文；表未跟 |
| E5 | D330 DESIGN/STATUS | 范围 = gear-structure + OPS + staged README | **未**覆盖 E1–E4 |
| E6 | （不做）`DESIGN-ember-raid.md`「不改方块甲 / Paper」 | 指团本最小实现**不改原版甲机制/Paper** | 语义不同；**本债不改** |

**玩家感知：** 本窗服务策划/运维真源；避免「甲未做」误读；**不**改玩家菜单（D345/D346 薄抽并行）。

**本窗不做：** 关观察、K3、改 ×0.97、动 TrMenu、重开 D330 主文件大改、全文重写史稿、抢薄抽文件。

---

## §1 方案表（荐 M）

| 方案 | 内容 | 评价 |
|------|------|------|
| **M（荐）** | §2 勘误清单：E1–E4 定点改口径 + 指针；史段落旁注「历史」可选 | **荐** |
| L | M + 全文重写 rpg-systems / character-storage / 合并全部旧装备稿 | 过厚 |
| W | 借勘误改玩法/关观察/开 K3 / 动菜单抢薄抽 | **否决** |

---

## §2 勘误清单（批 A 后另号落字 · 本文钉口径）

### 2.1 `DESIGN-ember-rpg-systems.md` §3.3 套装

**改为（口径）：**
- 2 件套（刃+护符同族）→ 族觉醒（P1）；另有「余烬同袍」= 团戒+刃（见 `ember_set` / `set.yml`）。  
- **护甲四件套已上线观察中**（Stage2 档 C：两件套已激活 + 同族掉落阶 T2+ 甲≥2 → 受伤 ×0.97 / −3%，不进 B/H）。  
- 指针：[`DESIGN-ember-gear-structure-2026-10-06.md`](DESIGN-ember-gear-structure-2026-10-06.md)（D330）· OPS · Stage2 set-bonus DESIGN。  
- **删除或划掉**「日后 4 件再扩 / 本期先做 2 件 / 不改方块甲可用 NI 饰品槽」作现行口径（可留一行「历史：上线前曾 HOLD 方块甲扩展」）。

### 2.2 `DESIGN-ember-character-storage.md` §2 套装

**总纲改为：** 刃+护符族觉醒 + Stage2 护甲四件套（观察中）；同袍戒+刃仍独立。  
**定案表「本期」行：** 改为「现行：2 件觉醒 + 四件套减伤已部署观察；细节见 gear-structure / OPS」——**勿**再写「仅 2 件 / 不改方块甲」。  
菜单/命令占位句可保留并加「四件套见护甲页」。

### 2.3 `design-ember-offhand-slot-pilot.md` 团戒行

**删假引用**「明文『日后 4 件…』」。  
**改为：** 菜单 `ember_set`：同袍戒+刃说明 + P1 觉醒/四件套 −3% 短指针（D331）；团戒仍不进 accessory 白名单 / 不当护符叠。

### 2.4 `DESIGN-ember-offhand-budget-decision-2026-10-07.md` §8 证据表

| 原 | 改 |
|----|-----|
| gear-structure「2 槽权威；Stat 不进 P1；**六槽未上线**」 | 「2 槽权威史；Stat 不进 P1；**六槽已上线观察中（D330 勘误后正文）**」 |

正文历史批注/关窗说明**不删**；只修证据表误导行。

### 2.5 明确不动

| 文件 / 项 | 理由 |
|-----------|------|
| `DESIGN-ember-gear-structure` / OPS / staged README | D330 已收 |
| `DESIGN-ember-raid.md`「不改方块甲 / Paper」 | 原版机制约束，≠「P1 甲未做」 |
| `ember_hub_legacy.yml` / 任何 TrMenu | 非本债；legacy 非主路径 |
| D345/D346 薄抽 STATUS、tmp 证据 | **勿碰** |
| jar / ×0.97 / set_bonus / bv / live yml | 不动 |

### 2.6 验收（落字另号）

| ID | 步骤 | 预期 | 中止 |
|----|------|------|------|
| D1 | `rg` rpg-systems / character-storage | 无现行口径「不改方块甲」「日后 4 件再扩」作现状 | 仍作现行承诺 |
| D2 | `rg` offhand-slot-pilot | 无「ember_set…日后 4 件」假引用 | 仍假称菜单文案 |
| D3 | offhand-budget §8 | 无「六槽未上线」作现状 | 与 D330 正文矛盾 |
| D4 | TrMenu / 开关 / bv | 本号未改 | 误动菜单或开关 |

**绿出口：** D1–D4 PASS。  
**回滚：** 还原上述 docs 行。

### 2.7 岗位

| 角色 | 职责 |
|------|------|
| 策划 | 批 A · 本规格 |
| 文档 | 批后另号按 §2 落字 |
| 总控 | 批 A；**≠关观察** |

---

## §3 不动与否决

| 项 | 状态 |
|----|------|
| ×0.97 / set_bonus / F / 价表 / jar / TrMenu | 不动 |
| 提前关观察 / 部署 K3 / 样本 R / Pack6 / 天赋 / 灰印 | 否决 |
| 抢 D345/D346 薄抽 | 否决 |
| 重开 D330 主权威大改 / 全文重写史 | 否决（本债旁路定点） |

---

## §4 批注栏

- [ ] **批 A：采纳方案 M** → 另号按 §2 落字  
- [ ] 升级 L（不荐）  
- [ ] 否决 / 改派  

**总控批注（待填）：** _

---

## 变更记录

| 日期 | 事件 | 谁 |
|------|------|-----|
| 2026-10-09 | 初稿 · 待批 A · 荐 M · 上游 D330/D331；菜单主路径已 D329–D346 | 策划执行手 |

*旁路 docs Stage2 诚实 · 待批 A · 荐 M · docs-only · ≠关观察 · 不抢 D345/D346/K3。*
