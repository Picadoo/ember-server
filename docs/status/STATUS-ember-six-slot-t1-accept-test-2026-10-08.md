# 余烬 · 六槽护甲 T1 离线独立验收（余烬-测试 · 2026-10-08）

- 派单：总控 D318「T1 过线」签字依据。要求独立复跑，不采信插件岗自测结论。
- 验收基准：**origin/main `163ca7f0`**（总控 09:42 改基准）。先按旧 tip `21327217` 跑过一轮；改基准后，变化文件相关的项在 `163ca7f0` 上重跑。下表「SHA」列写明每项在哪个提交上跑。
- 基线：`f2b74dec`。环境：`git worktree`，目录为 `/workspace/tmp/d318-wt-{tip,head,base}`（不在主工作区构建）；JDK 8u504，Maven 3.9.16，`nice -n 19`，surefire `-Xmx768m -XX:+UseSerialGC`，单进程，python3 3.13.5。
- 不部署、不重启任何服、不动 bv、不改配置或数值、未改插件代码。QA 自写的测试只临时放进 worktree，没有入库。源码存档在盒子上的 `/workspace/tmp/d318qa-src/`。
- **白板经济模型未覆盖**（规格 §5.2 要求注明）。本次只验 Java 逻辑与公式对拍。

## 总评

**FAIL：不建议现在签「T1 过线」。** 只有 1 个阻塞项，是 T1-4 迁移幂等的边界缺陷：迁移中途在「存玩家档」这一步抛异常，服务器继续运行，玩家下次回枢纽触发续上（ROLLED_FORWARD），之后服务器在下一次存档前被硬关，结果是原甲位物品复制一份（甲位一份，待领一份），4 件迁移甲从身上消失（DB 已记、标记已置，不会再发）。在两个 SHA 上都能确定性复现，修法很小（见 §T1-4）。
其余各项：T1-1、T1-2、T1-3、T1-5、T1-7、T1-8 PASS；开关默认关 PASS；线上未动 PASS；NI 40 模板 PASS；T1-6 按派单记 SKIP。

## PASS / FAIL / SKIP 表

| 项 | 结论 | SHA | 关键证据 / 数值 |
|----|------|-----|-----------------|
| 1 全量测试 | **PASS** | 163ca7f0 · f2b74dec（另跑 21327217） | 163ca7f0：633 跑 / **2 失败** / 0 跳过；f2b74dec：592 / 3 / 0；21327217：624 / 3 / 0。163ca7f0 的 2 个失败在基线上同样失败（对照见 §1） |
| T1-1 开关关逐位 | **PASS** | 21327217 + 163ca7f0 | golden `1838310 56afc560…dde4f6ee`。QA 在**基线 f2b74dec** 上独立重算出同一摘要；两个 SHA 上 2 槽重载与 armor=null 重载都和它逐位相等 |
| T1-2 Java↔p1sim | **PASS** | 163ca7f0（另跑 21327217） | g6 101,376 · var 101,376 · mismatches B0 H0 M0 D0（doubleToLongBits，无容差）· 无护符 101,376 坏 0。导出文件在干净构建中由本地 python 现生成，解压后 sha256 与插件岗文件相同（`45f12318…`）。QA 另用种子 20261008 和 7（空位率 0.35）导出，两份都是 0 差 |
| T1-3 取整 | **PASS** | 21327217 + 163ca7f0 | `EmberSixSlotRulesTest` 6/6；静态 diff：`enhanceCost/upgradeCost/refineCost/qualityCost` 无改动；甲走 `forgeable()` 拒绝且不扣费；零头用整数十分位 `addTenths` |
| T1-4 迁移幂等 | **FAIL** | 21327217 + 163ca7f0 | 插件岗自带测试 9/9（head 12/12）可复现为绿；QA 扩展场景找到反例：225 个场景中 5 个坏，fuzz 6000 轮中 1 轮坏（见 §T1-4） |
| T1-5 迁移不变量 | **PASS** | 21327217 + 163ca7f0 | `invariantPerCaseBitExact`、`failedSelfCheckRevertsEverything`、`crashDuringRevertFinishesTheRevert` 均绿；QA 复现日志里第 2 步之后 H/D 逐位相等 |
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

*余烬-测试 · 只验收不修 · 白板经济模型未覆盖*
