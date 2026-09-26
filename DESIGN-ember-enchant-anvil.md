# 余烬附魔 · 铁砧 — 成长闭环设计

**日期：** 2026-09-11（Asia/Shanghai）  
**栈：** Paper 自定义 jar + CoreEnchant / CoreAnvil / CoreCraft + NeigeItems + 余烬地窟掉落  
**原则：** 不改方块；不叠更多 Paper 钩子；用现有 YAML/NI 把材料花出去。

技术底座见 `DESIGN-enchant-anvil.md`（已通）。本文只定**玩法数值与配置落地**，把演示档换成可养成。

---

## 目标

1. 地窟材料（碎片 / 骨尘 / 核心碎片）有明确去处：合成附魔晶、修装备。
2. 附魔台只服务余烬 NI 装备，拒绝原版铁镐乱附。
3. 铁砧禁止附魔书合并（已开）；修理改为 NI 材料专属。
4. 属性强化（AttributePlus）下一轮再做；本轮只保证附魔/修理闭环可测。

---

## 物品与产线

### 已有（地窟）

| NI ID | 名称 | 来源 |
|-------|------|------|
| `mat_ember_shard` | 余烬碎片 | EmberCryptZombie |
| `mat_ember_bone_dust` | 余烬骨尘 | EmberCryptSkeleton |
| `mat_ember_core_fragment` | 余烬核心碎片 | EmberCryptBrute |
| `gear_ember_blade` | 余烬之刃 | 地窟稀有掉落 |
| `gear_ember_charm` | 余烬护符 | 地窟稀有掉落 |
| `crystal_ember_enchant` | 余烬附魔晶 | 已有；现当附魔台催化剂 |

### 新增（本轮）

| NI ID | 名称 | 用途 |
|-------|------|------|
| `crystal_ember_enchant_mid` | 余烬附魔晶·中阶 | 中档附魔台消耗（可选；见下「简化方案」） |
| `crystal_ember_enchant_high` | 余烬附魔晶·高阶 | 高档附魔台消耗（可选） |
| `mat_ember_repair_kit` | 余烬修补粉 | 铁砧修理专用（可选；可用碎片直修） |

**简化方案（推荐先做）：** 仍只用一种 `crystal_ember_enchant`，靠**消耗数量 + 等级表**分档；不新增晶种。修补直接吃碎片，不新增修补粉。中/高阶晶留给二期。

---

## 合成（CoreCraft + NI）

关闭玩家直接 `/ni give` 以外的「白嫖晶」路径：附魔晶只能合成。

| 产物 | 配方（示意 shapeless） | 数量 |
|------|------------------------|------|
| `crystal_ember_enchant` ×1 | `mat_ember_shard` ×3 + `mat_ember_bone_dust` ×1 | 1 |
| `crystal_ember_enchant` ×2 | `mat_ember_core_fragment` ×1 + `mat_ember_shard` ×2 | 2（稀有材料更赚） |

说明：

- 物品 bot 写 NI 配方或 CoreCraft YAML（跟现有 `ember-craft` 风格一致）。
- 工作台已 wipe 原版；只注册上述 NI→NI。
- 管理员调试仍可用 `/ni give`。

---

## 附魔台（CoreEnchant）

### 开关

```yaml
override_vanilla: true
require_ni_item: true
whitelist_ni_ids:
  - gear_ember_blade
  - gear_ember_charm
  # 日后加护甲/武器只往这里加
catalyst_ni_id: crystal_ember_enchant
reject_vanilla_lapis: true
bookshelf_cost_bump_cap: 2   # 书架略有用，但仍便宜可控
```

### 分档表（按装备 material 或 seed；先按装备分）

**刃（IRON_SWORD / gear_ember_blade）** — 偏输出：

| 槽 | 附魔 | 等级 | XP | 晶消耗 |
|----|------|------|----|--------|
| 0 | DAMAGE_ALL（锋利） | 1 | 3 | 1 |
| 1 | DAMAGE_ALL | 2 | 8 | 2 |
| 2 | FIRE_ASPECT 或 LOOT_BONUS_MOBS | 1 | 12 | 3 |

**护符（GOLD_NUGGET / gear_ember_charm）** — 偏生存（能挂的附魔有限，用保护类 + 耐久）：

| 槽 | 附魔 | 等级 | XP | 晶消耗 |
|----|------|------|----|--------|
| 0 | DURABILITY | 1 | 3 | 1 |
| 1 | DURABILITY | 2 | 8 | 2 |
| 2 | PROTECTION_ENVIRONMENTAL | 1 | 10 | 3 |

实现：用现有 `offer_tables` 的 `materials:` 过滤；`IRON_SWORD` / `GOLD_NUGGET` 各一张表。白名单卡住 NI id，避免原版同材质蹭表。

### 不做（本轮）

- 自定义附魔（需要另插件/NMS）
- 附魔书台（铁砧已 ban 合并）
- 书架硬性门槛（只做 cost bump）

---

## 铁砧（CoreAnvil）

### 政策（相对演示档升级）

```yaml
enabled: true
rename_cost: 5
max_level_cost: 30
prior_work_factor: 2.0
block_enchant_combines: true
material_repair_only: true
require_ni_repair_ingredient: true
repair_ingredient_ni_ids:
  - mat_ember_shard          # 刃/护符通用小修
  - mat_ember_core_fragment  # 大修 / 高消耗
material_repair_extra_cost: 3
```

规则说明（玩家可读）：

- 只能改名 + 用余烬材料修耐久；**不能**用铁锭/附魔书合。
- 小修：`mat_ember_shard`（多颗换耐久，费用略高）。
- 大修：`mat_ember_core_fragment`（一颗顶多，适合蛮兵掉的稀有料）。
- 原版修理材料全部失效（`require_ni_repair_ingredient: true`）。

若 NMS 钩子对「任意 NI id 当修理料」已支持（config 已有字段），插件 bot 只改 YAML + 热重载；不够再补钩子（尽量不碰）。

---

## 经济粗算（调参用）

假设僵尸掉 1 碎片/击杀、骷髅 1 骨尘、蛮兵 1 核心：

- 合成 1 晶 ≈ 3 碎片 + 1 骨尘 → 约 3～4 次小怪
- 刃槽 2（锋利 II）≈ 2 晶 + 8 级 → 约 6～8 次小怪 + 一点打怪经验（服已关经验球时靠任务/指令补，或临时开少量 XP 来源——**另议**）
- 提醒：当前服 **经验球已关**。附魔仍扣等级。需要么：
  - A）保留扣级，另做「余烬经验瓶」NI（酿造/地窟）；或
  - B）把 `cost` 全压到 0～1，几乎不吃等级，只吃晶

**推荐本轮：B（cost 保持表内小数字，书架 bump≤2），避免先开经验系统。** 经验瓶放到属性养成同一轮。

---

## 分工

| 角色 | 活 |
|------|----|
| 总控（本 bot） | 定稿本文；验收标准；派工 |
| 余烬-物品 | NI 配方/lore；晶合成；必要时修补粉 |
| 余烬-插件 | CoreEnchant / CoreAnvil / CoreCraft YAML；热重载；缺能力再改插件 |
| 余烬-Paper | **默认不动**；仅当铁砧 NI 修理料钩子不够用时介入 |
| 余烬-怪物 | 不动掉落（已单路径 ni give） |
| 余烬-测试 | 合成晶 → 白名单附魔 → 非白名单拒绝 → 碎片修刃 → 铁锭修失败 |

---

## 验收（PASS 条件）

1. 原版铁剑放附魔台：无有效报价 / 被拒。
2. `gear_ember_blade` + `crystal_ember_enchant`：出现刃表三槽；拿走后扣晶、附魔与显示一致。
3. 工作台：碎片+骨尘 → 附魔晶；无配方材料失败。
4. 铁砧：刃 + 碎片可修；刃 + 铁锭失败；附魔书合并失败。
5. `/coreenchant check`、`/coreanvil check` 与配置一致。
6. 不影响熔炉/酿造/地窟掉落（抽测一次僵尸仍只给一份碎片）。

---

## 落地顺序

1. 改 CoreEnchant YAML（白名单 + materials 分表 + 数值）
2. 改 CoreAnvil YAML（NI 修理料）
3. 物品：附魔晶合成配方
4. 测试 bot 按验收跑一遍
5. 通过后写 `STATUS-ember-enchant-anvil.md`

---

## 下一轮（不在本文）

- AttributePlus 起服卡死（datafixerupper）+ 属性强化石
- 中/高阶附魔晶分种
- 更多地窟装备进白名单
- 余烬经验瓶（若以后要拉开等级差）
