# STATUS · B2.37 NI 灾厄外观碎片 lore 批 A · 轻测

**日期：** 2026-09-29 03:34 → 03:34 Asia/Shanghai（CST）  
**岗：** 余烬-测试岗执行器  
**依据：** 设计 `3717031` · 批准 `d786875` · 施工 tip `3c49b53`  
**范围：** `plugins/NeigeItems/Items/ember-abyss-calamity.yml` 内 `cosmetic_calamity_shard` lore 已删裸 id 行；键名/name「灾厄外观碎片」/说明「灾厄掉落 · 外观碎片」「集齐可兑称号 / 特效」/enchantments/hideflags 应在；live Items `rg '&7cosmetic_'`（排除 bak）计数=**0**  
**禁项：** **禁改配置** · 禁 wall-clock/DPS/挑刺 · **不宣称 pet / 斜杠 / B0.1 / soft 已清** · 勿带 dirty runtime · **只报本件 cosmetic**  
**Verdict：** ✅ **PASS**（验收点全过 · ops=`[]` · `&7cosmetic_` rg=**0** · 悬停 **PASS**）

---

## 一句话

灾厄外观碎片 lore 列表无字面 `cosmetic_calamity_shard`（键名保留）；name 仍「灾厄外观碎片」、material=`NETHER_STAR`、说明「灾厄掉落 · 外观碎片」「集齐可兑称号 / 特效」未漂；enchantments=`DURABILITY:1` / hideflags=`HIDE_ENCHANTS` 未漂；`ni reload` 后 `/ni give … cosmetic_calamity_shard` 背包悬停无灰字裸 id；tip 仅删 1 行；live Items `rg '&7cosmetic_'`（`!*.bak*`）=**0**；ops=`[]`。

---

## 总评

| # | 验收点 | 结果 |
|---|--------|------|
| 1 | lore/`git show`：无字面 `&7cosmetic_calamity_shard` / `cosmetic_calamity_shard`（键名可留）；仅删裸 id | **PASS** |
| 2 | 悬停轻测：给 `cosmetic_calamity_shard` → 见「灾厄外观碎片」+ 两行说明 · **不见**灰字裸 id | **PASS** |
| 3 | live `rg -n '&7cosmetic_' plugins/NeigeItems/Items/ --glob '!*.bak*'` **计数=0** | **PASS**（计数=0） |
| 4 | 不宣称 pet / 斜杠 / B0.1 / soft；不叫挑刺；禁 wall-clock/DPS | **PASS** |
| 5 | 写本测报 · commit+push · ops=`[]` | **PASS**（本 tip） |

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 口 | proxy 25565 / login 25566 / play 25567 |
| 施工 tip | `3c49b53` fix(ni): B2.37 drop bare id from cosmetic_calamity_shard lore |
| 设计 / 批准 | `3717031` / `d786875` |
| 热更 | 测前 `ni reload` → `NeigeItems > 重载完毕` @ 03:34:14 CST |
| 账号 | 验收 `NiLnbv03`（非 OP）· 辅助 `NiLonbv03`（临时 OP，已 deop + LP unset） |
| 探针 | `/tmp/b237-cosmetic-calamity-shard-ni-lore-copy-test.js` · JSON `/tmp/b237-cosmetic-calamity-shard-ni-lore-copy-test.json`（未入 git） |
| UX | 自定义物走 NI ID `cosmetic_calamity_shard`；悬停=背包 display lore |

---

## 各点证据

### 1 · lore 列表无裸 id · tip 仅删 1 行

```
rg -n 'cosmetic_calamity_shard' plugins/NeigeItems/Items/ember-abyss-calamity.yml
→ L15 cosmetic_calamity_shard:   （键名 · 管理侧可留）
```

lore 块现网：

```
  lore:
    - '&8灾厄掉落 · 外观碎片'
    - '&8集齐可兑称号 / 特效'
```

字面 `&7cosmetic_calamity_shard` / `cosmetic_calamity_shard`（lore 内）：**0**（已删）。

`git show 3c49b53` hunk：

```
@@ -16,7 +16,6 @@ cosmetic_calamity_shard:
   material: NETHER_STAR
   name: '&d灾厄外观碎片'
   lore:
-    - '&7cosmetic_calamity_shard'
     - '&8灾厄掉落 · 外观碎片'
     - '&8集齐可兑称号 / 特效'
```

| 项 | 现网 |
|----|------|
| name | `&d灾厄外观碎片` |
| material | `NETHER_STAR` |
| lore×2 | 灾厄掉落 · 外观碎片；集齐可兑称号 / 特效 |
| enchantments | `DURABILITY: 1`（未漂） |
| hideflags | `HIDE_ENCHANTS`（未漂） |
| tip 文件 | **仅** `plugins/NeigeItems/Items/ember-abyss-calamity.yml`（−1 行） |
| mat_calamity_ember | tip **未改**；仍无裸 id（B2.30） |

### 2 · NI give + 悬停轻测

| 步骤 | 结果 |
|------|------|
| `ni reload` | `NeigeItems > 重载完毕` @ 03:34:14 CST |
| OP `/ni give NiLnbv03 cosmetic_calamity_shard 1` | chat：`NeigeItems > 你得到了 1 个 灾厄外观碎片` |
| 背包 NBT | name=`灾厄外观碎片` · mat=`nether_star` · lore=`[灾厄掉落 · 外观碎片, 集齐可兑称号 / 特效]` |
| 裸 id | lore **无** `cosmetic_calamity_shard` |
| 说明 | lore **有**「灾厄掉落 · 外观碎片」+「集齐可兑称号 / 特效」 |

### 3 · live `&7cosmetic_` rg 计数=0

```
rg -n '&7cosmetic_' plugins/NeigeItems/Items/ --glob '!*.bak*'
→ （无命中 · exit 1）
计数 = 0
```

**仅本轨 live 宣称**（本件 cosmetic 去裸 id · `&7cosmetic_`=0）；**不宣称** bak / pet / 斜杠 / soft / B0.1 已清。

### 4 · 禁项 / 未漂

| 项 | 证据 |
|----|------|
| tip 范围 | `3c49b53` **仅** cosmetic_calamity_shard lore 删 1 行裸 id |
| 说明行 | **保留**（未误删） |
| mat_calamity_ember | tip **未动**；仍无裸 id |
| 配方 / 给物逻辑 / TrMenu / CoreRpg Java / pet / 斜杠 | tip **未碰** |
| enchantments / hideflags | tip **未改**；现网仍 DURABILITY:1 / HIDE_ENCHANTS |
| B0.1 / soft / 附 B / 精英壳 / 挑刺 / wall-clock/DPS | **本岗未宣称、未开、未测** |
| pet / 斜杠 | **不宣称已清** |
| 本岗改配置 | **否**（只读 + 热更/给物/OP 生命周期；测报 docs only） |

### 5 · ops

测后 `server-runtime/ops.json` = `[]` · `login-runtime/ops.json` = `[]`；临时 OP 已 `deop`。

---

## Verdict

✅ **PASS · 勾销候选**（总控结案另开；本 tip 仅测报）。

ops=`[]` · `&7cosmetic_` rg 计数=**0** · 悬停 **PASS**。
