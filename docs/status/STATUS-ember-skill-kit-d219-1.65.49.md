# STATUS · 余烬技能组 后撤步（CoreRpg 1.65.49 / D219）

> 2026-10-06 · 上游 D210 / D214 / D217 RESEARCH-ember-backstep

## 已上线

| 项 | 内容 |
|---|---|
| 身法方向 | 技能页前冲（默认）/ 后撤；Q01 起、三套通用；出本切换、身法 CD 转满 |
| 后撤步 | 向后 4 格、无伤害、无无敌、保持朝向、严格落点（拒崖/岩浆/火/仙人掌/蛛网） |
| 火痕·后撤 | 焚烬+Q05 时后撤点燃**起跳点** 3 格内最近 1（×1.0） |
| 斜角缝 | `EmberDash` body 采样 ±0.3（踏步/火痕/烬突共用） |
| 计数器 | `p1_step_dir@all` SETTING（EmberCounters） |
| PAPI / 菜单 | `kit_step_dir` / `kit_step_dir_key`；TrMenu G=前冲 H=后撤 |

## 不变

- balance_version **57**；挂机不放身法（X7）；守招仍搁置（D212）
