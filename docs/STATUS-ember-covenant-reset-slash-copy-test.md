# STATUS · B2.40 NI 誓约重置券 lore 去斜杠 · 薄验收

**日期：** 2026-09-29 03:51 → 03:52 Asia/Shanghai（CST）  
**岗：** 余烬-测试岗执行器  
**依据：** 设计 `d49dd7b` · 批准 `1a0f820` · 施工 tip `c7d55d1`  
**范围：** `plugins/NeigeItems/Items/ember-covenant-talent.yml` 内 `mat_ember_covenant_reset` lore L9 已改为「用于枢纽 · 誓约」；L8「洗约 · 免晶钻」保留；`mat_ember_talent_reset` 斜杠未动  
**禁项：** **禁改配置** · 禁 wall-clock/DPS/挑刺 · **不宣称其它斜杠 / B0.1 / soft 已清** · 勿带 dirty runtime · 不扩测 talent_reset 去斜杠  
**Verdict：** ✅ **PASS**（验收点全过 · ops=`[]` · 悬停 PASS）

---

## 一句话

誓约重置券 lore 无字面 `/corerpg`；可见「用于枢纽 · 誓约」+「洗约 · 免晶钻」；`ni reload` 后 `/ni give … mat_ember_covenant_reset` 背包悬停同上；`mat_ember_talent_reset` 仍含 `/corerpg talent reset`（本窗预期）；tip 仅换 1 行；ops=`[]`。

---

## 总评

| # | 验收点 | 结果 |
|---|--------|------|
| 1 | 该件 lore 无字面 `/corerpg` | **PASS** |
| 2 | 可见「用于枢纽 · 誓约」+「洗约 · 免晶钻」 | **PASS** |
| 3 | `mat_ember_talent_reset` 仍含 `/corerpg`（本窗预期，勿当 FAIL） | **PASS**（仍含） |
| 4 | 悬停确认（server 可起） | **PASS** |
| 5 | 写本测报 · commit+push · ops=`[]` | **PASS**（本 tip） |

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 口 | proxy 25565 / login 25566 / play 25567 |
| 施工 tip | `c7d55d1` fix(ni): B2.40 covenant reset lore drop /corerpg slash |
| 设计 / 批准 | `d49dd7b` / `1a0f820` |
| 热更 | 测前 `ni reload` → `NeigeItems > 重载完毕` @ 03:51:54 CST |
| 账号 | 验收 `NiLnykdq`（非 OP）· 辅助 `NiLonykdq`（临时 OP，已 deop + LP unset） |
| 探针 | `/tmp/b240-covenant-reset-slash-copy-test.js` · JSON `/tmp/b240-covenant-reset-slash-copy-test.json`（未入 git） |
| UX | 自定义物走 NI ID `mat_ember_covenant_reset`；悬停=背包 display lore |

---

## 各点证据

### 1 · 该件 lore 无字面 `/corerpg`

```
rg -n '/corerpg|洗约|用于枢纽' plugins/NeigeItems/Items/ember-covenant-talent.yml
```

`mat_ember_covenant_reset` lore 块现网：

```
  lore:
    - '&8洗约 · 免晶钻'
    - '&7用于枢纽 · 誓约'
```

lore 列表字面 `/corerpg`：**0**。

### 2 · 「用于枢纽 · 誓约」+「洗约 · 免晶钻」

| 项 | 现网 |
|----|------|
| name | `&6誓约重置券` |
| material | `PAPER` |
| lore×2 | **洗约 · 免晶钻** / **用于枢纽 · 誓约** |
| enchantments / hideflags | tip 相对 parent **零改** |
| 键名 | `mat_ember_covenant_reset:`（管理侧 · 保留） |

`git show c7d55d1` hunk：

```
-    - '&7用于 /corerpg covenant set|reset'
+    - '&7用于枢纽 · 誓约'
```

### 3 · `mat_ember_talent_reset` 仍含斜杠（本窗预期）

```
mat_ember_talent_reset:
  lore:
    - '&8额外洗点 · 免晶钻'
    - '&7用于 /corerpg talent reset（日免费用尽后）'
```

字面 `/corerpg`：**仍在**（勿当 FAIL；本窗不扩测去斜杠）。

### 4 · NI give + 悬停轻测

| 步骤 | 结果 |
|------|------|
| `ni reload` | `NeigeItems > 重载完毕` @ 03:51:54 CST |
| OP `/ni give NiLnykdq mat_ember_covenant_reset 1` | chat：`NeigeItems > 你得到了 1 个 誓约重置券` |
| 背包 NBT | name=`誓约重置券` · mat=`paper` · lore=`[洗约 · 免晶钻, 用于枢纽 · 誓约]` |
| 斜杠 | lore **无** `/corerpg` |

### 5 · 禁项 / 未漂

| 项 | 证据 |
|----|------|
| tip 范围 | `c7d55d1` **仅** `plugins/NeigeItems/Items/ember-covenant-talent.yml`（1 行替换） |
| talent_reset / 其它斜杠 / B0.1 / soft | **本岗未宣称、未开、未清** |
| 本岗改配置 | **否**（只读 + 热更/给物/OP 生命周期） |
| wall-clock / DPS / 挑刺 | **未做** |

### 6 · ops

测后 `server-runtime/ops.json` = `[]` · `login-runtime/ops.json` = `[]`；临时 OP `NiLonykdq` 已 `deop`。

---

## Verdict

✅ **PASS · 勾销候选**（总控结案另开；本 tip 仅测报）。

ops=`[]`。悬停 **PASS**（非 SKIP）。
