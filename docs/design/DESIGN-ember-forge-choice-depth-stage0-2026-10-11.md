# DESIGN · 工坊选择深度 Stage0 · 2026-10-11

> **状态：** Stage0 + K1/K2/**K2b SHIP** · 已批 A · 方案 M · tip `TIP-ember-arch-health-forge-choice-d1130-d1138-2026-10-11.md`  
> **硬禁：** 无 K3/AFK 抬/sx20/legacy socket/gem flat/skill_mult/sticky · 无 hope PAPI/sig-dry · 无新永久伤乘 · 不改 D430 权表除非另批 p1sim

## 0. 一句话

让玩家默认走 **材料 → 确定 +1 档**；随机锻是旁路惊喜，不是唯一变强口。

## 1. 现状盘点（码确认）

| 路径 | 类 | 确定？ | 入口 |
|------|-----|--------|------|
| 强化 +1 | `EmberForgeService` / C03 | 是（有失败+保底次数） | enhance |
| 精工 +1 | refine | 是 | refine |
| 成色升档 | quality | 是 | quality |
| 烙纹定向 | imprint | 是（选类型） | imprint |
| 印记兑换 | C07 / marksPerExchange | 是 | redeem |
| 随机锻 | forgeroll / C22 / S59 | **否**（成色/精工权；极品 0） | forgeroll |
| 洗练 | affix | 半（pity） | reroll |
| 周目标 | `EmberForgeGoal` | 叙事 | forgegoal |

## 2. 缺口（本 Stage0）

| ID | 缺口 | 建议切片 | 风险 |
|----|------|----------|------|
| **K1** | 工坊/Hub 入口稳线与赌线权重同级，「先赌」误导 | **诚实入口** | UX only · 零经济 · **D1139 SHIP** |
| **K2** | 随机锻无「加料买下限」 | `forgeroll … floor` + C06.q0 加扣 | **D1140– SHIP** · out-forgeroll-floor-d1141 WITHIN |
| **K2b** | 下限可发现性 | TrMenu 下两排 + forge_gap_roll_floor + afterRoll 稳线按钮 | **D1163– SHIP** · 禁 K3 |
| **K3** | 目标路径与材料缺口未闭环到「下一稳线按钮」 | 已有 afterSet；补工坊菜单一行 gap→confirm | 薄 UX |
| **K4** | 技能 Stage2 微效用 | **本 DESIGN 不做**；见 shape-fork Stage2 指针 | 战斗 |

## 3. 方案 M（Stage0 裁定）

1. **本窗（D1130–D1138）**：只交 RESEARCH + 本 DESIGN + ARCH 健康注；**不改 jar 玩法码**。  
2. **下一施工窗优先 K1**（零经济诚实入口）→ 再评 K3 → K2 必须单独 tip+p1sim。  
3. **否决本窗**：抬 forgeroll 极品权、抬 AFK、skill_mult、新 PAPI hope、开 S16–S19。

## 4. 验收（Stage0）

- [x] RESEARCH 短摘已交  
- [x] 稳/赌路径表完整  
- [x] K1–K4 分级；施工顺序写明  
- [x] K1 码：D1139 TrMenu + hub + forge 入口半行  

## 5. 批注

- [x] 总控批 **A · 方案 M** · 2026-10-11 · D1130+
