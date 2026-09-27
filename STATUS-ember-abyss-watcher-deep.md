# STATUS · EmberAbyssWatcherDeep（阶段 4.1 怪物岗）

- 时间：2026-09-27（Asia/Shanghai）
- 执行：余烬-怪物
- 依据：`docs/design-stage4-abyss-9-12.md` §2.2 / §3
- 未改：DungeonPlus / CoreRpg jar；1～8 现怪数值；ops（始终 `[]`）

## 文件

| 路径 | 动作 |
|------|------|
| `plugins/MythicMobs/Mobs/EmberAbyss.yml` | **新增** `EmberAbyssWatcherDeep`（1～8 块未回滚） |

## Display（须与后续 DP `$kill` 一字不差）

| | |
|--|--|
| YAML Display | `'&5余烬深渊·看守·深'` |
| 去色后 / kill 名 | **`余烬深渊·看守·深`** |

## 改前 → 改后

| 项 | 改前 | 改后 |
|----|------|------|
| MM ID | （无） | **`EmberAbyssWatcherDeep`** |
| Type / 骨架 | — | 同 `EmberAbyssWatcher`：`WITHER_SKELETON` + 钻盔 |
| Health / Damage | — | **2200 / 4** |
| Gaze | — | 复用 `EmberAbyssWatcherGaze`；`~onTimer:100`（现看守为 120） |
| Slam | — | 同看守：`EmberCryptBruteSlam` ~onAttack 0.12 |
| mmxp | — | `corerpg mmxp <caster.uuid> elite` |
| 核心碎片 | — | `mat_ember_core_fragment` ×1 **必掉** |
| 随机孔石 | — | sharp/steady/drain/gale 各 **0.1125**（合计期望 ≈45%） |
| 保护券 | — | `mat_ember_protect_scroll` **0.12** |
| 稳定符 | — | **不掉**（周首通，插件） |

1～8 对照（未改）：潮尸 110/5 · 骨潮 80/4 · 混潮 130/5 · 蛮层 450/4 · 看守 1500/3。

## 加载

- 游玩服短停后 `./start.sh custom`（MM 启动加载；非改 ops）
- 日志：`成功加载 36 个怪物`（较此前 +1）
- `server-runtime/ops.json` = `[]`

## 插件岗待接

- DP floor9～12 / `$kill` 名 = `余烬深渊·看守·深`
- 小怪 9～12 复用现 ID（Brute/Zombie/Mix/Skeleton）


## 4.3 追加（2026-09-27）

Health **2200→20000** / Damage **4→7** / LM health **12→0**。详见 `STATUS-ember-abyss-watcher-deep-4.3.md`。


## 4.3b 追加

Health **20000→7500** / Damage **7→18**。见 `STATUS-ember-abyss-watcher-deep-4.3b.md`。
