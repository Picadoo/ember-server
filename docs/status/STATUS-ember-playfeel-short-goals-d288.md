# 状态 · D288：重打短目标可见化（花样 / 誓约 / 残响）

**日期：** 2026-10-07（上海时间）  
**上游：** 批 A · DESIGN playfeel §2 P2 · CoreRpg **1.65.81**（本窗零 jar）

**Git：** `75a64c9e`（与 D289 同提交上 main；本窗零 jar）

## 人话

进本前看不清本周精选还剩几次、这局可能出什么房间事件、誓约挂了没、残响本周领了几次；打完「为花样委托 +1」也不显眼。本窗只改 **TrMenu lore**：冒险页加「本周短目标」一眼卡；挑战 / 誓约同屏补齐；花样委托写明通关立刻刷完成提示。

## 改了什么

| 处 | 改动 |
|---|---|
| `ember_p1_adventure.yml` | Layout 插入 S；「本周短目标」：精选 / 规则 / 花样 / 事件池 / 誓约 / 残响 claim；W·V 补一眼 |
| `ember_p1_challenge.yml` | I 补残响 claim · 誓约 · 事件池仅普通重打 |
| `ember_p1_pledge.yml` | I 补精选 / 事件池 / 花样 / 残响 |
| 设计稿 §2 P2 | 标已施工 D288 |

## 不动

- variety 池权重、affix_shard / event_core 数量
- 六槽 · 副手物品 · jar / PAPI 新键（复用 `featured` / `modifier` / `vbounty` / `pledge_head` / `rush_echo_q01`）

## 验收

- `rg '本周短目标|事件池|残响本周' plugins/TrMenu/menus/ember_p1_{adventure,challenge,pledge}.yml`
- `trmenu reload`；play 保持 up（无需换 jar）
