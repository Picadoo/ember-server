# STATUS · TIP-CY W1a 短征花样结算回看 · D13330

> 2026-10-14 Asia/Shanghai · 余烬-插件岗 · **W1a 已交** · 勿勾 tip CLOSED · 物品 V 可并行/已交

## 交付
- `EmberShortVarietySettle`：settle 从 session 取 `affix`/`eventKind`（trim；空=无）写入 period 索引仓 `p1_short_var_affix` / `p1_short_var_event`（`@all`；对齐 ShortSettle int 编码 · KNOWN/EVENTS 1-based）
- 每次短征 settle **覆盖**到下次；`hit` / `affixLabel` / `eventLabel` / `line`（无 hit→「本局无花样」；有→「词缀 / 事件」）
- 中文标签复用 `EmberRunMaps.Variety.label` / `eventLabel`；未知 id 诚实回退 id
- 挂钩：`EmberShortService.settle` 旁路 `remember(pd, s)`（try/catch 吞；有奖/无奖/重复 settle 都记）
- PAPI：`%corerpg_p1_short_var_hit%` / `_affix_label` / `_event_label` / `_line`（Section.SHORT）
- 单测：`EmberShortVarietySettleTest`（空/仅affix/仅event/两者/覆盖/零经济键）；`EmberRunPapiTest` 路由四键
- `EmberCounters`：登记 `p1_short_var_affix` / `p1_short_var_event`（顺带补登记既有 `p1_short_settle_last`）
- jar：`1.65.823-d13330.local` · sha256 `62cc4277282b…` · ECONOMY_BV **123** 未动
- play：live `1.65.823-d13330.local` · storage=mysql · bv123 · MySQL connected
- **零发** event_core / affix_shard / NI；**零改** rate / variety yaml / 日帽 / S / targets / soft-reward / ShortPath / Cast / Aid / ShortSettle map 语义 / TrMenu（物品岗）

## 键
| 项 | 口径 |
|----|------|
| `short_var_hit` | hit→1 否则 0 |
| `short_var_affix_label` | 中文词缀；空→空 |
| `short_var_event_label` | 中文事件；空→空 |
| `short_var_line` | 有 hit 拼标签；无→本局无花样 |

## 落点
- `EmberShortVarietySettle`（新）
- `EmberShortService.settle`：variety remember
- `EmberRunPapi`：route / isShortDayKey / `shortVariety`
- `EmberCounters`：period 登记
- `pom.xml` / `plugin.yml`：1.65.823-d13330.local

## 零改边界
- event_rate / variety weights / 新 kind / variety yaml
- 日帽 / S40–S58 / targets / soft-reward claim / ShortPath resolve / Cast / Aid / todayRot
- ShortSettle map 记忆语义（只旁路加 variety）
- TrMenu yml · ECONOMY_BV 123 · sx20

## 下步
- 物品 W1b 结算薄页 V（Layout 增 V · 接 short_var_*）— 可接/并行
- 测岗抽测 PASS 后总控收口 CLOSED（本岗勿勾）

## 抽测
- 测岗 PASS live · STATUS/`176e2fe03` · 证据 `/workspace/tmp/tip-cy-variety-settle/` · **勿勾 tip CLOSED** · 2026-10-14 08:34 Asia/Shanghai
