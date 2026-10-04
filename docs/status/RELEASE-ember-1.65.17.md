# 发布凭证 · CoreRpg 1.65.17（2026-10-04）

D183 烬爆族返工（D174 签名传奇）。设计：`docs/design/DESIGN-ember-mainline-unlocks-2026-10-04.md` §12；原始表 `tools/p1sim/out-d174-mainline.md` §L。

## 线上状态（核对于 2026-10-04 21:38 Asia/Shanghai）

| 项 | 值 |
|---|---|
| CoreRpg 版本 | **1.65.17**（日志 `Enabling CoreRpg v1.65.17`，21:37:38）|
| class major | **52**（JDK 8，tools/jdk8u504-b01）|
| CoreRpg JAR sha256 | `1748782af01c0873afdc3088a81b1302ad2dd8cb5cd06a93b6e5cac6dfc713f9` |
| jar 条目数 | 2357 |
| balance_version | **40** |
| 代码提交 | `7b985fc`（rebase 在 1.65.16 `2bb75d6` / docs `90070a9` 之上）+ 文档 `c72ca58` |
| 回滚包 | `/workspace/backup/CoreRpg-1.65.16-pre-16517.jar` |
| MySQL | `[CoreRpg] [storage] MySQL connected` · `[CoreGacha] [db] MySQL connected` |
| SEVERE | 0 |
| 服务器 PID | 1033315（`server-runtime/stop.sh` → `start.sh`，21:37）|

## 内容

守炉重锤（L04，Q02 烬爆刃）代价改成只有「对首领伤害 ×0.985」（原来：被首领预警招打中多受 3% + 对首领伤害 ×0.98）。炉心护符 / 回廊护符不动。存档不变（签名按 id 存，数值在 `EmberSignature` 常量里）。

## 验收

| 项 | 结果 |
|---|---|
| 单测 `mvn -o package` | 268 / 0（新增 `burstReworkKeepsTheHammerOffTheTelegraphCost_D183`）|
| p1sim | 种子 13/17：单戴 +2.6 / −1.2；组合 +L02 +2.0 / −0.7、+L03 0.0 / −1.7、+L06 +0.5 / −2.6（旧 −3.0 / −3.3 / −4.0）；叠天赋可达 +1.4；W30 −0.24～+0.31 周 |
| 冒烟 | FreshQ230 6 / 6：L04 mods `dmg_boss=0.985`、没有 `taken_tele`；L04 + L03 同时生效，`taken_tele=1.03` 只来自护符；图鉴显示「代价：对首领伤害 ×0.985」|
| 资产回归 | 没有物品资产路径变化 → 没跑 persist-roundtrip |
