# DESIGN · 宝石 Stage2 p1sim 预算草案（docs only · 未批施工）

> **状态：** 已批 A·M · **p1sim FAIL（+1A 过强）** · 收紧提案 +0.25A/D 帽0.5 · **禁公式** · 见 out-gem-stage2-budget
> **链接：** [`DESIGN-ember-p1-socket-gems-2026-10-11.md`](DESIGN-ember-p1-socket-gems-2026-10-11.md) §4 · Stage1 已船 `17d33bfd` jar `1.65.292-gem-s1`
> **硬规则：** 无预算 PASS 不得把四石映射进 `EmberFormula`

## 1. 目标

为 Stage2「四石 → flat / 吸血 / 移速」写可跑的 p1sim 闸门表，使施工时只填绿格、不临场猜数。

## 2. 初值提案（待批 · 与主 DESIGN §4 对齐）

| 石 | code | 效用 | 帽 | 叠乘假设 |
|----|------|------|----|----------|
| 锋利 sharp | 1 | +1 ATK flat / 孔 | 刃 2 孔 → +2 | 进 B 前 flat |
| 稳固 steady | 2 | +1 DEF flat / 孔 | 符 1 孔 → +1 | 减伤侧 |
| 汲取 drain | 3 | +1% 吸血 / 孔 | ≤ 3% 总 | 受控层 |
| 疾风 gale | 4 | +2% 移速 / 孔 | ≤ 8% 总 | 仅 refreshP1 已支持时 |

## 3. p1sim 闸门（必过才可批施工）

钉：`P1SIM_RULES=out-guard-parry-t0b-rules-export.json`（或更新后的 canonical）· n≥800 · ±2 pp clear。

| 剖面 | 装备假设 | 判定 |
|------|----------|------|
| G0 base | 无石 | 对照 |
| G1 sharp×2 刃 | sock0=1 sock1=1 | 全可达格 ±2 |
| G2 steady 符 | sock0=2 | ±2 |
| G3 drain×1 | sock0=3 | 受伤 Δ% 不空廊；clear ±2 |
| G4 gale×1 | sock0=4 | 若移速未接线 → **跳过战斗格**，只验「无接线时等同 G0」 |
| G5 满配锋+稳 | 刃双锋 + 符稳 | mean \|Δ\| ≤ 1 · 无格 >±3 |

否决：任何剖面抬 skill_mult / 改 enhance 表 / 复用 legacy `/corerpg socket`。

## 4. 施工顺序（未批）

1. 本档批 A · 方案 M（或修订初值）
2. 加 `tools/p1sim` gem 剖面候选（`kit` 无关 · 走 builddiv / gear flat）
3. 出 `out-gem-stage2-budget-*.md` 绿表
4. 才允许改 `EmberFormula` + PAPI + 工坊镶嵌 UI

## 5. 批注

- [x] 总控批 **A · 方案 M（初值如上）** · 日期 2026-10-11 · 总控 COORD
- [ ] p1sim 绿表路径：`tools/p1sim/out-gem-stage2-budget-____.md`


## 6. FAIL 记录 · 2026-10-11

初值 +1A×2 / +1D 在 n=800 下 G1/G2/G5 全 ❌（最高 +19pp clear）。公式未接线。收紧见上表；绿表前禁止 `EmberLoadout`/`EmberFormula` 接线。
