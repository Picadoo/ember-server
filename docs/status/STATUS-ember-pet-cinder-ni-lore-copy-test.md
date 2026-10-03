# STATUS · B2.39 NI 余烬烬火 lore 批 A · 轻测

**日期：** 2026-09-29 03:45 → 03:45 Asia/Shanghai（CST）  
**岗：** 余烬-测试岗执行器  
**依据：** 设计 `88c53bd` · 批准 `8e2ef70` · 施工 tip `6e08d32`  
**范围：** `plugins/NeigeItems/Items/ember-pets.yml` 内 `pet_ember_cinder` lore 已删裸 id 行；键名/name「余烬烬火」/两行说明/`/corerpg pet summon`/enchantments/hideflags 应在；live Items `rg '&7pet_'`（排除 bak）计数=**0**（pet 本轨归零）  
**禁项：** **禁改配置** · 禁 wall-clock/DPS/挑刺 · **不宣称** 斜杠 / B0.1 / soft 已清 · 勿回改 ashling · 勿带 dirty runtime · **只报本件 cinder + pet 本轨归零**  
**Verdict：** ✅ **PASS**（验收点全过 · ops=`[]` · `&7pet_` rg=**0** · 悬停 **PASS**）

---

## 一句话

余烬烬火 lore 列表无字面 `pet_ember_cinder`（键名保留）；name 仍「余烬烬火」、material=`MONSTER_EGG`、说明「使魔蛋 · 外观 / 余火」「出战 1 只 · 不卖满级战力」、**`/corerpg pet summon`** 未漂；`ni reload` 后 `/ni give … pet_ember_cinder` 背包悬停无灰字裸 id、summon 行仍在；tip 仅删 1 行；live Items `rg '&7pet_'`（`!*.bak*`）=**0**（pet 本轨归零）；ashling **未回改**；**不宣称** 斜杠/B0.1/soft 已清；ops=`[]`。

---

## 总评

| # | 验收点 | 结果 |
|---|--------|------|
| 1 | lore/`git show`：无字面 `&7pet_ember_cinder` / `pet_ember_cinder`（键名可留）；仅删裸 id；name/说明/summon 保留 | **PASS** |
| 2 | live `rg -n '&7pet_' plugins/NeigeItems/Items/ --glob '!*.bak*'` **计数=0**（pet 本轨归零） | **PASS**（计数=0） |
| 3 | 悬停轻测：给 `pet_ember_cinder` → 见「余烬烬火」+ 两行说明 + summon · **不见**灰字裸 id | **PASS** |
| 4 | 不宣称斜杠 / B0.1 / soft；不回改 ashling；不叫挑刺；禁 wall-clock/DPS | **PASS** |
| 5 | 写本测报 · commit+push · ops=`[]` | **PASS**（本 tip） |

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 口 | proxy 25565 / login 25566 / play 25567 |
| 施工 tip | `6e08d32` fix(ni): B2.39 drop bare id from pet_ember_cinder lore |
| 设计 / 批准 | `88c53bd` / `8e2ef70` |
| 热更 | 测前 `ni reload` → `NeigeItems > 重载完毕` @ 03:45:13 CST |
| 账号 | 验收 `NiLnpzcw`（非 OP）· 辅助 `NiLonpzcw`（临时 OP，已 deop + LP unset） |
| 探针 | `/tmp/b239-pet-cinder-ni-lore-copy-test.js` · JSON `/tmp/b239-pet-cinder-ni-lore-copy-test.json`（未入 git） |
| UX | 自定义物走 NI ID `pet_ember_cinder`；悬停=背包 display lore；保留斜杠 `/corerpg pet summon` |

---

## 各点证据

### 1 · lore 列表无裸 id · tip 仅删 1 行

```
rg -n 'pet_ember_cinder' plugins/NeigeItems/Items/ember-pets.yml
→ L16 pet_ember_cinder:   （键名 · 管理侧可留）
```

lore 块现网：

```
  lore:
    - '&8使魔蛋 · 外观 / 余火'
    - '&8出战 1 只 · 不卖满级战力'
    - '&7用于 /corerpg pet summon'
```

字面 `&7pet_ember_cinder` / `pet_ember_cinder`（lore 内）：**0**（已删）。

`git show 6e08d32` hunk：

```
@@ -17,7 +17,6 @@ pet_ember_cinder:
   material: MONSTER_EGG
   name: '&6余烬烬火'
   lore:
-    - '&7pet_ember_cinder'
     - '&8使魔蛋 · 外观 / 余火'
     - '&8出战 1 只 · 不卖满级战力'
     - '&7用于 /corerpg pet summon'
```

| 项 | 现网 |
|----|------|
| name | `&6余烬烬火` |
| material | `MONSTER_EGG` |
| lore×3 | 使魔蛋 · 外观 / 余火；出战 1 只 · 不卖满级战力；用于 `/corerpg pet summon` |
| enchantments | `DURABILITY: 1`（未漂） |
| hideflags | `HIDE_ENCHANTS`（未漂） |
| tip 文件 | **仅** `plugins/NeigeItems/Items/ember-pets.yml`（−1 行） |
| pet_ember_ashling | tip **未改**；现网仍无 `&7pet_ember_ashling`（B2.38 已清 · **本岗未回改**） |

### 2 · live `&7pet_` rg 计数=0（pet 本轨归零）

```
rg -n '&7pet_' plugins/NeigeItems/Items/ --glob '!*.bak*'
→ （无输出 · exit 1）
计数 = 0
```

**仅本轨 live 宣称**（本件 cinder 去裸 id · pet 本轨 `&7pet_`=0）；**不宣称** 斜杠 / soft / B0.1 已清。

### 3 · NI give + 悬停轻测

| 步骤 | 结果 |
|------|------|
| `ni reload` | `NeigeItems > 重载完毕` @ 03:45:13 CST |
| OP `/ni give NiLnpzcw pet_ember_cinder 1` | chat：`NeigeItems > 你得到了 1 个 余烬烬火` |
| 背包 NBT | name=`余烬烬火` · mat=`spawn_egg` · lore=`[使魔蛋 · 外观 / 余火, 出战 1 只 · 不卖满级战力, 用于 /corerpg pet summon]` |
| 裸 id | lore **无** `pet_ember_cinder` |
| summon | lore **保留** `/corerpg pet summon` |
| 悬停 | **PASS**（非 SKIP） |

### 4 · 禁项 / 未漂

| 项 | 证据 |
|----|------|
| tip 范围 | `6e08d32` **仅** pet_ember_cinder lore 删 1 行裸 id |
| 说明行 / summon | **保留**（未误删） |
| pet_ember_ashling | tip **未动**；无裸 id 回加（**未回改**） |
| 配方 / 给物逻辑 / TrMenu / CoreRpg Java | tip **未碰** |
| enchantments / hideflags | tip **未改**；现网仍 DURABILITY:1 / HIDE_ENCHANTS |
| 斜杠 / B0.1 / soft / 附 B / 精英壳 / 挑刺 / wall-clock/DPS | **本岗未宣称、未开、未测** |
| 本岗改配置 | **否**（只读 + 热更/给物/OP 生命周期；测报 docs only） |

### 5 · ops

测后 `server-runtime/ops.json` = `[]` · `login-runtime/ops.json` = `[]`；临时 OP `NiLonpzcw` 已 `deop`；LP `neigeitems.admin` / `neigeitems.*` 已 unset。

---

## Verdict

✅ **PASS · 勾销候选**（总控结案另开；本 tip 仅测报）。

悬停：**PASS** · `&7pet_` 计数=**0**（pet 本轨归零）· ops=`[]`。
