# STATUS · 余烬技能组 S1（CoreRpg 1.65.43 / D211）

> 2026-10-06 · 执行 Ember skill-kit S1 · 上游 D209 调研 / D210 S0 定稿

## 已上线（本版）

| 项 | 内容 |
|---|---|
| 输入分流 | F = 烬斩；潜行+F：Q02 首通前仍烬斩，之后 = 烬突（X12） |
| 烬突 | 花烬斩 8s 充能；冲 4 格（`EmberDash`=踏步碰撞，关门 IRON_FENCE 钳住）；≤3 目标各 1.5B，首领×0.5；不暴击/不吸血/不套装计数；签名「烬斩命中」不对烬突（X11） |
| 烬斩符文 | 扇形 / 直线(=L07) / 环斩(=L09)；Q04 首通；出本换；换后充能转满（X1）；签名形状覆盖符文 |
| 技能页 | TrMenu `ember_skill_kit` + hub「技能组」；`/corerpg skill kit` / `shape <fan\|line\|ring>` |
| 挂机 | 自动烬斩跟所选/签名形状；**不**自动烬突（X7） |
| 计数器 | `p1_slash_shape@all` SETTING（0/1/2） |

## 明确推迟

| 项 | 去向 |
|---|---|
| 火痕步 / 后撤步（身法变体） | **S2**（D210 §3） |
| 守招（守墓壁垒 / 破势） | **S0b** 再模拟 |
| 灰印 / 聚火 | 已否决，不进本版 |

## 不变

- balance_version **57**；主动伤害预算仍约烬斩份额；无技能伤害乘区
- 单按 Q 不改（不取消扔刃）

## 构建

- JDK 8 · unit **363 / 0**
- jar sha256 见 RELEASE-ember-1.65.43.md

## 冒烟备注

- 枢纽 `/corerpg skill kit` 用 `EmberMode.active()`（枢纽不在 P1 战斗 scope）。
- FreshQ771：kit 文案 / Q02·Q04 解锁 / shape ring / hub→技能组菜单 PASS；SEVERE 0。
- FreshQ772：Q01 三房 + 首通 settle PASS，SEVERE 0。
