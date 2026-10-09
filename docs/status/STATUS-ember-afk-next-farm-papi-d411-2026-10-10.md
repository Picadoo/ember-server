# 状态 · D411：挂机 next_farm PAPI（批 A·M · W1a）

**日期：** 2026-10-10（上海时间）  
**上游：** 总控派单 · DESIGN [`DESIGN-ember-afk-next-farm-visible-2026-10-10.md`](../design/DESIGN-ember-afk-next-farm-visible-2026-10-10.md) **§2.1** · tip `9a936440`  
**裁决：** **本号交** 只读派生 Placeholder + 单测 + 装 play · **TrMenu W1b/c 另号** · **未动** daily_kills / afk.tiers / 离线 / upgrade_hint 默认 / gate_daily / 观察三开关 / ×0.97 · **≠sx09**

## 本号范围

| 项 | 做了 | 另号 |
|----|------|------|
| next_farm | `EmberAfkService.papi` 扩键；纯函数 `nextFarmLine` / `gapMatShort`；打稳复用 `shouldUpgradeHint`（菜单侧 `alreadyHinted=false`） | — |
| 路由 | 既有 `afk_*` → GROWTH → `EmberAfkService.papi` | — |
| 战况挂键 / Open 半行 | — | W1b/c TrMenu 另号 |

## 键清单（STATUS 钉死）

| Placeholder | 优先态 | 半行 |
|-------------|--------|------|
| `%corerpg_p1_afk_next_farm%` | 1 `afk_full` | `今日已满 · 去冒险/短征/工坊` |
| | 2 无更高 / 下一层未解锁 | `本层继续养 · 更高层需通主线`；T4：`已是最高 · 打满去花或短征` |
| | 3 下一层已解锁且打稳（`upgrade_hint` 开 · sessKills≥min · kph≥min · 本场无死 · 未满） | `可试{层名} · 多养{层差物} · 点上排换层` |
| | 4 已解锁未打稳（含 `upgrade_hint` 热关诚实降级） | `本层继续打稳 · 满速后再试{层名}` |

**层差物：** 灰坡→荒原=`核心碎片`；荒原→焦土=`胚料`；焦土→烬原深处=`最高档`。

**纪律：** 只读派生；禁暗示抬日顶/层表；禁「换层必出」绝对承诺；不自动换层；不拼仓差。

## 不动

daily_kills / afk.tiers / 离线% · feel.upgrade_hint* 默认 · gate_daily · 观察三开关 / ×0.97 · K3 · sx09 键 · 主仓切分支 · 用缺 six_slot 默认 yml 盖 live

## 验收（本号）

| # | 项 | 结果 |
|---|----|------|
| V-pure | 满/最高/未解锁/可试/未打稳/hint 关 | EmberAfkServiceTest.nextFarm_D411 |
| V-route | afk_next_farm → GROWTH | EmberRunPapiTest |
| V-cfg | daily_kills 仍 2400；upgrade_hint 默认仍开 | EmberAfkServiceTest |
| V-play | jar 装 play · Enabling | 见 tip/jar 下表 |

## tip / jar

| 项 | 值 |
|----|-----|
| tip | `TIP_PENDING` |
| jar | `CoreRpg-1.65.119-d411.local.jar` |
| sha256 | `SHA_PENDING` |
| Enabling | `CoreRpg v1.65.119-d411.local` |

## 下一号

菜单岗 W1b：`ember_p1_afk` 格 I 挂 `%corerpg_p1_afk_next_farm%`（键未 live 前禁假写）。
