# TEST · B2.123 挂机免费通关（周常 / 团本）— 根因与修复实测

**日期：** 2026-10-01 20:27–21:25 CST（Asia/Shanghai）· 执行：Grok executor · CoreRpg 1.15.29 → **1.15.30**

## 现象
无敌挂机 bot（抗性 IV + 再生 + 饱和，原地不动）在周常约 17 分钟、团本约 30 分钟被判通关：通关箱、`weekly_clear`/`raid_clear`、周首通团戒全部发放（19:36 周常 / 19:41 团本两局）。

## 根因（已实测确认）
1. **怪在石头里窒息，DP `$kill` 照算。** 本机 `plugins/DungeonPlus/map/` 的 `ember_weekly`、`ember_raid`、`ember_abyss`、`ember_daily` 模板仍是 09-21 的底图（gitignore，不入库，从未随 P2–P5 重建刷新）。`monster.yml` 用的是 P2–P5 新房间坐标，在旧图里全在实心方块中。
   - 开 `debug.mm_credit` 复现（20:27 周常，挂机）：每只怪 `last=SUFFOCATION`，无人击杀（`-> none`）：
     `20:29:36 mmcredit … en=SKELETON, last=SUFFOCATION` ×3 → 波次结束；`20:29:54 … ZOMBIE, last=SUFFOCATION` ×2；`20:32:34 mmxp … elite … ZOMBIE, last=SUFFOCATION`（蛮兵·甲）。
   - 时间吻合：窒息 1 点/0.5 秒 → 僵尸 110 血 ≈ 55 秒（前厅 57 秒清空）、蛮兵·乙 1500 血 ≈ 12.5 分钟、团本使徒 2000 血 ≈ 17 分钟。
   - 静态复核（`scripts/check-dp-spawns.py`，DP_MAP_ROOT=旧模板备份）：周常 7 个刷点/传送点「head inside block id 1/3 → suffocates」；深渊 22 个同类；团本、日常（庭院）旧图在新坐标处连区块都没有。玩家传送点同样在石头里（bot 靠抗性活着）。
2. **日照不是原因。** 副本实例 `mvgamerules`：`doDaylightCycle: false`；模板 DayTime 14538–14942（夜）。新图里怪刷点是露天的，但时间锁夜，无燃烧。未改 MM `PreventSunburn`。
3. **DP 计数**：`$kill{mobname=…}` 按怪名计死亡，不区分死因（窒息 / 环境都算）；DP 无「仅玩家击杀」选项。

## 修复
- **地图（本机，非仓库）**：4 个旧模板备份到 `/workspace/backup/dp-map-stale-20261001/`，用 Release v2026.10.01 资产覆盖（13 个模板现与 Release 逐文件一致）；重启游玩服重建 `dungeon-caches`。
- **兜底（仓库，CoreRpg 1.15.30 `QuestService.onInstanceMobSuffocate`）**：`dungeon_*` 实例里的 MythicMobs 怪不再受窒息伤害，抬到上方第一个 2 格空位，并 WARN 一次坏点位。覆盖周常/团本/深渊/精英/日常/灾厄/公会 Boss 全部 DP 实例。
- **检查脚本**：`scripts/check-dp-spawns.py`（无依赖、只读）。现模板：0 FAIL；2 个 WARN（不窒息）见下。

## 实测
| 项 | 结果 |
|---|---|
| 周常挂机（新模板，1.15.29）20:36:03 进本 | 25 分钟无一波清空、无 `mmcredit` 死亡；**21:01:05 超时触发** `周常超时失败。这次不算通关。` / `深核·周 超时失败`（PASS after 1495s） |
| 团本挂机 3 bot（新模板）20:36:33 进本 | 40 分钟无清波、无 `raid_clear`/团戒；**21:16:36 超时触发** `团本超时失败。这次不算通关。` / `余烬团本 超时失败`（PASS after 2397s） |
| 兜底（1.15.30）21:19:41 在周常实例 `-40.5,63,280.5`（地板里）`mm mobs spawn EmberWeeklyZombie` | `21:19:42 WARN MM mob 'EmberWeeklyZombie' was suffocating … (damage cancelled …)`；70 秒无死亡（无兜底约 55 秒死） |
| 深渊挂机 150s（1.15.30）| mmDeaths=0 · 无窒息 WARN · PASS |
| 日常庭院挂机 150s（1.15.30）| mmDeaths=0 · 无窒息 WARN · PASS |
| 精英 | 模板 20:00 前已是 Release 新图；19:48 挂机 718s 超时触发（B2.109） |

## 余项（未改，列出）
- `check-dp-spawns.py` WARN：EmberDaily `EmberDailyZombie @ ±3,65,27` 脚在石砖地板里；EmberDailyAsh `EmberAshZombie @ 8,65,10` 头在铁栏里（不窒息，可能卡住）。需挪刷点，属地图/关卡调整。
- 现模板已与 Release 一致，但模板仍不入库；新机器必须用 Release 资产（HANDOFF 已写）。
