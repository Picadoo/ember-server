# 余烬 · 生活页魂尘兑换/孵化回 Layout（使魔养成旁轨可点 · ≠抬挂机）

STATUS=**已批 A · 批 M · D376 · 已施工** · 2026-10-10 · tip [`STATUS-ember-next-hard-debt-life-soul-dust-layout-need-design-2026-10-10.md`](../status/STATUS-ember-next-hard-debt-life-soul-dust-layout-need-design-2026-10-10.md)（已关）· backlog `B-life-soul-dust-layout` · STATUS [`STATUS-ember-life-soul-dust-layout-d376-2026-10-10.md`](../status/STATUS-ember-life-soul-dust-layout-d376-2026-10-10.md) · 总控 D375 后内容真债（养成旁轨轻循环）

> **一句话玩家价值：** 补给·生活页能**点到**旧靴/碎骨兑魂尘、点到孵化——钓鱼→魂尘→投喂/孵化→出战真正闭环可玩，拉长在线趣味；**不**靠抬挂机产量。

---

## 0. 证据表

| # | 来源 | 口径 |
|---|------|------|
| E1 | `plugins/TrMenu/menus/ember_life.yml` Layout | 现盘：`# A B C #` / `#   D   #` / `###I#P#Z#` → 仅面包·钓竿·烤鱼·熬药·生活等级·打开使魔·返回 |
| E2 | 同文件 Icons G/K/H/J | **已定义**且 actions 接 `corerpg life buy soul_dust` / `soul_dust_bone` / `pet_ashling` / `pet_cinder`——**不在 Layout** |
| E3 | 同文件 L18–19 注释 | D99：「魂尘 / 使魔…stowed…G/K/H/J 仍定义；P 为使魔直达」——收起态未随 D373 回盘 |
| E4 | P 格 lore | 「魂尘：本页旧靴/碎骨兑换（非挂机）」→ **宣称本页可兑、盘上无兑** = 假平面 |
| E5 | D373 STATUS W1d | 「加格 P；孵化 H/J…（**图标仍可后置入 layout**）」→ 本债 = 后置升主，**≠**复述出战主交付 |
| E6 | `plugins/CoreRpg/life.yml` | `soul_dust`/`soul_dust_bone`：旧靴/碎骨×2 + 5 币 · **daily: 2** · Lv2；`pet_ashling`/`pet_cinder`：魂尘×10 + 600 币 · **weekly: 1** · Lv4 |
| E7 | `ember_pet.yml`（D373） | 出战左 summon / 右 dismiss；lore/Open 写生活·钓鱼真源；投喂仍 `pet feed` |
| E8 | `ember-v1.yml` `afk.tiers` | **无** `soul_dust`；本窗**禁止**加键抬表 |
| E9 | Stage2 / 硬禁 | 观察续；禁样本 R / Pack6 / 天赋灰印 / 关观察 / 改 ×0.97 / 开 K3 |

**一句话问题：** D373 把「魂尘来自生活」说真了、出战接上了，但生活页兑换/孵化键仍收在 Layout 外——旁轨断在「兑不了」。

**为何选本债、不选日路由/周钩子：**  
- **日路由加厚：** D306「今天该打哪」+ 四态已上；D375 已串挂机→工坊；缺同等「宣称可点却无键」硬证据。  
- **挂机周钩子：** D305 名片已齐；无现成周层 PAPI，易新 Java 或抬感产。  
- **本债：** Layout 假平面 + D373 官方后置句，证据最硬；轻量 TrMenu-only。

---

## 1. 玩家感知目标（≤3）

1. **打开补给·生活就能看见并点「旧靴/碎骨 → 魂尘」。**  
2. **有魂尘时可在同页点孵化（或明确半行指使魔页投喂）——孵化成功仍进使魔页。**  
3. **文案与盘面一致：** 不再出现「本页兑换」却无兑换格。

---

## 2. 方案表

| 方案 | 内容 | 评价 |
|------|------|------|
| **A · 只改 lore** | P/Open 改成「请去…」却仍无兑换格；或改口「命令兑换」 | **否** — 仍假平面或逼玩家打指令；违 UX |
| **M · Layout 回盘可点（荐）** | G+K 必回盘；H+J 同批回盘；订正 D99 注释；Open 半行；**零改** life 价表 | **荐** — 闭环可点；对齐 D373 后置；零经济压 |
| **L · 改价/抬日顶/挂机加魂尘** | 抬 `daily`/`weekly` 或 afk 加 soul_dust | **否决** — 硬禁抬表；本债是可见可点，不是经济窗 |

**批 A = 采纳方案（荐 M）。批 A ≠ 施工 ≠ 关观察 ≠ 抬挂机表 ≠ 开 R ≠ 开 K3。**

---

## 3. 可落地（方案 M）

### 3.1 窗拆分

| 窗 | 做什么 | 不动 |
|----|--------|------|
| **W1a · 魂尘兑换回盘（必做）** | `ember_life` Layout 纳入 **G**、**K**（旧靴→魂尘 / 碎骨→魂尘）。荐盘形（施工择一，不挤掉 A–D 主补给）：例第二行 `# A B C #` 下增一行 `# G   K #`，或把空位行 `#   D   #` 改为 `# G D K #`。保留既有 lore/actions | `life.yml` 价·daily·Lv；不新命令 |
| **W1b · 孵化回盘（同批荐）** | Layout 纳入 **H**、**J**（灰灵/烬火）。可与 G/K 同行或底栏邻 P：例 `##H#P#J#` / `# H P J Z`（保留 I 生活等级）。孵化成功后 `menu: ember_pet` **保留**（D373 已接） | 不改 weekly:1 / 600 币 / 魂尘×10 |
| **W1c · 注释与假平面清理（必做）** | 删/改 L18–19「魂尘/使魔 stowed」口径：改为「D376+：G/K/H/J 回盘；使魔主战在 `ember_pet`（D373）」；E/F/O/W/V **仍可**保持定义不入盘（本窗不回） | 不回盘重铸石/稳固符/副手/旧票 |
| **W1d · Open / P 半行（必做）** | 可选 Events.Open tell：`§8魂尘：本页旧靴/碎骨兑换 · 非挂机`；P lore 可改为「出战/收回/投喂 · 魂尘见本页 G/K」——与真盘对齐 | 不教裸 `/corerpg life buy` |
| **W1e · 枢纽/帮助半指（可选）** | 枢纽「补给·生活」lore +半行「可兑魂尘养使魔」；help 使魔相关句可互指 | 不重开 D358 菜单诚实扫 |

### 3.2 推荐 Layout 示意（施工可微调，须含 G K；荐含 H J）

```
#########
# A B C #
# G D K #
# H   J #
###I#P#Z#
```

或更紧：

```
#########
# A B C #
# G D K #
##H#P#J##
####I#Z##
```

**验收底线：** 打开生活页 **肉眼可见** G、K；点下去走现网 `life buy`（缺料/满日顶由服务端 tell）；H/J 若同批则可见可点。

### 3.3 不动清单

| 项 | 口径 |
|----|------|
| `life.yml` 兑换价 / daily / weekly / life_level | **不动** |
| `afk.tiers` / `daily_kills` / 离线% | **不动**（禁挂机加 soul_dust） |
| D373 出战 summon/dismiss / NI 来源句 | **不重开**；只消费其后置 layout |
| E（重铸石）/ F（稳固符）/ O·W（副手）/ V（旧票） | **本窗不回盘**（另债；副手已有 D289 诚实口径） |
| 体力 / 掉落 / ×0.97 / set_bonus / K3 | **不动** |

### 3.4 门禁

| 若动到… | 门禁 |
|---------|------|
| **仅 M · W1a–W1d**（Layout + 注释 + Open/P） | 静态：Layout 含 G/K（及批内 H/J）；冒烟：有旧靴→兑出魂尘；有魂尘→孵化进使魔页；P 仍开使魔；**不要求 p1sim**（零战斗收益压） |
| **改 life 价/日顶/周顶** | **本窗禁止**；须另号经济设计 |
| **挂机加 soul_dust** | **禁止** |

### 3.5 与邻域

| 邻域 | 边界 |
|------|------|
| D373 使魔出战+lore | **上游**；本债补 layout，不复做出战 |
| D375 挂机去哪花 | **不抢**；挂机材料≠魂尘 |
| D374 日更第三拍 | **不抢** |
| D306 日路由 / D305 名片 | **不重开** |
| D99 收起 | 本窗**局部撤销**魂尘/孵化收起；其它收起项仍收 |

---

## 4. 本窗不做（硬禁）

- 抬 `daily_kills` / 离线% / 各层每日量·币表 / 默默给挂机加魂尘  
- 开样本 R / Pack6 / 天赋·灰印 HOLD 重开  
- 提前关观察 / 改 ×0.97 / 翻 set_bonus / 开 K3 live  
- 改 `life.yml` 价与次数当「顺手福利」  
- 复述/重做 D373 出战主交付、D374 MM、D375 挂机去哪花  
- 观察运维变体 / 附录空转  
- 玩家面教裸 `/corerpg life buy …`  
- stage 脏 runtime（ladder / calamity-state / p1-six / MM SavedData 等）

---

## 5. 批注勾选（总控）

- [x] **批 M**（W1a G+K 必做；W1b H+J 同批；W1c+W1d 必做；W1e 可选）→ **D376 已施工** · **策划荐 · 总控采纳**  
- [ ] **批 M′**（仅 G+K；H/J 再后置）· 可接受但不如 M 完整  
- [ ] **批 A**（只改 lore、不回盘）· **不荐**  
- [ ] **批 L**（改价/抬顶/挂机加魂尘）· **否决默认**  
- [ ] 驳回改派（理由：________）

**策划荐勾：** **批 M**。

**说明：** 批 A ≠ 施工 ≠ 关观察 ≠ 抬挂机表 ≠ 开 R ≠ 开 K3。

---

## 6. 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-10 | 策划 · 初稿 STATUS **待批 A** · 荐 M；选题生活 Layout 回盘（D373 后置升主） |
| 2026-10-10 | 总控批 A·M · D376 施工：Layout 纳入 G/K/H/J + D99 注释订正 + Open/P 对齐 + hub 半行 · **≠关观察 ≠改 life 价/次 ≠抬挂机表** |

---

## 7. 参考

- tip · 本 STATUS 同 slug  
- `plugins/TrMenu/menus/ember_life.yml` · `ember_pet.yml`  
- `plugins/CoreRpg/life.yml` · `pet.yml`  
- D373 · [`DESIGN-ember-pet-loop-honest-2026-10-10.md`](DESIGN-ember-pet-loop-honest-2026-10-10.md) · [`STATUS-ember-pet-loop-honest-d373-2026-10-10.md`](../status/STATUS-ember-pet-loop-honest-d373-2026-10-10.md)  
- D375 · [`DESIGN-ember-afk-spend-loop-visible-2026-10-10.md`](DESIGN-ember-afk-spend-loop-visible-2026-10-10.md)

---

*已批 A · 批 M · D376 已施工 · ≠关观察 ≠抬挂机表 ≠开样本 R ≠复述 D373 出战主交付。*
