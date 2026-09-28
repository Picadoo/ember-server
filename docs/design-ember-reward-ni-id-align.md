# 设计稿 · 奖励预览页 `§8NI id:` 人话对齐（B2.18）

> **STATUS：已批 A · 待插件施工。** 本稿只定 **玩家可见** 五入口奖励预览 TrMenu 里灰字裸 NI id 的薄清；**禁**改 loot / DP / MM / 体力 cost / 掉率 / 套装数值 / NI 物品文件。  
> 债源：backlog 软观察「奖励页 NI id / 套装 `gear_ember_*` 未宣称已清」；B2.15～B2.17 旁扫反复点名；总控本派单例举为合法薄方向。  
> 对齐：`design-ember-daily-reward-preview.md` 等五入口真分页已结 · UX「文案短清楚 · TrMenu 点击 · NI ID（逻辑侧保留）」。  
> 排除本轮：刚结 B2.6–B2.17 / B-flex-1/2 / B-anvil-1；精英预览厚壳（hub 一点进本、无 P/无独立菜单 → 证据不足勿硬开）；五入口空壳 tell→真页（已结）；非日常体力灰显；挂机 over_chance*；B1.3；墙钟/DPS；霜锈前压；断塔无证据升 B；B0.1 除非新证据；四件甲/锻炉重做/誓约大改；位移+保命双上；灰箍+骨饰双上；挑刺里程碑。**勿宣称 B0.1 已清。**

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | 奖励预览 TrMenu · **玩家可见 `§8NI id:` / `§8NI:` 灰字对齐**（UX · B2.18） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-29 01:34 Asia/Shanghai |
| 关联 | live `plugins/TrMenu/menus/ember_{daily,weekly,raid,abyss,calamity}_rewards.yml` · 软观察 backlog |
| 状态 | **已批 A · 待插件** |
| 关联 STATUS / 稿 | backlog 挂 **B2.18** · 批后 STATUS-approve / 测报 |

### 硬约束（本稿）

| 可动（**仅若批 A 且另开施工**） | 不可动（硬禁 · 含本稿 commit） |
|--------------------------------|--------------------------------|
| 五份 `*_rewards.yml` 玩家 lore 中字面 `§8NI id:` / `§8NI:` 行（删或改为人话；见 §3） | loot.yml / DP option / MM 掉落 / 体力 cost / 掉率数值 |
| 热更 / TrMenu reload（服约定） | NI 物品 YAML（含物品 lore 内 `&7gear_ember_*` 行——另软挂）；`ember_set.yml`（方案 B，勿与 A 双上） |
| | git push；开精英预览子菜单壳；宣称 B0.1 已清；改玩法数值 |

---

## 1. 问题一句话

**五入口奖励预览已有中文主名与中文材料行，却仍在 lore 底部印 `§8NI id: gear_ember_blade` 等裸 id——玩家悬停即见管理用噪音，属可菜单轻测、不动数值的薄文案债。**

---

## 2. 现网事实（调研 · 本稿未改）

| 项 | 现网 / 证据 |
|----|-------------|
| 入口 | `/ember` → hub → 日/周/团/深渊/灾厄 → **奖励预览** → `ember_*_rewards`（TrMenu 点击，少打指令已满足） |
| **脏点计数** | 五文件 **34** 行含 `§8NI id:` 或 `§8NI:`（daily 8 / weekly 6 / raid 6 / abyss 4 / calamity 10）；涉及 **20** 个唯一 NI id |
| 主名已中文 | 例：日常 `name: '§c余烬之刃'` 下仍挂 `§8NI id: gear_ember_blade`；灾厄日箱 lore 已写「核心碎片/附魔晶/灾厄余烬」，底部再叠两行裸 id |
| 深渊汇总格 | 档位格 lore **已列**「余烬碎片 / 骨尘 / 核心 / 锋利石」中文，仍附一整行 `§8NI id: mat_ember_shard / …` |
| NI 显示名对照 | 20 id 均有现网中文名（如 `gear_ember_blade`→余烬之刃、`mat_ember_shard`→余烬碎片、`acc_ember_raid_ring`→余烬团本之戒）；**本窗不改 NI 文件** |
| 候选对比 · 套装 | `ember_set.yml`：**4** 处玩家可见 `gear_ember_*` / `acc_ember_raid_ring`（刃 lore×2 + 戒 lore + tell）——痛次、可另薄；**方案 B 旁附，勿与 A 同施工双上** |
| 候选对比 · 精英预览 | hub 精英一点 `corerpg elite start`、**无 P / 无独立 rewards 菜单** → 要做须新开子菜单壳，**非薄可勾销**；本轮 **不挂厚壳** |
| 候选对比 · 物品 lore 裸 id | NI 文件内 `&7gear_ember_blade` 等——属物品岗另软挂，**不进本窗 A**（免捆 NI 改写） |

### 替换口径（展示层 · 不改掉落逻辑）

#### A · 五入口奖励页 34 行裸 NI 灰字（推荐）

**做法：** 删除玩家 lore 中所有 `§8NI id: …` / `§8NI: …` 整行。  
理由：同格 `name:` 与上方中文 lore **已覆盖**品名；裸 id 纯管理噪音。不改图标 material / name / 概率句 / actions / 返回钮。

| 文件 | 行数 | 验收 rg（施工后） |
|------|------|-------------------|
| `ember_daily_rewards.yml` | 8 | `rg '§8NI' …` → 0 |
| `ember_weekly_rewards.yml` | 6 | 同上 |
| `ember_raid_rewards.yml` | 6 | 同上 |
| `ember_abyss_rewards.yml` | 4 | 同上 |
| `ember_calamity_rewards.yml` | 10 | 同上 |
| **合计** | **34** | 五文件合计 `rg '§8NI' plugins/TrMenu/menus/ember_*_rewards.yml` → **0** |

**口径备忘：** 只动玩家可见字符串；`command:` / `menu:` / loot 表 / DP / MM **一字不动**；概率「约 xx%」句 **保留**；管理用文档与插件逻辑仍用 NI id。

#### B · 套装菜单 `gear_ember_*` 四句（旁附 · 勿双上）

| # | 位置 | 旧（摘要） | 新（若另批） |
|---|------|------------|--------------|
| B1 | `ember_set` 刃 lore | `§8T0 gear_ember_blade` | `§8T0 余烬之刃` |
| B2 | 同上 | `§8T1/T2/T3 gear_ember_t*_blade` | `§8T1/T2/T3 精炼/深核/灾厄之刃` |
| B3 | 戒 lore | `§7套装件 B · NI acc_ember_raid_ring` | `§7套装件 B · 余烬团本之戒` |
| B4 | 戒 tell | `§7acc_ember_raid_ring · …` | `§7余烬团本之戒 · …` |

**本轮不与 A 同上施工**；总控若要可另开极薄窗。

---

## 3. 方案 A / B

| 方案 | 做法摘要 | 利 | 弊 | 本轮 |
|------|----------|----|----|------|
| **A. 只清五入口奖励页 34×`§8NI` 行（推荐）** | 删灰字裸 id 行；中文主名/材料行保留；零改掉落 | 软观察主债；菜单可点验；跨 5 文件但同构极薄；rg=0 可勾销 | 未顺手套装 4 句 | **推荐** |
| **B. 套装 `ember_set` 四句人话** | 上表 B1～B4 | 顺清次痛 | 与 A 捆则双表面；非奖励页主债 | **旁附 · 勿双上** |

**不施工：** 开精英奖励预览子菜单壳；改 NI 物品 lore 裸 id；改 loot/掉率/体力；宣称 B0.1 已清；未批改 YAML；重开 B2.6–B2.17 / B-flex / B-anvil；墙钟/DPS；叫挑刺。

---

## 4. 验收（≤5）

1. **本设计 commit：** 本稿落盘；玩法 YAML / loot / NI / DP / MM **零 diff**（本 commit 仅文档 + backlog）。  
2. **若批 A：** `rg '§8NI' plugins/TrMenu/menus/ember_*_rewards.yml` → **0**；五文件图标 `name:` / 概率句 / `menu:` 返回 **相对批前语义不变**。  
3. **若批 A：** `loot.yml` / 各本 DP option / MM Boss 掉落 / 体力 cost **相对批前零 diff**。  
4. **若批 A · 禁项：** 未改 `ember_set.yml`（除非另批 B）；未开精英预览壳；**不**宣称 B0.1 / 套装 NI / 物品 lore 裸 id 已清。  
5. **目视轻测：** `/ember` → 日/周/团/深渊/灾厄 → 奖励预览，悬停刃/材料/日箱等格，**无**字面 `NI id:` / `gear_ember_` / `mat_ember_` 灰字行；主名仍中文。

---

## 5. 站岗表（功能锚）

| 功能锚 / 用途 | NPC id | 显示名 | 位置 | 右键菜单 | 副交互 | 备注 |
|---|---|---|---|---|---|---|
| 奖励预览（五入口） | —（TrMenu） | 奖励预览 | hub → 各玩法页 → P | `ember_*_rewards` | 返回各玩法页 | **UI/文案可用** · 无新 NPC |

**站岗自检：** 无新 NPC；仅文案 → 最高标「UI 可用」，不宣称掉落/套装数值改动。

---

## 6. 动线

| 步骤 | 区域 | 玩家看到 | 发生什么 | 失败/返回 |
|------|------|----------|----------|-----------|
| 1 | `/ember` → 玩法页 | 「奖励预览」 | 打开对应 `*_rewards` | 返回玩法页 |
| 2 | 悬停物品格 | 中文名 + 概率/数量；**无**裸 NI id | 只读展示 | — |
| 3 | 返回 | 原玩法页 | `menu:` 既有 | — |

---

## 7. UX 否决条

| 硬条 | 预期 |
|------|------|
| 玩家须手打指令才能看奖励预览 | **FAIL** |
| 施工后奖励格仍出现字面 `§8NI id:` / `§8NI:` | **FAIL** |
| 借机改掉率 / loot / 体力 / 开精英厚壳 | **FAIL** |
| 验收靠 wall-clock 或 DPS / 称作挑刺 | **FAIL** |
| 宣称 B0.1 已清 | **FAIL** |
| 未批准即改 `*_rewards.yml` | **FAIL** |

---

## 8. 专岗

| 岗 | 活 | 时机 |
|----|----|------|
| **策划** | 本 tip；总控批 A（或不附 B） | **本窗**（文档 only） |
| **插件** | 五份 `*_rewards.yml` 删 34 行灰字（批 A 后） | **批 A 后** |
| **测试** | 静态 `rg` + 五入口菜单轻点悬停；**不叫挑刺**；禁 wall-clock/DPS | 施工后 |

**未批准前不施工**（含不改玩法 YAML / 不改 TrMenu rewards）。

---

## 9. 明确未动 / 不做

- 精英奖励预览子菜单壳（证据不足勿硬开）  
- `ember_set.yml`（B 旁附，勿与 A 双上）  
- NI 物品文件 lore 内裸 id（另软挂）  
- loot / DP / MM / 体力 / 掉率 / 套装 2 件数值  
- B0.1 票扣显示名；四件甲；锻炉重做；誓约大改  
- B-flex-1/2 / B-anvil-1 已结物回改  
- 墙钟 / DPS / 挑刺口径  

---

## 10. Backlog 挂账

- 新号：**B2.18**（B2 文案可维护轨 · 与灵活窗 B-flex / B-anvil **不捆**）  
- 软观察「奖励页 NI id」本窗主清；「套装 `gear_ember_*`」留 B 或批后另窗；「精英预览壳」仍 soft、勿硬开  
- 灵活三窗（B-flex-1/2 + B-anvil-1）已 PASS 勾销保持  

---

## 11. 回总控摘要

- **荐 A：** 五入口奖励预览删除 34 行玩家可见 `§8NI id:`/`§8NI:` 灰字（中文主名已在；零改掉落）。  
- **路径：** `docs/design-ember-reward-ni-id-align.md` · **B2.18** · STATUS **已批 A · 待插件** · 批准 `cef1017`。  
- **证据：** 5 文件 34 行 / 20 id；例日常刃格 `name: 余烬之刃` + `§8NI id: gear_ember_blade`。  
- **B 旁附：** 套装菜单 4 句 `gear_ember_*`/`acc_ember_raid_ring` → 中文；**勿双上**。  
- **未动：** 精英厚壳 / NI 物品 lore / loot·体力·掉率 / B0.1 / 四件甲·锻炉·誓约。  
- **验收专岗：** 插件改 YAML → 测试 `rg`+五入口轻点；禁 wall-clock/DPS；不叫挑刺。  
- **勿宣称 B0.1 已清。**

