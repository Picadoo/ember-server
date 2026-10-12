# tip · TIP-AG 刃签 L13 蓄力身份 DESIGN stage0（docs-only · HOLD 施工）· D12608–D12615

> **状态：** OPEN · 荐批 **A · 方案 M** · docs-only · HOLD 施工 · 2026-10-12 Asia/Shanghai  
> **上游：** [`DESIGN-ember-blade-sig-l13-charge-identity-stage0-2026-10-12.md`](../design/DESIGN-ember-blade-sig-l13-charge-identity-stage0-2026-10-12.md) · TIP-AF CLOSED docs-only `f6e16ea46` · RESEARCH [`RESEARCH-ember-blade-sig-l11-l13-next-2026-10-12.md`](../design/RESEARCH-ember-blade-sig-l11-l13-next-2026-10-12.md) · TIP-AD exclude 保留 · TIP-AE 甲轨正交  
> **前置：** RESEARCH 荐先 L13 蓄力身份；短线维持 exclude+观察 HOLD；深化 densify **仍冻**；烬斩门闩 TIP-Y **仍有效**  
> **施工岗：** **无码**（策划 DESIGN + tip；**不**派插件；勿借机改 `DEFS`/`SIGS` / SkillService / skill_mult / Affix / 甲 fits）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin** · **零 ForgeChoice·wear·pity·awkDelta densify** · **零 sx20 / 粒子 densify** · **零 skill_mult 再拧** · **零改 L11/L13 mods** · **零 jar 直至门禁过线 + 总控显式开工** · 不改 Economy/pom 脏树

---

## 0. 一句话

TIP-AF 后开 **L13 蓄力身份独立 DESIGN stage0**：钉炉锁巨锤 feel/identity 规格 + 门禁表；**HOLD 施工**；本窗 **docs-only**。L11 另轨 HOLD；TIP-AD exclude **保留**。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| (1) **刃 DESIGN stage0 · 先 L13 蓄力身份** | **采纳** | 总控点名；RESEARCH P1；路径 D 对症 |
| (2) attune 预览六槽对齐薄窗 | **否（本窗）** | 总控本窗否；不抢轴 |
| (3) 其它可感内容轴 | 次选 | L13 规格未钉前优先 DESIGN |
| 当场改 L13 mods / 抬表凑绿 | **否** | 硬禁；须门禁+显式开工 |
| 本窗钉 L11 / 改 L11 mods | **否** | L11 HOLD 另轨 |
| Affix / 伤乘 / skill_mult / sx20 / 深化 densify | **否** | 硬禁 |

---

## 2. 方案 M（定稿荐 · 无码）

| ID | 项 | 验收 |
|----|-----|------|
| D12608 | 本 tip + DESIGN `DESIGN-ember-blade-sig-l13-charge-identity-stage0-2026-10-12.md` | 路径可点 |
| D12609 | DESIGN power-paths 进度戳：**TIP-AF CLOSED `f6e16ea46`**；**TIP-AG OPEN**（L13 蓄力身份 DESIGN stage0 · 无码 · HOLD 施工） | 戳可见 |
| D12610 | 文首硬句可核：stage0 可批 ≠ 开工；施工须门禁过线 + 总控显式开工 | 可核 |
| D12611 | 问题陈述：TIP-AC L13 alone 三族大负 + skill_charge 方差分类；防具侧净对照 | 可核 |
| D12612 | L13 蓄力身份规格：身份核 + 允许方向 R1–R5（蓄力窗/输入/采样/与 L09 差分/resignature）；禁永久伤乘 | 可核 |
| D12613 | 门禁表 G-alone / G-pair / G-W30 / G-X / G-split / G-id；FAIL→停禁抬表；PASS≠开工 | 可核 |
| D12614 | HOLD：本 tip **无码**；**不**派插件；**不**改 L11/L13 mods；L11 另轨；TIP-AD exclude **保留**；不绑甲 stampable | 无 jar |
| D12615 | 本 commit docs-only；密码检查 0；缓冲仅本文/DESIGN 热修（错链/错戳） | PASS |

### 玩家口径（固定）

- 炉锁巨锤 **还在**；本窗 **不改数字、不加新招牌**。  
- 以后若修，修的是 **蓄力怎么打 / 环怎么算**，不是加伤害。  
- 防具烙印照旧；考试里暂时仍不拿这把刃当「甲签名能不能上」的考题。

---

## 3. 明确不做

- 任何 CoreRpg / TrMenu / p1map / Economy 业务改码或 YAML 数值表  
- 改 `DEFS` / `SIGS` / SkillService 蓄力逻辑；改 L11 或 L13 mods  
- 抬 Affix / STAMP / 伤乘 / skill_mult；glance twin；sx20；深化 densify  
- 扩甲 stampable；回改 defense-side fits；把刃债绑回防具开工令  
- 把本 DESIGN 当成开工令或数值终表；开施工 tip / 派插件

---

## 4. 门禁摘要（详 DESIGN §3）

| 门 | 一句话 |
|----|--------|
| G-alone | L13 alone 三族可达过 D174 硬线 |
| G-pair | 重纳入甲透镜不过线，或仍明示 exclude |
| G-W30 | 动结构则代表路线 \|ΔW30\|≤0.5 |
| G-X | 无伤乘 / skill_mult / Affix / 甲 stampable |
| G-split | 刃≠甲；不回滚 TIP-AE；L11 HOLD |
| G-id | 环身份核 + 与 L09 差分可核 |

---

## 5. 验收清单

- [ ] 总控批 **A · 方案 M**  
- [ ] DESIGN + tip 路径可点；问题 / 规格 R1–R5 / 门禁可核  
- [ ] DESIGN power-paths：TIP-AF CLOSED `f6e16ea46` + TIP-AG OPEN  
- [ ] **无**插件施工派单；**无** L11/L13 数值改动；**无**业务 jar  
- [ ] `git show` docs-only；密码检查 0  

---

## 6. 批注（荐）

- [ ] 总控批 **A · 方案 M** · 荐批  
- 选题：TIP-AF 后 · **刃 L13 蓄力身份 DESIGN stage0**（docs-only · HOLD 施工；非 attune）  
- **批注：** 规格对准 skill_charge 方差；允许 R1–R5 手感/身份重设；禁伤乘/skill_mult；L11 HOLD；TIP-AD exclude 保留；零 jar 直至门禁 + 显式开工；深化 densify / Affix / 抬表 / glance / sx20 仍冻；无派插件  

## STATUS: OPEN · 待批 A·M · 2026-10-12 19:40 CST · 无码
