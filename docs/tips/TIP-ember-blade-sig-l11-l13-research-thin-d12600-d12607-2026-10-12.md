# tip · TIP-AF 刃签 L11/L13 另轨研究薄窗（docs-only · 清 TIP-AD HOLD）· D12600–D12607

> **状态：** OPEN · 待批 **A · 方案 M** · docs-only · 2026-10-12 19:37 Asia/Shanghai  
> **上游：** [`RESEARCH-ember-blade-sig-l11-l13-next-2026-10-12.md`](../design/RESEARCH-ember-blade-sig-l11-l13-next-2026-10-12.md) · TIP-AE CLOSED live `1.65.773-d12599`（`f3a1073cb`/`abe84cb22`）· TIP-AD CLOSED PASS patched `abc3875d0` · RESULT [`RESULT-ember-armor-signature-gate-stage1-offline-2026-10-12.md`](../design/RESULT-ember-armor-signature-gate-stage1-offline-2026-10-12.md) · 补丁 [`DESIGN-ember-armor-signature-gate-stage0-s3-eval-patch-2026-10-12.md`](../design/DESIGN-ember-armor-signature-gate-stage0-s3-eval-patch-2026-10-12.md) · stage0 [`DESIGN-ember-armor-signature-overlay-gate-stage0-2026-10-12.md`](../design/DESIGN-ember-armor-signature-overlay-gate-stage0-2026-10-12.md)  
> **前置：** 防具最小切片已 live；甲 stampable 仍空；L11/L13 **未动**；深化 densify **仍冻**；烬斩门闩 TIP-Y **仍有效**  
> **施工岗：** **无码**（策划 RESEARCH + tip；**不**派插件；勿借机改 `DEFS`/`SIGS` / skill_mult / Affix / 甲 fits）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin** · **零 ForgeChoice·wear·pity·awkDelta densify** · **零 sx20 / 粒子 densify** · **零 skill_mult 再拧** · **零改 L11/L13 mods** · **零绑防具 stampable** · 无施工 tip

---

## 0. 一句话

TIP-AE 后开 **刃 L11/L13 另轨研究**：盘清 TIP-AC 超线根因与候选路径；**清 TIP-AD HOLD 债**；本窗 **docs-only**，**HOLD 施工与改 mods**。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| (1) 刃 L11/L13 另轨研究 docs-only | **采纳** | 总控点名；清 TIP-AD HOLD；不绑甲轨 |
| (2) attune 预览六槽对齐薄窗 | **否（本窗）** | 总控本窗否；不抢轴 |
| (3) 其它可感内容 / 观察窗 | 次选 | 刃债未盘清前优先研究 |
| 当场改 L11/L13 mods / 抬表凑绿 | **否** | 硬禁；须 DESIGN+门禁 |
| 扩甲 stampable / 回改 TIP-AE | **否** | 正交；NON-goal |
| Affix / 伤乘 / skill_mult / sx20 / 深化 densify | **否** | 硬禁 |

---

## 2. 方案 M（定稿荐 · 无码）

| ID | 项 | 验收 |
|----|-----|------|
| D12600 | 本 tip + RESEARCH `RESEARCH-ember-blade-sig-l11-l13-next-2026-10-12.md` | 路径可点 |
| D12601 | DESIGN power-paths 进度戳：**TIP-AE CLOSED live `1.65.773-d12599`**；**TIP-AF OPEN**（刃 L11/L13 另轨研究 · docs-only） | 戳可见 |
| D12602 | RESEARCH 钉死：L11/L13 定义（霜封长刀 / 炉锁巨锤 · SoT DEFS）+ TIP-AC 可达超线表 + 防具侧净对照 | 可核 |
| D12603 | 超线分类：L11 = 护盾兑现 vs skill_mult/taken_boss 恒压；L13 = skill_charge 大方差；**非**甲升格引入 | 可核 |
| D12604 | 候选路径 A–E（exclude 已做 / 观察 HOLD / 手感重设 / resignature / L11b 默认否）+ 荐：下一 **刃 DESIGN stage0** 或观察 HOLD | 可核 |
| D12605 | p1sim 门闩写入 RESEARCH：G-alone / G-pair / G-W30 / G-X / G-split；FAIL→停禁抬表 | 文档 |
| D12606 | HOLD：本 tip **无码**；**不**派插件；**不**改 L11/L13 mods；**不**绑甲 stampable；TIP-AD exclude **保留** | 无 jar |
| D12607 | 本 commit docs-only；密码检查 0；缓冲仅本文热修（错链/错戳） | PASS |

### 玩家口径（固定）

- 两把刃招牌（霜封长刀 / 炉锁巨锤）**还在游戏里**；本窗**不改它们的数字、不加新招牌**。  
- 防具能烙其它招牌的能力（TIP-AE）**照旧**；本窗不碰甲。  
- 考试里暂时不拿这两把刃当「甲签名能不能上」的考题——那是评测口径，不是删签。  
- 以后若要修，会先写设计 + 模拟，**不会**靠加伤害/拧熟练度假装修好。

---

## 3. 明确不做

- 任何 CoreRpg / TrMenu / p1map / Economy 业务改码或 YAML 数值表  
- 改 `DEFS` / `SIGS` 里 L11/L13（或任一签名）mods；复活 L11b 当默认出路  
- 抬 Affix / STAMP / 伤乘 / skill_mult；glance twin；sx20；深化 densify  
- 扩甲 stampable；回改 defense-side fits；把刃债绑回防具开工令  
- 把本 RESEARCH 当成开工令或数值终表；开施工 tip / 派插件

---

## 4. 验收清单

- [ ] 总控批 **A · 方案 M**  
- [ ] RESEARCH + tip 路径可点；超线分类 / 路径 A–E / 门闩可核  
- [ ] DESIGN power-paths：TIP-AE CLOSED live + TIP-AF OPEN  
- [ ] **无**插件施工派单；**无** L11/L13 数值改动；**无**业务 jar  
- [ ] `git show` docs-only；密码检查 0  

---

## 5. 批注（荐）

- [ ] 总控批 **A · 方案 M** · 荐批  
- 选题：TIP-AE 后 · **刃 L11/L13 另轨研究 docs-only**（选项 1 · 清 TIP-AD HOLD）  
- **批注：** 验收=研究可核；HOLD 施工与改 mods；荐下一刃 DESIGN stage0（HOLD 施工）或观察 HOLD；TIP-AD exclude 保留；深化 densify / Affix / 抬表 / glance / sx20 / skill_mult 仍冻；无派插件  

## STATUS: OPEN · 待批 A·M · 2026-10-12 19:37 CST · 无码
