# STATUS · B2.36 NI 疾风石 lore 批 A · 轻测

**日期：** 2026-09-29 03:28 → 03:29 Asia/Shanghai（CST）  
**岗：** 余烬-测试岗执行器  
**依据：** 设计 `eca16cd` · 批准 `dd582c8` · 施工 tip `3e3c63b`  
**范围：** `plugins/NeigeItems/Items/ember-enhance-gems.yml` 内 `gem_ember_gale` lore 已删裸 id 行；键名/name「疾风石」/说明「镶嵌石 · 微量移速」「刃 / 护符孔可用」/enchantments/hideflags 应在；sharp/steady/drain 已于 B2.33–B2.35 去裸 id（本 tip 未再动）；live Items `rg '&7gem_'`（排除 bak）计数=**0**（可写 gem 本轨归零）  
**禁项：** **禁改配置** · 禁 wall-clock/DPS/挑刺 · **不宣称 B0.1 / soft 已清** · **不宣称斜杠 / cosmetic / pet 已清** · 勿带 dirty runtime · **不扩测** 非 gale  
**Verdict：** ✅ **PASS**（验收点全过 · ops=`[]` · `&7gem_` rg=**0** · gem 本轨归零）

---

## 一句话

疾风石 lore 列表无字面 `gem_ember_gale`（键名保留）；name 仍「疾风石」、material=`FEATHER`、说明「镶嵌石 · 微量移速」「刃 / 护符孔可用」未漂；enchantments=`DURABILITY:1` / hideflags=`HIDE_ENCHANTS` 未漂；`ni reload` 后 `/ni give … gem_ember_gale` 背包悬停无裸 id、见移速/孔位说明；tip 仅删 1 行；sharp/steady/drain tip 未动仍干净；live Items `rg '&7gem_'`（`!*.bak*`）=**0**（gem 本轨归零；**不宣称** bak / 斜杠 / cosmetic / pet / soft / B0.1）；ops=`[]`。

---

## 总评

| # | 验收点 | 结果 |
|---|--------|------|
| 1 | lore/`git show`：无字面 `&7gem_ember_gale` / `gem_ember_gale`（键名可留）；仅删裸 id；其余 gem **未动** | **PASS** |
| 2 | 悬停轻测：给 `gem_ember_gale` → 见「疾风石」+「镶嵌石 · 微量移速」+「刃 / 护符孔可用」· **不见**灰字裸 id | **PASS** |
| 3 | live `rg -n '&7gem_' plugins/NeigeItems/Items/ --glob '!*.bak*'` **计数=0** | **PASS**（计数=0 · gem 本轨归零） |
| 4 | 未扩测其余 gem / cosmetic / pet / 斜杠；不宣称 B0.1/soft；不叫挑刺；禁 wall-clock/DPS | **PASS** |
| 5 | 写本测报 · commit+push · ops=`[]` | **PASS**（本 tip） |

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 口 | proxy 25565 / login 25566 / play 25567 |
| 施工 tip | `3e3c63b` fix(ni): B2.36 drop bare id from gem_ember_gale lore |
| 设计 / 批准 | `eca16cd` / `dd582c8` |
| 热更 | 测前 `ni reload` → `NeigeItems > 重载完毕` @ 03:28:41 CST |
| 账号 | 验收 `NiLn4q4d`（非 OP）· 辅助 `NiLon4q4d`（临时 OP，已 deop + LP unset） |
| 探针 | `/tmp/b236-gem-gale-ni-lore-copy-test.js` · JSON `/tmp/b236-gem-gale-ni-lore-copy-test.json`（未入 git） |
| UX | 自定义物走 NI ID `gem_ember_gale`；悬停=背包 display lore |

---

## 各点证据

### 1 · lore 列表无裸 id · tip 仅删 1 行 · 其余 gem 未动

```
rg -n 'gem_ember_gale' plugins/NeigeItems/Items/ember-enhance-gems.yml
→ L61 gem_ember_gale:     （键名 · 管理侧可留）
```

`gem_ember_gale` lore 块现网：

```
  lore:
    - '&8镶嵌石 · 微量移速'
    - '&8刃 / 护符孔可用'
```

字面 `&7gem_ember_gale` / `gem_ember_gale`（lore 内）：**0**（已删）。

`git show 3e3c63b` hunk：

```
@@ -62,7 +62,6 @@ gem_ember_gale:
   material: FEATHER
   name: '&a疾风石'
   lore:
-    - '&7gem_ember_gale'
     - '&8镶嵌石 · 微量移速'
     - '&8刃 / 护符孔可用'
```

| 项 | 现网 |
|----|------|
| name | `&a疾风石` |
| material | `FEATHER` |
| lore×2 | 镶嵌石 · 微量移速；刃 / 护符孔可用 |
| enchantments | `DURABILITY: 1`（未漂） |
| hideflags | `HIDE_ENCHANTS`（未漂） |
| tip 文件 | **仅** `plugins/NeigeItems/Items/ember-enhance-gems.yml`（−1 行） |
| sharp/steady/drain | tip **未动**；B2.33–B2.35 已去裸 id（仍干净） |
| mat_* / cosmetic / pet | tip **未碰** |

### 2 · NI give + 悬停轻测

| 步骤 | 结果 |
|------|------|
| `ni reload` | `NeigeItems > 重载完毕` @ 03:28:41 CST |
| OP `/ni give NiLn4q4d gem_ember_gale 1` | chat：`NeigeItems > 你得到了 1 个 疾风石`；服务端：`成功给予 NiLn4q4d 1 个 疾风石` @ 03:28:57–03:28:59 CST |
| 背包 NBT | name=`疾风石` · mat=`feather` · lore=`[镶嵌石 · 微量移速, 刃 / 护符孔可用]` |
| 裸 id | lore **无** `gem_ember_gale` |
| 说明 | lore **有**「镶嵌石 · 微量移速」+「刃 / 护符孔可用」 |

### 3 · live `&7gem_` rg 计数=0（gem 本轨归零）

```
rg -n '&7gem_' plugins/NeigeItems/Items/ --glob '!*.bak*'
→ （无命中）
计数 = 0
```

**仅本轨 live 宣称**（gale 已去裸 id · gem Items 归零）；**不宣称** bak / 斜杠 / cosmetic / pet / soft / B0.1 已清。

### 4 · 禁项 / 未漂

| 项 | 证据 |
|----|------|
| tip 范围 | `3e3c63b` **仅** gale lore 删 1 行裸 id |
| sharp/steady/drain | tip **未动**；仍无裸 id（B2.33–B2.35） |
| 说明行 | **保留**（未误删） |
| 配方 / 给物逻辑 / TrMenu / CoreRpg Java / cosmetic / pet | tip **未碰** |
| enchantments / hideflags | tip **未改**；现网仍 DURABILITY:1 / HIDE_ENCHANTS |
| B0.1 / soft / 附 B / 精英壳 / 挑刺 / wall-clock/DPS | **本岗未宣称、未开、未测** |
| bak / 斜杠 / cosmetic / pet | **不宣称已清** |
| 本岗改配置 | **否**（只读 + 热更/给物/OP 生命周期；测报 docs only） |

### 5 · ops

测后 `server-runtime/ops.json` = `[]` · `login-runtime/ops.json` = `[]`；临时 OP 已 `deop`。

---

## Verdict

✅ **PASS · 勾销候选**（总控结案另开；本 tip 仅测报）。

ops=`[]` · `&7gem_` rg 计数=**0**（gem 本轨 live Items 归零；不宣称 bak / 斜杠 / cosmetic / pet / soft / B0.1）。
