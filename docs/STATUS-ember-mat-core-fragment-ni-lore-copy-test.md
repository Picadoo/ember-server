# STATUS · B2.26 NI 余烬核心碎片 lore 批 A · 轻测

**日期：** 2026-09-29 02:37 → 02:37 Asia/Shanghai（CST）  
**岗：** 余烬-测试岗执行器  
**依据：** 设计 `0e76453` · 批准 `28a5048` · 施工 tip `ba460a7`  
**范围：** `plugins/NeigeItems/Items/ember-dungeon.yml` 内 `mat_ember_core_fragment` lore 已删裸 id 行；键名/name「余烬核心碎片」/稀有材料说明/enchantments/hideflags 应在  
**禁项：** **禁改配置** · 禁 wall-clock/DPS/挑刺 · **不宣称 B0.1 已清** · 勿带 dirty runtime · **不扩测** 其余 6 件 mat · 禁附 B / 精英壳  
**Verdict：** ✅ **PASS**（验收点全过 · ops=`[]`）

---

## 一句话

核心碎片 lore 列表无字面 `mat_ember_core_fragment`（键名保留）；name 仍「余烬核心碎片」、material=`MAGMA_CREAM`、说明「余烬地窟掉落 · 稀有材料」未漂；enchantments=`DURABILITY:1` / hideflags=`HIDE_ENCHANTS` 未漂；`ni reload` 后 `/ni give … mat_ember_core_fragment` 背包悬停无裸 id；tip 仅删 1 行；其余 6 件 `&7mat_*` 仍留灰字（未扩测清）；ops=`[]`。

---

## 总评

| # | 验收点 | 结果 |
|---|--------|------|
| 1 | lore 列表 `rg`/`git show` 无字面 `mat_ember_core_fragment`（键名行可留）；仅删 `&7mat_ember_core_fragment` | **PASS** |
| 2 | 悬停轻测：给 `mat_ember_core_fragment` → 见「余烬核心碎片」+ 稀有材料说明 · **不见**灰字裸 id；enchantments/hideflags 语义不漂 | **PASS** |
| 3 | 未扩测其余 6 件；不宣称 B0.1；不叫挑刺；禁 wall-clock/DPS | **PASS** |
| 4 | 写本测报 · commit+push · ops=`[]` | **PASS**（本 tip） |

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 口 | proxy 25565 / login 25566 / play 25567 |
| 施工 tip | `ba460a7` fix(ni): B2.26 drop bare id from mat_ember_core_fragment lore |
| 设计 / 批准 | `0e76453` / `28a5048` |
| 热更 | 测前 `ni reload` → `NeigeItems > 重载完毕` @ 02:37:08 CST |
| 账号 | 验收 `NiLlafkc`（非 OP）· 辅助 `NiLolafkc`（临时 OP，已 deop + LP unset） |
| 探针 | `/tmp/b226-mat-core-fragment-ni-lore-copy-test.js` · JSON `/tmp/b226-mat-core-fragment-ni-lore-copy-test.json`（未入 git） |
| UX | 自定义物走 NI ID `mat_ember_core_fragment`；悬停=背包 display lore |

---

## 各点证据

### 1 · lore 列表无裸 id · tip 仅删 1 行

```
rg -n 'mat_ember_core_fragment' plugins/NeigeItems/Items/ember-dungeon.yml
→ L17 mat_ember_core_fragment:   （键名 · 管理侧可留）
```

lore 块现网：

```
  lore:
    - '&8余烬地窟掉落 · 稀有材料'
```

字面 `&7mat_ember_core_fragment`：**0**（已删）。

`git show ba460a7` hunk：

```
@@ -18,7 +18,6 @@ mat_ember_core_fragment:
   material: MAGMA_CREAM
   name: '&6余烬核心碎片'
   lore:
-    - '&7mat_ember_core_fragment'
     - '&8余烬地窟掉落 · 稀有材料'
```

| 项 | 现网 |
|----|------|
| name | `&6余烬核心碎片` |
| material | `MAGMA_CREAM` |
| lore×1 | 余烬地窟掉落 · 稀有材料 |
| enchantments | `DURABILITY: 1`（未漂） |
| hideflags | `HIDE_ENCHANTS`（未漂） |
| tip 文件 | **仅** `plugins/NeigeItems/Items/ember-dungeon.yml`（−1 行） |
| 其余 6 件 `&7mat_*` | tip **未改**；现网仍留灰字（旁附 soft · 本岗不扩测；抽检 covenant_reset 仍留） |

### 2 · NI give + 悬停轻测

| 步骤 | 结果 |
|------|------|
| `ni reload` | `NeigeItems > 重载完毕` @ 02:37:08 CST |
| OP `/ni give NiLlafkc mat_ember_core_fragment 1` | chat：`NeigeItems > 你得到了 1 个 余烬核心碎片` |
| 背包 NBT | name=`余烬核心碎片` · mat=`magma_cream` · lore=`[余烬地窟掉落 · 稀有材料]` |
| 裸 id | lore **无** `mat_ember_core_fragment` |

### 3 · 禁项 / 未漂

| 项 | 证据 |
|----|------|
| tip 范围 | `ba460a7` **仅** core_fragment lore 删 1 行 |
| 其余 6 件 `&7mat_*` | **未扩测、未清**（covenant_reset 等仍留灰字） |
| 配方 / 给物逻辑 / TrMenu / CoreRpg Java | tip **未碰** |
| enchantments / hideflags | tip **未改**；现网仍 DURABILITY:1 / HIDE_ENCHANTS |
| B0.1 / 附 B / 精英壳 / 挑刺 / wall-clock/DPS | **本岗未宣称、未开、未测** |
| 本岗改配置 | **否**（只读 + 热更/给物/OP 生命周期；测报 docs only） |

### 4 · ops

测后 `server-runtime/ops.json` = `[]` · `login-runtime/ops.json` = `[]`；临时 OP 已 `deop`。

---

## Verdict

✅ **PASS · 勾销候选**（总控结案另开；本 tip 仅测报）。

ops=`[]`。
