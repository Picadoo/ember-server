# 余烬 · 生活等级/次数同屏（先证插件缺口 · ≠抬日表）

STATUS=**待批 A · 荐 M** · 2026-10-10 · tip [`STATUS-ember-next-hard-debt-life-level-papi-need-design-2026-10-10.md`](../status/STATUS-ember-next-hard-debt-life-level-papi-need-design-2026-10-10.md) · backlog `B-life-level-papi` · 总控 D386 后内容真债（有趣系统 · 非空跳转 · 非又回盘）

> **一句话玩家价值：** 补给·生活页一眼看见生活等级 / 经验进度 / 今日兑尘与本周孵化剩余——钓鱼与养成有目标感；**不**关菜单刷聊天，也**不**靠 lore 假写固定 Lv。

> **批 A ≠ 施工 ≠ 关观察 ≠ 抬日表 ≠ 开 R ≠ 开 K3 ≠ 改 ×0.97 ≠ 同号写 Java ≠ 改 life 价/曲线。**

---

## 0. 证据表（缺口证明）

| # | 来源 | 口径 |
|---|------|------|
| E1 | `ember_life.yml` Icons **I** | name「生活等级 / 次数」；actions=`close` + `corerpg life` | **关菜单**才输出等级/次数 |
| E2 | 同文件 Open / 各购键 lore | Open 无等级行；G/K「每日 2 次」、H/J「需生活 Lv.4」、D「需 Lv.2」——**无**个人进度 Placeholder | 门槛可见、自我进度不可见 |
| E3 | `LifeService.lifeLevel` / `lifeXp` / `levelXp` | `level_xp: [0,20,60,150,300,600,1000,1600]` → Lv1–8；`show()` 聊天：`Lv.N（经验 x/next）` + offer 日周计数 | **数据层有**；仅 chat |
| E4 | `life.yml` offers | 魂尘 `life_level:2`·`daily:2`；孵化 `life_level:4`·`weekly:1`；熬药 Lv.2；遗物/宝珠更高（E/F 仍 stowed） | 主路径门槛钉死 |
| E5 | `CorePapi.java` | 段含 PET（D384）；键集**无** `life` / `life_*` | **无** `%corerpg_life_*%` |
| E6 | `CoreRpgExpansion` | `onPlaceholderRequest` → `CorePapi.route`；无 life 分支 | 占位未暴露 |
| E7 | D384/D385 | 使魔 `pet_*` 同屏刚落 | **范式可复用**；本债标的=**生活轨**，≠复述使魔正文 |
| E8 | 排除项实扫 | 图录 P 未入 Layout=又回盘；E/F stowed=硬禁；日更入口 LegacyGate；挂机层钩 D383 已齐 | 见 tip §1 |
| E9 | Stage2 / 硬禁 | 观察续；禁抬日表/开R/Pack6/天赋灰印/关观察/×0.97/开K3/空跳转/回盘E/F | docs-only |

### 0.1 PAPI 有/无结论（钉死）

| 项 | 结论 |
|----|------|
| **已有键（生活）** | **无** — 全仓 `CorePapi*` / Expansion **未**注册 `%corerpg_life_*%` |
| **已有相关（易混）** | `%corerpg_ember_level%` 等 = **角色**等级，**≠**生活；禁止挪用冒充 |
| **数据层** | **有** — `lifeLevel` / `lifeXp` / `levelXp` / `periodCount("life_"+id, day|week)` |
| **分叉** | → **无键路径**：荐 M = 插件补键规格 + 菜单同屏草案；施工 **拆号**（插件岗 → 菜单岗） |

**一句话问题：** 生活数字在 LifeService，菜单只能关页刷聊天 → 缺 PAPI 桥。

**为何不是只改 lore 假字 / 抬日表 / 又回盘：** 假字=假平面；抬表硬禁；图录 P / E/F 回盘=派单禁或用不上。

**为何选本债、排除其它：**  
- **≠复述 D384/D385：** 使魔键已齐；本号=生活轨同构缺口。  
- **≠复述 D376/D386：** 那些是 Layout 回盘；本号**不**再回盘，只给已在盘的 I/G/K…挂真数。  
- **≠空跳转：** 零新 A→B 菜单跳。  
- **新短本房 / 新挂机产量层：** 另点名。

---

## 1. 玩家感知目标（≤3）

1. **一眼生活等级：** `生活 Lv.N` + 经验进度（或满级态）。  
2. **一眼关键次数：** 今日魂尘兑换剩余（旧靴/碎骨合计或分列）· 本周孵化剩余（可选）。  
3. **不关菜单：** I 格改为同屏展示（可保留右键/次要「完整列表」聊天，但主语义不再强制 close）。

---

## 2. 方案表

| 方案 | 内容 | 评价 |
|------|------|------|
| **A · 仅菜单假写固定字** | lore 写「最高 Lv.8」或「看聊天」无个人进度 | **否决** — 假平面；对齐 D384 否决 A |
| **M · 插件补键规格 + 菜单同屏草案（荐）** | docs 钉 Placeholder 名/含义 + `ember_life` 哪格挂键；施工拆号：①插件另号注册键 ②菜单另号挂 `%…%` | **荐** — 对齐无键证据；批 A ≠ 同号 Java |
| **L · 改 level_xp / 抬 daily·weekly / 回盘 E/F / 开 R** | 动曲线或次数顶，或回盘旧轨 | **否决** — 硬禁；本债=展示 |

**批注勾选意向：** ✅ **M**（§2.1 键规格必做 · §2.2 菜单草案必做 · 施工拆号）。**否决 A、L。**

**批 A = 采纳方案 M。批 A ≠ 施工 ≠ 关观察 ≠ 抬日表 ≠ 开 R ≠ 开 K3 ≠ 同号写 Java。**

---

## 3. 可落地（方案 M）§2

### 2.1 Placeholder 规格（插件另号 · 本号只定名）

**Identifier：** 沿用 `%corerpg_…%`（`CoreRpgExpansion`），**新建** `life_*` 只读段（实现可仿 `CorePapiPet`；**不得**占用 `p1_` / `pet_` / `gate_` 语义）。

设计钉死键名（最少集 · 只读 · 禁改生活经济）：

| Placeholder | 含义（玩家可见） | 真源 | 空/异常 |
|-------------|------------------|------|---------|
| `%corerpg_life_level%` | 当前生活等级 | `LifeService.lifeLevel(data)` | 恒 ≥1（现网） |
| `%corerpg_life_xp%` | 累计生活经验 | `lifeXp(data)` | `0` |
| `%corerpg_life_xp_next%` | 升到下一级所需累计 xp；满级=`0` | `levelXp[lv]`（与 `show()` 一致：下标=`lv`）；满级短切 | 满级 `0` |
| `%corerpg_life_level_line%` | 一行：`生活 Lv.N（经验 x/next）` 或 `生活 Lv.N（已满级）` | 上列组合 | 恒有 |
| `%corerpg_life_soul_daily_left%` | 今日魂尘兑换**剩余次数**（旧靴+碎骨池：各 daily:2 → 展示「还可兑几次」需约定） | 见下「计数约定」 | `0` |
| `%corerpg_life_soul_daily_line%` | 一行：`今日兑尘 已用 a/4 · 剩 b`（若按两线合计顶 4）或分列短签 | 上列 | 见约定 |
| `%corerpg_life_hatch_weekly_left%` | 本周孵化剩余（灰灵+烬火各 weekly:1 → 合计或分列） | `periodCount` | `0` |
| `%corerpg_life_hatch_weekly_line%` | 一行：`本周孵化剩…` | 上列 | 见约定 |

**计数约定（钉死，避免施工歧义）：**

| 键族 | 约定 |
|------|------|
| 魂尘日顶 | `soul_dust` 与 `soul_dust_bone` **各** `daily: 2`（现网）。荐 **合计展示**：顶=4，已用=`count(soul_dust)+count(soul_dust_bone)`，剩=`max(0,4-已用)`。菜单 lore 可另注「旧靴/碎骨各最多 2 次」。 |
| 孵化周顶 | `pet_ashling` / `pet_cinder` **各** `weekly: 1`。荐 **分列或合计顶=2**；施工选一并在 STATUS 写死。本 DESIGN 默认：**合计顶 2**，与魂尘同构。 |
| 副手周顶 | `offhand_*` weekly:1 **可选**键（`life_offhand_weekly_line`）；不挡 M 主验收。 |

**可选（有余力再加，不挡 M）：**

| Placeholder | 含义 |
|-------------|------|
| `%corerpg_life_unlock_hint%` | 未达 Lv.2：「再钓/烤升到 Lv.2 可兑魂尘」；已达：「可兑魂尘 / 可孵化看周顶」 |
| `%corerpg_life_offer_left_<id>%` | 单 offer 剩余（通用）；菜单可不挂 |

**实现约束（写给插件岗）：**

- 只读 `LifeService` / `PlayerData.periodCount` / `life.yml` 已有字段；**禁止**本债改 `level_xp` / `daily` / `weekly` / 价 / give。  
- 主线程安全：与既有 Expansion 一致。  
- 单测：增 `life_*` 键集断言；无 Player 返回约定空/0。  
- **批 A 本号 ≠ 写/合并 Java**；插件施工另号。

### 2.2 菜单同屏草案（`ember_life.yml` · 插件键就绪后）

| 窗 | 做什么 | 不动 |
|----|--------|------|
| **W1a · I 格改同屏（必做）** | I：去掉强制 `close` 主路径；lore 挂 `%corerpg_life_level_line%` · `%corerpg_life_soul_daily_line%` · `%corerpg_life_hatch_weekly_line%`（或等价）；actions 可改为仅 `sound` / 可选 `tell` 短确认，**或**左键刷新感知、右键才 `corerpg life` 完整列表 | **不**改 A–D/G/K/H/J/O/W 购键命令；**不**回盘 E/F/V |
| **W1b · G/K 半行（同批荐）** | 旧靴/碎骨兑尘 lore 顶 +1 行今日剩次（或共用 `soul_daily_line`） | 不改 `life buy` 与 daily 数值 |
| **W1c · H/J 半行（可选）** | 孵化格 +本周剩 / 需 Lv.4 时用 `life_level` 条件灰态（若 TrMenu 条件可写） | 不改价·weekly |
| **W1d · Open 半行（可选）** | Open +「本页可见生活等级与兑尘次数」 | 不教裸指令；不复述 D376/D386 回盘主交付 |
| **W1e · hub 旁轨半指（可选）** | 若「今日可追」已写钓鱼兑尘，可加「生活页可见等级」；不重开互指长扫 | 零数值 |

### 2.3 施工拆号

| 号 | 岗 | 交付 | 门闩 |
|----|----|------|------|
| **P1** | 插件岗（另号） | 注册 `life_*`；装 play；单测 | 键可被 Expansion 解析 |
| **P2** | 菜单岗（另号或 P1 后串行） | `ember_life` W1a–W1b；TrMenu reload 验收 | P1 键在 live 可解析 |
| **本号不做** | — | 不改 Java、不改 live YAML、不 stage 脏 runtime | — |

### 2.4 验收（落字 / 施工后薄抽）

| ID | 步骤 | 预期 | 中止 |
|----|------|------|------|
| G1 | tip/DESIGN 入库 | 含无键结论 + 荐 M + 否决 A/L | 缺任一项或默示关窗/抬表 |
| G2 | 明文 | 批 A ≠ 施工 ≠ 关观察 ≠ 抬日表；禁假写固定 Lv | 默示同号改 Java/价表 |
| G3 | P1 后 | `%corerpg_life_level_line%` 等非空且随钓鱼/兑换变化 | 键 404 或写死字 |
| G4 | P2 后 | 打开生活页 **不**必关菜单即可读等级与兑尘剩次；购键仍真命令 | I 仍强制 close 且无 PAPI |

**绿出口（本 docs 号）：** G1–G2 PASS。  
**施工绿出口：** G3–G4 PASS（另号）。  
**回滚：** 还原 docs；施工号可关键/回菜单。

### 2.5 岗位

| 角色 | 职责 |
|------|------|
| 策划 | 本 DESIGN 待批 A；维护键名与计数约定 |
| 总控 | 批 A（荐 M）；派 P1/P2 |
| 插件 | P1 补键（批 A 后） |
| 菜单 | P2 挂键（P1 后） |

---

## 4. 本窗不做（硬禁）

- 关观察 / 勾 §2.4 / 开闸 / 开 K3 / 开样本 R / 改 ×0.97 / set_bonus  
- 抬 `daily_kills` / afk.tiers / 改 `life.yml` 价·daily·weekly·`level_xp`  
- Pack6 / 天赋 / 灰印 HOLD 重开  
- 空跳转 / 又回盘 E/F/V / 图录 P 回盘（另号若总控点名）  
- 复述 D373–D386 主交付正文当本号交付  
- 菜单假写固定「Lv.8」冒充个人进度  
- 同号写 Java / stage 脏 runtime  

---

## 5. 不动与否决

| 项 | 状态 |
|----|------|
| live ×0.97 / set_bonus / 三开关 / bv / afk.tiers / daily_kills | **不动** |
| `life.yml` 经济与曲线 | **不动** |
| 开 R / 关观察 / 开 K3 | **否决** |
| 方案 A 假写 / 方案 L 抬顶回盘 E/F | **否决** |

---

## 6. 批注栏

- [ ] **批 A：采纳方案 M（插件补 `life_*` 规格 + 菜单同屏草案 · 施工拆号）** → **≠关观察 ≠抬日表 ≠开 R ≠开 K3 ≠同号 Java**
- [ ] 升级 L（改曲线/抬顶/回盘 E/F · 不荐）
- [ ] 否决 / 改派（须点名带证据新主题，如新短本房）

**策划荐批：** **批 A · 方案 M**。

---

## 变更记录

| 日期 | 事件 | 谁 |
|------|------|-----|
| 2026-10-10 | 初稿 · 待批 A · 荐 M · D386 后内容真债 · 生活同屏（类比使魔轨但标的不同） | 策划执行手 |

---

*STATUS=待批 A·荐 M · B-life-level-papi · ≠关观察 · ≠抬日表 · ≠复述 D373–D386。*
