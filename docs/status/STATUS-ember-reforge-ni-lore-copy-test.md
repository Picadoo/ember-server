# STATUS · B2.22 重铸石 NI lore 批 A · 轻测

**日期：** 2026-09-29 02:18 → 02:19 Asia/Shanghai（CST）  
**岗：** 余烬-测试岗执行器  
**依据：** 设计 `115ebab` · 批准 `7724338` · 施工 tip `b24510c`  
**范围：** `plugins/NeigeItems/Items/ember-disassemble.yml` 内 `mat_ember_reforge_stone` lore 已删裸 id 行；键名/name「余烬重铸石」/其余 lore 应在  
**禁项：** **禁改配置** · 禁 wall-clock/DPS/挑刺 · **不宣称 B0.1 已清** · 勿带 dirty runtime · 禁附 B / 精英壳 · 禁改配方/给物/stone_ni_id/其它 NI 批  
**Verdict：** ✅ **PASS**（验收点全过 · ops=`[]`）

---

## 一句话

重铸石 lore 列表无字面 `mat_ember_reforge_stone`（键名保留）；name 仍「余烬重铸石」、material=`FIREBALL`、其余 3 行人话 lore 未漂；`ni reload` 后 `/ni give … mat_ember_reforge_stone` 背包悬停无裸 id；tip 仅删 1 行；`stone_ni_id` 未动；ops=`[]`。

---

## 总评

| # | 验收点 | 结果 |
|---|--------|------|
| 1 | lore 列表 `rg` 无字面 `mat_ember_reforge_stone`（键名行可留） | **PASS** |
| 2 | name 仍「余烬重铸石」；其余 lore / material 语义未漂 | **PASS** |
| 3 | 悬停/给物轻测无裸 id（NI give + 背包 NBT lore） | **PASS** |
| 4 | 未改配方/给物逻辑/stone_ni_id/其它 NI 批；不宣称 B0.1；不叫挑刺 | **PASS** |
| 5 | 写本测报 · commit+push · ops=`[]` | **PASS**（本 tip） |

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 口 | proxy 25565 / login 25566 / play 25567 |
| 施工 tip | `b24510c` fix(ni): B2.22 drop bare id from reforge stone lore |
| 设计 / 批准 | `115ebab` / `7724338` |
| 热更 | 测前 `ni reload` → `NeigeItems > 重载完毕` @ 02:18:15 CST |
| 账号 | 验收 `NiLkm57n`（非 OP）· 辅助 `NiLokm57n`（临时 OP，已 deop + LP unset） |
| 探针 | `/tmp/b222-reforge-ni-lore-copy-test.js` · JSON `/tmp/b222-reforge-ni-lore-copy-test.json`（未入 git） |
| UX | 自定义物走 NI ID `mat_ember_reforge_stone`；悬停=背包 display lore |

---

## 各点证据

### 1 · lore 列表无裸 id

```
rg -n 'mat_ember_reforge_stone' plugins/NeigeItems/Items/ember-disassemble.yml
→ L5 mat_ember_reforge_stone:   （键名 · 管理侧可留）
```

lore 块现网：

```
  lore:
    - '&8重铸石 · 刷新次要词缀'
    - '&8主词缀不随重铸乱飙'
    - '&7用于 /corerpg reforge'
```

字面 `&7mat_ember_reforge_stone`：**0**（已删）。

### 2 · name / material / 其余 lore 未漂

| 项 | 现网 |
|----|------|
| name | `&6余烬重铸石` |
| material | `FIREBALL` |
| lore×3 | 重铸石 · 刷新次要词缀 / 主词缀不随重铸乱飙 / 用于 /corerpg reforge |
| enchantments / hideflags | tip 相对 parent **零改** |

### 3 · NI give + 悬停轻测

| 步骤 | 结果 |
|------|------|
| `ni reload` | `NeigeItems > 重载完毕` |
| OP `/ni give NiLkm57n mat_ember_reforge_stone 1` | chat：`NeigeItems > 你得到了 1 个 余烬重铸石` |
| 背包 NBT | name=`余烬重铸石` · mat=`fire_charge` · lore=`[重铸石 · 刷新次要词缀, 主词缀不随重铸乱飙, 用于 /corerpg reforge]` |
| 裸 id | lore **无** `mat_ember_reforge_stone` |

### 4 · 禁项 / 未漂

| 项 | 证据 |
|----|------|
| tip 范围 | `b24510c` **仅** `plugins/NeigeItems/Items/ember-disassemble.yml`（−1 行） |
| tip diff | 仅删 `- '&7mat_ember_reforge_stone'`；name/其余 lore/material 仍在 diff 上下文 |
| `stone_ni_id` | `scrap.yml` 仍 `mat_ember_reforge_stone` |
| 配方 / 给物逻辑 / 其它 NI 批 / TrMenu / CoreRpg Java | tip **未碰** |
| B0.1 / 附 B / 精英壳 / 挑刺 | **本岗未宣称、未开** |
| 本岗改配置 | **否**（只读 + 热更/给物/OP 生命周期） |

### 5 · ops

测后 `server-runtime/ops.json` = `[]` · `login-runtime/ops.json` = `[]`；临时 OP 已 `deop`。

---

## Verdict

✅ **PASS · 勾销候选**（总控结案另开；本 tip 仅测报）。

ops=`[]`。
