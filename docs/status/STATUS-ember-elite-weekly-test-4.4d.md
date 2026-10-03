# STATUS · EmberEliteWeekly 阶段 4.4d 粗平衡复测（测试岗）

- 时间：2026-09-27 19:00～19:11（Asia/Shanghai）
- 执行：余烬-测试岗
- 依据：`docs/status/STATUS-ember-elite-mobs-4.4d.md`
- 脚本：`/tmp/elite-weekly-4.4d-clear.js`（`FORCE_BUFF=false`）
- 原始 JSON：`/tmp/elite44d-out.json`
- **未修改玩法数值、怪物 HP/伤害或配置数值**

## 总评

**FAIL**。本轮无 buff、Lv42、烬刃 T2+7 进入成功并扣票成功；约 **459s** 到达 Boss，但第 4 次死亡后复活次数耗尽，约 **529s** 判定挑战失败，未完成全本。因此 Boss TTK 与有效结束剩血无法验收。

## 必测结果

| 指标 | 结果 | 实测 |
|---|---|---:|
| hub「精英试炼」进本 | PASS | 菜单进入，试炼一开启 |
| 扣票×1 | PASS | `ticket_ember_elite`：1 → 0 |
| 全本 480～720s | FAIL | 未通关；约529s失败（非完成时长） |
| Boss TTK 45～75s | FAIL/不可测 | Boss约459s出现，约58s后玩家死亡，未击杀 |
| 结束剩血 25～55% | FAIL/不可测 | 失败后复活血量不作结束剩血 |
| 死亡 / 成功复活 | 记录 | **4 / 3**（第4次复活尝试失败） |
| minHp | 记录 | 约 **0.1/40** |
| 窒息 | PASS | 0 |
| 战斗 buff | PASS | 测前 `/effect clear`；无 resistance/strength/任何 op 战斗 buff |

## 账号与装备

- 账号：`ElD185857`
- 等级：**Lv42**
- 主武器：烬刃 T2 **+7**
- 护符：T2 **+4**；sharpness **3**
- 誓约：`blaze`；天赋：`blaze_root` / `blaze_crit1` / `blaze_leech1`
- 最大生命：40.0
- hits / skill casts：**659 / 57**

## 与 4.4c 对照

| 指标 | 4.4c | 4.4d | 变化 |
|---|---:|---:|---:|
| 全本 | 383s 通关（FAIL，低于480） | 约529s失败，未通关 | 波次显著变长，但复活耗尽 |
| Boss | TTK 68s PASS | 未击杀；约58s后死亡 | 不可验收 |
| 结束 HP | 31% PASS | 不可测 | 未通关 |
| 死亡 / 复活 | 3 / 3 | **4 / 3** | 各增加1次死亡/成功复活不变 |
| minHp | ≈0.0105/40 | **≈0.1/40** | 仍有濒死压力 |

## 结论与建议

4.4d 波血 ×1.5 将 Boss 前流程推到目标时长附近，但当前单人参照档在 Boss 前后耗尽复活，无法同时满足通关、Boss TTK、结束剩血三项验收。建议怪物岗在不抬伤害的前提下复核前中段承伤/复活压力，并另行复测；测试岗不改数值。

## 测后状态

- 临时 `RpgBot` 已 deop。
- `server-runtime/ops.json`：`[]`
- 测试脚本进程已结束；游玩服端口 **25567**、proxy **25565** 仍运行。
