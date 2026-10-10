# DESIGN · 经济 yml 真源搬家 · Stage0 盘点（2026-10-11 · D942）

> **状态：文档 only · 不改数值 · 不开大搬家。**  
> **上游：** ARCH §S2；`DESIGN-ember-v1-economy-yml-2026-10-06.md`（E1 SoT D224 / E3 p1sim D225）；`REG-ember-source-sink-cap-2026-10-05.md`；D933–D941 健康窗。  
> **本窗产出：** 下一刀「全量发放/价目收进 economy yml」的**范围清单与禁区**，供以后开 Stage1 时照单施工。

---

## 0. 现状一句话

`ember-v1-economy.yml` **已经是** `EmberEconomy.amount()` 的 SoT（缺/坏/漂移 fail-closed；Java `golden` 二次断言）。  
**还没搬家的**是：大量**价目仍住在业务 yml / Java 常量**里，只经 `spend*` 扣账、不经 `amount(id,key)` 读数。

---

## 1. 已在 amount() 路由上的键（Stage0 实测 · 2026-10-11）

源码 `EmberEconomy.amount("…","…")` 调用共 **19** 键，**全部**在 live `ember-v1-economy.yml` 有叶子（0 缺失）：

| 行 | 键 |
|----|-----|
| S01 | coin / shard / bone / core / xp / mark |
| S02 | coin |
| S03 | shard / core |
| S09 | per_rule |
| S23 | daily.coin / daily.xp / makeup_per_month / sigmark_fallback_coin |
| S24 | coin_total / xp_total / top_min |
| C07 | marks |
| C14 | coin |

另有登记行带 golden、但运行时**不走** `amount()`（价在 forge / growth / convert / brand / gem 等）——见 §2。

---

## 2. Stage1 候选搬家面（仍零改数值）

| 面 | 现行住处 | 登记行 | Stage1 动作（提案） | 风险 |
|----|----------|--------|---------------------|------|
| 工坊强化/升阶/精工/成色价 | `EmberUpgradeRules` + growth/forge 常量 | C03–C06 | yml 叶子 + `amount` 双读；forge 读表 | 中（路径多） |
| 深渊层费 | `ember-v1-runs.yml` abyss | C08 | mirror 进 economy 或 `$ref` | 中 |
| 洗练价 | EmberPay / growth | C11 | 同上 | 中 |
| 烙印 / 调律 | EmberSignature 常量 | C12 / C13 | 同上 | 低–中 | → **D957–D961 已切** |
| 烙纹合成/定向 | EmberBrandRules | C20 / C21 | 已有 C20.shard；定向币补叶 | 低 |
| 随机锻 / 转化 | ForgeRoll / ConvertRules | C22 / C23 | 已有叶子；确认 Java 读表 | 低 |
| 宝石合成 | EmberGemAcquire.CRAFT_* | **C24** | D935/D941 已登记+钉测；下一刀可让 craft 扣料也读 amount | 低 |
| 天赋学/重置 | growth yml | C09 / C10 | 已部分钉；统一只读 economy | 低 |
| 国庆店 | festival yml | C18 | PARTIAL；化妆品窗前不动大表 | 低（HOLD 感） |
| 短征 S40–S58 | ShortRules / runs yml | S40–S58 | 标签行已有；数量若硬编码则 mirror | 中 |
| Admin smoke | EmberCommand | **S60** | NONE；永不进 p1sim | — |

**明确不做（本 Stage0 / 近窗）：**

- C15 / C16（外观 / 扭蛋兑券 · 暂停）
- 把徽记账户强行标 FULL 进 p1sim（需独立设计窗，见 §3）
- 合并徽记与印记、新货币、改任何 golden 数字

---

## 3. p1sim modelGaps（stub 政策 · D946）

`EmberEconomy.modelGaps()` 钉死（含 D937 **S60**）：

`S07 S08 S09 S16 S17 S18 S27 S30 S31 S32 S36 S37 S39 S60 · C10 C12 C13 C16 C17 C18`

**政策：** 缺口保持 `Model.NONE` / `PART` / `OUT`，**禁止**为了「测例绿」把未建模行改成 FULL 空 stub。  
真要进 p1sim：单独 tip → 设计账户语义 → 再改 `p2econ` / `rules.py`（非本 Stage0）。

---

## 4. Stage1 建议切片（以后开窗）

1. **最低风险：** C20/C22/C23/C24 Java 扣款路径确认只读 `amount()`（已有叶子）。 → **D948–D951 C20/C24**；**D952–D956 C22/C23**；**D957–D961 C12/C13**（数值不变）。  
2. **中：** C03–C06 工坊价 mirror → economy + UpgradeRules 双读。  
3. **后：** C08/C11/C12/C13；短征数量 mirror。  
每刀：`EmberEconomyTest` 金样 + p1sim 逐位 + 短冒烟；**bv 仅当数字变才动**（本搬家 bv 不变）。

---

## 5. 验证（Stage0 本文）

- 文档落地；ARCH / REG 互链。  
- 可选薄码：`EmberLedgerGlance` 满包暂存收口（D944）+ 纯测（D945）——与搬家正交。

