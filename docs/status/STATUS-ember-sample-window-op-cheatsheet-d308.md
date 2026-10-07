# OP 半页 · D308 样本窗读数速查（禁玩家面）

**日期：** 2026-10-08（上海时间）  
**上游：** [`STATUS-ember-sample-window-readiness-d308.md`](STATUS-ember-sample-window-readiness-d308.md) · DESIGN §2.1.4  
**权限：** `corerpg.admin` · **勿**写进玩家菜单 / lore / 成就

## 命令

```
/corerpg p1 telemetry              # 自己
/corerpg p1 telemetry <player>     # 点名（单号满 ≠ 全服满）
/corerpg p1 telemetry server       # 全服周合计（须已排除测试号）
# 落盘：plugins/CoreRpg/p1-telemetry/<week>.yml
```

## 开闸前先问（对照 DESIGN 硬表）

1. 战斗向（事件/调律）：是否已有 **≥1 完整 P 周**，且排除测试号后全服 `p1_pf_runs` **≥ 30**？（荐满 2 周）  
2. 排除表：`telemetry.exclude_uuids` ∪ `leaderboard_exclude` 是否已写入冒烟/压测 UUID？  
3. 对应 M 是否已收 **≥3 自然日**？有无 ≥3 名非测试真人反馈或总控书面记档？  
4. 是否总控 **批 A**？（策划交稿 / 施工自开 ≠ 开闸）  
5. 红线是否触发？（日刷 `pf_runs` 冒充深渊/周本/挂机；借 D307 改价；玩家面看板；HOLD 轨夹带……）

**任一「否」或红线触发 → 本周不得批开该 R。**

## 看哪几行

| 要判断 | 看 | 不算数 |
|--------|----|--------|
| 事件能不能议 R/W | `runs` · `evt_roll` · `evt_ok` · 出房率/成功率 · 人感「空」 | 单局 tip、测试号局 |
| 调律能不能议 R | `sig_wear` · `sig_alt` · `runs` · 人感「进本前看不见」 | 仅 `p1_attuneprompt` 旗 |
| 破绽课是否还在 | 局均 `wall`/`whiff`/`break`（辅助） | 玩家面排行 |
| forge R | **不看**破绽行；人感「养不起」+ 经济草稿 | 菜单费用文案再改一版 |

口径与门槛数字以 [`DESIGN-ember-sample-window-readiness-2026-10-08.md`](../design/DESIGN-ember-sample-window-readiness-2026-10-08.md) §2.1 为准。  
**2026-10-08 当前态：全表不得开。**
