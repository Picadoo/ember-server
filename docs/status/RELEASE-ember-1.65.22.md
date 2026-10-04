# 发布凭证 · CoreRpg 1.65.22（2026-10-05）

D179 Room Events Pack 3：房间事件池 3→6（+hold 占点 / beacon 护灯 / relay 传火）。设计：`docs/design/DESIGN-ember-room-events-pack3-2026-10-04.md`。

## 线上状态（部署后核对于 2026-10-05 Asia/Shanghai）

| 项 | 值 |
|---|---|
| CoreRpg 版本 | **1.65.22** |
| class major | **52**（JDK 8，tools/jdk8u504-b01）|
| CoreRpg JAR sha256 | `JARSHA` |
| balance_version | **45** |
| 回滚包 | `/workspace/backup/CoreRpg-1.65.21-pre-1.65.22.jar` |
| 服务器 PID | **PAPERPID** |
| MySQL | `[CoreRpg] [storage] MySQL connected` · `[CoreGacha] [db] MySQL connected` |
| SEVERE | 0 |
| Stage C p1sim | **deferred**（与 D181/D176 同策略；时间紧，本窗未跑 42 格）|

## 内容

- `variety.events` +`hold` / `beacon` / `relay`（池 6）；词缀池不动（仍 8）
- hold 占点：圈内且 onGround 累计秒数；多人**不**加速；软时限 40s
- beacon 护灯：盔甲架+海晶灯柱；无玩家在 aggro_r 内则 tick 咬血；不进清房计数、不走 Extra.TREASURE
- relay 传火：有序标记 1→2→3；跳序无效；软时限 40s
- 强制钩子：`/corerpg p1 runs variety hold|beacon|relay[:rN]`
- 图录/冒险页三行扩到六事件；`event_core` / 花样委托 kind=`timed` 不变

## 验收

| 项 | 结果 |
|---|---|
| 单测 `mvn -o test`（JDK 8）| 279 / 0 |
| 冒烟 FreshQ350+ | SMOKE |
| 资产回归 | 无物品资产路径变化 → 未跑 persist-roundtrip |
