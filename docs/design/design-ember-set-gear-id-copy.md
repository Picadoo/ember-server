# 设计稿 · 套装菜单 `gear_ember_*` / 团戒 NI 人话对齐（B2.19）

> **STATUS：已批 A · 待插件施工。** 本稿只定 **玩家可见** TrMenu `ember_set.yml` 中 4 句裸 NI id → 中文；**禁**改套装数值 / 掉落 / 体力 / NI 物品文件 / loot / DP / MM。  
> 债源：B2.18 PASS 旁附（`91061b3` / tip `docs/design/design-ember-reward-ni-id-align.md` §B）；backlog 软观察「套装 `gear_ember_*` 可升 B2.19」。  
> 对齐：B2.18 已清五入口奖励页 `§8NI`；UX「文案短清楚 · TrMenu 点击 · NI ID 管理侧可留」。  
> 排除本轮：刚结 B2.6–B2.18 / B-flex-1/2 / B-anvil-1；精英预览厚壳（hub 一点 `corerpg elite start`、无 P / 无独立 rewards → 证据不足勿硬开）；NI 物品 lore 内 `&7gear_ember_*`（另软挂）；拆解页 `mat_ember_reforge_stone`（方案 B soft · 勿双上）；墙钟/DPS；霜锈前压；断塔无证据升 B；B0.1 除非新证据；四件甲/锻炉重做/誓约大改。**勿宣称 B0.1 已清。** 与 B-flex / B-anvil **不捆**。

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | 套装菜单 · **玩家可见 `gear_ember_*` / `acc_ember_raid_ring` 人话对齐**（UX · B2.19） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-29 01:43 Asia/Shanghai |
| 关联 | live `plugins/TrMenu/menus/ember_set.yml` · B2.18 旁附 B1～B4 |
| 状态 | **已批 A · 待插件** |
| 关联 STATUS / 稿 | backlog 挂 **B2.19** · 批后 STATUS-approve / 测报 |

### 硬约束（本稿）

| 可动（**仅若批 A 且另开施工**） | 不可动（硬禁 · 含本稿 commit） |
|--------------------------------|--------------------------------|
| `ember_set.yml` 刃 lore×2 + 戒 lore + 戒 tell 共 **4** 句玩家可见字符串（见 §3） | 套装 2 件激活逻辑 / `set.yml` 数值 / 掉落 / 体力 cost |
| 热更 / TrMenu reload（服约定） | NI 物品 YAML（含 lore 内 `&7gear_ember_*`）；其它 TrMenu 页；loot / DP / MM |
| | git push；开精英预览子菜单壳；宣称 B0.1 已清；未批改玩法 YAML |

---

## 1. 问题一句话

**套装页主名与说明已中文，刃/戒 lore 与戒 tell 仍印 `gear_ember_blade` / `gear_ember_t*_blade` / `acc_ember_raid_ring` 裸 id——玩家悬停与点击即见管理噪音；属 B2.18 已点名、可菜单轻测、零改数值的极薄文案债。**

---

## 2. 现网事实（调研 · 本稿未改）

| 项 | 现网 / 证据 |
|----|-------------|
| 入口 | `/ember` → hub → 套装 → `ember_set`（TrMenu 点击；Open tell 已人话） |
| **脏点计数** | **4** 处玩家可见裸 id（刃 lore×2 + 戒 lore + 戒 tell） |
| L48 | `§8T0 gear_ember_blade` |
| L49 | `§8T1/T2/T3 gear_ember_t*_blade` |
| L64 | `§7套装件 B · NI acc_ember_raid_ring` |
| L73 | `tell: … §7acc_ember_raid_ring · 周首通保底…` |
| 对照 · 已中文 | 刃 `name: §6余烬之刃`；戒 `name: §6余烬团本之戒`；刃 tell（L56）已写「任意余烬之刃（T0～T3）」无裸 id |
| NI 显示名 | `gear_ember_blade`→余烬之刃；`t1`→余烬精炼之刃；`t2`→余烬深核之刃；`t3`→余烬灾厄之刃；`acc_ember_raid_ring`→余烬团本之戒（**本窗不改 NI 文件**） |
| 候选对比 · 拆解 | `ember_disassemble.yml` L67：`§8mat_ember_reforge_stone`（上行已有「余烬重铸石」）—— **1** 行次痛；**方案 B soft，勿与 A 双上** |
| 候选对比 · 精英预览 | hub 精英一点进本、**无 P / 无独立 rewards** → 厚壳；**勿硬开** |
| 候选对比 · NI 物品 lore | `&7gear_ember_*` 等 —— 物品岗另软挂，**不进本窗 A** |
| 扫网结论 | **无**比本 4 句更实、更高优的更薄可勾销债；默认仍推本窗（拆解 1 行可写下一项 soft） |

### 替换口径（展示层 · 不改套装逻辑）

#### A · 套装菜单 4 句（推荐 · 升自 B2.18 旁附）

| # | 位置 | 旧 | 新 |
|---|------|----|----|
| A1 | 刃 lore L48 | `§8T0 gear_ember_blade` | `§8T0 余烬之刃` |
| A2 | 刃 lore L49 | `§8T1/T2/T3 gear_ember_t*_blade` | `§8T1/T2/T3 精炼/深核/灾厄之刃` |
| A3 | 戒 lore L64 | `§7套装件 B · NI acc_ember_raid_ring` | `§7套装件 B · 余烬团本之戒` |
| A4 | 戒 tell L73 | `§7acc_ember_raid_ring · 周首通保底。与刃同持即 §6余烬同袍 ✓` | `§7余烬团本之戒 · 周首通保底。与刃同持即 §6余烬同袍 ✓` |

**口径备忘：** 只动上表 4 句玩家可见字符串；`name:` / 其它 lore / Open tell / 二件套·副手图标 / `actions` 里 sound·menu **一字不动**；套装激活规则与数值 **零改**；管理侧文档与 NI 键仍用 id。

#### B · 拆解重铸石灰字（旁附 soft · 勿双上）

| # | 位置 | 旧 | 新（若另批） |
|---|------|----|--------------|
| B1 | `ember_disassemble` 重铸 lore | `§8mat_ember_reforge_stone` | **删行**（上行已有「§8消耗 §6余烬重铸石」）或改 `§8余烬重铸石` |

**本轮不与 A 同上施工**；可作下一项 soft。

---

## 3. 方案 A / B

| 方案 | 做法摘要 | 利 | 弊 | 本轮 |
|------|----------|----|----|------|
| **A. 套装 4 句人话（推荐）** | 上表 A1～A4；零改数值/掉落/体力 | B2.18 旁附主债；单文件极薄；rg 可勾销；TrMenu 可点验 | 未顺手拆解 1 行 | **推荐** |
| **B. 拆解 `mat_ember_reforge_stone` 一行** | 上表 B1 | 更薄（1 行） | 非 B2.18 点名主债；与 A 捆则双表面 | **旁附 soft · 勿双上** |

**不施工：** 开精英奖励预览子菜单壳；改 NI 物品 lore 裸 id；改套装数值/掉落/体力；宣称 B0.1 已清；未批改 YAML；重开 B2.6–B2.18 / B-flex / B-anvil；墙钟/DPS；叫挑刺。

---

## 4. 验收（≤5）

1. **本设计 commit：** 本稿落盘；玩法 YAML / loot / NI / DP / MM / `ember_set.yml` **零 diff**（本 commit 仅文档 + backlog）。  
2. **若批 A：** `ember_set.yml` 无字面 `gear_ember_blade` / `gear_ember_t*_blade` / `acc_ember_raid_ring`（玩家 lore/tell）；A1～A4 为人话；其余 lore/name/Open tell **语义不变**。  
3. **若批 A：** 套装相关 `set.yml` / loot / 体力 cost / NI 物品文件 **相对批前零 diff**。  
4. **若批 A · 禁项：** 未改 `ember_disassemble.yml`（除非另批 B）；未开精英预览壳；**不**宣称 B0.1 / NI 物品 lore 裸 id 已清。  
5. **目视轻测：** `/ember` → 套装 → 悬停刃/戒、点击戒，无裸 NI id；说明仍短清楚。**禁** wall-clock/DPS；**不叫挑刺**。

静态 rg（施工后）：

```bash
rg -n 'gear_ember_|acc_ember_raid_ring' plugins/TrMenu/menus/ember_set.yml
# 期望：0（玩家可见行）
```

---

## 5. 站岗表（功能锚）

| 功能锚 / 用途 | NPC id | 显示名 | 位置 | 右键菜单 | 副交互 | 备注 |
|---|---|---|---|---|---|---|
| 套装说明 | —（TrMenu） | 余烬 · 套装 | hub → 套装 | `ember_set` | 返回 hub | **UI/文案可用** · 无新 NPC |

**站岗自检：** 无新 NPC；仅文案 → 最高标「UI 可用」，不宣称套装数值/掉落改动。

---

## 6. 动线

| 步骤 | 区域 | 玩家看到 | 发生什么 | 失败/返回 |
|------|------|----------|----------|-----------|
| 1 | `/ember` → 套装 | 「余烬 · 套装」 | 打开 `ember_set` | 返回 hub |
| 2 | 悬停刃 / 戒 | 中文档位/品名；**无**裸 NI id | 只读展示 | — |
| 3 | 点击戒 | tell 含「余烬团本之戒」人话 | 说明 | — |

---

## 7. UX 否决条

| 硬条 | 预期 |
|------|------|
| 玩家须手打指令才能看套装说明 | **FAIL** |
| 施工后刃/戒 lore 或戒 tell 仍出现字面 `gear_ember_*` / `acc_ember_raid_ring` | **FAIL** |
| 借机改套装数值 / 掉落 / 体力 / 开精英厚壳 | **FAIL** |
| 验收靠 wall-clock 或 DPS / 称作挑刺 | **FAIL** |
| 宣称 B0.1 已清 | **FAIL** |
| 未批准即改 `ember_set.yml` | **FAIL** |

---

## 8. 专岗

| 岗 | 活 | 时机 |
|----|----|------|
| **策划** | 本 tip；总控批 A（或不附 B） | **本窗**（文档 only） |
| **插件** | `ember_set.yml` A1～A4 四句替换（批 A 后） | **批 A 后** |
| **测试** | 静态 `rg` + 套装菜单悬停/点击轻测；**不叫挑刺**；禁 wall-clock/DPS | 施工后 |

**未批准前不施工**（含不改 `ember_set.yml`）。

---

## 9. 明确未动 / 不做

- 精英奖励预览子菜单壳（证据不足勿硬开）  
- `ember_disassemble.yml`（B soft，勿与 A 双上）  
- NI 物品文件 lore 内裸 id（另软挂）  
- 套装 2 件数值 / loot / DP / MM / 体力 / 掉率  
- B0.1 票扣显示名；四件甲；锻炉重做；誓约大改  
- B-flex-1/2 / B-anvil-1 已结物回改  
- 墙钟 / DPS / 挑刺口径  

---

## 10. Backlog 挂账

- 新号：**B2.19**（B2 文案可维护轨 · 与 B-flex / B-anvil **不捆**）  
- 软观察「套装 `gear_ember_*`」本窗主清；「拆解 `mat_ember_reforge_stone`」留 B soft；「精英预览壳」仍 soft、勿硬开  
- B2.18 PASS 勾销保持；**勿宣称 B0.1 已清**

---

## 11. 回总控摘要

- **荐 A：** `ember_set.yml` 4 句玩家可见裸 NI → 中文（刃 lore×2 + 戒 lore + 戒 tell）；零改套装数值/掉落/体力。  
- **路径：** `docs/design/design-ember-set-gear-id-copy.md` · **B2.19** · STATUS **已批 A · 待插件** · 批准见 docs/status/STATUS-ember-set-gear-id-copy-approve.md。  
- **证据：** L48/49/64/73；升自 B2.18 旁附 B1～B4；NI 中文名已对照。  
- **B 旁附 soft：** 拆解页 1 行 `mat_ember_reforge_stone`；**勿双上**。  
- **未动：** 精英厚壳 / NI 物品 lore / 套装数值·掉落·体力 / B0.1 / 四件甲·锻炉·誓约。  
- **验收专岗：** 插件改 4 句 → 测试 `rg`+套装轻点；禁 wall-clock/DPS；不叫挑刺。  
- **勿宣称 B0.1 已清。** 与 B-flex / B-anvil 不捆。
