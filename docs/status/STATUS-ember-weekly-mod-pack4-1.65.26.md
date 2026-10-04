# STATUS · Weekly Modifier Pack 4 → CoreRpg 1.65.26（D187，2026-10-05）

## 结论

Pack 4 落地为 **CoreRpg 1.65.26** / 裁决 **D187** / `balance_version` **49**，线上 PID 1306094。Stage C `p2econ.py --mods --weeks 98` **within range**。冒烟进入下一合批（本包为新批次第 1 包）。

## 改动摘要

| 区域 | 内容 |
|---|---|
| 周规则池 | 12→**14**；斜移索引 (w mod 7 + w div 7) mod 14 |
| `hexplate` 咒甲 | heavy→caster，hp×1.25 / interval×1.2；challenge-only |
| `blades` 断咒 | caster→melee，speed×1.2 / atk×0.9；challenge-only |
| remap 方向 | 12 个方向全部用完，周规则线收尾 |
| 奖励 | 不变 |

## 测试

- 单测 281/0；开服双 MySQL、SEVERE 0
- 冒烟：DEFERRED batch
- 资产路径：未改 → 跳过 persist-roundtrip

## 下批合测待办

- 1.65.26 D187：挑战版强制 hexplate（Q03+ 重甲→术者）/ blades（有术者图）各一局，确认 converted 生效、奖励未变

## 协调

- `COORD-routine-0418`（中断）→ `COORD-routine-0517` 收尾 → DONE
