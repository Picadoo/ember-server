# 设计稿 · 踏步少开菜单热键（B-flex-4）

> **STATUS：已批 A（总控 · 2026-09-29 04:35 Asia/Shanghai）。** 本稿只定「装配余烬踏步后，战斗里少开菜单也能释放」的**额外释放入口**（热键/快捷愿望）。  
> **本窗 commit 只写 docs；玩法 / skills.yml / TrMenu YAML 零改。**  
> **禁**动踏步 CD/数值、誓约三主动、位移+保命双上、四件甲、锻炉重做、体力日周门；禁长测/挑刺；**勿宣称 B0.1 已清**。  
> disassemble 管理注释、精英壳 **不捆**本窗。  
> 对齐挑刺 #2 soft（只读 · 勿开挑刺流程）。前序 B2.45 close `7dc96c9`（PASS · 插件 `3066746` · 测 `d6768d9`）。

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | 踏步 · **少开菜单热键**（B-flex-4） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-29 04:32 Asia/Shanghai |
| 关联 | TrMenu `ember_flex_skill.yml` / `ember_hub.yml` · CoreRpg `FlexSkillService` / `skills.yml` · 挑刺 `STATUS-ember-flex-trilogy-picky.md` #2 |
| 状态 | **已批 A** · 交插件 CoreRpg + 可选 TrMenu lore |
| tip 路径 | `docs/design-ember-flex-step-hotkey.md` |
| 上游 | B-flex-2 PASS · B2.45 close `7dc96c9` · 挑刺 tip `43bf843` 域 |

### 硬约束（本稿）

| 可动（**仅若批 A 且另开施工**） | 不可动（硬禁 · 含本稿 commit） |
|--------------------------------|--------------------------------|
| CoreRpg `FlexSkillService` 薄加 Listener（代理键 → `cast`）+ `registerEvents` | `skills.yml` `flex_ember_step` 的 `cooldown_seconds` / `distance` / 粒子音效 |
| TrMenu 轻技页 / hub「轻技」**玩家 lore** 半行提示热键（可选薄） | 誓约三主动 id/数值/CD；位移+保命双上；四件甲；锻炉/`part.yml`/`forge.yml` |
| （可选）config 开关 `flex.hotkey` 便于关 | 体力日周门；长测/挑刺；git push；宣称 B0.1 已清；未批改玩法 YAML |

---

## 1. 问题一句话

**战斗里要点菜单（或先开 hub）再放踏步，钝——已装配轻技后仍缺一条「少开菜单」的释放口。**

---

## 2. 对齐挑刺 #2 soft（只读摘要 · 勿扩 scope）

摘自 `docs/STATUS-ember-flex-trilogy-picky.md`（2026-09-29 · **无挡级**）：

- hub「轻技」vs「技能」双入口，不跟誓约打架——干脆好玩。
- 开阔地 5 格短移一眼到位；零体力不肝。
- **刺：** 战斗里要点菜单再放，略钝（**愿望热键，勿技能大改**）。
- 软债 / 愿望 #2：**踏步战斗释放少开菜单**（热键/快捷愿望；勿技能大改）。

**本窗只做释放入口愿望；踏步 CD/距离与誓约主动本窗禁碰。**

---

## 3. 现网证据（只读扫网 · 本稿未改）

### 3.1 玩家当前释放路径（几乎肯定菜单）

| 步骤 | 现网 | 证据 |
|------|------|------|
| 装配 | hub「轻技」→ `ember_flex_skill` →「装配 · 余烬踏步」→ `corerpg flex equip flex_ember_step` | `plugins/TrMenu/menus/ember_flex_skill.yml` L39–56 |
| 菜单释放 | 同页「释放轻技」→ `corerpg flex cast` + close | 同文件 L76–92 |
| hub 捷径 | hub 图标 `m`「轻技」：**普通点击开页**；**潜行点击** `corerpg flex cast` | `ember_hub.yml` L136–155 lore「潜行点击直接释放轻技」 |
| 底层命令 | `/corerpg flex [cast\|equip\|unequip\|info]`；裸 `flex` → cast | `FlexSkillService.cmdRoot` / `CoreRpgPlugin` sub `flex` |
| 数值（**只引用**） | CD **14s** · 位移 **5.0** 格 · 零体力 · 无伤害 | `plugins/CoreRpg/skills.yml` L52–59；源码 `FlexSkillService` 注释同口径 |

**结论：** 战斗内「少开菜单」缺口真实——hub 潜行点仍须**先开 hub 菜单**；轻技页释放仍是菜单点击。无独立世界内热键。

### 3.2 CoreRpg 钩子面（热键可接性）

| 项 | 现网 | 含义 |
|----|------|------|
| `FlexSkillService` | **仅命令驱动** `cast(Player)`；**无** `@EventHandler`；**未** `implements Listener` | 要热键必须插件薄加事件 |
| `registerEvents` | 已注册 `statService` / `questService` 等；**未**注册 `flexSkillService` | 批后加一行即可 |
| `PlayerSwapHandItemsEvent` | **已有**：`StatService.onSwapHand` → `refreshLater`（B-flex-1 副手刷新） | F 键事件链存在，可参考；**非**释放钩 |
| `PlayerDropItemEvent` | CoreRpg **无** flex/技能释放监听 | 可新建薄听 |
| 1.12.2 客户端自定义键 | **无**原版/Paper 自定义键 API | 勿空想「随便绑 F 成独立键」；只能用原版键/组合 **代理** |

### 3.3 副手位冲突面（B-flex-1 / 灰箍）

| id | 槽 | 来源 |
|----|-----|------|
| `acc_ember_offhand_ward` / `_vita` | `stats.offhand` | B-flex-1 PASS |
| `part_ember_ash_brace` | 同上 | B-anvil-1 PASS |

副手交换键（默认 **F**）在现网是**换饰品/灰箍**的主操作，与「F=放踏步」叙事强冲突。

---

## 4. 候选对比 → 荐案 A（择一）

### 候选 1 · 副手交换 / 丢弃键代理（裸 F 或裸 Q）

| | |
|--|--|
| 做法概要 | 听 `PlayerSwapHandItemsEvent`（F）或 `PlayerDropItemEvent`（Q）→ cancel + `cast` |
| 利 | 单键；实现薄 |
| 弊 | **裸 F**：与守腕/生坠/灰箍副手位**强冲突**（B-flex-1 目标玩家反而丢热键或乱换饰）。**裸 Q**：战斗丢刃/材料误触高 |
| 可实现性 | 1.12.2 事件齐全；**若强上裸 F 须「仅副手 AIR」**——装了副手饰品的玩家等于没热键，与灵活三窗目标相悖 → **不荐裸 F/裸 Q** |

### 候选 2 · 快捷栏固定槽

| | |
|--|--|
| 做法概要 | 固定槽（如 9）放「踏步符」NI；右键/`PlayerInteractEvent` → cast |
| 利 | 事件常见；可不改太多 UX 叙事 |
| 弊 | **占格子**、易误触切槽/右键；符可被挪/丢/整理弄丢；要 NI + 发放/补发动线，面比「一 Listener」厚 |
| 可实现性 | 1.12.2 可做；偏「物品岗+插件」双岗 → **备选，不荐本窗主案** |

### 候选 3 · CoreRpg 绑定一键（原版键组合代理）← **荐**

| | |
|--|--|
| 做法概要 | 插件听 **潜行 + 丢弃（Shift+Q）**：`PlayerDropItemEvent` 且 `player.isSneaking()` 且已装配轻技 → **cancel 丢物** + `FlexSkillService.cast(player)` |
| 利 | **不抢副手位**；不占快捷栏；菜单释放口可保留；误触低于裸 Q（须同时潜行）；实现面 ≈ 1 个 EventHandler + registerEvents |
| 弊 | 双键组合略不如单键爽；需插件岗改 Java（非纯 YAML）；玩家需学一次（lore 半行） |
| 可实现性 | **有依据**：Bukkit 1.12.2 `PlayerDropItemEvent` + `Player#isSneaking` 标准可用；`cast()` 已公开；StatService 的 SwapHand 监听可作「怎么挂 Listener」样板。**FlexSkillService 现无 Listener —— 需插件岗确认**：类改 `implements Listener`、构造后 `registerEvents(flexSkillService)`、事件优先级与「真要丢物」放行（未潜行 / 未装配 → 原样丢）。无客户端自定义键——本方案诚实用原版组合代理，**不**宣称可绑任意键 |

### **荐案一句话**

**荐候选 3：CoreRpg 薄绑「潜行 + Q（丢弃）」代理释放；TrMenu 装配/菜单释放保留为额外口。**

**利弊一句：** 不抢副手饰品叙事、不占快捷栏、误触可控；代价是要插件加 Listener（现网无热键钩）+ 玩家学 Shift+Q，且 1.12 不能真·自定义键。

---

## 5. 硬条（批 A 后施工清单 · 本稿仍不改 YAML/源码）

听谁 / 改哪些（**数值文件勿动**）：

1. **插件（主）：** `CoreRpg/src/.../FlexSkillService.java`
   - `implements Listener`
   - `@EventHandler`：`PlayerDropItemEvent` —— 条件：在线玩家 · `isSneaking()` · `PlayerData.hasFlexSkill()` ·（建议）未开箱子/未在 GUI 内 → `e.setCancelled(true)` + `cast(player)`
   - 未潜行或未装配：**不拦截**（保持原版丢物）
2. **插件：** `CoreRpgPlugin.java` —— `registerEvents(flexSkillService, this)`（与 `statService` 同级）
3. **可选 config：** `skills.yml` 或 `config.yml` 增 `flex.hotkey: sneak_drop`（默认开）——**勿**改 `cooldown_seconds` / `distance`
4. **TrMenu（辅 · 文案岗/插件岗）：** `ember_flex_skill.yml` 说明 I / 释放 C lore；`ember_hub.yml` 轻技 lore —— 各加半行人话「战斗中 §e潜行+Q §7可释放（不开菜单）」；**保留**装配页与菜单释放钮
5. **版本号：** 按服约定 bump CoreRpg（施工窗自定）
6. **仍不改：** `flex_ember_step` CD/距离；誓约三主动；副手白名单；`part.yml`；体力门

---

## 6. 硬禁

- 不动踏步 CD/数值、誓约三主动、位移+保命双上、四件甲、锻炉重做、体力日周门  
- 禁长测 / 挑刺；勿宣称 B0.1 已清  
- disassemble 管理注释、精英壳 **不捆**本窗  
- 未批前 **零改**玩法 YAML / 源码（本稿仅 docs）  
- 勿把裸 F 写成主案却不写副手冲突  

---

## 7. 验收（批后 · 禁 wall-clock / DPS）

- **静态 rg：** `PlayerDropItemEvent` / `sneak` / `cast(` 出现在 `FlexSkillService`；`registerEvents` 含 flex；`skills.yml` `cooldown_seconds: 14` / `distance: 5.0` **未变**  
- **菜单/按键轻测：** 装配踏步后，开阔地 **潜行+Q** → 短位移 / CD 提示；**未潜行 Q** → 仍可丢物；hub/轻技页菜单释放仍可用  
- **副手回归：** 副手持守腕/生坠/灰箍时，**裸 F** 仍只换手（不触发踏步）  
- **禁** wall-clock / DPS；**不叫挑刺**

---

## 8. 玩家动线人话

1. 枢纽 →「轻技」→ 点「装配 · 余烬踏步」（**菜单装配仍保留**）。  
2. 战斗中：**潜行 + Q** 直接余烬踏步（**热键是额外释放口**）。  
3. 仍可开轻技页点「释放轻技」，或 hub 潜行点「轻技」（旧口不删）。  
4. 需要丢手上物品：站直按 Q（不潜行）；或先卸轻技（一般不必）。

---

## 9. 需插件岗确认的点（诚实）

1. `FlexSkillService` 现未注册事件 —— 确认 bump 与 `registerEvents` 落点。  
2. 潜行+Q 与其它插件（若有）丢物监听优先级 —— 建议 `HIGH` + `ignoreCancelled` 策略与 StatService 风格对齐，冲突时再薄调。  
3. 是否要 config 开关（方便回滚）—— 荐有，非硬条。  
4. **不**做客户端键位重绑；若总控日后要「真单键 F」，须另批并写清「仅副手空」或放弃副手热键，**本窗不绑裸 F**。

---

## 10. 回总控摘要

| 项 | 内容 |
|----|------|
| tip | `docs/design-ember-flex-step-hotkey.md` |
| STATUS | **待批 A** |
| 荐案 | CoreRpg · **潜行+Q** 代理释放（候选 3） |
| 本窗 | docs only · 玩法 YAML **零改** · **勿 push** |

---

## 11. 总控批示

- [x] **批 A** · 候选 3（潜行+Q / `PlayerDropItemEvent`）（总控 · tip 本提交）
- [ ] **驳回** · 说明

**批示摘要：** 采纳「潜行+Q」代理释放；**勿**裸 F/裸 Q/快捷栏符；**勿**改 `skills.yml` CD14/距离5；菜单装配/释放保留。交 **余烬-插件**：`FlexSkillService` implements Listener + Drop 事件 + `registerEvents`；可选 TrMenu lore 半行；config `flex.hotkey` 荐有非硬。禁长测/挑刺；勿宣称 B0.1 已清。验收：静态 rg + 按键/菜单轻测（禁 wall-clock/DPS）。
