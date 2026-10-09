# 余烬 · 使魔页→生活兑魂尘跳转（闭环反向互指 · ≠抬日表）

STATUS=**待批 A · 荐 M** · 2026-10-10 · tip [`STATUS-ember-next-hard-debt-pet-life-jump-need-design-2026-10-10.md`](../status/STATUS-ember-next-hard-debt-pet-life-jump-need-design-2026-10-10.md) · backlog `B-pet-life-jump` · 总控 D377 后内容真债（使魔养成闭环反向可点）

> **一句话玩家价值：** 站在使魔页缺魂尘时，能**点一下**跳到补给·生活兑旧靴/碎骨——养成不绕枢纽，在线粘性↑；**不**靠抬挂机产量或改兑换价。

> **批 A ≠ 施工 ≠ 关观察 ≠ 抬日表 ≠ 开 R ≠ 开 K3 ≠ 复述 D373 出战主交付。**

---

## 0. 证据表

| # | 来源 | 口径 |
|---|------|------|
| E1 | `plugins/TrMenu/menus/ember_pet.yml` Layout | `# L E S #` + 返回 B；空行两侧无其它业务格 |
| E2 | 同文件 Icons | L=`corerpg pet list`；E=左 `summon` / 右 `dismiss`（D373）；S=`pet feed`；**全文无** `menu: ember_life` |
| E3 | Open / S lore | 「魂尘：补给 · 生活（旧靴/碎骨兑换）· 非挂机日表」「生活兑换魂尘升级使魔」——**指向生活、无跳转键** |
| E4 | `ember_life.yml` | P / 孵化后 → `menu: ember_pet`（D373 W1d + D376）；G/K 兑尘已回盘可点 |
| E5 | D373 DESIGN §W1d | 只交付「生活→使魔」；**未**写使魔→生活 |
| E6 | D377 枢纽旁轨 | 今日卡可指生活·使魔；**进使魔页后**仍靠玩家记「尘在生活」 |
| E7 | `plugins/CoreRpg/life.yml` / `pet.yml` | 兑尘价·daily；feed `cost_*` ——本窗**零改** |
| E8 | Stage2 / 硬禁 | 观察续；禁抬日表 / 样本 R / Pack6 / 天赋灰印 / 关观察 / 改 ×0.97 / 开 K3 / 观察运维变体 |

**一句话问题：** 单向「生活→使魔」已通；使魔页把「尘在生活」说真了，却缺反向可点出口——缺尘时养成卡死在页内。

**为何选本债、排除其它：**  
- **日更菜单「可躲」半行：** D374 战斗已 PASS，lore 另号可后置；不如本债「宣称去不了」硬。  
- **工坊←挂机：** D375 单向够用；工坊盘密。  
- **使魔等级同屏：** 无现成 pet level PAPI，易新 Java。  
- **软身份再加厚：** D305 已齐。  
- **本债：** 反向断链 Icon + 总控明示方向；TrMenu-only。

---

## 1. 玩家感知目标（≤3）

1. **使魔页能看见并点到「去生活兑魂尘」（或等价短名）。**  
2. **点下去打开 `ember_life`，盘上 G/K 仍可兑（沿用 D376）——不教裸指令。**  
3. **投喂/Open 文案与跳转一致：** 不再只写「生活兑换」却无处点。

---

## 2. 方案表

| 方案 | 内容 | 评价 |
|------|------|------|
| **A · 只加 lore / tell** | 投喂格再写「请回枢纽→补给」；无新格 | **不荐独批** — 仍多绕一层；半假平面未消 |
| **M · 使魔页加生活跳转格（荐）** | Layout 增一格 → `menu: ember_life`；投喂/Open 半行互指；可选 feed 失败 tell 指生活；**零改**价表/出战 | **荐** — 闭环双向可点；对齐 D373/D376；零经济 |
| **L · 新 PAPI 进度墙 / 改价 / 挂机加尘** | 等级同屏新 Java；或抬 feed/life；或 afk 加 soul_dust | **否决** — 过厚或硬禁 |

**批 A = 采纳方案（荐 M）。批 A ≠ 施工 ≠ 关观察 ≠ 抬挂机表 ≠ 开 R ≠ 开 K3。**

---

## 3. 可落地（方案 M）

### 3.1 窗拆分

| 窗 | 做什么 | 不动 |
|----|--------|------|
| **W1a · 跳转格入盘（必做）** | `ember_pet` Layout 增格（荐键名 **F** 或 **G**，施工择一不与 L/E/S/B 冲突）。例第二行改为 `# F L E S #` 或底栏 `# F     #` 邻空位。display：短名「§d去生活兑魂尘」；lore：旧靴/碎骨→魂尘 · 非挂机 · ➥ 打开补给·生活；actions：`menu: ember_life`（可 + 短 sound） | 不改 summon/dismiss/feed 命令；不改 life 价 |
| **W1b · 投喂/Open 互指（必做）** | S 投喂 lore 在「生活兑换」旁加半行「缺尘点旁格去生活」或「➥ 邻格兑尘」；Open tell 可半行「缺魂尘：点本页去生活」 | 不删 D373「非挂机」诚实句 |
| **W1c · 失败导向（可选同批）** | 若 feed 失败仅服务端 tell：菜单侧可在 S 点击后追加一行 tell「§8没有魂尘？点本页去生活兑换」（**不**伪造成功）；不得教 `/corerpg life buy` | 不改 PetService 失败文案也可（可选） |
| **W1d · 枢纽半指（可选）** | 非必须；D377 旁轨已指生活·使魔。若施工顺手：使魔入口 lore 不要求本窗改 hub | 不重开 D377 决策卡 |

### 3.2 推荐 Layout 示意（施工可微调，须含跳转格）

```
#########
#       #
# F L E S #
#       #
####B####
```

或更松：

```
#########
#   F   #
# L E S #
#       #
####B####
```

**验收底线：** 打开使魔页 **肉眼可见**「去生活兑魂尘」（或等价）；点击 → 打开 `ember_life`；G/K 仍可兑；出战/投喂原键仍在；Open/投喂无「挂机副产」回潮。

### 3.3 不动清单

| 项 | 口径 |
|----|------|
| `pet.yml` feed cost / max_level / power | **不动** |
| `life.yml` 兑尘价 / daily / weekly / Lv | **不动** |
| D373 出战 summon/dismiss / NI 来源句 | **不重开**；只补反向跳 |
| D376 G/K/H/J Layout | **不重开**；消费其可兑态 |
| `afk.tiers` / daily_kills / 离线% | **不动**（禁挂机加魂尘） |
| 新 `%corerpg_pet_*` PAPI / 等级墙 | **本窗不做** |
| 体力 / 掉落 / ×0.97 / set_bonus / K3 | **不动** |

### 3.4 门禁

| 若动到… | 门禁 |
|---------|------|
| **仅 M · W1a–W1b**（跳转格 + 互指） | 静态：Layout 含跳转且 `menu: ember_life`；冒烟：使魔→生活→可见 G/K；返回使魔出战/投喂仍通；**不要求 p1sim** |
| **改 life/pet 价或次数** | **本窗禁止** |
| **挂机加 soul_dust** | **禁止** |
| **新 pet level PAPI** | **另号**；本窗否决 L |

### 3.5 与邻域

| 邻域 | 边界 |
|------|------|
| D373 出战+lore+生活→使魔 | **上游**；本债只补**反向** |
| D376 生活兑尘回盘 | **上游可兑态**；本债跳进该页 |
| D375 挂机去哪花 | **不抢**；魂尘≠挂机材料 |
| D374 日更预警 | **不抢**；菜单「可躲」半行另号后置 |
| D377 枢纽旁轨 | **不重开**决策卡；页内闭环自洽 |
| D305 软身份 | **不重开** |

---

## 4. 本窗不做（硬禁）

- 抬 `daily_kills` / 离线% / 各层日表 / 挂机加魂尘  
- 开样本 R / Pack6 / 天赋·灰印 HOLD 重开  
- 提前关观察 / 改 ×0.97 / 翻 set_bonus / 开 K3 live  
- 改 `life.yml` / `pet.yml` 价与次数当「顺手福利」  
- 复述/重做 D373 出战、D374 MM、D375 去哪花、D376 Layout、D377 旁轨主交付  
- 观察运维变体 / 附录空转  
- 新使魔等级 PAPI / 进度墙假平面  
- 玩家面教裸 `/corerpg life buy …`  
- stage 脏 runtime（ladder / calamity-state / p1-six / MM SavedData 等）

---

## 5. 批注勾选（总控）

- [ ] **批 M**（W1a 跳转格必做；W1b 互指必做；W1c 可选；W1d 可选）· **策划荐**  
- [ ] **批 M′**（仅 W1a 跳转格；W1b 后置）· 可接受但不如 M  
- [ ] **批 A**（只改 lore/tell、不加格）· **不荐**  
- [ ] **批 L**（新 PAPI / 改价 / 挂机加尘）· **否决默认**  
- [ ] 驳回改派（理由：________；次选日更菜单「终厅可躲」半行）

**策划荐勾：** **批 M**。

**说明：** 批 A ≠ 施工 ≠ 关观察 ≠ 抬挂机表 ≠ 开 R ≠ 开 K3。

---

## 6. 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-10 | 策划 · 初稿 STATUS **待批 A** · 荐 M；选题使魔→生活兑尘跳转（D373 反向补齐） |

---

## 7. 参考

- tip · [`STATUS-ember-next-hard-debt-pet-life-jump-need-design-2026-10-10.md`](../status/STATUS-ember-next-hard-debt-pet-life-jump-need-design-2026-10-10.md)  
- `plugins/TrMenu/menus/ember_pet.yml` · `ember_life.yml`  
- `plugins/CoreRpg/life.yml` · `pet.yml`  
- D373 · [`DESIGN-ember-pet-loop-honest-2026-10-10.md`](DESIGN-ember-pet-loop-honest-2026-10-10.md)  
- D376 · [`DESIGN-ember-life-soul-dust-layout-2026-10-10.md`](DESIGN-ember-life-soul-dust-layout-2026-10-10.md)  
- D377 · [`DESIGN-ember-hub-chase-side-track-2026-10-10.md`](DESIGN-ember-hub-chase-side-track-2026-10-10.md)

---

**完。**
