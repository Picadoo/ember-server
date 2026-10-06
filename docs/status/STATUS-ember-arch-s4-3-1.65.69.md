# STATUS · ARCH S4-3（CoreRpg 1.65.69 / D244）

**日期：** 2026-10-06 Asia/Shanghai · **上游：** live 1.65.68 / D243（d43d518）/ bv58

两件事，**数量全不变**（`balance_version` 仍 58）：(1) 词缀原语 → 导出表 → p1sim 读表 + 漂移测试；(2) G10 物品级缺口登记（只打标签）。插件只多了登记行（`EmberEconomy`），所以发 1.65.69。

## 1 · 词缀导出表（D241 §3 的下一刀）

| 件 | 内容 |
|---|---|
| 导出器 | `CoreRpg/src/test/java/town/sunshine/corerpg/p1/EmberAffixExportTest.java`（4 测试）：`EmberAffixes.all()` + `AffixCycle` + 各 `Affix*` 原语静态方法 × 线上 `plugins/CoreRpg/ember-v1-runs.yml` `variety` → 每个词缀的 family / `first_arm_s` / `first_hit_s` / `period_s` / `warn_s` / `dmg_atk` / 形状参数 / yml 输入 |
| 入库表 | `tools/p1sim/affix-table.json`（12 个词缀，grace 1.5 s，bv58）。重导：`cd CoreRpg && mvn -o test -Dtest=EmberAffixExportTest -Daffix.export=write` |
| 漂移即失败 | Java：`exportMatchesCheckedInTable`（原语 / yml 改了没重导 → 红）、`everyPrimitiveAndEveryLiveAffixIsExported`、`firstArmMatchesTheReplayHarness`（首次就绪 = 回放 harness PROMOTE `next=`）、`spotValues`。p1sim：`rules.affix_table_drift()` → `validate()` 拒跑；`affixtable.row()` 对 what-if 改 variety 没重导抛 `RuleError`；selfcheck 4 条 D244 检查 PASS |
| p1sim 改动 | `affix_mob`：blazing / venom / jailer / arcane 用表里 `first_hit_s` / `period_s` / `dmg_atk`；firechain 用 `first_burn_s` + `burn_every_s / FIRECHAIN_EXPOSURE`；`AFFIX_STRESS` 用表 grace；`affixpack5.py` 压力表头读表。覆盖面不变（regen / charge / frost / mortar / molten 仍未建模） |

节奏变化（`python3 tools/p1sim/affixtable.py diff`；伤害系数全部不变）：

| 词缀 | 首击 s 旧 → 新 | 周期 s 旧 → 新 |
|---|---:|---:|
| blazing | 4.5 → 5.5 | 3.0 → 4.0 |
| venom | 7.5 → 8.8 | 6.0 → 7.3 |
| jailer | 8.5 → 9.7 | 7.0 → 8.2 |
| arcane | 12.5 → 12.5 | 11.0 → 12.5 |
| firechain（首次可烧） | 2.7 → 3.9 | 1.0 → 1.0（每人烧伤间隔，再 ÷ exposure 0.33） |

线上冒烟对得上：venom 晋升到首次落地 ≈ 9 s（表 8.8，旧 7.5）；arcane 扫完记日志 ≈ 14 s（表 9.5 + 1.5 + 3.0 = 14.0）。

### Gate 前 / 后（改前 = d43d518 worktree；不调参）

| Gate | 改前 | 改后 | 结论 |
|---|---|---|---|
| p1sim 默认（60 人 × dodge 0.3 / 0.5 / 0.7） | Q07 首通中位 24 / 8 / 3 天，P90 33 / 11 / 4，100 % | 中位 **23** / 8 / 3，P90 33 / 11 / 4，100 % | 21 格前沿通关率**逐格相同**；首通天 20/21 格相同（dodge 0.3 Q07 −1 天，60 人样本；800 人同格 26 = 26）。不翻 |
| p1sim 800 人 | Q07 中位 26 / 8 / 3，P90 35 / 11 / 4 | 相同 | 21 格前沿率、首通天全部相同（只 Q06 dodge 0.3 符 +7 → +6、H 165 → 164）。不翻 |
| `--ref` 书参考通关率 | — | 逐位相同（只 rules 戳不同） | 不翻 |
| affixpack5（600 × 种子 7 / 11 / 13，42 格，帽 3.0 pp） | MAX_ABS_DPP 1.1 · 压力比毒十字更难 0.4 | MAX_ABS_DPP **0.8** · 压力 **0.6** | 两项仍 WITHIN RANGE；单格最大移动 venom 列 +0.5 pp（q06 书参考 0.5）、火链列 −0.7 pp（q03）、压力 venom 列 +0.9 pp（q03）。不翻 |
| selfcheck | 2 个已知 FAIL（图录菜单「预警 3.0 秒」文案、`signin.py:35`） | 同样 2 个 + 4 条 D244 新检查 PASS | 不变 |
| W30（p2econ 300 人 × 12 周 dodge 0.5 + 深渊 + 团本 + 周目标） | **5.50** 周（P2-2 深渊 = 深渊 + 周目标；P50 7.33；= D243 改后，复现） | **5.31** 周（P2-2 深渊；P50 7.33） | 标题 W30 −0.19 周（±0.5 内），最快路线不变，路线排序不变，不翻。其余：深渊 + 周目标 5.50 → 5.67、团本 + 周目标 6.00 → 6.12、无轮换 6.86 = 6.86、P2-1 7.17 → 7.14、轮换 + 团本 7.57 → 7.71；P50 无「达 ↔ 未达」变化，P90 全部仍未达。不调参 |

## 2 · G10：物品级缺口登记（只打标签）

| 行 | 名称 | 账户 / 周期 / 模型 | 依据 |
|---|---|---|---|
| **S36** | 钓鱼产出（CoreFish） | `LIFE_ITEM` · 无周期 · NONE | `plugins/CoreFish/config.yml` `tables` / `category_weights` |
| **S37** | 扭蛋券发放（CoreGacha） | `GACHA_TICKET` · 日 · NONE | `CoreGacha/config.yml tickets`（welcome 5、在线 30/90 分、委托 1/3 局、实物券） |
| **S38** | 扭蛋抽取产出 | `COSMETIC` · 日 · OUT | `CoreGacha/gacha.yml items`（只外观） |
| **C19** | 扭蛋抽取（耗券） | `GACHA_TICKET` · 日 · OUT | `cost_per_pull` 1、`daily_pull_cap` 50 |
| C17 | 生活 offer | 账户加 `LIFE_ITEM` | offer 吃生活件 |

无金样、无路由、`ember-v1-economy.yml` 不动。`ember-source-map.yml`：S36–S38 / C19 条目 + 新 `stocks:` 块（`insignia` / `badge` / `life_item` / `gacha_ticket` / `cosmetic`，每个写存储位置、来源 / 消耗行、上限、模型状态）。新单测 `EmberSourceMapTest.stocksMatchEconomyAndItemConfigs`（来源 ∪ 消耗 = `EmberEconomy.touching(账户)`、消耗必须是 C 行、生活件清单 = life.yml + CoreFish、券来源 = CoreGacha `tickets` 键、扭蛋件 kind 全是外观）；`EmberEconomyTest` 改成 S01–S38 / C01–C19 + `d244ItemLevelRowsAreTagsOnly`。REG §2 / §3 / §5 / §8、装备结构文档 §6.2 / §8 已同步。

## 测试

全量 **539/0/0**（JDK8；+6：导出 4、`d244ItemLevelRowsAreTagsOnly`、`stocksMatchEconomyAndItemConfigs`）。

## 部署 / 冒烟

- jar `5ddbe7526b6c90b1…` · play PID **2426288** · 09:27 Asia/Shanghai · `Enabling CoreRpg v1.65.69`、CoreRpg MySQL、CoreGacha MySQL、`ember-v1-economy.yml loaded as amount() SoT (bv58)`、无 FAIL-CLOSED、SEVERE 0。回滚：`/workspace/backup/CoreRpg-1.65.68-pre-1.65.69.jar`。
- 冒烟 **35/0/0**（FreshQ833 / FreshQ834），见 `docs/tests/smoke-2026-10-06-d244-arch-s4-3.md`。venom 那局 r2 一只僵尸卡墙后，控制台 tp 一次 bot 才清完（bot 寻路问题）。

## 不变

`balance_version` **58**；所有发放 / 消耗数量；词缀线上行为（只改 sim 读法）；钓鱼 / 扭蛋 / 生活玩法实际行为；C15；内容包。
