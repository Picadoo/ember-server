# STATUS · B2.43 NI 余烬烬火 lore summon 去斜杠 · 轻测

**日期：** 2026-09-29 04:06 → 04:06 Asia/Shanghai（CST）  
**岗：** 余烬-测试岗执行器  
**依据：** 设计 `e8d3704` · 批准 `2d2fcf8` · 施工 tip `94ad273`  
**范围：** `plugins/NeigeItems/Items/ember-pets.yml` 内 `pet_ember_cinder` lore L22 已改为「用于枢纽 · 使魔」；L20/L21/name 保留；`pet_ember_ashling` 枢纽文案 / `mat_ember_soul_dust` feed 斜杠未动  
**禁项：** **禁改配置** · 禁 wall-clock/DPS/挑刺 · **不宣称其它斜杠 / B0.1 / soft 已清** · 勿带 dirty runtime · 不扩测其它斜杠  
**Verdict：** ✅ **PASS**（验收点全过 · ops=`[]` · 悬停 PASS）

---

## 一句话

余烬烬火 lore **无**字面 `/corerpg`；L22「用于枢纽 · 使魔」· L20/L21 仍在；`ni reload` 后 `/ni give … pet_ember_cinder` 背包悬停见枢纽文案、无斜杠；旁证 `pet_ember_ashling` 仍「用于枢纽 · 使魔」、`mat_ember_soul_dust` L34 仍 feed 斜杠；tip 仅换 1 行；ops=`[]`。

---

## 总评

| # | 验收点 | 结果 |
|---|--------|------|
| 1 | `rg`：该件 lore **无**字面 `/corerpg`；L22=`&7用于枢纽 · 使魔`；L20/L21 仍在 | **PASS** |
| 2 | 悬停：给物见枢纽文案、无斜杠 | **PASS** |
| 3 | 旁证勿改：`pet_ember_ashling` 仍「用于枢纽 · 使魔」；`mat_ember_soul_dust` L34 仍 feed 斜杠 | **PASS** |
| 4 | 不宣称其它斜杠 / B0.1；不叫挑刺；禁 wall-clock/DPS | **PASS** |
| 5 | 写本测报 · commit+push · ops=`[]` | **PASS**（本 tip） |

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 口 | proxy 25565 / login 25566 / play 25567 |
| 施工 tip | `94ad273` fix(ni): B2.43 cinder summon lore drop /corerpg slash |
| 设计 / 批准 | `e8d3704` / `2d2fcf8` |
| 热更 | 测前 `ni reload` → `NeigeItems > 重载完毕` @ 04:06:18 CST |
| 账号 | 验收 `NiLoh3fo`（非 OP）· 辅助 `NiLooh3fo`（临时 OP，已 deop + LP unset） |
| 探针 | `/tmp/b243-pet-cinder-summon-slash-copy-test.js` · JSON `/tmp/b243-pet-cinder-summon-slash-copy-test.json`（未入 git） |
| UX | 自定义物走 NI ID `pet_ember_cinder`；悬停=背包 display lore |

---

## 各点证据

### 1 · 该件 lore 无字面 `/corerpg` · L22 枢纽 · L20/L21 仍在

```
rg -n 'pet_ember_cinder|/corerpg|用于枢纽 · 使魔' plugins/NeigeItems/Items/ember-pets.yml
→ L10 - '&7用于枢纽 · 使魔'          （ashling · 旁证）
→ L16  pet_ember_cinder:   （键名）
→ L22 - '&7用于枢纽 · 使魔'
→ L34 - '&a喂使魔：/corerpg pet feed'  （soul_dust · 旁证）
```

`pet_ember_cinder` lore 块现网：

```
  lore:
    - '&8使魔蛋 · 外观 / 余火'
    - '&8出战 1 只 · 不卖满级战力'
    - '&7用于枢纽 · 使魔'
```

cinder lore 列表字面 `/corerpg`：**0**。

| 项 | 现网 |
|----|------|
| name | `&6余烬烬火` |
| material | `MONSTER_EGG` |
| L20 | `&8使魔蛋 · 外观 / 余火` |
| L21 | `&8出战 1 只 · 不卖满级战力` |
| L22 | `&7用于枢纽 · 使魔` |
| enchantments / hideflags | tip **零改**（DURABILITY:1 / HIDE_ENCHANTS） |

`git show 94ad273` hunk：

```
-    - '&7用于 /corerpg pet summon'
+    - '&7用于枢纽 · 使魔'
```

tip 文件：**仅** `plugins/NeigeItems/Items/ember-pets.yml`（1 行替换）。

### 2 · NI give + 悬停轻测

| 步骤 | 结果 |
|------|------|
| `ni reload` | `NeigeItems > 重载完毕` @ 04:06:18 CST |
| OP `/ni give NiLoh3fo pet_ember_cinder 1` | chat：`NeigeItems > 你得到了 1 个 余烬烬火` |
| 背包 NBT | name=`余烬烬火` · mat=`spawn_egg` · lore=`[使魔蛋 · 外观 / 余火, 出战 1 只 · 不卖满级战力, 用于枢纽 · 使魔]` |
| 斜杠 | lore **无** `/corerpg` |
| 枢纽文案 | lore **有**「用于枢纽 · 使魔」 |
| 悬停 | **PASS**（非 SKIP） |

### 3 · 旁证勿改

| 旁证 | 现网 | 结果 |
|------|------|------|
| `pet_ember_ashling` L10 | `- '&7用于枢纽 · 使魔'` | **仍在**（预期） |
| `mat_ember_soul_dust` L34 | `- '&a喂使魔：/corerpg pet feed'` | **仍在**（预期） |

**本岗不宣称** 其它斜杠 / B0.1 / soft 已清。

### 4 · 禁项 / 未漂

| 项 | 证据 |
|----|------|
| tip 范围 | `94ad273` **仅** cinder L22 一行替换 |
| ashling / soul_dust / 其它斜杠 / B0.1 / soft | **本岗未宣称、未开、未清** |
| 本岗改配置 | **否**（只读 + 热更/给物/OP 生命周期；测报 docs only） |
| wall-clock / DPS / 挑刺 | **未做** |

### 5 · ops

测后 `server-runtime/ops.json` = `[]` · `login-runtime/ops.json` = `[]`；临时 OP `NiLooh3fo` 已 `deop`；LP `neigeitems.admin` / `neigeitems.*` 已 unset。

---

## Verdict

✅ **PASS · 勾销候选**（总控结案另开；本 tip 仅测报）。

悬停：**PASS** · 旁证 ashling 枢纽 / soul_dust feed **仍在** · ops=`[]`。
