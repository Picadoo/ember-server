# STATUS · 短征 sx01 刷点钉死（EmberSx01 · D391 续）

- 时间：2026-10-10 Asia/Shanghai
- 执行：余烬-怪物
- 上游：总控派 · 地图壳 `ember_short_sx01` + DP `EmberSx01` 已登记 · MM tip `7a512ad7`
- 对照源：**Q01 / `ember_daily_v1`**（`ember-v1-runs.yml` `q01.rooms` / `boss.at`）+ DP 范式 `EmberDailyFrost`（分房 `$kill` + 门 AIR）
- 地图：`ember_short_sx01` = `cp -a ember_daily_v1`（README 已记）；模板门洞为 **AIR**（CoreRpg 开本关栅）；本号开本用 `$operation-block` 关/开

## 人话

短征三房刷点按 Q01 白盒房间坐标钉进 `EmberSx01`：前庭尸+弓 → 兵营伍长 → 终厅哨长。开本先封三道门洞铁栅，清房再开。旧七线日常没动。

## 改动文件

| 文件 | 改动 |
|------|------|
| `plugins/DungeonPlus/dungeon/EmberSx01/option.yml` | 开本关门×3（IRON_FENCE y64–66）+ `$monstergroup wave1` |
| `plugins/DungeonPlus/dungeon/EmberSx01/monster.yml` | wave1 / wave2 / boss 刷点 + 开门 + 传终厅口 |
| `plugins/DungeonPlus/dungeon/EmberSx01/obstacle.yml` | 注释：门由 operation-block 管 |
| 本 STATUS | 坐标表 / reload |

**未改：** MM 数值 / Cast；旧七线 Daily option/monster；体力；`ember_short_sx01` 小写壳模板；ops；map 二进制（gitignore）

## 刷点表

| 房 | 组 | MM ID | Display/$kill | 坐标（本地） | 数量 | 清后 |
|----|----|-------|---------------|--------------|------|------|
| R1 | wave1 | `EmberSx01Zombie` | 哨岗尸×3 | (-6,64,29)×2 · (0,64,29)×1 | 3 | 开 **门1** → wave2 |
| R1 | wave1 | `EmberSx01Skeleton` | 哨弓骷×2 | (6,64,29)×1 · (9,64,28)×1 | 2 | （同组 AND `$kill`） |
| R2 | wave2 | `EmberSx01Elite` | 哨岗伍长×1 | (30,64,39) | 1 | 开 **门2+门3** · 传 (0,64,91) → boss |
| R3 | boss | `EmberSx01Warden` | 烬门哨长×1 | (0,64,102) | 1 | `$end` **reward=false** COMPLETE（S40 交 CoreRpg） |

Cast：已在 MM `EmberSx01Warden` `~onTimer:80` 挂 `EmberSx01SentryCast`；DP 只挂 mob ID。

## 门洞（Q01 AABB · y 裁到 64–66）

| 门 | AABB（开/关） | 时机 |
|----|---------------|------|
| 门1 | x=16 · y=64..66 · z=29..35 | 开本关；wave1 end → AIR |
| 门2 | x=29..35 · y=64..66 · z=52 | 开本关；wave2 end → AIR |
| 门3 | x=18 · y=64..66 · z=91..97 | 开本关；wave2 end → AIR（跳空 r3 走廊，直达终厅口） |

出生：`0,64,5`（option 已有，与 Q01 同）。

## 静态校验

- `scripts/check-dp-spawns.py plugins/DungeonPlus/dungeon/EmberSx01` → **ok · 9 points · 0 bad**
- YAML safe_load OK

## 热更

| 命令 | 时间（CST） | 结果 |
|------|-------------|------|
| FIFO `mm reload` | 04:32:24 | **117** 怪 · **30** 技能 · 93 ms |
| FIFO `dp reload` | 04:32:27–28 | **插件重载完毕** · **`EmberSx01` 地牢内容初始化完毕** · `ember_short_sx01` 地图导入成功 |

- `plugins/` 与 `server-runtime/plugins/` **同 inode**（无需另拷）
- `login-runtime/ops.json` = `[]`；play ops 既有测试号 `D388Life`（本档未改）
- 服务保持可玩；未长停服

## 明确零改

霜/锈/庭/焦/潮/断/窖 七线 Daily · MM HP/攻/Cast 数值 · 体力 · gate_daily · afk

## 测岗建议

`corerpg p1 enter sx01`（或 pass 就绪后）：前庭清 → 东闸开 → 伍长 → 传终厅口 → 哨长 Cast 可读；通关 reward=false 无双发；七线日常回归零。

## 残留

- 地图主题 WE / 三区叙事重切仍待地图岗（坐标现沿用 Q01 结构）
- `ember-v1-runs.yml` `short.sx01` 仍无完整 rooms 表（键号已交；本号 DP 自驱波次）
