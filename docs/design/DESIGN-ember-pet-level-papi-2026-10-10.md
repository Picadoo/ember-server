# 余烬 · 使魔等级 PAPI 同屏（主题 B · 先证插件缺口）

STATUS=**已批 A · 批 M · D384 · 插件补键中 · 菜单挂键另号** · 2026-10-10 · tip [`STATUS-ember-next-hard-debt-pet-level-papi-need-design-2026-10-10.md`](../status/STATUS-ember-next-hard-debt-pet-level-papi-need-design-2026-10-10.md)（已关）· backlog `B-pet-level-papi` · STATUS [`STATUS-ember-pet-level-papi-d384-2026-10-10.md`](../status/STATUS-ember-pet-level-papi-d384-2026-10-10.md) · 总控 D383 后点名主题 B

> **一句话玩家价值：** 使魔页一眼看见当前等级 / 满级与否 / 下一级魂尘——投喂有反馈，养成可感；**不**靠聊天刷屏，也**不**靠菜单假写固定字。

> **批 A ≠ 施工 ≠ 关观察 ≠ 抬日表 ≠ 开 R ≠ 开 K3 ≠ 改 ×0.97 ≠ 同号写 Java ≠ 改 feed 公式。**

---

## 0. 证据表（缺口证明）

| # | 来源 | 口径 |
|---|------|------|
| E1 | `PlayerData` ~453–509 | `petsUnlocked` · `activePet` · `petLevels`；`getPetLevel(id)` 默认 **1**；`setPetLevel` | **有**持久化等级 |
| E2 | `PetService` + `pet.yml` | `feed.enabled` · `item=mat_ember_soul_dust` · `max_level=10` · `cost_base=1` · `cost_per_level=1` · `power_per_level=1`；`feedCostFor(L)=base+(L-1)*per`；`getPowerBonus` = base + (Lv−1)*per | **有**养成与展示战力公式（本窗**只读不改**） |
| E3 | `PetService.cmdList` / `cmdFeed` | 聊天输出 `Lv.N`、投喂后 `Lv.A→B` | 反馈**仅聊天**；菜单未镜像 |
| E4 | `CorePapi.java` | 段路由：ACCOUNT / CASH / KIT / PROGRESS / GATE / P1 / STAMINA；键集**无** `pet` / `pet_*` | **无**使魔段 |
| E5 | `CoreRpgExpansion` | `onPlaceholderRequest` → `CorePapi.route`；**无** pet 分支 | **无**已注册 `%corerpg_pet_*%` |
| E6 | `CoreRpgPlugin.getPetService()` | 服务已装、可 reload/cmd；**未**接到 Expansion | 数据可达，占位未暴露 |
| E7 | `plugins/TrMenu/menus/ember_pet.yml` | Layout `# F # / # L E S #`；F→生活兑尘、L=`pet list`、E=summon/dismiss、S=`pet feed`；**零** `%corerpg_…%` | 同屏**无**等级/进度行 |
| E8 | 邻域旁注 | D378/D380/D381/D383 DESIGN 均写「无 pet level PAPI → 另号」 | 本号=主题 B **升主**；**≠**复述互指主交付 |
| E9 | 总控派单 | 先证缺口；有键→菜单挂；无键→插件补键规格+菜单草案、施工拆号；禁假写固定字 | 分叉边界 |

### 0.1 PAPI 有/无结论（钉死）

| 项 | 结论 |
|----|------|
| **已有键（使魔）** | **无** — 全仓 `CorePapi*` / Expansion **未**注册任何 `%corerpg_pet_*%` / `%corerpg_*pet*level*%` |
| **已有相关（非使魔）** | `%corerpg_ember_level%` 等 = **角色**等级，**≠**使魔；禁止挪用冒充 |
| **数据层** | **有** — `getPetLevel` / active / unlocked / feedCost |
| **分叉** | → **无键路径**：荐 M = 插件补键规格 + 菜单同屏草案；施工 **拆号**（插件岗 → 菜单岗） |

**一句话问题：** 养成数字在 PlayerData，菜单看不到 → 缺 PAPI 桥。

**为何不是只改 lore 假字 / 抬日表 / 改 feed：** 假字=假平面；抬表/改公式硬禁；总控要求先证缺口再方案。

---

## 1. 玩家感知目标（≤3）

1. **一眼等级：** 选定/出战使魔 `Lv.N / max`（或满级态）。  
2. **一眼下一级成本：** 未满时「下一级需要魂尘×C」；满级「已满级」。  
3. **投喂后同屏可变：** 菜单重开或 TrMenu 刷新后数字更新（依赖真 Placeholder，非写死）。

---

## 2. 方案表

| 方案 | 内容 | 评价 |
|------|------|------|
| **A · 仅菜单假写固定字** | lore 写「Lv.? / 看聊天」或写死「最高 10」无个人进度 | **否决** — 假平面；总控明示否决 |
| **M · 插件补键规格 + 菜单同屏草案（荐）** | docs 钉 Placeholder 名/含义 + `ember_pet` 哪格挂键；施工拆号：①插件另号注册键 ②菜单另号（或同批串行）挂 `%…%` | **荐** — 对齐无键证据；批 A ≠ 同号 Java |
| **L · 改 feed 公式 / 抬挂机加尘 / 新空跳转 / 开 R** | 动 `cost_*`/`max_level`/`power_per_level`，或 afk 产魂尘，或再塞跳转格 | **否决** — 硬禁；本债=展示 |

**批注勾选意向：** ✅ **M**（§2.1 键规格必做 · §2.2 菜单草案必做 · 施工拆号）。**否决 A、L。**

---

## 3. 可落地（方案 M）§2

### 2.1 Placeholder 规格（插件另号 · 本号只定名）

**Identifier：** 沿用既有 `%corerpg_…%`（`CoreRpgExpansion`），**新建**轻量段或挂 ACCOUNT 旁路由（实现细节交插件岗；**不得**占用 `p1_` / `gate_` 前缀语义）。

设计钉死键名（最少集 · 只读 · 禁改养成公式）：

| Placeholder | 含义（玩家可见） | 真源 | 空/无宠时 |
|-------------|------------------|------|-----------|
| `%corerpg_pet_active_id%` | 选定使魔 id（内部） | `data.getActivePet()` | `""` 或 `-` |
| `%corerpg_pet_active_name%` | 选定使魔显示名 | `PetDef.display` | `未选定` |
| `%corerpg_pet_level%` | 选定使魔当前等级 | `getPetLevel(active)`；无 active→0 或 `-` | 约定：**无宠=`0`**，菜单用条件/文案兜底 |
| `%corerpg_pet_max_level%` | 配置上限 | `feed.max_level`（现网 10） | 恒有 |
| `%corerpg_pet_level_line%` | 一行聚合：`Lv.N/10` 或 `已满级 Lv.10` | level + max | 无宠：`尚无使魔` |
| `%corerpg_pet_feed_cost%` | 当前→下一级魂尘成本；满级=`0` | `feedCostFor(level)`；满级短切 | 无宠=`0` |
| `%corerpg_pet_feed_hint%` | 一行：`下一级需要魂尘×C` / `已满级` / `先解锁使魔` | 上列组合 | 见左 |
| `%corerpg_pet_power_bonus%` | 当前展示战力加值（可选） | `getPowerBonus(data)` | `0` |
| `%corerpg_pet_unlocked_count%` | 已解锁只数（可选） | `petsUnlocked.size()` | `0` |

**可选（有余力再加，不挡 M 主验收）：**

| Placeholder | 含义 |
|-------------|------|
| `%corerpg_pet_spawned%` | 实体是否在场：`出战中` / `未出战`（读 `activeEntities`） |
| `%corerpg_pet_level_<id>%` | 指定 id 等级（图鉴墙用；本窗菜单可不挂） |

**实现约束（写给插件岗）：**

- 只读 `PlayerData` / `PetService` 已有 getter；**禁止**本债改 `feed.*` YAML 数值或升级曲线。  
- 主线程安全：与既有 Expansion 一致；TrMenu 异步求值时禁止碰世界实体（`spawned` 若做，须与 loadout 同类 dirty/主线程约定，否则本窗可砍掉 `pet_spawned`）。  
- 单测：`CorePapi.route` 增段或键集断言；无 Player 时返回约定空串。  
- **批 A 本号 ≠ 写/合并 Java**；插件施工另号（总控派插件岗）。

### 2.2 菜单同屏草案（`ember_pet.yml` · 插件键就绪后）

| 窗 | 做什么 | 不动 |
|----|--------|------|
| **W1a · 战况/状态格（必做）** | 增一格或复用空位（荐键名 **I** 或扩 E 上方空行）：name `§a使魔状态`；lore 挂 `%corerpg_pet_level_line%` · `%corerpg_pet_feed_hint%` · 可选 `%corerpg_pet_active_name%` / `%corerpg_pet_power_bonus%` | **不**改 F/L/E/S 既有 actions；**不**新造无目的跳转（F 兑尘已有） |
| **W1b · 投喂格 S lore（必做）** | S 格 lore 顶部 +2 行：当前 `level_line` + `feed_hint`；actions 仍 `pet feed` | 不改 feed 命令与次数逻辑 |
| **W1c · 出战格 E lore（同批）** | E 格 +1 行选定名/等级短签 | summon/dismiss 保留 |
| **W1d · Open tell（可选）** | Open +半行「本页可见等级与下一级魂尘」 | 不复述 D378 跳转主交付 |
| **W1e · 图鉴 L（可选）** | 仍可 `pet list`；若有 `unlocked_count` 可半行展示 | 不强制新图鉴墙 |

**Layout 建议（施工择一，不挤死现键）：**

```
#########
#   F   #
# L E S #   ← 现状
#   I   #   ← 可选插入状态格 I；若版面紧则 W1b/W1c 挂在 S/E 即可、可不增格
####B####
```

**禁止：** 在插件键**未**上线前，把 `%corerpg_pet_*%` 写进 live 菜单冒充已通（预合可进 staged 片段，live 跟插件号）。

### 2.3 施工拆号（钉死）

| 号 | 岗 | 交付 | 依赖 |
|----|-----|------|------|
| **P0** | 本号（策划） | tip+DESIGN 待批 A | — |
| **P1** | 插件岗（另号） | 注册 §2.1 最少集；单测；发 jar | 批 A·M |
| **P2** | 菜单岗（另号或 P1 后串行） | `ember_pet` W1a–W1c；TrMenu reload 验收 | P1 键在 live 可解析 |
| **本号不做** | — | 不改 Java、不改 live YAML、不 stage 脏 runtime | — |

### 2.4 与 Stage2 / 邻域

| 项 | 口径 |
|----|------|
| 观察 | **≠关观察**；**≠改 ×0.97 / set_bonus**；**≠开 K3 live** |
| D373–D378 | 出战/魂尘诚实/生活互指 **保留**；本债**只**加等级同屏，不重做跳转 |
| D383 | 挂机层钩 **另一主题**；本债不夹带 afk |
| 样本 R / Pack6 / 天赋灰印 | **≠开** |
| 养成数值 | **禁**改 `feed.cost_*` / `max_level` / `power_per_level` / NI 价表，除非总控另批经济号 |

### 2.5 验收（施工号用 · 本号只定规格）

| ID | 步骤 | 预期 |
|----|------|------|
| V1 | `/papi parse me %corerpg_pet_level_line%`（有宠账号） | 非字面量；形如 `Lv.N/10` |
| V2 | 打开 `ember_pet` | 可见等级行 + 下一级魂尘提示（或满级态） |
| V3 | 投喂 1 次后重开菜单 | 等级行变化（或满级） |
| V4 | 无宠号 | 不崩；显示「尚无使魔」类兜底 |
| V5 | `pet.yml` feed 数值 | 与批前一致（本债零改公式） |
| V6 | 全文 | **无**假写固定个人 Lv；**无**挂机产魂尘暗示；**无**复述 D373–D383 为新主交付 |

**不跑 p1sim**（零经济/挂机 RES 变更）。

---

## 4. 本窗不做（硬禁）

- 抬 `daily_kills` / afk.tiers / 挂机加 `soul_dust`  
- 开样本 R / Pack6 / 天赋·灰印 HOLD 重开  
- 关观察 / 改 ×0.97 / set_bonus / 开 K3 live  
- 菜单假写个人等级固定字（方案 A）  
- 改 `feed.*` 成本/上限/战力公式（除非另批）  
- 空跳转格 / 复述 D373–D383 互指·日更·挂机层钩主交付  
- **同号写/合并 Java 或改 live YAML**；stage 脏 runtime（ladder/calamity-state/p1-six 等）

---

## 5. 邻域边界

| 邻域 | 边界 |
|------|------|
| D373 出战+魂尘诚实 | **保留**；本债补同屏数字 |
| D376/D378 生活↔使魔 | **保留**；F 兑尘不重做 |
| D383 挂机层钩 | **不夹带** |
| 角色 `%corerpg_ember_level%` | **禁止**当使魔等级用 |
| K3 / 六槽观察运维 | **不夹带** |

---

## 6. 批注区（总控填）

- [x] **批 A · 方案 M**（§2.1 键规格 + §2.2 菜单草案；施工拆号 P1→P2）· tip `@a869c892` · **D384 docs 占位 · 插件补键中 · 菜单挂键另号**  
- [x] 否决 A（菜单假写）、L（改公式/抬表/空跳转）  
- [x] 批 A ≠ 关观察 ≠ 抬日表 ≠ 开 R ≠ 开 K3 ≠ 同号写 Java（本号 docs-only）  

### 总控批注（D384）

- **已批 A · 批 M** · 本号 **docs-only** 落字；**插件补键中**（P1 另号）；**菜单挂键另号**（P2，键就绪后）。
- **≠本号写 Java** · **≠改 live 菜单** · **≠改 feed 公式** · **≠关观察** · **≠抬日表** · **≠开 R** · **≠开 K3** · **≠改 ×0.97 / 三开关 / bv** · **≠假写固定 Lv** · **≠复述 D373–D383**。

### 变更记录
| 日 | 事 |
|----|-----|
| 2026-10-10 | 策划 · DESIGN 待批 A·荐 M · 先证 **无** PAPI · 数据层有等级 · tip `a869c892` |
| 2026-10-10 | 总控批 A·M · **D384** docs 占位 · tip 关 · backlog→已批·插件施工中 · 插件补键中 · 菜单挂键另号 · **≠本号写 Java ≠改菜单 ≠关观察** |

---

*使魔等级 PAPI 同屏 · 已批 A·M · D384 docs 占位 · 插件补键中 · 菜单挂键另号 · ≠本号写 Java ≠关观察 ≠抬日表。*
