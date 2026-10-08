# 余烬 · 六槽护甲 T1 离线独立验收（余烬-测试 · 2026-10-08）

- 派单：总控 D318「T1 过线」签字依据。要求独立复跑，不采信插件岗自测结论。
- 验收基准：**origin/main `163ca7f0`**（总控 09:42 改基准）。先按旧 tip `21327217` 跑过一轮；改基准后，变化文件相关的项在 `163ca7f0` 上重跑。下表「SHA」列写明每项在哪个提交上跑。
- 基线：`f2b74dec`。环境：`git worktree`，目录为 `/workspace/tmp/d318-wt-{tip,head,base}`（不在主工作区构建）；JDK 8u504，Maven 3.9.16，`nice -n 19`，surefire `-Xmx768m -XX:+UseSerialGC`，单进程，python3 3.13.5。
- 不部署、不重启任何服、不动 bv、不改配置或数值、未改插件代码。QA 自写的测试只临时放进 worktree，没有入库。源码存档在盒子上的 `/workspace/tmp/d318qa-src/`。
- **白板经济模型未覆盖**（规格 §5.2 要求注明）。本次只验 Java 逻辑与公式对拍。

## 总评（复测 @d5128ea5 后更新 · 09:54）

**原阻塞已修复，T1-4 / T1-5 在 `d5128ea5` 上 PASS。** 上次的 5 个坏场景和 fuzz 中坏的 1 轮都已变绿；全量测试 642 个，2 个失败，与基线自带的一致；T1-1 golden 仍绿；原 `savePlayerFile` 的方法体和其余调用点都没变（见「复测 @d5128ea5」一节）。
**插件岗报的残余风险已离线复现，后果是原甲位物品丢失**（扔 1 件的变体还会出现同 uid 复制件）。触发需要两个条件叠加：在「已存档、未置标记」的窄窗口内进程死亡或存档失败，**并且**玩家在下一次枢纽续上之前把迁移甲从身上拿走。概率低，但迁移是对全服一次性、不可逆的操作，修法小而且能离线测。**测试岗建议先在 T1 内修完再签「T1 过线」**；如果总控接受这个风险，也可以签 T1，但必须把这项修复列为 T2 迁移演练的硬前置条件。由总控定夺。
首轮（@163ca7f0）结论保留在下表和各节中作为记录。

## PASS / FAIL / SKIP 表

| 项 | 结论 | SHA | 关键证据 / 数值 |
|----|------|-----|-----------------|
| 1 全量测试 | **PASS** | 163ca7f0 · f2b74dec（另跑 21327217）；复测 d5128ea5 | d5128ea5：642 / 2 / 0；163ca7f0：633 跑 / **2 失败** / 0 跳过；f2b74dec：592 / 3 / 0；21327217：624 / 3 / 0。163ca7f0 的 2 个失败在基线上同样失败（对照见 §1） |
| T1-1 开关关逐位 | **PASS** | 21327217 + 163ca7f0 | golden `1838310 56afc560…dde4f6ee`。QA 在**基线 f2b74dec** 上独立重算出同一摘要；两个 SHA 上 2 槽重载与 armor=null 重载都和它逐位相等 |
| T1-2 Java↔p1sim | **PASS** | 163ca7f0（另跑 21327217） | g6 101,376 · var 101,376 · mismatches B0 H0 M0 D0（doubleToLongBits，无容差）· 无护符 101,376 坏 0。导出文件在干净构建中由本地 python 现生成，解压后 sha256 与插件岗文件相同（`45f12318…`）。QA 另用种子 20261008 和 7（空位率 0.35）导出，两份都是 0 差 |
| T1-3 取整 | **PASS** | 21327217 + 163ca7f0 | `EmberSixSlotRulesTest` 6/6；静态 diff：`enhanceCost/upgradeCost/refineCost/qualityCost` 无改动；甲走 `forgeable()` 拒绝且不扣费；零头用整数十分位 `addTenths` |
| T1-4 迁移幂等 | ~~FAIL~~ → **PASS @d5128ea5**（残余风险见复测节） | 21327217 + 163ca7f0；复测 d5128ea5 | 插件岗自带测试 9/9（head 12/12）可复现为绿；QA 扩展场景找到反例：225 个场景中 5 个坏，fuzz 6000 轮中 1 轮坏（见 §T1-4） |
| T1-5 迁移不变量 | **PASS** | 21327217 + 163ca7f0；复测 d5128ea5 | `invariantPerCaseBitExact`、`failedSelfCheckRevertsEverything`、`crashDuringRevertFinishesTheRevert` 均绿；QA 复现日志里第 2 步之后 H/D 逐位相等 |
| T1-6 资产事件级 | **SKIP** | — | 派单：拖放 / 发射器 / 盔甲架 / 死亡 / 换世界留给 T2。注意：T1-4 的缺陷同样属于「资产不丢不复制」，T2 须复测 |
| T1-7 掉甲口径 | **PASS** | 21327217 + 163ca7f0 | QA 独立 120,000 局（种子 700001 起，T1/T2/T3 × 无目标 / 三族）：部位 29574/30091/30145/30190，χ²=8.23（df3，临界 16.27）；成色 χ²=2.27（70/23/6/1）；精工 χ²=0.11（70/20/9/1）；目标族占比 0.6033（TARGET_WEIGHT 0.60）；阶≠图阶 0；额外件≠1 0；其它奖励被改 0；未击杀首领 = 空。开关关时 0 件甲。只在 `!challenge && abyss==0 && !raid && !event && !rush` 时发，与 p1sim `_settle` 一致（p1sim 只在主线结算 roll_armor）。S39 已入 source map |
| T1-8 护甲页占位符 | **PASS** | 163ca7f0 | `EmberSixRankTest` 10/10。排序键与 §5.4③ 一致：生命差 → 现穿同族（空位跳过）→ 护符同族 → 掉落阶 → 获得时间新者 → uid。（21327217 缺「获得时间」键，由 694fef6d 补上） |
| 3 开关默认值 | **PASS** | 163ca7f0 | `EmberSixSlot.enabled()` = `m.b("gear.six_slot.enabled", false)`；`migrateEnabled()` = `enabled() && m.b("gear.six_slot.migrate", false)`；`EmberMode.b` → `cfg.getBoolean(path, def)`。`six_slot` 在 bundled resources、`plugins/CoreRpg/` 里 0 处命中 |
| 3 线上未动 | **PASS** | 现场 | 详见 §3 |
| 4 NI 40 模板 | **PASS** | 163ca7f0（96201025 引入） | 40/40，缺 0、多 0，与 `templateId()` 和 ni-armor-ids.md 三方一致；缺模板时安全失败（详见 §4） |
| bv58/60 | **PASS（只是文档）** | 163ca7f0 | 详见 §5 |
| p1sim 规则哈希 | **预期变化** | 163ca7f0 | `tools/p1sim/rules.py` 把 `docs/design/ember-source-map.yml` 纳入内容哈希，a8e9f15c 改了表头，哈希会变。按总控口径记为预期，不判 FAIL |

## §1 全量测试失败清单对照

命令（三个 worktree 相同）：`nice -n 19 mvn -B clean test -DargLine="-Xmx768m -XX:+UseSerialGC -XX:ActiveProcessorCount=1"`，日志在 `/workspace/tmp/d318qa-{head,tip,base}-test.log`。

| 失败测试 | f2b74dec 基线 | 21327217 | 163ca7f0 |
|----------|---------------|----------|----------|
| `EmberGrowthTest.splitAffixRetiredFromPool_D165:289` | 失败 | 失败 | 失败 |
| `EmberGrowthTest.t2cIsArmorBreak_D164:232`（row-2 text says 仅主线重打生效） | 失败 | 失败 | 失败 |
| `EmberSourceMapTest.headerMatchesBalanceVersion:543`（expected 60 but was 58） | 失败 | 失败 | **通过**（a8e9f15c 勘误） |
| 合计 | 592 / 3 | 624 / 3 | **633 / 2** |

- 163ca7f0 比基线多 41 个测试，全部是六槽新测试：SlotTest 8、RulesTest 6、RankTest 10、MigrationTest 12、OffGolden 1、P1sim 2、NiArmorTemplate 2。全部通过，0 跳过。P1sim 没有因为缺 python 被 SKIP。
- 符合总控新口径：633 个，2 个失败，都是基线自带。

## §T1-4 阻塞缺陷（证据，未修）

**位置：** `EmberSixMigration.reconcile()`，在「issued 全部在身上（`all == true`）」这个分支里直接调 `commit(rec)`，**之前没有 `port.persistInventory()`**。正常路径 `run()` 是先 persist 再 commit 的；部分在身的分支也有 persist；只有这个分支漏了。

**最小复现**（`QaSixReproTest.reproA`，用插件岗自己的 `EmberSixMigrationTest.World` 模型，case 0 = 原版甲位有物品）：
1. migrate，在第 10 次副作用（`persistInventory`）处注入异常。此时身上已是 4 件迁移甲，磁盘玩家档里仍是原物，journal 已存。
2. 不重启（异常被 `tryMigrate` 捕获，服继续跑），玩家再次回枢纽，migrate 返回 `ROLLED_FORWARD`。此时 flag=true，待领=3，db=4，但**磁盘玩家档里仍是原物**。
3. 硬关（玩家档从磁盘回来）：甲位 = `[iron_helmet, null, diamond_leggings, other_players_p1_boots]`，待领里还有同样 3 件，**原物 ×2**；身上的迁移甲为 0（db 有 4 条）。
4. 再跑 migrate 返回 `ALREADY`，不会自愈。

**规模：** 「每一步崩溃 × 3 种重启 → 续上 → 硬关」共 225 个场景，5 个坏。5 个坏场景都是 crash@10 且不重启，5 种 case 各一个。case 2（甲位原本为空）不复制，但 4 件甲丢失，H 少 w·h·(q+f)，不变量被破坏。另用种子 1/42/1008/20261008 做 fuzz，共 6000 轮 × 10 步，1 轮坏（seed 1008 run 347，同一根因）。在 21327217 和 163ca7f0 上结果相同（694fef6d 没有改 `reconcile`）。

**为什么插件岗测试是绿的：** `crashAtEveryStepIssuesOnceOrNever` 在崩溃、重启、续上之后就断言结束，没有覆盖「续上成功后又被硬关」。

**读代码发现的相关风险（未能离线实测）：** 线上 `LivePort.persistInventory()` 调用的是 `EmberVault.savePlayerFile`，它会**吞掉** `saveData` 抛出的 RuntimeException，只记一条 warning。所以线上存档失败时，`run()` 不会走到异常分支，而是照常 commit（置标记、写待领），之后同样存在「硬关 → 原物复制」的窗口。

**建议修法（供插件岗参考，测试岗未改）：** ① 在 `reconcile` 的 all-held 分支里，commit 前补一次 `port.persistInventory()`；② 迁移场景下存档失败应当抛出（或返回失败），不要静默 commit；③ 补测试：crash@persist + 不重启 + 续上 + 硬关，以及上面的多种子 fuzz。修复后测试岗只需复跑 T1-4 和 T1-5。

**触发条件：** 存玩家档恰好失败，之后到下一次存档之间又发生硬关（kill -9 或 OOM）。概率低，但会复制玩家资产，与 T1-4「不重复、不半发」和 T1-6「0 复制」直接冲突。

## §3 开关与线上未动

- 代码默认值见上表。两个键都不在 jar 的 bundled yml 和线上 yml 里，开关实际取代码默认值 false。`onJoin`、`onWorld`、`tryMigrate`、`cmd`、papi 都先判开关再执行。
- `git diff f2b74dec 163ca7f0 -- plugins/CoreRpg CoreRpg/src/main/resources` 为 0 行。`plugins/CoreRpg/ember-v1.yml` 最后一次提交是 `53fb4a79`（10-08 01:38，D305），工作区无改动，mtime 04:11:55。
- 线上 jar `plugins/CoreRpg.jar`（`server-runtime/plugins → ../plugins`）不受 git 跟踪：
  - plugin.yml 版本 **1.65.97**，mtime **04:11:44**，早于 D318 首个提交（09:16）；
  - sha256 `eab972db…aa45d8d`，d318 jar 为 `7917c14b…61fe6`（1.65.98，09:34），**不同**；
  - 线上 jar 里 `EmberSix*` 类 0 个，d318 jar 里 16 个。
- 游玩服 06:55:31 启动，日志 `[06:55:44] Enabling CoreRpg v1.65.97`；本窗内无 NI reload 记录。
- 服务全程运行，未重启：login 47542 和 waterfall 47617（05:38 起）、play 101651（06:55 起）、mysqld_safe 46976。

## §4 NI 40 模板

- 文件：`plugins/NeigeItems/Items/ember-armor-v1.yml`（96201025 提交）。
- QA 用 python 解析 yml，与 `EmberItemData.templateId()`（T0 → `ember_v1_t0_<slot>`，其余 → `ember_v1_<fam>_<slot>_t<tier>`）展开的 40 个 ID、ni-armor-ids.md 展开的 40 个 ID 三方比对：**缺 0、多 0**。
- 逐个模板检查：
  - 材质 = `materialFor`（T0 皮革 / T1 锁链 / T2 铁 / T3 钻石），40/40；
  - `unbreakable: true`，有 HIDE_ATTRIBUTES 和 HIDE_UNBREAKABLE；
  - generic.armor 和 armorToughness 都为 0，Slot 正确（靴子用 MC 正确写法 `feet`）；
  - 80 个 AttributeModifier 的 UUID 互不重复；
  - name 和 lore 里没有 物理伤害 / 生命力 / 物理防御，也没有附魔。
- 插件岗的 `EmberSixNiArmorTemplateTest` 2/2 通过，未跳过。
- **缺模板时安全失败（读代码 + 测试）：**
  - `EmberItems.create` 在 `createNiItem == null` 时返回 null；
  - 结算掉甲和 Q01 起步件走 `giveItem`：create 为 null 时返回 null → `waiting++`，这一行保持待发，下次再试，不 remember、不丢；
  - 迁移：`run()` 在 create 为 null 时返回 `NO_TEMPLATE`，不存 journal、不动甲位、不置标记（`missingTemplateDoesNothing` 绿），`reconcile` 同样处理，`tryMigrate` 会告警 OP。
- 观察（非 FAIL）：模板放在线上 NI 目录，下次 NI reload 或重启就会加载；开关关时没有任何代码引用这些模板，是惰性的。

## §5 bv58/60

- `rg "ember-source-map" CoreRpg/src/main`：只在 `EmberEconomy.java` L34、L36 的 javadoc 注释里出现，没有读取代码。只有 `EmberSourceMapTest`（测试）会读这个文件。
- 运行时读 bv 的两处：`EmberRunMaps` 读 `ember-v1-runs.yml` 的 `balance_version: 60`；`EmberEconomy.economyYmlDrift` 读 `ember-v1-economy.yml`。都与 source-map 无关，本窗也没有改动。
- a8e9f15c 只改了 `docs/design/ember-source-map.yml` 的一行（`balance_version: 58 → 60`），属于规格 §5.4⑤ 说的文档勘误，**bv 未变**。

## §6 复跑命令摘要

- 全量：见 §1。
- T1-1 基线独立 golden：把 `EmberSixSlotOffGoldenTest` 改写为只算 mode 0 的 `QaT1GoldenBaseTest`，放进 base worktree，跑 `mvn -o test -Dtest=QaT1GoldenBaseTest`，输出 `1838310 56afc560…`，与仓库 golden 一致。
- T1-2 换种子：`QA_SEED={20261008,7} QA_EMPTY=0.35 python3 qa_six_export.py OUT.tsv.gz`（只把 `six_t1_export.py` 的种子和空位率参数化），然后 `mvn -o test -Dtest=EmberSixP1simTest -Dsix.p1sim.file=OUT`。
- T1-4 / T1-7：`mvn -o test -Dtest='QaSixIndependentTest,QaSixReproTest'`。在两个 SHA 上结果相同：T1-7 PASS，T1-4 的 2 个 QA 用例 FAIL。
- 内存：盒子可用内存最低 **5981 MB**（每 5 s 采样），未触发 1 GB 停等。
- worktree：三个 worktree 用完已 `git worktree remove`。

## 复测 @d5128ea5（T1-4 修复 · 2026-10-08 09:54）

修法（`git diff 163ca7f0 d5128ea5 -- CoreRpg/src/main`，3 个文件）：
- `Port.persistInventory()` 改为返回 boolean；
- `reconcile` 中「4 件都在身上」的分支先存档再 commit；「什么都没发」的分支在丢 journal 前先存档；
- `run()` 的 swap 存档失败时撤回 live、保留 journal，返回 `SAVE_FAILED`；
- revert 和领取都改为先存档再记账；
- 线上 Port 改用新增的 `EmberVault.savePlayerFileChecked`，只在迁移和领取时使用。

### T1-4 / T1-5 复跑（QA 自写用例 + 插件岗用例，全部在 d5128ea5 上）

| 用例 | 结果 |
|------|------|
| 上次的最小复现 reproA（crash@10 → 不重启 → 续上 → 硬关），case 0–4 | 5/5 绿：硬关后身上仍是 4 件迁移甲，原物不复制，再跑返回 ALREADY |
| 每一步崩溃 × 3 种重启 → 续上 → 硬关（225 个场景） | **坏 0**（上次 5） |
| fuzz 原种子 1 / 42 / 1008 / 20261008，各 1500 轮 × 10 步 | 每个种子都**坏 0**（上次 seed 1008 run 347 坏，这次变绿） |
| fuzz 新种子 777 / 31337，各 1500 轮 | 坏 0（合计 9000 轮，0 丢 / 0 复制 / uid 唯一） |
| 两次崩溃后再续上（300 个场景） | 全部恰好一次（ALREADY 84 / MIGRATED 103 / ROLLED_FORWARD 113） |
| 连跑 1 + 3 次 | MIGRATED 一次，之后都是 ALREADY，db=4 |
| 插件岗 `EmberSixMigrationTest` | 21/21：续上 × 每个写点硬关 6330 场景坏 0；存档失败 13122 场景坏 0；fuzz 50000 轮坏 0；qa225 坏 0 |
| T1-5（`invariantPerCaseBitExact` 等） | 绿 |

### 抽查

| 项 | 结果 |
|----|------|
| 全量测试 | `mvn -B clean test`：**642 跑 / 2 失败 / 0 跳过**，失败为 `EmberGrowthTest` D165:289、D164:232，与基线一致；比 163ca7f0 多出的 9 个都是迁移测试，全部通过 |
| T1-1 golden | `EmberSixSlotOffGoldenTest` 1/1 绿；T1-2 对拍顺带也是 0 差 |
| 原 `savePlayerFile` | 方法体逐字未变（EmberVault 的 diff 只有新增，0 行删除）。其余 6 处外部调用 Forge:605、Pay:233、Delivery:164 和 199、GearLib:289 和 292 所在文件**都没有 diff**；Vault 内部 :199 也没变。SixSlot 的换甲动作（163ca7f0 :434 → d5128ea5 :442）仍走原方法，行为不变。`savePlayerFileChecked` 只有 `LivePort.persistInventory` 在用（LivePort 同时实现迁移 Port 和领取 ClaimPort），插件岗测试 `liveSaveIsCheckedOnlyForSixSlot` 已锁住这一点 |

### 残余风险评估（QA 离线复现：`QaSixResidualTest`，用插件岗的 World 测试替身）

**能复现。** 先进入「journal 保留、迁移甲在身上（磁盘也是这个状态）、未置标记」的状态，三种进入路径都能到：
- **C**：进程在已存档、未置标记的窗口里死亡（crash@11，也就是第一次 remember），然后硬重启。这条**不需要存档失败**。
- **B**：crash@10 后续上，续上时存档失败，返回 SAVE_FAILED。这个分支没法把 live 撤回，因为原物只存在 journal 里。
- **A**：存档报失败但其实已写盘（ghost），然后硬重启。

之后玩家在下一次枢纽续上之前处理迁移甲：

| 变体 | case 0（原物 3 件）结果 | case 3（原物 4 件，背包满） |
|------|------------------------|-----------------------------|
| 4 件全扔地上 → 回枢纽（C1、B1、A1 结果相同） | JOURNAL_DROPPED → 下一次 MIGRATED：**原物丢 3**；又发一套新的 4 件（旧的 4 件不在 DB 里，成了惰性物品） | **丢 4** |
| 只扔 1 件（头）→ 回枢纽（C2、B2） | ROLLED_FORWARD：**头部原物丢 1**（journal 的原物被替换成空槽），重建出的头盔和地上那件**同 uid**，复制 1 | 丢 1，uid 复制 1 |
| 全放进箱子 → 回枢纽 → 再从箱子拿回（C3） | 丢 3；身上 8 件 P1 甲（旧 4 件不在 DB，惰性） | 丢 4 |
| 扔完下线再上线（存档）→ 回枢纽（C4、B3） | 丢 3 | 丢 4 |
| 扔完被硬关，存档恢复后再续上（C5） | **安全**：ROLLED_FORWARD，丢 0，待领 3 | 安全 |
| 对照：不扔，直接回枢纽（C0） | 安全 | 安全 |

**根因：** `reconcile` 用「迁移甲是否在身上」来推断原物在哪里：
- 一件都不在身上，就认为 swap 没落盘，原物还在原位，于是丢掉 journal；
- 部分在身上，就把缺件槽里的当前内容当作原物。

但迁移甲不在身上也可能是**玩家自己拿走的**，这时原物唯一的副本就在 journal 里，丢掉 journal 就丢了原物。

**后果：** 主要是原甲位物品丢失（原版甲或他人的 P1 物品）。扔 1 件的变体还会产生同 uid 的 P1 复制件。迁移甲本身是 `source=migrate`、绑定、不可分解，复制件价值有限，但违反「同 uid 不占两格」。

**触发条件和概率：**
- 条件一：进程在「已存档、未置标记」的窗口内死亡（只隔 4 次 remember + 1 次记录写），或者存档失败（磁盘满、IO 错误）。
- 条件二：玩家在下一次枢纽续上之前拿走迁移甲。重启后玩家多半直接回到枢纽，`onJoin` 120 tick（约 6 秒）后就续上，所以条件二的窗口很窄。B 路径下服务器不重启，玩家要等下一次换世界才会续上，这段时间里摘头盔、换原版甲都很平常，概率相对高一些，但前提是存档已经失败。
- 两个条件同时成立，整体概率**低**。

**是否应在 T1 内修：建议修。** 理由：
1. 迁移在 T3 对全服各执行一次，不可逆，规格 T1-4 / T1-6 的口径是 0 丢 0 复制；
2. 已能确定性离线复现，修法局部、能离线测；
3. 推迟到 T2 就得在测试服迁移演练时才发现。

**建议修法（供插件岗参考，测试岗未改）：**
1. **以原物为准，不以迁移甲为准。** journal 只有在确认其中的原物都已回到玩家身上之后才能丢弃（逐槽比对 `originals[i]` 与当前 `armor(i)`，用 isSimilar + 数量）。凡是不在身上的原物，在丢 journal 的**同一次记录写**里转入待领。
2. **部分在身的分支不要用 `port.armor(i)` 覆盖 journal 原物。** 缺件槽里只有在当前内容就是原物时才沿用；否则原物进待领，当前内容保持不动。
3. **不要用已发出的 uid 重建迁移甲。** 缺件要么换新 uid 重发（旧 uid 不 remember，成为惰性物品），要么不补发并在提示里说明；不得出现同 uid 两件。
4. **补测试：** 上表 C1–C5、B1–B3、A1 这些变体，加上「每个写点 × 扔 0 / 1 / 4 件 × 3 种重启」的组合 fuzz，断言原物 0 丢、uid 唯一。

### 复测结论

- T1-4、T1-5：**PASS @d5128ea5**（原阻塞已修复）。
- 残余风险：**已复现，等级中低**（概率低，后果是资产丢失），建议在 T1 内修。
- 能否签「T1 过线」：测试岗建议**先修残余风险再签**。若总控接受风险，则签 T1，并把这项修复列为 T2 迁移演练的硬前置条件。
- 复测环境：worktree `/workspace/tmp/d318-wt-fix`（d5128ea5）和 `d318-wt-prev`（163ca7f0，仅用于 diff），用完已删；可用内存最低 6015 MB；服务全程运行，未重启、未部署。

*余烬-测试 · 只验收不修 · 白板经济模型未覆盖*
