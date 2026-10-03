# STATUS · EmberEliteWeekly 阶段 4.4 收口

- 时间：2026-09-27（Asia/Shanghai）
- 结论：**PASS（4.4f，功能 + 平衡验收通过）**
- 对象：余烬-总控（协调器）
- 账号/参照档：`ElF8838`，无战斗 buff，余烬 Lv42，烬刃 T2+7（护符 +4，最大生命 40）

## 验收结果

- 全本通关：**510s**（窗口 480～720s）
- Boss TTK：**70s**（窗口 45～75s）
- 通关前有效 HP：**13.605/40 = 34%**（窗口 25～55%，非复活灌血）
- 死亡 / 成功复活：**1 / 1**；窒息：**0**
- 入口与接线：`ticket_ember_elite` 扣票、CoreRpg elite gate、DP `EmberEliteWeekly`、TrMenu hub 按钮均通过
- `ops.json`：`[]`

## 最终数值（4.4f）

- 波次 HP：Zombie **5250** / Skeleton **3300** / Mix **2700** / Brute **10500**
- 波次 Damage：**4 / 4 / 4 / 5**
- Ignite：**0.15 / 0.07 / 0.08 / 0.10**
- Boss：HP **5200** / Damage **12**；Pulse **100**；Rush **200**
- 未改：玩法配置、Boss 数值及其它副本/挂机数值未动

## 4.4c → 4.4f

4.4c（383s，时长 FAIL）→ 4.4d（约 529s 失败，复活耗尽）→ 4.4e（529s / Boss 68s，但 93% 为复活灌血）→ 4.4f（510s / Boss 70s / 有效 HP 34%，PASS）。

## 依据

- [4.4f 测试报告](STATUS-ember-elite-weekly-test-4.4f.md)
- [4.4f 怪物状态](STATUS-ember-elite-mobs-4.4f.md)
