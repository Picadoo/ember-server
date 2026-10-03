# STATUS · EmberEliteWeekly 怪物（阶段 4.4 怪物岗）

- 时间：2026-09-27（Asia/Shanghai）
- 执行：余烬-怪物
- 依据：`docs/design/design-stage4-elite-weekly.md` §4；总控派活 4.4
- 未改：Paper / DP / CoreRpg；`EmberAbyss.yml` / `EmberAfk.yml`；ops（始终 `[]`）
- 稳定符：`mat_ember_stable_charm` **未**写入任何 MM 掉落（走周首通箱）

## 新文件

| 路径 | 内容 |
|------|------|
| `plugins/MythicMobs/Mobs/EmberEliteWeekly.yml` | 5 只试炼怪 |
| `plugins/MythicMobs/Skills/EmberEliteSkills.yml` | Rush / Ignite / BossPulse |

## 规格落地

| MM ID | Display（去色后） | Type | Health | Damage | 词缀要点 |
|-------|-------------------|------|--------|--------|----------|
| EmberEliteZombie | 余烬试炼·炽尸 | ZOMBIE | 160 | 6 | 自加速 + 攻击点燃 |
| EmberEliteSkeleton | 余烬试炼·骨刺 | SKELETON | 110 | 5 | 缓速 + 轻点燃；弓 |
| EmberEliteMix | 余烬试炼·混纹 | ZOMBIE | 180 | 6 | Pressure + 点燃 |
| EmberEliteBrute | 余烬试炼·蛮纹 | HUSK | 700 | 5 | Slam + 点燃；`mmxp elite` |
| EmberEliteBoss | 余烬试炼·烬纹执行官 | WITHER_SKELETON | **2800** | **4** | 初值；Pulse/Slam；`mmxp elite` |

Display 色码 `&e`；明文与设计稿 / DP `$kill` 对齐。

## 掉落（小额 mmgive）

- 炽尸：`mat_ember_shard` 0.45
- 骨刺：`mat_ember_bone_dust` 0.45
- 混纹：shard 0.35 / core_fragment 0.06
- 蛮纹：core_fragment 0.25 / shard 0.4
- Boss：core_fragment 0.5 / shard 0.6  
- **无** 稳定符、无装备大爆、无 afk 刷怪点

## 加载

- 游玩服短重启（`./stop.sh` + `./start.sh custom`）
- MythicMobs：成功加载 **41** 个怪物（+5）、**22** 个技能（+3）
- 仅既有 ExampleItems `GOLDEN_HELMET` WARN；新 yml 无报错
- `ops.json` = `[]`

## 后续

- 插件：DP 三波 `$kill` Display 对齐本表明文
- Boss 数值上线后 bot 调（目标 TTK 45～75s）
