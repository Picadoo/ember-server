# 余烬 · 使魔选定盘面化（有趣系统 · list→可点换宠 · ≠抬日表 · ≠sx06）

STATUS=**已批 A · 批 M1 · D402 · 已落地** · 2026-10-10 · tip [`STATUS-ember-next-hard-debt-pet-select-menu-need-design-2026-10-10.md`](../status/STATUS-ember-next-hard-debt-pet-select-menu-need-design-2026-10-10.md)（**已关 · 已批·已落地**）· backlog `B-pet-select-menu`（**已关 · 已落地**）· STATUS [`STATUS-ember-pet-select-menu-d402-2026-10-10.md`](../status/STATUS-ember-pet-select-menu-d402-2026-10-10.md) · **≠关观察 ≠开闸 ≠抬日表 ≠开 R ≠开 K3 ≠假开旧日常 ≠交 sx06 ≠教玩家手打指令**

> **一句话玩家价值：** 打开使魔页就能**点一下换灰灵/烬火**——不再靠聊天 list 和手打 `/corerpg pet summon <id>`；养成旁轨从「能养」变成「愿意换着玩」。

> **批 A ≠ 施工 ≠ 关观察 ≠ 抬日表 ≠ 开 R ≠ 开 K3 ≠ 假开旧日常 ≠ 交 sx06。**
>
> **总控批 A·M1 · 2026-10-10：** 采纳方案 **M1**（薄页 `ember_pet_select` · L→选定 · 灰灵/烬火点 `summon <id>` · 玩家路径禁教斜杠 · 未解锁禁假写已拥有；**非** M2 本页两格）；**同号已落地 TrMenu**；≠关观察 ≠抬日表 ≠sx06 ≠开 R ≠开 K3 · 零改 Java/afk/Stage2。

---

## 0. 证据表

| # | 来源 | 口径 |
|---|------|------|
| E1 | `plugins/TrMenu/menus/ember_pet.yml` 格 L | 名「使魔图鉴」· actions=`command: corerpg pet list` · **无**选定盘 / **无** `summon <id>` |
| E2 | `PetService.cmdList` | 刷已解锁列表后**明示** `/corerpg pet summon [id] · dismiss · unlock <id> · feed` | 教斜杠 |
| E3 | 同菜单格 E | 左 `summon`（无 id）/ 右 `dismiss` · 只动**当前 active** | 换宠断链 |
| E4 | `PetService.cmdSummon(id)` | 有 id → `setActivePet` + 出战；无解锁且无蛋 → 诚实拒门 | **现网可复用** |
| E5 | `plugins/CoreRpg/pet.yml` | 仅 `pet_ember_ashling`（余烬灰灵）· `pet_ember_cinder`（余烬烬火）；别名 ashling/cinder | 两格薄交付 |
| E6 | `CorePapiPet` | 有 active_name / level_line / unlocked_count；**无** `pet_unlocked_<id>` | 未解锁禁假写「已拥有」；可静态灰态 + 点后插件 tell |
| E7 | D373/D378/D385 | 出战可点 · 兑尘跳 · 等级同屏 **已齐**；**未**改 list | 本债升主合法；≠复述同屏 |
| E8 | Stage2 / 总控催 D402 | 观察续；禁抬日表/假开旧日常/开R·K3/关观察；**sx06 等 D400 PASS**；禁复述 D373–D401 | 选题边界 |

**一句话问题：** 使魔页能养、能出战，但不能在盘上换宠——第二只使魔的趣味被聊天 list 挡死。

**定位拍板（用证据）：**

| 选项 | 结论 | 理由 |
|------|------|------|
| 复述周目标/五本短征/配方速览/枢纽旁注/等级同屏 | **否** | 禁复述 D373–D401 |
| sx06 | **否** | 等 D400 PASS |
| 挂机 remain / 仓内还差（D383/D398 后置） | **否本窗** | 禁复述挂机加厚；证据弱于本 UX |
| 空跳转 / 又一张纯 PAPI 同屏 | **否** | 无新假平面硬证；本债是可点选定 |
| 新使魔物种 / 改 feed 公式 / 抬战力 5% 帽 | **否** | 过厚；硬禁经济压 |
| **使魔选定盘面化（荐）** | **采纳** | E1–E5；零新 API；对齐 UX；有趣旁轨 |

---

## 1. 玩家感知目标（≤3）

1. **点一下换宠：** 使魔页（或薄子页）能看见灰灵/烬火格；已解锁点一下即选定并出战（或明确「选定」语义）。  
2. **不再教斜杠：** 玩家路径上**不出现**「请手打 `/corerpg pet …`」；list 聊天灌指令退出主路径。  
3. **未解锁诚实：** 未孵化/未解锁不写「已拥有」；点后走现网拒门 tell（持蛋可首次解锁逻辑保留）。

---

## 2. 方案表

| 方案 | 内容 | 评价 |
|------|------|------|
| **A · 只改 lore / Open tell「去聊天看 list」** | 仍点 L→list | **假改善**；仍教斜杠；**否决** |
| **M · 选定盘面化 · 复用 `summon <id>`（荐）** | L 改开薄选定页（或本页两格）· 灰灵/烬火 → `corerpg pet summon pet_ember_*` · 互指/状态刷新 · 可选略收 list 教斜杠文案 | **荐** — 零新经济；TrMenu 为主；对齐 UX；可拆薄插件号 |
| **L · 新 `pet select` API + 按 id 解锁 PAPI + 第三只使魔 / 改 feed** | 大插件+新宠 | **否决当默认真** — 过厚；本窗不绑 |
| **W · sx06 / 假开旧日常 / 抬日表 / 空跳转复述** | — | **否决** — 硬禁 |

**批 A = 采纳方案 M1（薄页 `ember_pet_select`）。批 A ≠ 施工 ≠ 关观察 ≠ 抬日表 ≠ 开 R ≠ 开 K3 ≠ 交 sx06。**

### 批 A 勾选（总控填）

- [x] **方案 M1**（薄页 `ember_pet_select` · W1a–W1d 见 §2；**钉死 M1，非 M2 本页两格**）
- [x] 钉死：薄页 `ember_pet_select` · 格 L → `menu: ember_pet_select` · 两宠可点选定（`pet_ember_ashling` / `pet_ember_cinder`）· 复用现网 `summon <id>` · 玩家路径禁教手打斜杠 · 未解锁禁假写已拥有 · 底栏返回 `ember_pet`
- [x] 否决 A（只改 lore 仍 list）/ L（新宠/改 feed/大 API 当默认真）/ W（sx06/假开旧日常/空转）· **否决本窗默认真采用 M2**（本页两格；施工若极薄可另议，本批钉 M1）
- [x] 批注：总控批 A·M1 · 2026-10-10 · D402 · **同号已落地 TrMenu** · ≠关观察 ≠抬日表 ≠sx06 ≠开 R ≠开 K3 · 零改 Java/afk/Stage2

---

## 3. 可落地（方案 M）§2

### 2.1 菜单形态（钉死 · 施工择一）

| 项 | 钉 |
|----|----|
| 入口 | 现网 `ember_pet` 格 **L**（原「使魔图鉴」） |
| 形态 **M1（已批钉死）** | 新薄菜单 `ember_pet_select`（Title 如「§a使魔 · 选定」）· L → `menu: ember_pet_select` · 底栏返回 `ember_pet` |
| 形态 **M2（本批否决默认真）** | 不新文件：在 `ember_pet` Layout 增两格（例 A/C）直接选定；L 改名「选定说明」或改为打开同一页锚点 · **本批钉 M1，勿默认采用** |
| 两宠格 | **灰灵** → `command: corerpg pet summon pet_ember_ashling`（或别名 `ashling`）· **烬火** → `… pet_ember_cinder` |
| 点击语义 | 与现网 summon 一致：**设为选定 + 出战**（若已出战他宠则替换） |
| 状态同屏 | 选定页可挂 `%corerpg_pet_active_name%` / `%corerpg_pet_level_line%`；返回使魔页后 E/S/I 已有 update:20 |
| 未解锁 | lore 写「未解锁 · 生活页孵化/持蛋出战」；**禁**假写 ✔/已拥有；点击允许走插件拒门（持蛋首次解锁逻辑**保留**） |
| 已选定高亮 | 可选：lore 行「当前选定」对比 `active_name`；无按 id PAPI 时用静态+返回刷新即可 |

### 2.2 文案与假平面禁（必做）

| 项 | 钉 |
|----|----|
| 玩家路径 | **禁止** tell/lore 教手打 `/corerpg pet …` |
| Open tell | 使魔页/选定页一句：「点灰灵或烬火切换出战」 |
| list 命令 | 主路径不再调用；若插件仍保留 list（admin/兼容）· **可选另号**收掉教斜杠末行（不绑本窗主交付） |
| 图鉴名 | L 可改「选定使魔」或「换一只」· 避免「图鉴」暗示只读 list |

### 2.3 与现网闭环对齐

| 既有 | 本债 |
|------|------|
| D373 出战/收回 | **不重开**；E 格保留；选定页负责换 id |
| D378 去生活兑尘 | **不重开** |
| D385 等级同屏 | **不重开**；选定后同屏键继续有效 |
| 生活孵化 H/J | 未解锁入口仍指向生活；选定页可半行「去生活孵化」→`menu: ember_life`（**可选**；有则真跳，禁空跳） |

### 2.4 本窗不做与 Stage2 边界

| 不做 | 理由 |
|------|------|
| 关观察 / 勾 §2.4 / 改 ×0.97 | Stage2 续 |
| 抬 `daily_kills` / afk.tiers | 硬禁 |
| 开 R / 开 K3 / Pack6 / 天赋灰印 | 硬禁 |
| 假开 `gate_daily` 旧七线 | 硬禁 |
| 交 sx06 | 等 D400 PASS |
| 新使魔物种 / 改 feed.* / 抬 5% 战力帽 | 过厚 |
| 必做新 `pet_unlocked_<id>` PAPI | 可后置；无键不假写 |
| 必做独立 `pet select` 无出战 API | 现网 summon 已够；另号可拆 |
| 复述 D401 周目标 / 五本短征 / 配方速览 / 枢纽旁注 | 禁复述 |
| stage 脏 runtime | docs-only |

### 2.5 验收（批 A 后施工用）

| # | 步骤 | 期望 |
|---|------|------|
| V1 | 开使魔页 → 点 L（或 A/C） | 进入选定盘或本页可选格；**不**刷 list 教斜杠墙 |
| V2 | 已解锁两宠 · 点非当前宠 | active 切换且出战实体换皮/名；返回页 `%corerpg_pet_active_name%` 更新 |
| V3 | 未解锁 · 无蛋点击 | 插件诚实拒门；菜单**无**「已拥有」假字 |
| V4 | 未解锁 · 持对应蛋点击 | 现网首次解锁逻辑仍可走（与 summon 一致） |
| V5 | 全文玩家路径 | **无**教手打 `/corerpg pet`；**无**sx06；**无**旧日常窟；**无**抬日表文案 |

---

## 4. 本窗不做（硬禁）

- 关观察 / 开闸 / 开样本 R / 开 K3 live / 改 ×0.97  
- 抬挂机 `daily_kills` / afk.tiers / 改 life·feed 价表  
- Pack6 / 天赋 / 灰印 HOLD 重开  
- 假开 `gate_daily` 旧七线 / 假宣传日常窟开放  
- **交 sx06**  
- 以空跳转 / 空转菜单复扫 / 纯 PAPI 同屏复述当主交付  
- 复述 D373–D401（周目标刚批、五本短征、配方速览、枢纽旁注、等级同屏）为主债  
- 教玩家手打指令；菜单假写未拥有为已拥有  
- 本号改 YAML/Java（**docs-only**）；stage 脏 runtime  

---

## 5. 与上下游

| 项 | 关系 |
|----|------|
| D373 出战诚实 | 保留 E；本债补「换 id」断链 |
| D378/D385 | 不重开兑尘/同屏 |
| D401 周目标 | 施工中；**不**本号主交付 |
| D400 sx05 | 抽测/PASS 闸管 sx06；本号不交第六本 |
| 挂机 D383/D398 | **不**本号主交付 |

---

## 6. 后置（本号不写）

1. sx06（等 D400 PASS）  
2. 按 id 解锁态 PAPI（`pet_unlocked_ashling` 等）灰态精确化  
3. 独立 `pet select`（只 setActive 不出战）  
4. list 命令去斜杠教学（admin 兼容另号）  
5. 第三只使魔 / 图鉴收集奖  

---

*D402 · 使魔选定盘面化 · 已批 A·M1 · 已落地 · ≠关观察 ≠抬日表 ≠sx06。*

---

| 日 | 说明 |
|----|------|
| 2026-10-10 | 初稿 · tip 打开 · 待批 A·荐 M · 拟 D402 · tip @`e12c548c` |
| 2026-10-10 | **已批 A · 方案 M1 · D402** · 总控批注勾选（薄页 `ember_pet_select` · L→选定 · summon <id> · 非 M2）· **施工另派 TrMenu** · ≠关观察 ≠抬日表 ≠sx06 |
| 2026-10-10 | **已落地** · `ember_pet_select` + `ember_pet` L · 去 list · Open 换宠 tell · 去生活孵化半行 · tip/backlog 已关 · ≠改 Java/afk/Stage2 |
