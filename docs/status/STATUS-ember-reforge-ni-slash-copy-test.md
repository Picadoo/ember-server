# STATUS · B2.23 重铸石去斜杠批 A · 轻测

**日期：** 2026-09-29 02:23 → 02:24 Asia/Shanghai（CST）  
**岗：** 余烬-测试岗执行器  
**依据：** 设计 `6485a89` · 批准 `75ff5e7` · 施工 tip `47baea0`  
**范围：** `plugins/NeigeItems/Items/ember-disassemble.yml` 内 `mat_ember_reforge_stone` lore 已改为「用于枢纽 · 拆解 → 重铸」  
**禁项：** **禁改配置** · 禁 wall-clock/DPS/挑刺 · **不宣称 B0.1 已清** · 勿带 dirty runtime · 禁附 B / 精英壳 · 禁改数值/配方/给物/其它 NI  
**Verdict：** ✅ **PASS**（验收点全过 · ops=`[]`）

---

## 一句话

重铸石 lore 列表无字面 `/corerpg`（文件头注释 L3 可留）；末行已为菜单口径「用于枢纽 · 拆解 → 重铸」；name「余烬重铸石」/其余 lore/material 未漂；`ni reload` 后 `/ni give … mat_ember_reforge_stone` 背包悬停无斜杠；tip 仅换 1 行；`stone_ni_id` 未动；ops=`[]`。

---

## 总评

| # | 验收点 | 结果 |
|---|--------|------|
| 1 | lore 列表无字面 `/corerpg`（文件头注释可留） | **PASS** |
| 2 | 新句为菜单口径；name/其余 lore 未漂 | **PASS** |
| 3 | NI give 悬停无斜杠 | **PASS** |
| 4 | 未改数值/配方/给物/其它 NI；不宣称 B0.1；不叫挑刺 | **PASS** |
| 5 | 写本测报 · commit+push · ops=`[]` | **PASS**（本 tip） |

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 口 | proxy 25565 / login 25566 / play 25567 |
| 施工 tip | `47baea0` fix(ni): B2.23 reforge stone lore drop /corerpg slash |
| 设计 / 批准 | `6485a89` / `75ff5e7` |
| 热更 | 测前 `ni reload` → `NeigeItems > 重载完毕` @ 02:23:22 CST |
| 账号 | 验收 `NiLkspw7`（非 OP）· 辅助 `NiLokspw7`（临时 OP，已 deop + LP unset） |
| 探针 | `/tmp/b223-reforge-ni-slash-copy-test.js` · JSON `/tmp/b223-reforge-ni-slash-copy-test.json`（未入 git） |
| UX | 自定义物走 NI ID `mat_ember_reforge_stone`；悬停=背包 display lore；菜单口径「枢纽 · 拆解 → 重铸」 |

---

## 各点证据

### 1 · lore 列表无字面 `/corerpg`

```
rg -n "/corerpg" plugins/NeigeItems/Items/ember-disassemble.yml
→ L3:# 命令（插件岗待接）：/corerpg scrap · /corerpg reforge   （文件头注释 · 可留）
```

lore 块现网：

```
  lore:
    - '&8重铸石 · 刷新次要词缀'
    - '&8主词缀不随重铸乱飙'
    - '&7用于枢纽 · 拆解 → 重铸'
```

lore 列表字面 `/corerpg`：**0**。

### 2 · 新句菜单口径 · name / 其余 lore 未漂

| 项 | 现网 |
|----|------|
| name | `&6余烬重铸石` |
| material | `FIREBALL` |
| lore×3 | 重铸石 · 刷新次要词缀 / 主词缀不随重铸乱飙 / **用于枢纽 · 拆解 → 重铸** |
| enchantments / hideflags | tip 相对 parent **零改** |
| 键名 | `mat_ember_reforge_stone:`（管理侧 · 保留） |

### 3 · NI give + 悬停轻测

| 步骤 | 结果 |
|------|------|
| `ni reload` | `NeigeItems > 重载完毕` @ 02:23:22 CST |
| OP `/ni give NiLkspw7 mat_ember_reforge_stone 1` | chat：`NeigeItems > 你得到了 1 个 余烬重铸石` |
| 背包 NBT | name=`余烬重铸石` · mat=`fire_charge` · lore=`[重铸石 · 刷新次要词缀, 主词缀不随重铸乱飙, 用于枢纽 · 拆解 → 重铸]` |
| 斜杠 | lore **无** `/corerpg` |

### 4 · 禁项 / 未漂

| 项 | 证据 |
|----|------|
| tip 范围 | `47baea0` **仅** `plugins/NeigeItems/Items/ember-disassemble.yml`（1 行替换） |
| tip diff | `- '&7用于 /corerpg reforge'` → `+ '&7用于枢纽 · 拆解 → 重铸'`；name/其余 lore/material **未出现在变更行** |
| `stone_ni_id` | `scrap.yml` 仍 `mat_ember_reforge_stone` |
| 配方 / 给物逻辑 / 其它 NI 批 / TrMenu / CoreRpg Java | tip **未碰** |
| B0.1 / 附 B / 精英壳 / 挑刺 | **本岗未宣称、未开** |
| 本岗改配置 | **否**（只读 + 热更/给物/OP 生命周期） |

### 5 · ops

测后 `server-runtime/ops.json` = `[]` · `login-runtime/ops.json` = `[]`；临时 OP `NiLokspw7` 已 `deop`（日志 @ 02:23:41 CST）。

---

## Verdict

✅ **PASS · 勾销候选**（总控结案另开；本 tip 仅测报）。

ops=`[]`。
