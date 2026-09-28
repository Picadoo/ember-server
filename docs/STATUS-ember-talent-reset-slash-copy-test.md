# STATUS · B2.41 NI 天赋重置券 lore 去斜杠 · 轻测

**日期：** 2026-09-29 03:56 → 03:57 Asia/Shanghai（CST）  
**岗：** 余烬-测试岗执行器  
**依据：** 设计 `5e6d605` · 批准 `506ea1a` · 施工 tip `067002d`  
**范围：** `plugins/NeigeItems/Items/ember-covenant-talent.yml` 内 `mat_ember_talent_reset` lore L20 已改为「用于枢纽 · 天赋（日免费用尽后）」；L19「额外洗点 · 免晶钻」保留  
**禁项：** **禁改配置** · 禁 wall-clock/DPS/挑刺 · **不宣称其它斜杠 / B0.1 / soft 已清** · 勿带 dirty runtime · 不扩测 pet / covenant 再改  
**Verdict：** ✅ **PASS**（验收点全过 · ops=`[]` · 悬停 PASS）

---

## 一句话

天赋重置券 lore 无字面 `/corerpg`；L20「用于枢纽 · 天赋（日免费用尽后）」+ L19「额外洗点 · 免晶钻」；`ni reload` 后 `/ni give … mat_ember_talent_reset` 背包悬停同上；旁证 `mat_ember_covenant_reset` 仍「用于枢纽 · 誓约」、`ember-pets.yml` summon/feed 斜杠仍在；tip 仅换 1 行；ops=`[]`。

---

## 总评

| # | 验收点 | 结果 |
|---|--------|------|
| 1 | 该件 lore 无字面 `/corerpg`；L20 枢纽文案；L19 仍在 | **PASS** |
| 2 | 悬停确认（server 可起） | **PASS** |
| 3 | 旁证 `mat_ember_covenant_reset` 仍「用于枢纽 · 誓约」 | **PASS** |
| 4 | 旁证 `ember-pets.yml` summon/feed 斜杠仍在 | **PASS** |
| 5 | 写本测报 · commit+push · ops=`[]` | **PASS**（本 tip） |

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 口 | proxy 25565 / login 25566 / play 25567 |
| 施工 tip | `067002d` fix(ni): B2.41 talent reset lore drop /corerpg slash |
| 设计 / 批准 | `5e6d605` / `506ea1a` |
| 热更 | 测前 `ni reload` → `NeigeItems > 重载完毕` @ 03:56:50 CST |
| 账号 | 验收 `NiLo4wxx`（非 OP）· 辅助 `NiLoo4wxx`（临时 OP，已 deop + LP unset） |
| 探针 | `/tmp/b241-talent-reset-slash-copy-test.js` · JSON `/tmp/b241-talent-reset-slash-copy-test.json`（未入 git） |
| UX | 自定义物走 NI ID `mat_ember_talent_reset`；悬停=背包 display lore |

---

## 各点证据

### 1 · 该件 lore 无字面 `/corerpg` + L19/L20

```
rg -n '/corerpg|额外洗点|用于枢纽' plugins/NeigeItems/Items/ember-covenant-talent.yml
```

`mat_ember_talent_reset` lore 块现网：

```
  lore:
    - '&8额外洗点 · 免晶钻'
    - '&7用于枢纽 · 天赋（日免费用尽后）'
```

该文件字面 `/corerpg`：**0**。L19 / L20 均符合 PASS 条件。

### 2 · tip 范围与 hunk

`git show 067002d`：

```
-    - '&7用于 /corerpg talent reset（日免费用尽后）'
+    - '&7用于枢纽 · 天赋（日免费用尽后）'
```

仅 `plugins/NeigeItems/Items/ember-covenant-talent.yml`（1 行替换）；covenant_reset / pet 未入 hunk。

| 项 | 现网 |
|----|------|
| name | `&b天赋重置券` |
| material | `PAPER` |
| lore×2 | **额外洗点 · 免晶钻** / **用于枢纽 · 天赋（日免费用尽后）** |
| enchantments / hideflags | tip 相对 parent **零改** |
| 键名 | `mat_ember_talent_reset:`（管理侧 · 保留） |

### 3 · 旁证勿改

| 旁证 | 现网 | 结果 |
|------|------|------|
| `mat_ember_covenant_reset` L9 | `&7用于枢纽 · 誓约` | **PASS**（仍枢纽 · 誓约） |
| `ember-pets.yml` summon | `/corerpg pet summon` ×2 | **PASS**（斜杠仍在） |
| `ember-pets.yml` feed | `/corerpg pet feed` ×1 | **PASS**（斜杠仍在） |

**本岗未宣称** 其它斜杠或 B0.1 已清。

### 4 · NI give + 悬停轻测

| 步骤 | 结果 |
|------|------|
| `ni reload` | `NeigeItems > 重载完毕` @ 03:56:50 CST |
| OP `/ni give NiLo4wxx mat_ember_talent_reset 1` | chat：`NeigeItems > 你得到了 1 个 天赋重置券` |
| 背包 NBT | name=`天赋重置券` · mat=`paper` · lore=`[额外洗点 · 免晶钻, 用于枢纽 · 天赋（日免费用尽后）]` |
| 斜杠 | lore **无** `/corerpg` |

### 5 · 禁项 / 未漂

| 项 | 证据 |
|----|------|
| tip 范围 | `067002d` **仅** `plugins/NeigeItems/Items/ember-covenant-talent.yml`（1 行替换） |
| 其它斜杠 / B0.1 / soft / pet 去斜杠 | **本岗未宣称、未开、未清** |
| 本岗改配置 | **否**（只读 + 热更/给物/OP 生命周期） |
| wall-clock / DPS / 挑刺 | **未做** |

### 6 · ops

测后 `server-runtime/ops.json` = `[]` · `login-runtime/ops.json` = `[]`；临时 OP `NiLoo4wxx` 已 `deop`。

---

## Verdict

✅ **PASS · 勾销候选**（总控结案另开；本 tip 仅测报）。

ops=`[]`。悬停 **PASS**（非 SKIP）。
