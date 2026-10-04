# 发布凭证 · CoreRpg 1.65.24（2026-10-05）

D185 奖励精英变招 Pack 2：Extra.ELITE 每图再加 1 条互补轻招（`elite_twists.alt`，与 Pack 1 交替）。设计：`docs/design/DESIGN-ember-reward-elite-twists-pack2-2026-10-05.md`。

## 线上状态（部署后核对于 2026-10-05 Asia/Shanghai）

| 项 | 值 |
|---|---|
| CoreRpg 版本 | **1.65.24** |
| class major | **52**（JDK 8，tools/jdk8u504-b01）|
| CoreRpg JAR sha256 | `a8e87ec5e0953fa56f6c83c698550265fa2c9b063360303fc4e2ee40e936cb94` |
| balance_version | **47** |
| 回滚包 | `/workspace/backup/CoreRpg-1.65.23-pre-1.65.24.jar` |
| 服务器 PID | **1228980** |
| MySQL | `[CoreRpg] [storage] MySQL connected` · `[CoreGacha] [db] MySQL connected` |
| SEVERE | 0 |
| p1sim Stage C | deferred（同 D182：Extra.ELITE 无招式模型）|

## 内容

- Q01 灰烬扇（cone，every 16，alt of 门廊推）
- Q02 焦线（line，every 16，alt of 焦焰踏）
- Q03 誓踏（circle ahead 0，every 16，alt of 誓印扫）
- Q04 潮扇（cone，kb=0，every 16，alt of 闸冲）
- Q05 碎带（line，every 16，alt of 落尘）
- Q06 霜环踏（circle ahead 0，every 16，alt of 霜息）
- Q07 炉扇（cone，every 16，alt of 矿渣劈）
- Director 交替 primary↔alt；刷出聊天带两招名；奖励 10+1 / Extra 权重不变
- 单测 `_D185` + bv47 断言

## 验收

| 项 | 结果 |
|---|---|
| 单测 `mvn -o test`（JDK 8）| 281 / 0 |
| p1sim Stage C | deferred（同 D182）|
| 冒烟 | deferred to batch (POLICY 01:53; packs since 1.65.20) |
| 资产回归 | 无物品资产路径变化 → 未跑 persist-roundtrip |
