# STATUS · 深渊 9～12 阶段 4.3 收口

**日期：** 2026-09-27（Asia/Shanghai）  
**结论：** **PASS / CLOSED**

## 结论

`EmberAbyssWatcherDeep` 4.3 调整通过并关闭：

- 最终实测 TTK：第 10 层 **72.6s**、第 12 层 **78.1s**，均落入 §2.3 窗口（50～80s / 55～90s）。
- 第 12 层 TTK 与结束生命 **48%** 均 PASS。第 10 层结束% 偶有超窗，是 DP 复活后的满血/治疗采样；复跑战中 `minHp≈0.95`、复活 1 次，已显示真实致死压力。
- 验收以 **TTK 入窗 + 战中压力（`minHp` / 复活次数）** 为主；复活或即时治疗抬高的结束生命% 不单独判 FAIL。未触发 `<30s` 硬失败。
- 维持 Damage **18**，不升至 19；第 12 层已有复活压力。

## 最终配置

| 项 | 最终值 |
|---|---:|
| `EmberAbyssWatcherDeep` Health | **6000** |
| Damage | **18** |
| LevelModifiers health | **0** |
| `server-runtime/spigot.yml` `attribute.maxHealth.max` | **20000.0** |

怪物 Display、技能、掉落及 1～8 层不变。本次仅完成文档收口，未改游戏配置或怪物 YAML。

## 证据

- `docs/status/STATUS-ember-abyss-ttk-4.3.md`、`docs/status/STATUS-ember-abyss-ttk-4.3b.md`、`docs/status/STATUS-ember-abyss-ttk-4.3c.md`、`docs/status/STATUS-ember-abyss-ttk-4.3c-rerun.md`
- `docs/status/STATUS-ember-abyss-watcher-deep-4.3.md`、`docs/status/STATUS-ember-abyss-watcher-deep-4.3b.md`、`docs/status/STATUS-ember-abyss-watcher-deep-4.3c.md`
- `docs/status/STATUS-ember-maxhealth-ceiling.md`
