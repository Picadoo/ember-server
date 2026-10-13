# 余烬 · 挂机庭「顺手分解」（产→拆可点 · ≠第五件花料 · ≠半行空转 · ≠抬日表）

STATUS=**已批 A · 方案 M · CLOSED live** · menu tip `b9e86a017` · PASS `8a3a398a8` · D13316 · TIP-CK 重开 · 2026-10-14 01:02 Asia/Shanghai
> **头注（号段）：** 本窗 **取代** 被否的半行题「短征选页三房可读可躲」`d00ed84bc`（TIP-CK / D13316 同号重开 · **勿**把半行题当 CLOSED）。  
> tip [`STATUS-ember-next-hard-debt-afk-courtyard-quick-dismantle-need-design-2026-10-14.md`](../status/STATUS-ember-next-hard-debt-afk-courtyard-quick-dismantle-need-design-2026-10-14.md) · RESEARCH [`RESEARCH-ember-afk-courtyard-quick-dismantle-2026-10-14.md`](RESEARCH-ember-afk-courtyard-quick-dismantle-2026-10-14.md) · backlog `B-afk-courtyard-quick-dismantle` · **≠关观察 ≠抬日表 ≠开 R ≠开 K3 ≠假开旧日常 ≠sx20 ≠菜单诚实/observe 空转 ≠第五件花料 ≠P格兼确认 ≠复开 D423 ≠教裸指令 ≠撤分格 ≠Cast粒子主交付**

> **一句话玩家价值：** 挂机庭里手持废件/甲，一点就能拆回料（预览→聊天确认）——产→拆不断跳工坊；**不**新开第五花料、**不**抬日产。

> **批 A = 采纳方案（荐 M）。批 A ≠ 施工 ≠ 关观察 ≠ 抬日表 ≠ 开 R ≠ 开 K3 ≠ 交 sx20。**

---

## 0. 证据表

| # | 来源 | 口径 |
|---|------|------|
| E1 | 总控 TIP-CK REJECT `d00ed84bc` | 半行无新动作=诚实空转；须真可点/可进机制；可复用 TIP-CK/D13316 |
| E2 | `ember_p1_afk.yml` | E/U/R/Q/P live；**无** D/d；布局 `'#P1 2 3 #'` 行尾空 `#` 可落 D |
| E3 | `ember_p1_forge.yml` D/d | `corerpg p1 dismantle` + `%held_dismantle_yield%` + 聊天确认 live |
| E4 | TIP-BX YieldBoost | `held_dismantle_yield` 已含日首+1 peek；零新键 |
| E5 | 甲 K0 只能拆 | 庭内四花对甲灰 → 拆是甲主可点事 |
| E6 | 硬禁 | 第五花料/P兼确认/半行空转/抬表/sx20 |
| E7 | **为何不是半行** | 主交付=**新格真 command**；Open 半行不可独批（见 §2 A 否决） |

**一句话问题：** 庭内能花不能拆——资源环拆侧断链。

**定位拍板：**

| 选项 | 结论 | 理由 |
|------|------|------|
| 再交选页可躲半行 | **否** | 刚 REJECT |
| P 格兼确认 / 第五花料 | **否** | 硬禁 |
| Cast 粒子打磨主交付 | **否** | 派单禁 |
| 只 Open「记得去工坊拆」 | **否** | 半行空转 |
| **庭内顺手分解真格（荐）** | **采纳 · 改题** | E2–E5；真动作；≠花料 |

---

## 1. 玩家感知目标（≤3）

1. **庭内可拆：** 打开挂机庭见「顺手分解」，点一下走真 `p1 dismantle` 预览，聊天确认成交。  
2. **预览诚实：** lore 挂 `%held_dismantle_yield%`（含日首+1 待用口径）；不可拆件不假成功。  
3. **零抬产：** 不改 dismantleYield / YieldBoost / afk.tiers / daily_kills；与工坊同命令同确认。

---

## 2. 方案表

| 方案 | 内容 | 评价 |
|------|------|------|
| **A · 只加 Open「记得去工坊拆」半行** | 口号 | **否决独批** — 无新动作=诚实空转（刚 REJECT 同族） |
| **M · 庭内 D 顺手分解 + 真 dismantle（荐）** | 新格 `command: corerpg p1 dismantle`；lore 挂 yield；可选 d 指引；Open 半行仅同批辅 · **零改**拆表/产量 | **荐** — 真可点；对位四花模式；≠第五花料 |
| **L · 本窗改交粒子/推荐花复读/P兼确认** | 换题 | **否决独批** — 弱或硬禁 |
| **W · 静默拆无确认 / 抬拆表 / sx20 / 半行独批** | — | **否决** — 硬禁或假平面 |

### 批 A 勾选（总控填）

- [x] **方案 M**（W1a D 格必做；W1b Open/去哪花半行同批辅；W1c 确认对齐工坊聊天确认；见 §3）
- [ ] 或勾 **方案 L**（改交粒子/推荐花复读）· **本窗不批 L**
- [x] 否决 A（只半行）/ W（静默拆/抬表/sx20）
- [x] 批注：总控批 A·M · D13316 · TIP-CK 重开 · TrMenu D→p1 dismantle（预览·聊天确认）· 零改拆表/产 · 不撤四花+P · 半行仅辅

---

## 3. 可落地（方案 M）

### 3.1 条件与键（钉死 · 只读）

| 项 | 口径 | 纪律 |
|----|------|------|
| 预览展示 | `%corerpg_p1_held_dismantle_yield%` | **零新 PAPI**；吃 live YieldBoost peek |
| 命令 | `corerpg p1 dismantle` → 聊天 `[确认分解]` | **与工坊同**；禁发明庭内第二拆轨 / 静默 confirm |
| 不可拆 | 首通/起步件等既有失败 tell | **不**假成功；可选灰态仅文案（无第二键亦可） |
| 甲 | 可拆（K0 主路径）；与工坊同 | 禁暗示庭内能强化甲 |

### 3.2 窗拆分

| 窗 | 做什么 | 不动 |
|----|--------|------|
| **W1a · 顺手分解格（必做 · TrMenu）** | `ember_p1_afk` 第二行 `'#P1 2 3 #'`→`'#P1 2 3D#'`（或等价空槽）：**D** 名 `§7顺手分解` · lore≤3：yield 行 +「只限普通掉落件；甲→白板零头」+「点预览·聊天确认」· `command: corerpg p1 dismantle` + close；可选 **d** 指引格对齐 forge（布局挤则 lore 并入 D） | 不撤 E/U/R/Q/P；不改层传送/战况 |
| **W1b · Open / 去哪花半行（必做同批 · 辅）** | Open +半行「本页也可顺手拆」；S 去哪花 +半行同指针 | **不可**无 W1a 独交半行；不盖四花 Open |
| **W1c · 确认诚实（必做）** | 仍走聊天确认；料/件不可拆 → 既有失败；**禁**菜单内一键静默拆 | 不改 YieldBoost/拆表 |
| **W1d · jar（默认不做）** | TrMenu-only；键已 live | 禁借号抬产 |
| **W1e · 经济/花料（不做）** | — | 禁第五花料/改价/抬日表 |

### 3.3 玩家路径

| 步 | 路径 | 说明 |
|----|------|------|
| 挂 | 庭内打怪产料 / 换手持 | 现网 |
| **拆（本债）** | 点 D → 预览 tell → 聊天确认 | W1a/W1c |
| 花 | 仍点 E/U/R/Q/P | 四花+推荐花保留 |
| 深 | 「去哪花」进工坊全功能 | D375 保留 |

**文案钉死示例（施工可微调色码，语义不得漂）：**

```
§7顺手分解
§e预览：§f%corerpg_p1_held_dismantle_yield%
§7只限普通掉落件；甲→白板胚零头
§e➥ §f点预览·聊天确认
```

### 3.4 与邻域边界

| 邻域 | 边界 |
|------|------|
| D13304–08/15 四花+推荐花 | **保留**；本债=拆侧；**≠**第五花料 |
| forge D/d · TIP-BX YieldBoost | **同命令同 PAPI**；不改表 |
| 被否半行 D13316 | **取代**；半行档 REJECT 归档 |
| P 兼确认 / fest / calamity / sx20 | **本窗不做** |

### 3.5 为何不是半行（验收必读）

| 半行空转（禁） | 本窗 M（过） |
|----------------|--------------|
| 只改 lore/Open 口号 | **新 Icon D + 真 `command: corerpg p1 dismantle`** |
| 玩家无新点击结果 | 点击→预览→聊天确认→成交拆件 |
| 对位 sure/pity/observe | 对位四花「顺手*」真花费格 |

---

## 4. 验收（施工号用 · 本号只定规格）

| ID | 项 |
|----|-----|
| V1 | 庭内出现 D「顺手分解」；左键触发 `p1 dismantle` 预览（与工坊同） |
| V2 | lore 含 `%held_dismantle_yield%`；日首待用口径与工坊一致（抽测） |
| V3 | 聊天确认后成交；不可拆件不假成功 |
| V4 | E/U/R/Q/P 仍在；无第五花料命令；无静默拆 |
| V5 | `git diff`：无改 dismantleYield/afk.tiers/daily_kills/伤表、无 sx20、无 P兼确认、无半行独档 |
| V6 | 玩家面无裸指令（仅菜单+既有聊天确认按钮） |

---

## 5. 禁区

- sx20 · 新地图硬开 · 抬挂机产量/日顶 · 抬伤乘/skill_mult · Affix densify  
- Pack6 · 天赋 · 灰印 · R · L13 · K3 · 假开旧日常 · 重开 calamity · 新 fest  
- 菜单诚实/sure·pity·soft 半行空转 · observe 空转 · **只半行无 D 格**  
- **第五件花料** · **P 格兼确认** · 静默拆 · 改 dismantleYield/抬 BONUS · 撤 E/U/R/Q/P  
- Cast 粒子纯打磨当主交付 · 复开 D423 · stage 脏 runtime · 教裸指令  

---

## 6. 后置（明文 · 本窗不做）

1. P 格兼确认（须另批）  
2. Guard/Elite/Archer/Boss Cast 粒子二次打磨  
3. 庭内互换 S 顺手（若另证真债）  
4. fest 继任 / 灾厄枢纽可见（须总控另批）  
5. sx20（仍禁，除非总控另开窗）  

---

## 变更记录

| 日 | 事 |
|----|-----|
| 2026-10-14 01:02 Asia/Shanghai | 策划勾毕旁注 · STATUS/RESEARCH/tip/backlog **已批 A · 方案 M · CLOSED** · 半行 REJECT 保持否决 · 顺手互换→TIP-CL |
| 2026-10-14 01:00 Asia/Shanghai | **CLOSED live** · menu `b9e86a017` · PASS `8a3a398a8` · 证据 tip-ck-afk-dismantle |
| 2026-10-14 00:53 | 总控批 A·M · 派物品 · 批 sha `80dc1c28e` |
| 2026-10-14 00:54 | 策划补记 · STATUS/RESEARCH/tip/backlog 标**已批 A·方案 M** · 旁注批 sha `80dc1c28e` · **勿勾 CLOSED** · 半行 REJECT `d00ed84bc` 保持否决 |
| 2026-10-14 | 策划 · 初稿 STATUS=**待批 A** · 荐 M · TIP-CK REJECT 后重开 · 选题挂机庭顺手分解 · 拟 D13316 · TIP-CK · **取代**半行否决题 `d00ed84bc` · ≠sx20 ≠抬表 ≠第五件花料 ≠P兼确认 ≠复开 D423 · **改题有证据**（真 command · 见 §3.5） |

*D13316 **CLOSED live** · menu `b9e86a017` · PASS `8a3a398a8` · 收口 `b8eaf7696` · 挂机庭顺手分解 · TIP-CK 重开 · **已批 A · 方案 M** · 批 sha `80dc1c28e` · 半行 REJECT `d00ed84bc` 保持否决 · ≠半行空转 ≠第五花料 ≠关观察 ≠抬日表 ≠sx20。*
