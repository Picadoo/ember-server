# 日图恢复实测（Release v2026.10.01）

**时间：** 2026-10-01 19:25–19:33 CST · 测试机器人：EmberTestOp（非 op）· 脚本：`mineflayer-tests/daily-maps-check.js`

## 恢复了什么

- `gh release download v2026.10.01` → `/workspace/release-dl/ember-binaries-20261001.tar.xz`，`sha256sum -c` **OK**。
- 解到暂存目录后，**只**放入本地缺失的目录（`cp -a`，不覆盖任何已有文件）：
  - 世界：`server-runtime/ember_daily_{ash,crypt,frost,rail,spire,tide}`、`ember_daily_{frost,rail,spire,tide}_build`、`ember_daily_build`、`ember_abyss_build`
  - DP 地图：`plugins/DungeonPlus/map/ember_daily_{ash,crypt,frost,rail,spire,tide}`、`ember_elite`
- **没有动**：数据库（release 里的 `sql/*.sql` 与本地 `sql/` 逐字节相同，线上库未导入）、全部 jar。差异如下，仅报告未部署：
  - `plugins/CoreRpg.jar`：release 版也标 1.15.28，但与本地已部署的 1.15.28 构建字节不同，保留本地版。
  - `plugins/LuckPerms-Bukkit-5.4.145.jar`（及 `_downloads/` 备份）：本地没有，未安装（会改权限体系，需要决定）。

## 结果

重启游玩服后，Multiverse 正常加载 `ember_daily_ash` / `ember_daily_crypt`（不再告警缺失），DungeonPlus 日志对六条日图线和 `ember_elite` 都打印「地牢地图导入成功」。

机器人以玩家方式 `/corerpg enter <线>` 进入（控制台给门票、补体力，升到 Lv.10）：

| 线 | 结果 | 进本提示（截取） |
|----|------|------------------|
| daily_ash | PASS | 余烬窟·焦骨甬道 开始！… 实例 `dungeon_EmberDailyAsh_CE1CA362`，出生 (0,65,0) |
| daily_crypt | PASS | 余烬窟·残誓地窖 开始！… 实例 `dungeon_EmberDailyCrypt_FB6FDBED`，出生 (0,72,0) |
| daily_tide | PASS | 余烬窟·潮蚀水道 开始！ |
| daily_spire | PASS | 余烬窟·断塔回廊 开始！ |
| daily_frost | PASS | 余烬窟·霜晶裂隙 开始！ |
| daily_rail | PASS | 余烬窟·锈轨矿道 开始！ |
| elite | 门槛拦截（预期） | 余烬精英需要余烬 Lv.40（当前 Lv.10） |

注：DungeonPlus 离开副本后 5 秒内不允许再进（「副本挑战过快」），脚本在两条线之间等 6 秒。
