# 设计稿 · 非日常进本 · 体力不足灰显对齐（UX）

> **未批准前不施工。** 本稿只定 TrMenu 进本键灰显（+ 可选薄 PAPI）；**禁**未批改体力 cost / 上限 / 刷新 / 扣费逻辑 / MM / DP 波次 / `over_chance*`。  
> 债源：日常七线体力灰显已 **PASS**（`ember_daily` · 测 `docs/status/STATUS-ember-daily-menu-mustfix-retest.md`）；周本 / 深渊 / 团本 / 枢纽精英进本键仍无灰显 → 体力不够仍可点进、吃失败 tell。  
> 对齐：`docs/design/design-ember-content-backlog.md`（硬债空 · 软债冷却 chat 已 1.15.21 PASS 勾销）· `docs/design/design-ember-afk-ux-tier2-menu.md` 未选表。  
> 排除本轮：B0.4/B2.5 二档数值、挂机菜单 UX、B1.3、使徒 TTK、kill-any live、日常第三拍双线、断塔防坠 A/B、霜晶/锈轨前压、墙钟≥2h、宣称封死通胀、挑刺玩家。

---

## 0. 稿件信息

| 字段 | 填写 |
|------|------|
| 稿件 | 非日常进本 · **体力不足灰显**对齐日常（UX · 可维护性） |
| 负责人 / 日期 | 余烬-策划执行 / 2026-09-28 21:59 Asia/Shanghai |
| 关联 | `ember_weekly` / `ember_abyss` / `ember_raid` / `ember_hub` 精英键 · 日常范式 `ember_daily` · PAPI `CoreRpgExpansion` |
| 状态 | **已批 A · 可施工**（总控 2026-09-28 22:01 Asia/Shanghai） |
| 关联 STATUS / 稿 | `docs/status/STATUS-ember-daily-menu-mustfix.md`（字面量条件教训）· `docs/status/STATUS-ember-daily-rail-flank-cooldown-chat-test.md`（冷却已 PASS）· backlog |

### 硬约束（本稿）

| 可动（**仅若批 A 且另开施工**） | 不可动（硬禁 · 含本稿 commit） |
|----------------------------------|--------------------------------|
| `plugins/TrMenu/menus/ember_weekly.yml` · `ember_abyss.yml` · `ember_raid.yml` 进本键 S | 体力 cost / max / 日重置 / `consumeForEnter` 扣费规则 |
| `plugins/TrMenu/menus/ember_hub.yml` 精英试炼键（直进本） | DP `monster.yml` / MM / 票 NI / 日常七线灰显（已 PASS 勿重开） |
| 可选薄 Java：`CoreRpgExpansion` 增 `stamina_blocked_weekly\|elite\|raid`（0/1）+ bump | `afk_caps` / `over_chance*`；Citizens；git push |
| 热更 `trmenu reload`；若动 PAPI 则短重启 jar | 教玩家手打 `/corerpg`；改等级门槛数值 |

---

## 1. 问题一句话

**日常选线键体力不够会灰显拒点；周本 / 深渊 / 团本 / 枢纽精英仍亮着「点击进本」，点了才失败——与已还清的日常灰显债不对齐，属可维护性 / 体验薄修。**

---

## 2. 现网事实（调研 · 本稿未改）

| 项 | 现网 / 证据 |
|----|-------------|
| 日常灰显 | `ember_daily` 七键：`condition: check papi %corerpg_stamina% < 30` → gray pane +「体力不足 · 每日 0:00 回满」+ **无** `corerpg enter`；`refresh: 20` · **PASS** |
| 条件教训 | TrMenu 3.12.5：**双侧 PAPI 比较不成立**；右侧须 **字面量**（`docs/status/STATUS-ember-daily-menu-mustfix.md`） |
| 周本 S | `ember_weekly.yml`：始终绿剑 + `command: corerpg enter weekly`；lore 有当前体力，**无** condition 灰显 |
| 深渊 S | `ember_abyss.yml`：同上，cost **30**、**无**周免费 |
| 团本 S | `ember_raid.yml`：同上，cost **50** + 本周首次免费 |
| 枢纽精英 | `ember_hub.yml` 键 `2`：直 `corerpg elite start`，cost **40** + 本周首次免费；**无**子菜单、无灰显 |
| 周免费 PAPI | 已有 `%corerpg_stamina_credit_weekly|elite|raid%`（剩余抵扣次数）；**无**「能否进本」合一布尔 |
| 进本冷却 chat | CoreRpg **1.15.21** 启发式 tell + 退还 **已 PASS**（测 `docs/status/STATUS-ember-daily-rail-flank-cooldown-chat-test.md`）→ backlog 应勾销，**非**本窗债 |

**解读：** 硬债空；冷却软债已还。残留是 **菜单一致性** 薄窗：专岗清晰（插件 TrMenu ± 薄 PAPI），乐趣杠杆中（少一次「点了才吃瘪」），零经济风险。

### 周免费为何不能「只抄 `< 45`」

| 玩家态 | 若灰显条件仅 `stamina < 45` | 正解 |
|--------|------------------------------|------|
| credit≥1 · stamina=0 | **误灰**（其实免费能进） | 保持可点 |
| credit=0 · stamina=20 | 应灰 | 灰 |
| credit=0 · stamina=50 | 可点 | 可点 |

→ 周本 / 团本 / 精英必须 **「无抵扣且体力不足」** 才灰；深渊无抵扣，可直抄日常字面量。

---

## 3. 方案 A / B

| 方案 | 做法摘要 | 利 | 弊 | 本轮 |
|------|----------|----|----|------|
| **A. 四门对齐灰显（推荐）** | **深渊**：TrMenu 字面量 `< 30` 灰显（零 Java）。**周本 / 团本 / 枢纽精英**：薄 PAPI `stamina_blocked_<kind>`（有抵扣→0；否则 stamina&lt;cost→1）+ TrMenu `check papi %…% > 0` 灰显；灰态无 enter、tell 人话、`refresh:20`；cost 字面量 45/50/40 与现网一致 | 与日常体感一致；避开双侧 PAPI；专岗清晰；改动面可控 | 有抵扣三门需 **一次** CoreRpg 小 bump | **推荐** |
| **B. 本轮继续挂 / 仅深渊另议** | backlog 明文「非日常灰显本轮不批」；或只做深渊试点、周本三门另挂 | 零 / 更小 diff | 周本精英团本「点了才失败」继续；下轮仍占窗 | 备选 |

### 方案 A · 施工意图（批准后 · 插件可微调同义）

**深渊 `ember_abyss` · S（示意）：**

```
icons:
  - condition: 'check papi %corerpg_stamina% < 30'
    priority: 2
    inherit: true
    display:
      material: gray stained glass pane
      name: '§8开始下潜 · 体力不足'
      lore: …需要 30 · 当前 … · 体力不足 · 每日 0:00 回满
    actions:
      all:
        - sound …
        - 'tell: §c体力不足，需 30 点体力；每日 0:00 回满。'
    # 无 corerpg enter
update: 20
refresh: 20
# 默认枝保留现网绿键 + enter
```

**周本 / 团本 / 精英 · 推荐 PAPI（薄 Java）：**

| 占位 | 语义（只读） |
|------|----------------|
| `stamina_blocked_weekly` | 无周本抵扣 **且** `stamina < cost(weekly)=45` → `1`，否则 `0` |
| `stamina_blocked_raid` | 同理 · cost **50** |
| `stamina_blocked_elite` | 同理 · cost **40** |

实现落点：`CoreRpgExpansion` 调已有 `StaminaService` / `PlayerData` **只读**；**不**改 `consumeForEnter`。TrMenu：`condition: 'check papi %corerpg_stamina_blocked_weekly% > 0'` → 灰显（字面量比较，避开双侧 PAPI）。

**灰态文案意图（人话 · 可润色）：**

- 无抵扣且不足：「体力不足 · 每日 0:00 回满」（周本/团本/精英可补半句「本周免费已用完」）
- **禁**写裸 NI id；**禁**教 `/corerpg enter`

**不施工：** 日常七线灰显；灾厄奔赴（cost 0）；hub 上周本/深渊/团本「打开子菜单」键（非直进本）；冷却 chat（已 PASS）；奖励预览空壳 tell（另债）。

---

## 4. 验收（≤5）

1. **批准前：** 本稿落盘；体力配置 / DP / MM / `over_chance*` **零 diff**（本 commit 仅文档）。  
2. **若批 A：** 深渊体力 &lt;30 时 S 为灰 pane，点之 **不** `enter`，tell 人话；≥30 仍一点进本。  
3. **若批 A：** 周本 / 团本 / 枢纽精英：有周免费抵扣时即使体力低仍可点进；**无**抵扣且体力&lt;对应 cost 时灰显拒进；cost 数值与现网 45/50/40/30 **不变**。  
4. **若批 A · 禁项：** 未改扣费/上限/刷新；玩家可见路径 **无** 新增斜杠教学；未动日常七线 / 挂机 / 第三拍。  
5. **若批 B：** backlog 明文「非日常体力灰显本轮不批」，不得把日常灰显 PASS 写成「全进本菜单已对齐」。

---

## 5. 禁项清单

- 禁未批改任何玩法 YAML / Java（本 commit 仅文档）  
- 禁改体力 cost / max / 日重置 / 周免费发放规则  
- 禁未批改 `over_chance*`；禁墙钟≥2h；禁宣称封死通胀  
- 禁重开：B0.4 数值、挂机菜单 UX、B1.3、使徒 TTK、kill-any、日常第三拍双线、断塔方案 B、霜锈前压  
- 禁双侧 PAPI 比较条件（已知 3.12.5 翻车）  
- 禁稿内派挑刺玩家；禁 Citizens；禁 git push；禁一图一服  

---

## 6. 站岗 / 动线（无新锚）

无新 NPC。动线不变：枢纽菜单 / 子菜单 → 点进本键 →（灰则 tell 停；绿则 enter）。

| UX 硬条 | 判定 |
|---------|------|
| 零手打指令 | **PASS（沿用）** · 仅灰显/tell；底层 `command:` 可留 |
| NI id | **N/A**（无动物品逻辑） |
| 功能锚可点 | **N/A**（无新锚；UI 可用沿用） |
| 占位债务 | **PASS**：债务名「非日常进本体力灰显」；还债 = 批 A 施工 STATUS（或批 B 明文挂） |

---

## 7. 建议专岗

| 岗 | 职责 |
|----|------|
| **插件**（主） | TrMenu 四门灰显；薄 PAPI `stamina_blocked_*` + bump（若走 A）；`trmenu reload` / 短重启 |
| **策划** | 本稿文案意图；批注润色 |
| **测试** | 深渊不足/够；周本「有抵扣+低体力」「无抵扣+低体力」「无抵扣+够体力」；团本/精英抽样；确认 cost **未动** |
| 物品 / 怪物 / 地图 | **不派** |

---

## 8. 为何选这扇窗（给总控）

| 候选 | 本轮 | 一句否因 |
|------|------|----------|
| **本窗 · 非日常进本体力灰显** | **选** | 硬债空；日常范式已 PASS；周四门仍点进失败；可维护性薄修；专岗清晰 |
| 进本冷却 chat 转发 | **勾销 · 不选** | 1.15.21 + 测报已 PASS；backlog 陈旧「不捕 DP」表述与现网启发式不符 → **同步勾销** |
| 周本/团本奖励预览空壳 tell | 未选 | 日常真分页已还；杠杆低于「点进失败」；可下轮 |
| 日常异拍 / 第三拍再扩 | 未选 | 霜锈刚双 PASS；排除项 |
| 断塔掉底厅 / 霜锈前压 | 排除 | 软观察；无口碑点名 |
| 重开 B0.4 数值 / 挂机 UX / B1.3 | 排除 | 已勾销 |
| 菜单去指令大扫 / B0.1 | 未选 | 排期已结 |

---

## 9. 推荐摘要（可转发）

**推荐方案 A（非日常进本键对齐日常体力灰显：深渊字面量 TrMenu；周本/团本/枢纽精英薄 PAPI `stamina_blocked_*` + 灰显；cost 与扣费逻辑不动；未批不施工）。**  
一句话理由：日常已经「不够就灰」，其它进本门还在让人点了才吃瘪——薄修菜单一致性即可，别再开内容窗。

**建议专岗：插件（TrMenu ± 薄 PAPI）主 · 测试抽样 · 策划润色；物品/怪物/地图不派。**

**未批准前不施工。**


## 总控批注

**批准方案 A**（2026-09-28 22:01 Asia/Shanghai · 余烬-总控）

- 深渊：TrMenu 字面量 `stamina < 30` 灰显（零 Java 亦可）。
- 周本 / 团本 / 枢纽精英：薄 PAPI `stamina_blocked_*`（有周免费抵扣不灰；否则 stamina<cost 灰）+ TrMenu 字面量比较；**禁双侧 PAPI**。
- **硬禁**改 cost / max / 日重置 / `consumeForEnter` / `over_chance*` / 日常七线灰显。
- 灰态无 enter、人话 tell、`refresh:20`；禁斜杠教学、裸 NI id。
- 施工：插件 → STATUS 未 push → 总控代推 → 测岗抽样。
