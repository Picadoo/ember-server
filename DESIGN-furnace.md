# 熔炉封闭冶炼链设计（NeigeItems + CoreSmelt）

## 目标

- **清空**全部原版 `FurnaceRecipe`（启动时从 `Bukkit.recipeIterator()` 移除）
- 熔炉冶炼**只**使用 NeigeItems 自定义物品（输入与结果均为 NI ID）
- 原版铁矿等不再能产出有效冶炼结果（配方已清空；若材质重合则由 `FurnaceSmeltEvent` 拒绝非 NI 输入）

## 架构

| 组件 | 路径 | 职责 |
|------|------|------|
| NeigeItems | `plugins/NeigeItems-1.21.151.jar` | 自定义物品定义与 API |
| NI 物品配置 | `plugins/NeigeItems/Items/ember-furnace.yml` | YAML 物品（`Items/` 目录，本版本布局） |
| CoreSmelt | `plugins/CoreSmelt.jar`（源码 `CoreSmelt/`） | 删原版配方 + 按 `config.yml` 注册 NI 配方 |
| Paper | `paper/paper-1.12.2-1620.jar` | 1.12.2 测试服 |
| 运行时 | `server-runtime/` | eula / properties / start.sh / stop.sh |

### NeigeItems API（Java 调用）

Kotlin object，从 Java 用静态单例：

```java
pers.neige.neigeitems.manager.ItemManager.INSTANCE.hasItem(id);
pers.neige.neigeitems.manager.ItemManager.INSTANCE.getItemStack(id);
pers.neige.neigeitems.manager.ItemManager.INSTANCE.isNiItem(stack); // -> ItemInfo / null
info.getId();
```

### 1.12.2 限制

- `FurnaceRecipe` **按 Material（+ data）匹配输入**，无法仅靠 NBT 区分同材质的原版/自定义。
- 本链矿石基底使用 `IRON_ORE` / `GOLD_ORE` / `COAL_ORE`（产品要求）；清空原版后注册的配方会让「同材质任意物品」进入熔炉槽，因此 **CoreSmelt 在 `FurnaceSmeltEvent` 中取消非对应 NI ID 的输入**。
- 1.12.2 `FurnaceRecipe` **无 cookTime API**（仅有 `setExperience`）。**HYBRID 已实现**：`cook_time_ticks` / `result_amount` 由 Paper `FurnaceNmsHooks` + CoreSmelt 在 NMS 层强制生效（见 `DESIGN-hybrid.md`）。须使用 `paper-custom.jar`。

## 物品表（NI ID = snake_case）

| NI ID | 显示名 | Material (1.12.2) | 角色 |
|-------|--------|-------------------|------|
| `ore_ember_iron` | 余烬铁矿 | `IRON_ORE` | 输入 |
| `ore_ember_gold` | 余烬金矿 | `GOLD_ORE` | 输入 |
| `ore_ember_coal` | 余烬煤矿 | `COAL_ORE` | 输入 |
| `ingot_ember_iron` | 余烬铁锭 | `IRON_INGOT` | 输出 |
| `ingot_ember_gold` | 余烬金锭 | `GOLD_INGOT` | 输出 |
| `crystal_ember_carbon` | 余烬碳晶 | `COAL` | 输出（煤矿冶炼） |
| `raw_ember_flesh` | 生余烬肉 | `RAW_BEEF` | 输入（食物线） |
| `steak_ember` | 余烬牛排 | `COOKED_BEEF` | 输出 |

## 熔炉配方（CoreSmelt `config.yml`）

| input_ni_id | result_ni_id | cook_time_ticks | exp |
|-------------|--------------|-----------------|-----|
| ore_ember_iron | ingot_ember_iron | 100 (amount 2) | 0.7 |
| ore_ember_gold | ingot_ember_gold | 300 (amount 1) | 1.0 |
| ore_ember_coal | crystal_ember_carbon | 150 (amount 3) | 0.1 |
| raw_ember_flesh | steak_ember | 80 (amount 1) | 0.35 |

## 验证

1. 日志：`NeigeItems` enabled；`CoreSmelt` 报告 `removed N furnace recipes`（N>0）与 `registered M custom NI recipes`。
2. `/coresmelt check`：炉配方数量、链是否齐全、IRON_ORE 原版是否可冶炼。
3. Mineflayer：`cd mineflayer-tests && npm run smoke` 连 `localhost:25565` 后退出。

## 测试给物

```
/ni give <player> ore_ember_iron 16
/ni give <player> ore_ember_gold 16
/ni give <player> ore_ember_coal 16
/ni give <player> raw_ember_flesh 16
```

## NI 版本说明

`NeigeItems-1.21.151` 的 `plugin.yml` 含 `api-version: 1.13`，在 Paper 1.12.2 上实测可正常 enable（未知键被忽略），并已下载 1.12.2 语言包。若日后硬失败，可换 GitHub 显式支持 1.12.2 的旧 release。
