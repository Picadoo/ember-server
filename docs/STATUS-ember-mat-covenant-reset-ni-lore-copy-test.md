# STATUS · B2.27 NI 誓约重置券 lore 批 A · 轻测

**日期：** 2026-09-29 02:42 → 02:43 Asia/Shanghai（CST）  
**岗：** 余烬-测试岗执行器  
**依据：** 设计 `8bb16c9` · 批准 `1d659d0` · 施工 tip `d0830a7`  
**范围：** `plugins/NeigeItems/Items/ember-covenant-talent.yml` 内 `mat_ember_covenant_reset` lore 已删裸 id 行；键名/name「誓约重置券」/洗约说明/**`/corerpg covenant set|reset` 斜杠行保留**/enchantments/hideflags 应在  
**禁项：** **禁改配置** · 禁 wall-clock/DPS/挑刺 · **不宣称 B0.1 已清** · 勿带 dirty runtime · **不扩测** talent_reset / 其余 5 件 · 本窗**保留**斜杠（与 B2.23 重铸石去斜杠不同）  
**Verdict：** ✅ **PASS**（验收点全过 · ops=`[]`）

---

## 一句话

誓约重置券 lore 列表无字面 `mat_ember_covenant_reset`（键名保留）；name 仍「誓约重置券」、material=`PAPER`、说明「洗约 · 免晶钻」未漂；**斜杠行「用于 /corerpg covenant set|reset」仍在**；enchantments=`DURABILITY:1` / hideflags=`HIDE_ENCHANTS` 未漂；`ni reload` 后 `/ni give … mat_ember_covenant_reset` 背包悬停无裸 id、见洗约+斜杠；tip 仅删 1 行；`mat_ember_talent_reset` 仍留灰字（未扩测清）；ops=`[]`。

---

## 总评

| # | 验收点 | 结果 |
|---|--------|------|
| 1 | lore/`git show`：无字面 `mat_ember_covenant_reset`（键名可留）；仅删裸 id；**`/corerpg covenant set|reset` 仍在** | **PASS** |
| 2 | 悬停轻测：给 `mat_ember_covenant_reset` → 见「誓约重置券」+ 洗约说明 · **不见**灰字裸 id；斜杠行仍可见 | **PASS** |
| 3 | 未扩测 talent_reset / 其余 5 件；不宣称 B0.1；不叫挑刺；禁 wall-clock/DPS | **PASS** |
| 4 | 写本测报 · commit+push · ops=`[]` | **PASS**（本 tip） |

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 口 | proxy 25565 / login 25566 / play 25567 |
| 施工 tip | `d0830a7` fix(ni): B2.27 drop bare id from covenant reset lore |
| 设计 / 批准 | `8bb16c9` / `1d659d0` |
| 热更 | 测前 `ni reload` → `NeigeItems > 重载完毕` @ 02:42:36 CST |
| 账号 | 验收 `NiLlhgfz`（非 OP）· 辅助 `NiLolhgfz`（临时 OP，已 deop + LP unset） |
| 探针 | `/tmp/b227-mat-covenant-reset-ni-lore-copy-test.js` · JSON `/tmp/b227-mat-covenant-reset-ni-lore-copy-test.json`（未入 git） |
| UX | 自定义物走 NI ID `mat_ember_covenant_reset`；悬停=背包 display lore |

---

## 各点证据

### 1 · lore 列表无裸 id · 斜杠保留 · tip 仅删 1 行

```
rg -n 'mat_ember_covenant_reset' plugins/NeigeItems/Items/ember-covenant-talent.yml
→ L4 mat_ember_covenant_reset:   （键名 · 管理侧可留）
```

lore 块现网：

```
  lore:
    - '&8洗约 · 免晶钻'
    - '&7用于 /corerpg covenant set|reset'
```

字面 `&7mat_ember_covenant_reset`：**0**（已删）。  
斜杠行 `/corerpg covenant set|reset`：**仍在**（本窗保留，异于 B2.23）。

`git show d0830a7` hunk：

```
@@ -5,7 +5,6 @@ mat_ember_covenant_reset:
   material: PAPER
   name: '&6誓约重置券'
   lore:
-    - '&7mat_ember_covenant_reset'
     - '&8洗约 · 免晶钻'
     - '&7用于 /corerpg covenant set|reset'
```

| 项 | 现网 |
|----|------|
| name | `&6誓约重置券` |
| material | `PAPER` |
| lore×2 | 洗约 · 免晶钻；用于 /corerpg covenant set\|reset |
| enchantments | `DURABILITY: 1`（未漂） |
| hideflags | `HIDE_ENCHANTS`（未漂） |
| tip 文件 | **仅** `plugins/NeigeItems/Items/ember-covenant-talent.yml`（−1 行） |
| talent_reset `&7mat_*` | tip **未改**；现网仍留灰字（旁附 soft · 本岗不扩测） |

### 2 · NI give + 悬停轻测

| 步骤 | 结果 |
|------|------|
| `ni reload` | `NeigeItems > 重载完毕` @ 02:42:36 CST |
| OP `/ni give NiLlhgfz mat_ember_covenant_reset 1` | chat：`NeigeItems > 你得到了 1 个 誓约重置券` |
| 背包 NBT | name=`誓约重置券` · mat=`paper` · lore=`[洗约 · 免晶钻, 用于 /corerpg covenant set\|reset]` |
| 裸 id | lore **无** `mat_ember_covenant_reset` |
| 斜杠 | lore **有** `用于 /corerpg covenant set\|reset` |

### 3 · 禁项 / 未漂

| 项 | 证据 |
|----|------|
| tip 范围 | `d0830a7` **仅** covenant_reset lore 删 1 行裸 id |
| talent_reset / 其余 5 件 `&7mat_*` | **未扩测、未清**（talent_reset 仍留灰字） |
| 斜杠行 | **保留**（本窗不删；异于 B2.23） |
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
