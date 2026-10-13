# 余烬 · 短征 R2 精英可读 Cast（短本玩法花样 · ≠sx20 · ≠抬表）

STATUS=**已批 A · 方案 M · D13305** · 总控 2026-10-13 21:34 Asia/Shanghai · 2026-10-13 21:31 Asia/Shanghai · tip [`STATUS-ember-next-hard-debt-short-r2-elite-cast-need-design-2026-10-13.md`](../status/STATUS-ember-next-hard-debt-short-r2-elite-cast-need-design-2026-10-13.md) · RESEARCH [`RESEARCH-ember-short-r2-elite-cast-readable-2026-10-13.md`](RESEARCH-ember-short-r2-elite-cast-readable-2026-10-13.md) · backlog `B-short-r2-elite-cast-readable` · **≠关观察 ≠抬日表 ≠开 R ≠开 K3 ≠假开旧日常 ≠sx20 ≠菜单诚实/observe 空转 ≠抬 Elite/Boss 基础伤表 ≠新地图 ≠复开 D423 ≠教裸指令**

> **一句话玩家价值：** 短征第二房精英也会「蓄力→可躲」——主题演出提前一房；**不**开 sx20、**不**抬日表。

> **批 A = 采纳方案（荐 M）。批 A ≠ 施工 ≠ 关观察 ≠ 抬日表 ≠ 开 R ≠ 开 K3 ≠ 交 sx20。**

---

## 0. 证据表

| # | 来源 | 口径 |
|---|------|------|
| E1 | 总控连开 · TIP-BY live `e214b0399` | 再交薄 tip：短本花样或挂机庭第二件；禁 sx20/抬日表/诚实空转/L13/K3 |
| E2 | sx01–19 Elite 仅 SoftHit | MM `EmberSxNNElite` |
| E3 | 同本 Boss 主题 Cast live（timer70 · dmg0.85 · 喊话+粒子+delay） | `EmberDungeonDiffSkills.yml` |
| E4 | D13304§6#2 后置 R2 Cast | DESIGN-afk-courtyard-quick-spend |
| E5 | 短征链停 sx19 · 禁 sx20 | backlog / STATUS |

**一句话问题：** R2 精英房无可读招式，短本中段体感空。

**定位拍板：**

| 选项 | 结论 | 理由 |
|------|------|------|
| 再叠选页/诚实半行 | **否** | 空转硬禁 |
| sx20 / 新图 | **否** | 硬禁 |
| 挂机庭顺手升阶 | **后置** | 刚交顺手强化；本窗优先短本花样 |
| 今日推轮换 | **后置** | 非房内花样 |
| **R2 Elite 轻压可读 Cast（荐）** | **采纳** | E2–E4；范式现成；MM-only 薄 |

---

## 1. 玩家感知目标（≤3）

1. **精英会蓄力：** 进 R2 数秒内见主题喊话+粒子蓄力，侧移可躲。  
2. **比 Boss 轻：** 伤害与频率明显低于本本 Boss Cast（不是两个 Boss）。  
3. **旧本变鲜：** sx01–19 不用新本也能感到「第二房有花样」。

---

## 2. 方案表

| 方案 | 内容 | 评价 |
|------|------|------|
| **A · 只改选页「精英更狠」口号** | 文案 | **否决独批** — 无房内动作 |
| **M · sx01–19 Elite 挂轻压主题 Cast（荐）** | 每本 `EliteCast`（同主题降伤慢频）+ Elite Skills 一行；**零改** HP/掉落/日帽/地图 | **荐** — 真花样；薄可施工 |
| **L · 仅 sx01–06 试点** | 同 M 缩范围 | 可批更薄；全线同形成本仍低 |
| **W · sx20 / 原伤贴 Boss Cast / 抬伤表 / 诚实空转** | — | **否决** — 硬禁 / 双 Boss 感 |

### 批 A 勾选（总控填）

- [x] **方案 M**（W1a Skills 轻压 Cast 必做；W1b Elite 挂接必做；W1c 全线 sx01–19 同批；见 §3）
- [ ] 或勾 **方案 L**（未选·全线）（仅 sx01–06；其余后置）
- [x] 否决 A（只口号）/ W（sx20/原伤复读/抬表/诚实空转）
- [x] 批注：总控批 A·M · D13305 · TIP-BZ · 派余烬-怪物 MM-only · 不派插件 · 全线 sx01–19

---

## 3. 可落地（方案 M）

### 3.1 数值与可读钉死

| 项 | Boss Cast（对照） | Elite Cast（本窗） | 纪律 |
|----|-------------------|-------------------|------|
| Timer | `~onTimer:70` | **`~onTimer:100`** | 更慢；禁同 70 双压 |
| 结算伤害 | ≈0.85 | **≈0.50–0.55** | 轻压；禁 ≥Boss |
| 蓄力 delay | 30 | **30–35**（保持可读） | 禁无蓄力瞬伤 |
| 半径 | r5 | **r4–r5** | 可躲；禁全图秒杀 |
| 喊话 | 守吏/哨长主题 | **伍长/精英蓄力——拉开** | 短文案；玩家不打指令 |
| CD | 8 | **8–10** | 与 Timer 协调 |

### 3.2 窗拆分

| 窗 | 做什么 | 不动 |
|----|--------|------|
| **W1a · EliteCast Skills（必做 · MM）** | `EmberDungeonDiffSkills.yml`（或并列 Skills 文件）为 sx01–19 各增 `EmberSxNNEliteCast`：粒子主题对齐本本 Boss Cast，伤害/Timer 按 §3.1 | 不改 Boss Cast 本体数值 |
| **W1b · Elite 挂接（必做 · MM）** | `EmberSxNNElite` Skills 追加 `- skill{s=EmberSxNNEliteCast} @self ~onTimer:100` | 不改 SoftHit/掉落/mmxp；不改 Type/Equipment |
| **W1c · 范围（荐全线）** | sx01–19 一次同形；若批 L 则只 01–06 | 不交 sx20；不新建地图/DP |
| **W1d · 选页半行（可选）** | `ember_p1_short` 某说明格 +半行「精英房可躲蓄力」 | **非主交付**；禁诚实半行族扩写 |
| **W1e · jar（默认不做）** | — | 纯 YAML；无 CoreRpg 改动 |

### 3.3 主题对照（施工索引）

| 本 | Boss Cast | EliteCast 主题提示 |
|----|-----------|-------------------|
| sx01 | SentryCast | 哨岗轻扫 |
| sx02 | BeaconCast | 锈灯轻落点 |
| sx03 | FrostCast | 霜雾轻楔 |
| sx04 | FurnaceCast | 井心轻喷 |
| sx05 | WindCast | 风剪轻条 |
| sx06 | RingCast | 环扫轻弧 |
| sx07 | TowerCast | 塔冠轻坠 |
| sx08 | LayerCast | 层间轻砸 |
| sx09 | CanalCast | 渠心轻溅 |
| sx10 | GateCast | 闸心轻压 |
| sx11 | MirrorCast | 镜面轻扫 |
| sx12 | PivotCast | 枢扫轻斩 |
| sx13 | BalanceCast | 衡扫轻斩 |
| sx14 | RiftCast | 裂扫轻斩 |
| sx15 | SpiralCast | 旋扫轻斩 |
| sx16 | ShadowCast | 影扫轻斩 |
| sx17 | TideCast | 潮扫轻斩 |
| sx18 | PrismCast | 晶扫轻斩 |
| sx19 | StringCast | 弦扫轻斩 |

粒子限 1.12.2 已用集（flame/lava/crit/cloud 等）；禁无效粒子名。

### 3.4 与邻域边界

| 邻域 | 边界 |
|------|------|
| D13304 顺手花料 | **不抢**；挂机庭已 live |
| Boss Cast / D443 可读 | **保留**；Elite 另技能，禁改 Boss 手感 |
| sx20 | **不做** |
| 挂机日表 / 伤乘 | **不动** |
| 顺手升阶/精工 | **后置**（挂机庭第二件下一候选） |

---

## 4. 验收（施工号用 · 本号只定规格）

| ID | 项 |
|----|-----|
| V1 | 任选 ≥3 本（含 sx01 + 一中段 + sx19）：R2 Elite 战斗中出现蓄力喊话+粒子，delay 后结算 |
| V2 | 同本对比：Elite Cast 伤害与触发频率 **低于** Boss Cast |
| V3 | `git diff`：无改 `daily_kills`/`afk.tiers`/UpgradeRules；无新增 sx20；无改 Elite/Boss 基础 HP·Damage 字段（除非总控另批） |
| V4 | SoftHit/掉落/mmxp/日帽/选页进本链路与施工前一致 |
| V5 | 玩家面无裸指令；真地形本内可感（忌空平面测试当交付） |

---

## 5. 禁区

- sx20 · 新地图硬开 · 抬挂机产量/日顶 · 抬伤乘/skill_mult · Affix densify  
- Pack6 · 天赋 · 灰印 · R · L13 · K3 · 假开旧日常  
- 菜单诚实/sure·pity·soft 半行空转 · observe 空转  
- Elite 直接复用 Boss Cast **同伤同 Timer** · 无蓄力瞬伤  
- 复开 D423 · stage 脏 runtime  

---

## 6. 后置（明文 · 本窗不做）

1. 挂机庭顺手升阶 / 精工（D13304§6#1 · 庭内第二件花料）  
2. 今日推短征轮换  
3. Elite Cast 主题粒子二次打磨 / 专属音效包  
4. sx20（仍禁，除非总控另开窗）  

---

## 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-13 | 策划 · 初稿 STATUS=**待批 A** · 荐 M · TIP-BY 后连开 · 选题短征 R2 Elite 可读 Cast · 拟 D13305 · ≠sx20 ≠抬日表 |

*拟 D13305 · 短征 R2 精英可读 Cast · 待批 A·方案 M · ≠关观察 ≠抬日表 ≠sx20。*
