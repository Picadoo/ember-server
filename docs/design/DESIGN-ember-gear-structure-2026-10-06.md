# Ember 装备结构 · 权威设计文档（D242 / ARCH S4-1 · 2026-10-06）

> **状态：AUTHORITATIVE（现行 P1 装备结构的唯一权威说明）。** 对齐 CoreRpg **1.65.67**（`fa92a49`）/ `balance_version` **58**。
> 本文把散在 P1 书第 04–06、09 章、D169 / D174 / D167 / D141–D143 / D208 等文档里的「槽位 · 套装 · 签名（传奇）· 词条 · 锻造 · 保底 · 强化 · 物品键」合并成一份；
> 旧文档保留作历史与推导依据，顶部都加了指向本文的说明（§7）。
> 机器可读来源表：`docs/design/ember-source-map.yml`（§6），由 `CoreRpg/src/test/java/town/sunshine/corerpg/p1/EmberSourceMapTest.java` 守护。
> **本次只改文档 + 测试：没有改 CoreRpg 主代码、线上配置、服务器；CoreRpg 仍 1.65.67，不发版、不部署。**
>
> 真源优先级（冲突时）：**代码 / 线上配置 > 本文 > 旧文档**。本文与代码不一致 = 本文错（改本文）；本文发现代码 / 配置与设计意图不一致的地方列在 §8，**不在本次修**。

---

## 1. 槽位（Slots）

| 项 | 现行规则 | 真源 |
|---|---|---|
| 战斗槽 | **2 槽：刃（blade）+ 护符（charm）**。没有护甲槽；原版护甲 / 附魔 / 旧 StatService 词条不进 P1 结算 | `EmberItemData.slot`、P1 书 §4.1 / §4.5 |
| 国庆护符 | `fest_charm`：活动限定外观 / 小幅属性护符，**不是套装件、不参与觉醒** | `ember-v1-festival.yml` `charm`、C18 |
| 部位锁 | **不按图锁部位**（服主硬约束）：任何能掉装备的图都掉两个部位；地图只决定阶级、家族偏向（`loot_bias`），再叠该图专属签名（§3） | `ember-v1-runs.yml` `maps.<q>.loot` / `loot_bias` |
| 6 槽 / 8 槽 | **未上线**。D169 6 槽分阶段计划 = SETTLED（文档），代码 HOLD；Stage 0 = 设计 GO / 该掉落模型 NO-GO；Stage 1 未开。8 槽（gear8 草稿，未入库）= 取消 | §9 |

## 2. 家族、阶级、成色、精工（随机装备）

- **家族**：`scorch` 焚烬 / `burst` 烬爆 / `sustain` 炽愈（T0 = 无家族）。
- **阶级**：T0（起步包）· **T1** = Q01–Q03 · **T2** = Q04–Q06 · **T3** = Q07 / 挑战 / 深渊 / 团本。NI 模板 18 + 2：`ember_v1_{family}_{slot}_t{1..3}` + `ember_v1_t0_{blade|charm}`。
- **掉落一件装备**（`EmberRunRules.rollItem`）：目标家族 `TARGET_WEIGHT` 0.60（其余两族均分）＋ `loot_bias`（own_family / map_share / slot）；阶级 = 图阶级（E04：永不高于图）。
- **成色 quality 0–3**（+0 / 4 / 8 / 12 %）：普通 `QUALITY_WEIGHTS` **70 / 23 / 6 / 1**；挑战 & 团本 `CHALLENGE_QUALITY_WEIGHTS` **60 / 28 / 10 / 2**（`ember-v1-runs.yml challenge.quality`）；深渊按 `abyss.tiers[t].quality`；团本额外件 `raid_item.quality_floor` 1。
- **精工 craft 0–3**（锋刃 / 护心 +0 / 2 / 4 / 6 %）：`CRAFT_WEIGHTS` **70 / 20 / 9 / 1**。
- **成色 / 精工提升**（工坊，C05 / C06）：`refineCost(craft)` 0→1 胚 3 骨 5 币 300、1→2 6/10/600、2→3 12/20/1200；`qualityCost(q)` 0→1 胚 8 骨 8 币 800、1→2 16/16/1600；**成色 3 只能掉落获得**（工坊无 2→3）。
- P1 书 §5.2「不设自由词缀池」仍成立：**没有**自由词缀池；D141–D143 加的是每件 1 个**洗练词条位**（§5），值域小、档位上限受成色约束。

## 3. 套装与签名（Sets & Signatures / 传奇）

### 3.1 两件套三段觉醒（`EmberSetRules`）

两件**同家族**（刃 + 护符）= 套装激活。觉醒（P1 书 §4.3）：**I** 同族 T1+ 两件成套；**II** 两件均 T2+ 且均 ≥ +6；**III** 两件均 T3 且均 ≥ +9。同一被动，只换系数，不叠加。

| 家族 | 触发 | I / II / III |
|---|---|---|
| 焚烬 scorch | 每 `SCORCH_EVERY`=3 次命中点燃（4 跳，≤5 目标） | `BURN_COEF` 0.26 / 0.34 / 0.42 |
| 烬爆 burst | 每 `BURST_EVERY`=5 次爆发 | `BURST_COEF` 0.65 / 0.80 / 0.95，半径 3 / 3 / 3.5（≤5 目标） |
| 炽愈 sustain | 每 `SUSTAIN_EVERY`=5 次回复 | `SUSTAIN_PCT` 2.5 / 3.25 / 4 %；炽愈生命 ×1.12 |

三族互斥；`MIN_CHARGE` 0.9；根事件去重（`ROOT_MEMORY` 128）。

### 3.2 签名装备（D174，L01–L15）

- 每张主线图 2 件（Q07 3 件）**只在这张图出**的签名；在基础掉落**之上**叠加，不替代基础掉落。完整表见 `ember-source-map.yml signatures`（测试逐字段对 `EmberSignature.DEFS`）。
- **烙印掉落（S08）**：重打已首通的普通主线，`u < STAMP_RATE 0.12` **且**该图有适配这件基装的签名（部位相同，家族相同或 `any`）时，在基装上烙签名；没有适配签名则 0（即 12 % 是「有适配签名时」的率）。
- **首领徽记**（每图一种）：首通 `FC_MARKS` 3（S07）、重打 `CLEAR_MARKS` 1（S08）、誓约（S09）、前哨 / 残响（S17 / S18）、签到兜底（S23）。
- **烬炉烙印**（C12）：`IMPRINT_MARKS` 5 + `IMPRINT_COIN_PER_TIER` 300 × 阶级；需本人首通 Q02（`IMPRINT_UNLOCK`）且该图已首通。
- **双签名**：首通 Q03（`DUAL_UNLOCK`）后两件可各带一个签名（同 tag 排除仍生效）。
- **调律版**（C13）：首通 Q07 后每件有调律版的签名花 `ALT_MARKS` 10 枚该图徽记解锁一次；**代码里有 6 件**（L01 / L02 / L06 / L08 / L10 / L12，`EmberSignature.ALTS`）。
- 存储：签名码在物品 v2 字段 `sigCode`（D208 起），调律选择 `p1_sigalt_<id>`、解锁 `p1_sigaltu_`。

## 4. 强化、升阶、保底（Enhance / Forge / Pity）

| 强化 → +n | +1 | +2 | +3 | +4 | +5 | +6 | +7 | +8 | +9 | +10 |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| 成功率 `RATE` | 100% | 100% | 100% | 85% | 70% | 55% | 40% | 30% | 22% | 15% |
| 保底次数 `MAX_TRIES` | 1 | 1 | 1 | 3 | 4 | 5 | 6 | 8 | 10 | 12 |
| 碎片 `SHARDS` | 4 | 6 | 8 | 12 | 16 | 22 | 30 | 40 | 54 | 72 |
| 核心 `CORES` | 0 | 0 | 0 | 0 | 1 | 1 | 2 | 3 | 4 | 6 |
| 币 `COINS` | 40 | 60 | 80 | 120 | 180 | 260 | 380 | 540 | 760 | 1080 |

- **强化保底** = 物品字段 `pity`（当前目标的失败计数），第 `MAX_TRIES[n]` 次必成（`EmberItemData.MAX_PITY` 12）。失败不降级、不爆装；材料不足不抽随机、不推进保底。消耗 C03。
- **同槽强化轨道互换**（P1 书 §6.3）：同槽两件合法装备在城内免费交换「强化等级 + pity」，事务锁两个 uid。
- **升阶**（C04，`upgradeCost`）：T1→T2 碎 60 / 核 12 / 胚 6 / 币 1500（需首通 Q04）；**T2→T3 60 / 15 / 6 / 1800**（D104 减半，需首通 Q07）。保留家族、强化、成色、精工、绑定。
- **8 印记兑换**（S28 / C07，「锻造」）：8 枚同阶锻造印记 → 选定家族 + 部位的**标准件**（成色 0 / 精工 0 / +0，绑定，`source=drop`，可分解）。
- **分解**（S29）：只有 `source=drop` 可分解，得胚料 = 阶级（T1–T3）。首通自选件、起步包、管理员件不能分解。
- **随机锻造 / 烬砧**（D167）：**设计-only，未实现**（等 6 槽 Stage 1）。

## 5. 洗练词条（Affixes，D141–D143）

- 每件 1 个词条位；词条表 `ember-v1-growth.yml reroll.affixes`（刃：猎缀纹 / 余烬纹 / 裂身纹〔不可洗出〕；护符：定身纹 / 分核纹 / 抗缀纹）。
- 洗练（C11）：档位权重 `tier_weights` 50 / 30 / 15 / 5，档位上限按成色 `tier_cap` 1 / 2 / 3 / 4；保底 `pity` 5（物品字段 `afPity`）；费用 `coin` 300 / 600 / 1000、`shard` 40 / 80 / 120，锁词条 `lock_shard`。
- 词条只在其注明的内容生效（如「仅主线重打」）；D241 原语化（`DESIGN-ember-affix-primitives-d241.md`）只改实现路径，数值不变。

## 6. 物品键与来源（Item keys / source map）

### 6.1 物品数据（`EmberItemData` v2）

- NBT：`ember_v1`（数据）+ `cr_p1_item`（标记）+ HMAC 签名（密钥 `ember-v1-item.key`，gitignore，不入库）。
- 字段：`uid, ni, family, slot, tier, quality, craft, enhance, pity, bound, source, version, rev, affix, afPity, sigCode, rerollN`。D208 已把旧计数 `p4_af_ / p4_afp_ / p1_sig_ / p4_rrn_` 折叠进物品。
- `source` ∈ `drop | quest | admin | reissue | migrate`（`EmberItemData.SOURCES`）：决定分解 / 图录 / 洗练重复判定。`drop` = 结算随机件、团本额外件、宝箱额外件、8 印记兑换件；`quest` = 首通自选件、起步包；`admin` = OP 发放。

### 6.2 `ember-source-map.yml`

每一条能**拿到**装备 / 材料 / 货币的来源（以及每条登记的消耗）→ 内容（图 / 模式 / 首领）→ 掉落表 → 经济键（`ember-v1-economy.yml`）/ 账本键 / 计数器 → 配置文件 + 代码位置。结构：

| 块 | 内容 |
|---|---|
| `accounts` | 账户（= `EmberEconomy.Account`）、NI id、存储位置 |
| `item_sources` | 物品 `source` 值与可否分解 |
| `content` | q01–q07、挑战、深渊、r01–r03、连战 / 前哨 / 残响、gq26、挂机庭 4 档、账号菜单；阶级 / 名称 / 首领 / 掉落家族 / 签名 / 来源 |
| `signatures` / `item_affixes` | L01–L15、6 个洗练词条 |
| `sources` | S01–S38（REG / `EmberEconomy` 行；D243 起 S33 图录阶段币 / S34 宝箱额外件 / S35 起步包；D244 起 S36 钓鱼产出 / S37 扭蛋券发放 / S38 扭蛋抽取产出，标签 only）+ X03（OP `give` / `givedup` 的 `admin` 件，不是经济来源） |
| `legacy` / `sinks` | LS1–LS5（P1 下关闭）、C01–C19（D244 加 C19 扭蛋抽取） |
| `stocks`（D244） | 物品级库存：`insignia`（S07/S08/S09/S17/S18/S23 → C12/C13，模型 `insignia.py`）、`badge`（S16/S19/S27 → C15/C16/C18）、`life_item`（S30/S36 → C17，17 个 NI 件）、`gacha_ticket`（S37/C16 → C19）、`cosmetic`（S38/C15） |

`EmberSourceMapTest`（JDK8 单测，14 个）在以下情况**让构建失败**：

1. `EmberEconomy` 有行（S/LS/C）没在表里，或表里引用了不存在的行；
2. `ember-v1-economy.yml` 的 `sources/sinks` 下有键没进任何 `economy_keys`（或列了已删除的键）；
3. 扫描 `p1/*.java` 得到的发放 / 账本键（`Grant(…)`、`ledgerRow(…)`、`.record(…)`、`item(…)`、`raidItem(…)`、首通 choice→item、`var_event_*`、签到 / 在线后缀）或账本 run 前缀（`p1afk-`、`p1sign-`、`p1online-`、`mark-`、`failrefund@`、`deathrefund@`、`codex`、`starter`）没被任何 `grant_keys / ledger_keys / ledger_runs` 覆盖；反过来表里的模式在代码里找不到（`runtime_keys: true` 的运行时拼键除外）；
4. `ember-v1-runs.yml` 的 maps / raids / rush、挑战、深渊、国庆本、挂机庭档位缺失，或名称 / 阶级 / 首领 / 掉落家族不一致；内容 ↔ 来源互链不对称；
5. 签名与 `EmberSignature.DEFS` / `ALTS` 不一致；词条与 `reroll.affixes` 不一致；`item_sources` ≠ `EmberItemData.SOURCES`；金库白名单 / `EmberUpgradeRules.MAT_*` 没有对应账户；
6. 任何代码引用（`文件#片段`）或配置引用（`文件#点分键`）失效；计数器族不在 `EmberCounters`；`balance_version` 与线上不一致；
7. （D243）打包进 jar 的 5 个 `ember-v1*.yml`（`src/main/resources`）与线上 `plugins/CoreRpg/` 不是逐字节相同；
8. （D244）`stocks` 每个库存的 `rows_in ∪ rows_out` ≠ `EmberEconomy.touching(账户)`，或 `rows_out` 里有非消耗行；`life_item.items` ≠ life.yml + CoreFish 的 NI 件；`gacha_ticket.grants` ≠ CoreGacha `tickets` 键；扭蛋件 kind 不是外观。

**规则：新来源 / 新经济键 / 新图 / 新签名 / 新词条，同一提交里更新 `ember-source-map.yml`。** 插件不读本文件；D243 起 p1sim 读它（`tools/p1sim/sourcemap.py`：带 `sim:` 的来源 = S33 `codex_stage`、S35 `starter_kit`，数量从 `ember-v1-economy.yml` 取并与 Java 常量双重断言）。

## 7. 被取代 / 仍有效的旧文档索引

| 文档 | 状态 | 说明 |
|---|---|---|
| `design-ember-v1.0-P1.md` 第 04–06、09 章（装备部分） | **部分取代** | 原则仍有效；数字以本文为准（§6.4 T2→T3 已改为 D104；§5.2 加洗练注；§3.2 / §9.4 首通自选加注） |
| `DESIGN-ember-gear-staged-plan-2026-10-04.md`（D169） | 计划仍 SETTLED，代码 HOLD | 现行结构见本文 §1；§0「每图掉所有部位」被 D174 补充为「基础掉落 + 每图签名」 |
| `DESIGN-ember-gear-6slot-stage0-2026-10-04.md` | 离线模拟，历史 | 6 槽数字未上线 |
| `DESIGN-ember-forge-random-2026-10-04.md`（D167） | 设计-only | 未实现 |
| `DESIGN-ember-growth-sidegrades-2026-10-04.md` | 待 6 槽复核 | 未上线 |
| `DESIGN-ember-mainline-unlocks-2026-10-04.md`（D174） | 阶段 1–3 已上线 | 签名规则见本文 §3.2；§11.3 列 7 个调律候选，上线 6 件（L11b 不开放，D190） |
| `DESIGN-ember-enhance-socket.md` / `docs/ember-gear-drop-t0-t3.md` / `docs/ember-gear-stats.md` | **旧系统，P1 下不适用** | 镶嵌 / 旧掉落表 / StatService |
| `DESIGN-ember-build-diversity-2026-10-04.md` / `RESEARCH-ember-gear-reference-2026-10-04.md` | 输入 / 调研 | 不是规则 |
| `DESIGN-ember-v1.2-gear8.md`（未入库） | 取消（D168） | 本次未触碰 |

## 8. 不一致清单（D242 审计）

**已修（明确的文档错误）：**

| # | 位置 | 错误 | 处理 |
|---|---|---|---|
| F1 | P1 书 §6.4 | T2→T3 写 120 / 30 / 12 / 3600 | 改为 D104 现行 60 / 15 / 6 / 1800（注原稿数） |
| F2 | P1 书 §5.2 | 「不设自由词缀池」未提洗练词条位 | 加注 D141–D143 洗练位 |
| F3 | REG S06 | 「Q02 自选 T1 刃」 | 代码 / 配置 `choice: piece` = 自选家族 × 部位（刃或护符） |
| F4 | REG S13 | 「层 1–10；无尽层」 | 10 层硬上限，无无尽层 |
| F5 | REG S08 | 12 % 未写适配条件 | 注明仅在该图有适配签名时 |
| F6 | REG C13 | 「7 件调律版」 | 代码 `EmberSignature.ALTS` = 6 件（L01 / L02 / L06 / L08 / L10 / L12） |
| F7 | 6 槽 Stage 0 头注 | 「服主否决所有图掉所有部位」易被误读为按图锁部位 | 加注：现行 = 基础掉落全部位 + 每图签名叠加 |
| F8 | D174 文档 §11.4 第 5 行 | 「每件签名一个计数、不动物品 NBT」 | 加注：D208 起签名码在物品 v2 `sigCode` |
| F9 | P1 书 §3.2 / §9.4 | 「Q01 自选武器、Q02 自选护符」 | 加注现行：Q01 = T1 护符（D98 / bv12），Q02 = 自选族 × 部位一次定向兑换（D110 / bv15） |

**记录为后续缺口（真实行为 / 配置差异，本次不修）：**

| # | 缺口 | 影响 | 建议 |
|---|---|---|---|
| G1 | 图录阶段奖励 200 / 400 / 600 / 1000 币（`EmberCodex.STAGE_COIN`，账本 run `codex` / `stage<n>`）不在 REG / `EmberEconomy`，币未打来源标签，p1sim 未建模 | 每角色一次性 2200 币，账本审计看不到来源 | 登记为 S33（或 X01 正式化），`grantCoin` 带标签，p1sim 加一次性项 |
| G2 | `extra_chest_item`（宝箱额外件 5 %）没有自己的 REG 行（混在 S01） | 装备产出统计合并 | S02 拆「宝箱怪币 / 宝箱额外件」或加 S01b |
| G3 | 起步包（T0 刃 + 护符 + `starter.heal_potions` 5 瓶药）不是 REG 行 | 无战力影响，审计缺口 | 登记 X02 |
| G4 | OP `give dup` 生成 `source=drop`（可分解）物品 | 仅 OP 测试；可被分解成胚料 | 改 `source=admin` 或在分解处排除 dup 标记 |
| G5 | 打包的 `CoreRpg/src/main/resources/ember-v1.yml` 落后于线上：缺 `legacy_gate` 块（走代码默认）、注释过时 | 新环境首启拿到旧默认 | 下次触碰配置的版本同步 |
| G6 | 线上 `ember-v1.yml` 挂机庭注释写「不发材料」，实际各档发绑定碎片 / 骨尘 / 核心 / 胚料（D177 rev 2） | 只是注释 | 下次配置发版改注释 |
| G7 | `ember-v1-runs.yml` raids 注释「r01 + r02 共用周次数」，实际 r03 也 `cap_group: raid` | 只是注释 | 同上 |
| G8 | S09 誓约徽记数量无 yml 经济键（代码按誓约条数计） | 不能纯配置调 | E2 加 `S09.per_rule` |
| G9 | p1sim 未建模图录 / 起步来源 | 模拟少算 2200 币 / 角色 | 随 G1 |
| G10 | REG §5 物品级缺口仍在（徽记 / 余烬徽库存、生活产出、扭蛋） | 见 REG §5 | D244 已修（见下表） |
| G11 | 线上 `ember-v1-runs.yml` balance_version 历史注释 41 写「7 件调律版」，代码 6 件 | 只是注释 | 下次配置发版改「6 件（L11b 不开放）」 |

**D243（CoreRpg 1.65.68，ARCH S4-2）处理状态：**

| # | 状态 | 怎么修的 |
|---|---|---|
| G1 | **已修** | REG / `EmberEconomy` **S33**「图录阶段奖励」（COIN，一次性，键 `p1_codex_` / `p1_codex_stage_`，账本 `codex/stage<n>`），金样 `at5/10/15/20.coin` = 200 / 400 / 600 / 1000（= `EmberCodex.STAGE_COIN`，数不变）；账本币行 `sourceForGrant` → S33，走带标签的 `grantCoin` |
| G2 | **已修** | **S34**「宝箱额外装备」（GEAR，按局），金样 `chest_weight` 5（= `EXTRA_WEIGHTS[3]`）；`extra_chest_item` 从 S01 移到 S34 |
| G3 | **已修** | **S35**「起步包」（GEAR + POT，一次性，键 `p1_starter`，账本 `starter/starter_<slot>`），金样 `pieces` 2、`potions` 5（= `starter.heal_potions`） |
| G4 | **已修** | `/corerpg p1 givedup` 生成 `source=admin`（原 `drop`）：不可分解、不进图录；`EmberAffix.duplicateOk` 接受 drop 或 admin，所以 OP 洗练测试照常 |
| G5 | **已修** | 打包的 5 个 `ember-v1*.yml` 与线上逐字节相同（`ember-v1.yml` 补 `legacy_gate` 块与 MapV2 前缀）；`EmberSourceMapTest.bundledConfigsMatchLive` 守住 |
| G6 | **已修** | 挂机庭注释改为 rev 2 实际发的绑定材料 |
| G7 | **已修** | raids 注释改「r01 + r02 + r03 共用」（顺带 r01 / r02 `purpose` 文案「与 r02 / r03 合计」「与 r01 / r03 合计」） |
| G8 | **已修** | `ember-v1-economy.yml` `S09.per_rule: 1`；`EmberPledgeService.settleGrant` = 誓约条数 × `amount("S09","per_rule")`（值不变） |
| G9 | **已修** | p1sim 经 `sourcemap.py` 读 S33 / S35（见 §6.2）；21 格 A/B 全部 ±2pp 内 |
| G10 | **已修（D244）** | REG / `EmberEconomy` **S36** 钓鱼产出（`LIFE_ITEM`）、**S37** 扭蛋券发放（`GACHA_TICKET`）、**S38** 扭蛋抽取产出（`COSMETIC`，OUT）、**C19** 扭蛋抽取；C17 加 `LIFE_ITEM`；source map `stocks:` 块登记徽记 / 余烬徽 / 生活件 / 扭蛋券 / 外观库存（§6.2）。只打标签：无金样、无路由、数量不变；库存仍不进 sim（除徽记 D222） |
| G11 | **已修** | bv41 注释改「6 件调律版 L01/L02/L06/L08/L10/L12，L11b 不开放」 |

## 9. 未来 / 计划状态（不是现行规则）

- **D169 6 槽分阶段**：SETTLED（文档），结构代码 HOLD（`/workspace/COORD-gear-structure-hold.txt`）。Stage 1 需服主显式开工，且单独 `balance_version`。
- **Stage 0 6 槽**：设计 GO，该掉落模型 NO-GO。
- **D168 8 槽**：取消。
- **D167 随机锻造**：设计-only。
- **成长 sidegrade**：待 6 槽复核。
- **D174 主线解锁**：阶段 1 / 1.5 / 2a / 2b / 3 已上线，2c 暂缓。
- 硬约束：不按图锁部位；外观 / 粒子 / 宠物仍暂停。

## 10. 变更记录

| 日 | 项 |
|---|---|
| 2026-10-06 | D242（ARCH S4-1）：初版。合并装备结构文档；`ember-source-map.yml` + `EmberSourceMapTest`（12 测试，全套 530 / 0，JDK8）；旧文档加指针头；修 F1–F9；记 G1–G11。CoreRpg 不发版（仍 1.65.67 / bv58）。 |
| 2026-10-06 | D243（ARCH S4-2，CoreRpg 1.65.68 / bv58）：G1–G9、G11 修（S33–S35 登记、S09.per_rule、givedup src admin、打包配置同步、注释、p1sim 读 source map）；G10 仍在。`EmberSourceMapTest` 13 个，全套 533 / 0（JDK8）。 |
| 2026-10-06 | D244（ARCH S4-3，CoreRpg 1.65.69 / bv58）：G10 修（S36–S38 / C19 登记、C17 加 LIFE_ITEM、source map `stocks:` 块 + `stocksMatchEconomyAndItemConfigs`）。另：词缀原语导出表 `tools/p1sim/affix-table.json`，p1sim 读表（见 `DESIGN-ember-affix-primitives-d241.md` §3.1）。数量不变。 |
