# 余烬 · 短征 R1 近战卫可读轻 Cast（短本玩法花样续 · ≠sx20 · ≠抬表）

STATUS=**CLOSED live · 批 A · 方案 M · D13311** · tip `1c90911ef` · PASS `9322cf4bc` · 总控勾毕 2026-10-13 23:22 Asia/Shanghai · 2026-10-13 23:10 Asia/Shanghai · tip [`STATUS-ember-next-hard-debt-short-r1-guard-cast-need-design-2026-10-13.md`](../status/STATUS-ember-next-hard-debt-short-r1-guard-cast-need-design-2026-10-13.md) · RESEARCH [`RESEARCH-ember-short-r1-guard-cast-readable-2026-10-13.md`](RESEARCH-ember-short-r1-guard-cast-readable-2026-10-13.md) · backlog `B-short-r1-guard-cast-readable` · **≠关观察 ≠抬日表 ≠开 R ≠开 K3 ≠假开旧日常 ≠sx20 ≠菜单诚实/observe 空转 ≠抬 Guard/Elite/Boss 基础伤表 ≠新地图 ≠重开 calamity ≠Pack6 ≠再叠挂机庭花料 ≠复开 D423 ≠教裸指令**

> **一句话玩家价值：** 短征第一房近战卫也会「蓄力→可躲」——三房主题演出齐；**不**开 sx20、**不**抬日表。

> **批 A = 采纳方案（荐 M）。批 A ≠ 施工 ≠ 关观察 ≠ 抬日表 ≠ 开 R ≠ 开 K3 ≠ 交 sx20。**

---

## 0. 证据表

| # | 来源 | 口径 |
|---|------|------|
| E1 | 总控 TIP-CE CLOSED · 连开 | 挂机庭/短征/冒险可感；禁 sx20/抬表/诚实空转/L13/K3/Pack6/灾厄重开 |
| E2 | sx01–19 R1 近战仅 SoftHit | MM `EmberSxNNGuard` / sx01 `Zombie` |
| E3 | EliteCast / BossCast 已 live | D13305 · DiffSkills |
| E4 | 挂机庭四件 / 今日推 / 冲一下已齐 | D13304–10 |
| E5 | 短征链停 sx19 · 禁 sx20 | backlog |

**一句话问题：** R1 开场房无可读招式，短本前段体感空。

**定位拍板：**

| 选项 | 结论 | 理由 |
|------|------|------|
| 再叠选页/诚实半行 | **否** | 空转硬禁 |
| sx20 / 新图 | **否** | 硬禁 |
| 挂机庭第五件花料 | **否** | 四件齐 |
| 周目标选页冲 / 冒险聚合 | **后置** | 非房内花样 |
| **R1 近战卫轻压可读 Cast（荐）** | **采纳** | E2–E3；梯队完整；MM-only 薄 |

---

## 1. 玩家感知目标（≤3）

1. **开场会蓄力：** 进 R1 数秒内见主题喊话+粒子蓄力，侧移可躲。  
2. **比精英更轻：** 伤害与频率明显低于本本 EliteCast（不是三个 Boss）。  
3. **旧本变鲜：** sx01–19 不用新本也能感到「第一房有花样」。

---

## 2. 方案表

| 方案 | 内容 | 评价 |
|------|------|------|
| **A · 只改选页「开场更狠」口号** | 文案 | **否决独批** — 无房内动作 |
| **M · sx01–19 R1 近战挂轻压主题 Cast（荐）** | 每本 `GuardCast` + 近战主怪 Skills 一行；**零改** HP/掉落/日帽/地图；**不**挂远程 | **荐** — 真花样；薄可施工 |
| **L · 仅 sx01–06 试点** | 同 M 缩范围 | 可批更薄；全线同形成本仍低 |
| **W · sx20 / 原伤贴 Elite·Boss / 远程同挂 / 抬伤表 / 诚实空转** | — | **否决** — 硬禁 / 刷屏 / 三 Boss 感 |

### 批 A 勾选（总控填）

- [x] **方案 M**（W1a Skills 轻压 Cast 必做；W1b 近战主怪挂接必做；W1c 全线 sx01–19 同批；见 §3）
- [ ] 或勾 **方案 L**（仅 sx01–06；其余后置）· **本窗不批 L**
- [x] 否决 A（只口号）/ W（sx20/原伤复读/远程同挂/抬表/诚实空转）
- [x] 批注：总控批 A·M 全线 sx01–19 · D13311 · TIP-CF · 派余烬-怪物 MM-only · timer140 · dmg≈0.30–0.35 · 仅近战 · 禁远程/抬表/sx20


---

## 3. 可落地（方案 M）

### 3.1 数值与可读钉死（梯队）

| 项 | Boss Cast | Elite Cast（D13305） | **Guard Cast（本窗）** | 纪律 |
|----|-----------|----------------------|------------------------|------|
| Timer | `~onTimer:70` | `~onTimer:100` | **`~onTimer:140`** | 最慢；禁同 100/70 |
| 结算伤害 | ≈0.85 | ≈0.50–0.55 | **≈0.30–0.35** | 最轻；禁 ≥Elite |
| 蓄力 delay | 30 | 30–35 | **30–35** | 禁无蓄力瞬伤 |
| 半径 | r5 | r4–r5 | **r3.5–r4** | 可躲 |
| 喊话 | 守吏主题 | 伍长/精英 | **哨卒/卫士蓄力——侧移** | 短文案 |
| CD | 8 | 8–10 | **10–12** | 与 Timer 协调 |
| 挂接对象 | Warden | Elite | **仅近战主怪**（sx01=`Zombie`；sx02–19=`Guard`） | **禁** Crossbow/Skeleton |

### 3.2 窗拆分

| 窗 | 做什么 | 不动 |
|----|--------|------|
| **W1a · GuardCast Skills（必做 · MM）** | `EmberDungeonDiffSkills.yml`（或并列）为 sx01–19 各增 `EmberSxNNGuardCast`：粒子主题对齐本本 Boss/Elite，伤害/Timer 按 §3.1 | 不改 Boss/Elite Cast 本体数值 |
| **W1b · 近战挂接（必做 · MM）** | sx01 `EmberSx01Zombie`、sx02–19 `EmberSxNNGuard` Skills 追加 `- skill{s=EmberSxNNGuardCast} @self ~onTimer:140` | 不改 SoftHit/掉落/mmxp；不改 Type/Equipment；**不**改 Crossbow/Skeleton |
| **W1c · 范围（荐全线）** | sx01–19 一次同形；若批 L 则只 01–06 | 不交 sx20；不新建地图/DP |
| **W1d · 选页半行（可选）** | `ember_p1_short` 说明格 +半行「开场卫可躲蓄力」 | **非主交付**；禁诚实半行族扩写 |
| **W1e · jar（默认不做）** | — | 纯 YAML；无 CoreRpg 改动 |

### 3.3 主题对照（施工索引）

| 本 | Boss / Elite 主题 | GuardCast 提示 |
|----|-------------------|----------------|
| sx01 | Sentry / 哨岗轻扫 | 哨卒轻扫 |
| sx02 | Beacon / 锈灯轻落点 | 栈卫轻落点 |
| sx03 | Frost / 霜雾轻楔 | 闸卫轻楔 |
| sx04 | Furnace / 井心轻喷 | 井卫轻焰 |
| sx05 | Wind / 风剪轻条 | 崖卫轻剪 |
| sx06 | Ring / 环扫轻弧 | 环卫轻弧 |
| sx07 | Tower / 塔冠轻坠 | 塔卫轻坠 |
| sx08 | Layer / 层间轻砸 | 层卫轻砸 |
| sx09 | Canal / 渠心轻溅 | 渠卫轻溅 |
| sx10 | Gate / 闸心轻压 | 闸卒轻压 |
| sx11 | Mirror / 镜面轻扫 | 镜卫轻扫 |
| sx12 | Pivot / 枢扫轻斩 | 枢卫轻斩 |
| sx13 | Balance / 衡扫轻斩 | 衡卫轻斩 |
| sx14 | Rift / 裂扫轻斩 | 裂卫轻斩 |
| sx15 | Spiral / 旋扫轻斩 | 旋卫轻斩 |
| sx16 | Shadow / 影扫轻斩 | 影卫轻斩 |
| sx17 | Tide / 潮扫轻斩 | 潮卫轻斩 |
| sx18 | Prism / 晶扫轻斩 | 晶卫轻斩 |
| sx19 | String / 弦扫轻斩 | 弦卫轻斩 |

粒子限 1.12.2 已用集（flame/lava/crit/cloud 等）；禁无效粒子名。

### 3.4 与邻域边界

| 邻域 | 边界 |
|------|------|
| D13305 EliteCast | **保留**；Guard 另技能，禁改 Elite/Boss 手感 |
| D13309 今日推 / D13310 冲一下 | **不抢**；本债=房内开场花样 |
| D13304–08 挂机庭花料 | **不叠**第五件 |
| fest / calamity | **本窗不做** |
| sx20 | **不做** |

---

## 4. 验收（施工号用 · 本号只定规格）

| ID | 项 |
|----|-----|
| V1 | 任选 ≥3 本（含 sx01 + 一中段 + sx19）：R1 近战战斗中出现蓄力喊话+粒子，delay 后结算 |
| V2 | 同本对比：Guard Cast 伤害与触发频率 **低于** Elite Cast **低于** Boss Cast |
| V3 | 同房 Crossbow/Skeleton **无** GuardCast；无双怪同喊刷屏 |
| V4 | `git diff`：无改 `daily_kills`/`afk.tiers`/UpgradeRules；无新增 sx20；无改基础 HP·Damage 字段 |
| V5 | SoftHit/掉落/mmxp/日帽/选页进本链路与施工前一致；玩家面无裸指令 |

---

## 5. 禁区

- sx20 · 新地图硬开 · 抬挂机产量/日顶 · 抬伤乘/skill_mult · Affix densify  
- Pack6 · 天赋 · 灰印 · R · L13 · K3 · 假开旧日常 · 重开 calamity · 新 fest  
- 菜单诚实/sure·pity·soft 半行空转 · observe 空转  
- Guard 直接复用 Elite/Boss Cast **同伤同 Timer** · 无蓄力瞬伤 · R1 远程同挂  
- 再叠挂机庭花料第五件 · 复开 D423 · stage 脏 runtime  

---

## 6. 后置（明文 · 本窗不做）

1. 短征周目标选页「可冲」格（赛季页已有入口）  
2. 冒险「今日三冲」聚合格  
3. R1 远程轻 Cast / GuardCast 粒子二次打磨  
4. fest 继任 / 灾厄枢纽可见（须总控另批）  
5. sx20（仍禁，除非总控另开窗）  

---

## 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-13 | 策划 · 初稿 STATUS=**待批 A** · 荐 M · TIP-CE 后连开 · 选题短征 R1 近战卫轻 Cast（玩法花样续）· 拟 D13311 · ≠sx20 ≠抬表 ≠远程同挂 ≠复开 D423 |

*拟 D13311 · 短征 R1 近战卫轻 Cast · 待批 A·方案 M · ≠关观察 ≠抬日表 ≠sx20。*
