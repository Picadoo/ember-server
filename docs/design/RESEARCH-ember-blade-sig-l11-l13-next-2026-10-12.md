# RESEARCH · 刃签 L11 / L13 另轨（TIP-AD HOLD 清债）· 2026-10-12

> **状态：** CLOSED · TIP-AF 批 A·M · docs-only · 2026-10-12 19:39 · 对接 tip `TIP-ember-blade-sig-l11-l13-research-thin-d12600-d12607-2026-10-12.md`  
> **起因：** TIP-AC `STAGE1_OFFLINE=FAIL`（S3-T/P 刃 L11/L13 可达超 D174 ±3pp）；TIP-AD 评测集砍出后防具门禁 PASS(patched)；TIP-AE 防具最小切片已 live **未动** L11/L13。总控点名下一 tip = **刃另轨研究**。  
> **硬禁：** **零抬表凑线 · 零 skill_mult 再拧 · 零 Affix densify · 零伤乘 · 零 glance twin · 零深化 densify · 零 sx20 · 不绑防具 stampable / 不回改 TIP-AE fits**

---

## 0. 一句话

L11/L13 是 **刃轨既有方差债**，不是防具升格引入的新乘区；评测排除已解绑防具开工。本窗只盘清 **为何超线 / 候选路径 / p1sim 门闩**，**HOLD 改 mods 与施工**，荐下一窗 **刃签 DESIGN stage0（HOLD 施工）** 或长期观察 HOLD。

---

## 1. 它们是什么（SoT）

权威：`CoreRpg/.../EmberSignature.java` `DEFS` · p1sim `tools/p1sim/mainline.py` `SIGS`（须同构）。

| ID | 图/槽/tag | 名 | 玩家可读收益 | 代价 / mods（摘要） |
|----|-----------|-----|--------------|---------------------|
| **L11** | Q06 · **blade** · guard | 霜封长刀 | 烬斩每命中 1 敌得 0.5% 最大生命护盾（最多 1%，5 秒，不叠） | `skill_mult` **0.9** · `taken_boss` **1.05** · shield 键 |
| **L13** | Q07 · **blade** · shape | 炉锁巨锤 | 烬斩改 **蓄力环斩**：按住 0.5 秒后落身边一圈 | `skill_charge` **0.5** · `skill_plus` 1 · 蓄力窗内普攻无效 |

补充：

- L11 **调律 L11b 未上架**（D184：W30 P2-1 −0.55 周超 ±0.5；D190 再扫仍 HOLD）。`ALTS['L11b']` 仅存 sim 备查（`taken_tele` 1.15），**非 live**。  
- L13–L15 同批新签；L13 **无** Alt。  
- tip-AD / stage0：**防具侧** = 原 charm 升格 `L02/L03/L06/L08/L10/L12/L14/L15`；**刃** L11/L13 与甲轨 **分轨**。

---

## 2. 实测超线（TIP-AC / RESULT · 可达口径）

来源：[`RESULT-ember-armor-signature-gate-stage1-offline-2026-10-12.md`](RESULT-ember-armor-signature-gate-stage1-offline-2026-10-12.md) · [`out-armor-sig-stage1-tol.md`](out-armor-sig-stage1-tol.md) · pairs 同因扩散。

硬线（D174 §8 精神）：可达格 mean\|Δ\|≤1 pp 且 **无格超 ±3 pp**。

| 变体 | 族 · 可达 | 代表最差 Δ | mabs | 判 |
|------|-----------|------------|------|----|
| L11 alone | burst | **−3.025**（q02c d0.5 · 贴边超） | 0.66 | ❌ |
| L11 alone | sustain | **−5.5**（q01c d0.3） | **1.02** | ❌ |
| L11 alone | scorch | −2.8 | 0.59 | 🟡 |
| L13 alone | burst | −8.2 | **1.85** | ❌ |
| L13 alone | scorch | −9.8 | **1.92** | ❌ |
| L13 alone | sustain | **−13.8**（q01c d0.3） | **2.70** | ❌ |
| L11+\* / L13+\* pairs | 多族 | 随 alone 扩散 | — | ❌（防具门禁已 exclude） |

对照透镜（同 RESULT）：

- **防具侧 alone**：可达 **0** 条超硬线。  
- **TIP-AD `judge-s3`**：excl L11/L13 后 S3-T/P **PASS**；S4 继承 PASS。  
- 结论：**污染源 = 刃 alone**，不是甲升格 / 烙印扩甲。

---

## 3. 为何超线（分类 · 非抬表叙事）

### 3.1 L11（guard · 贴边 + sustain 深负）

| 因子 | 说明 |
|------|------|
| **已有 skill_mult 0.9** | 烬斩伤恒压；在早期图 / 低 dodge 格更吃紧 → sustain/burst 下偏 |
| **taken_boss 1.05** | 受首领伤↑；与「护盾换打法」叠在高压力格放大负偏 |
| **护盾收益 sim 难等权** | 短窗 / 命中数依赖；可达格护盾兑现不均 → 方差而非「少乘一点就能绿」 |
| **L11b 历史** | 换代价仍砸 W30 → **禁**把「再拧一档代价」当默认出路 |

**分类标签：** `feel/cost-structure variance`（护盾兑现 vs 恒定伤压）· **非**「表低了抬一下」。

### 3.2 L13（shape · skill_charge 大方差）

| 因子 | 说明 |
|------|------|
| **skill_charge 0.5** | 蓄力窗丢普攻；sim 在短图 / 高机动格惩罚极重（RESULT 点名 charge 方差） |
| **环斩几何** | 与对准扇形基线利用率差大；族间（burst/scorch/sustain）落点不一致 |
| **无 Alt / 新签** | 缺「同收益另一代价」缓冲轨 |

**分类标签：** `identity/charge timing variance` · 重症于 L11 · 更倾向 **改签名身份或长期 HOLD**，而非小数点凑线。

### 3.3 明确不是

- 不是 TIP-AE 烙印扩甲引入  
- 不是 STAMP / 掉落 / 甲 stampable 空集问题  
- 不是「缺 Affix / 伤乘 / skill_mult 再抬」能合法修的洞

---

## 4. NON-goals（本轨钉死）

- **不**绑防具 stampable / 不回改 defense-side fits / 不扩掉落  
- **不**抬表凑 S3 绿（Affix / STAMP / 伤乘 / 徽记价）  
- **不**再拧 `skill_mult`（L11 已 0.9；上拧 = 假变强；下拧凑线 = 抬表同源禁）  
- **不**把 TIP-AD exclude 当成「签已删 / 已废」——排除 = **评测成员**，定义仍 live  
- **不**本窗改 `DEFS` / `SIGS` 数值 / 开施工 tip / 业务 jar

---

## 5. 候选路径（研究结论 · 非开工令）

| # | 路径 | 含义 | 利 | 弊 / 门闩 | 荐级 |
|---|------|------|----|-----------|------|
| A | **评测排除 only（已做）** | TIP-AD：甲门禁不采 L11/L13 | 防具轨已通 | 刃债仍 live；玩家仍可戴到「怪」签 | **已 CLOSED** |
| B | **长期观察 HOLD** | 定义保留；甲门禁继续 exclude；不改 mods | 零风险；等试玩真投诉 | 图鉴仍有两把「像坏的」刃 | **可接受底线** |
| C | **手感/代价重设 · DESIGN stage0** | 改 **非伤乘** 结构（例：L11 护盾窗/触发叙事；L13 蓄力时长/蓄力中可取消或脉冲反馈）后 **p1sim 重跑 alone** | 对准真方差 | 须独立 DESIGN + S3-T alone 门禁；**禁** skill_mult/Affix | **L11 优先候选** |
| D | **改签身份 resignature** | 重写 L11 或 L13 玩法标签（仍 blade；可换 tag/收益类型） | L13 大方差最对症 | 高成本；须 stage0→离线→显式开工；禁夹带甲轨 | **L13 优先候选** |
| E | **复活/新开 L11b 类 Alt** | 同收益另一代价 | 历史已试 | D184/D190 **已 FAIL W30**；默认 **否决**，除非全新代价集 + 新 W30 门禁 | **默认否** |

**荐（本 RESEARCH）：**

1. **短线：** 维持 A + B（甲轨不动；刃 live HOLD）。  
2. **下一可施工前文档：** 开 **刃签独立 DESIGN stage0**（单主题可拆：先 L13 身份/蓄力，或先 L11 护盾代价结构）· **HOLD 改码** 直至 alone 门禁草案过线 + 总控显式开工。  
3. **不**荐本窗直接调律数字；**不**荐 attune 六槽对齐抢轴（总控本窗已否）。

---

## 6. p1sim 门闩（解冻刃施工才启用）

任一刃改动 tip 开工前须书面满足：

| 门 | 要求 |
|----|------|
| **G-alone** | `mainline.py tol`：L11 与/或 L13 **alone** 可达 · 三族 · D174 硬线（mean\|Δ\|≤1 且无格超 ±3） |
| **G-pair** | 若仍进组合透镜：含该刃的合法 pairs 可达不过线（或显式声明「仍 exclude 出甲门禁」） |
| **G-W30** | 若动代价/收益结构：至少一条代表佩戴路线 \|ΔW30\|≤0.5（禁只靠抬掉落/STAMP） |
| **G-X** | 清单：无新伤乘 · 无 skill_mult 再拧 · 无 Affix densify · 无改甲 stampable |
| **G-split** | 文首硬句：刃轨 ≠ 甲轨；不回滚 TIP-AE |

FAIL → 停；**禁抬表凑线**。PASS ≠ 自动开工。

---

## 7. 与甲轨 / 其它轴边界

| 轴 | 关系 |
|----|------|
| 防具签名 TIP-AE live | **正交**；本 RESEARCH **不**追加甲 stampable / 不改 fits |
| TIP-AD exclude | **保留** 直至刃 alone 门禁绿且总控决定重新纳入甲评测集 |
| 烬斩手感 TIP-Y | 门闩仍有效；L13 蓄力「手感」若进施工，走 **刃签 tip**，不借 skill_mult / 不重开 TIP-K cue densify |
| attune 六槽 / 主线 loot | **本窗不选** |

---

## 8. 推荐下一窗（给总控）

| 优先 | 产出 | 形态 |
|------|------|------|
| **P1** | **刃签 L11/L13 DESIGN stage0**（规格 + 门禁表 + HOLD 施工） | docs-only tip；可先钉 L13 或 L11 单刀 |
| P2 | 观察 HOLD（无投诉则不改） | 停窗 / 无码 |
| 禁 | 当场改 mods · 抬表 · skill_mult · Affix · 绑甲 stampable · sx20 |

---

## 9. 本 RESEARCH 验收

- [x] 写明 L11/L13 是什么（槽/收益/代价）  
- [x] 写明 TIP-AC 可达超线数字与防具侧净对照  
- [x] 分类超线原因；NON-goals 含不绑甲、不抬表、不拧 skill_mult  
- [x] 候选路径 A–E + 荐级；下一窗 DESIGN stage0 或观察 HOLD  
- [x] p1sim 门闩 G-alone / G-pair / G-W30 / G-X / G-split  
- [x] **零**数值终表、零 jar 指引  
