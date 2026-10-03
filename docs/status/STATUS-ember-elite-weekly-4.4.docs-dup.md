# STATUS · EmberEliteWeekly 精英试炼（阶段 4.4 插件岗）

- 时间：2026-09-27（Asia/Shanghai）
- 执行：余烬-插件
- 依据：`docs/design/design-stage4-elite-weekly.md`；票 NI / 五怪 MM 已由物品/怪物岗就绪
- 未改：Paper NMS、深渊看守·深、afk_caps/挂机、git commit/push
- `ops.json` = `[]`（测后保持空）
- 稳定符：仅周首通箱（`corerpg elite weekly-first` / lootWeekMarks），**不**进 MM 掉落

## 1. 变更文件

### DungeonPlus
| 路径 | 说明 |
|------|------|
| `plugins/DungeonPlus/dungeon/EmberEliteWeekly/option.yml` | 新建：门控/扣票/进本文案/COMPLETE 奖励脚本 |
| `plugins/DungeonPlus/dungeon/EmberEliteWeekly/monster.yml` | 新建：三波 MM + kill-any Display + 波末治疗 |
| `plugins/DungeonPlus/dungeon/EmberEliteWeekly/task/timeout.yml` | 新建：720s 超时 FAILURE |
| `plugins/DungeonPlus/dungeon/EmberEliteWeekly/obstacle.yml` | 自周本复制空壳 |
| `plugins/DungeonPlus/config.yml` | `ember_weekly` 地图绑定增加 `EmberEliteWeekly: 1` |

### CoreRpg（源码 + 资源 + 运行目录 + jar）
| 路径 | 说明 |
|------|------|
| `CoreRpg/.../EliteService.java` | **新建** start / weekly-first / gate / status |
| `CoreRpg/.../TicketGrantService.java` | 周一发精英票；持有硬顶 1 |
| `CoreRpg/.../PlayerData.java` + `PlayerDataStore.java` | `eliteTicketGrantWeekId` |
| `CoreRpg/.../ProgressService.java` | `elite_weekly` 周幂等标记 `elite_weekly_clear`；默认 gate elite=40 |
| `CoreRpg/.../LootService.java` | 支持 `pick_one` 列表 |
| `CoreRpg/.../CoreRpgExpansion.java` | `%corerpg_gate_elite%` = Lv+未通关+有票 |
| `CoreRpg/.../CoreRpgPlugin.java` / `CashService.java` | 接线 `elite` 子命令与补发检测 |
| `*/cash.yml` `*/progress.yml` `*/loot.yml` | elite 周票 / XP 源 / elite_gem + elite_weekly_first |
| `CoreRpg` → **1.15.0** · `plugins/CoreRpg.jar`（与 server-runtime 同 inode） |

### TrMenu
| 路径 | 说明 |
|------|------|
| `plugins/TrMenu/menus/ember_hub.yml` | 周常旁「§e精英试炼」一点 `corerpg elite start`；锻炉/生活 lore 去命令 |
| `plugins/TrMenu/menus/ember_forge.yml` | 说明 lore 改玩家话术 |
| `plugins/TrMenu/menus/ember_disassemble.yml` | 去「§8命令：/corerpg …」 |

## 2. 门控规则

1. 余烬 Lv ≥ **40**（`progress.yml` `level_gates.elite`）
2. 本周未通关（`lootWeekMarks` 含 `elite_weekly_clear=<weekId>` 则拒）
3. 背包持有 `ticket_ember_elite` ≥ 1（显示名「余烬精英票」；DP 进本扣 1）
4. OP / `corerpg.admin`：`corerpg elite start` 可绕校验；DP 条件仍为 `gate\|\|op`，扣票条件仍要票（管理测前 `/ni give`）
5. Placeholder：`%corerpg_gate_elite%` → `yes`/`no`（供 DP / 菜单）

失败短句（`corerpg elite start`）：
- 等级：`§c精英试炼需要余烬 Lv.40（当前 Lv.x）`
- 已通关：`§c本周已通关精英试炼，下周再来`
- 无票：`§c缺少余烬精英票（每周一发放 1 张，持有上限 1，进本即扣）`

## 3. 周票发放

- `cash.yml` → `elite.free_tickets: 1` / `ticket_ni_id: ticket_ember_elite` / `hard_cap: 1`
- 与周本同节奏：ISO 周（Asia/Shanghai，周一换周）；登录 `TicketGrantService.grantOnJoin` 发「精英票×1」
- **不**进商城日票池；背包已有 ≥1 则只记本周已发、不再叠票

## 4. DP 波次 / 奖励要点

| 波 | 怪 | 条件 | 开场 |
|----|----|------|------|
| wave1 | Zombie×3 + Skeleton×2 | kill-any 炽尸/骨刺 ×5 | `§e—— 试炼一：词缀苏醒 ——` |
| wave2 | Brute×1 + Mix×2 | kill-any 蛮纹/混纹 ×3 | `§c—— 试炼二：蛮压词缀 ——` |
| wave3 | Boss×1 | kill 烬纹执行官 ×1 | `§4—— 试炼终：烬纹执行官 ——` → COMPLETE |

- 波末 Instant Health III；超时 720s；人数 1～2；地图 `ember_weekly`
- COMPLETE：核心碎片×4 / 碎片×12 / 骨尘×6 / 附魔晶×1 → `loot elite_gem` → `elite weekly-first` → `progress elite_weekly` → `quest event elite_weekly_clear` → `mvtp ember_hub`
- progress：余烬 XP **80** + 战令 XP **15**（战令受日顶；通关标记防同周双领）

## 5. 重载 / 重启

1. **游玩服短重启**（推荐）：新 DP 地牢目录 + CoreRpg 1.15.0 jar  
   `server-runtime`：`./stop.sh` → `./start.sh custom`（或全栈 `scripts/ember-down.sh` / `ember-up.sh`）
2. 仅配置热更不足：DP 新地牢、新 class 需重启；TrMenu 可 `trmenu reload`；NI 票已就绪则不必再 reload
3. 测后确认 `ops.json` = `[]`

## 6. 验收命令（测试岗）

```
# 门控
/corerpg elite status
# 无票 / Lv<40 / 已通关 → 短句拒绝（菜单点「精英试炼」同）

# 发票（管理）
/ni give <玩家> ticket_ember_elite 1

# 进本（玩家入口，勿教玩家打 dp）
/corerpg elite start
# 或 /ember → 精英试炼

# 通关后检查
# 箱物料 + 随机孔石一枚 + 稳定符×1（本周首次）
# /corerpg level · 战令经验
# 再点进本 → 「本周已通关」
# /papi parse me %corerpg_gate_elite%  → no

# 周票（换周或清 eliteTicketGrantWeekId 后重登）
# 应见 [门票] … 精英票×1；持有已 1 时不叠

# 管理
/dp start EmberEliteWeekly   # 仅管理/测试
/corerpg elite weekly-first <玩家>  # 幂等
```

测后：`ops.json` 必须仍为 `[]`。

## 7. 已知缺口 / 风险

- 地图为周本占位，非独立美化三室；刷点已落开放房间（对齐窒息教训）
- Boss 数值初值（HP 2800 / Dmg 4），TTK 需 bot 再调（怪物岗备注）
- OP 无票时 DP 扣票条件仍会拒（与周本一致）；测前给票
- 主线 ch9 `elite_weekly_clear` 依赖 quest 卷已接线；本岗只保证 event 命令可触发
- 未改日常/周常菜单里仍存在的 `/dp start` lore（仅按任务清锻炉/分解 + 新按钮）

## 8. 平衡收口

- 平衡收口：**CLOSED / PASS（4.4f）**；收口文档：[docs 路径](STATUS-ember-elite-weekly-4.4-close.md) / [仓库根路径](STATUS-ember-elite-weekly-4.4-close.md)
