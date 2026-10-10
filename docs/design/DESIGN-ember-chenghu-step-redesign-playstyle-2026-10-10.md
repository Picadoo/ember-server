# 余烬 · 成长花样 · 承护步改效用（D435）

STATUS=**已批 A · 方案 M · D435 · PASS** · 2026-10-10 · tip [`STATUS-ember-next-hard-debt-chenghu-step-redesign-need-design-2026-10-10.md`](../status/STATUS-ember-next-hard-debt-chenghu-step-redesign-need-design-2026-10-10.md) · backlog `B-chenghu-step-redesign` · **≠守招 ≠抬日表 ≠关 Stage2（＜17:40）≠拧回 D434 的 0.20×2s 抗性**

**上游：** D434 PASS · S0 承护 Resist I ❌（+8.3pp）· live 抗性已关 · 展示名「承护步」保留。

## 0. 问题

承护身份需要**可感**落点效用，但不能走「短抗性」旧 cand（已证空廊/超预算）。

## 1. 方案对照

| 方案 | 内容 | 裁定 |
|------|------|------|
| **M（荐）** | 新效用键族进 S0：荐 **落点微疗 flat×H** 或 **下一次受击减伤单次**（非窗抗性）；过线再 T1 | **主推** |
| A | 只改文案「暂缓」永不做效用 | **否决** |
| L | 把 0.20×2s 小数拧进 ±2 | **否决** |
| W | 守招/灰印/抬日表/sx | **否决** |

### 批 A 勾选

- [x] **方案 M**
- [x] 否决 A / L / W
- [x] 批注：总控 **已批 A · 方案 M · D435** · 2026-10-10 · **先 S0** · 禁回潮 D434 抗性小数

## 2. 候选（S0 只跑这些 · 不预写 jar 药效）

| id | 意向 | 模型键（草案） |
|----|------|----------------|
| C_heal | 落点自疗 `0.04×H`（cap 另钉） | `kit_step_heal_pct` |
| C_once | 下一次受伤 ×0.7（单次消耗） | `kit_step_next_taken` |
| C_poison | （否决倾向）再短抗性 | — |

门禁：三套装可达格 Δ∈±2；择优成立；火痕/爆闪回归不破。

## 3. 本窗起点

- p1sim：承护分支改读 heal/next_taken（抗性键保留但 S0 不交）  
- 报告 `out-skillkit-d435-chenghu.md`  
- PASS 后才开 FlexSkillService 药效

*D435 · PASS · jar 1.65.138 · C_heal005。*
