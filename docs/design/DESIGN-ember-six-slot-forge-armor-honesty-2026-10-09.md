# 余烬 · 六槽工坊手持甲菜单诚实（K0 只能分解 · 决策页）

STATUS=**已批 A · 批 M · D333**（总控同号施工：TrMenu + 可选 held_is_armor；**不抢 D332/K3**；**不改 ember_set** / 价表 / ×0.97 / set_bonus / bv）· 2026-10-09 · 总控

> **一句话：** K0「甲只能分解」已在命令/PAPI 落地，但 `ember_p1_forge` 菜单壳对手持甲仍像可养成，分解旁还写整胚 1/2/3。**已批方案 M · D333**：TrMenu 养成格灰显/禁命令 + 分解 lore 对齐 ×0.1 零头（可选 held_is_armor）。

### 0. 证据

| # | 来源 | 口径 |
|---|------|------|
| E1 | `plugins/TrMenu/menus/ember_p1_forge.yml` | 无手持甲 `icons` 条件分支；E/e·U/u·R/r·Q/q·S/s 常驻可点并 `command: corerpg p1 …`；分解 D lore 静态「胚料 1/2/3」；已嵌 `%held_*_cost%` / `%held_dismantle_yield%`（D307） |
| E2 | `EmberUpgradeRules` | `ARMOR_REFUSE`＝「护甲随护符成长；成色和精工看掉落」；甲 `dismantleYield`＝0；`armorDismantleTenths`＝tier（×0.1 零头）；`dismantleCheck` 非 drop 拒 |
| E3 | `EmberRunPapi.heldForgeLine` / `EmberSixPapi.heldArmorLine` | 甲 + 非 dismantle → `§8`+ARMOR_REFUSE；甲 + dismantle 可拆 →「分解得白板胚 x.y …」；`held_*_lack` 对甲恒空 |
| E4 | T1 / D318 / D332 | K0 接受精工差；甲分解白板；D12-②-F 要求「仅分解可用」——**菜单未灰显仍会误点** |
| E5 | D307 | 费用同屏已为刃/护符服务；**未**覆盖甲态视觉闸 |

**玩家感知（≤3）：**
1. 手持甲打开工坊：一眼看出强化/升阶/精工/成色/互换**不可用**（灰显或短拒因），不必点进聊天才懂。  
2. 分解格仍可用时，文案写清**白板胚零头（×0.1×阶）**，不再暗示整胚 1/2/3。  
3. 换回刃/护符后菜单恢复 D307 费用同屏行为；价表与 K0 规则不变。

**本窗不做：** 改公式/价表、开甲养成（反 K0）、改 ×0.97、K3、ember_set、live 部署、抢 D332 抽测。

---

## §1 方案表（荐 M）

| 方案 | 内容 | 评价 |
|------|------|------|
| **M（荐）** | 本文 §2 为菜单补丁真源；批 A = 采纳规格；**施工另号**（优先 TrMenu；若缺闸可加只读 `%corerpg_p1_held_is_armor%` 0/1）；零改价/零改 K0 | **荐** |
| L | M + 手持甲时整页换成「甲专用工坊」新菜单 | 过厚；不荐默认 |
| W | 借机开甲精工/强化、改 ×0.1、改价表、观察期部署 K3 | **否决**（反 K0 / 偷 forge R / 观察禁） |

---

## §2 菜单补丁规格（方案 M 正文）

### 2.1 目标文件（施工另号）

| 文件 | 动作 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_forge.yml` | 手持甲：养成格灰显/禁点；分解 lore 去「1/2/3」整胚暗示；保留 `%held_dismantle_yield%` |
| （可选）CoreRpg PAPI | 若 TrMenu 无法稳定分支：新增 `%corerpg_p1_held_is_armor%` → `1`/`0`（只读；主手可信 P1 甲） |

**禁改：** `EmberUpgradeRules` 价/权重、`armorDismantleTenths` 公式、set_bonus、×0.97、F 跟随、`ember_set.yml`。

### 2.2 手持甲时 · 养成格（E/e U/u R/r Q/q S/s）

| 项 | 规格 |
|----|------|
| 视觉 | gray pane / barrier 类灰显；名称带「不可用」或「甲 · 拒」 |
| lore | 1 行短拒因镜像 K0：例 `§8护甲随护符成长；成色和精工看掉落`（可直接嵌 `%corerpg_p1_held_enhance_cost%` 等已返回 ARMOR_REFUSE 的行） |
| 点击 | **无** `corerpg p1 enhance|upgrade|refine|quality|swap`（含 confirm）；可 `tell` 一句拒因或静默 |
| 非甲 | 保持现网 D307 预览/确认/费用同屏 |

**推荐实现：** `icons` 条件 `check papi %corerpg_p1_held_is_armor% == 1`（若批后施工加签）priority 高于默认；无新 PAPI 时可用「费用行已是拒因 + 灰显材质」折中，但**必须禁命令**以免误点刷聊天。

### 2.3 手持甲时 · 分解格（D / d）

| 项 | 规格 |
|----|------|
| 静态 lore | **删除或改写**「胚料 1/2/3」；改为「掉落甲 → 白板胚零头（0.1×阶）；攒满 1 到账」+ 仍「预览后聊天确认」 |
| PAPI 行 | 保留 `§e预览：§f%corerpg_p1_held_dismantle_yield%`（已含白板 x.y / 不可分解） |
| 点击 | 预览命令可保留 `corerpg p1 dismantle`（与现网一致）；migrate/非 drop 由命令拒绝（D332 抽测覆盖） |
| 注记 | 报告/验收可旁注「白板经济模型未覆盖」（对齐 D318/D332） |

### 2.4 岗位与出口

| 角色 | 职责 |
|------|------|
| 策划 | 本 DESIGN 批 A |
| 插件/菜单 | 另号按 §2 改 TrMenu（±可选 PAPI）；自测手持甲/刃切换 |
| 测试 | 另号：甲灰显且无养成命令；分解预览口径；刃路径 D307 回归；**不**替代 D332 A–G |
| 总控 | 批 A；施工/观察期显示部署另签 |

**绿出口（本债施工号）：** 手持 drop 甲 → 养成格不可用且文案拒因正确；分解预览示白板零头；手持刃 → D307 费用同屏仍在；零价表 diff。  
**失败中止：** 误开甲强化/精工；误改 ×0.1 公式；误动价表 → 停并报总控。

### 2.5 与观察期 / D332 关系

- 不挡 Stage2 绿出口日历。  
- 不抢 D332 分解实机抽测；本债是**菜单壳**，D332 是**分解资产路径**。  
- 观察期若批后施工：仅显示/菜单（+可选只读 PAPI），**不**升玩法 bv、不改 set_bonus。

---

## §3 不动与否决

| 项 | 状态 |
|----|------|
| 分解公式 / 价表 / ×0.97 / set_bonus / jar 玩法逻辑 | 不动 |
| K0（甲无强化/精工/成色工坊养成） | **维持**；本债只诚实展示 |
| K3 / Pack6 / 天赋 / 灰印 / 样本 R / 动 F | 不动 / 硬禁 |
| D329–D331 已落菜单 | 不重开；本窗只 `ember_p1_forge` |
| D332 测试号 | 不抢 |

---

## §4 批注栏

- [x] **批 A：采纳方案 M** → **D333 同号**按 §2 施工（TrMenu ± held_is_armor PAPI）
- [ ] 升级 L（甲专用工坊页）
- [ ] 否决 / 改派

**签字：** 总控 · D333 · 2026-10-09

**总控批注：** 批 A·M·D333。施工范围 = TrMenu `ember_p1_forge.yml` + 可选只读 `%corerpg_p1_held_is_armor%`（插件岗另交 PAPI；本号先写入条件键名）。**不抢 D332 测试 / K3**。禁改 set_bonus / ×0.97 / bv 真值 / 价表公式 / ember_set / CoreRpg Java（本号）。

---

## 变更记录

| 日期 | 事件 | 谁 |
|------|------|-----|
| 2026-10-09 | 初稿 · 待批 A · 荐 M · 上游 D332 | 策划执行手 |
| 2026-10-09 | **已批 A·M·D333** · 同号 TrMenu 落地；held_is_armor 待插件 | 总控 |

*工坊手持甲诚实 · 已批 A·M·D333 · K0 只能分解 · 分解 lore×0.1 · 零改价 · 不抢 D332/K3。*
