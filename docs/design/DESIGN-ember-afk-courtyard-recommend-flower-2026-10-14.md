# 余烬 · 挂机庭「顺手推荐花」聚合（四花智路由 · ≠第五件花料 · ≠抬日表）

STATUS=**已批 A · 方案 M · CLOSED live · D13315** · PASS `843dfee65` · jar `1.65.811-d13315.local` · menu `f85daf617` · 总控 2026-10-14 00:43 · 策划勾毕 00:45 Asia/Shanghai

> **一句话玩家价值：** 打开挂机庭一眼看见「现在该顺手花哪条」——一点进仓够的强化/升阶/精工/成色预览；**不**新开第五花料、**不**抬日产。

> **批 A = 采纳方案（荐 M）。批 A ≠ 施工 ≠ 关观察 ≠ 抬日表 ≠ 开 R ≠ 开 K3 ≠ 交 sx20。**

---

## 0. 证据表

| # | 来源 | 口径 |
|---|------|------|
| E1 | 总控 TIP-CI CLOSED · 连开 | 拟 D13315 · TIP-CJ；优先挂机庭资源环新乐趣；合法接力可看 D13314§6②；禁 sx20/抬表/诚实空转/L13/K3/Pack6/灾厄/第五件花料 |
| E2 | D13314§6② | 明文后置「顺手推荐花」聚合（四花智路由；≠第五件花料） |
| E3 | `ember_p1_afk.yml` | E/e·U/u·R/r·Q/q 四花八格 live；Open 四条「也可本页点」；**无**推荐聚合键 |
| E4 | EmberRunPapi | `recipe_gap_enhance1` 数字；upgrade/refine/quality gap 为拼接半行；`held_*_lack` 空=够 |
| E5 | D13313 今日三冲 | 薄 jar + 一格聚合已验证可感 |
| E6 | 硬禁 | 第五件花料 / 抬产 / 撤分格 / 诚实空转 |

**一句话问题：** 四花分格齐了，缺「仓够该点哪」的一键决策。

**定位拍板：**

| 选项 | 结论 | 理由 |
|------|------|------|
| 再叠 Open「记得点四花」 | **否** | 空转硬禁 |
| 第五件花料 / 新价 | **否** | 硬禁 |
| 只粒子/Cast 打磨 | **否** | 非本债；§6② 已点名推荐花 |
| 改题短征再花样 | **否** | ArcherCast 刚齐；弱于四花决策债 |
| **四花智路由聚合（荐）** | **采纳** | E2–E5；合法接力；≠第五件 |

---

## 1. 玩家感知目标（≤3）

1. **一眼推荐：** 打开挂机庭看见「顺手推荐花」亮态，名/lore 写清此刻推哪条（强化/升阶/精工/成色）。  
2. **一点进预览：** 点击走**既有**预览命令；确认仍点原确认格——两步诚实不静默扣。  
3. **不够诚实：** 四花都仓不够（或手持不可花）时灰态写清，点一下进工坊；**不**假成功。

---

## 2. 方案表

| 方案 | 内容 | 评价 |
|------|------|------|
| **A · 只加 Open「四花都能点」口号** | 文案 | **否决独批** — 无新决策动作 |
| **M · 薄 jar 智路由 + 庭内 P 聚合格（荐）** | `afk_rec_flower*` + P 格点进既有预览；**不撤**分格；**零改**产量/价 | **荐** — 真聚合；对位三冲 |
| **L · 纯 TrMenu 条件 cascade 无 jar** | 仅 enhance 数字闸可靠；其余 gap 非数字 | 可批更薄但成色/升阶「仓够」易飘；不荐独批 |
| **W · 第五花料命令 / 一键 confirm 静默扣 / 撤 E–Q / sx20 / 抬表 / 诚实空转** | — | **否决** — 硬禁 |

### 批 A 勾选（总控填）

- [x] **方案 M**（W1a jar 必做；W1b TrMenu P 必做；W1c Open/去哪花半行同批；见 §3）
- [ ] 或勾 **方案 L**（纯 TrMenu；仅 enhance 优先可靠）· **本窗不批 L**
- [x] 否决 A（只口号）/ W（第五件/静默扣/撤分格/sx20/抬表/空转）
- [x] 批注：总控批 A·M · D13315 · TIP-CJ · 先插件 W1a afk_rec_flower*/afk-rec-flower（_ok 单键；预览非 confirm；优先 enhance→upgrade→refine→quality）再物品 P · 零第五花料/零抬产 · 不撤 E/U/R/Q

---

## 3. 可落地（方案 M）

### 3.1 路由钉死（优先序 · 只读）

| 序 | kind | 仓够口径（jar 内） | 预览命令（既有） | 确认格（既有） |
|----|------|-------------------|------------------|----------------|
| 1 | `enhance` | `recipe_gap_enhance1==0` 且手持可强化 | `corerpg p1 enhance` | `e` |
| 2 | `upgrade` | 非护甲 · 升阶闸过 · `lackingFor(upgrade)` 空 | `corerpg p1 upgrade` | `u` |
| 3 | `refine` | 非护甲 · `lackingFor(refine)` 空 | `corerpg p1 refine craft` | `r` |
| 4 | `quality` | 非护甲 · `lackingFor(quality)` 空 | `corerpg p1 refine quality` | `q` |
| — | `empty` | 以上皆否 | — | 灰→工坊 |

护甲手持：跳过 2–4；若强化也不可 → `empty`。  
**禁**新 Cost / 新 UpgradeRules / 静默 `confirm`。

### 3.2 PAPI / enter（W1a · jar）

| 键 | 口径 |
|----|------|
| `%corerpg_p1_afk_rec_flower%` | 短行总览（如 `§a推荐·强化+1` / `§8暂无仓够花`） |
| `%corerpg_p1_afk_rec_flower_name%` | 显示名 |
| `%corerpg_p1_afk_rec_flower_kind%` | `enhance`/`upgrade`/`refine`/`quality`/`empty` |
| `%corerpg_p1_afk_rec_flower_ok%` | `1`/`0` |
| enter `afk-rec-flower` | `_ok=1` → 派发上表预览命令；`_ok=0` → tell 诚实 / 可由菜单灰态进工坊 |

### 3.3 窗拆分

| 窗 | 做什么 | 不动 |
|----|--------|------|
| **W1a · jar 智路由（必做 · 插件）** | 上表 PAPI + enter；单测优先序与护甲跳过 | 不改产量/tiers/UpgradeRules；不改四花原命令语义 |
| **W1b · 庭内 P 格（必做 · TrMenu）** | Layout 空位落 **P**（荐改第二行为 `#P1 2 3 #`；施工可微调空槽）；亮：`§a顺手推荐花` + lore 挂 name/kind +「点预览·再回确认格」+ `enter afk-rec-flower`；灰：`§7顺手推荐 · 暂无` → `menu: ember_p1_forge`；`update:20` | **不撤** E/e/U/u/R/r/Q/q；不改层格/F/H/I/S |
| **W1c · Open / 去哪花半行（必做同批）** | Open +半行「仓够推荐见本页 P」；去哪花 lore +半行「也可看推荐花」 | 不重写 vault 主块；不叠诚实半行族 |
| **W1d · 合并确认到 P（本窗不做）** | — | 后置；本窗确认仍原格，保两步诚实 |
| **W1e · 怪物/地图（不做）** | — | — |

### 3.4 与邻域边界

| 邻域 | 边界 |
|------|------|
| D13304–08 四花 | **保留分格**；本窗只加路由入口 |
| D13313 今日三冲 | 同形聚合；域不同（冒险 vs 挂机庭） |
| D375/D404–415 展示链 | **不**再叠仓差/remain 主交付 |
| fest / calamity / sx20 | **本窗不做** |

---

## 4. 验收（施工号用 · 本号只定规格）

| ID | 项 |
|----|-----|
| V1 | 仓够强化时 P 亮且 kind=enhance；点进 enhance 预览 tell（非 confirm） |
| V2 | 强化不够、升阶够时 kind=upgrade（非护甲）；精工/成色同理按优先序 |
| V3 | 四花皆不够或手持不可花 → `_ok=0` 灰态；点进工坊；无假成功 |
| V4 | E/U/R/Q 及确认格仍在且原命令可用（未撤分格） |
| V5 | `git diff`：无改 `daily_kills`/`afk.tiers`/UpgradeRules 价表；无第五花料命令；无 sx20 |
| V6 | 玩家面无裸指令（仅菜单/enter） |

---

## 5. 禁区

- sx20 · 新地图硬开 · 抬挂机产量/日顶 · 抬伤乘/skill_mult · Affix densify  
- Pack6 · 天赋 · 灰印 · R · L13 · K3 · 假开旧日常 · 重开 calamity · 新 fest  
- 菜单诚实/sure·pity·soft 半行空转 · observe 空转  
- **第五件花料命令** · 新价轨 · 一键静默 confirm · **撤** E/U/R/Q  
- 复开 D423 · stage 脏 runtime · 教裸指令  

---

## 6. 后置（明文 · 本窗不做）

1. P 格兼确认（两步并一，须另批防误花）  
2. GuardCast / EliteCast / ArcherCast 粒子二次打磨  
3. fest 继任 / 灾厄枢纽可见（须总控另批）  
4. sx20（仍禁，除非总控另开窗）  

---

## 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-14 00:28 | 总控批 A·M · 派插件 W1a |
| 2026-10-14 00:43 | 总控 · CLOSED live · PASS `843dfee65` · menu `f85daf617` |
| 2026-10-14 00:45 | 策划旁注勾毕 · tip/STATUS/RESEARCH/backlog 补关 · **已批 A · 方案 M · CLOSED** · 短征选页三房可读可躲→TIP-CK / 拟 D13316 |
| 2026-10-14 | 策划 · 初稿 STATUS=**待批 A** · 荐 M · TIP-CI 后连开 · 选题挂机庭顺手推荐花聚合 · 拟 D13315 · TIP-CJ · ≠sx20 ≠抬表 ≠第五件花料 ≠复开 D423 · **未改题**（采纳 D13314§6② · 证据表见 RESEARCH） |

*D13315 CLOSED live · 挂机庭顺手推荐花 · PASS `843dfee65` · ≠关观察 ≠抬日表 ≠sx20 ≠第五件花料。*
