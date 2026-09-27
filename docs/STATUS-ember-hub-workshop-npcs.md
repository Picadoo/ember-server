# STATUS · 枢纽工坊 NPC（烬砧 / 余晶 / 灰粮）

**日期：** 2026-09-27 21:25–21:34（Asia/Shanghai）  
**岗：** 余烬-插件岗执行器  
**依据：** `docs/design-ember-hub-workshop-npcs.md` · 总控纠偏（方案 A 锁死 · 余晶必开薄菜单）  
**制品：** CoreRpg **1.15.6** · `plugins/CoreRpg.jar`  
**Verdict：** **✅ 落地可验收**（三工坊 NPC 右键开菜单/引导；灰烛 talk 未改且仍通；ops=[]）

---

## 1. 根因 / 实现方式（Ady + hitbox）

| 点 | 说明 |
|----|------|
| 背景 | 工坊仅靠 TrMenu + 告示，无职能 NPC；灰烛已有 Ady 包实体，右键需 Bukkit hitbox 兜底（见 P1b） |
| 实现 | 新建 **`HubNpcService`**：与灰烛同级——**Adyeshach api+core 双钩** + **隐形 NoAI Nitwit 村民 hitbox**（`PlayerInteractEntityEvent`） |
| 元数据 | hitbox metadata `corerpg_hub_npc=<id>`（与灰烛 `corerpg_quest_npc` **分离**，互不抢事件） |
| 灰烛 | **未改** `QuestService` talk / 坐标 / quest.yml；本批不碰主线交互 |
| 配置 | `hub_npcs.yml`（resources + `plugins/CoreRpg/`） |
| 命令 | `/corerpg hubnpc [ensure\|reload\|list]`（admin） |
| 启动 | enable 后 hook + ensure；**+10s 再 ensure** 清掉区块晚加载残留 hitbox |

---

## 2. 三 NPC 坐标与动作

| id | 显示名 | 坐标 / yaw | 右键 |
|----|--------|------------|------|
| `ember_smith` | §6锻炉师 · 烬砧 | **(-11.5, 58, 105.5)** yaw **90** | tell 一句 → **`trmenu open ember_forge`**（**仅 forge**；强化/镶嵌不在右键分流） |
| `ember_enchanter` | §d咒火师 · 余晶 | **(-21.5, 58, 104.5)** yaw **270** | tell（含「**手持附魔晶点附魔台**」）→ **必开** `ember_enchant_guide`（禁只 tell；禁 `/enchant`） |
| `ember_quartermaster` | §a补给官 · 灰粮 | **(-18.5, 58, 114.5)** yaw **0** | tell 一句 → **`trmenu open ember_life`** |
| `ember_guide` | 引路人 · 灰烛 | (-16.5, 58, 106.5) | **不动**（QuestService） |

**方案 A（锁死）：** 烬砧右键 **只**开 `ember_forge`；`ember_forge.yml` 内按钮「§c去强化」→ `ember_enhance`、「§e去镶嵌」→ `ember_socket`。无潜行分流 / 无二次对话选项。

---

## 3. 菜单 / 告示 / lore

| 文件 | 变更 |
|------|------|
| `plugins/TrMenu/menus/ember_forge.yml` | 方案 A：布局加 `E`/`S` → 去强化 / 去镶嵌 |
| `plugins/TrMenu/menus/ember_enchant_guide.yml` | **新建**薄菜单：核心文案「手持附魔晶点附魔台」+ 打开强化 / 打开锻炉；禁 `/enchant` |
| `plugins/TrMenu/menus/ember_hub.yml` | 附魔 / 补给 / 锻炉 lore 加 `§8也可找工坊：烬砧 / 余晶 / 灰粮（右键）` |
| 锻炉告示 (-13,58,105) | **`锻炉师·烬砧 / 就在棚里 / 右键他 / 勿手打指令`**（`HubNpcService.updateForgeSign` + `HubPlazaService` 同步） |

未改：数值、NI id、票、挂机、灰烛 talk。

---

## 4. 自检（mineflayer · `/tmp/hub-workshop-npcs-*.json`）

| 检查 | 结果 |
|------|------|
| 右键烬砧 → tell + 窗「余烬锻炉 · 锻造」 | ✅ |
| 右键余晶 → tell 含「手持附魔晶点附魔台」+ 窗「咒火师 · 附魔引导」 | ✅（菜单 Open 再 tell 一次） |
| 右键灰粮 → tell + 窗「补给 · 生活」 | ✅ |
| 右键灰烛 → 主线 talk 仍通（「这城烧了三十年…」→ 挂机庭目标） | ✅ |
| forge 内去强化/去镶嵌 | ✅ YAML 落盘；同页按钮（mineflayer `displayName` 为原版名，以 YAML/布局为准） |
| 引导菜单无 `/enchant` 教学 | ✅ |
| 锻炉告示文案 | ✅ 读牌：`锻炉师·烬砧 / 就在棚里 / 右键他` |
| `ops.json` | ✅ **`[]`**（测后清空） |

日志要点：`CoreRpg 1.15.6 enabled` · `HubNpc: Adyeshach interact hooks active=2` · 三 NPC hitbox ensured。

---

## 5. STATUS 路径

`docs/STATUS-ember-hub-workshop-npcs.md`（本文件）

---

## 6. 风险 / 未做

1. **hitbox 叠层** — 多次 ensure / 区块晚加载曾导致同坐标多只隐形村民；已改「按 id 全图 purge」+ 启动后 10s 再 ensure。若仍叠，执行 `/corerpg hubnpc ensure`。  
2. **客户端双实体** — Ady 包实体 + Bukkit hitbox（与灰烛同）；玩家只见 Ady 名牌。  
3. **余晶站位** — (-21.5,58,104.5) 贴附魔台锚；若现场无附魔台方块，引导文案仍指向约 (-22,58,104)，不挡菜单验收。  
4. **未做** Citizens、改灰烛、教 `/corerpg` `/enchant` `/hub`、commit/push、数值/票/挂机。

---

## 7. 变更文件清单

| 路径 | 变更 |
|------|------|
| `CoreRpg/.../HubNpcService.java` | **新建** Ady+hitbox+分发 |
| `CoreRpg/.../CoreRpgPlugin.java` | 接线 · `hubnpc` · 延迟 ensure |
| `CoreRpg/.../HubPlazaService.java` | 锻炉告示文案 · hubbuild 后 ensure 工坊 NPC |
| `CoreRpg/src/main/resources/hub_npcs.yml` | **新建** |
| `CoreRpg/pom.xml` · `plugin.yml` | **1.15.5 → 1.15.6** |
| `plugins/CoreRpg.jar` · `plugins/CoreRpg/hub_npcs.yml` | 换入 |
| `plugins/TrMenu/menus/ember_forge.yml` | 方案 A 跳转 |
| `plugins/TrMenu/menus/ember_enchant_guide.yml` | **新建** |
| `plugins/TrMenu/menus/ember_hub.yml` | lore 一行 |
| `docs/STATUS-ember-hub-workshop-npcs.md` | 本文件 |
