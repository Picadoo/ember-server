# DESIGN · 余烬成长曲线 + 世界拓扑 + 副本装备阶梯

**日期：** 2026-09-13（Asia/Shanghai）  
**总控决定：** 优先补副本内容/玩法/装备；公共区用 Multiverse；限次本继续走 DungeonPlus 实例。  
**约束：** 不改方块玩法；奖励走 NI/MM/DP 结算；不改 Paper。

---

## 1. 等级：有，且已有字段

| 项 | 设计 | 现状 |
|----|------|------|
| `emberLevel` | 1～60（赛季可扩 70） | 玩家 YAML 已有；新号约 **10**；天赋点按等级算 |
| 经验 `emberXp` | 升级曲线 | **未做完整经验条**（下期 CoreRpg） |
| `powerScore` | 展示战力 | LIVE（强化/天赋等汇总，不直接乘伤） |

**经验供给（目标）：**

| 来源 | 日软顶 | 作用 |
|------|--------|------|
| 挂机庭 | 有 | 稳态；越肝越稀 |
| 悬赏 | 无（次数硬顶） | 日常尖峰 |
| 日/周/深渊/团本 | 票硬顶 | 尖峰经验 + 材料 |
| 签到/活跃箱 | 有 | 保底日活 |

**等级门槛建议（进本软门，后期接线）：**

| 内容 | 建议等级 | 说明 |
|------|----------|------|
| 挂机庭 | 1+ | 无门槛 |
| 日常本 | 10+ | 新号默认即可进 |
| 周常本 | 20+ | 建议装 +3 再打 |
| 深渊 | 25+ | 冲层；低级可进但难 |
| 灾厄（公共窗） | 30+ | 世界事件 |
| 团本 | 35+ | 3～5 人协作 |
| 盟 Boss | 在盟即可 | 贡献门控已有 |

---

## 2. 五套副本的讲究（全是「特殊限次」，不是全部内容）

它们是 **PVE 尖峰阶梯**，挂机/悬赏/竞技/盟课是另一层日常。

| 本 | 次数 | 时长感 | 玩法要点 | 主产出 | 地图/世界 |
|----|------|--------|----------|--------|-----------|
| **日 EmberDaily** | 3/日 | 8～12 分 | 3 波清怪→蛮兵；1～2 人 | 核心×1、附魔晶、碎片 | DP 实例 `ember_daily` |
| **周 EmberWeekly** | 1/周 | 20～25 分 | 4 波加压；1～3 人 | 核心×3、晶×2、**保底刃** | DP `ember_weekly` |
| **深渊 EmberAbyss** | 日票 1 | 15～30 分 | 多层下行；冲层榜 | 孔石、强化材、层数 | DP `ember_abyss` |
| **灾厄 EmberCalamity** | 日窗 2～3 | ~10 分 | **公共世界事件**（全服可集） | 灾厄余烬、外观材 | **目标：MV 世界 `ember_event`**；现仍 DP stub |
| **团本 EmberRaid** | 周票 1 | 30～40 分 | 3～5 人通道→终厅 Boss | 套装饰品、称号进度、高核心 | DP `ember_raid` |

**另有：** 挂机庭（∞）、悬赏、盟 Boss、竞技（独立垫/日后 MV `ember_pvp`）。

---

## 3. 装备成长曲线（刷装主轴）

```
挂机材料 → 附魔晶/修理
     ↓
日/周本核心 → 强化 +1～+6
     ↓
深渊孔石 → 镶嵌
     ↓
周本/团本稀有装 → 词缀层 + 套装件
     ↓
灾厄变异材 → 外观/赛季外观 + 微量重铸材
```

**品质阶梯（NI 命名约定）：**

| 阶 | 前缀 | 来源 | 强化建议上限 |
|----|------|------|--------------|
| T0 入门 | `gear_ember_*` | 合成/周本保底 | +3 |
| T1 精炼 | `gear_ember_t1_*` | 日本高难/周本 | +6 |
| T2 深核 | `gear_ember_t2_*` | 深渊通关/团本 | +8 |
| T3 灾厄 | `gear_ember_t3_*` | 团本终厅/灾厄窗 | +10（需保护券） |

本期落地优先：**补 T1 刃/护符 + 团本饰品 stub**，日/周/深渊奖励表对齐上表数量。

**战力贡献权重（展示用，非伤公式）：** 强化 40% · 附魔词缀 20% · 孔石 15% · 天赋 15% · 使魔/套装 10%。

---

## 4. 世界拓扑：Multiverse vs DungeonPlus

| 世界/空间 | 插件 | 用途 |
|-----------|------|------|
| `world` | 原版 | 临时主世界；逐步瘦身为中转 |
| **`ember_hub`** | Multiverse | **主城枢纽**（菜单 NPC、商店、告示） |
| **`ember_afk`** | Multiverse | **挂机庭**（公共刷怪区，无限） |
| **`ember_event`** | Multiverse | **灾厄/世界 Boss 公共场**（窗内开放） |
| **`ember_pvp`** | Multiverse（后期） | 竞技垫独立世界 |
| `ember_daily/weekly/abyss/calamity/raid` | **DungeonPlus map** | **每人/每队实例**，不进 MV 列表当常驻世界 |

**原则：**

1. **限次副本 = DP 实例**（隔离进度、票、重置）。  
2. **公共社交/挂机/世界事件 = Multiverse 常驻世界**。  
3. 现灾厄仍是 DP stub；迁到 `ember_event` 前保持可 `/dp start EmberCalamity` 测 Boss。  
4. 已装：`Multiverse-Core` 2.5.0 + `Multiverse-Portals` 2.5.0（1.12.2）。

**创建命令（启服后）：**

```
/mv create ember_hub normal -g VoidGenerator   # 若无 void 插件则用 NORMAL 后清空
/mv create ember_afk normal
/mv create ember_event normal
/mv modify set mode adventure ember_hub
/mvtp ember_hub
```

无 Void 生成器时：先 `NORMAL`，再用 fill/WE 铺平台；禁止改玩法方块规则（仍可建造场景）。

---

## 5. 本轮落地顺序（总控）

1. ✅ 装 Multiverse-Core / Portals  
2. 写本文 + 重启加载 MV，创建 `ember_hub` / `ember_afk` / `ember_event` 并铺出生平台  
3. 策划岗：按本文补副本波次文案与装备掉落表（T1/饰品）  
4. 物品岗：NI `gear_ember_t1_*` + `acc_ember_raid_*`  
5. 怪物岗：日/周 MM level 与 Display 分层（不改方块）  
6. CoreRpg：经验条 / 进本等级软门（可稍后）

---

## 6. 相关文档

- `docs/design/DESIGN-ember-rpg-systems.md` §2～4  
- `docs/design/DESIGN-dungeon-daily-weekly.md`  
- `docs/design/DESIGN-ember-map-plan.md`  
- `docs/status/STATUS-ember-rpg-progress.md`
