# STATUS · B2.32 NI 余烬稳固符 lore 批 A · 轻测（live 最后 1 件）

**日期：** 2026-09-29 03:07 → 03:07 Asia/Shanghai（CST）  
**岗：** 余烬-测试岗执行器  
**依据：** 设计 `cf7eaaf` · 批准 `c4194e4` · 施工 tip `1cdf73b`  
**范围：** `plugins/NeigeItems/Items/ember-enhance-gems.yml` 内 `mat_ember_stable_charm` lore 已删裸 id 行；键名/name「余烬稳固符」/说明「+10 强化失败时不掉级」「仅失败，不掉档」/enchantments/hideflags 应在；**`gem_*` 未被本 tip 改动**；live Items `rg '&7mat_'`（排除 bak）归零  
**禁项：** **禁改配置** · 禁 wall-clock/DPS/挑刺 · **不宣称 B0.1 已清** · **不宣称 bak / gem_* / 斜杠已清** · 勿带 dirty runtime · **不扩测** gem_*  
**Verdict：** ✅ **PASS**（验收点全过 · ops=`[]` · `&7mat_` rg=**0**）

---

## 一句话

稳固符 lore 列表无字面 `mat_ember_stable_charm`（键名保留）；name 仍「余烬稳固符」、material=`NETHER_STAR`、说明「+10 强化失败时不掉级」「仅失败，不掉档」未漂；enchantments=`DURABILITY:1` / hideflags=`HIDE_ENCHANTS` 未漂；`ni reload` 后 `/ni give … mat_ember_stable_charm` 背包悬停无裸 id、见 +10/仅失败；tip 仅删 1 行；四件 `gem_*` 仍留灰字裸 id（未扩测清）；live Items `rg '&7mat_'`（`!*.bak*`）=**0**；ops=`[]`。

---

## 总评

| # | 验收点 | 结果 |
|---|--------|------|
| 1 | lore/`git show`：无字面 `&7mat_ember_stable_charm` / `mat_ember_stable_charm`（键名可留）；仅删裸 id；gem_* **未动** | **PASS** |
| 2 | 悬停轻测：给 `mat_ember_stable_charm` → 见「余烬稳固符」+「+10 强化失败时不掉级」+「仅失败，不掉档」· **不见**灰字裸 id | **PASS** |
| 3 | live `rg -n '&7mat_' plugins/NeigeItems/Items/ --glob '!*.bak*'` **归零**（仅本轨；不宣称 bak/gem_*/斜杠） | **PASS**（计数=0） |
| 4 | 未扩测 gem；不宣称 B0.1；不叫挑刺；禁 wall-clock/DPS | **PASS** |
| 5 | 写本测报 · commit+push · ops=`[]` | **PASS**（本 tip） |

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 口 | proxy 25565 / login 25566 / play 25567 |
| 施工 tip | `1cdf73b` fix(ni): B2.32 drop bare id from mat_ember_stable_charm lore |
| 设计 / 批准 | `cf7eaaf` / `c4194e4` |
| 热更 | 测前 `ni reload` → `NeigeItems > 重载完毕` @ 03:07:25 CST |
| 账号 | 验收 `NiLmdd9n`（非 OP）· 辅助 `NiLomdd9n`（临时 OP，已 deop + LP unset） |
| 探针 | `/tmp/b232-mat-stable-charm-ni-lore-copy-test.js` · JSON `/tmp/b232-mat-stable-charm-ni-lore-copy-test.json`（未入 git） |
| UX | 自定义物走 NI ID `mat_ember_stable_charm`；悬停=背包 display lore |

---

## 各点证据

### 1 · lore 列表无裸 id · tip 仅删 1 行 · gem 未动

```
rg -n 'mat_ember_stable_charm|gem_' plugins/NeigeItems/Items/ember-enhance-gems.yml
→ L16 mat_ember_stable_charm:     （键名 · 管理侧可留）
→ L28+ gem_ember_* + '&7gem_ember_*'   （仍留灰字 · 未扩测清）
```

`mat_ember_stable_charm` lore 块现网：

```
  lore:
    - '&8+10 强化失败时不掉级'
    - '&7仅失败，不掉档'
```

字面 `&7mat_ember_stable_charm` / `mat_ember_stable_charm`（lore 内）：**0**（已删）。

`git show 1cdf73b` hunk：

```
@@ -17,7 +17,6 @@ mat_ember_stable_charm:
   material: NETHER_STAR
   name: '&b余烬稳固符'
   lore:
-    - '&7mat_ember_stable_charm'
     - '&8+10 强化失败时不掉级'
     - '&7仅失败，不掉档'
```

| 项 | 现网 |
|----|------|
| name | `&b余烬稳固符` |
| material | `NETHER_STAR` |
| lore×2 | +10 强化失败时不掉级；仅失败，不掉档 |
| enchantments | `DURABILITY: 1`（未漂） |
| hideflags | `HIDE_ENCHANTS`（未漂） |
| tip 文件 | **仅** `plugins/NeigeItems/Items/ember-enhance-gems.yml`（−1 行） |
| protect_scroll | tip **未改**；现网仍无裸 id（B2.31 态） |
| gem_*×4 | tip **未改**；现网仍有 `&7gem_ember_*` |

### 2 · NI give + 悬停轻测

| 步骤 | 结果 |
|------|------|
| `ni reload` | `NeigeItems > 重载完毕` @ 03:07:25 CST |
| OP `/ni give NiLmdd9n mat_ember_stable_charm 1` | chat：`NeigeItems > 你得到了 1 个 余烬稳固符` |
| 背包 NBT | name=`余烬稳固符` · mat=`nether_star` · lore=`[+10 强化失败时不掉级, 仅失败，不掉档]` |
| 裸 id | lore **无** `mat_ember_stable_charm` |
| 说明 | lore **有**「+10 强化失败时不掉级」+「仅失败，不掉档」 |

### 3 · live `&7mat_` rg 归零

```
rg -n '&7mat_' plugins/NeigeItems/Items/ --glob '!*.bak*'
→ （无匹配）计数 = 0
```

**仅本轨 live 宣称**；**不宣称** bak / `gem_*` / 斜杠已清。

### 4 · 禁项 / 未漂

| 项 | 证据 |
|----|------|
| tip 范围 | `1cdf73b` **仅** stable_charm lore 删 1 行裸 id |
| gem_*×4 | tip **未动**；现网仍留灰字裸 id（**未扩测清**） |
| 说明行 | **保留**（未误删） |
| 配方 / 给物逻辑 / TrMenu / CoreRpg Java | tip **未碰** |
| enchantments / hideflags | tip **未改**；现网仍 DURABILITY:1 / HIDE_ENCHANTS |
| B0.1 / 附 B / 精英壳 / 挑刺 / wall-clock/DPS | **本岗未宣称、未开、未测** |
| bak / gem_* / 斜杠 | **不宣称已清** |
| 本岗改配置 | **否**（只读 + 热更/给物/OP 生命周期；测报 docs only） |

### 5 · ops

测后 `server-runtime/ops.json` = `[]` · `login-runtime/ops.json` = `[]`；临时 OP 已 `deop`。

---

## Verdict

✅ **PASS · 勾销候选**（总控结案另开；本 tip 仅测报）。

ops=`[]` · `&7mat_` rg 计数=**0**。
