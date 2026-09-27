# STATUS · 枢纽门吏 · 灰钥（日常场景锚）

**日期：** 2026-09-28 06:16（Asia/Shanghai）  
**岗：** 余烬-插件  
**依据：** `docs/design-ember-hub-dungeon-clerk.md`（已批准）；总控【派工 · 门吏灰钥落地】  
**Verdict：** **✅ 落地**：`ember_dungeon_clerk` 已入 `hub_npcs.yml` 并 live ensure；`ember_hub` 日常图标 lore 已指路；**未改 Java / 体力数值**；无 Citizens；ops=`[]`；未 commit/push

---

## 一句话

广场出生点正东现有 **门吏 · 灰钥**：右键 tell（体力 + 七线）→ 开 TrMenu `ember_daily`。枢纽菜单日常图标补一行「也可找广场：门吏·灰钥」。可选身旁告示因控制台不在 `ember_hub` 未落（非阻塞；NPC 本身即场景锚）。

---

## 1. 交付对照

| 项 | 结果 |
|----|------|
| `plugins/CoreRpg/hub_npcs.yml` + `CoreRpg/src/main/resources/hub_npcs.yml` | ✅ 同源增 `ember_dungeon_clerk` |
| name / 坐标 / yaw | ✅ `§e门吏 · 灰钥` · ember_hub **(-14.5,58.0,110.5)** yaw **90** |
| action / menu | ✅ `action: daily`（**非 forge**，避免锁 `ember_forge`）+ `menu: ember_daily` + `open_menu: true` |
| tell §2.1 | ✅ 体力语义 + 七线一句 + 点开选本 |
| Java | ✅ **未改**（HubNpcService 已按 yaml `menu` 开 TrMenu） |
| `ember_hub.yml` H lore | ✅ `§8也可找广场：门吏·灰钥（右键）` |
| 身旁告示 §4 | ⚠ **未落**（控制台 `setblock` 报 outside world；无在线玩家可 `execute`；非验收硬条） |
| 灰烛 / 工坊三岗 | ✅ list 仍见 smith/enchanter/quartermaster；ensure 未动其配置 |
| jar / 版本 | ✅ **未 bump**（纯 YAML） |
| Citizens | ✅ 未引入 |
| 体力上限/日切/单次 30 | ✅ 未改 |

---

## 2. Live ensure

| 步骤 | 证据 |
|------|------|
| `corerpg hubnpc reload` | `HubNpc: loaded **4** workshop NPCs` · `created Adyeshach NPC ember_dungeon_clerk` · hitbox villager @ -14.5,58.0,110.5 |
| `corerpg hubnpc list` | `ember_dungeon_clerk 门吏 · 灰钥 … yaw=90.0 action=daily menu=ember_daily` |
| TrMenu | `ember_hub.yml` 自动重载；`trmenu reload` 31 菜单 |
| `ops.json` | **`[]`** |

---

## 3. `hub_npcs` 字段（落盘）

```yaml
  ember_dungeon_clerk:
    enabled: true
    id: ember_dungeon_clerk
    name: '§e门吏 · 灰钥'
    world: ember_hub
    x: -14.5
    y: 58.0
    z: 110.5
    yaw: 90.0
    action: daily
    tell: |-
      §e灰钥：§f日常窟靠体力进。顶栏能看见还剩多少，一条扣 30，0 点回满。
      §7庭院、焦骨、地窖、潮蚀、断塔、霜晶、锈轨——点菜单选一条。
      §e灰钥：§f点开选本。不够体力就改天或用药。
    menu: ember_daily
    open_menu: true
```

---

## 4. 改动文件

| 路径 | 变更 |
|------|------|
| `plugins/CoreRpg/hub_npcs.yml` | +灰钥 |
| `CoreRpg/src/main/resources/hub_npcs.yml` | 同源 |
| `plugins/TrMenu/menus/ember_hub.yml` | H lore +灰钥指路 |
| `docs/STATUS-ember-hub-dungeon-clerk.md` | 本文件 |

**未 commit/push。**

---

## 5. 给总控 · 结案转发

```
【结案 · 门吏灰钥落地】priority=true
岗：余烬-插件
交付：hub_npcs + resources 增 ember_dungeon_clerk；名「门吏 · 灰钥」；ember_hub (-14.5,58,110.5) yaw90
action=daily + menu=ember_daily + open_menu（未写 forge，未改 Java）
tell 按稿 §2.1；ember_hub H lore +「也可找广场：门吏·灰钥（右键）」
live：hubnpc reload → n=4 · Ady+hitbox 已建；list 见 action=daily menu=ember_daily
可选告示未落（控制台 outside world）；灰烛/工坊三岗配置未动；未 bump jar；ops=[]；未 commit/push
请派测：出生点东侧见灰钥 → 右键 tell+开 ember_daily；工坊三岗回归。
```
