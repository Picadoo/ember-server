# STATUS · B2.46 拆解菜单管理注释人话化 · TrMenu · 薄验收

**日期：** 2026-09-29 04:49 → 04:50 Asia/Shanghai（CST）  
**岗：** 余烬-测试岗执行器  
**依据：** 施工 tip `530c2f8` docs: humanize ember disassemble menu lore  
**范围：** `ember_disassemble.yml` 规则速览 lore 末行人话化（去 STATUS 路径）  
**禁项：** **禁改配置** · 禁改 scrap/reforge 数值/逻辑 · 禁长测/挑刺 · **不宣称 B0.1 已清** · 勿带 dirty runtime  
**Verdict：** ✅ **PASS**（验收点全过 · ops=`[]`）

---

## 一句话

`ember_disassemble.yml` 玩家 lore 无 `STATUS-ember-disassemble`（L1 YAML 注释可留）；L89=`§8点左侧即可分解或重铸`；hub→拆解→悬停「规则速览」见人话句、无 docs/status/STATUS.md 路径；未改 scrap/reforge；ops=`[]`。

---

## 总评

| # | 验收点 | 结果 |
|---|--------|------|
| 1 | 玩家 lore 段无 `STATUS-ember-disassemble`（L1 注释可留）；L89=`§8点左侧即可分解或重铸` | **PASS** |
| 2 | hub→拆解→悬停「规则速览」见人话句、无 docs/status/STATUS.md 路径 | **PASS** |
| 3 | 未改 scrap/reforge；本岗未改配置；ops=`[]`；写测报 · commit+push | **PASS** |

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 口 | proxy 25565 / login 25566 / play 25567 |
| 施工 tip | `530c2f8` docs: humanize ember disassemble menu lore |
| TrMenu | tip 后自动热更 `ember_disassemble.yml`（04:47:39 CST · 6ms）· live=`server-runtime` 与 `plugins/` diff 空 |
| 账号 | 验收 `AvPq0eqj`（非 OP · Lv.10）· 辅助 `AvOpq0eqj`（临时 OP，已 deop + LP unset） |
| 探针 | `/tmp/b246-disassemble-rule-copy-test.js` · JSON `/tmp/b246-disassemble-rule-copy-test.json`（未入 git） |
| 坐标 | hub (-18.5, 58, 110.5) 全程未变 |

---

## 各点证据

### 1 · 静态

```
L1  # 分解 / 重铸子菜单壳 — docs/status/STATUS-ember-disassemble.md   ← YAML 注释可留
L89 - '§8点左侧即可分解或重铸'                             ← 人话句
```

- 玩家 lore / 非注释行：`STATUS-ember-disassemble` → **0**
- tip `530c2f8`：仅 `ember_disassemble.yml` `1 file changed, 1 insertion(+), 1 deletion(-)`  
  （`§8详见 docs/status/STATUS-ember-disassemble.md` → `§8点左侧即可分解或重铸`）
- live sync：`plugins/` == `server-runtime/plugins/`（diff 空）

### 2 · UX 菜单路径 hub→拆解→悬停「规则速览」

| 步骤 | 结果 |
|------|------|
| `/ember` → hub | 标题「余烬 · 冒险枢纽」 |
| 点「分解」 | 标题「余烬 · 分解」· 槽见「分解装备 / 重铸词缀 / 规则速览 / 返回主菜单」 |
| 悬停「规则速览」lore | `分解：多余刃 / 护符 → 碎片 / 骨尘` · `低概率孔石…` · `重铸：刷次要词缀…` · `消耗重铸石 · 不耗核心` · **`点左侧即可分解或重铸`** · **无** `STATUS-ember-disassemble` / `docs/status/STATUS.md` |

（clickWindow 可能报 transaction timeout，属既有 TrMenu/mineflayer 吞事务；**菜单标题与槽位/lore 已切换**，以窗口 dump 为准。）

### 3 · 禁项

| 项 | 证据 | 判定 |
|----|------|------|
| tip 仅菜单 1 行 | `git show 530c2f8 --stat` → 仅 `ember_disassemble.yml` ±1 | **PASS** |
| 未改 scrap/reforge | tip name-only 无 scrap/reforge/ItemsAdder/体力 | **PASS** |
| 本岗未改配置 | 仅写测报 | **PASS** |
| 未进本 / 坐标 hub | (-18.5, 58, 110.5) 前后同 | **PASS** |
| 未宣称 B0.1 | 本报不涉及 B0.1 | **PASS** |
| 不叫挑刺 / 禁长测 | 仅菜单轻点 | **PASS** |
| 未带 dirty runtime | commit 仅 docs 测报 | **PASS** |
| ops=`[]` | play=`[]` · login=`[]`；临时 OP 已 deop | **PASS** |

---

## 阻塞点

无。

---

## 交付

- 报告：`docs/status/STATUS-ember-disassemble-rule-copy-test.md`
- 施工 tip SHA：`530c2f8`
- ops：`[]`
- push：见本 commit 是否已 push（失败则交总控代推）
