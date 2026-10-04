# 发布凭证 · CoreRpg 1.65.10（2026-10-04）

D174 主线签名传奇 · 第 1.5 阶段（签名页零命令）。设计：`docs/design/DESIGN-ember-mainline-unlocks-2026-10-04.md`（§5 状态、§9 上线记录）。

## 线上状态（核对于 2026-10-04 18:35 Asia/Shanghai）

| 项 | 值 |
|---|---|
| CoreRpg 版本 | **1.65.10**（日志 `Enabling CoreRpg v1.65.10`）|
| class major | **52**（JDK 8，tools/jdk8u504-b01）|
| CoreRpg JAR sha256 | `727c8f75d3bb1246685de8b0683a4e787400531baf4bff014c6b77e5b67f9a70` |
| jar 条目数 | 2352 |
| balance_version | **33** |
| 代码提交 | `108a0a3` |
| 回滚包 | `/workspace/backup/CoreRpg-1.65.9-pre-16510.jar` |
| MySQL | `[CoreRpg] [storage] MySQL connected` · `[CoreGacha] [db] MySQL connected` |
| SEVERE | 0 |
| 服务器 PID | 860609（18:35），`server-runtime/stop.sh` → `start.sh` |

## 内容

TrMenu `ember_p1_sig`（按首领的图鉴：已有 / 没见过 / 未解锁 + 效果 / 代价、徽记、正在用的刃 / 护符点击开关）+ `ember_p1_sig_imprint`（选件、费用、核对、确认；覆盖要 Shift+点击，服务器再核一次）；装备页图标 + 大厅说明一行；首通解锁消息直接打开页面；「见过」计数；烙印的城镇限制也挡副本世界

## 验收

| 项 | 结果 |
|---|---|
| 单测 `mvn -o package` | 252 / 0 |
| p1sim | 不改数值（只加菜单） |
| 冒烟 | FreshQ150（修冒烟脚本）+ FreshQ151 23 / 0：装备页图标 → 签名页 → 点 L02 → 烙印页 → 点确认（菜单里 saved=true）→ 关 / 开 → 覆盖普通点击被拒、Shift+点击成功 → 返回 → 装备页 |
| 资产回归 | 菜单是新资产，但不经过物品进出仓库 / 装备库 → 没跑整套 persist-roundtrip |

## 如何复核 JAR

```bash
JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01
# git archive 108a0a3 CoreRpg | tar -x -C /tmp/x && cd /tmp/x/CoreRpg && mvn -o -q package
# （把 plugins/PlaceholderAPI.jar、plugins/NeigeItems-1.21.151.jar 链到 /tmp/x/plugins/）
```
