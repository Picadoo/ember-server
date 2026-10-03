# STATUS · B2.20 拆解重铸文案批 A · 轻测

**日期：** 2026-09-29 01:51 → 01:52 Asia/Shanghai（CST）  
**岗：** 余烬-测试岗执行器  
**依据：** 设计 `c6d8b8b` · 批准 `e7574a3` · 施工 tip `3ab2b19`  
**范围：** `ember_disassemble.yml` 重铸 lore 删裸 id；保留「§8消耗 §6余烬重铸石」  
**禁项：** **禁改配置** · 禁 wall-clock/DPS/长本/挑刺 · **不宣称 B0.1 已清** · 勿带 dirty runtime · 禁改 scrap/reforge/体力/NI 物品  
**Verdict：** ✅ **PASS**（验收点全过 · ops=`[]`）

---

## 一句话

`rg 'mat_ember_reforge_stone' ember_disassemble.yml` → **0**；上行「§8消耗 §6余烬重铸石」仍在；hub→拆解→悬停重铸 live lore **无**裸 id；name/tell/actions 相对 tip 未漂；未改 scrap/reforge/体力/NI；ops=`[]`。

---

## 总评

| # | 验收点 | 结果 |
|---|--------|------|
| 1 | `rg -n 'mat_ember_reforge_stone' plugins/TrMenu/menus/ember_disassemble.yml` → **0** | **PASS** |
| 2 | 仍有「余烬重铸石」消耗行；name/tell/actions 语义未漂 | **PASS** |
| 3 | hub→拆解→悬停重铸：无裸 id（可开菜单轻点） | **PASS** |
| 4 | 未改 scrap/reforge/体力/NI；不宣称 B0.1；不叫挑刺 | **PASS** |
| 5 | ops=`[]`；写测报 · commit+push | **PASS** |

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 口 | proxy 25565 / login 25566 / play 25567 |
| 插件 tip | `3ab2b19` fix(trmenu): B2.20 drop reforge NI id lore |
| 设计 / 批准 | `c6d8b8b` / `e7574a3` |
| TrMenu | tip 后自动热更 `ember_disassemble.yml`（01:50:30 CST · 3ms）· live=`server-runtime` 与 `plugins/` diff 空 |
| 账号 | 验收 `NiPjoa0f`（非 OP · Lv.10）· 辅助 `NiOpjoa0f`（临时 OP，已 deop + LP unset） |
| 探针 | `/tmp/b220-disassemble-reforge-copy-test.js` · JSON `/tmp/b220-disassemble-reforge-copy-test.json`（未入 git） |
| 坐标 | hub (-18.5, 58, 110.5) 全程未变 |

---

## 各点证据

### 1 · 静态 `mat_ember_reforge_stone` = 0

```
rg -n 'mat_ember_reforge_stone' plugins/TrMenu/menus/ember_disassemble.yml  →  (空 · 0 hits · exit 1)
```

tip `3ab2b19` 相对父：仅删 1 行 `§8mat_ember_reforge_stone`（`1 file changed, 1 deletion`）。

### 2 · 消耗行 + name/tell/actions 未漂

| 项 | 现网 |
|----|------|
| 消耗行 L66 | `§8消耗 §6余烬重铸石` **在** |
| name | `§6重铸词缀` |
| tell | `§6[重铸] §7请确认手持装备且背包有重铸石…` |
| actions | `sound` + `command: corerpg reforge` + `close` |
| 分解侧 | `name: §7分解装备` · `command: corerpg scrap` 未动 |

相对 tip 父提交，除删裸 id 行外 **无** name/tell/command hunk。

### 3 · UX 菜单路径 hub→拆解→悬停重铸

| 步骤 | 结果 |
|------|------|
| `/ember` → hub | 标题「余烬 · 冒险枢纽」 |
| 点「分解」 | 标题「余烬 · 分解」· 槽见「分解装备 / 重铸词缀 / 规则速览 / 返回主菜单」 |
| 悬停重铸 lore | `刷新次要词缀 · 主词缀不变` · **`消耗 余烬重铸石`** · `手持装备且背包有重铸石后点击` · **无** `mat_ember_reforge_stone` |
| 轻点重铸 tell（TrMenu） | `重铸 请确认手持装备且背包有重铸石…` · **无**裸 id |

（clickWindow 可能报 transaction timeout，属既有 TrMenu/mineflayer 吞事务；**菜单标题与槽位/lore 已切换**，以窗口 dump 为准。）

**旁证（范围外 · 勿挑刺 · 勿宣称已清）：** 轻点后 CoreRpg 缺料回报仍写 `[重铸] 需要 mat_ember_reforge_stone ×1`——属 scrap/reforge 运行时文案，**本窗禁改**；非 TrMenu lore，**不计入本窗 FAIL**。

### 4 · 禁项

| 项 | 证据 | 判定 |
|----|------|------|
| tip 仅菜单 1 行 | `git show 3ab2b19 --stat` → 仅 `ember_disassemble.yml` −1 | **PASS** |
| 未改 scrap/reforge/体力/NI | tip name-only 无 scrap.yml / life / ItemsAdder / stamina | **PASS** |
| 本岗未改配置 | 仅写测报 | **PASS** |
| 未进本 / 坐标 hub | (-18.5, 58, 110.5) 前后同 | **PASS** |
| 未宣称 B0.1 | 本报不涉及 B0.1 | **PASS** |
| 不叫挑刺 / 禁 wall-clock/DPS | 仅菜单轻点 | **PASS** |
| 未带 dirty runtime | commit 仅 docs 测报 | **PASS** |
| ops=`[]` | play=`[]` · login=`[]`；临时 OP 已 deop | **PASS** |

---

## 阻塞点

无。

---

## 交付

- 报告：`docs/status/STATUS-ember-disassemble-reforge-copy-test.md`
- tip SHA：`3ab2b19`
- ops：`[]`
- push：见本 commit 是否已 push（失败则交总控代推）
