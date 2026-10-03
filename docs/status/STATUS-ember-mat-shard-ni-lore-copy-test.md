# STATUS · B2.24 NI 余烬碎片 lore 批 A · 轻测

**日期：** 2026-09-29 02:28 → 02:29 Asia/Shanghai（CST）  
**岗：** 余烬-测试岗执行器  
**依据：** 设计 `9931476` · 批准 `60f43b7` · 施工 tip `e004458`  
**范围：** `plugins/NeigeItems/Items/ember-dungeon.yml` 内 `mat_ember_shard` lore 已删裸 id 行；键名/name「余烬碎片」/地窟说明应在  
**禁项：** **禁改配置** · 禁 wall-clock/DPS/挑刺 · **不宣称 B0.1 已清** · 勿带 dirty runtime · **不扩测** bone_dust 等其余 8 件 · 禁附 B / 精英壳  
**Verdict：** ✅ **PASS**（验收点全过 · ops=`[]`）

---

## 一句话

碎片 lore 列表无字面 `mat_ember_shard`（键名保留）；name 仍「余烬碎片」、material=`REDSTONE`、说明「余烬地窟掉落 · 基础材料」未漂；`ni reload` 后 `/ni give … mat_ember_shard` 背包悬停无裸 id；tip 仅删 1 行；同文件 bone_dust/core_fragment 仍留灰字（未扩测清）；ops=`[]`。

---

## 总评

| # | 验收点 | 结果 |
|---|--------|------|
| 1 | lore 列表 `rg`/`git show` 无字面 `mat_ember_shard`（键名行可留）；仅删 `&7mat_ember_shard` | **PASS** |
| 2 | 悬停轻测：给 `mat_ember_shard` → 见「余烬碎片」+ 地窟说明 · **不见**灰字裸 id | **PASS** |
| 3 | 未扩测其余 8 件；不宣称 B0.1；不叫挑刺；禁 wall-clock/DPS | **PASS** |
| 4 | 写本测报 · commit+push · ops=`[]` | **PASS**（本 tip） |

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 口 | proxy 25565 / login 25566 / play 25567 |
| 施工 tip | `e004458` fix(ni): B2.24 drop bare id from mat_ember_shard lore |
| 设计 / 批准 | `9931476` / `60f43b7` |
| 热更 | 测前 `ni reload` → `NeigeItems > 重载完毕` @ 02:28:44 CST |
| 账号 | 验收 `NiLkzmln`（非 OP）· 辅助 `NiLokzmln`（临时 OP，已 deop + LP unset） |
| 探针 | `/tmp/b224-mat-shard-ni-lore-copy-test.js` · JSON `/tmp/b224-mat-shard-ni-lore-copy-test.json`（未入 git） |
| UX | 自定义物走 NI ID `mat_ember_shard`；悬停=背包 display lore |

---

## 各点证据

### 1 · lore 列表无裸 id · tip 仅删 1 行

```
rg -n 'mat_ember_shard' plugins/NeigeItems/Items/ember-dungeon.yml
→ L5 mat_ember_shard:   （键名 · 管理侧可留）
```

lore 块现网：

```
  lore:
    - '&8余烬地窟掉落 · 基础材料'
```

字面 `&7mat_ember_shard`：**0**（已删）。

`git show e004458` hunk：

```
@@ -6,7 +6,6 @@ mat_ember_shard:
   material: REDSTONE
   name: '&c余烬碎片'
   lore:
-    - '&7mat_ember_shard'
     - '&8余烬地窟掉落 · 基础材料'
```

| 项 | 现网 |
|----|------|
| name | `&c余烬碎片` |
| material | `REDSTONE` |
| lore×1 | 余烬地窟掉落 · 基础材料 |
| tip 文件 | **仅** `plugins/NeigeItems/Items/ember-dungeon.yml`（−1 行） |
| bone_dust / core_fragment | tip **未改**；现网仍留 `&7mat_*` 灰字（旁附 soft · 本岗不扩测） |

### 2 · NI give + 悬停轻测

| 步骤 | 结果 |
|------|------|
| `ni reload` | `NeigeItems > 重载完毕` @ 02:28:44 CST |
| OP `/ni give NiLkzmln mat_ember_shard 1` | chat：`NeigeItems > 你得到了 1 个 余烬碎片` |
| 背包 NBT | name=`余烬碎片` · mat=`redstone` · lore=`[余烬地窟掉落 · 基础材料]` |
| 裸 id | lore **无** `mat_ember_shard` |

### 3 · 禁项 / 未漂

| 项 | 证据 |
|----|------|
| tip 范围 | `e004458` **仅** shard lore 删 1 行 |
| 其余 8 件 `&7mat_*` | **未扩测、未清**（siblings 仍留灰字） |
| 配方 / 给物逻辑 / TrMenu / CoreRpg Java | tip **未碰** |
| B0.1 / 附 B / 精英壳 / 挑刺 / wall-clock/DPS | **本岗未宣称、未开、未测** |
| 本岗改配置 | **否**（只读 + 热更/给物/OP 生命周期；测报 docs only） |

### 4 · ops

测后 `server-runtime/ops.json` = `[]` · `login-runtime/ops.json` = `[]`；临时 OP 已 `deop`。

---

## Verdict

✅ **PASS · 勾销候选**（总控结案另开；本 tip 仅测报）。

ops=`[]`。
