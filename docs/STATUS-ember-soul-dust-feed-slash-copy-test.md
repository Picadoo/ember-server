# STATUS · B2.44 NI 余烬魂尘 lore feed 去斜杠 · 轻测

**日期：** 2026-09-29 04:11 → 04:12 Asia/Shanghai（CST）  
**岗：** 余烬-测试岗执行器  
**依据：** 设计 `8ef2acb` · 批准 `a82321d` · 施工 tip `513bf8b`  
**范围：** `plugins/NeigeItems/Items/ember-pets.yml` 内 `mat_ember_soul_dust` lore L34 已改为「用于枢纽 · 使魔 · 投喂」（保留 `&a`）；L33/L35/name 保留；ashling/cinder 枢纽文案未回改  
**禁项：** **禁改配置** · 禁 wall-clock/DPS/挑刺 · **不宣称其它斜杠 / B0.1 / soft 已清** · 勿带 dirty runtime · 不扩测其它斜杠  
**Verdict：** ✅ **PASS**（验收点全过 · ops=`[]` · 悬停 PASS）

---

## 一句话

余烬魂尘 lore **无**字面 `/corerpg`；L34「`&a`用于枢纽 · 使魔 · 投喂」· L33/L35 仍在；`ni reload` 后 `/ni give … mat_ember_soul_dust` 背包悬停见枢纽投喂文案、无斜杠；旁证 ashling/cinder 仍「用于枢纽 · 使魔」、`ember-disassemble.yml` L3 管理注释仍可含 `/corerpg`；tip 仅换 1 行；ops=`[]`。

---

## 总评

| # | 验收点 | 结果 |
|---|--------|------|
| 1 | `rg`：该件 lore **无**字面 `/corerpg`；L34=`&a用于枢纽 · 使魔 · 投喂`；L33/L35 仍在 | **PASS** |
| 2 | 悬停：给物见枢纽文案、无斜杠 | **PASS** |
| 3 | 旁证勿改：ashling/cinder 仍「用于枢纽 · 使魔」；`ember-disassemble.yml` L3 管理注释仍可含 `/corerpg`（非本窗 FAIL） | **PASS** |
| 4 | 不宣称其它斜杠 / B0.1；不叫挑刺；禁 wall-clock/DPS | **PASS** |
| 5 | 写本测报 · commit+push · ops=`[]` | **PASS**（本 tip） |

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 口 | proxy 25565 / login 25566 / play 25567 |
| 施工 tip | `513bf8b` fix(ni): B2.44 soul dust feed lore drop /corerpg slash |
| 设计 / 批准 | `8ef2acb` / `a82321d` |
| 热更 | 测前 `ni reload` → `NeigeItems > 重载完毕` @ 04:11:54 CST |
| 账号 | 验收 `NiLooadg`（非 OP）· 辅助 `NiLoooadg`（临时 OP，已 deop + LP unset） |
| 探针 | `/tmp/b244-soul-dust-feed-slash-copy-test.js` · JSON `/tmp/b244-soul-dust-feed-slash-copy-test.json`（未入 git） |
| UX | 自定义物走 NI ID `mat_ember_soul_dust`；悬停=背包 display lore |

---

## 各点证据

### 1 · 该件 lore 无字面 `/corerpg` · L34 枢纽投喂 · L33/L35 仍在

```
rg -n 'mat_ember_soul_dust|/corerpg|用于枢纽' plugins/NeigeItems/Items/ember-pets.yml
→ L10 - '&7用于枢纽 · 使魔'          （ashling · 旁证）
→ L22 - '&7用于枢纽 · 使魔'          （cinder · 旁证）
→ L29  mat_ember_soul_dust:   （键名）
→ L34 - '&a用于枢纽 · 使魔 · 投喂'
ember-pets.yml 全文字面 /corerpg：**0**
```

`mat_ember_soul_dust` lore 块现网：

```
  lore:
    - '&8挂机 / 副本副产 · 可堆叠'
    - '&a用于枢纽 · 使魔 · 投喂'
    - '&7不消耗核心'
```

soul_dust lore 列表字面 `/corerpg`：**0**。

| 项 | 现网 |
|----|------|
| name | `&b余烬魂尘` |
| material | `SUGAR` |
| L33 | `&8挂机 / 副本副产 · 可堆叠` |
| L34 | `&a用于枢纽 · 使魔 · 投喂` |
| L35 | `&7不消耗核心` |

`git show 513bf8b` hunk：

```
-    - '&a喂使魔：/corerpg pet feed'
+    - '&a用于枢纽 · 使魔 · 投喂'
```

tip 文件：**仅** `plugins/NeigeItems/Items/ember-pets.yml`（1 行替换）。

### 2 · NI give + 悬停轻测

| 步骤 | 结果 |
|------|------|
| `ni reload` | `NeigeItems > 重载完毕` @ 04:11:54 CST |
| OP `/ni give NiLooadg mat_ember_soul_dust 1` | chat：`NeigeItems > 你得到了 1 个 余烬魂尘` |
| 背包 NBT | name=`余烬魂尘` · mat=`sugar` · lore=`[挂机 / 副本副产 · 可堆叠, 用于枢纽 · 使魔 · 投喂, 不消耗核心]` |
| 斜杠 | lore **无** `/corerpg` |
| 枢纽文案 | lore **有**「用于枢纽 · 使魔 · 投喂」 |
| 悬停 | **PASS**（非 SKIP） |

### 3 · 旁证勿改

| 旁证 | 现网 | 结果 |
|------|------|------|
| `pet_ember_ashling` L10 | `- '&7用于枢纽 · 使魔'` | **仍在**（预期） |
| `pet_ember_cinder` L22 | `- '&7用于枢纽 · 使魔'` | **仍在**（预期） |
| `ember-disassemble.yml` L3 | `# 命令（插件岗待接）：/corerpg scrap · /corerpg reforge` | **仍可含** `/corerpg`（管理注释 · **非本窗 FAIL**） |

**本岗不宣称** 其它斜杠 / B0.1 / soft 已清。

### 4 · 禁项 / 未漂

| 项 | 证据 |
|----|------|
| tip 范围 | `513bf8b` **仅** soul_dust L34 一行替换 |
| 其它斜杠 / B0.1 / soft | **本岗未宣称、未开、未清** |
| 本岗改配置 | **否**（只读 + 热更/给物/OP 生命周期；测报 docs only） |
| wall-clock / DPS / 挑刺 | **未做** |

### 5 · ops

测后 `server-runtime/ops.json` = `[]` · `login-runtime/ops.json` = `[]`；临时 OP `NiLoooadg` 已 `deop`；LP `neigeitems.admin` / `neigeitems.*` 已 unset。

---

## Verdict

✅ **PASS · 勾销候选**（总控结案另开；本 tip 仅测报）。

悬停：**PASS** · 旁证 ashling/cinder 枢纽仍在 · disassemble L3 管理注释仍可含 `/corerpg` · ops=`[]`。
