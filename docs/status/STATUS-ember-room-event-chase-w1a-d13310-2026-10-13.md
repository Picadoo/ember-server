# STATUS · TIP-CE W1a 房间事件「冲一下」chase/ready PAPI · D13310

> 2026-10-13 Asia/Shanghai · 余烬-插件岗

## 交付
- `%corerpg_p1_event_chase%` / `_name` / `%corerpg_p1_event_soft_ready%`（0/1←SoftPity dry≥NEED 只读）
- `/corerpg p1 enter event-chase`（及 `event_chase`）→ 同 chase 键 → tryEnter；全未通诚实拒
- 单测：`EmberEventChaseTest` 精选已通优先 / 回落最高已通 / 全未通空 / ready 边界；`EmberRunPapiTest` 路由三键
- jar：`1.65.806-d13310.local` · sha256 `17dd90f68ee3…` · ECONOMY_BV **123** 未动 · **零改** SoftPity.onSettle / event_rate / weights / event_core / NEED / Pack
- play：live `1.65.806-d13310.local` · storage=mysql · bv123 · MySQL connected

## 键清单
| 键 | 口径 |
|----|------|
| `event_chase` | featured 已首通→featured；否则已通最高 q01…q07；皆无→空 |
| `event_chase_name` | 冲图显示名（无 chase→空） |
| `event_soft_ready` | SoftPity.get≥NEED → 1 否则 0（只读） |
| enter `event-chase` | 解析为 chase 后同 enter &lt;map&gt; |

## 落点
- `EmberEventChase.chaseKey` / `softReady`
- `EmberRunPapi.route` + `rotation`（ROTATION 节）
- `EmberRunService` case enter 别名

## 零改边界
- SoftPity.onSettle / NEED / C_DRY 语义
- event_rate / event_core / variety weights / Pack 池
- balance_version 123 / afk.tiers / daily_kills / Stage2

## 下步
- 物品 W1b/c（TrMenu E 格 + Open 半行）等键
