# 余烬 · 短征 R1 远程卫可读轻 Cast（短本玩法花样续 · ≠sx20 · ≠抬表）

STATUS=**已批 A · 方案 M · D13314** · 总控 2026-10-14 00:15 Asia/Shanghai · TIP-CI · docs→施工

> **一句话玩家价值：** 短征第一房远程卫也会「蓄力→可躲」——R1 近战+远程双侧可读；**不**开 sx20、**不**抬日表。

> **批 A = 采纳方案（荐 M）。批 A ≠ 施工 ≠ 关观察 ≠ 抬日表 ≠ 开 R ≠ 开 K3 ≠ 交 sx20。**

---

## 0. 证据表

| # | 来源 | 口径 |
|---|------|------|
| E1 | 总控 TIP-CH CLOSED · 连开 | 拟 D13314 · TIP-CI；优先短征日更/挂机庭资源环新乐趣；禁 sx20/抬表/诚实空转/L13/K3/Pack6/灾厄/第五件花料 |
| E2 | D13313§6① / D13311§6③ | 明文后置「R1 远程轻 Cast」 |
| E3 | sx01 Skeleton / sx02 Archer / sx03–19 Crossbow 仅 SoftHit | MM Mobs 实扫 |
| E4 | GuardCast live timer140·dmg0.32 | D13311 · DiffSkills |
| E5 | Elite/Boss Cast 梯队 live | D13305 |
| E6 | 四花/今日推/冲一下/周短征/三冲已齐；第五件硬禁 | backlog · 派单 |

**一句话问题：** R1 近战已会蓄力，远程仍平砍——开场房可读只做了一半。

**定位拍板：**

| 选项 | 结论 | 理由 |
|------|------|------|
| 再叠选页/诚实半行 | **否** | 空转硬禁 |
| sx20 / 新图 / 抬伤表 | **否** | 硬禁 |
| 挂机庭第五件花料 | **否** | 硬禁 |
| 只粒子打磨不挂远程 | **否** | 非债清 |
| 改题挂机庭推荐花聚合 | **后置** | 可感但弱于远程 SoftHit 硬债 |
| **R1 远程卫轻压可读 Cast（荐）** | **采纳** | E2–E5；合法接力；MM-only 薄 |

---

## 1. 玩家感知目标（≤3）

1. **远程也会蓄力：** 进 R1 数秒内见弓/弩卫主题喊话+粒子蓄力，侧移可躲。  
2. **比近战卫更轻更慢：** 伤害与频率明显低于本本 GuardCast（不是双 Boss）。  
3. **旧本变鲜：** sx01–19 不用新本也能感到「开场双侧有花样」。

---

## 2. 方案表

| 方案 | 内容 | 评价 |
|------|------|------|
| **A · 只改选页「远程更狠」口号** | 文案 | **否决独批** — 无房内动作 |
| **M · sx01–19 R1 远程挂轻压主题 Cast（荐）** | 每本 `ArcherCast` + 远程主怪 Skills 一行；**零改** HP/掉落/日帽/地图；**不**改近战 GuardCast | **荐** — 真花样；薄可施工 |
| **L · 仅 sx01–06 试点** | 同 M 缩范围 | 可批更薄；全线同形成本仍低 |
| **W · sx20 / 原伤贴 Guard·Elite·Boss / 与近战同 Timer / 抬伤表 / 诚实空转 / 第五件花料** | — | **否决** — 硬禁 / 刷屏 |

### 批 A 勾选（总控填）

- [x] **方案 M**（W1a Skills 轻压 Cast 必做；W1b 远程主怪挂接必做；W1c 全线 sx01–19 同批；见 §3）
- [ ] 或勾 **方案 L**（仅 sx01–06；其余后置）· **本窗不批 L**
- [x] 否决 A（只口号）/ W（sx20/原伤复读/同 Timer 双压/抬表/诚实空转/第五件）
- [x] 批注：总控批 A·M · D13314 · TIP-CI · 全线 sx01–19 ArcherCast · timer180 · dmg≈0.22–0.28 · 仅远程挂接 · 零改 Guard/Elite/Boss/HP/掉落 · 不派插件/物品主交付

---

## 3. 可落地（方案 M）

### 3.1 数值与可读钉死（梯队）

| 项 | Boss Cast | Elite Cast | Guard Cast（近战） | **Archer Cast（本窗·远程）** | 纪律 |
|----|-----------|------------|-------------------|------------------------------|------|
| Timer | `~onTimer:70` | `~onTimer:100` | `~onTimer:140` | **`~onTimer:180`** | 最慢；**禁**同 140 双压 |
| 结算伤害 | ≈0.85 | ≈0.50–0.55 | ≈0.30–0.35 | **≈0.22–0.28** | 最轻；禁 ≥Guard |
| 蓄力 delay | 30 | 30–35 | 30–35 | **30–35** | 禁无蓄力瞬伤 |
| 半径 | r5 | r4–r5 | r3.5–r4 | **r3.5–r4** | 可躲；禁全图秒杀 |
| 喊话 | 守吏主题 | 伍长/精英 | 哨卒/卫士——侧移 | **弓卫/弩卫蓄力——侧移** | 短文案 |
| CD | 8 | 8–10 | 10–12 | **14–16** | 与 Timer 错开 |
| 挂接对象 | Warden | Elite | 近战主怪 | **仅远程**（sx01=`Skeleton`；sx02=`Archer`；sx03–19=`Crossbow`） | **禁**再挂 Guard/Zombie |

### 3.2 窗拆分

| 窗 | 做什么 | 不动 |
|----|--------|------|
| **W1a · ArcherCast Skills（必做 · MM）** | `EmberDungeonDiffSkills.yml`（或并列）为 sx01–19 各增 `EmberSxNNArcherCast`：粒子主题对齐本本 Boss/Elite/Guard，伤害/Timer 按 §3.1 | 不改 Boss/Elite/Guard Cast 本体数值 |
| **W1b · 远程挂接（必做 · MM）** | sx01 `EmberSx01Skeleton`、sx02 `EmberSx02Archer`、sx03–19 `EmberSxNNCrossbow` Skills 追加 `- skill{s=EmberSxNNArcherCast} @self ~onTimer:180`；保留 SoftHit | 不改 SoftHit/掉落/mmxp；不改 Type/Equipment；**不**改近战 Guard/Zombie |
| **W1c · 范围（荐全线）** | sx01–19 一次同形；若批 L 则只 01–06 | 不交 sx20；不新建地图/DP |
| **W1d · 选页半行（可选）** | `ember_p1_short` 说明格 +半行「开场弓弩可躲蓄力」 | **非主交付**；禁诚实半行族扩写 |
| **W1e · jar（默认不做）** | — | 纯 YAML；无 CoreRpg 改动 |

### 3.3 主题对照（施工索引）

| 本 | 远程怪键 | ArcherCast 提示 |
|----|----------|-----------------|
| sx01 | Skeleton | 哨弓轻矢 |
| sx02 | Archer | 栈弩轻矢 |
| sx03 | Crossbow | 闸弩轻楔矢 |
| sx04 | Crossbow | 井弩轻焰矢 |
| sx05 | Crossbow | 崖弩轻剪矢 |
| sx06 | Crossbow | 环弩轻弧矢 |
| sx07 | Crossbow | 塔弩轻坠矢 |
| sx08 | Crossbow | 层弩轻砸矢 |
| sx09 | Crossbow | 渠弩轻溅矢 |
| sx10 | Crossbow | 闸弩轻压矢 |
| sx11 | Crossbow | 镜弩轻扫矢 |
| sx12 | Crossbow | 枢弩轻斩矢 |
| sx13 | Crossbow | 衡弩轻斩矢 |
| sx14 | Crossbow | 裂弩轻斩矢 |
| sx15 | Crossbow | 旋弩轻斩矢 |
| sx16 | Crossbow | 影弩轻斩矢 |
| sx17 | Crossbow | 潮弩轻斩矢 |
| sx18 | Crossbow | 晶弩轻斩矢 |
| sx19 | Crossbow | 弦弩轻斩矢 |

粒子限 1.12.2 已用集（flame/lava/crit/cloud 等）；禁无效粒子名。

### 3.4 与邻域边界

| 邻域 | 边界 |
|------|------|
| D13311 GuardCast | **保留**；Archer 另技能，禁改近战手感；Timer/伤须更慢更轻 |
| D13305 EliteCast / BossCast | **保留**；禁原伤复读 |
| D13309–13 导航链 | **不抢**；本债=房内开场花样 |
| D13304–08 挂机庭花料 | **不叠**第五件 |
| fest / calamity | **本窗不做** |
| sx20 | **不做** |

---

## 4. 验收（施工号用 · 本号只定规格）

| ID | 项 |
|----|-----|
| V1 | 任选 ≥3 本（含 sx01 + 一中段 + sx19）：R1 远程战斗中出现蓄力喊话+粒子，delay 后结算 |
| V2 | 同本对比：Archer Cast 伤害与触发频率 **低于** Guard Cast **低于** Elite **低于** Boss |
| V3 | 同房近战 Guard/Zombie **仍仅**既有 GuardCast（本窗不改）；远程与近战 **不同** Timer |
| V4 | `git diff`：无改 `daily_kills`/`afk.tiers`/UpgradeRules；无新增 sx20；无改基础 HP·Damage 字段 |
| V5 | SoftHit/掉落/mmxp/日帽/选页进本链路与施工前一致；玩家面无裸指令 |

---

## 5. 禁区

- sx20 · 新地图硬开 · 抬挂机产量/日顶 · 抬伤乘/skill_mult · Affix densify  
- Pack6 · 天赋 · 灰印 · R · L13 · K3 · 假开旧日常 · 重开 calamity · 新 fest  
- 菜单诚实/sure·pity·soft 半行空转 · observe 空转  
- Archer 直接复用 Guard/Elite/Boss Cast **同伤同 Timer** · 无蓄力瞬伤 · 与近战同 Timer 双压  
- 再叠挂机庭花料第五件 · 复开 D423 · stage 脏 runtime · 教裸指令 · 撤分格  

---

## 6. 后置（明文 · 本窗不做）

1. GuardCast / EliteCast 粒子二次打磨（无新挂接）  
2. 挂机庭「顺手推荐花」聚合（四花智路由；≠第五件花料）  
3. fest 继任 / 灾厄枢纽可见（须总控另批）  
4. sx20（仍禁，除非总控另开窗）  

---

## 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-14 00:15 | 总控批 A·M · 派怪物全线 |
| 2026-10-14 | 策划 · 初稿 STATUS=**待批 A** · 荐 M · TIP-CH 后连开 · 选题短征 R1 远程卫轻 Cast（玩法花样续）· 拟 D13314 · TIP-CI · ≠sx20 ≠抬表 ≠第五件花料 ≠复开 D423 · **未改题**（采纳 D13313§6① · 证据表见 RESEARCH） |

*D13314 · 短征 R1 远程卫轻 Cast · **待批 A·方案 M** · ≠关观察 ≠抬日表 ≠sx20。*
