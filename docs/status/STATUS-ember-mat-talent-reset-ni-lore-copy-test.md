# STATUS · B2.28 NI 天赋重置券 lore 批 A · 轻测

**日期：** 2026-09-29 02:46 → 02:47 Asia/Shanghai（CST）  
**岗：** 余烬-测试岗执行器  
**依据：** 设计 `2c50336` · 批准 `8ff1b4d` · 施工 tip `c74dfdf`  
**范围：** `plugins/NeigeItems/Items/ember-covenant-talent.yml` 内 `mat_ember_talent_reset` lore 已删裸 id 行；键名/name「天赋重置券」/洗点说明/**`/corerpg talent reset…` 斜杠行保留**/enchantments/hideflags 应在；**covenant_reset 未被本 tip 回改**  
**禁项：** **禁改配置** · 禁 wall-clock/DPS/挑刺 · **不宣称 B0.1 已清** · 勿带 dirty runtime · **不扩测** 其余 4 件 · 本窗**保留**斜杠（与 B2.23 重铸石去斜杠不同）  
**Verdict：** ✅ **PASS**（验收点全过 · ops=`[]`）

---

## 一句话

天赋重置券 lore 列表无字面 `mat_ember_talent_reset`（键名保留）；name 仍「天赋重置券」、material=`PAPER`、说明「额外洗点 · 免晶钻」未漂；**斜杠行「用于 /corerpg talent reset（日免费用尽后）」仍在**；enchantments=`DURABILITY:1` / hideflags=`HIDE_ENCHANTS` 未漂；`ni reload` 后 `/ni give … mat_ember_talent_reset` 背包悬停无裸 id、见洗点+斜杠；tip 仅删 1 行；`mat_ember_covenant_reset` lore 仍无裸 id（B2.27 态未回改）；ops=`[]`。

---

## 总评

| # | 验收点 | 结果 |
|---|--------|------|
| 1 | lore/`git show`：无字面 `mat_ember_talent_reset`（键名可留）；仅删裸 id；**`/corerpg talent reset…` 仍在**；**covenant_reset 未回改** | **PASS** |
| 2 | 悬停轻测：给 `mat_ember_talent_reset` → 见「天赋重置券」+ 洗点说明 · **不见**灰字裸 id；斜杠行仍可见 | **PASS** |
| 3 | 未扩测其余 4 件；不宣称 B0.1；不叫挑刺；禁 wall-clock/DPS | **PASS** |
| 4 | 写本测报 · commit+push · ops=`[]` | **PASS**（本 tip） |

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 口 | proxy 25565 / login 25566 / play 25567 |
| 施工 tip | `c74dfdf` fix(ni): B2.28 drop bare id from talent reset lore |
| 设计 / 批准 | `2c50336` / `8ff1b4d` |
| 热更 | 测前 `ni reload` → `NeigeItems > 重载完毕` @ 02:46:48 CST |
| 账号 | 验收 `NiLlmuma`（非 OP）· 辅助 `NiLolmuma`（临时 OP，已 deop + LP unset） |
| 探针 | `/tmp/b228-mat-talent-reset-ni-lore-copy-test.js` · JSON `/tmp/b228-mat-talent-reset-ni-lore-copy-test.json`（未入 git） |
| UX | 自定义物走 NI ID `mat_ember_talent_reset`；悬停=背包 display lore |

---

## 各点证据

### 1 · lore 列表无裸 id · 斜杠保留 · tip 仅删 1 行 · covenant 未回改

```
rg -n 'mat_ember_(talent|covenant)_reset' plugins/NeigeItems/Items/ember-covenant-talent.yml
→ L4  mat_ember_covenant_reset:   （键名 · 管理侧可留）
→ L15 mat_ember_talent_reset:     （键名 · 管理侧可留）
```

`mat_ember_talent_reset` lore 块现网：

```
  lore:
    - '&8额外洗点 · 免晶钻'
    - '&7用于 /corerpg talent reset（日免费用尽后）'
```

字面 `&7mat_ember_talent_reset`：**0**（已删）。  
斜杠行 `/corerpg talent reset…`：**仍在**（本窗保留，异于 B2.23）。

`git show c74dfdf` hunk：

```
@@ -16,7 +16,6 @@ mat_ember_talent_reset:
   material: PAPER
   name: '&b天赋重置券'
   lore:
-    - '&7mat_ember_talent_reset'
     - '&8额外洗点 · 免晶钻'
     - '&7用于 /corerpg talent reset（日免费用尽后）'
```

| 项 | 现网 |
|----|------|
| name | `&b天赋重置券` |
| material | `PAPER` |
| lore×2 | 额外洗点 · 免晶钻；用于 /corerpg talent reset（日免费用尽后） |
| enchantments | `DURABILITY: 1`（未漂） |
| hideflags | `HIDE_ENCHANTS`（未漂） |
| tip 文件 | **仅** `plugins/NeigeItems/Items/ember-covenant-talent.yml`（−1 行） |
| covenant_reset lore | tip **未改**；现网仍无 `&7mat_ember_covenant_reset`；斜杠 `/corerpg covenant set\|reset` 仍在（B2.27 态） |

### 2 · NI give + 悬停轻测

| 步骤 | 结果 |
|------|------|
| `ni reload` | `NeigeItems > 重载完毕` @ 02:46:48 CST |
| OP `/ni give NiLlmuma mat_ember_talent_reset 1` | chat：`NeigeItems > 你得到了 1 个 天赋重置券` |
| 背包 NBT | name=`天赋重置券` · mat=`paper` · lore=`[额外洗点 · 免晶钻, 用于 /corerpg talent reset（日免费用尽后）]` |
| 裸 id | lore **无** `mat_ember_talent_reset` |
| 斜杠 | lore **有** `用于 /corerpg talent reset（日免费用尽后）` |

### 3 · 禁项 / 未漂

| 项 | 证据 |
|----|------|
| tip 范围 | `c74dfdf` **仅** talent_reset lore 删 1 行裸 id |
| covenant_reset | tip **未回改**；现网仍无灰字裸 id（B2.27 PASS 态） |
| 其余 4 件 `&7mat_*` | **未扩测、未清** |
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
