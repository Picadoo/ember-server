# STATUS · B2.10 DP 局内/超时假票→体力 A

- 批准 tip：`bf174fc74e1d28cfb99fd234230ed80a423fb75d`
- 设计 tip：`f2eb738d9230e1774e5384d844d2d010278f708a`
- 范围：仅 DungeonPlus 玩家可见 `$message` / timeout 文案；未改扣费、门控、奖励或玩法逻辑。
- 施工时间：2026-09-28 23:37 Asia/Shanghai

## 九处落地

| # | 文件 | 替换结果 |
|---:|---|---|
| 1 | `plugins/DungeonPlus/dungeon/EmberDaily/task/timeout.yml` | `日票不返还` → `体力不返还` |
| 2 | `plugins/DungeonPlus/dungeon/EmberWeekly/task/timeout.yml` | `周票不返还` → `体力不返还` |
| 3 | `plugins/DungeonPlus/dungeon/EmberAbyss/option.yml` 撤离 | `票不退` → `体力不退` |
| 4 | `plugins/DungeonPlus/dungeon/EmberAbyss/option.yml` 结算 | `票不退` → `体力不退` |
| 5 | `plugins/DungeonPlus/dungeon/EmberAbyss/task/timeout.yml` | `深渊票不返还` → `体力不返还` |
| 6 | `plugins/DungeonPlus/dungeon/EmberRaid/option.yml` 开场 | `已扣除余烬团本票 ×1` → `已消耗体力 ×1` |
| 7 | `plugins/DungeonPlus/dungeon/EmberRaid/option.yml` 通关 | `团票每周 1` → `周首通一次` |
| 8 | `plugins/DungeonPlus/dungeon/EmberRaid/task/timeout.yml` | `团本票不返还` → `体力不返还` |
| 9 | `plugins/DungeonPlus/dungeon/EmberEliteWeekly/task/timeout.yml` | `票已扣` → `体力已扣` |

## 校验与热更

- 静态核验：九处新文案已落盘；玩家可见 message 行未检出 `日票|周票|团票|深渊票|团本票|票不退|票已扣|余烬团本票`。
- 禁项零 diff：`text=` 门控表达式、人数/等级条件、`stamina.costs`、loot、MM、TrMenu、quest、灾厄 OP 均未改；注释中的历史「票」保留。
- 热更命令：`printf 'dp reload\n' > server-runtime/console.in`
- 热更证据：`server-runtime/logs/latest.log`，`[23:37:56] [Server thread/INFO]: [DungeonPlus] 插件重载完毕`（Asia/Shanghai）；随后 EmberAbyss、EmberEliteWeekly、EmberRaid 等内容初始化完成。

## 交付

- 建议提交：`fix(dp): B2.10 in-run timeout copy ticket→stamina`
- 只 stage 本次 7 个 DP YAML 与本 STATUS；未 push。
- 未施工方案 B：quest 深渊票两句、灾厄 OP #6。
