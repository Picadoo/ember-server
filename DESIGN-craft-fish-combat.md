# 工作台 / 钓鱼 / 图腾 / 盾牌 设计说明

**日期：** 2026-09-09 Asia/Shanghai  
**栈：** Paper custom jar + `CraftNmsHooks` / `FishNmsHooks` / `TotemNmsHooks` / `ShieldNmsHooks`  
**附属：** CoreCraft · CoreFish · CoreCombat  
**物品：** 仅 NeigeItems（Paper 不依赖 NI）

## 原则

- 清掉大部分原版产出路径；基建保留可配置白名单。
- 底层规则进 Paper 钩子；表数据与 NI 判定进附属 YAML。
- **不改方块硬度 / 方块掉落**；村民交易与生物原版掉落禁令保持不动（CoreWorldRules）。

---

## 1. 工作台（CoreCraft）

### 行为

1. 启动后清除大部分合成配方；**保留**结果材质在基建白名单内的原版配方（默认：工作台、熔炉、箱子、木棍、木板）。
2. **保留全部熔炉配方**（交给 CoreSmelt，互不踩踏）。
3. 从 YAML 注册有序 / 无序 NI→NI 配方；用 `PrepareItemCraftEvent` / `CraftItemEvent` 校验格子里必须是对应 NI id（同材质原版物品不能糊弄过去）。
4. `CraftNmsHooks` 只记统计，方便 `/corecraft check`。

### Demo 配方

| id | 类型 | 输入 | 输出 |
|----|------|------|------|
| plate_ember_iron | shaped 2×2 | ingot_ember_iron ×4 | plate_ember_iron |
| rod_ember_iron | shaped 1×2 | ingot_ember_iron ×2 | rod_ember_iron |
| core_ember_compact | shapeless | crystal_ember_carbon + ingot_ember_gold | core_ember_compact |

命令：`/corecraft check|reload`

---

## 2. 钓鱼（CoreFish）

### 行为

1. `FishNmsHooks` 覆盖 `EntityFishingHook` 战利品抽取 → 只出 YAML 表里的 NI。
2. 分类权重：fish / treasure / junk；类内再按 weight 抽。
3. 可选 `wait_ticks_min/max`（demo 40–120）；未配则用 `paper.yml` 的 fishing-time-range。

### Demo 物品

- 鱼：`fish_ember_cod` / `fish_ember_salmon` / `fish_ember_puffer`
- 宝藏：`treasure_ember_relic` / `treasure_ember_pearl`
- 垃圾：`junk_ember_boot` / `junk_ember_bone`

命令：`/corefish check|reload`

---

## 3. 不死图腾（CoreCombat）

### 行为

1. `TotemNmsHooks.hasOverride()` 时，NMS `EntityLiving` 复活判定只接受校验器通过的物品（不再认裸原版 `TOTEM`）。
2. CoreCombat 校验：必须是配置里的 NI id（demo：`totem_ember_life`）。
3. 可选冷却（demo **15 秒**）；仍走 `EntityResurrectEvent`。

命令：`/corecombat check|reload`（别名 `/ccbt`）

---

## 4. 盾牌（CoreCombat）

### 行为

1. `ShieldNmsHooks` 挂在 `isBlocking()`：override 开启时，只有白名单 NI 盾可格挡。
2. demo：`shield_ember_guard`；原版盾无效。
3. `damageShield` 耐久损耗 × `durability_loss_multiplier`（demo **1.5**）。

---

## Give 命令速查

```
/ni give <player> plate_ember_iron 1
/ni give <player> rod_ember_iron 1
/ni give <player> core_ember_compact 1
/ni give <player> fish_ember_cod 1
/ni give <player> treasure_ember_relic 1
/ni give <player> totem_ember_life 1
/ni give <player> shield_ember_guard 1
/ni give <player> ingot_ember_iron 16
```

物品文件：`plugins/NeigeItems/Items/ember-craft-fish-combat.yml`（合成产物 / 鱼 / 图腾 / 盾）+ 既有 `ember-furnace.yml`（锭 / 碳晶等）。
