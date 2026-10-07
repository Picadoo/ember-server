# 状态 · D285：挂机到顶引导去冒险

**日期：** 2026-10-07（上海时间）  
**上游：** 批 A · DESIGN playfeel §8-3 · CoreRpg **1.65.81** · `daily_kills` **2400** 未改

## 人话

日 2400 击杀满后继续站挂机庭零收益。本窗只改**引导**：菜单主按钮变成「体力还在 · 去冒险」、ActionBar/停战提示带去向。

## 改了什么

| 处 | 改动 |
|---|---|
| `EmberAfkService` | `capStopText` / `capActionBar` / `capStatusWord`；停战后与站场时 ActionBar；PAPI `afk_full`；状态词 |
| `ember_p1_afk.yml` | 满额时 F 键 → 冒险页；战况/开页提示 |
| 版本 | **1.65.80 → 1.65.81** |

## 不动

- `daily_kills` / 离线 25% / 各层每日量 / 六槽 / VIP / Pack

## 验收

- `rg '体力还在|去冒险|afk_full|capStopText'`
- 单测 `capStopText_D285`
- play Enabling **1.65.81**；`trmenu reload`
