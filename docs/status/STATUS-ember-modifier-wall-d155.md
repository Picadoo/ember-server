# STATUS · D155 铁卫（P2-8 第 6 条周规则）

- **版本**：CoreRpg **1.63.2**（10-04 08:52 CST 部署）
- **balance_version**：28（`ember-v1-runs.yml` 两份）
- **内容**：精选图挑战局新增周规则 `wall` / **铁卫**：`remap: {melee: heavy}`（近战换成重甲；图上没有重甲角色时保持原样）。**挑战专用**（不设 `normal: true`，与 `casters` / `disarm` / `guards` 相同）。不改奖励、不加倍率（书 §23.3）。
- **实现**：只加 YAML + 单测；复用已有 `Modifier.remap`，无 Java 字段改动。轮换 6 规则 × 7 图 = 42 周把 42 种组合各轮一次。

## 模型（`p2econ.py --mods`，60 人 8 周，躲避 0.5）

输出：`tools/p1sim/out-p2econ-mods-d155.md`

精选图挑战通关率（同一种子 40 局配对）：

| 规则 | 玩家-周 | 无规则 | 有规则 | 差 |
|---|---:|---:|---:|---:|
| lean 限药 | 60 | 100% | 100% | +0 点 |
| casters 术者换防 | 120 | 98% | 100% | +1 点 |
| reverse 逆行 | 60 | 99% | 99% | +0 点 |
| disarm 卸甲 | 60 | 100% | 99% | −1 点 |
| guards 卫士潮 | 60 | 100% | 100% | +0 点 |
| **wall 铁卫** | 60 | 100% | 100% | **+0 点** |

第 8 周累计 T3 印记中位：无轮换 168 / 轮换 188 / 轮换+周规则 189（噪声内）。成套 / 强化 / 币列相对「轮换」方案也在噪声内。铁卫预期略难（近战→重甲），本表已顶到 100% 所以差为 0；未触发 −12 点否决线，**保留铁卫**。

## 冒烟（FreshQ46，10-04 08:53～08:54 CST）

1. `corerpg p1 runs firstclear FreshQ46 q01..q07` + `stamina set 120`
2. `corerpg p1 runs modifier wall`
3. `/corerpg p1 enter q01 challenge force`
4. 开局聊天：`本周规则「铁卫」近战全换成重甲…（奖励不变）`
5. 日志：`modifier forced wall (admin test)`；`r1 rule wall: heavy,heavy,heavy,ranged`（本种子 R1 变体 B 近战→重甲）
6. 短测后离开（未打满通关，按「几分钟冒烟」）

**结果：PASS**（规则写入本局、聊天与刷怪日志一致；单测覆盖 melee→heavy remap）。MySQL：`[storage] MySQL connected`；CoreGacha 仍连上。

## 备份

- `/workspace/backup/CoreRpg-1.63.1-pre-d155.jar`
- `/workspace/backup/CoreRpg-1.63.2.jar`

下次测试号：**FreshQ47** / FreshG04。
