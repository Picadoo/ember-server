# 余烬 · 短征 R3 Boss 半血轻添头（短本玩法花样续 · ≠Cast粒子打磨 · ≠半行 · ≠sx20 · ≠抬表）

STATUS=**已批 A · 方案 M · D13318** · 总控 2026-10-14 01:17 Asia/Shanghai · TIP-CM · docs→施工
> tip [`STATUS-ember-next-hard-debt-short-r3-boss-aid-need-design-2026-10-14.md`](../status/STATUS-ember-next-hard-debt-short-r3-boss-aid-need-design-2026-10-14.md) · RESEARCH [`RESEARCH-ember-short-r3-boss-aid-readable-2026-10-14.md`](RESEARCH-ember-short-r3-boss-aid-readable-2026-10-14.md) · backlog `B-short-r3-boss-aid-readable` · **≠关观察 ≠抬日表 ≠开 R ≠开 K3 ≠假开旧日常 ≠sx20 ≠菜单诚实/observe 空转 ≠半行独批 ≠Cast粒子主交付 ≠第五件花料 ≠推荐花复读 ≠P格兼确认 ≠庭内再叠顺手 ≠复开 D423 ≠教裸指令 ≠撤分格 ≠灾厄重开 ≠Pack6**

> **一句话玩家价值：** 短征终厅 Boss 半血会喊「添头」并多出 1 只本本近战——先清再打首领；**不**开 sx20、**不**抬日表、**不**再叠 Cast 粒子。

> **批 A = 采纳方案（荐 M）。批 A ≠ 施工 ≠ 关观察 ≠ 抬日表 ≠ 开 R ≠ 开 K3 ≠ 交 sx20。**

---

## 0. 证据表

| # | 来源 | 口径 |
|---|------|------|
| E1 | 总控 TIP-CL CLOSED `95a0d5fe4` · 连开 TIP-CM | 首选短征日更花样；须真可点/房内可感；禁半行/Cast粒子主交付/庭内再叠 |
| E2 | Cast 波全线 live | D13305 Elite · D13311 Guard · D13314 Archer · BossCast |
| E3 | sx01–19 Warden = SoftHit + BossCast only | MM EmberSx* |
| E4 | `EmberRaidSummonAid` 范式 live | DiffSkills |
| E5 | 挂机庭环/导航链已齐 | D13304–17 |
| E6 | 半行 REJECT `d00ed84bc` 保持否决 | backlog |
| E7 | **为何不是半行** | 主交付=真 summon 新敌；见 §3.5 |

**一句话问题：** 短征 R3 终厅只有单 Cast 循环，无半血换挡——日更旧本终局可感薄。

**定位拍板：**

| 选项 | 结论 | 理由 |
|------|------|------|
| 再交选页半行 / 诚实口号 | **否** | REJECT 保持 |
| Cast 粒子打磨 / 再叠 Cast | **否** | 硬禁；已齐 |
| 庭内再叠顺手 | **否** | 派单禁；无新硬债 |
| 只 Open「Boss 会添头」 | **否** | 半行空转 |
| **R3 Boss 半血轻添头（荐）** | **采纳** | E2–E5；真召唤；Raid 范式；MM-only 薄 |

---

## 1. 玩家感知目标（≤3）

1. **半血换挡：** 打到约半血，见喊话「半血添头——先清！」并出现 1 只本本 R1 近战。  
2. **须先清：** 添头可平砍清掉；**不**改 BossCast 本身可读性。  
3. **一场一次：** 同一 Boss 战最多添头 1 次；**不**抬日帽/掉落表。

---

## 2. 方案表

| 方案 | 内容 | 评价 |
|------|------|------|
| **A · 只改选页/Open「Boss 会添头」半行** | 口号 | **否决独批** — 无房内动作=诚实空转 |
| **M · sx01–19 Warden 半血轻添头 + 真 summon（荐）** | 新 `BossAid` 技能 + Warden 挂接；召唤 1×本本 R1 近战；超大 CD 一场一次；**零改** Cast/HP/掉落/日帽 | **荐** — 真房内机制；≠粒子打磨主交付 |
| **L · 仅 sx01–06 试点；其余后置** | 减面 | 可批但弱于全线日更感；**本窗荐全线** |
| **W · 抬伤/多召唤/改日帽/sx20/半行独批/Cast打磨主交付** | — | **否决** — 硬禁 |

### 批 A 勾选（总控填）

- [x] **方案 M**（W1a BossAid Skills 必做；W1b Warden 挂接必做；W1c 全线 sx01–19 同批；见 §3）
- [ ] 或勾 **方案 L**（仅 sx01–06；其余后置）· **本窗默认不批 L**
- [x] 否决 A（只半行）/ W（抬表/sx20/打磨主交付）
- [x] 批注：总控批 A·M · D13318 · TIP-CM · MM BossAid×19 半血 summon 1×R1 · 零改 Cast/HP/掉落 · 半行/粒子打磨否决 · 荐 ~onDamaged；fallback timer160 须记 STATUS

---

## 3. 可落地（方案 M）

### 3.1 条件与键（钉死 · 只读）

| 项 | 口径 | 纪律 |
|----|------|------|
| 触发 | 荐：`~onDamaged` + Conditions `healthpercent{percent=<50}`（MM 4.11）；Cooldown **9999**（一场一次） | 禁每 tick 刷兵；禁无 CD |
| fallback | 若半血条件实机不稳：改 `~onTimer:160` + CD9999（≈8s 中段一次），文案仍「添头」 | **须**在 STATUS 注明实际挂钩；禁静默双挂 |
| 召唤 | `summon{mob=<R1近战>;amount=1;noise=3}` | **仅 1 只**；禁 ≥2；禁召唤 Elite/Boss |
| R1 近战键 | sx01 `EmberSx01Zombie` · sx02 `EmberSx02Guard` · sx03 `EmberSx03Patrol` · sx04–19 `EmberSxNNGuard` | 复用现网键；禁新 mob 族 |
| BossCast | **零改** timer70 / dmg0.85 / 粒子 | 添头≠打磨 Cast |

### 3.2 窗拆分

| 窗 | 做什么 | 不动 |
|----|--------|------|
| **W1a · BossAid Skills（必做 · MM）** | `EmberDungeonDiffSkills.yml`（或并列）为 sx01–19 各增 `EmberSxNNBossAid`：Conditions 半血 + CD9999 + message「半血添头——先清！」+ summon 1×R1 + 轻粒子/音效（辅） | 不改既有 Cast 技能体 |
| **W1b · Warden 挂接（必做 · MM）** | `EmberSxNNWarden` Skills 追加一行挂 BossAid；保留 SoftHit + BossCast | 不改 Type/Equipment/HP/掉落/mmxp |
| **W1c · 全线（必做同批）** | sx01–19 同批（非 L） | 不派 sx20 |
| **W1d · 选页半行（本窗不做）** | — | 半行独批否决；宣传后置另批 |

### 3.3 喊话示例（短 · 可感）

```
&6【烬门】&f半血添头——&7先清！
```
（各本主题括号名对齐既有 BossCast 前缀，如【锈灯】【霜雾】…）

### 3.4 与邻域边界

| 邻域 | 边界 |
|------|------|
| D13305/11/14 Cast 波 | **保留**；本债=**新机制类别（召唤）**，≠粒子打磨 |
| EmberRaidSummonAid | **范式参考**；短征召唤本本 R1，不跨用团本 Footman |
| 挂机庭环 / 导航链 | **不抢**；本窗短征房内 |
| 半行 REJECT | **保持否决** |

### 3.5 为何不是半行（验收必读）

| 半行空转（禁） | 本窗 M（过） |
|----------------|--------------|
| 只改 lore/Open 口号 | **新 MM 技能 + Warden 真挂接** |
| 玩家无新房内结果 | 半血→**场上多 1 敌**→须先清 |
| 对位 sure/pity/observe | 对位团本「添头」真召唤 |

---

## 4. 验收（施工号用 · 本号只定规格）

| ID | 项 |
|----|-----|
| V1 | sx01/07/13（或抽测 3 本）Warden 半血（或 fallback 中段）出现喊话 + 1 只本本 R1 近战 |
| V2 | 同一场 Boss 战添头 **≤1**（CD/条件诚实） |
| V3 | BossCast / Guard / Archer / Elite Cast 数值与挂接 **与施工前一致** |
| V4 | `git diff`：无改日帽/`afk.tiers`/伤乘表、无 sx20、无半行独档、无庭内菜单 |
| V5 | 玩家面无裸指令（仅房内遭遇） |

---

## 5. 禁区

- sx20 · 新地图硬开 · 抬挂机产量/日顶 · 抬伤乘/skill_mult · Affix densify  
- Pack6 · 天赋 · 灰印 · R · L13 · K3 · 假开旧日常 · 重开 calamity · 新 fest  
- 菜单诚实/sure·pity·soft 半行空转 · observe 空转 · **只半行无 summon**  
- **Cast 粒子纯打磨当主交付** · 再叠第四档 Cast 当主交付  
- **第五件花料** · **推荐花复读** · **P 格兼确认** · 庭内再叠顺手  
- 一次召唤≥2 · 召唤 Elite/Boss · 抬小兵掉落当主交付  
- 复开 D423 · stage 脏 runtime · 教裸指令  

---

## 6. 后置（明文 · 本窗不做）

1. 选页/Open「终厅半血添头」半行宣传（须另证且过半行硬禁另议）  
2. Elite 半血添头 / Boss 半血第二 Cast  
3. Cast 粒子二次打磨  
4. fest 继任 / 灾厄枢纽可见（须总控另批）  
5. sx20（仍禁，除非总控另开窗）  

---

## 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-14 01:17 Asia/Shanghai | 总控批 A·M · 派怪物 |
| 2026-10-14 01:16 Asia/Shanghai | 策划 · 初稿 STATUS=**待批 A** · 荐 M · TIP-CL CLOSED 后连开 · 选题短征 R3 Boss 半血轻添头 · 拟 D13318 · TIP-CM · ≠半行 ≠Cast粒子主交付 ≠庭内再叠 · **改题有证据**（Cast 齐后终厅无换挡；Raid SummonAid 范式；见 RESEARCH） |

*D13318 **已批 A · 方案 M** · TIP-CM · 短征 R3 半血轻添头 · 已派怪物 · 2026-10-14 01:17 Asia/Shanghai · ≠半行 ≠Cast粒子主交付 ≠sx20。*
