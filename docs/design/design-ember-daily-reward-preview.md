# 设计稿 · 日常软债：奖励预览真分页

> **债源：** `docs/status/STATUS-ember-daily-ux-closeout-review.md` §可改 #3（软尾巴）；backlog「日常软债：奖励预览真分页」。  
> **对齐：** `docs/handoff/TEMPLATE-ember-design-handoff.md`；父入口 `plugins/TrMenu/menus/ember_daily.yml`。  
> **已批准方案 1（总控）：** 本额度由插件新建 `ember_daily_rewards` + 改 P actions。不动 DP/MM/体力。  
> **日期：** 2026-09-28（Asia/Shanghai） · 岗：余烬服策划执行助手

---

## 问题（一句话）

日常选线页图标 **P「奖励预览」** 只有悬停 lore、**无 actions**，占一格死按钮；玩家点了无反馈，也无法按「通关箱 / Boss 材料 / 装备机会」看清 NI 与概率语义。

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 / 功能名 | 日常奖励预览真分页（TrMenu 子菜单） |
| 负责人 / 日期 | 余烬-策划 / 2026-09-28（Asia/Shanghai） |
| 关联世界 / 地图 | 无新地图；入口挂在现有 `ember_daily`（门吏·灰钥 / hub 日常） |
| 目标验收里程碑 | 日常软债收口（不挡「日常体验收口」主宣称） |
| 当前状态 | **已批准 · 方案 1**（总控 2026-09-28；本额度插件改 TrMenu） |
| 关联实现 / STATUS | `docs/status/STATUS-ember-daily-ux-closeout-review.md` §可改3；`docs/status/STATUS-ember-daily-boss-loot-align.md`；`docs/design/design-ember-content-backlog.md` 软债行 |

**约束（硬）：** TrMenu + NI ID（文案可写展示名）；玩家 **零 slash**；不改体力 / 进本 / 掉落表数值；不改 Citizens；专岗施工仅 TrMenu。

---

## 1. 现网证据与奖励结构摘要

### 1.1 P 死按钮证据

`plugins/TrMenu/menus/ember_daily.yml` 图标 **P**：

- `material: chest` · `name: '§6奖励预览'`
- lore 已写：通关箱材料（各线相同）+ Boss 掉落摘要 +「各线均有概率：刃/符/T1」
- **无 `actions:` 块**（对照同页 **R** 体力说明有 `tell`；**B** 有 `menu: ember_hub`；七线键有 `corerpg enter`）
- Layout 占位：`# G P R #` —— 点 P = 无声无反馈

对照历史：`docs/status/STATUS-ember-daily-s1-s3-player-review.md` 时 P 曾是空壳 `tell`；现网已升级 lore，但仍是死按钮（挑刺 §可改3）。

### 1.2 通关箱：七线同池（共用）

七份 `DungeonPlus/dungeon/EmberDaily*/option.yml` 的 `dungeon-reward-script` **内容一致**（diff≈0）：

| 发放 | NI / 命令 | 数量 | 语义 |
|------|-----------|------|------|
| 核心碎片 | `mat_ember_core_fragment`（展示名：余烬核心碎片） | ×1 | 通关箱必给 |
| 附魔晶 | `crystal_ember_enchant`（余烬附魔晶） | ×1 | 通关箱必给 |
| 碎片 | `mat_ember_shard`（余烬碎片） | ×5 | 通关箱必给 |
| 原版经验 | `xp 3L` | 3 级 | 通关箱附带 |
| 进度 | `corerpg progress … daily_clear` | — | 战令/等级事件；预览可一句带过，不装作「物品」 |

**预览结论：** 通关箱 **不必按线分页**；一区展示即可。

### 1.3 Boss：装备机会七线对齐；风味材料分线

依据现网 MM `EmberDaily*.yml` Boss `~onDeath`（`docs/status/STATUS-ember-daily-boss-loot-align.md` 已对齐装备三行）：

| 类别 | NI | 展示名 | 概率语义（只描述，不改数值） | 范围 |
|------|-----|--------|------------------------------|------|
| 必掉 | `mat_ember_core_fragment` | 余烬核心碎片 | 必掉 ×1 | 七线 Boss |
| 进度感 | `corerpg mmxp … elite` | （精英击杀经验） | 必触发 | 七线 Boss |
| 装备机会 | `gear_ember_blade` | 余烬之刃 | **0.12** | 七线相同 |
| 装备机会 | `gear_ember_charm` | 余烬护符 | **0.08** | 七线相同 |
| 装备机会 | `gear_ember_t1_blade` | 余烬精炼之刃 | **0.03** | 七线相同 |
| 风味 | `mat_ember_shard` | 余烬碎片 | **0.5** | 焦骨 / 潮蚀 / 霜晶（庭院 Boss **无**此行） |
| 风味 | `mat_ember_bone_dust` | 余烬骨尘 | **0.5** | 地窖 / 断塔 / 锈轨（庭院 Boss **无**此行） |

庭院 Boss（`EmberDailyBrute`）仅：核心×1 + 刃/符/T1 三概率 + mmxp；**无** 0.5 风味行。

小怪掉落（尸→碎片、骷→骨尘）属过程掉，**不进预览主区**（避免页肥；可选 lore 一句「清怪另有碎片/骨尘」）。

**预览结论：** 装备机会与通关箱均可总览；风味用 **一行说明 + lore 列线名** 即可，**不必七线×七页**。

---

## 2. 方案对比与推荐

| | **方案 1 · 真预览子菜单** | **方案 2 · 轻量 click tell** |
|--|---------------------------|------------------------------|
| 玩家体验 | 点 P → 新页分栏看 NI/概率 → 返回选线 | 点 P → chat 再念一遍 lore |
| 相对现网 | 死按钮 → 可点真页 | 死按钮 → 有反馈但仍弱 |
| 施工量 | 新建 1 个 TrMenu + 改 P 的 `actions` | 仅改 P 加 `tell`（+可选 sound） |
| 与债源对齐 | 直接还「真分页」软债 | 半还；挑刺仍可骂「占格假预览」 |
| 过肥风险 | **低**（见 §3：一页总览足够） | 无 |
| 零指令 | `menu:` 跳转 / 返回 | tell 即可 |

### **明确推荐：方案 1 · 真预览子菜单**

**理由：**

1. 债名即「真分页」；方案 2 只是把死按钮改成嘲讽式 chat，不匹配 backlog 表述。  
2. 调研显示通关箱同池、装备三行同概率 → **一页三类分区即可**，七线×三类不会过肥。  
3. 现网已有 `menu:` 跳转先例（`ember_hub`→`ember_daily` / `ember_afk`；`ember_forge`→`ember_enhance`/`ember_socket`），返回写 `menu: ember_daily` 即可，玩家零 slash。  
4. 方案 2 仅当真页被判定过肥时的退路；**本债不构成过肥**。

---

## 3. 过肥评估与推荐布局

### 3.1 评估

| 布局候选 | 菜单数 | 判定 |
|----------|--------|------|
| 七线各一页预览 | 7 + 入口改动大 | **过肥**；内容 90% 重复（箱同、装同） |
| 一页总览 · 三类分区 | **1**（`ember_daily_rewards`） | **推荐**；风味用 lore 列线 |
| 三类各一页 | 3 | 可做但多余；一类材料撑不满页 |

**结论：真页不过肥。** 用 **单页总览**，不要分线分页。

### 3.2 菜单 id 与跳转

| 项 | 定稿 |
|----|------|
| 新菜单 id | **`ember_daily_rewards`** |
| 文件 | `plugins/TrMenu/menus/ember_daily_rewards.yml`（批准后新建） |
| 入口 | `ember_daily` 图标 **P**：`sound` + `menu: ember_daily_rewards`（可保留短 lore 作悬停摘要） |
| 返回 | 预览页底栏 **B**：`menu: ember_daily`（**不要**直接回 `ember_hub`，避免打断选线动线） |
| 玩家路径 | 门吏/hub → `ember_daily` → 点 P → 预览 → 返回 → 继续选线进本 |

### 3.3 推荐 Layout（槽位示意）

6 行 × 9 列（与日常页同高，认知一致）：

```
#########
#   H   #     H = 页头说明（chest / book）
# A C I #     通关箱三件：核心碎片 / 附魔晶 / 碎片×5（+ lore 写 xp3L）
# D E F #     Boss 材料：核心必掉 / 风味总览 / 精英经验说明
# X Y Z #     装备机会：刃 12% / 符 8% / T1 3%
####B####     B = 返回日常选线（键勿与通关箱冲突）
```

| 槽 | 角色 | 展示意图（文案宜短） | 点击 |
|----|------|----------------------|------|
| H | 页头 | `§6日常奖励预览` · lore：`通关箱各线相同 · Boss 装掉各线同概率 · 风味小料分线` | 无 / 可选短 tell |
| A | 通关箱 | NI `mat_ember_core_fragment` · `§e余烬核心碎片 §f×1` · `§8通关箱 · 各线相同` | 无（纯展示） |
| C | 通关箱 | `crystal_ember_enchant` · `§d余烬附魔晶 §f×1` | 无 |
| I | 通关箱 | `mat_ember_shard` · `§c余烬碎片 §f×5` · lore 附 `原版经验 +3 级` | 无 |
| D | Boss 必掉 | `mat_ember_core_fragment` · `§eBoss 核心碎片 §f×1（必掉）` | 无 |
| E | Boss 风味 | `§7风味小料 §f50%` · lore 列：`焦骨/潮蚀/霜晶→碎片`；`地窖/断塔/锈轨→骨尘`；`庭院无此行` | 无 |
| F | Boss 经验 | `§b精英击杀经验` · `§8通关进度另计 daily_clear` | 无 |
| X | 装备 | `gear_ember_blade` · `§c余烬之刃` · `§e约 12%` · `§8各线 Boss 相同` | 无 |
| Y | 装备 | `gear_ember_charm` · `§e余烬护符` · `§e约 8%` | 无 |
| Z | 装备 | `gear_ember_t1_blade` · `§b余烬精炼之刃` · `§e约 3%` | 无 |
| B | 返回 | `§7返回日常选线` | `menu: ember_daily` |

**文案原则：** 玩家可见用展示名；lore 可附 `§8` NI id 一行（对齐服内 NI lore 习惯）；概率写「约 12%」等口语，与现网 `0.12` 语义一致，**禁止改数值**。

**图标材质建议（非 NI 实装亦可）：** 材料用 `nether star` / `glowstone dust` / `sulfur` 等常见 1.12 材质占位；装备用 `iron sword` / `gold nugget` / `diamond sword`。若插件岗支持 TrMenu 直接挂 NI 展示则更佳，**非硬条**。

### 3.4 `ember_daily` P 改动要点（批准后）

保留现有悬停摘要 lore（方便不点开的玩家），**补 actions**：

```yaml
  P:
    display:
      material: chest
      name: '§6奖励预览'
      lore:
        - ''
        - '§7通关箱材料（各线相同）…'   # 可保留现网短摘要
        - ''
        - '§e➥ §f点击打开预览页'
    actions:
      all:
        - 'sound: BLOCK_CHEST_OPEN-1-1'
        - 'menu: ember_daily_rewards'
```

---

## 4. 站岗表（功能锚）

本功能为 **既有日常入口下的菜单子页**，不新开场景 NPC。

| 功能锚 / 用途 | NPC id | 显示名 | 位置 | 右键菜单 | 副交互 | 验收 / 备注 |
|---|---|---|---|---|---|---|
| 日常选线（既有） | `ember_dungeon_clerk` | 门吏 · 灰钥 | `ember_hub` (-14.5,58,110.5) | `ember_daily` | 无 | 已验收；本债不改 |
| 奖励预览子页 | —（菜单内） | — | — | 自 `ember_daily` P → **`ember_daily_rewards`** | 返回 `ember_daily` | **仅 UI**；无新场景锚 |

**状态上限：** 落地后标 **「UI 可用」**；不得宣称新「场景完成」（无新锚）。

### 动线（玩家）

```
门吏·灰钥 / hub「日常」 → ember_daily
  → 点 P「奖励预览」→ ember_daily_rewards（看三类）
  → 点返回 → ember_daily → 选线进本
```

失败/取消：关菜单即可；预览页 **禁止** 绑定 `corerpg enter`（避免误进本）。

---

## 5. UX 否决条

| 硬条 | PASS / FAIL | 证据或债务说明 |
|---|---|---|
| **零手打指令** | **PASS**（设计） | 全程 `menu:` / 既有选线；不教 `/dp` `/corerpg` `/hub` |
| **自定义物均为 NI ID** | **PASS**（设计） | 展示与逻辑身份用上表 NI id；展示名仅文案 |
| **功能锚可点** | **PASS（UI）** | 无新场景锚；挂在已验收灰钥 / hub 日常下；状态上限 UI 可用 |
| **实景地图** | **N/A** | 纯菜单债，不涉地图 |
| **换皮不算视觉 PASS** | **N/A** | 不涉地图视觉 |
| **占位有名字与里程碑** | **PASS** | 菜单 id `ember_daily_rewards` 已命名；里程碑=本软债批准后插件施工 |

---

## 6. 专岗与不动清单

### 专岗

| 岗 | 职责 |
|----|------|
| **余烬-插件** | 批准后：新建 `ember_daily_rewards.yml`；改 `ember_daily.yml` 图标 P 的 actions / 可选 lore 末行「点击打开」；reload TrMenu；**不**动 DP/MM |
| 余烬-策划 | 本稿；批准前不落 YAML |
| 余烬-测试 | 验收硬条（§7） |
| 物品 / 怪物 / 体力 | **不派工** |

### 不动清单

- 体力数值 / 日切 / cost 30 / 上限  
- `corerpg enter` 进本逻辑与冷却数值  
- 七线 `dungeon-reward-script` 与 MM Boss / 小怪掉落 **任何概率与数量**  
- Citizens（禁用政策不变）；Adyeshach 门吏配置  
- 通关箱「同池」、Boss「装掉同价」设计结论  

---

## 7. 验收硬条（插件落地后）

1. 打开 `ember_daily`，点 **P** → 打开 **`ember_daily_rewards`**（有声即可）；**不是**无反馈。  
2. 预览页可见三类分区：通关箱（核心×1 / 附魔晶×1 / 碎片×5 + xp 说明）、Boss 材料（核心必掉 + 风味分线说明）、装备机会（刃/符/T1 与约 12%/8%/3%）。  
3. 文案含对应 **NI id 或展示名**；概率语义与现网一致，**抽样核对**不得改表。  
4. 点返回 → 回到 **`ember_daily`**（仍可继续选线）；**不**误 `enter`。  
5. 玩家路径 **无** 要求手打 slash。  
6. `git diff` / 配置 diff：**仅** TrMenu 上述文件；无 DP/MM/CoreRpg 体力/掉落改动。

---

## 8. 方案 2（退路，不推荐）

仅当总控认定「多一页菜单过重」时启用：

- P 保留 lore，加 `tell:` 把三类摘要打到 chat（可参考 R 体力说明模式）  
- **不**新建菜单  
- 交卷须改口：软债降为「有反馈的死按钮」，**不宣称真分页已还**

本稿 **不采用** 方案 2。

---

## 9. 交卷表

| 项 | 结论 |
|----|------|
| **推荐方案** | **方案 1 · 真预览子菜单**（单页总览，不过肥） |
| **菜单 id** | **`ember_daily_rewards`**；返回 **`ember_daily`** |
| **是否建议本额度施工 YAML** | **建议批准后本额度施工**（专岗：插件改 TrMenu；仅新建预览页 + 改 P 的 actions；工作量小、直接还软债） |
| **未批准前** | **零 YAML 改动**（本稿仅文档） |
| **状态** | 设计稿 **未完成 / 待批准**；落地后最高标 **UI 可用** |

### 关键路径列表

| 路径 | 用途 |
|------|------|
| `docs/design/design-ember-daily-reward-preview.md` | **本稿** |
| `docs/status/STATUS-ember-daily-ux-closeout-review.md` | 债源 §可改3 |
| `docs/handoff/TEMPLATE-ember-design-handoff.md` | 交稿模板 |
| `docs/status/STATUS-ember-daily-boss-loot-align.md` | Boss 装掉对齐证据 |
| `docs/design/design-ember-content-backlog.md` | backlog 软债行 |
| `plugins/TrMenu/menus/ember_daily.yml` | 现网 P 死按钮 + 批准后入口改动 |
| `plugins/TrMenu/menus/ember_daily_rewards.yml` | **批准后新建**（尚不存在） |
| `plugins/TrMenu/menus/ember_forge.yml` / `ember_afk.yml` / `ember_hub.yml` | `menu:` 跳转先例 |
| `plugins/DungeonPlus/dungeon/EmberDaily*/option.yml` | 通关箱同池 |
| `plugins/MythicMobs/Mobs/EmberDaily*.yml` | Boss 分线风味 + 装掉概率 |
| `plugins/NeigeItems/Items/ember-dungeon.yml` / `ember-furnace.yml` / `ember-gear-t1.yml` | NI 展示名 |

---

## 10. 交付结论

- [x] 问题、现网证据、奖励结构（箱同 / Boss 分线）已写清  
- [x] 方案 1 vs 2 对比并 **明确推荐方案 1**  
- [x] 真页：id、布局槽位、返回、专岗、验收硬条、不动清单、交卷表齐全  
- [x] UX：零指令 / NI id / 无新场景锚（UI 上限）已自检  
- [x] **已批准方案 1**；本额度插件施工 TrMenu  

**最终结论：** 设计调研完成，**推荐真预览页 `ember_daily_rewards`**；建议批准后由插件本额度改 TrMenu。当前稿件状态：**已批准 · 方案 1（施工中）**。

---

## 总控批注（2026-09-28）

**批准方案 1 · 真预览子菜单。** 本额度施工：仅 TrMenu（新建 `ember_daily_rewards.yml` + `ember_daily` P 加 actions）。

**布局键修正：** 通关箱三槽改为 **A / C / I**（勿再用 B 占通关箱，底栏 **B** 专用于返回 `ember_daily`）。

不动体力 / 进本 / DP / MM 掉落数值。落地后最高标 **UI 可用**。
