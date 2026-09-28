# STATUS · B2.52 shop 文件头去斜杠 · 纯静态薄验收

**日期：** 2026-09-29 05:23 CST（Asia/Shanghai）  
**岗：** 余烬-测试岗执行器  
**依据：** 施工 tip `838d492` · 设计 `63db349` · 批准 `037b6ad` · tip `docs/design-ember-shop-header-copy.md`  
**口径：** 纯静态 · 禁开菜单 · 禁长测/挑刺 · 本岗不改配置（仅测报）· **勿宣称 B0.1**  
**Verdict：** ✅ **PASS** · STATUS 已 push (`38eeecc`)

---

## 一句话

`ember_shop.yml` L3 已改为在售摘要（无「软通货用 /corerpg coin」）；该文件 `/corerpg` 命中 0；Icons/Open/SKU/货架相对批前零漂（diff 仅 L3）；hub/NI 旁记未回改；ops=[]。

---

## 总评

| 验收点 | 结果 |
|--------|------|
| 1. `ember_shop.yml` L3 == `# 在售：月卡、战令解锁、体力药、周体力包（SKU 别名仍 daily_ticket/weekly_ticket）` | **PASS** |
| 2. 该文件无 `/corerpg`；Icons/Open/SKU/货架相对批前零漂（diff 仅 L3） | **PASS** |
| 3. hub/NI 旁记未回改；ops=[]；未开菜单；未宣称 B0.1 | **PASS** |

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| 施工 tip | `838d492` B2.52 remove slash from shop header teaching comment |
| 设计 / 批准 | `63db349` / `037b6ad` |
| 测法 | 纯静态（sed L3 + `rg '/corerpg'` + tip parent diff + full_minus_L3） |
| 热更 / 开服 / 开菜单 | **未碰** |
| ops | `[]` |

---

## 各点证据

### 1 · shop L3 原文

```
# 在售：月卡、战令解锁、体力药、周体力包（SKU 别名仍 daily_ticket/weekly_ticket）
```

与 PASS 条件一字对齐（`plugins/TrMenu/menus/ember_shop.yml` L3）。

### 2 · 无 `/corerpg` · Icons/Open/SKU/货架零漂

```bash
rg -n '/corerpg' plugins/TrMenu/menus/ember_shop.yml
# 输出：空；exit 1（无匹配）→ 命中数 0
```

`838d492` 相对 tip parent 仅改 L3 一行注释：

```
plugins/TrMenu/menus/ember_shop.yml | 2 +-
```

批前 → 批后：

| 文件 | L3 批前 | L3 批后 |
|------|---------|---------|
| shop | `# 在售：月卡、战令解锁、体力药、周体力包（SKU 别名仍 daily_ticket/weekly_ticket）· 软通货用 /corerpg coin` | `# 在售：月卡、战令解锁、体力药、周体力包（SKU 别名仍 daily_ticket/weekly_ticket）` |

整文件去掉 L3 后字节相等（`full_minus_L3_equal True`）；行数 169→169；`Options:` L7 / `Layout:` L15 / `Events:` L21 / `Open:` L22 / `Icons:` L27 行号与内容相对 tip parent **零 diff**。SKU 关键字计数不变（`daily_ticket`/`weekly_ticket`/`pass_unlock`/`monthly`/`stamina`）。

### 3 · hub/NI 旁记未回改 · ops=[]

`838d492^..838d492`（及 `037b6ad..HEAD`）对下列路径 **零 diff**（旁记 soft 未回改、未顺手清理）：

- `plugins/TrMenu/menus/ember_hub.yml`（L188 附近 AFK 注释仍含 `/corerpg afk`）
- `plugins/NeigeItems/Items/ember-disassemble.yml` / `ember-pets.yml` 文件头未动

本岗未碰服 · **未开菜单** · **ops=[]** · **未宣称 B0.1**。

---

## 回报摘要（交总控）

| 项 | 值 |
|----|-----|
| 总评 | **PASS** |
| 各点 | 1 PASS · 2 PASS · 3 PASS |
| 施工 tip SHA | `838d492` |
| 测报 tip short SHA | `38eeecc` |
| 是否已 push | **是** |
| 报告路径 | `docs/STATUS-ember-shop-header-copy-test.md` |
| ops | `[]` |
| 阻塞点 | 无 |
| L3 原文 | `# 在售：月卡、战令解锁、体力药、周体力包（SKU 别名仍 daily_ticket/weekly_ticket）` |
| `/corerpg` rg 证据 | 目标文件命中 0（`rg '/corerpg'` exit 1） |
| Icons/Open/SKU/货架零漂旁证 | tip diff 仅 L3；`full_minus_L3_equal True`；Open L22 / Icons L27 未漂；SKU 计数不变 |
