# STATUS · 灾厄公共窗（ember_event）

**日期：** 2026-09-13（Asia/Shanghai）  
**CoreRpg：** **1.4.7** · 主配置 `plugins/CoreRpg/calamity.yml`

## LIVE

| 项 | 值 |
|----|-----|
| 版本 / jar | 1.4.7 · `plugins/CoreRpg.jar` 4752663 B |
| 世界 | `ember_event` |
| 坐标 | -96.5, 64, 266.5（MV spawn） |
| 刷怪 | `mm m spawn EmberCalamityBoss 1 {world},{x},{y},{z}` |
| 窗 | 12:00 / 20:00 / 22:00 · 10 分 · Asia/Shanghai |
| 状态文件 | `calamity-state.yml`：lastFireDate, firedTimes, currentWindowSlot, windowOpen, windowEndEpochMs, bossSpawnedThisWindow, bossKilledThisWindow |
| 玩家字段 | `calamityChestDate`（上海日 yyyy-MM-dd）日箱限 1 |
| 命令 | `/corerpg calamity` 窗外=`message_closed`+`{next_window}`；开窗=进行中+结束时刻+日箱已领/未领 |
| 强制 | `forceopen`/`trigger` · `forceend` · 同窗不二刷 |
| 公开 API | `CalamityService.isWindowOpen()` |
| 重启 | **OK** 17:08 CST · log `Calamity window service 1.4.7 loaded` |

## 冒烟（RpgBot · 17:08 CST）

| 步 | 结果 |
|----|------|
| status 窗外 | `灾厄未苏醒。下一窗：20:00` + 日箱未领 **PASS** |
| forceopen | 全服 `message_open` + spawn OK 祭坛 **PASS** |
| status 开窗 | `灾厄进行中 · 结束 17:18:29 CST` **PASS** |
| 再 forceopen | `本窗已刷过 Boss，同窗不二刷` **PASS** |
| forceend | `本轮窗口结束，灰烬合拢` + despawn 1 **PASS** |
| status 再关 | `message_closed` + 下一窗 20:00 **PASS** |

击杀发 A/B 已接线（EntityDeathEvent → onCalamityKilled）；本次冒烟未实打死 Boss（forceend 清场）。

## 对表 `docs/ember-calamity-window-spec.md`

| 验收 | 状态 |
|------|------|
| 1 enabled + ember_event 坐标 | **PASS**（calamity.yml 主读） |
| 2 forceopen 刷 Boss | **PASS** |
| 3 日箱限1 / 同窗不二刷 | **PASS**（字段+命令；击杀 B 待实刀） |
| 4 forceend / 10 分结束 | **PASS** |
| 5 主路径 mvtp 非 DP | **PASS** |
| 6 不影响深渊 / 材料仓 | **PASS**（未改那些服务） |
