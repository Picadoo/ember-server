# STATUS · B2.30 NI 灾厄余烬 lore 批 A · 轻测

**日期：** 2026-09-29 02:56 → 02:57 Asia/Shanghai（CST）  
**岗：** 余烬-测试岗执行器  
**依据：** 设计 `3e7e1ba` · 批准 `a9f95e5` · 施工 tip `b9b1786`  
**范围：** `plugins/NeigeItems/Items/ember-abyss-calamity.yml` 内 `mat_calamity_ember` lore 已删裸 id 行；键名/name「灾厄余烬」/两行说明应在  
**禁项：** **禁改配置** · 禁 wall-clock/DPS/挑刺 · 勿动 `cosmetic_*` / protect_scroll / stable_charm · 勿带 dirty runtime · 不宣称 B0.1  
**Verdict：** ✅ **PASS**（验收点全过 · ops=`[]`）

---

## 一句话

灾厄余烬 lore 列表无字面 `mat_calamity_ember`（键名保留）；悬停仍见「灾厄余烬」+「世界 Boss · 变异材料」+「用于外观兑换 / 重铸」；tip 仅删 1 行；`cosmetic_calamity_shard` / protect_scroll / stable_charm 未动；ops=`[]`。

---

## 总评

| # | 验收点 | 结果 |
|---|--------|------|
| 1 | lore 列表 `rg`/`git show` 无字面 `&7mat_calamity_ember` / `mat_calamity_ember`（键名行可留） | **PASS** |
| 2 | 悬停轻测：给 `mat_calamity_ember` → 见「灾厄余烬」+ 两行说明 · **不见**灰字裸 id | **PASS** |
| 3 | 未误删说明；未动 `cosmetic_*` / protect_scroll / stable_charm；禁 wall-clock/DPS/挑刺 | **PASS** |
| 4 | 写本测报 · commit+push · ops=`[]` | **PASS**（本 tip） |

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 口 | proxy 25565 / login 25566 / play 25567 |
| 施工 tip | `b9b1786` fix(ni): B2.30 drop bare id from mat_calamity_ember lore |
| 设计 / 批准 | `3e7e1ba` / `a9f95e5` |
| 热更 | 测前 `ni reload` → `NeigeItems > 重载完毕` @ 02:56:55 CST |
| 账号 | 验收 `NiLlzvcl`（非 OP）· 辅助 `NiLolzvcl`（临时 OP，已 deop + LP unset） |
| 探针 | `/tmp/b230-mat-calamity-ember-ni-lore-copy-test.js` · JSON `/tmp/b230-mat-calamity-ember-ni-lore-copy-test.json`（未入 git） |
| UX | 自定义物走 NI ID `mat_calamity_ember`；悬停=背包 display lore |

---

## 各点证据

### 1 · lore 列表无裸 id · tip 仅删 1 行

```
rg -n 'mat_calamity_ember' plugins/NeigeItems/Items/ember-abyss-calamity.yml
→ L4 mat_calamity_ember:   （键名 · 管理侧可留）
```

lore 块现网：

```
  lore:
    - '&8世界 Boss · 变异材料'
    - '&8用于外观兑换 / 重铸'
```

字面 `&7mat_calamity_ember`：**0**（已删）。

`git show b9b1786` hunk：

```
@@ -5,7 +5,6 @@ mat_calamity_ember:
   material: BLAZE_POWDER
   name: '&4灾厄余烬'
   lore:
-    - '&7mat_calamity_ember'
     - '&8世界 Boss · 变异材料'
     - '&8用于外观兑换 / 重铸'
```

| 项 | 现网 |
|----|------|
| name | `&4灾厄余烬` |
| material | `BLAZE_POWDER` |
| lore×2 | 世界 Boss · 变异材料；用于外观兑换 / 重铸 |
| tip 文件 | **仅** `plugins/NeigeItems/Items/ember-abyss-calamity.yml`（−1 行） |
| cosmetic_calamity_shard | tip **未改**；现网仍留 `&7cosmetic_calamity_shard` |
| protect_scroll / stable_charm | tip **未列**（仍在 `ember-enhance-gems.yml`） |

### 2 · NI give + 悬停轻测

| 步骤 | 结果 |
|------|------|
| `ni reload` | `NeigeItems > 重载完毕` @ 02:56:55 CST |
| OP `/ni give NiLlzvcl mat_calamity_ember 1` | chat：`NeigeItems > 你得到了 1 个 灾厄余烬` |
| 背包 NBT | name=`灾厄余烬` · mat=`blaze_powder` · lore=`[世界 Boss · 变异材料, 用于外观兑换 / 重铸]` |
| 裸 id | lore **无** `mat_calamity_ember` |

### 3 · 禁项 / 未漂

| 项 | 证据 |
|----|------|
| tip 范围 | `b9b1786` **仅** calamity_ember lore 删 1 行 |
| cosmetic_* | 旁附仍留灰字裸 id（本岗不扩测清） |
| protect_scroll / stable_charm | tip 文件列表 **无** |
| 配方 / 给物逻辑 / TrMenu / CoreRpg Java | tip **未碰** |
| B0.1 / 挑刺 / wall-clock/DPS | **本岗未宣称、未开、未测** |
| 本岗改配置 | **否**（只读 + 热更/给物/OP 生命周期；测报 docs only） |

### 4 · ops

测后 `server-runtime/ops.json` = `[]` · `login-runtime/ops.json` = `[]`；临时 OP 已 `deop`。

---

## Verdict

✅ **PASS · 勾销候选**（总控结案另开；本 tip 仅测报）。

ops=`[]`。
