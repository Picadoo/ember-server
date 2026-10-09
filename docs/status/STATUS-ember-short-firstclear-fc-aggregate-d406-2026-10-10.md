# 状态 · D406：短征生涯首通合计键（批 A·M · W1d 可选）

**日期：** 2026-10-10（上海时间）  
**上游：** 总控派单 · DESIGN [`DESIGN-ember-short-firstclear-visible-2026-10-10.md`](../design/DESIGN-ember-short-firstclear-visible-2026-10-10.md) **§2.1 / W1d**  
**裁决：** **本号交** 确认六本 `_fc` live + 可选合计键 + 单测 + 装 play · **TrMenu W1a/b/c 另号** · **未动** 日帽 / S40–S45 / 首通包数额 / afk / gate_daily / 观察三开关

## 本号范围

| 项 | 做了 | 另号 |
|----|------|------|
| 六本 `%corerpg_p1_sx0N_fc%` | **确认** `route→MAP` + `mapField("fc")` + `shortMaps.byKey` | W1a/b 菜单挂键 |
| `sx_fc_left` / `sx_fc_pending_line` | SHORT 段只读合计（扫 sx01–sx06 `!firstCleared`） | W1c I/Open 挂键 |
| 日帽 / 结算 / 首通包 | **未改** | — |

## 六本 `_fc` 抽样（STATUS 钉死）

| 键 | route | 未领显示（`firstClearLabel`） | 已领 |
|----|-------|------------------------------|------|
| `%corerpg_p1_sx01_fc%` | MAP | `碎片 8 胚料 1 币 200` | `已领取` |
| `%corerpg_p1_sx02_fc%` | MAP | `碎片 6 胚料 1 币 180` | `已领取` |
| `%corerpg_p1_sx03_fc%` | MAP | `碎片 8 胚料 1 币 160` | `已领取` |
| `%corerpg_p1_sx04_fc%` | MAP | `碎片 6 胚料 1 币 150` | `已领取` |
| `%corerpg_p1_sx05_fc%` | MAP | `碎片 6 胚料 1 币 140` | `已领取` |
| `%corerpg_p1_sx06_fc%` | MAP | `碎片 6 胚料 1 币 130` | `已领取` |

真源：`EmberRunPapi.mapField("fc")` → `firstCleared` ? `已领取` : `m.firstClearLabel()`；`maps.byKey` 含 `shortMaps`。

## 新增合计键清单

| Placeholder | 行为 |
|-------------|------|
| `%corerpg_p1_sx_fc_left%` | 未领本数 `0..6` |
| `%corerpg_p1_sx_fc_pending_line%` | `还有 N 本生涯首通未领` / `六本首通已齐` |

**纪律：** 只读；禁与日有奖次数混写；禁改 `first_clear` YAML / S40–S45。

## 不动

日帽顶 / S40–S45 有奖与首通包数额 · afk.tiers / daily_kills · gate_daily · 观察三开关 / ×0.97 · K3 · 主仓切分支 · 用缺 six_slot 默认盖 live ember-v1.yml

## 验收（本号）

| # | 项 | 结果 |
|---|----|------|
| V-fc | 六本 byKey+shortMaps+label+route MAP | EmberShortRulesTest.shortMaps_firstClearLabel_D406_sixFcSample |
| V-agg | fcLeft / pending_line 边界 | EmberRunPapiTest.textsMatchPreSplit |
| V-route | sx_fc_* → SHORT；sx0N_fc → MAP | EmberRunPapiTest |
| V-play | jar 装 play · Enabling | 见 tip/jar 表 |

## tip / jar

| 项 | 值 |
|----|-----|
| tip | `af408d9e` |
| jar | `CoreRpg-1.65.115-d406.local.jar` |
| sha256 | `d9a16337d24144406f70586119f7a3762a8c7614bf8ad0276cf7d16119f69293` |
| Enabling | `CoreRpg v1.65.115-d406.local` · play PID 1191598 |

## 下一号

菜单岗 W1a/b 已 `3e8cff26`；**合计挂键** I/Open → `sx_fc_left` / `pending_line`（本号续交 · 见 menu STATUS）。勿派测。
