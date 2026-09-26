# STATUS · EmberRaid 通道→终厅 + 同袍套装

**日期：** 2026-09-13 17:10 Asia/Shanghai (UTC+8)  
**规格：** `docs/ember-raid-channel-spec.md` · `DESIGN-ember-raid.md`  
**约束：** 未改 Paper/方块；未改 EmberAbyss DP。CoreRpg **1.4.7** 已接 raidRingWeek + SetService。

## 结论

| 项 | 状态 |
|----|------|
| 四波骨架 | **LIVE** 保留 wave1→2→3→boss |
| 左右道播报 + 刷点错开 | **LIVE** |
| 通关取消必给 T3 | **LIVE** |
| 周首通团戒 | **LIVE** 通关箱 `corerpg raid grant-ring <player>` |
| 同袍 2 件套 戒+刃 | **LIVE** `/corerpg set` 背包刃+戒即激活 |
| 击杀回能代码 | **LIVE** EntityDeathEvent → Saturation I 2s |
| raidRingWeek 真去重 | **LIVE** PlayerData+Store；同周再 grant 拒 |

## 波次（与周本不再换皮）

| 组 | 播报 | 刷点 |
|----|------|------|
| wave1 | `§9【左道】卫兵压上——左边清！` | Footman **-48**,65,275 ×6 |
| wave2 | `§9【右道】射手就位——右边先压远程！` | 卫兵左 **-48** ×3 · 射手右 **-32** ×4 |
| wave3 | `§d【汇合】通道精英挡住终厅门！` | Elite 中央 + 添头 |
| boss | `§4【终厅】使徒！分摊砸击，别叠脸！` | EmberRaidBoss |

进本：`§9团本已点燃。§7分路推进——左卫兵、右射手，汇合后再闯终厅。`  
通关：`§a使徒倒下。§9同袍之证已结算。`

## 通关箱

| NI | 数量 |
|----|------|
| `mat_ember_core_fragment` | 4 |
| `crystal_ember_enchant` | 2 |
| `mat_ember_shard` | 10 |
| `mat_ember_bone_dust` | 6 |
| `gem_ember_sharp` | 1（孔石二选一取锐） |
| **`acc_ember_raid_ring`** | **1（周首通保底）** |
| T3 刃 | **已取消必给** |
| `mat_calamity_ember` | **已移出**（灾厄主产） |

终厅 MM：T2 刃/符 15%/12%；T3 刃/符 **8%/6%**。Boss **不再** onDeath 发戒。

## 套装选择

**余烬同袍 = `acc_ember_raid_ring` + 任意 `gear_ember_*blade`（T0～T3）**  
背包持有即计件。护符线不做本期最小实现。

## 路径

| 路径 | 说明 |
|------|------|
| `plugins/DungeonPlus/dungeon/EmberRaid/option.yml` | 进本播报 / 通关箱 |
| `plugins/DungeonPlus/dungeon/EmberRaid/monster.yml` | 左右道+终厅 |
| `plugins/DungeonPlus/dungeon/EmberRaid/task/timeout.yml` | 2400s（未改） |
| `plugins/MythicMobs/Mobs/EmberRaid.yml` | 终厅 T2/T3 概率；去戒 |
| `plugins/NeigeItems/Items/ember-gear-t1.yml` | **`acc_ember_raid_ring`** |
| `plugins/TrMenu/menus/ember_raid.yml` | 菜单文案 |
| `plugins/TrMenu/menus/ember_set.yml` | 同袍说明 |
| `plugins/CoreRpg/set.yml` | 套装 YAML 旋钮（jar 未读） |
| `DESIGN-ember-raid.md` | 本刀短设计 |
| `docs/ember-raid-channel-spec.md` | 策划规格 |

## 验收对照（规格 §6）

| # | 标准 | 本期 |
|---|------|------|
| 1 | 3 人持票可进；2 人拒进 | YAML 仍 min=3 max=5（组队实开未复测） |
| 2 | 四波播报左/右/汇合/终厅 | **是** |
| 3 | 通关材料箱；不每人必掉 T3 | **是** |
| 4 | 本周首次通关得戒；同周第二次不发 | **软保底**（票周 1）；真 weekId **挂账** |
| 5 | 持戒+刃可见反馈 | **lore + 菜单**（回血代码挂账） |
| 6 | 不影响深渊撤离 / 灾厄窗 | **是**（未改那两套 DP） |

## 重载

```
/dp reload
/ni reload
/mm reload
/trmenu reload
```

测：`/ni give <玩家> ticket_ember_raid 1` → 3 人 `/dp start EmberRaid`

## 热更冒烟（2026-09-13 17:04 CST）

服未停。OpReload：`/dp reload` `/ni reload` `/mm reload` `/trmenu reload` 均成功。  
`/ni give acc_ember_raid_ring` → **余烬团本之戒**；`gear_ember_blade` → **余烬之刃**。  
单人 `/dp start EmberRaid` → `团本人数 3～5，当前 (1)`（人数门 PASS）。  
DP log：`[EmberRaid] 地牢内容导入完毕` / `初始化完毕`。

## 阻断

1. **raidRingWeek**：无 jar 字段时，admin 多发票同周 COMPLETE 会再发戒。  
2. **同袍击杀回能**：`set.yml` 未接入 CoreRpg 监听。  
3. **3～5 人实开本**：本期未组队跑图（人数门 YAML 仍在）。  
4. AttributePlus / CoreCombat **无**现成套装节点，故走 lore + TrMenu + set.yml。
