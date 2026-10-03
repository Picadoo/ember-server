# STATUS · D152 卸甲（P2-8 第 4 条周规则）

- **版本**：CoreRpg **1.63.0**（10-04 07:48 CST 部署）
- **balance_version**：26（`ember-v1-runs.yml` 两份）
- **内容**：精选图挑战局新增周规则 `disarm` / **卸甲**：`remap: {heavy: melee}`（重甲换成近战；图上没有近战角色时保持原样）。**挑战专用**（不设 `normal: true`，与 `casters` 相同）。不改奖励、不加倍率（书 §23.3）。
- **实现**：只加 YAML + 单测；复用已有 `Modifier.remap`，无 Java 字段改动。轮换 4 规则 × 7 图 = 28 周把 28 种组合各轮一次。

## 模型（`p2econ.py --mods`，60 人 8 周，躲避 0.5）

输出：`tools/p1sim/out-p2econ-mods-d152.md`

精选图挑战通关率（同一种子 40 局配对）：

| 规则 | 玩家-周 | 无规则 | 有规则 | 差 |
|---|---:|---:|---:|---:|
| lean 限药 | 60 | 100% | 100% | +0 点 |
| casters 术者换防 | 120 | 98% | 100% | +1 点 |
| reverse 逆行 | 120 | 99% | 99% | +0 点 |
| **disarm 卸甲** | 120 | 100% | 100% | **+0 点** |

第 8 周累计 T3 印记中位：无轮换 168 / 轮换 188 / 轮换+周规则 189（噪声内）。成套 / 强化 / 币列相对「轮换」方案也在噪声内。卸甲预期略易（重甲→近战），本表已顶到 100% 所以差为 0。

## 冒烟（FreshQ44，10-04 07:51 CST）

1. `corerpg p1 runs firstclear FreshQ44 q01..q07` + `stamina set 120`
2. `corerpg p1 runs modifier disarm`
3. `/corerpg p1 enter q01 challenge force`
4. 开局聊天：`本周规则「卸甲」重甲换成近战…（奖励不变）`
5. 日志：`modifier forced disarm (admin test)`；进 R1 后 `r1 rule disarm: melee,melee,melee,melee`
6. 短测后离开（未打满通关，按「几分钟冒烟」）

**结果：PASS**（规则写入本局、聊天与刷怪日志一致）。MySQL：`[storage] MySQL connected`；CoreGacha 仍连上。

## 备份

- `/workspace/backup/CoreRpg-1.62.0-pre-d152.jar`
- `/workspace/backup/CoreRpg-1.63.0.jar`

下次测试号：**FreshQ45** / FreshG04。
