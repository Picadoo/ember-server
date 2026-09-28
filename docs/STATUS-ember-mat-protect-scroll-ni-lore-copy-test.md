# STATUS · B2.31 NI 余烬保护券 lore 批 A · 轻测

**日期：** 2026-09-29 03:02 → 03:02 Asia/Shanghai（CST）  
**岗：** 余烬-测试岗执行器  
**依据：** 设计 `1bdacd8` · 批准 `4359e89` · 施工 tip `c0e8fcc`  
**范围：** `plugins/NeigeItems/Items/ember-enhance-gems.yml` 内 `mat_ember_protect_scroll` lore 已删裸 id 行；键名/name「余烬保护券」/说明「强化失败时防止掉级」「用于 +7～+9 等高危强化」/enchantments/hideflags 应在；**`mat_ember_stable_charm` / `gem_*` 未被本 tip 改动**  
**禁项：** **禁改配置** · 禁 wall-clock/DPS/挑刺 · **不宣称 B0.1 已清** · 勿带 dirty runtime · **不扩测** stable_charm / gem_*  
**Verdict：** ✅ **PASS**（验收点全过 · ops=`[]`）

---

## 一句话

保护券 lore 列表无字面 `mat_ember_protect_scroll`（键名保留）；name 仍「余烬保护券」、material=`PAPER`、说明「强化失败时防止掉级」「用于 +7～+9 等高危强化」未漂；enchantments=`DURABILITY:1` / hideflags=`HIDE_ENCHANTS` 未漂；`ni reload` 后 `/ni give … mat_ember_protect_scroll` 背包悬停无裸 id、见防掉级+高危强化；tip 仅删 1 行；`mat_ember_stable_charm` / 四件 `gem_*` 仍留灰字裸 id（未扩测清）；ops=`[]`。

---

## 总评

| # | 验收点 | 结果 |
|---|--------|------|
| 1 | lore/`git show`：无字面 `&7mat_ember_protect_scroll` / `mat_ember_protect_scroll`（键名可留）；仅删裸 id；stable_charm / gem_* **未动** | **PASS** |
| 2 | 悬停轻测：给 `mat_ember_protect_scroll` → 见「余烬保护券」+「强化失败时防止掉级」+「用于 +7～+9 等高危强化」· **不见**灰字裸 id | **PASS** |
| 3 | 未扩测 stable/gem；不宣称 B0.1；不叫挑刺；禁 wall-clock/DPS | **PASS** |
| 4 | 写本测报 · commit+push · ops=`[]` | **PASS**（本 tip） |

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 口 | proxy 25565 / login 25566 / play 25567 |
| 施工 tip | `c0e8fcc` fix(ni): B2.31 drop bare id from mat_ember_protect_scroll lore |
| 设计 / 批准 | `1bdacd8` / `4359e89` |
| 热更 | 测前 `ni reload` → `NeigeItems > 重载完毕` @ 03:02:02 CST |
| 账号 | 验收 `NiLm6fr0`（非 OP）· 辅助 `NiLom6fr0`（临时 OP，已 deop + LP unset） |
| 探针 | `/tmp/b231-mat-protect-scroll-ni-lore-copy-test.js` · JSON `/tmp/b231-mat-protect-scroll-ni-lore-copy-test.json`（未入 git） |
| UX | 自定义物走 NI ID `mat_ember_protect_scroll`；悬停=背包 display lore |

---

## 各点证据

### 1 · lore 列表无裸 id · tip 仅删 1 行 · stable/gem 未动

```
rg -n 'mat_ember_protect_scroll|mat_ember_stable_charm|gem_' plugins/NeigeItems/Items/ember-enhance-gems.yml
→ L5  mat_ember_protect_scroll:   （键名 · 管理侧可留）
→ L16 mat_ember_stable_charm:     （键名）
→ L20    - '&7mat_ember_stable_charm'  （仍留灰字 · 未扩测清）
→ L29+ gem_ember_* + '&7gem_ember_*'   （仍留灰字 · 未扩测清）
```

`mat_ember_protect_scroll` lore 块现网：

```
  lore:
    - '&8强化失败时防止掉级'
    - '&7用于 +7～+9 等高危强化'
```

字面 `&7mat_ember_protect_scroll` / `mat_ember_protect_scroll`（lore 内）：**0**（已删）。

`git show c0e8fcc` hunk：

```
@@ -6,7 +6,6 @@ mat_ember_protect_scroll:
   material: PAPER
   name: '&d余烬保护券'
   lore:
-    - '&7mat_ember_protect_scroll'
     - '&8强化失败时防止掉级'
     - '&7用于 +7～+9 等高危强化'
```

| 项 | 现网 |
|----|------|
| name | `&d余烬保护券` |
| material | `PAPER` |
| lore×2 | 强化失败时防止掉级；用于 +7～+9 等高危强化 |
| enchantments | `DURABILITY: 1`（未漂） |
| hideflags | `HIDE_ENCHANTS`（未漂） |
| tip 文件 | **仅** `plugins/NeigeItems/Items/ember-enhance-gems.yml`（−1 行） |
| stable_charm | tip **未改**；现网仍有 `&7mat_ember_stable_charm` |
| gem_*×4 | tip **未改**；现网仍有 `&7gem_ember_*` |

### 2 · NI give + 悬停轻测

| 步骤 | 结果 |
|------|------|
| `ni reload` | `NeigeItems > 重载完毕` @ 03:02:02 CST |
| OP `/ni give NiLm6fr0 mat_ember_protect_scroll 1` | chat：`NeigeItems > 你得到了 1 个 余烬保护券` |
| 背包 NBT | name=`余烬保护券` · mat=`paper` · lore=`[强化失败时防止掉级, 用于 +7～+9 等高危强化]` |
| 裸 id | lore **无** `mat_ember_protect_scroll` |
| 说明 | lore **有**「强化失败时防止掉级」+「用于 +7～+9 等高危强化」 |

### 3 · 禁项 / 未漂

| 项 | 证据 |
|----|------|
| tip 范围 | `c0e8fcc` **仅** protect_scroll lore 删 1 行裸 id |
| stable_charm / gem_* | tip **未动**；现网仍留灰字裸 id（**未扩测清**） |
| 说明行 | **保留**（未误删） |
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
