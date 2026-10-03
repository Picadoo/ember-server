# STATUS · 余烬深渊 / 灾厄

**日期：** 2026-09-13（Asia/Shanghai）  
**正式规格（冲层+撤离）：** `docs/ember-abyss-evacuate-spec.md`  
**实现简记：** `docs/design/DESIGN-ember-abyss-evacuate.md`  
**策划表：** `docs/ember-abyss-calamity.md`（文案 / 档位表）  
**总纲：** `docs/design/DESIGN-ember-rpg-systems.md` §4  
**约束：** 未改 Paper NMS；YAML / TrMenu / console 命令接线

---

## 如何开始 · 深渊

1. 持有 **余烬深渊票** ×1（`ticket_ember_abyss`）  
   - 测试：`/ni give <玩家> ticket_ember_abyss 1`
2. `/ember` → **深渊** → **开始下潜**，或 `/dp start EmberAbyss`
3. 进本即扣票；**撤离 / 超时 / 失败均不返还**。日免费 1（硬顶建议 ≤2）。
4. 本内随时：**菜单「上浮撤离」** 或 `/corerpg abyss evacuate` → 按本局最高层结算一次。

## 如何开始 · 灾厄

1. 窗口内（草案 12:00 / 20:00 / 22:00 Asia/Shanghai）：  
   `/ember` → **灾厄** → **奔赴灾厄**，或 `/dp start EmberCalamity`
2. 场地占位 TP：菜单「传送灾厄场地」→ `-40 65 270`
3. 备用控制台刷怪：`mm m spawn EmberCalamityBoss 1 world,-40,65,270`
4. 每日宝箱限 1 次 — **调度/限领未接线**（`CoreRpg/calamity.yml` `enabled: false`）

重载：

```
/dp reload
/ni reload
/trmenu reload
/mm reload
```

---

## 深渊 EmberAbyss（冲层 + 撤离 · YAML 已接）

| 项 | 值 |
|----|-----|
| 地图 | `ember_abyss` · 出生 `-40,65,270` |
| 人数 / 复活 / 时限 | 1～2 / 2 / 1500s |
| 结构 | **8 层 stub**（floor5 清完续 floor6；顶层 floor8 → COMPLETE）；无尽语义靠 progress + 撤离/超时 settle |
| MM / `$kill` | Display 现网：`余烬深渊·潮尸/混潮/骨潮/蛮层/看守` |
| 进本 | `corerpg abyss progress <player> 0` |
| 清层 | `corerpg abyss progress <player> N`（N=1..8）+ 撤离提示行 |
| 结算 | `corerpg abyss settle <player>` — 档位 1～4 / 5～9 / 10～14…（见正式规格）；`recordAbyssFloor` |
| 撤离 | `/corerpg abyss evacuate`（TrMenu 键 E）· 票不退 |
| 超时 | settle → `$end reward=false`（强制结算最高层，防双箱） |
| COMPLETE 额外 | T2 刃 + T2 护符（材料/孔石只由 settle） |

**插件命令：** 现网 CoreRpg jar **1.4.5** 可能尚无 `progress/settle/evacuate` 子命令；YAML/菜单已接线，**待 1.4.6+ jar** 落地后命令生效。未知命令时控制台会报错，不影响 DP 层推进。

战斗掉落见 `MythicMobs/Mobs/EmberAbyss.yml`（与结算箱叠加）。

---

## 灾厄 EmberCalamity（本期 stub · 未改）

| 项 | 值 |
|----|-----|
| DP | `/dp start EmberCalamity` · 人数 1～5 · 时限 600s |
| MM Boss | `EmberCalamityBoss`（Display：余烬灾厄使） |
| 通关箱 | 表 2.3-B：核心×2 + 附魔晶×1 + `mat_calamity_ember`×2 |
| 参战掉落 | MM `EmberCalamity.yml`（孔石 / 凝核 / 核心等） |
| 新 NI | `mat_calamity_ember` / `cosmetic_calamity_shard`（`NeigeItems/Items/ember-abyss-calamity.yml`） |

---

## 路径清单

| 路径 | 说明 |
|------|------|
| `docs/ember-abyss-evacuate-spec.md` | **冲层+撤离正式规格 / 验收** |
| `docs/design/DESIGN-ember-abyss-evacuate.md` | 本刀实现简记 |
| `docs/ember-abyss-calamity.md` | 策划主表 / 档位文案 |
| `plugins/DungeonPlus/dungeon/EmberAbyss/*` | 深渊 DP（与 server-runtime 同 symlink） |
| `plugins/TrMenu/menus/ember_abyss.yml` | 开始下潜 + **上浮撤离** |
| `plugins/NeigeItems/Items/ember-dungeon-tickets.yml` | `ticket_ember_abyss` |
| `plugins/MythicMobs/Mobs/EmberAbyss.yml` | 深渊怪 |

---

## 验收对照（evacuate-spec §6）

| # | 标准 | 本期 |
|---|------|------|
| 1 | 扣票进本；无票拒进 | **是**（option.js-condition） |
| 2 | 可打过第 5 层进第 6 层 | **是**（floor5→floor6；非固定 5 层通关） |
| 3 | 中途撤离 → 按最高层档发箱；票不回 | **YAML 已接 evacuate/settle**；待 jar |
| 4 | 同一局不第二次结算 | **依赖 jar settled 标志** |
| 5 | 天梯 `recordAbyssFloor` | **依赖 jar settle** |
| 6 | 超时同样 settle（floor≥1） | **是**（timeout.yml settle + reward=false） |
| 7 | 不破坏日/周本 | **是**（独立目录） |

菜单无「即将点燃」、可 `dp start`：**是**。测前请 `/dp reload` + `/trmenu reload`。
