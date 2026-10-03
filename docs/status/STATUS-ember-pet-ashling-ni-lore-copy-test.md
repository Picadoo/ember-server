# STATUS · B2.38 NI 余烬灰灵 lore 批 A · 轻测

**日期：** 2026-09-29 03:40 → 03:40 Asia/Shanghai（CST）  
**岗：** 余烬-测试岗执行器  
**依据：** 设计 `965ab2b` · 批准 `bbded5f` · 施工 tip `7dbf09c`  
**范围：** `plugins/NeigeItems/Items/ember-pets.yml` 内 `pet_ember_ashling` lore 已删裸 id 行；键名/name「余烬灰灵」/两行说明/`/corerpg pet summon`/enchantments/hideflags 应在；live Items `rg '&7pet_'`（排除 bak）计数=**1**（仅 `pet_ember_cinder`）  
**禁项：** **禁改配置** · 禁 wall-clock/DPS/挑刺 · **不宣称** 另一 pet / 斜杠 / B0.1 / soft 已清 · 勿带 dirty runtime · **只报本件 ashling**  
**Verdict：** ✅ **PASS**（验收点全过 · ops=`[]` · `&7pet_` rg=**1**/仅 cinder · 悬停 **PASS**）

---

## 一句话

余烬灰灵 lore 列表无字面 `pet_ember_ashling`（键名保留）；name 仍「余烬灰灵」、material=`MONSTER_EGG`、说明「使魔蛋 · 外观 / 微光」「出战 1 只 · 不卖满级战力」、**`/corerpg pet summon`** 未漂；`ni reload` 后 `/ni give … pet_ember_ashling` 背包悬停无灰字裸 id、summon 行仍在；tip 仅删 1 行；live Items `rg '&7pet_'`（`!*.bak*`）=**1**（仅 `pet_ember_cinder`）；**不宣称** cinder/斜杠/B0.1/soft 已清；ops=`[]`。

---

## 总评

| # | 验收点 | 结果 |
|---|--------|------|
| 1 | lore/`git show`：无字面 `&7pet_ember_ashling` / `pet_ember_ashling`（键名可留）；仅删裸 id；name/说明/summon 保留 | **PASS** |
| 2 | live `rg -n '&7pet_' plugins/NeigeItems/Items/ --glob '!*.bak*'` **计数=1**（仅 `pet_ember_cinder`） | **PASS**（计数=1 · 命中 cinder） |
| 3 | 悬停轻测：给 `pet_ember_ashling` → 见「余烬灰灵」+ 两行说明 + summon · **不见**灰字裸 id | **PASS** |
| 4 | 不宣称另一 pet / 斜杠 / B0.1 / soft；不叫挑刺；禁 wall-clock/DPS | **PASS** |
| 5 | 写本测报 · commit+push · ops=`[]` | **PASS**（本 tip） |

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 口 | proxy 25565 / login 25566 / play 25567 |
| 施工 tip | `7dbf09c` fix(ni): B2.38 drop bare id from pet_ember_ashling lore |
| 设计 / 批准 | `965ab2b` / `bbded5f` |
| 热更 | 测前 `ni reload` → `NeigeItems > 重载完毕` @ 03:40:05 CST |
| 账号 | 验收 `NiLnjdd2`（非 OP）· 辅助 `NiLonjdd2`（临时 OP，已 deop + LP unset） |
| 探针 | `/tmp/b238-pet-ashling-ni-lore-copy-test.js` · JSON `/tmp/b238-pet-ashling-ni-lore-copy-test.json`（未入 git） |
| UX | 自定义物走 NI ID `pet_ember_ashling`；悬停=背包 display lore；保留斜杠 `/corerpg pet summon` |

---

## 各点证据

### 1 · lore 列表无裸 id · tip 仅删 1 行

```
rg -n 'pet_ember_ashling' plugins/NeigeItems/Items/ember-pets.yml
→ L4 pet_ember_ashling:   （键名 · 管理侧可留）
```

lore 块现网：

```
  lore:
    - '&8使魔蛋 · 外观 / 微光'
    - '&8出战 1 只 · 不卖满级战力'
    - '&7用于 /corerpg pet summon'
```

字面 `&7pet_ember_ashling` / `pet_ember_ashling`（lore 内）：**0**（已删）。

`git show 7dbf09c` hunk：

```
@@ -5,7 +5,6 @@ pet_ember_ashling:
   material: MONSTER_EGG
   name: '&a余烬灰灵'
   lore:
-    - '&7pet_ember_ashling'
     - '&8使魔蛋 · 外观 / 微光'
     - '&8出战 1 只 · 不卖满级战力'
     - '&7用于 /corerpg pet summon'
```

| 项 | 现网 |
|----|------|
| name | `&a余烬灰灵` |
| material | `MONSTER_EGG` |
| lore×3 | 使魔蛋 · 外观 / 微光；出战 1 只 · 不卖满级战力；用于 `/corerpg pet summon` |
| enchantments | `DURABILITY: 1`（未漂） |
| hideflags | `HIDE_ENCHANTS`（未漂） |
| tip 文件 | **仅** `plugins/NeigeItems/Items/ember-pets.yml`（−1 行） |
| pet_ember_cinder | tip **未改**；现网仍留 `&7pet_ember_cinder`（旁附 soft · **本岗不宣称已清**） |

### 2 · live `&7pet_` rg 计数=1（仅 cinder）

```
rg -n '&7pet_' plugins/NeigeItems/Items/ --glob '!*.bak*'
→ plugins/NeigeItems/Items/ember-pets.yml:20:    - '&7pet_ember_cinder'
计数 = 1
```

**仅本轨 live 宣称**（本件 ashling 去裸 id · `&7pet_` 剩 cinder=1）；**不宣称** cinder / 斜杠 / soft / B0.1 已清。

### 3 · NI give + 悬停轻测

| 步骤 | 结果 |
|------|------|
| `ni reload` | `NeigeItems > 重载完毕` @ 03:40:05 CST |
| OP `/ni give NiLnjdd2 pet_ember_ashling 1` | chat：`NeigeItems > 你得到了 1 个 余烬灰灵` |
| 背包 NBT | name=`余烬灰灵` · mat=`spawn_egg` · lore=`[使魔蛋 · 外观 / 微光, 出战 1 只 · 不卖满级战力, 用于 /corerpg pet summon]` |
| 裸 id | lore **无** `pet_ember_ashling` |
| summon | lore **保留** `/corerpg pet summon` |
| 悬停 | **PASS**（非 SKIP） |

### 4 · 禁项 / 未漂

| 项 | 证据 |
|----|------|
| tip 范围 | `7dbf09c` **仅** pet_ember_ashling lore 删 1 行裸 id |
| 说明行 / summon | **保留**（未误删） |
| pet_ember_cinder | tip **未动**；仍留 `&7pet_ember_cinder`（**不宣称已清**） |
| 配方 / 给物逻辑 / TrMenu / CoreRpg Java | tip **未碰** |
| enchantments / hideflags | tip **未改**；现网仍 DURABILITY:1 / HIDE_ENCHANTS |
| 另一 pet / 斜杠 / B0.1 / soft / 附 B / 精英壳 / 挑刺 / wall-clock/DPS | **本岗未宣称、未开、未测** |
| 本岗改配置 | **否**（只读 + 热更/给物/OP 生命周期；测报 docs only） |

### 5 · ops

测后 `server-runtime/ops.json` = `[]` · `login-runtime/ops.json` = `[]`；临时 OP `NiLonjdd2` 已 `deop`；LP `neigeitems.admin` / `neigeitems.*` 已 unset。

---

## Verdict

✅ **PASS · 勾销候选**（总控结案另开；本 tip 仅测报）。

悬停：**PASS** · `&7pet_` 计数=**1**（命中 `pet_ember_cinder`）· ops=`[]`。
