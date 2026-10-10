# DESIGN · Stage2 全量发放 yml 真源搬家 · Stage0 盘点（2026-10-11 · D1040）

> **状态：Stage2 A–J COMPLETE；safe 扫完；review/unsafe 未开；数值不变（AFK 层表未抬）。**  
> **上游：** Stage1 COMPLETE（sink `amount()`）；Stage0 经济价目盘点 D942；健康扫 D1030–D1038。  
> **本窗：** 列出 **发放（source / grant）** 仍散落在 Java 常量 / 业务 yml / 运行时 Grant 表的位点，分级 **safe / review / unsafe**，供以后 Stage2 切片施工。

---

## 0. ARCH 一文件薄债结论（D1039）

扫 `ARCH-ember-systems-map` §6.1 与 S0/S4 残余：

| 项 | 状态 | 本窗 |
|----|------|------|
| S0-9 世界传送兜底 | **HOLD** | 不动（需新入口证据） |
| 六槽装备 | **HOLD** | 需显式开工窗 |
| C15/C16 | 暂停令 | 跳过 |
| C19 CoreGacha | Stage0 已有 | 外库，不开 |
| Stage1 sinks | COMPLETE | — |

**无剩余「一文件可改、零设计」薄债。** 下一结构刀 = Stage2 发放真源（本文）。

---

## 1. 已 SoT / 已读 `amount()` 的发放面（勿重复搬家）

| 来源 | 键 | 读表处 |
|------|-----|--------|
| S01 通关基线 | coin/shard/bone/core/xp/mark | `EmberRunRules` settle Grant；`EmberForgeService.sources` |
| S02/S03 | 宝箱/精英 | 既有 amount 调用 |
| S09 誓约 | per_rule | `EmberPledgeService` |
| S23/S24 | 签到/在线 | `EmberSignService`（与 ember-v1.yml 双源校验） |
| S60 | admin smoke coin:1 | `EmberCommand` |

`EmberRunRules.BASE_*`（300/24/6/2/120/1）仍为 **金样常量**，发放路径已改读 S01；Stage2 可删冗余或改 `BASE_* → amount` 别名（低风险切片 A）。

---

## 2. 发放点清单（2026-10-11 实测）

### 2.1 `EmberEconomy.grant*` 调用（12）

| 类 | 源 | 分级 | 注 |
|----|-----|------|-----|
| EmberCommand | S60 补齐币 | **safe** | 已标签；金额动态补差，叶子 coin:1 仅烟测 |
| EmberFestival | S27 badge | **review** | 数量来自兑换逻辑 n；model PART |
| EmberSeason | S19 badge | **review** | 赛季榜；非固定叶子 |
| EmberRushService | rush badge via sourceForRushMode | **review** | 配置 rushBadges |
| EmberRunService deliver | coin/xp/mat via Grant.amount | **safe-infra** | 金额在 Grant 构建时定；真源在 rules/settle |

### 2.2 Grant 表构建仍用 Java 常量（Stage2 候选）

| 位点 | 现行 | economy 叶子 | 分级 |
|------|------|--------------|------|
| `EmberSignature.CLEAR_MARKS` → S08 | 1 | S08 已有 insignia/stamp 语义 | **safe** 切片 B：`clearMarks()`←amount |
| `EmberSignature.FC_MARKS` → S07 | 3 | S07.insignia:3 | **safe** 切片 B |
| `EmberRunRules.BASE_*` | 镜像 S01 | 已 amount | **safe** 切片 A：常量改委托或删死引用 |
| 短征 S40–S58 clear/fc | runs `short:` + economy 叶子 | 多已登记 | **done** 切片 C |
| 深渊/挑战结算 | 走 S01 + 品质表 | — | **safe-infra** |
| AFK S22 | ember-v1.yml afk | 有 golden | **done** 切片 D（仅帽；层表未抬） |
| 图录 S33 / 宝箱 S34 / 起步 S35 | 标签+部分叶子 | — | **done** D/E/I |
| Mail/Arena/Cash/Auction/Forge(旧) | 非 P1 / LegacyGate | — | **unsafe** 本 Stage2 不做 |

### 2.3 明确不做

- C15/C16 化妆品；C19 gacha 数值  
- 把 modelGaps 改 FULL  
- 合并徽记账户进 p1sim（需独立设计）  
- 旧路径发奖（S0 已封）

---

## 3. 建议切片顺序（以后开窗，每刀零改数值）

1. **A** — `BASE_*` 与 S01 金样单一真相（委托 `amount` 或测试钉死未使用）。 → **D1045–D1050 已切** `baseCoin()`…`baseMark()`。  
2. **B** — S07/S08 徽记 `FC_MARKS`/`CLEAR_MARKS` ← `amount`（与 Stage1 imprint 同型）。 → **D1051–D1055 已切**（含 stampRate）。  
3. **C** — 短征 clear/fc Grant 全路径审计 → 缺则补读表。 → **D1056–D1058 已切**（本已 amount；补 dailyCap）。  
4. **D** — AFK/图录 → **D1063–D1072 已切**（图录 stageCoin；AFK 仅 daily_kills/offMax 接线，层表不动）。  

每刀：`EmberEconomyTest` + SourceMap + p1sim validate；bv 仅数字变才动。

---

## 4. 与 Stage1 的边界

| | Stage1 | Stage2 |
|--|--------|--------|
| 对象 | **sink** 价 / 扣款 | **source** 发放量 |
| 状态 | COMPLETE | Stage0 本文 |



## D1073–D1082 Stage2-E
S02/S03 helpers + S34 chest_weight in extraWeights(); settle already amount.

## D1083–D1090 Stage2-F
S04/S05 Variety pin + helpers; nums identical.

## D1091–D1100 Stage2-G + COMPLETE
S06 first_clear pin via s06Amount; Stage2 grant SoT slices A–G done.

## D1101–D1108 Stage2-H
S10 rotation bonus_marks/weekly_cap pin.

## D1109–D1120 Stage2-I
S11.mark + normalBonus; S12 raid weeklyCap; S14 rate; S15/S35 potions.

## D1121–D1128 Stage2-J
S20 daily bounty + S21 variety coin SoT pin.

### Safe sweep note (D1128)
Remaining DESIGN rows marked **review** (S16–S19/S27/season/rush badges) or **unsafe** (legacy) — skipped per Stage2 gate.
