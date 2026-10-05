# STATUS · 余烬技能组 S2（CoreRpg 1.65.45 / D214）

> 2026-10-06 · 执行 Ember skill-kit S2 · 上游 D210 S0 定稿 / D211 S1

## 已上线（本版）

| 项 | 内容 |
|---|---|
| 火痕步 | 潜行+Q 在 Q05 首通 + 焚烬两件套时自动替换踏步；落点 3 格内最近 1 敌点燃；burn = 套装系数 ×1.0（`skillIgnite`）；CD 仍 14s；不暴击/不吸血/不套装计数 |
| 技能页 | TrMenu `ember_skill_kit` C 格显示 `%corerpg_kit_step%`；PAPI `kit_step` / `kit_step_unlock`；kit info 文案 |
| 挂机 | 仍不自动放身法（X7）；无改动 |

## 明确推迟

| 项 | 去向 |
|---|---|
| 后撤步（向后 4 格） | **暂缓** — D210 表有位、模型与踏步同伤（0），但无套装亲和 / 菜单选型定稿；不进本版 |
| 守招 | 搁置（D212） |

## 不变

- balance_version **57**；踏步距离 / CD 不变；无技能伤害乘区
- 非焚烬或未通 Q05：潜行+Q 仍为普通踏步

## 构建

- JDK 8 · unit **373 / 0**（+2 `huohen*` in EmberSkillKitTest；含 ARCH D213 EmberEconomy 8）
- jar sha256 见 RELEASE-ember-1.65.45.md

## 冒烟备注

- FreshQ774–776：kit 文案 / Q05+焚烬→火痕步 cast / burst→踏步 / Q01 首通；SEVERE 0。详见 smoke-2026-10-06-d214-huohen.md。
