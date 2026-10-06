# p1sim — P1 离线节奏模拟器

Python 3 标准库，不联网、不连服务器。用仓库里的**真实配置**模拟一个新角色从 Q01 推到 Q07，代替长时间的机器人试玩来估平衡和节奏。

## 读哪些文件（没有手抄的平衡数）

| 内容 | 来源 |
|---|---|
| 七图房间编排（A/B）、怪的生命 / 攻击 / 间隔、首领技能与追加段、增援、额外事件位置、首通奖励、阶级、体力费用 | `plugins/CoreRpg/ember-v1-runs.yml`（和 `CoreRpg/src/main/resources/` 那份比对，不同会警告） |
| 怪物实际生成生命 | `plugins/MythicMobs/Mobs/EmberP1Main.yml`（和 runs 不一致会警告并以 MM 为准） |
| A / h / D / q / f / e、暴击、防御、炽愈生命倍率、烬斩冷却、回复药 20% / 15 秒、商店价、起步药、死亡退药 | `plugins/CoreRpg/ember-v1.yml` |
| 等级攻击 / 生命、等级上下限、烬斩倍率 | `EmberTables.java` DEFAULTS |
| 强化成功率 / 保底 / 碎片 / 核心 / 币，升阶配方 | `EmberUpgradeRules.java` |
| 三套觉醒系数、触发间隔、内置冷却 | `EmberSetRules.java` |
| 基础结算、额外事件权重与奖励、成色 / 精工权重、目标族权重、印记兑换 | `EmberRunRules.java`（权重 / 公式仍读 Java；**数量**见下一行） |
| **结算 / 徽记 / 兑换数量（E3 · D225）** | `plugins/CoreRpg/ember-v1-economy.yml`（与 `CoreRpg/src/main/resources/` 比对；与线上 `EmberEconomy.amount()` 同真源；S01–S03 / C07 / S07–S08 / S17–S18 / C12–C13 / S22） |
| **图录阶段币 / 起步包（D243）** | `docs/design/ember-source-map.yml`（带 `sim:` 的 S33 `codex_stage` / S35 `starter_kit`）→ 数量仍从 `ember-v1-economy.yml` 取，与 `EmberCodex.java` / `ember-v1.yml` 双重断言（`sourcemap.py`） |
| 每天体力 | `plugins/CoreRpg/cash.yml` `stamina.base_max` |
| 余烬经验曲线 | `plugins/CoreRpg/progress.yml` `ember_xp.curve` |

## 模型（简化，故意少参数）

- **每局**：r1 → r2 → r3（每房 A/B 各 50%）→ 首领；按 §9.3 抽额外事件（宝藏怪 / 奖励精英要打，箱子直接给）。房间之间不回血（§19.4）。
- **战斗**（每房 Monte Carlo、事件驱动）：玩家每 `swing/uptime` 秒一次满蓄力普攻（B，10% ×1.5），烬斩 1.5B 每 8 秒打 `skill_hits` 个；烬爆 / 焚烬 / 炽愈按 `EmberSetRules`。怪各按自己的间隔出手，同时贴身的近战最多 `engage` 只；每下 `atk × M`，以概率 `dodge` 躲开（首领技能和术者直线是预警招，躲避 +0.25）。生命 < 67.5% 且冷却好了就喝药。首领追加段、`below` 条件、Q03 增援都照配置。
- **养成策略**（=试玩机器人的规则）：每局前用自己的币把药补到 5 瓶；通关领结算，首通拿首通包；新件只在整套更强（DPS × EHP）时换上，其余分解成胚料；8 印记兑换目标族较弱部位；能升阶就先升阶（只差币就攒钱），再把强化较低的一件强化到留 100 币为止；每天 90 体力 = 3 局；每天第一次死亡退回本局用掉的药（D32）。
- **路线**：`stepdown`（第三次试玩：前沿失败就往下一图刷，逐级下探；下面通关就回前沿）或 `alternate`（第二次试玩：失败 → 上一图刷一局 → 再试）。
- **技能参数** `dodge`：0.3 / 0.5 / 0.7 代表笨拙 / 一般 / 熟练。校准结果：两次自然试玩的机器人都约等于 **dodge 0.40–0.45**。

不模拟：走位路径、拉怪、地形卡怪、多人、强化互换（`--swap` 可开）、精工 / 成色养成。

## 用法

```bash
cd tools/p1sim
python3 selfcheck.py                         # 公式对书 §7.4 / §6.2 / §9.3 的算例、解析器、确定性、单调性
python3 p1sim.py                             # 当前配置，dodge 0.3/0.5/0.7，每档 60 名玩家，上限 60 天
python3 p1sim.py --dodge 0.45 --players 200  # 单一档
python3 p1sim.py --ref                       # 每图在书 §3.2 参考投入下的通关率
python3 p1sim.py --calibrate                 # 用 v2（改数前）和 v3（1.21.0）的实测首通局数拟合 dodge
python3 p1sim.py --profile v2 --route alternate   # 重放 D31 之前的数
```

改了平衡数（runs yml / MM / ember-v1.yml / Java 常量）后重跑即可；`--profile` 里只放历史对照用的覆盖值。
输出是中位数 / P90（没到的玩家记作无穷大），「首通时装备」是众数，B / H / Lv 是首通那一局的中位数。

## 规则快照、可复现性与构筑分析（2026-10-04，审查 §04–§06）

- **规则快照**（M06）：所有入口通过 `rules.py` 读同一份规范快照（runs / growth / festival 的 plugins/ 与 src/ 两份必须一致，否则报错；Java 常量表），第一行打印 `# rules sha256 …`。`python3 rules.py --export f.json` 导出；`P1SIM_RULES=f.json` 固定快照，`P1SIM_RULES_EXPECT=<hash>` 校验。快照存原文、加载时重新解析。
- **随机流**（M01 / M02）：模拟代码不许用模块级 `random.*`（selfcheck 第 7 节）；每局从调用方 rng 取 4 次，派生 布局 / 命中 / 暴击 / 刷怪 四条流；团本阵容 `p1party.rosters(seed, n, trials, pool)` 预先固定（结果带 `lineup`）。
- **焚烬**（M03）：`burnbook.py` 镜像 `EmberBurnBook.java`；`burncheck.py` + `javacheck/BurnCheck.java` 用仓库 JDK8 编译真实 Java 源逐跳对拍。
- `simfix_check.py` / `simfix_report.py`：修正前后对照 → `out-simfix-m01-m06.md`（`SIMFIX_DIR` 默认 /tmp/bd）。

```bash
P1SIM_RULES=f.json python3 builddiv.py m04 --n 3000         # 243 结构穷举 → out-build-diversity-m04.md
P1SIM_RULES=f.json python3 builddiv.py b01                  # 拆分补丁对照 → out-build-diversity-b01-split.md
P1SIM_RULES=f.json NPROC=6 python3 builddiv.py propose [--ids P4b,P5b] [--noise]   # 提案校准（轮次合并到 /tmp/bd/prop.pkl）→ out-build-diversity-proposals.md
P1SIM_RULES=f.json NPROC=6 python3 builddiv.py raid --n 1500 # 团本 1 号位换构筑 → out-build-diversity-raid.md
P1SIM_RULES=f.json python3 raidcomp.py out.json --trials 1500; python3 raidcomp.py --report out.json > out-build-diversity-raidcomp.md   # 焚烬人数 × 7 池聚类区间
python3 growthrun.py p2econ '<build json>' --players 300 --weeks 12 --abyss --raid --goals --every-week --dodge 0.5 [--seed-offset 100000]   # 上限效果检查
python3 growthrun.py p2econ-real '{"talents":[…],"affixes":{…},"policy":"gear"}' …   # 真实成本成长（realcost.py）
python3 growthrun.py weeks out1.md out2.md …                 # W30（候选策略中达到 30% 双极品拥有率的最早插值周）+ P50 / P90 / 第 12 周未拥有
B03_DIR=… python3 b03report.py                               # → out-build-diversity-b03-b04.md
```

- `skillkit.py`（D210）：技能组 S0——`p1sim.py` 的可选 `kit_*` 键（守招 / 副招 / 身法变体 / 共享充能，无键时逐位一致），42 格 × 3 套对照基线 + 择优行 → `out-skillkit-d210-r1..r5.md`；`SK_ROUND=2..5 NPROC=7 python3 skillkit.py run 1500 /tmp/sk/rN.pkl`，`SK_BEST=1 python3 skillkit.py report …`。

- `insignia.py`（D222）：首领徽记分图账户（S07/S08/S09/S17/S18/S23 → C12 烙印 / C13 调律），**opt-in**，不开时所有输出逐位不变；花费策略见模块说明（假设）。`python3 p2econ.py --insignia [--ins-pledge 2] …` 在周表后加徽记表；`python3 insignia.py report --signin [--pledge 2]` → `out-insignia-d222-w30*.md`（3 档躲避 × 方案，Q07 后 30 周：徽记结余 / 烙印次数 / 调律次数）。
- `insignia.py` R1 what-if（D226）：`--whatif A|B|C`（A=echo→最高已通图；B=残响厅 q01–q07 共用周帽；C=有帽低→高兑换，默认 5×2/周仅 sim）。报告 `out-insignia-r1-*.md` / `out-insignia-r1-compare.md`；**推荐 B**（设计，未改 live）。

- **economy yml（E3 · D225）**：`rules.py` 把 `ember-v1-economy.yml` 纳入规范快照（plugins/ ↔ src 必须一致，且 `balance_version` = runs）。`p1config` 的 `base` / `treasure_coin` / `elite_*` / `marks_per`、`insignia.py` 的 S07/S08/S17/S18/C12/C13、`afk.py` 的 `daily_kills` 都经 `rules.amount`。与 Java 金样不一致 → `RuleError`（拒跑）。改数量只改 yml（并 bump bv + 对齐 Java golden）后重跑即可；yml 对齐现网金样时**数值结果**与改前一致（rules sha256 戳会变，因为快照多了 economy 文件）。
- **source map（D243 · ARCH S4-2）**：`rules.py` 把 `docs/design/ember-source-map.yml` 纳入规范快照（`validate()` 要求每个带 `sim:` 的来源在 economy yml 里有块）。`sourcemap.py` 读其中的 S33 图录阶段币（`codex_stage`：阶段 / 币数从 `S33.at<n>.coin` 取，并与 `EmberCodex.STAGE_AT` / `STAGE_COIN` / `FAMILIES` 双重断言）和 S35 起步包（`starter_kit`：药数 = `S35.potions` = `ember-v1.yml starter.heal_potions`）。`p1sim.py` 的 `codex_see()` 在 T0 起步件和每次拿到新件时登记图录种类并发阶段币（不抽随机数）。`P1SIM_NO_CODEX=1` 关掉 → 输出与 D242 逐位相同（只有 rules 戳不同）。`python3 sourcemap.py show` 打印读到的数；`python3 sourcemap.py dyn --players 800` = 21 格 A/B（关 / 开图录币，±2pp）。
