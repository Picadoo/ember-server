# DESIGN · 刃签 L13 蓄力身份（stage0 · HOLD 施工）· 2026-10-12

> **状态：** APPROVED stage0 · TIP-AG 批 A·M · **HOLD 施工** · 2026-10-12 19:41 Asia/Shanghai  
> **文首硬句：** 本文件 **APPROVED stage0 前可批规格**；**施工须** L13 alone 门禁过线 **且** 总控 **显式开工**。本 tip / 本 DESIGN **不是**开工令。**零改 mods · 零 jar · 零插件派单** 直至显式开工。  
> **对接 tip：** [`TIP-ember-blade-sig-l13-design-stage0-thin-d12608-d12615-2026-10-12.md`](../tips/TIP-ember-blade-sig-l13-design-stage0-thin-d12608-d12615-2026-10-12.md)  
> **上游：** TIP-AF CLOSED docs-only `f6e16ea46` · [`RESEARCH-ember-blade-sig-l11-l13-next-2026-10-12.md`](RESEARCH-ember-blade-sig-l11-l13-next-2026-10-12.md) · TIP-AD exclude 保留 · TIP-AE 甲轨正交 · 防具 stage0 平行结构 [`DESIGN-ember-armor-signature-overlay-gate-stage0-2026-10-12.md`](DESIGN-ember-armor-signature-overlay-gate-stage0-2026-10-12.md)  
> **硬禁：** 零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin · 零深化 densify · 零 sx20 · **零 skill_mult 再拧** · 零 ForgeChoice·wear·pity·awkDelta densify · **不绑防具 stampable / 不回改 TIP-AE fits** · **L11 HOLD 另轨（本窗不钉 L11 规格）**

---

## 0. 一句话

把 RESEARCH 已点名的 **L13 炉锁巨锤** 钉成可批 **蓄力身份** stage0：对准 `skill_charge` 三族大负方差，只允许 **改手感/身份结构**（非永久伤乘）；施工 **HOLD** 至门禁 + 显式开工。

---

## 1. 问题陈述（从 RESEARCH）

### 1.1 现网定义（SoT · 只读）

| 项 | 现网 |
|----|------|
| ID | **L13** · Q07 · **blade** · tag `shape` · 名 **炉锁巨锤** |
| 玩家收益 | 烬斩改 **蓄力环斩**：按下 **0.5s** 后落身边一圈（背也打） |
| 代价 / mods | `skill_var` 1 · **`skill_charge` 0.5** · `skill_plus` 1；蓄力窗内 **普攻无效**；CD 从按下起算 |
| Alt | **无**（L13–L15 新签；无调律轨） |
| 实现锚 | `EmberSignature.DEFS` · `SkillService` skill_charge 前摇 · `EmberCombatListener` 蓄力中取消近战 |

对照：同族 **L09** 为 **瞬时** 环斩（无 charge）；L13 = 环几何 + **强制蓄力窗**。

### 1.2 TIP-AC 可达超线（alone · 污染源）

硬线精神 D174：可达 mean\|Δ\|≤1 pp 且 **无格超 ±3 pp**。

| 族 · 可达 | 代表最差 Δ | mabs | 判 |
|-----------|------------|------|----|
| burst | −8.2 | 1.85 | ❌ |
| scorch | −9.8 | 1.92 | ❌ |
| sustain | **−13.8**（q01c d0.3） | **2.70** | ❌ |

- **防具侧 alone**：可达 **0** 条超硬线 → 污染源 = **刃 L13 alone**（及 L11；本窗只钉 L13）。  
- **TIP-AD**：甲门禁评测集 **排除** L11/L13 后 STAGE1_OFFLINE=PASS(patched)；**exclude 本窗保留**。  
- **分类标签（RESEARCH §3.2）：** `identity/charge timing variance` · 重症于 L11 · **非**「表低了抬一下」。

### 1.3 为何是身份债（非抬表叙事）

| 因子 | 说明 |
|------|------|
| **skill_charge 0.5** | 蓄力窗丢普攻；短图 / 高机动格惩罚极重 |
| **环斩几何** | 与对准扇形基线利用率差大；burst/scorch/sustain 落点不一致 |
| **无 Alt** | 缺「同收益另一代价」缓冲 |
| **已有缓解件** | 蓄力落空回充 / 蓄力首启 / 落地缓速等是 **日限或反馈**，不消 alone 三族硬负 |

**明确不是：** TIP-AE 烙印扩甲引入；STAMP/掉落问题；「缺 Affix / 伤乘 / skill_mult」能合法修的洞。

---

## 2. L13 蓄力身份规格（stage0 · 可批 · feel/identity）

### 2.1 身份一句话（目标态 · 非数值终表）

**炉锁巨锤** = 「**蓄后再砸的环刃**」：玩家为环覆盖 **主动付出时间窗**，换 **不必对准的环几何**；身份应读作 **节奏/形状选择**，不应读作「永久少打伤害换几何」。

### 2.2 必须保留的身份核（改版不得删）

| 核 | 规格 |
|----|------|
| 槽 / 图 | 仍 **blade** · Q07 主题可保留「炉锁」叙事 |
| 形状承诺 | 仍提供 **环/不必对准** 的烬斩变体（可与 L09 差分：L09 瞬时轻环；L13 **蓄力重环**） |
| 行为型 | 效果改 **技能形状/时机**；**禁止**新开永久伤乘 / skill_mult |

### 2.3 允许的重设方向（改行为/手感 · **禁**永久伤乘）

下列为 stage0 **授权探索集**（施工 tip 单选或组合，仍须门禁）：

| ID | 方向 | 含义（例） | 对准的方差 | 禁夹带 |
|----|------|------------|------------|--------|
| **R1** | **蓄力窗结构** | 缩短/分段蓄力；CD 改「释放起算」；蓄满可取消不扣满代价 | charge 时长惩罚 | 禁顺手加 skill_mult / 伤乘 |
| **R2** | **蓄力中输入** | 蓄力中允许弱普攻 / 允许取消回普攻 / 允许移动惩罚换可攻 | 「普攻无效」死窗 | 禁抬基础伤表 |
| **R3** | **几何采样时机** | 形状在按下锁定 vs 落地锁定；落空判定与日限回充对齐叙事 | 环利用率族间差 | 禁扩 cap 当抬伤 |
| **R4** | **与 L09 差分钉死** | 书面钉：L09=轻瞬时环；L13=蓄力环（半径/cap/plus 可调 **结构**，非伤乘） | 「两把环斩撞车」 | 禁两签叠出双环伤乘 |
| **R5** | **resignature（身份重写）** | 仍 blade；可换 tag/收益类型（例：非 charge 的 shape 代价集），**重写玩法标签** | 大方差最对症（RESEARCH 路径 D） | 高成本；须完整 alone 门禁；禁绑甲轨 |

**明确不授权（本 stage0）：**

- 拧 `skill_mult`（上/下）或任何永久伤害乘区 / Affix densify / 抬 STAMP  
- 复活「小数点凑 S3 绿」调律  
- 把 TIP-Y 烬斩 cue/粒子 densify 当本签补丁  
- 本窗改 L11；扩甲 stampable；回改 TIP-AE

### 2.4 玩家口径（固定 · 本窗）

- 炉锁巨锤 **还在游戏里**；本窗 **不改它的数字、不加新招牌**。  
- 以后若修，修的是 **蓄力怎么打、环怎么算**，**不是**加伤害假装修好。  
- 防具烙印（TIP-AE）**照旧**；甲评测暂时仍不拿 L13 当考题（TIP-AD exclude）。

---

## 3. 门禁表（解冻 L13 施工才启用）

> 任一门 FAIL → **停**；**禁抬表凑线**。PASS ≠ 自动开工。开工另需总控显式令。

| 门 | 要求 |
|----|------|
| **G-alone** | `mainline.py tol`：L13 **alone** · 三族可达 · D174 硬线（mean\|Δ\|≤1 且无格超 ±3） |
| **G-pair** | 若重纳入甲组合透镜：含 L13 的合法 pairs 可达不过线；**或** 显式声明「仍 exclude 出甲门禁」（继承 TIP-AD） |
| **G-W30** | 若动代价/收益/蓄力结构：至少一条代表佩戴路线 \|ΔW30\|≤0.5（禁只靠抬掉落/STAMP） |
| **G-X** | 清单核：无新伤乘 · 无 skill_mult 再拧 · 无 Affix densify · 无改甲 stampable · 无 sx20/深化 densify |
| **G-split** | 文首硬句：刃轨 ≠ 甲轨；不回滚 TIP-AE；**L11 仍 HOLD 另轨** |
| **G-id** | 书面对照 §2.2：环/不必对准身份核仍在；与 L09 差分可核（R4 或 resignature 替代说明） |

**分阶提醒（平行防具 stage0）：**

| 阶 | 内容 | 本窗 |
|----|------|------|
| **0 · 规格** | 本文 + TIP-AG | **本窗** |
| **1 · 离线门禁** | 跑 G-alone（± pair/W30）；不过 → 停 | **另窗**（可 docs+tools，仍禁业务 jar） |
| **2 · 施工 tip** | 仅门禁 PASS + 总控显式开工后：单主题改 L13 行为 | **另 tip** |

---

## 4. L11 / TIP-AD / 甲轨边界

| 轴 | 本 DESIGN |
|----|-----------|
| **L11 霜封长刀** | **HOLD 另轨**；不写 L11 护盾规格；不借本窗改 L11 mods |
| **TIP-AD exclude** | **保留** 直至刃 alone 门禁绿且总控决定重新纳入甲评测集 |
| **TIP-AE 防具最小切片** | **正交**；不追加甲 stampable、不改 defense-side fits |
| **TIP-Y 烬斩手感门闩** | 仍有效；L13 蓄力若进施工走 **刃签 tip**，不借 skill_mult、不重开 cue densify |
| **attune 六槽** | 本窗不选 |

---

## 5. NON-goals / 硬禁

- 本窗改 `DEFS` / `SIGS` / SkillService 蓄力逻辑 / 业务 jar / 派插件  
- skill_mult 再拧；Affix densify；永久伤乘；抬表；glance twin  
- 深化 densify；sx20；粒子 densify；ForgeChoice·wear·pity·awkDelta densify  
- 把 TIP-AD exclude 说成「签已删」  
- 绑防具 stampable；回改 TIP-AE；夹带 L11 调律 / L11b  
- 本文件冒充数值终表或开工令

---

## 6. 成功标准（本 stage0）

- [x] 文首：可批 ≠ 开工；HOLD 至门禁 + 显式开工  
- [x] 问题陈述含三族大负 / skill_charge 方差 + 防具侧净对照  
- [x] L13 蓄力身份规格 §2（核 + 允许方向 R1–R5 · 禁伤乘）  
- [x] 门禁表 G-alone / G-pair / G-W30 / G-X / G-split / G-id  
- [x] L11 HOLD · TIP-AD exclude 保留 · 甲轨正交  
- [x] **零**施工指引冒充开工令；**零** jar

---

## 7. 参考

- [`RESEARCH-ember-blade-sig-l11-l13-next-2026-10-12.md`](RESEARCH-ember-blade-sig-l11-l13-next-2026-10-12.md) §2–§6  
- [`RESULT-ember-armor-signature-gate-stage1-offline-2026-10-12.md`](RESULT-ember-armor-signature-gate-stage1-offline-2026-10-12.md) · `out-armor-sig-stage1-tol.md`  
- [`DESIGN-ember-armor-signature-gate-stage0-s3-eval-patch-2026-10-12.md`](DESIGN-ember-armor-signature-gate-stage0-s3-eval-patch-2026-10-12.md)（TIP-AD exclude）  
- [`DESIGN-ember-armor-signature-overlay-gate-stage0-2026-10-12.md`](DESIGN-ember-armor-signature-overlay-gate-stage0-2026-10-12.md)（平行：规格+门禁+HOLD）  
- SoT：`CoreRpg/.../EmberSignature.java` L13 · `tools/p1sim/mainline.py` `SIGS['L13']` · `SkillService` skill_charge
