# 设计稿 · B0.4 挂机日顶二档（文档标定）

> 债源：`docs/design-ember-content-backlog.md` **B0.4** / **B2.5**；阶段 3 挂账见 `HANDOFF.md` §阶段3「未完成 / 收尾」。  
> 对齐：`docs/TEMPLATE-ember-design-handoff.md` · `docs/design-ember-afk-p0-surface.md` · `STATUS-afk-caps.md`。  
> **已批准方案 A（总控）：文档债闭环。本额度不改现网 YAML / 插件数值。经济窗另批 B1。**

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 / 功能名 | B0.4 挂机日顶二档 · 经济债标定 |
| 负责人 / 日期 | 余烬-策划 / 2026-09-28（Asia/Shanghai） |
| 关联世界 | `ember_afk`（`afk_caps.worlds` 另含 `world`） |
| 目标验收里程碑 | 本额度：**文档债闭环**（不宣称「挂机已封死通胀」）；若总控另开经济窗再走方案 B |
| 当前状态 | **已批准 · 方案 A**（总控 2026-09-28 07:57 CST；文档债闭环，本额度不施工数值） |
| 关联实现 / STATUS | `plugins/CoreRpg/config.yml` → `afk_caps`；`CoreRpgPlugin.afkCapped`；`STATUS-afk-caps.md`；`STATUS-ember-afk.md`；`HANDOFF.md` |

**约束（本稿硬锁）：**

- 禁 Citizens；玩家零指令（TrMenu / 场景锚）。
- **不新开系统**；不改体力数值；不改日常 / Boss / 票 / 掉落表（除非总控另批且本稿推荐不做）。
- 不改 `afk_tiers` 等级门槛、入口坐标、MM 怪 HP/伤（P0 地图与阶段 3 战斗已结）。
- UX：NI 自定义物；入口仍是枢纽菜单 → `ember_afk` 选层；不教打 `/corerpg afk`。

---

## 1. 问题一句话（通胀风险）

**现网挂机日顶后固定 `over_chance=0.25`、无第二档 → 超长挂机仍按约 1/4 满速线性产材料，日顶只推迟通胀、封不死。**

---

## 2. Backlog 原文锚点

### B0.4（`docs/design-ember-content-backlog.md`）

| | |
|--|--|
| **问题** | `afk_caps` 超顶后固定 25% 递减，无第二档 → 超长挂机仍近似线性（约 1/4 产速）。 |
| **建议** | 额度紧时可先 **文档标定「已知经济债」**；若做：第二档更低概率或小时软顶（需总控批经济）。 |
| **验收硬条** | 若本轮不做：STATUS 写明债务+不宣称「挂机已封死通胀」。若做：给曲线表 + 测 2h 产出对比 PASS。 |
| **专岗** | **总控**拍是否做 · 策划出曲线 · 插件 · 测试 |

### B2.5

| ID | 项 | 验收硬条（摘要） | 专岗 |
|----|----|------------------|------|
| B2.5 | `afk_caps` 第二档（若总控开经济） | 见 B0.4 | 策划+插件 |

总控快照（2026-09-28）：**进行中 / 仍挂 = B0.4 文档标定（经济债）**；排期写「经济可选：B0.4 / B2.5 总控拍板后再动」。

---

## 3. 现网扫描（YAML / 代码 · 不编造）

### 3.1 日顶配置（有日顶 · 无第二档）

**线上生效：** `plugins/CoreRpg/config.yml`（与源码副本 `CoreRpg/src/main/resources/config.yml` 数值一致，见 `STATUS-afk-caps.md`）

```yaml
afk_caps:
  enabled: true
  worlds: [ember_afk, world]
  over_chance: 0.25          # ← 唯一超顶概率；无 over_chance_2 / softcap_hours 等字段
  kill_coin: 150
  items:
    mat_ember_shard: 150
    mat_ember_bone_dust: 80
    mat_ember_core_fragment: 10
    mat_ember_stable_charm: 1
    mat_ember_protect_scroll: 2
    mat_ember_soul_dust: 2
```

| 结论项 | 现网事实 |
|--------|----------|
| 有无**日顶** | **有** — 按「玩家 + 物品」日计数（键 `afk_<niId>`），四层共用 |
| 有无**第二档 / 小时软顶** | **无** — 配置与 `afkCapped()` 均只有单一 `over_chance` |
| 超顶行为 | 达 cap 后每份仍以 **25%** 概率入账，并继续累加计数（非硬停） |
| 副本豁免 | `afk_caps.worlds` 仅 `ember_afk` / `world`；日常/周本/深渊/团本实例图不进此名单（见阶段 4 STATUS） |

**实现引用：** `CoreRpg/src/main/java/.../CoreRpgPlugin.java` → `afkCapped(Player, item, n)`（约 L1241–1260）：

- `c < cap \|\| Math.random() < over` → 发放并 `addPeriodCount`
- 首次达顶提示：`今日野外掉落已达收益上限，之后掉率降为 25%…（每日 0 点重置）`
- **无**「超顶后再降一档」或「按挂机小时衰减」分支

`AfkTierService` 仅展示今日计数（碎片/骨尘/核心/魂尘/击杀币），不另设软顶。

### 3.2 分层入口与菜单（本债不改）

| 路径 | 用途 |
|------|------|
| `plugins/CoreRpg/config.yml` → `afk_tiers` | ①灰坡 Lv10 · ②荒原 Lv20 · ③焦土 Lv30 · ④烬原深处 Lv40；世界 `ember_afk` |
| `plugins/TrMenu/menus/ember_afk.yml` | 选层；底层 `command: corerpg afk n`（玩家路径点菜单，不教手打） |
| `plugins/TrMenu/menus/ember_hub.yml` | 枢纽「挂机庭」→ `menu: ember_afk` |

### 3.3 掉落物 ID / 概率（MM → `corerpg mmgive` → `afk_caps`）

**文件：** `plugins/MythicMobs/Mobs/EmberAfk.yml`

| 层 / Mob | 主掉（~onDeath） | 副掉概率字段 |
|----------|------------------|--------------|
| ① `EmberAfkZombie` | `mat_ember_shard` ×1 | `mat_ember_soul_dust` @ **0.02** |
| ① `EmberAfkSkeleton` | `mat_ember_bone_dust` ×1 | soul_dust @ **0.02** |
| ② `EmberAfk2Husk` | shard ×1 | core_fragment @ **0.01** · soul_dust @ **0.02** |
| ② `EmberAfk2Skeleton` | bone_dust ×1 | core @ **0.01** · soul @ **0.02** |
| ③ `EmberAfk3Zombie` | shard ×1 | bone @ **0.3** · core @ **0.02** · soul @ **0.03** |
| ③ `EmberAfk3Stray` | bone ×1 | shard @ **0.3** · core @ **0.02** · soul @ **0.03** |
| ④ `EmberAfk4Husk` / `EmberAfk4WitherSkeleton` | shard ×1 | bone @ **0.5** · core @ **0.04** · soul @ **0.03** |

说明：`afk_caps.items` 里的 `mat_ember_stable_charm` / `mat_ember_protect_scroll` 为野外通用日顶槽；**当前 EmberAfk 怪表未挂这两项**（预留/其它野外源）。灾厄余烬未进挂机层（HANDOFF 明示）。

### 3.4 刷怪 CD（产速上界约束 · 非精确 /小时）

**目录：** `plugins/MythicMobs/Spawners/EmberAfk*.yml`

| 层 | Spawner | MaxMobs | Cooldown(s) |
|----|---------|---------|-------------|
| ① | Z1 / Z2 / S1 | 6 / 6 / 4 | 8 / 10 / 12 |
| ② | A/B/C | 2 / 1 / 1 | 10 / 10 / 12 |
| ③ | A/B/C | 2 / 1 / 1 | 11 / 11 / 13 |
| ④ | A/B/C | 2 / 1 / 1 | 12 / 12 / 14 |

每层同时存活上界约 **4** 只（②③④）；产速主要被 **TTK + CD + MaxMobs** 共同卡住，而非无限刷。

### 3.5 挂机产物 / 小时 · 粗估（基于现网字段 + 实测击杀速）

**粗估方法：**

1. 取测试岗实测 **kills/min**（达顶前、合理装）：见 `STATUS-afk-tier-t3-t4.md` / `HANDOFF.md`。
2. ×60 → kills/h；再 × 该层主掉期望（shard 近似 1.0/杀 在④）。
3. 达 `afk_caps` 后期望 × `over_chance(0.25)`。
4. **不确定点：** 真人走位/多刷点抢怪/死亡回入口/刷怪未满 MaxMobs → 区间给宽；本表**不是**验收硬数字。

| 层 | 实测击杀速（证据） | 达顶前粗估 shard/h | 达顶后（×0.25）粗估 shard/h | 日顶 150 约需 |
|----|-------------------|--------------------|-----------------------------|---------------|
| ② 荒原 | ~14 /min（HANDOFF 调参后） | ~800（上界乐观） | ~200 | ~10～12 min |
| ③ 焦土 | 11.7 /min（首跑 35/180s）；复测 36/≈3min 同量级 | ~700 | ~175 | ~13～15 min |
| ④ 烬原 | 13.3 /min（40/180s） | ~780 | ~195 | ~12～15 min |

**解读（通胀债）：**

- 日顶把「满速」压在约 **一刻钟内吃完**（碎片 150）；之后仍以约 **1/4 速** 无限续产。
- 若挂机 **2h**：满速段 ≈150 + 超顶段 ≈（2h − 0.25h）×195 ≈ **340+** 碎片量级（④乐观上界）→ **日有效产出可数倍于 cap 名义值**。
- 骨尘 cap 80、核心 10、魂尘 2 同理：达顶后仍 25% 续掉；核心在④期望 0.04/杀，满速约 30+/h，**约 20 min 触顶**后仍以 ~8/h 续。
- 击杀币 `kill_coin: 150`、击杀经验 `progress.yml` → `ember_xp.kill_daily_cap: 100` 为旁路日顶；**经验是硬截断日给，材料是软顶续掉** — 债在材料侧。

**不确定点清单（写进 STATUS 时保留）：**

- 未做「连续 2h 同账号」官方产量表；上表由 3 min 战斗外推。
- ①灰坡 MaxMobs 更高（6+6+4），新手区 /小时可能更高或更抖（箭压），本债标定以②～④材料层为主。
- `world` 在名单内：若主世界另有 `mmgive` 同 NI，会与挂机**共用**日计数（需插件岗确认现网是否还有野外 mmgive；本扫以 EmberAfk 为主）。

### 3.6 已验收事实（不得宣称「已封死」）

`STATUS-afk-caps.md`（2026-09-27）：跨层合并 / 上限文案 / 超限≈25% 均 **PASS**。  
→ 证明的是 **「有一档软顶且跨层共用」**，不是 **「长挂机无通胀」**。

---

## 4. 方案对比与推荐

### 方案 A · 文档标定已知债（本额度推荐）

| 项 | 内容 |
|----|------|
| 做什么 | 本文件落盘；总控/STATUS 标明「B0.4 已知经济债」；对外/对内 **禁止**写「挂机已封死通胀」 |
| 改 YAML？ | **否** |
| 专岗 | 策划（本稿）· 总控勾选 · 测试无需新测窗 |
| 验收 | backlog B0.4「若本轮不做」硬条：债务写明 + 不宣称封死 |

### 方案 B · 第二档更低概率 / 小时软顶（需总控另批经济）

| 子型 | 思路（批准后可微改 · 须总控再批） | 触碰面 |
|------|-----------------------------------|--------|
| B1 双概率 | 达日顶后 `over_chance`；再设 `over_chance_2`（建议量级 **0.05～0.10**）+ `over_after_extra`（例如超顶后再计入 N 份或再挂机 M 分钟后切二档） | 仅 `afk_caps` + `afkCapped()` |
| B2 小时软顶 | 达日顶后按「今日挂机有效分钟」分段衰减，或超顶后每小时额外乘子 | 需记挂机时长（新字段或复用 periodCount） |
| B3 硬停 | 达顶后掉率 0 | **过狠**，易伤挂机体验与材料口；本稿 **不推荐** |

**推荐：方案 A。**

**理由：**

1. 总控本派工标题即「文档标定」；backlog 明文「额度紧时可先文档标定」。
2. 一档 25% 已验收；二档属**经济曲线**，要测 2h 产出对比 + 可能回调锻造消耗预期，本额度并行窗已挤满日常 UX / 地图债外工作。
3. 材料通胀有缓冲：锻造/镶嵌/修理持续消耗；债是「长挂机相对日活过肥」，不是「立刻炸服」。标定后可在下一经济窗用 B1 最小改动还债。
4. 若误在本额度半施工（只改 `over_chance` 全局）会误伤「刚达顶的一小时体验」，必须带曲线表与对照测 — 超出「标定」范围。

### 若总控日后选 B：建议默认 B1（双概率）

**批准后可微改（须总控再批一遍数值）：** 示意曲线，**非现网生效值**：

| 阶段 | 条件（示意） | 掉率乘子 | 备注 |
|------|--------------|----------|------|
| 满速 | `count < cap` | 1.0 | 现网不变 |
| 一档 | `count ≥ cap` 且未进二档 | 0.25（现 `over_chance`） | 保持 |
| 二档 | 超顶后再获得 ≥ **cap** 份（即累计约 2×cap 计数）**或** 达顶后累计挂机 **≥ 60 min** | **0.05～0.10**（`over_chance_2`） | 二选一触发，插件实现时定一种，避免双计 |

文案：达一档仍用现句；进二档追加一句短提示（例：`收益再降，去打日常吧`）— 仍零指令。

---

## 5. 若做方案 B：验收硬条 / 专岗 / 不动哪些

### 5.1 验收硬条

1. 配置出现明确第二档字段（名由插件定，稿意：`over_chance_2` + 触发条件键）；**不得**只靠口头约定。
2. 测试：同装同层，**达顶后连续战斗 ≥ 2h**（或等价击杀数），产出对比表写入 STATUS：  
   - 一档段期望 ≈ 0.25 × 满速；  
   - 二档段期望落在设定带（如 0.05～0.10）±样本波动；  
   - 跨层切层计数仍合并（回归 `STATUS-afk-caps` 三项）。
3. 玩家可见：达顶/二档提示仍人话；**不**教指令；菜单 lore 可不改。
4. **不**宣称「绝对零通胀」；对外表述改为「双软顶，长挂机收益显著低于满速」。
5. 回滚路径：改回仅 `over_chance` 即可恢复一档行为（字段兼容）。

### 5.2 专岗

| 岗 | 职责 |
|----|------|
| **总控** | 拍「开经济 / 仍挂债」；批 B1 数值带 |
| **策划** | 曲线表（本 §4）定稿；文案一句 |
| **插件** | `afkCapped` + `config.yml afk_caps`；不动 MM 掉落概率除非总控另令 |
| **测试** | 2h 产出对照 + 跨层合并回归 |
| **物品 / 怪物 / 地图** | **本债不派** |

### 5.3 明确不动（除非另开稿）

| 类别 | 文件/系统 | 本债 |
|------|-----------|------|
| 体力 | `cash.yml` / `StaminaService` 日回、扣次、药顶 | **不动** |
| 日常 / Boss | DP map、日常 MM、票、通关箱 | **不动** |
| 挂机怪强度 / 掉落% | `EmberAfk.yml` Skills 概率 | **不动**（二档只乘在 `mmgive` 日顶逻辑上） |
| 刷怪 CD / MaxMobs | `Spawners/EmberAfk*.yml` | **不动** |
| 层门槛 / 传送 | `afk_tiers` | **不动** |
| 战令 / 击杀经验日顶 | `progress.yml` | **不动**（已是硬顶；与材料软顶分轨） |

---

## 6. 站岗 / 动线（本债无新锚）

本债**不新增** NPC / 菜单 / 地图锚。挂机入口维持现状：

| 功能锚 | 现状 | 本债 |
|--------|------|------|
| 枢纽「挂机庭」 | TrMenu `ember_hub` → `ember_afk` | 不改 |
| 分层选层 | `ember_afk.yml` → `corerpg afk n` | 不改 |
| 场景 | `ember_afk` ①～④（P0 贴地已结） | 不改 |

**站岗表自检：** 无新功能锚 → 本项标「文档债 / 无场景施工」，**不得**报「场景完成」。

动线（玩家无感本债）：枢纽菜单 → 挂机庭选层 → 击杀至日顶提示 →（现网）仍可 25% 续挂 → 回枢纽打日常。方案 B 仅多一句二档提示。

---

## 7. UX 否决条

| 硬条 | PASS / FAIL | 证据或债务说明 |
|---|---|---|
| **零手打指令** | **PASS**（本债不改路径） | 入口仍菜单；管理 `/corerpg` 不进玩家话术 |
| **自定义物均为 NI ID** | **PASS** | 掉落经 `mmgive` + NI id（`mat_ember_*`） |
| **功能锚可点** | **N/A → 文档债** | 无新锚；不宣称场景完成 |
| **实景地图** | **N/A** | 地图不在本债范围（P0 另结） |
| **换皮不算视觉 PASS** | **N/A** | 同上 |
| **占位有名字有债务有里程碑** | **PASS** | 债务名 **B0.4 / B2.5**；还债里程碑 = **总控开经济窗后的方案 B1**；在此之前玩法可测、**观感/经济封死不可宣称完成** |

---

## 8. 本额度结论（给总控）

| 项 | 结论 |
|----|------|
| **推荐方案** | **A（文档标定已知债）** |
| **是否建议本额度施工** | **建议仅文档债**（不改 YAML；方案 B 待总控开经济后再派） |
| **现网日顶有无** | **有**（`afk_caps.items.*` + `kill_coin`）；**无第二档 / 无小时软顶** |
| **一句话问题** | 超顶后固定 25%，长挂机仍约 1/4 速线性产，日顶封不死通胀 |
| **推荐摘要** | 本额度只落本文档；对外禁止「挂机已封死通胀」；若开经济优先 B1 双概率（0.25→0.05～0.10），不动体力/日常/Boss/怪表 |

### 关键 YAML / 代码路径列表（调研引用）

1. `plugins/CoreRpg/config.yml` — `afk_caps` / `afk_tiers`
2. `CoreRpg/src/main/resources/config.yml` — 同源注释与默认值
3. `CoreRpg/src/main/java/town/sunshine/corerpg/CoreRpgPlugin.java` — `afkCapped`
4. `CoreRpg/src/main/java/town/sunshine/corerpg/AfkTierService.java` — 日计数展示
5. `plugins/MythicMobs/Mobs/EmberAfk.yml` — 掉落 NI / 概率
6. `plugins/MythicMobs/Spawners/EmberAfk*.yml` — CD / MaxMobs
7. `plugins/TrMenu/menus/ember_afk.yml` · `ember_hub.yml` — 入口
8. `plugins/CoreRpg/progress.yml` — `ember_xp.kill_daily_cap`（旁路，本债不动）
9. 证据 STATUS：`STATUS-afk-caps.md` · `STATUS-ember-afk.md` · `STATUS-afk-tier-t3-t4.md` · `HANDOFF.md`
10. 债源：`docs/design-ember-content-backlog.md` B0.4 / B2.5

---

## 9. 交付自检

- [x] 问题一句话（通胀）
- [x] 现网日顶/软顶扫清 + 路径字段 + /小时粗估方法与不确定点
- [x] 方案 A vs B + **推荐 A** + 理由
- [x] 若做 B：验收硬条 / 专岗 / 不动体力·日常·Boss
- [x] 明确本额度：**建议仅文档债**
- [x] 未改任何现网 YAML

**最终结论：** 本额度 **文档债闭环可报**；数值施工 **不可验收**（未开经济窗）。状态保持 **未完成（仅文档标定）**，待总控勾选是否升级方案 B。

---

## 总控批注（2026-09-28）

**批准方案 A。** 本额度仅文档标定已知经济债；禁止对外宣称「挂机已封死通胀」。日后开经济窗优先 **B1 双概率**（0.25 → 0.05～0.10），须再批数值带与 2h 对照测。**本额度不施工 YAML/Java。**
