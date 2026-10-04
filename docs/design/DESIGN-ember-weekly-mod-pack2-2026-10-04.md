# Ember Weekly Modifier Pack 2：精选图周规则扩包（D175，设计-only，2026-10-04）

> 状态：**已上线 CoreRpg 1.65.8 / D178**（2026-10-04）。实现：双写 `ember-v1-runs.yml` +3（bolters/shell/press），`balance_version` 31→32；单测 9 规则 / 63 对；冒烟 FreshQ139–141。Stage C `p2econ --mods` **DEFERRED**（p1sim 仍被 afk-p1 / mainline 占用）。
> 前置：D80（限药 / 术者换防 / 逆行）、D94（`normal: true` 门槛）、D152 卸甲、D154 卫士潮、D155 铁卫、D158 `converted:` 转化怪扭矩。
> 实现对齐：等 `COORD-asset-fix-1655` DONE，再用 `tools/p1sim/p2econ.py --mods` 过 §7 门禁后，在下一空闲 CoreRpg 窗落地。
> 参考：Diablo 大秘境式周旋转突变（改遭遇不改掉落表）、PoE 地图词缀（侧向压力、非永久乘区）、WoW 大秘境词缀轮换（每周一条、可预测）。机制**只落在**现有 `rotation.modifiers` 字段：`remap` / `potion_cap` / `swap_rooms` / `converted:{hp,atk,interval,speed}` / `text` / `normal`。

## 0. 一段话裁决

在 **P2-8 精选图周规则**（现网 6 条）上再加 **3 条侧向 remap+converted**，全部 **challenge-only**（不设 `normal: true`），不改奖励、不加乘区、不加新货币。`p2econ --mods` 通关率差目标贴近 0（绝不能做成绝药级干涸）。缺源角色 / 缺目标角色的图自动 noop（与现网一致）。本窗只出文档；源表 D175 行等 asset-fix 提交后再补。

## 1. 白话版（给服主 / 玩家）

- 精选图的挑战局里，每周仍只有一条规则；规则池从 6 条扩到 **9** 条（ISO 周 mod 9；图仍 mod 7 → 63 周把 63 种组合各轮一次）。
- 新三条都只改「谁刷出来、转化怪手感」，**奖励表一字不动**：
  - **弓潮**：有术者的图，术者全换成弓手（更多箭、少直线预警）。
  - **龟甲**：有术者的图，术者换成厚血慢手的重甲（硬、慢、不疼那么快）。
  - **压阵**：有弓手的图，弓手换成冲得快的近战（贴脸杂兵潮，少远程）。
- Q01 / Q02 没有术者 → 弓潮、龟甲在那两张图等于没规则（noop）；没有弓手或缺近战角色的图 → 压阵 noop。
- 普通版首通、未首通重打、别的挑战图、深渊、团本、连战：**新三条都不进**（与术者换防 / 卸甲 / 卫士潮 / 铁卫同一闸）。

## 2. 现网基线（只读）

| id | 名 | 字段 | normal | 备注 |
|---|---|---|---|---|
| `lean` | 限药 | `potion_cap: 3` | true | 轻规则；已首通普通重打也生效 |
| `casters` | 术者换防 | `remap: {ranged: caster}` | false | Q01/Q02 无 caster → 保持弓手 |
| `reverse` | 逆行 | `swap_rooms: true` | true | 房 1↔3 敌群互换 |
| `disarm` | 卸甲 | `remap: {heavy: melee}` + converted | false | D152 / D158 |
| `guards` | 卫士潮 | `remap: {ranged: heavy}` + converted | false | D154 / D158 |
| `wall` | 铁卫 | `remap: {melee: heavy}` + converted | false | D155 / D158 |

代码锚点（本窗不改）：

- `plugins/CoreRpg/ember-v1-runs.yml` / `CoreRpg/.../ember-v1-runs.yml` → `rotation.modifiers`
- `EmberRunMaps.Modifier`（`remap` / `potionCap` / `swapRooms` / `normal` / `convHp|Atk|Interval|Speed`）
- `EmberRunDirector.spawn(..., conv)`（D158 转化怪）
- `EmberRunService` 进本写 `modifier`；`/corerpg p1 runs modifier <id|clear>`
- 模型：`tools/p1sim/p2econ.py --mods`、`p1sim.convert_roles` / `mod_cfg`

已占用 remap 方向：`ranged→caster`、`heavy→melee`、`ranged→heavy`、`melee→heavy`。  
本包只用**尚未占用**的：`caster→ranged`、`caster→heavy`、`ranged→melee`。

## 3. 成熟游戏对照（短）

| 参考 | 学什么 | 落到 Ember |
|---|---|---|
| Diablo 大秘境 / 赛季突变 | 每周改遭遇形态，掉落表不变 | 只改刷怪角色与转化扭矩；结算表不动 |
| PoE 地图词缀 | 侧向压力（更多某类怪、药限制），不是永久角色强度 | `remap` / `potion_cap`；禁止全局伤害乘区 |
| WoW M+ 词缀 | 可预测轮换、缺机制图可「空转」 | ISO 周索引；缺角色 noop |
| 本服既有 6 条 | 挑战专用战斗 remap + 轻规则才 `normal: true` | 新三条全部 challenge-only |

不抄：反伤、全图增伤、体力税、新货币、能见度黑雾（1.12 无现成钩子）。

## 4. 提案表（选中的 3 条）

数值示意；落地前以 `p2econ.py --mods` 扫一遍，超标只调 `converted` 倍率，**禁止**加奖励。

| id | 玩家名 | 机制 | 字段草图 | 闸门 | 玩家要做什么 | noop 条件 |
|---|---|---|---|---|---|---|
| `bolters` | 弓潮 | 术者槽位全换成弓手；略加快射速手感 | `remap: {caster: ranged}`，`converted: {interval: 0.9}` | challenge-only | 多躲箭、少躲直线；Q03–Q07 有感 | 图无 `caster` 或无 `ranged`（Q01/Q02） |
| `shell` | 龟甲 | 术者换成厚血慢手重甲 | `remap: {caster: heavy}`，`converted: {hp: 1.2, atk: 0.7, interval: 1.35}` | challenge-only | 多砍硬壳、少躲直线；DPS 时间税，不是爆发 | 图无 `caster` 或无 `heavy` |
| `press` | 压阵 | 弓手换成冲得快的近战 | `remap: {ranged: melee}`，`converted: {speed: 1.3, hp: 0.85}` | challenge-only | 少挨箭、多清贴脸；走位拉开 / 集火 | 图无 `ranged` 或无 `melee` |

yml 追加草图（实现窗双写两份 runs yml；**本窗不写**）：

```yaml
  # Pack 2 (D175): 3 sidegrades; all challenge-only. Pool 6→9 → ISO week mod 9 (63 map×rule pairs).
  - {id: bolters, name: 弓潮, remap: {caster: ranged}, converted: {interval: 0.9}, text: 术者换成弓手：更多箭、少直线预警（出手间隔 ×0.9；没有术者的图不变）}
  - {id: shell, name: 龟甲, remap: {caster: heavy}, converted: {hp: 1.2, atk: 0.7, interval: 1.35}, text: 术者换成龟甲重甲：更肉、更慢、单下更轻（生命 ×1.2，攻击 ×0.7，间隔 ×1.35；没有术者的图不变）}
  - {id: press, name: 压阵, remap: {ranged: melee}, converted: {speed: 1.3, hp: 0.85}, text: 弓手换成冲锋近战：贴脸杂兵潮（移速 ×1.3，生命 ×0.85；没有弓手的图不变）}
```

聊天 / PAPI：沿用现网 `%corerpg_p1_modifier%` 与开局一行；`name` / `text` 如上。

与既有条的差异（防撞车）：

| 新条 | 易混旧条 | 差别 |
|---|---|---|
| 弓潮 caster→ranged | 术者换防 ranged→caster | **反向**；不是同一 remap |
| 龟甲 caster→heavy | 卫士潮 ranged→heavy（快）、铁卫 melee→heavy（重击） | 源是术者；扭矩是高血低攻慢手，不是冲锋也不是重击 |
| 压阵 ranged→melee | 卫士潮 ranged→heavy、卸甲 heavy→melee | 目标是近战杂兵+移速，不是重甲；也不是卸甲方向 |

## 5. 明确否决

| 想法 | 原因 |
|---|---|
| `drought` / `potion_cap: 2` 更严限药 | 与 `lean` 同源叠床架屋；更严药帽易成「绝药」类干涸，违反门禁 |
| `fog` / 黑暗 / 降视距 | 1.12 无现成 hooks；客户端亮度可抵消；伤预警可读性（D80 已否决黑暗） |
| `swarm` 增加刷怪数量 | `rotation.modifiers` **没有** count 乘子字段；不得发明新 Java 刷怪倍增 |
| `splitters` heavy→melee 精英变体 | 与 `disarm` 同 remap 方向；改精英需 variety/新钩子，越界 |
| 反伤 / 全局伤害乘区 / 真实伤害 | 对账麻烦；违反「不加倍率区」；玩家手感差 |
| 体力税 / 进本加费 | 改经济入口，超出侧向花样；需另开经济窗 |
| 新货币 / 印记 / 碎片奖励 | §23.3；周规则明确不改奖励 |
| 化妆品 / 粒子 / 宠物周规则 | 用户 10-04：化妆品后置 |
| `treasure→*` / `*→treasure` | 宝藏怪 atk:0；转战斗或战斗变免费肉是漏洞邻域 |
| 新三条设 `normal: true` | 战斗 remap 在普通版会污染首通体验或像术者换防那样「误减压」；一律 challenge-only |
| 新 Java 技能类型 / 新 Mythic 包 | 本包只允许现有 Modifier 字段 |

## 6. 漏洞审查

| 场景 | 风险 | 设计约束 |
|---|---|---|
| 周上限 / 精选挑战 cap 绕过 | 靠换规则多领奖 | 规则**不改**结算；`weekly_cap` 与印记奖励路径不动 |
| 失败重试刷 | 同一周反复打精选挑战 | 与现网一致：体力 / 周 cap 约束；规则不给额外掉落 |
| `normal: true` 误开 | 首通普通局被 remap，或未首通被减压 | 新三条 **禁止** `normal`；单测断言 `normal==false` |
| 图缺源/目标角色 | 规则「空转」玩家以为坏了 | 既有 noop 模式；开局 text 仍显示，刷怪日志可看到无 converted；可接受 |
| 弓潮 / 龟甲同周互斥 | 两路都吃 caster | 每周只一条；轮换里自然分开，无需互斥表 |
| 压阵让远程图变「纯近战」过易 | 通关率上飘 | 门禁：`--mods` clear-rate Δ 贴近 0；过易则降 `speed` 或去掉 hp 减免 |
| 龟甲过肉拖局 | 时间税过大 | 过难过长则降 `hp` 或升 `atk`；禁止用加奖励补偿 |
| 与 variety 词缀叠乘 | 重打普通才有 variety；新条 challenge-only | 模式不重叠；无需额外互斥 |
| 强制测试钩子泄漏 | 管理钩子带进生产周 | 现网 `forcedModifier` 一次性；实现勿改语义 |
| 源表 / balance 误 bump | 抢 asset-fix / 无谓升版本 | 本窗不改源表、不 bump；实现窗再 +1 |

## 7. 平衡门禁（实现窗，非本窗）

本窗 **不**跑 `tools/p1sim`（`COORD-gear-stage0` / growthrun 占锁）。实现前须：

| 检查 | 通过线 |
|---|---|
| `tools/p1sim/p2econ.py --mods` | 每条新规则相对「无规则 / 轮换基线」精选挑战通关率差 **贴近 0**（参考 D152/D154/D155 的 +0 顶格或噪声内）；**禁止**出现 lean 更严或绝药级干涸 |
| 经济列 | 印记 / 币中位噪声内（规则不改奖励，预期不变） |
| 超标 | 只许调该条 `converted` 的 hp/atk/interval/speed；**禁止**加掉落 / 加永久乘区 / 放宽 `normal` |
| 首通污染 | 新三条不得出现在普通首通路径（单测 + 代码审 `normal`） |

冒烟（实现窗，几分钟）：FreshQ 系列强制 `runs modifier bolters|shell|press` 各一局精选挑战（建议 Q03 有 caster+ranged、Q01 验证 noop）；确认开局一行、房间刷怪角色日志、结算与无规则周一致。

## 8. 实现清单（下一空闲 CoreRpg 窗）

前置：`COORD-asset-fix-1655` **DONE**；`tools/p1sim` 锁已释放（或仅跑 `--mods` 的短扫描征得 gear-stage0 同意）。

1. 双写 `ember-v1-runs.yml`：`rotation.modifiers` 追加 §4 三条；注释改为「7 图 × 9 规则 → 63 组合」。
2. `balance_version` +1（战斗遭遇变更；**本设计提交不 bump**）。
3. 单测（建议名）：
   - `weekModsPack2IdsPresent_D175` — KNOWN 含 bolters/shell/press；`normal==false`
   - `weekModsPack2RemapDirections_D175` — caster→ranged / caster→heavy / ranged→melee；converted 键解析
   - `weekModsPack2NoopWhenRoleMissing_D175` — Q01 上 bolters/shell 不改组成
4. 冒烟 FreshQ 强制三 id；`check-dp-spawns` 如常。
5. 源表补 D175 行 + 本 P2 节实现回填（若 asset-fix 已提交）。
6. **不**改：资产路径、variety、首领招式、装备结构、主线独占掉落、化妆品。

## 9. 源表与协调

- 决策号：**D175**（本设计）。
- `DESIGN-ember-v1.0-P1-source-table.md` 的 D175 行：本窗 **不写入**——该文件正被 `COORD-asset-fix-1655`（D172）改动；等其提交后再由例行任务或实现窗补行（同 D173 处理）。
- P2 草案指针：`design-ember-v1.1-P2-draft.md` §5zj。
- 协调：`/workspace/COORD-routine-1714.txt`。

## 10. 一句话验收

精选挑战周规则池 6→9，多三条纯侧向 remap+converted（弓潮 / 龟甲 / 压阵）；奖励与首通体验不变；`--mods` 通关率差贴近 0；缺角色图 noop；没有新养成、没有新货币、没有新 Java 钩子。
