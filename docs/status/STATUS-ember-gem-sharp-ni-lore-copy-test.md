# STATUS · B2.33 NI 锋利石 lore 批 A · 轻测（gem 轨首件）

**日期：** 2026-09-29 03:12 → 03:13 Asia/Shanghai（CST）  
**岗：** 余烬-测试岗执行器  
**依据：** 设计 `48b5218` · 批准 `6a43ab1` · 施工 tip `fbef72e`  
**范围：** `plugins/NeigeItems/Items/ember-enhance-gems.yml` 内 `gem_ember_sharp` lore 已删裸 id 行；键名/name「锋利石」/说明「镶嵌石 · 微量攻击」「刃 / 护符孔可用」/enchantments/hideflags 应在；**steady/drain/gale 仍留灰字（未扩测清）**；live Items `rg '&7gem_'`（排除 bak）计数=**3**  
**禁项：** **禁改配置** · 禁 wall-clock/DPS/挑刺 · **不宣称 B0.1 / soft 已清** · **不宣称其余 gem / cosmetic / pet / 斜杠已清** · 勿带 dirty runtime · **不扩测** steady/drain/gale  
**Verdict：** ✅ **PASS**（验收点全过 · ops=`[]` · `&7gem_` rg=**3** · 命中 steady/drain/gale）

---

## 一句话

锋利石 lore 列表无字面 `gem_ember_sharp`（键名保留）；name 仍「锋利石」、material=`QUARTZ`、说明「镶嵌石 · 微量攻击」「刃 / 护符孔可用」未漂；enchantments=`DURABILITY:1` / hideflags=`HIDE_ENCHANTS` 未漂；`ni reload` 后 `/ni give … gem_ember_sharp` 背包悬停无裸 id、见攻击/孔位说明；tip 仅删 1 行；其余 3 件 gem 仍留灰字裸 id（未扩测清）；live Items `rg '&7gem_'`（`!*.bak*`）=**3**；ops=`[]`。

---

## 总评

| # | 验收点 | 结果 |
|---|--------|------|
| 1 | lore/`git show`：无字面 `&7gem_ember_sharp` / `gem_ember_sharp`（键名可留）；仅删裸 id；steady/drain/gale **未动** | **PASS** |
| 2 | 悬停轻测：给 `gem_ember_sharp` → 见「锋利石」+「镶嵌石 · 微量攻击」+「刃 / 护符孔可用」· **不见**灰字裸 id | **PASS** |
| 3 | live `rg -n '&7gem_' plugins/NeigeItems/Items/ --glob '!*.bak*'` **计数=3**（steady/drain/gale） | **PASS**（计数=3） |
| 4 | 未扩测其余 gem / cosmetic / pet；不宣称 B0.1/soft；不叫挑刺；禁 wall-clock/DPS | **PASS** |
| 5 | 写本测报 · commit+push · ops=`[]` | **PASS**（本 tip） |

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 口 | proxy 25565 / login 25566 / play 25567 |
| 施工 tip | `fbef72e` fix(ni): B2.33 drop bare id from gem_ember_sharp lore |
| 设计 / 批准 | `48b5218` / `6a43ab1` |
| 热更 | 测前 `ni reload` → `NeigeItems > 重载完毕` @ 03:12:43 CST |
| 账号 | 验收 `NiLmk6g4`（非 OP）· 辅助 `NiLomk6g4`（临时 OP，已 deop + LP unset） |
| 探针 | `/tmp/b233-gem-sharp-ni-lore-copy-test.js` · JSON `/tmp/b233-gem-sharp-ni-lore-copy-test.json`（未入 git） |
| UX | 自定义物走 NI ID `gem_ember_sharp`；悬停=背包 display lore |

---

## 各点证据

### 1 · lore 列表无裸 id · tip 仅删 1 行 · 其余 gem 未动

```
rg -n 'gem_ember_sharp|gem_ember_' plugins/NeigeItems/Items/ember-enhance-gems.yml
→ L28 gem_ember_sharp:     （键名 · 管理侧可留）
→ L39+ gem_ember_steady/drain/gale + '&7gem_ember_*'   （仍留灰字 · 未扩测清）
```

`gem_ember_sharp` lore 块现网：

```
  lore:
    - '&8镶嵌石 · 微量攻击'
    - '&8刃 / 护符孔可用'
```

字面 `&7gem_ember_sharp` / `gem_ember_sharp`（lore 内）：**0**（已删）。

`git show fbef72e` hunk：

```
@@ -29,7 +29,6 @@ gem_ember_sharp:
   material: QUARTZ
   name: '&c锋利石'
   lore:
-    - '&7gem_ember_sharp'
     - '&8镶嵌石 · 微量攻击'
     - '&8刃 / 护符孔可用'
```

| 项 | 现网 |
|----|------|
| name | `&c锋利石` |
| material | `QUARTZ` |
| lore×2 | 镶嵌石 · 微量攻击；刃 / 护符孔可用 |
| enchantments | `DURABILITY: 1`（未漂） |
| hideflags | `HIDE_ENCHANTS`（未漂） |
| tip 文件 | **仅** `plugins/NeigeItems/Items/ember-enhance-gems.yml`（−1 行） |
| steady/drain/gale | tip **未改**；现网仍有 `&7gem_ember_*` |
| mat_* / cosmetic / pet | tip **未碰** |

### 2 · NI give + 悬停轻测

| 步骤 | 结果 |
|------|------|
| `ni reload` | `NeigeItems > 重载完毕` @ 03:12:43 CST |
| OP `/ni give NiLmk6g4 gem_ember_sharp 1` | chat：`NeigeItems > 你得到了 1 个 锋利石`；服务端：`成功给予 NiLmk6g4 1 个 锋利石` |
| 背包 NBT | name=`锋利石` · mat=`quartz` · lore=`[镶嵌石 · 微量攻击, 刃 / 护符孔可用]` |
| 裸 id | lore **无** `gem_ember_sharp` |
| 说明 | lore **有**「镶嵌石 · 微量攻击」+「刃 / 护符孔可用」 |

### 3 · live `&7gem_` rg 计数=3

```
rg -n '&7gem_' plugins/NeigeItems/Items/ --glob '!*.bak*'
→ plugins/NeigeItems/Items/ember-enhance-gems.yml:43:    - '&7gem_ember_steady'
→ plugins/NeigeItems/Items/ember-enhance-gems.yml:55:    - '&7gem_ember_drain'
→ plugins/NeigeItems/Items/ember-enhance-gems.yml:67:    - '&7gem_ember_gale'
计数 = 3
```

**仅本轨 live 宣称**（sharp 已去裸 id · 剩 3）；**不宣称** bak / 斜杠 / cosmetic / pet / soft / B0.1 已清。

### 4 · 禁项 / 未漂

| 项 | 证据 |
|----|------|
| tip 范围 | `fbef72e` **仅** sharp lore 删 1 行裸 id |
| steady/drain/gale | tip **未动**；现网仍留灰字裸 id（**未扩测清**） |
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

ops=`[]` · `&7gem_` rg 计数=**3**（命中文件：`plugins/NeigeItems/Items/ember-enhance-gems.yml` · steady/drain/gale）。
