# STATUS · 阶段 4.6 全链路验收勾选

- 时间：2026-09-27 19:54～20:07（Asia/Shanghai / CST）
- 执行：余烬-测试岗
- 依据：`docs/design-stage4-mainline-vol2.md` §7 + §6.5
- 前提：4.1～4.5 分项已 PASS；本轮全链路勾选（能快复验则复验；重战斗与既有 PASS 一致可 SKIP 并引用）
- 脚本：`/tmp/stage4-4.6-spot.js`、`/tmp/stage4-4.6-retest2.js`（日志 `/tmp/stage4-4.6-spot.log`、`/tmp/stage4-4.6-retest2.log`）
- 抽检账号：`S46_3629` / `S46r_7452` / `S46d_1775` / `S46e_8156` / `S46f_*`；OP `RpgBot`（uuid `148ec8b6-253f-36f4-bcd7-bf2395b8df80`，仅测时）
- **未修改玩法数值**、怪物/YAML 平衡、Paper/MM、afk_caps 数值

## 总评

**PASS（有条件）**

条件说明：

1. **重战斗项 SKIP 引用既有 PASS**：7.2.4 带满二层周本/团本 TTK&lt;15% 估测未本轮重跑；7.3 深渊冲层/TTK 与精英平衡引用 4.1/4.3/4.4f，本轮仅功能抽检。
2. **深渊撤离**：`STATUS-ember-abyss-9-12.md` 初测菜单撤离 FAIL，同文件「菜单撤离复测」已 **PASS**（白名单修复后）；本轮未再进本抽检撤离。
3. **天赋二层节点满解锁**：本轮抽检验证 Lv30=20 / Lv33→21 / Lv60=30、洗点；二层 `*_ember2` 等满树解锁 **引用** `STATUS-ember-talent-layer2.md`（4.2 PASS），本轮一层解锁+洗点 PASS。

其余 §7.1～7.3 可测项本轮抽检或静态核对均为 PASS；`afk_caps` 未漂；测后 `ops=[]`；CoreRpg **1.15.1**、Quest **10 chapters**。

## 分项表

| # | 项 | 结果 | 说明 |
|---|----|------|------|
| **7.1.1** | ch6→ch7 / `quest set 7 0` | **PASS** | 引用 4.5 + 本轮抽检：`quest set → 第二卷 · 烬原之下` |
| **7.1.2** | ch7～10 可推进（forge / abyss_floor 等） | **PASS** | 引用 4.5；抽检 `forge`→「刃认新主了。」；`abyss_floor 9`→`best=9`「第 9 层的蛮压…」 |
| **7.1.3** | 主线发奖 NI（稳定符/凝核/孔石）与设计一致 | **PASS** | quest.yml 章末与设计表一致；NI 定义均存在；`/ni give` 发包 + ch10 完成实测发「余烬凝核×1，余烬稳固符×1，灾厄外观碎片×1」 |
| **7.1.4** | 定额 XP 不占击杀日顶 | **PASS** | 代码 `grantFlatEmberXp` 无日顶；抽检击杀经验 0/100→0/100（主线/progress 堆级期间不变） |
| **7.1.5** | ch10+Lv60「第二卷完成」无死胡同 | **PASS** | 抽检：`[第二卷完成] 烬火未灭…` + `主线第二卷「烬火未灭」完成。`；`/corerpg quest`→`烬火未灭 · 完` / `第二卷已完成。` |
| **7.2.1** | Lv30≤20；Lv60 上限30 | **PASS** | 引用 4.2；抽检 Lv31→已获=20；Lv60→已获=30 硬顶=30 |
| **7.2.2** | Lv33 起每3级+1 | **PASS** | 引用 4.2；抽检 Lv34→已获=21（=20+⌊(34-30)/3⌋） |
| **7.2.3** | 新节点解锁/洗点 | **PASS** | 抽检一层解锁+`洗点完成（免费）`；二层满树解锁引用 4.2 |
| **7.2.4** | 带满二层 TTK&lt;15% | **SKIP** | 重战斗；未做估测。引用既有深渊/精英 TTK 报告作环境稳定旁证，**非**本条直接证据 |
| **7.3.1** | 深渊可达12 + 第12周首通稳定符≤1/周 | **PASS** | 引用 4.1 内容 PASS + 撤离复测 PASS；TTK 收口引用 4.3-close / 4.3c-rerun。本轮未再冲层 |
| **7.3.2** | 精英每周1次；无票/已打拒绝；通关箱 | **PASS** | 功能引用 4.4；本轮抽检拒绝短句：`缺少余烬精英票…`、`本周已通关精英试炼，下周再来`（无 `/dp`/`/dungeon` 泄漏） |
| **7.3.3** | 精英平衡 45～90s / 25～55% | **PASS** | **引用** `STATUS-ember-elite-weekly-test-4.4f.md`：**Boss TTK 70s / 通关前 HP 34%**（设计窗 45～90s / 25～55%；4.4f 窗为 45～75s 亦入） |
| **7.3.4** | 掉落不进 afk 日顶；世界名单无误加 | **PASS** | 引用 4.4；本轮确认 `afk_caps.worlds=[ember_afk, world]`，无 `ember_weekly`/精英图 |
| **回归** | afk_caps 未被本阶段改坏 | **PASS** | 配置未漂（enabled/worlds/over_chance/kill_coin/items 与 STATUS-afk-caps 一致）；`/corerpg afk` 显示四层共用日顶计数 |

## 引用清单

| 路径 | 结论 | 本轮用法 |
|------|------|----------|
| `STATUS-ember-quest-vol2-test-4.5.md` | **PASS**（ch7～10 / forge / abyss_floor / elite_weekly / XP4600） | 7.1 主引用 + 抽检 |
| `STATUS-ember-quest-vol2-4.5-close.md` | **PASS** / CoreRpg 1.15.1 | 关闭确认 |
| `STATUS-ember-talent-layer2.md` | **PASS**（earned 公式 / 二层节点 / 硬顶30） | 7.2 主引用；二层解锁 |
| `STATUS-ember-abyss-9-12.md` | 内容 **PASS**；撤离初 FAIL→同文件复测 **PASS** | 7.3.1 |
| `STATUS-ember-abyss-ttk-4.3c-rerun.md` | 第12 PASS；第10 方差说明 | 7.3 TTK 旁证 |
| `STATUS-ember-abyss-4.3-close.md` | **PASS / CLOSED**（6000/18） | 7.3 收口 |
| `STATUS-ember-elite-weekly-test-4.4.md` | 功能 CONDITIONAL PASS（拒绝/扣票/箱/NI） | 7.3.2 功能 |
| `STATUS-ember-elite-weekly-test-4.4f.md` | **PASS**（510s / Boss **70s** / HP **34%**） | 7.3.3 平衡 |
| `STATUS-afk-caps.md` | **PASS** | 回归基线 |
| `STATUS-afk-tier-t3-t4.md` | T3 FAIL / T4 PASS（阶段3，非本阶段改坏依据） | 回归旁证 |

引用报告数：**10**

## 本轮抽检证据（摘要）

### 主线
- `quest set S46_3629 7 0` → `第二卷 · 烬原之下 · 向 灰烛…`
- `quest event forge` → `灰烛：刃认新主了。` → 目标 Lv.40
- `quest event abyss_floor 9` → `best=9` / `第 9 层的蛮压，你顶住了。`
- 章末奖励 YAML：ch7 `mat_ember_stable_charm:1 + mat_ember_shard:20`；ch8 `gem_ember_steady:1 + mat_ember_core_fragment:5`；ch9 `core_ember_compact:1 + mat_ember_talent_reset:1`；ch10 `core_ember_compact:1 + mat_ember_stable_charm:1 + cosmetic_calamity_shard:1`；章 XP 900/1100/1200/1400=**4600**
- NI 定义存在：`mat_ember_stable_charm` / `core_ember_compact` / `gem_ember_steady` / `mat_ember_talent_reset` / `cosmetic_calamity_shard` 等
- `/ni give` 后背包计数 charm1/core1/gem1/reset1/shard20/frag5/cos1
- 击杀日顶：level UI `击杀经验 0/100` 在主线 XP / progress 堆级前后不变
- ch10 终：`[第二卷完成] 烬火未灭。日常、深渊与试炼仍在。` + `主线第二卷「烬火未灭」完成。` + 实发凝核/稳固符/外观碎片

### 天赋
- Lv31：`已获=20 硬顶=30`
- Lv34：`已获=21 硬顶=30`
- Lv60：`已获=30 硬顶=30`
- 洗点：`[天赋] 洗点完成（免费）`

### 精英拒绝
- 无票：`缺少余烬精英票（每周一发放 1 张，持有上限 1，进本即扣）`
- 已通关：`本周已通关精英试炼，下周再来`

### afk_caps
```
enabled: true
worlds: [ember_afk, world]
over_chance: 0.25
kill_coin: 150
items.mat_ember_shard: 150  （余同 STATUS-afk-caps）
```
`/corerpg afk`：四层共用「今日：碎片 0/150 · …」

## 环境与测后

| 项 | 值 |
|----|-----|
| CoreRpg | **1.15.1**（`Enabling CoreRpg v1.15.1`；jar `plugins/CoreRpg.jar`） |
| Quest | **10 chapters loaded** |
| 端口 | proxy **25565** / play **25567**（login 25566） |
| JAVA_HOME | `/workspace/minecraft/tools/jdk8u504-b01` |
| `server-runtime/ops.json` | **[]** |
| `login-runtime/ops.json` | **[]** |
| 游玩服 | 测后已短重启（Done **20:07:06** CST） |

## 不改数值声明

本轮仅验收与文档；**未改** quest/talent/深渊/精英/挂机/怪物 YAML 数值，未改 Paper/MM 平衡，未改 `afk_caps` 上限。

## §7.4 文档收尾（非本岗）

`HANDOFF.md` / `ember-master-plan.md` 阶段 4 收口属总控/文档岗；本报告不代替 §7.4。
