# 设计稿 · shop 文件头在售摘要去斜杠（B2.52）

> **STATUS：待批 A（总控 · 2026-09-29 05:20 Asia/Shanghai）。**
> 本稿只定 TrMenu `ember_shop.yml` **YAML 文件头 L3 注释** 1 行：去掉「/corerpg coin」教斜杠，保留在售摘要。
> **本窗只处理维护者可见注释（非玩家 UI / 非 Open tell）；新句不写斜杠、不写 STATUS 路径。**
> **本窗 commit 只写 docs；玩法 / TrMenu / NI YAML 零改**（批 A 后由专岗只改 shop L3）。
> **禁**改 Icons / Open / 其它 actions；禁长测/挑刺；**勿宣称 B0.1 已清**；hub / NI soft 不捆。
> 前序：B2.51 enhance/socket **PASS · 勾销**（close `900eeed`）；本窗升收 shop 文件头 soft。

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | shop · **文件头注释去命令教学，保留在售摘要**（UX · B2.52） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-29 05:20 Asia/Shanghai |
| 关联 | live `plugins/TrMenu/menus/ember_shop.yml` · B2.51 PASS 旁证 |
| 状态 | **待批 A** · 批后交插件 TrMenu |
| tip 路径 | `docs/design-ember-shop-header-copy.md` |
| 上游 | B2.51 close `900eeed` · enhance/socket 已清；升本窗收 shop L3 |

### 硬约束（本稿）

| 可动（**仅若批 A 且另开 TrMenu 施工**） | 不可动（硬禁 · 含本稿 commit） |
|----------------------------------------|--------------------------------|
| `ember_shop.yml` **L3** 文件头注释 1 行 | Icons / Open Events / 其它 actions；Title / Layout |
| 仅替换为荐案在售摘要 | SKU、价格、货架、购买动作、玩法 YAML；hub / NI 文件 |
| | git push；热更 / reload；开精英预览壳；宣称 B0.1 已清；写 STATUS 路径；教玩家手打命令 |

---

## 1. 问题一句话

**商城 YAML 文件头 L3 仍带「/corerpg coin」教斜杠；这是维护注释中的命令教学，不是玩家 UI，保留在售摘要即可。**

---

## 2. 现网证据（只读扫网 · 本稿未改）

### 2.1 目标行（实际行号）

| 文件 | 行 | 现句（全文） | 可见性 |
|------|----|--------------|--------|
| `plugins/TrMenu/menus/ember_shop.yml` | **L3** | `# 在售：月卡、战令解锁、体力药、周体力包（SKU 别名仍 daily_ticket/weekly_ticket）· 软通货用 /corerpg coin` | 否 · YAML 注释 |

**文件头与 Icons / Open 确认：**

```
# plugins/TrMenu/menus/ember_shop.yml
L1 # 余烬商城 — DESIGN-ember-economy-monetization.md §5 + DESIGN-ember-rpg-systems.md §8.1
L2 # S0：体力药 / 周体力包（原日票/周票）
L3 # 在售：月卡、战令解锁、体力药、周体力包（SKU 别名仍 daily_ticket/weekly_ticket）· 软通货用 /corerpg coin
L4 （空）
L5 Title: '§6余烬 · 商城'
L7 Options:
L15 Layout:
L21 Events:
L22   Open:
L27 Icons:
```

Icons 与 Open 仅作现网确认，本窗不改。SKU、货架与购买动作同样不在本窗范围。

### 2.2 B2.51 旁证（施工已落地 · 本窗勿回改）

| 项目 | 现况 | 备注 |
|------|------|------|
| `ember_enhance.yml` / `ember_socket.yml` L2 | 功能摘要已替换 | B2.51 PASS · 勾销 · close `900eeed` |

### 2.3 旁记 soft（不捆施工）

| # | 文件 / 位置 | 现况 | 玩家可见？ |
|---|-------------|------|------------|
| 1 | `plugins/TrMenu/menus/ember_hub.yml` L188 | AFK tier picker 注释仍带命令 | 否 · 注释 |
| 2 | `plugins/NeigeItems/Items/ember-disassemble.yml` 文件头 | 仍有管理注释 | 否 · 注释 |
| 3 | `plugins/NeigeItems/Items/ember-pets.yml` 文件头 | 仍有模块待接线注释 | 否 · 注释 |

旁记 soft **不捆施工**，不在本稿验收中顺手清理。

---

## 3. 荐案（明确推荐其一）

| 方案 | 做法 | 利 | 弊 | 本轮 |
|------|------|----|----|------|
| **A. 只留在售摘要（推荐）** | 删除软通货命令教学，保留月卡、战令解锁、体力药、周体力包与 SKU 别名摘要 | 最短；维护者一眼懂在售范围；无斜杠 | 不在注释重复支付入口 | **推荐** |
| A′. 保留软通货功能词 | 末尾改成「软通货支付」 | 保留支付类别 | 仍超出纯在售摘要，信息收益低 | 不荐 |

**推荐句（批 A 后专岗改 TrMenu · 新旧全文）：**

| # | 位置 | 旧（全文） | 新（荐 · 全文） |
|---|------|------------|-----------------|
| A1 | `ember_shop.yml` **L3** | `# 在售：月卡、战令解锁、体力药、周体力包（SKU 别名仍 daily_ticket/weekly_ticket）· 软通货用 /corerpg coin` | `# 在售：月卡、战令解锁、体力药、周体力包（SKU 别名仍 daily_ticket/weekly_ticket）` |

**口径备忘：** 批后只动上表 1 行 L3 注释；Icons / Open / Title / Layout / SKU / 货架 / 购买动作 **一字不动**；荐案无斜杠、无 STATUS 路径、无命令教学。

**非目标 / 不施工：** 改 Icons / Open；改 SKU 或商品逻辑；改价格、货架、购买动作；改 hub L188；改 NI `ember-disassemble.yml` / `ember-pets.yml` 文件头；开精英壳；宣称 B0.1 已清；未批改 YAML；长测/挑刺；推送远端。

---

## 4. 验收（≤5，批 A 后另开施工）

1. **本设计 commit：** 仅本稿 + backlog；`ember_shop.yml`、其它玩法 YAML / NI / loot / DP / MM **零 diff**。
2. **若批 A：** 仅 `ember_shop.yml` L3 替换为荐案；新句保留在售摘要，**无**斜杠、**无** STATUS 路径、**无**命令教学。
3. **若批 A：** Icons / Open / Title / Layout / SKU / 货架 / 购买动作相对批前零 diff。
4. **若批 A · 禁项：** hub L188、NI 两份文件头仍不捆；未开精英壳；**不**宣称 B0.1 已清。
5. **禁长测/挑刺**（文件头注释非玩家 UI · 静态 diff / rg 即可）。

预期施工后静态检查（本设计窗不执行改动）：

```bash
rg -n '^#' plugins/TrMenu/menus/ember_shop.yml
# 仅 L3 替换为在售摘要；其它文件头与正文不动

git diff -- plugins/TrMenu/menus/ember_shop.yml
# 仅显示 L3 1 行
```

---

## 5. 专岗 / 动线

| 岗 | 活 | 时机 |
|----|------|------|
| **策划** | 本 tip + backlog；总控批 A | 本窗（文档 only） |
| **插件** | 批 A 后仅替换 shop L3 | 批 A 后 |
| **测试** | 静态 diff / rg；不测玩法，不叫挑刺 | 施工后 |

玩家菜单动线、Icons、Open tell、SKU 与购买动作均不变。本稿只处理维护者可见文件头注释，不新增玩家命令教学。

---

## 6. 回总控摘要

- **STATUS：待批 A** · tip `docs/design-ember-shop-header-copy.md`
- **荐案：** `ember_shop.yml` **L3** → `# 在售：月卡、战令解锁、体力药、周体力包（SKU 别名仍 daily_ticket/weekly_ticket）`
- **旧/新已全文对照：** 仅 L3 1 行；Icons / Open / SKU / 货架 / 购买动作 **零改**
- **旁记 soft 不捆：** hub L188 AFK 注释；NI `ember-disassemble.yml` / `ember-pets.yml` 文件头
- **本窗零改：** `ember_shop.yml` / TrMenu / NI / CoreRpg / 玩法 YAML **未动**
- **勿 push**；批后专岗改 TrMenu · 禁长测/挑刺 · **勿宣称 B0.1 已清**
- **前序确认：** B2.51 enhance/socket **PASS · 勾销**（close `900eeed`）

---

## 7. 总控批示

- [ ] **批 A** · 仅 `ember_shop.yml` L3 按上表荐案去斜杠教学、保留在售摘要；Icons / Open / SKU / 玩法零改
- [ ] **驳回** · 说明

**批示摘要（待填）：** —
