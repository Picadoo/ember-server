# STATUS · 非日常进本体力灰显抽样 A

**日期：** 2026-09-28 22:21 CST（Asia/Shanghai）  
**岗：** 余烬-测试岗执行器  
**依据：** 施工 tip `23a816e` · CoreRpg **1.15.23** · `docs/STATUS-ember-enter-stamina-gray.md` · `docs/design-ember-enter-stamina-gray.md`（批 A）  
**口径：** 菜单路径抽样；**勿长挂**；**禁改** cost / 扣费 / `over_chance*`  
**Verdict：** ✅ **PASS** · STATUS **未 push**（交总控代推）

---

## 一句话

深渊 `<30` 灰拒 + 人话 tell、`≥30` 可进；周本三态（有抵扣低体力不灰 / 无抵扣低体力灰 / 无抵扣够体力可进）均成立；团本·枢纽精英「无抵扣+不足灰」与「够体力可点」各一；cost 30/45/50/40 未动；`over_chance*=0.25/0.08` 未动；日常七线灰显未回归坏；禁双侧 PAPI；ops=[]。

---

## 总评

| 验收点 | 结果 |
|--------|------|
| 1. 深渊：体力&lt;30 → S 灰、点无 enter、人话 tell；≥30 → 可进 | **PASS** |
| 2. 周本三态：有抵扣+低体力不灰；无抵扣+低体力灰拒；无抵扣+够体力可进 | **PASS** |
| 3. 团本 / 枢纽精英各「无抵扣+不足灰」与「够体力可点」 | **PASS** |
| 4. cost 仍 30/45/50/40；`over_chance*` 未动；日常七线未回归坏 | **PASS** |
| 5. 禁双侧 PAPI；ops=[] | **PASS** |

---

## 环境

| 项 | 值 |
|----|-----|
| 工作区 | `/workspace/minecraft` |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| 口 | proxy 25565 / login 25566 / play 25567 |
| 施工 tip | `23a816e` fix: non-daily enter stamina gray (abyss literal + blocked_* PAPI) |
| CoreRpg | **1.15.23** Enabling @ **22:06:58 CST** |
| 账号 | 验收 `EsGyc0pqj`（非 OP · Lv.42）· 辅助 `EsOpc0pqj`（临时 OP，已 deop） |
| 探针 | `/tmp/enter-stamina-gray-A2.js` · JSON `/tmp/enter-stamina-gray-A.json`（未入 git） |
| 抵扣清零 | 离线 MySQL `REGEXP_REPLACE`（仅测号 `weeklyGrantCreditElite/Raid→0`，测后无关现网配置） |

---

## 各点证据

### 1 · 深渊

| 态 | 实测 | 判定 |
|----|------|------|
| stamina=10 | S=`开始下潜 · 体力不足` · `stained_glass_pane` · lore「需要 30」「0:00 回满」 | **PASS** |
| 点灰 | chat **仅** `体力不足，需 30 点体力；每日 0:00 回满。` · **无**「正在进入」/ 建队 | **PASS** |
| stamina=90 | S=`开始下潜` · `obsidian` | **PASS** |
| 点绿 | `[深渊] 正在进入……（体力 -30）` → 建队 → 深渊开启 | **PASS** |

### 2 · 周本三态

| 态 | 实测 | 判定 |
|----|------|------|
| 抵扣×1 · stamina=10 | S=`开始挑战` · `diamond_sword`（**不灰**） | **PASS** |
| 烧抵扣后 · stamina=10 | S=`开始挑战 · 体力不足` · gray pane · lore「本周免费已用完」 | **PASS** |
| 点灰 | `体力不足，需 45 点体力（本周免费已用完）；每日 0:00 回满。` · 无 enter | **PASS** |
| 无抵扣 · stamina=50 | S=`开始挑战` · diamond_sword → `[周本] 正在进入……（体力 -45）` | **PASS** |

### 3 · 枢纽精英 / 团本

| 门 | 无抵扣+不足灰 | 够体力可点 |
|----|---------------|------------|
| 精英（hub 键） | `精英试炼 · 体力不足` gray pane · tell 需 40 · 无 enter | `精英试炼` golden_sword → `正在进入……（体力 -40）` |
| 团本 | `开始协作 · 体力不足` gray pane · tell 需 50 · 无 enter | `开始协作` beacon · 点后走 enter（人数 1&lt;3 拒开本并退还，**非**灰拒体力） |

均 **PASS**。

### 4 · cost / over_chance / 日常七线

| 项 | 证据 | 判定 |
|----|------|------|
| cost | live `stamina show`：日常**30** · 周**45** · 精英**40** · 深渊**30** · 团**50**；Java `costs.put` 同值 | **PASS** |
| `over_chance` / `_2` | `plugins/CoreRpg/config.yml` 测前=测后 **0.25** / **0.08** | **PASS** |
| 日常七线 | stamina=10 开 `ember_daily`：七键均 `· 体力不足` + glass_pane（庭院/焦骨/地窖/潮蚀/断塔/霜晶/锈轨） | **PASS** |
| 静态灰态无 enter | 四门灰枝 **无** `command: corerpg enter/elite` | **PASS** |

### 5 · 禁双侧 PAPI · ops

| 项 | 证据 | 判定 |
|----|------|------|
| 条件 | 深渊 `stamina% < 30`；周/团/精英 `blocked_*% > 0`（右侧字面量） | **PASS** |
| ops | play=`[]` · login=`[]`；临时 OP `deop EsOpc0pqj` | **PASS** |

---

## UX

- 路径：`/ember` → 深渊 / 周常 / 团本 / 精英试炼 / 日常（菜单点选）  
- **未**教玩家手打 `/corerpg enter`；灰态 tell 为人话（需 N 点体力 · 0:00 回满）  
- OP 斜杠仅测号体力/等级/清场，不入玩家可见文案  

---

## 阻塞点

无。

---

## 回传摘要（总控）

1. **总评：** ✅ PASS  
2. tip 施工：`23a816e`（本测报 commit 另见短 hash · **未 push**）  
3. cost：**30/45/50/40** 确认；`over_chance=0.25` / `over_chance_2=0.08` 未动  
4. 报告：`docs/STATUS-ember-enter-stamina-gray-test.md`  
5. ops=[]  
6. 阻塞：无  
