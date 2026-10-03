# STATUS · B2.29 NI 余烬魂尘 lore 批 A · 轻测

**日期：** 2026-09-29 02:51 → 02:52 Asia/Shanghai（CST）  
**岗：** 余烬-测试岗执行器  
**依据：** 设计 `bc58aaf` · 批准 `c87423c` · 施工 tip `9875d1b`  
**范围：** `plugins/NeigeItems/Items/ember-pets.yml` 内 `mat_ember_soul_dust` lore 已删裸 id 行；键名/name「余烬魂尘」/说明/`/corerpg pet feed`/「不消耗核心」应在  
**禁项：** **禁改配置** · 禁 wall-clock/DPS/挑刺 · **不宣称 B0.1 已清** · 勿带 dirty runtime · **勿动** `pet_ember_*` · 禁附 B / 精英壳  
**Verdict：** ✅ **PASS**（验收点全过 · ops=`[]`）

---

## 一句话

魂尘 lore 列表无字面 `mat_ember_soul_dust`（键名保留）；name 仍「余烬魂尘」、material=`SUGAR`、说明「挂机 / 副本副产 · 可堆叠」、**`/corerpg pet feed`**、「不消耗核心」未漂；`ni reload` 后 `/ni give … mat_ember_soul_dust` 背包悬停无裸 id；tip 仅删 1 行；`pet_ember_*` 仍留灰字（未扩测清）；ops=`[]`。

---

## 总评

| # | 验收点 | 结果 |
|---|--------|------|
| 1 | lore 列表 `rg`/`git show` 无字面 `mat_ember_soul_dust`（键名行可留）；仅删 `&7mat_ember_soul_dust` | **PASS** |
| 2 | 悬停轻测：给 `mat_ember_soul_dust` → 见「余烬魂尘」+ 说明 + **`/corerpg pet feed`** +「不消耗核心」· **不见**灰字裸 id | **PASS** |
| 3 | 未动 `pet_ember_*`；不宣称 B0.1；不叫挑刺；禁 wall-clock/DPS | **PASS** |
| 4 | 写本测报 · commit+push · ops=`[]` | **PASS**（本 tip） |

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 口 | proxy 25565 / login 25566 / play 25567 |
| 施工 tip | `9875d1b` fix(ni): B2.29 drop bare id from mat_ember_soul_dust lore |
| 设计 / 批准 | `bc58aaf` / `c87423c` |
| 热更 | 测前 `ni reload` → `NeigeItems > 重载完毕` @ 02:51:26 CST |
| 账号 | 验收 `NiLlsszg`（非 OP）· 辅助 `NiLolsszg`（临时 OP，已 deop + LP unset） |
| 探针 | `/tmp/b229-mat-soul-dust-ni-lore-copy-test.js` · JSON `/tmp/b229-mat-soul-dust-ni-lore-copy-test.json`（未入 git） |
| UX | 自定义物走 NI ID `mat_ember_soul_dust`；悬停=背包 display lore；保留斜杠 `/corerpg pet feed` |

---

## 各点证据

### 1 · lore 列表无裸 id · tip 仅删 1 行

```
rg -n 'mat_ember_soul_dust' plugins/NeigeItems/Items/ember-pets.yml
→ L31 mat_ember_soul_dust:   （键名 · 管理侧可留）
```

lore 块现网：

```
  lore:
    - '&8挂机 / 副本副产 · 可堆叠'
    - '&a喂使魔：/corerpg pet feed'
    - '&7不消耗核心'
```

字面 `&7mat_ember_soul_dust` / `mat_ember_soul_dust`（lore 行）：**0**（已删）。

`git show 9875d1b` hunk：

```
@@ -32,7 +32,6 @@ mat_ember_soul_dust:
   material: SUGAR
   name: '&b余烬魂尘'
   lore:
-    - '&7mat_ember_soul_dust'
     - '&8挂机 / 副本副产 · 可堆叠'
     - '&a喂使魔：/corerpg pet feed'
     - '&7不消耗核心'
```

| 项 | 现网 |
|----|------|
| name | `&b余烬魂尘` |
| material | `SUGAR` |
| lore×3 | 挂机/副本副产 · 可堆叠；喂使魔：`/corerpg pet feed`；不消耗核心 |
| tip 文件 | **仅** `plugins/NeigeItems/Items/ember-pets.yml`（−1 行） |
| pet_ember_ashling / pet_ember_cinder | tip **未改**；现网仍留 `&7pet_ember_*` 灰字（旁附 soft · 本岗不扩测） |

### 2 · NI give + 悬停轻测

| 步骤 | 结果 |
|------|------|
| `ni reload` | `NeigeItems > 重载完毕` @ 02:51:26 CST |
| OP `/ni give NiLlsszg mat_ember_soul_dust 1` | chat：`NeigeItems > 你得到了 1 个 余烬魂尘` |
| 背包 NBT | name=`余烬魂尘` · mat=`sugar` · lore=`[挂机 / 副本副产 · 可堆叠, 喂使魔：/corerpg pet feed, 不消耗核心]` |
| 裸 id | lore **无** `mat_ember_soul_dust` |
| 斜杠 | lore **保留** `/corerpg pet feed` |

### 3 · 禁项 / 未漂

| 项 | 证据 |
|----|------|
| tip 范围 | `9875d1b` **仅** soul_dust lore 删 1 行 |
| pet_ember_* | tip **未碰**；现网仍留 `&7pet_ember_*` 灰字 |
| 斜杠行 | **未误删**（静态 + 悬停均见 `/corerpg pet feed`） |
| 配方 / 给物逻辑 / TrMenu / CoreRpg Java | tip **未碰** |
| B0.1 / 附 B / 精英壳 / 挑刺 / wall-clock/DPS | **本岗未宣称、未开、未测** |
| 本岗改配置 | **否**（只读 + 热更/给物/OP 生命周期；测报 docs only） |

### 4 · ops

测后 `server-runtime/ops.json` = `[]` · `login-runtime/ops.json` = `[]`；临时 OP 已 `deop`。

---

## Verdict

✅ **PASS · 勾销候选**（总控结案另开；本 tip 仅测报）。

ops=`[]`。
