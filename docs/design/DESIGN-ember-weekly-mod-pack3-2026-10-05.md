# Ember Weekly Modifier Pack 3：精选图周规则扩包（D186，2026-10-05）

> 状态：**已上线 CoreRpg 1.65.25 / D186**（2026-10-05）。实现：双写 `ember-v1-runs.yml` +3（skirmish/hexers/ballista），`balance_version` 47→48；单测 12 规则 / 84 对；冒烟 **合批 PASS 12/0**（2026-10-05）。Stage C `p2econ --mods --weeks 24` **within range**。
> 前置：D80 / D94 / D152 / D154 / D155 / D158 / D175→D178 Pack 2。
> 参考：Diablo 大秘境式周旋转突变、PoE 地图词缀、WoW 大秘境词缀轮换（同 Pack 2）。机制**只落在**现有 `rotation.modifiers` 字段：`remap` / `converted:{hp,atk,interval,speed}` / `text` / `normal`。

## 0. 一段话裁决

在 **P2-8 精选图周规则**（现网 9 条）上再加 **3 条侧向 remap+converted**，全部 **challenge-only**（不设 `normal: true`），不改奖励、不加乘区、不加新货币。缺源角色 / 缺目标角色的图自动 noop。本包用完 Pack 2 留下的未占用方向中的 3 条；余下 `heavy→caster` / `caster→melee` 留给 Pack 4。

## 1. 白话版（给服主 / 玩家）

- 精选图的挑战局里，每周仍只有一条规则；规则池从 9 条扩到 **12** 条（ISO 周 mod 12；图仍 mod 7 → 84 周把 84 种组合各轮一次）。
- 新三条都只改「谁刷出来、转化怪手感」，**奖励表一字不动**：
  - **散兵**：有近战的图，近战全换成弓手（更多箭、少贴脸）。
  - **咒潮**：有近战的图，近战换成术者（直线预警，躲开就不疼）。
  - **重弩**：有重甲的图，重甲换成慢射硬弓（单下重、出手慢、走得慢）。
- 没有近战或缺弓手/术者目标角色的图 → 散兵 / 咒潮 noop；没有重甲或缺弓手目标 → 重弩 noop。
- 普通版首通、未首通重打、别的挑战图、深渊、团本、连战：**新三条都不进**。

## 2. 现网基线（只读，Pack 2 后）

| id | 名 | 字段 | normal | 备注 |
|---|---|---|---|---|
| `lean` | 限药 | `potion_cap: 3` | true | 轻规则 |
| `casters` | 术者换防 | `remap: {ranged: caster}` | false | |
| `reverse` | 逆行 | `swap_rooms: true` | true | |
| `disarm` | 卸甲 | `remap: {heavy: melee}` + converted | false | |
| `guards` | 卫士潮 | `remap: {ranged: heavy}` + converted | false | |
| `wall` | 铁卫 | `remap: {melee: heavy}` + converted | false | |
| `bolters` | 弓潮 | `remap: {caster: ranged}` + converted | false | Pack 2 |
| `shell` | 龟甲 | `remap: {caster: heavy}` + converted | false | Pack 2 |
| `press` | 压阵 | `remap: {ranged: melee}` + converted | false | Pack 2 |

已占用 remap：`ranged→caster`、`heavy→melee`、`ranged→heavy`、`melee→heavy`、`caster→ranged`、`caster→heavy`、`ranged→melee`。  
本包只用尚未占用的：`melee→ranged`、`melee→caster`、`heavy→ranged`。  
留给 Pack 4：`heavy→caster`、`caster→melee`。

## 3. 成熟游戏对照（短）

| 参考 | 学什么 | 落到 Ember |
|---|---|---|
| Diablo 大秘境 / 赛季突变 | 每周改遭遇形态，掉落表不变 | 只改刷怪角色与转化扭矩；结算表不动 |
| PoE 地图词缀 | 侧向压力，不是永久角色强度 | `remap` + `converted`；禁止全局伤害乘区 |
| WoW M+ 词缀 | 可预测轮换、缺机制图可「空转」 | ISO 周索引；缺角色 noop |
| 本服 Pack 1–2 | 战斗 remap 一律 challenge-only | 新三条全部 `normal` 缺省 false |

不抄：反伤、全图增伤、体力税、新货币、能见度黑雾、treasure 重映射。

## 4. 提案表（选中的 3 条）

| id | 玩家名 | 机制 | 字段草图 | 闸门 | 玩家要做什么 | noop 条件 |
|---|---|---|---|---|---|---|
| `skirmish` | 散兵 | 近战槽位全换成弓手；略加快射速、略脆 | `remap: {melee: ranged}`，`converted: {interval: 0.85, hp: 0.9}` | challenge-only | 多躲箭、少清贴脸近战 | 图无 `melee` 或无 `ranged` |
| `hexers` | 咒潮 | 近战换成术者 | `remap: {melee: caster}`，`converted: {interval: 1.1}` | challenge-only | 多躲直线预警；Q03–Q07 有感 | 图无 `melee` 或无 `caster`（Q01/Q02） |
| `ballista` | 重弩 | 重甲换成慢射硬弓 | `remap: {heavy: ranged}`，`converted: {atk: 1.25, interval: 1.35, speed: 0.8}` | challenge-only | 少砍硬甲、多躲重箭；走位拉开 | 图无 `heavy` 或无 `ranged` |

yml 追加：

```yaml
  # Pack 3 (D186): 3 sidegrades; all challenge-only. Pool 9→12 → ISO week mod 12 (84 map×rule pairs).
  - {id: skirmish, name: 散兵, remap: {melee: ranged}, converted: {interval: 0.85, hp: 0.9}, text: 近战换成弓手：更多箭、少贴脸（出手间隔 ×0.85，生命 ×0.9；没有近战的图不变）}
  - {id: hexers, name: 咒潮, remap: {melee: caster}, converted: {interval: 1.1}, text: 近战换成术者：直线预警，躲开就不疼（出手间隔 ×1.1；没有近战或术者的图不变）}
  - {id: ballista, name: 重弩, remap: {heavy: ranged}, converted: {atk: 1.25, interval: 1.35, speed: 0.8}, text: 重甲换成重弩弓手：单下更重、更慢（攻击 ×1.25，间隔 ×1.35，移速 ×0.8；没有重甲的图不变）}
```

与既有条的差异（防撞车）：

| 新条 | 易混旧条 | 差别 |
|---|---|---|
| 散兵 melee→ranged | 铁卫 melee→heavy、压阵 ranged→melee、弓潮 caster→ranged | 源是近战、目标是弓手；扭矩是快射略脆，不是重甲也不是反向压阵 |
| 咒潮 melee→caster | 术者换防 ranged→caster、弓潮 caster→ranged | 源是近战；与术者换防同目标但不同源 |
| 重弩 heavy→ranged | 卸甲 heavy→melee、卫士潮 ranged→heavy | 源仍是重甲但目标是弓；扭矩是慢硬箭，不是卸甲脆近战 |

## 5. 明确否决

| 想法 | 原因 |
|---|---|
| `heavy→caster` / `caster→melee` 本包一起上 | 留 Pack 4；本包正好 +3；与 Pack 2 节奏一致 |
| `drought` / 更严限药 | 与 `lean` 叠床架屋；易成绝药 |
| `fog` / 黑暗 | 1.12 无钩子；D80 已否决 |
| `swarm` 刷怪倍增 | 无 count 字段；不得发明新 Java |
| 反伤 / 全局乘区 / 真实伤害 | 违反「不加倍率区」 |
| 体力税 / 新货币 / 奖励改动 | 超出侧向花样 |
| 化妆品周规则 | 用户 10-04：化妆品后置 |
| `treasure→*` / `*→treasure` | 宝藏怪 atk:0；漏洞邻域 |
| 新三条设 `normal: true` | 战斗 remap 污染首通；一律 challenge-only |
| 新 Java 字段 / 新 Mythic 包 | 本包只允许现有 Modifier 字段 |

## 6. 漏洞审查

| 场景 | 风险 | 设计约束 |
|---|---|---|
| 周上限 / 精选挑战 cap 绕过 | 靠换规则多领奖 | 规则**不改**结算 |
| 失败重试刷 | 同一周反复打 | 体力 / 周 cap；规则不给额外掉落 |
| `normal: true` 误开 | 首通被 remap | 新三条禁止 `normal`；单测断言 |
| 图缺源/目标角色 | 规则「空转」 | 既有 noop；可接受 |
| 散兵 / 咒潮同吃 melee | 两路都吃近战 | 每周只一条；无需互斥表 |
| 散兵让近战图变「纯远程」过易 | 通关率上飘 | Stage C deferred；超标只调 converted |
| 重弩单下过痛 | 通关率下沉 | 超标降 `atk` 或升 `interval`；禁止加奖励 |
| 与 variety 词缀叠乘 | 重打普通才有 variety；新条 challenge-only | 模式不重叠 |
| treasure 误 remap | 免费肉 / 零伤战斗 | 本包无 treasure 键 |

## 7. 平衡门禁

| 检查 | 通过线 |
|---|---|
| `tools/p1sim/p2econ.py --mods` | **DEFERRED**（POLICY：offline sim within range；本窗不跑长扫） |
| 经济列 | 规则不改奖励，预期不变 |
| 超标（若日后跑） | 只调该条 `converted`；禁止加掉落 / 永久乘区 / 放宽 `normal` |
| 首通污染 | 新三条不得出现在普通首通路径（单测 `normal==false`） |

冒烟：**DEFERRED batch**（用户 01:53 攒一堆后再测）。本窗不做 FreshQ 长测。

## 8. 实现清单

1. 双写 `ember-v1-runs.yml`：追加 §4 三条；注释「7 图 × 12 规则 → 84 组合」。
2. `balance_version` 47→48；注释行加 `48 = D186 Weekly Mod Pack 3: ...`。
3. `pom.xml` + `plugin.yml` → 1.65.25。
4. 单测：池 9→12；skirmish/hexers/ballista 在场；`normal==false`；84 对循环；converted 键解析。
5. 源表 D186 行 + §13.100。
6. **不**改：资产路径、variety、首领招式、装备结构、主线独占掉落、化妆品、AFK、签到。

## 9. 源表与协调

- 决策号：**D186**。
- `DESIGN-ember-v1.0-P1-source-table.md` D186 行 + ### 13.100。
- 协调：`/workspace/COORD-routine-0346.txt`。

## 10. 一句话验收

精选挑战周规则池 9→12，多三条纯侧向 remap+converted（散兵 / 咒潮 / 重弩）；奖励与首通体验不变；缺角色图 noop；没有新养成、没有新货币、没有新 Java 钩子。
