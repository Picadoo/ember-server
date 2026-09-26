# Paper 1.12.2 核心改造挂钩点（scaffolding）

目标机制：**酿造台配方、工作台配方、熔炉配方、钓鱼**。  
自定义物品：**仅**通过 NeigeItems 插件（`/workspace/minecraft/plugins/`），不要在核心里新增物品定义。

构建完成后，可改源码树大致位于：

- `Paper/Paper-Server/`（NMS + CraftBukkit 应用补丁后）
- 或先在 `work/CraftBukkit/nms-patches/` / `Spigot-Server-Patches/` 加补丁，再 `./paper jar` / `./paper rb`

以下类名均为 **1.12.2 NMS / CraftBukkit**（`net.minecraft.server` / `org.bukkit.craftbukkit`）。

---

## 1. 酿造台（Brewing）

| 层 | 类 / 文件 | 作用 |
|----|-----------|------|
| NMS | `TileEntityBrewingStand` | 酿造逻辑、配方匹配、耗时 |
| NMS | `ContainerBrewingStand` | 容器槽位 / 交互 |
| Craft | `org.bukkit.craftbukkit.block.CraftBrewingStand` | Bukkit 方块 API |
| Craft | `org.bukkit.craftbukkit.inventory.CraftInventoryBrewer` | 酿造库存 |
| Craft | `org.bukkit.craftbukkit.potion.CraftPotionBrewer` | 药水酿造注册 |
| 已有 CB 补丁 | `work/CraftBukkit/nms-patches/TileEntityBrewingStand.patch` | 上游已有改动，新补丁需叠在此上 |
| 已有 CB 补丁 | `work/CraftBukkit/nms-patches/ContainerBrewingStand.patch` | 同上 |

**挂钩建议**：在 `TileEntityBrewingStand` 的配方解析处扩展（自定义 ingredient → result），结果物品可由 NeigeItems 在运行时注入/替换，核心只认 Material/ItemStack 规则。

相关 Paper 事件补丁（女巫药水，非酿造台）：`Witch*PotionEvent` — 勿与酿造台混淆。

---

## 2. 工作台合成（Crafting table）

| 层 | 类 / 文件 | 作用 |
|----|-----------|------|
| NMS | `CraftingManager` | 合成配方注册与匹配 |
| NMS | `ShapedRecipes` / `ShapelessRecipes` | 有序 / 无序配方 |
| NMS | `IRecipe` | 配方接口 |
| Craft | `CraftShapedRecipe` / `CraftShapelessRecipe` / `CraftRecipe` | Bukkit 配方桥接 |
| Bukkit API | `org.bukkit.inventory.ShapedRecipe` 等 + `Bukkit.addRecipe` | 插件侧也可注册（但核心改动走 NMS） |
| 已有 CB 补丁 | `ShapedRecipes.patch`, `ShapelessRecipes.patch`, `IRecipe.patch` | |
| Paper | `Spigot-Server-Patches/0312-Ignore-Missing-Recipes-in-RecipeBook-to-avoid-data-e.patch` | RecipeBook 容错 |

**挂钩建议**：扩展 `CraftingManager` 注册表，或在匹配结果处插入自定义规则；产物留给 NeigeItems 物品 ID/NBT。

---

## 3. 熔炉（Furnace）

| 层 | 类 / 文件 | 作用 |
|----|-----------|------|
| NMS | `RecipesFurnace` | 熔炼配方（输入→输出、经验） |
| NMS | `TileEntityFurnace` | 烧炼 tick、燃料、进度 |
| NMS | `ContainerFurnace` / `SlotFurnaceResult` | 容器与结果槽 |
| Craft | `CraftFurnaceRecipe` / `CraftInventoryFurnace` / `CraftFurnace` | Bukkit 桥接 |
| 已有 CB 补丁 | `RecipesFurnace.patch`, `TileEntityFurnace.patch`, `ContainerFurnace.patch`, `SlotFurnaceResult.patch` | |
| Paper | `0074-Fix-Furnace-cook-time-bug.patch` | 烹饪时间修复 |

**挂钩建议**：改 `RecipesFurnace` 的配方 map；烧炼速度/燃料可动 `TileEntityFurnace`。

---

## 4. 钓鱼（Fishing）

| 层 | 类 / 文件 | 作用 |
|----|-----------|------|
| NMS | `EntityFishingHook` | 浮漂实体、咬钩判定、战利品抽取 |
| NMS | `ItemFishingRod` | 钓竿使用 |
| Craft | `CraftFish`（若指鱼实体）/ FishingHook Bukkit 实体 | API 侧 |
| 已有 CB 补丁 | `EntityFishingHook.patch`, `ItemFishingRod.patch` | |
| Paper | `0012-Configurable-fishing-time-ranges.patch` | 可配置等待时间 |
| Paper | `0175-Don-t-let-fishinghooks-use-portals.patch` | |
| Paper | `0158-Remove-FishingHook-reference-on-Craft-Entity-removal.patch` | |
| Paper | `0309-Configurable-Alternative-LootPool-Luck-Formula.patch` | 幸运与战利品池 |

**挂钩建议**：在 `EntityFishingHook` 战利品/咬钩逻辑处改机制；掉落物可用 NeigeItems 自定义物品。时间范围可先复用 Paper 配置补丁思路。

---

## 5. 与 NeigeItems 的边界

| 职责 | 位置 |
|------|------|
| 物品定义、NBT、给物命令 | NeigeItems（`/workspace/minecraft/plugins/`） |
| 配方能否成功、耗时、钓鱼概率/机制 | Paper 核心补丁 |
| 测试 | `server-runtime` 加载 Paper + 插件；`mineflayer-tests` 冒烟 |

**不要**在本阶段编写具体配方内容；仅保留挂钩文档与运行脚手架。

---

## 6. 后续补丁工作流（构建成功后）

1. `cd /workspace/minecraft/Paper && ./paper jar`（或已生成后改 `Paper-Server`）
2. 修改对应 NMS/Craft 类后：`./paper rb`（rebuild patches）或按 README 流程
3. 产出 jar → `server-runtime/start.sh rebuilt`
4. 确认 `/workspace/minecraft/plugins/` 内有 NeigeItems jar
