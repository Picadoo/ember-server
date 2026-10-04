# 发布凭证 · CoreRpg 1.65.23（2026-10-05）

D173 Boss Moves Pack 2：主线 Q01–Q07 半血转阶段轻压。设计：`docs/design/DESIGN-ember-boss-moves-pack2-2026-10-04.md`。

## 线上状态（部署后核对于 2026-10-05 Asia/Shanghai）

| 项 | 值 |
|---|---|
| CoreRpg 版本 | **1.65.23** |
| class major | **52**（JDK 8，tools/jdk8u504-b01）|
| CoreRpg JAR sha256 | `JAR_SHA_PLACEHOLDER` |
| balance_version | **46** |
| 回滚包 | `/workspace/backup/CoreRpg-1.65.22-pre-1.65.23.jar` |
| 服务器 PID | **PID_PLACEHOLDER** |
| MySQL | `[CoreRpg] [storage] MySQL connected` · `[CoreGacha] [db] MySQL connected` |
| SEVERE | 0 |
| bossmoves 门禁 | MAX_ABS_DPP **2.2**（`tools/p1sim/out-bossmoves-d173.md`；硬顶 3，prefer ≤2）|

## 内容

- Q01 门廊突刺（line，below 0.5，every 40，light）
- Q02 焦冲（charge，below 0.5，every 18，light）；砸地 12.5→13.0
- Q03 誓印扇（cone follow on 誓印圈，below 0.5）；誓斩 13.5→16.5；誓印圈 15→18
- Q04 回浪（line follow on 潮涌）；潮涌 15→18
- Q05 余震（circle follow on 落石）；落石 14→19
- Q06 霜锥（cone follow on 霜环）；刀气 12.5→13；霜环 14→18
- Q07 矿渣（circle target:player follow on 矿锤横扫）；矿锤 15→17.5
- 半血全队提示：`§e首领进入半血 · 招式变强，盯紧预警`（顶层 below 或 follow below）
- 图录七格 + 连战摘要同步；单测 `_D173`

## 验收

| 项 | 结果 |
|---|---|
| 单测 `mvn -o test`（JDK 8）| 280 / 0 |
| bossmoves 5×4000 | MAX_ABS_DPP 2.2 |
| 冒烟 FreshQ370+ | SMOKE_PLACEHOLDER |
| 资产回归 | 无物品资产路径变化 → 未跑 persist-roundtrip |
