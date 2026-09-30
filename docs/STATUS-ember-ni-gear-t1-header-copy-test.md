# STATUS · B2.57 NI gear-t1 文件头去管理口吻 · 纯静态薄验收

**日期：** 2026-09-30 19:44 CST（Asia/Shanghai）  
**岗：** 余烬-测试岗执行器  
**依据：** 施工 tip `8c5bb51` · 批准 `325b2e0` · tip `docs/design-ember-ni-gear-t1-header-copy.md`  
**口径：** 纯静态 · 禁开服测 · 禁长测/挑刺 · 本岗不改配置（仅测报）· **勿宣称 B0.1**  
**Verdict：** ✅ **PASS** · STATUS 已 push (`01a41b2`)

---

## 一句话

`ember-gear-t1.yml` L1–L2 已改为功能摘要/来源（无 stub、「插件岗」、斜杠）；L35 stub 分隔注释本窗允许保留；lore/数值相对 `8c5bb51^` 零漂（`git show 8c5bb51 --stat` 仅该文件 2 行）；ops=[]；未开服；未宣称 B0.1；未改日周深渊箱数量。

---

## 总评

| 验收点 | 结果 |
|--------|------|
| 1. L1 == `# T1 精炼装备与团本饰品：精炼刃、护符等功能摘要` | **PASS** |
| 2. L2 == `# 来源：日本高难、周本、团本` | **PASS** |
| 3. 文件头 L1–L2 无 stub/「插件岗」/斜杠；L35 stub 分隔注释本窗允许保留 | **PASS** |
| 4. lore/数值/正文相对 `8c5bb51^` 零漂（`git show 8c5bb51 --stat` 仅该文件 2 行） | **PASS** |
| 5. ops=[]；未开服；禁长测/挑刺；勿宣称 B0.1；未改日周深渊箱数量 | **PASS** |

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| 施工 tip | `8c5bb51` B2.57: NI gear-t1 文件头去管理口吻 |
| 设计 / 批准 | tip `docs/design-ember-ni-gear-t1-header-copy.md` / `325b2e0` |
| 目标文件 | `plugins/NeigeItems/Items/ember-gear-t1.yml` |
| 测法 | 纯静态（L1/L2 原文比对 + L1–L2 违禁词 rg + tip parent body diff + `--stat`/`--numstat`） |
| 热更 / 开服 / 开菜单 | **未碰** |
| ops | `[]`（`server-runtime/ops.json` / `login-runtime/ops.json`） |

---

## 各点证据

### 1–2 · L1 / L2 原文

```
# T1 精炼装备与团本饰品：精炼刃、护符等功能摘要
# 来源：日本高难、周本、团本
```

路径 `plugins/NeigeItems/Items/ember-gear-t1.yml`；`L1_exact True` · `L2_exact True`（与 PASS 条件一字对齐）。

### 3 · L1–L2 无违禁词 · L35 旁证

```bash
head -2 plugins/NeigeItems/Items/ember-gear-t1.yml | rg -n 'stub|插件岗|/'
# → rg: no match (PASS) · L1_forbidden none · L2_forbidden none
```

| 检查 | 结果 |
|------|------|
| stub | L1–L2 无 |
| 「插件岗」 | L1–L2 无 |
| 斜杠 `/` | L1–L2 无（顿号 `、` 替代） |
| L35 | `# --- 团本饰品 stub ---`（本窗允许保留） |

L33–L37 旁证：

```
    33	    - HIDE_ENCHANTS
    34	
    35	# --- 团本饰品 stub ---
    36	acc_ember_raid_ring:
    37	  material: GOLD_INGOT
```

### 4 · lore / 数值零漂 · 仅该文件 2 行

`8c5bb51` 相对 tip parent 仅改 L1–L2 两行注释：

```
git show 8c5bb51 --stat
 plugins/NeigeItems/Items/ember-gear-t1.yml | 4 ++--
 1 file changed, 2 insertions(+), 2 deletions(-)

git show 8c5bb51 --numstat
2	2	plugins/NeigeItems/Items/ember-gear-t1.yml
```

批前 → 批后：

| 位置 | 批前（`8c5bb51^`） | 批后（`8c5bb51`） |
|------|-------------------|-------------------|
| L1 | `# T1 精炼装备 + 团本饰品 stub — DESIGN-ember-growth-curve.md §3` | `# T1 精炼装备与团本饰品：精炼刃、护符等功能摘要` |
| L2 | `# 来源：日本高难 / 周本 / 团本；奖励接线交给插件岗（勿改日周深渊箱数量）` | `# 来源：日本高难、周本、团本` |
| L3+ | lore / material / enchantments 等正文（含 L35 stub 分隔） | 同左（`BODY_ZERO_DRIFT_OK` / L3+ 与 parent 逐行相同） |

`git show 8c5bb51 --name-only` 仅 `plugins/NeigeItems/Items/ember-gear-t1.yml` → 其它 NI / 日周深渊箱数量零触。

### 5 · ops=[] · 未开服 · 未宣称 B0.1

- `server-runtime/ops.json` → `[]`
- `login-runtime/ops.json` → `[]`
- 本窗未开服、未热更、未长测/挑刺
- **未宣称 B0.1**
- 本岗仅测报，未改任何玩法配置 / 日周深渊箱数量

---

## 回报摘要（交总控）

| 项 | 值 |
|----|-----|
| 总评 | **PASS** |
| 各点 | 1 PASS · 2 PASS · 3 PASS · 4 PASS · 5 PASS |
| 施工 tip SHA | `8c5bb51` |
| 测报 tip short SHA | `01a41b2` |
| 是否已 push | 是 (`01a41b2`) |
| 报告路径 | `docs/STATUS-ember-ni-gear-t1-header-copy-test.md` |
| ops | `[]` |
| 阻塞点 | 无 |
| L1 原文 | `# T1 精炼装备与团本饰品：精炼刃、护符等功能摘要` |
| L2 原文 | `# 来源：日本高难、周本、团本` |
| L1–L2 违禁词 rg | 命中 0（`stub`/`插件岗`/`/`） |
| L35 旁证 | `# --- 团本饰品 stub ---`（允许保留） |
| 零漂旁证 | `BODY_ZERO_DRIFT_OK` · `--numstat 2 2` |
| `git show 8c5bb51 --stat` | 仅 `ember-gear-t1.yml` · `4 ++--`（2 行替换） |

