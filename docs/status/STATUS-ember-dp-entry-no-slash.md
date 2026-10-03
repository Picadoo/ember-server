# STATUS — B2.8 DP 进本拒门去斜杠 A

- **批复 tip:** `4c99c21` — `docs: approve B2.8 DP gate no-cmd (A)`
- **设计 tip:** `83d641a` — `docs: design B2.8 DP gate no-cmd thin window (A); sync backlog`
- **施工时间:** 2026-09-28 23:10 CST (UTC+8)
- **结果:** **PASS · 勾销**；只改玩家可见 message 字符串 #1～#5；未 push。

## 5 处精确替换

| # | 文件 | 变更 |
|---:|---|---|
| 1 | `plugins/DungeonPlus/dungeon/EmberWeekly/option.yml` | 等级门提示去 `/corerpg level 查看`，改为 `· 打开枢纽 → 角色查看等级`；保留 `Lv.20` |
| 2 | `plugins/DungeonPlus/dungeon/EmberRaid/option.yml` | 同上；保留 `Lv.35` |
| 3 | `plugins/DungeonPlus/dungeon/EmberAbyss/option.yml` | 同上；保留 `Lv.25` |
| 4 | `plugins/DungeonPlus/dungeon/EmberGuildBoss/option.yml` gate | 改为 `需由队长在枢纽 → 盟约 →「周盟 Boss」开启（消耗盟约贡献）` |
| 5 | `plugins/DungeonPlus/dungeon/EmberGuildBoss/option.yml` 开场 `$message` | 改为 `进本由盟约菜单「周盟 Boss」门控（贡献消耗）` |

## 校验与热更

- live `plugins/DungeonPlus` 与 runtime `server-runtime/plugins/DungeonPlus` 四个目标文件 inode 相同，未重复改 runtime。
- `git diff --check`: PASS。
- `printf 'dp reload\n' > server-runtime/console.in`: PASS，写入时间 `2026-09-28 23:10:04 CST`。
- `server-runtime/logs/latest.log`：`23:10:04` 各 Ember 地牢内容导入、地图导入，随后 `23:10:04 [DungeonPlus] 插件重载完毕`；`23:10:05` 各目标地牢内容初始化完毕。

## 禁项

- `$js-condition` / `$team-condition` 的 `text=`、人数门、体力、TrMenu、loot、MM：**EMPTY（零 diff）**。
- 灾厄 OP #6：**未做**。
- `#` 注释中的 `/corerpg`：保留。
- Git push：**未执行**。
