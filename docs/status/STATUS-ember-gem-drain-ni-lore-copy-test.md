# STATUS · B2.35 NI 汲取石 lore 批 A · 轻测

**日期：** 2026-09-29 03:23 → 03:23 Asia/Shanghai（CST）  
**岗：** 余烬-测试岗执行器  
**依据：** 设计 `c9cdb28` · 批准 `bf3bf4b` · 施工 tip `1ef8a41`  
**范围：** `plugins/NeigeItems/Items/ember-enhance-gems.yml` 内 `gem_ember_drain` lore 已删裸 id 行；键名/name「汲取石」/说明「镶嵌石 · 微量吸血」「刃 / 护符孔可用」/enchantments/hideflags 应在；**gale 仍留灰字（未扩测清）**；sharp/steady 已于 B2.33/B2.34 去裸 id（本 tip 未再动）；live Items `rg '&7gem_'`（排除 bak）计数=**1**  
**禁项：** **禁改配置** · 禁 wall-clock/DPS/挑刺 · **不宣称 B0.1 / soft 已清** · **不宣称其余 gem / cosmetic / pet / 斜杠已清** · 勿带 dirty runtime · **不扩测** gale  
**Verdict：** ✅ **PASS**（验收点全过 · ops=`[]` · `&7gem_` rg=**1** · 命中 gale）

---

## 一句话

汲取石 lore 列表无字面 `gem_ember_drain`（键名保留）；name 仍「汲取石」、material=`GHAST_TEAR`、说明「镶嵌石 · 微量吸血」「刃 / 护符孔可用」未漂；enchantments=`DURABILITY:1` / hideflags=`HIDE_ENCHANTS` 未漂；`ni reload` 后 `/ni give … gem_ember_drain` 背包悬停无裸 id、见吸血/孔位说明；tip 仅删 1 行；gale 仍留灰字裸 id（未扩测清）；live Items `rg '&7gem_'`（`!*.bak*`）=**1**；ops=`[]`。

---

## 总评

| # | 验收点 | 结果 |
|---|--------|------|
| 1 | lore/`git show`：无字面 `&7gem_ember_drain` / `gem_ember_drain`（键名可留）；仅删裸 id；gale **未动** | **PASS** |
| 2 | 悬停轻测：给 `gem_ember_drain` → 见「汲取石」+「镶嵌石 · 微量吸血」+「刃 / 护符孔可用」· **不见**灰字裸 id | **PASS** |
| 3 | live `rg -n '&7gem_' plugins/NeigeItems/Items/ --glob '!*.bak*'` **计数=1**（gale） | **PASS**（计数=1） |
| 4 | 未扩测其余 gem / cosmetic / pet；不宣称 B0.1/soft；不叫挑刺；禁 wall-clock/DPS | **PASS** |
| 5 | 写本测报 · commit+push · ops=`[]` | **PASS**（本 tip） |

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 口 | proxy 25565 / login 25566 / play 25567 |
| 施工 tip | `1ef8a41` fix(ni): B2.35 drop bare id from gem_ember_drain lore |
| 设计 / 批准 | `c9cdb28` / `bf3bf4b` |
| 热更 | 测前 `ni reload` → `NeigeItems > 重载完毕` @ 03:23:17 CST |
| 账号 | 验收 `NiLmxs8p`（非 OP）· 辅助 `NiLomxs8p`（临时 OP，已 deop + LP unset） |
| 探针 | `/tmp/b235-gem-drain-ni-lore-copy-test.js` · JSON `/tmp/b235-gem-drain-ni-lore-copy-test.json`（未入 git） |
| UX | 自定义物走 NI ID `gem_ember_drain`；悬停=背包 display lore |

---

## 各点证据

### 1 · lore 列表无裸 id · tip 仅删 1 行 · 其余 gem 未动

```
rg -n 'gem_ember_drain|gem_ember_' plugins/NeigeItems/Items/ember-enhance-gems.yml
→ L50 gem_ember_drain:     （键名 · 管理侧可留）
→ L61+ gem_ember_gale + '&7gem_ember_gale'   （仍留灰字 · 未扩测清）
```

`gem_ember_drain` lore 块现网：

```
  lore:
    - '&8镶嵌石 · 微量吸血'
    - '&8刃 / 护符孔可用'
```

字面 `&7gem_ember_drain` / `gem_ember_drain`（lore 内）：**0**（已删）。

`git show 1ef8a41` hunk：

```
@@ -51,7 +51,6 @@ gem_ember_drain:
   material: GHAST_TEAR
   name: '&5汲取石'
   lore:
-    - '&7gem_ember_drain'
     - '&8镶嵌石 · 微量吸血'
     - '&8刃 / 护符孔可用'
```

| 项 | 现网 |
|----|------|
| name | `&5汲取石` |
| material | `GHAST_TEAR` |
| lore×2 | 镶嵌石 · 微量吸血；刃 / 护符孔可用 |
| enchantments | `DURABILITY: 1`（未漂） |
| hideflags | `HIDE_ENCHANTS`（未漂） |
| tip 文件 | **仅** `plugins/NeigeItems/Items/ember-enhance-gems.yml`（−1 行） |
| gale | tip **未改**；现网仍有 `&7gem_ember_gale` |
| sharp/steady | tip **未动**；B2.33/B2.34 已去裸 id（仍干净） |
| mat_* / cosmetic / pet | tip **未碰** |

### 2 · NI give + 悬停轻测

| 步骤 | 结果 |
|------|------|
| `ni reload` | `NeigeItems > 重载完毕` @ 03:23:17 CST |
| OP `/ni give NiLmxs8p gem_ember_drain 1` | chat：`NeigeItems > 你得到了 1 个 汲取石`；服务端：`成功给予 NiLmxs8p 1 个 汲取石` |
| 背包 NBT | name=`汲取石` · mat=`ghast_tear` · lore=`[镶嵌石 · 微量吸血, 刃 / 护符孔可用]` |
| 裸 id | lore **无** `gem_ember_drain` |
| 说明 | lore **有**「镶嵌石 · 微量吸血」+「刃 / 护符孔可用」 |

### 3 · live `&7gem_` rg 计数=1

```
rg -n '&7gem_' plugins/NeigeItems/Items/ --glob '!*.bak*'
→ plugins/NeigeItems/Items/ember-enhance-gems.yml:65:    - '&7gem_ember_gale'
计数 = 1
```

**仅本轨 live 宣称**（drain 已去裸 id · 剩 1 gale）；**不宣称** bak / 斜杠 / cosmetic / pet / soft / B0.1 已清。

### 4 · 禁项 / 未漂

| 项 | 证据 |
|----|------|
| tip 范围 | `1ef8a41` **仅** drain lore 删 1 行裸 id |
| gale | tip **未动**；现网仍留灰字裸 id（**未扩测清**） |
| sharp/steady | tip **未动**；仍无裸 id（B2.33/B2.34） |
| 说明行 | **保留**（未误删） |
| 配方 / 给物逻辑 / TrMenu / CoreRpg Java / cosmetic / pet | tip **未碰** |
| enchantments / hideflags | tip **未改**；现网仍 DURABILITY:1 / HIDE_ENCHANTS |
| B0.1 / soft / 附 B / 精英壳 / 挑刺 / wall-clock/DPS | **本岗未宣称、未开、未测** |
| bak / 斜杠 / 其余 gem | **不宣称已清** |
| 本岗改配置 | **否**（只读 + 热更/给物/OP 生命周期；测报 docs only） |

### 5 · ops

测后 `server-runtime/ops.json` = `[]` · `login-runtime/ops.json` = `[]`；临时 OP 已 `deop`。

---

## Verdict

✅ **PASS · 勾销候选**（总控结案另开；本 tip 仅测报）。

ops=`[]` · `&7gem_` rg 计数=**1**（命中文件：`plugins/NeigeItems/Items/ember-enhance-gems.yml` · gale）。
