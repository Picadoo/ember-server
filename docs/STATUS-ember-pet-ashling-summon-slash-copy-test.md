# STATUS · B2.42 NI 余烬灰灵 lore summon 去斜杠 · 轻测

**日期：** 2026-09-29 04:01 → 04:02 Asia/Shanghai（CST）  
**岗：** 余烬-测试岗执行器  
**依据：** 设计 `696c80c` · 批准 `48dc3d1` · 施工 tip `1dbcb88`  
**范围：** `plugins/NeigeItems/Items/ember-pets.yml` 内 `pet_ember_ashling` lore L10 已改为「用于枢纽 · 使魔」；L8/L9/name 保留；`pet_ember_cinder` summon / `mat_ember_soul_dust` feed 斜杠未动  
**禁项：** **禁改配置** · 禁 wall-clock/DPS/挑刺 · **不宣称其它斜杠 / B0.1 / soft 已清** · 勿带 dirty runtime · 不扩测 cinder/soul_dust 去斜杠  
**Verdict：** ✅ **PASS**（验收点全过 · ops=`[]` · 悬停 PASS）

---

## 一句话

余烬灰灵 lore **无**字面 `/corerpg`；L10「用于枢纽 · 使魔」· L8/L9 仍在；`ni reload` 后 `/ni give … pet_ember_ashling` 背包悬停见枢纽文案、无斜杠；旁证 `pet_ember_cinder` L22 仍 `/corerpg pet summon`、`mat_ember_soul_dust` L34 仍 feed 斜杠；tip 仅换 1 行；ops=`[]`。

---

## 总评

| # | 验收点 | 结果 |
|---|--------|------|
| 1 | `rg`：该件 lore **无**字面 `/corerpg`；L10=`&7用于枢纽 · 使魔`；L8/L9 仍在 | **PASS** |
| 2 | 悬停：给物见枢纽文案、无斜杠 | **PASS** |
| 3 | 旁证勿改：`pet_ember_cinder` L22 仍 `/corerpg pet summon`；`mat_ember_soul_dust` L34 仍 feed 斜杠 | **PASS** |
| 4 | 不宣称其它斜杠 / B0.1；不叫挑刺；禁 wall-clock/DPS | **PASS** |
| 5 | 写本测报 · commit+push · ops=`[]` | **PASS**（本 tip） |

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 口 | proxy 25565 / login 25566 / play 25567 |
| 施工 tip | `1dbcb88` fix(ni): B2.42 ashling summon lore drop /corerpg slash |
| 设计 / 批准 | `696c80c` / `48dc3d1` |
| 热更 | 测前 `ni reload` → `NeigeItems > 重载完毕` @ 04:01:41 CST |
| 账号 | 验收 `NiLob5rp`（非 OP）· 辅助 `NiLoob5rp`（临时 OP，已 deop + LP unset） |
| 探针 | `/tmp/b242-pet-ashling-summon-slash-copy-test.js` · JSON `/tmp/b242-pet-ashling-summon-slash-copy-test.json`（未入 git） |
| UX | 自定义物走 NI ID `pet_ember_ashling`；悬停=背包 display lore |

---

## 各点证据

### 1 · 该件 lore 无字面 `/corerpg` · L10 枢纽 · L8/L9 仍在

```
rg -n 'pet_ember_ashling|/corerpg|用于枢纽 · 使魔' plugins/NeigeItems/Items/ember-pets.yml
→ L4  pet_ember_ashling:   （键名）
→ L10 - '&7用于枢纽 · 使魔'
→ L22 - '&7用于 /corerpg pet summon'   （cinder · 旁证）
→ L34 - '&a喂使魔：/corerpg pet feed'  （soul_dust · 旁证）
```

`pet_ember_ashling` lore 块现网：

```
  lore:
    - '&8使魔蛋 · 外观 / 微光'
    - '&8出战 1 只 · 不卖满级战力'
    - '&7用于枢纽 · 使魔'
```

ashling lore 列表字面 `/corerpg`：**0**。

| 项 | 现网 |
|----|------|
| name | `&a余烬灰灵` |
| material | `MONSTER_EGG` |
| L8 | `&8使魔蛋 · 外观 / 微光` |
| L9 | `&8出战 1 只 · 不卖满级战力` |
| L10 | `&7用于枢纽 · 使魔` |
| enchantments / hideflags | tip **零改**（DURABILITY:1 / HIDE_ENCHANTS） |

`git show 1dbcb88` hunk：

```
-    - '&7用于 /corerpg pet summon'
+    - '&7用于枢纽 · 使魔'
```

tip 文件：**仅** `plugins/NeigeItems/Items/ember-pets.yml`（1 行替换）。

### 2 · NI give + 悬停轻测

| 步骤 | 结果 |
|------|------|
| `ni reload` | `NeigeItems > 重载完毕` @ 04:01:41 CST |
| OP `/ni give NiLob5rp pet_ember_ashling 1` | chat：`NeigeItems > 你得到了 1 个 余烬灰灵` |
| 背包 NBT | name=`余烬灰灵` · mat=`spawn_egg` · lore=`[使魔蛋 · 外观 / 微光, 出战 1 只 · 不卖满级战力, 用于枢纽 · 使魔]` |
| 斜杠 | lore **无** `/corerpg` |
| 枢纽文案 | lore **有**「用于枢纽 · 使魔」 |
| 悬停 | **PASS**（非 SKIP） |

### 3 · 旁证勿改

| 旁证 | 现网 | 结果 |
|------|------|------|
| `pet_ember_cinder` L22 | `- '&7用于 /corerpg pet summon'` | **仍在**（预期） |
| `mat_ember_soul_dust` L34 | `- '&a喂使魔：/corerpg pet feed'` | **仍在**（预期） |

**本岗不宣称** cinder/soul_dust/其它斜杠/B0.1 已清。

### 4 · 禁项 / 未漂

| 项 | 证据 |
|----|------|
| tip 范围 | `1dbcb88` **仅** ashling L10 一行替换 |
| cinder / soul_dust / 其它斜杠 / B0.1 / soft | **本岗未宣称、未开、未清** |
| 本岗改配置 | **否**（只读 + 热更/给物/OP 生命周期；测报 docs only） |
| wall-clock / DPS / 挑刺 | **未做** |

### 5 · ops

测后 `server-runtime/ops.json` = `[]` · `login-runtime/ops.json` = `[]`；临时 OP `NiLoob5rp` 已 `deop`；LP `neigeitems.admin` / `neigeitems.*` 已 unset。

---

## Verdict

✅ **PASS · 勾销候选**（总控结案另开；本 tip 仅测报）。

悬停：**PASS** · 旁证 cinder summon / soul_dust feed **仍在** · ops=`[]`。
