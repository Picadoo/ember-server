# REG · Ember 计数器注册表草案（ARCH N2，2026-10-05，D203）

> 只写文档，不改代码 / 配置 / p1sim。作为 ARCH §5 **S1 状态模型**的规格。
> 来源：`CoreRpg/src/main/java/town/sunshine/corerpg/**` 里全部 `periodCount(` / `addPeriodCount(` 调用（308 处）、所有 `static final String C_*` 常量、以及直接遍历 `getCounters()` 的 5 处前缀扫描（grep，2026-10-05 18:4x，HEAD `bc52157`，CoreRpg 1.65.38）。没读线上数据（玩家 blob 在 MySQL，未取样）。

## 0. 一页结论

1. **存储模型**：`PlayerData.counters` 是一个 `Map<String,Integer>`，键 = `名字@周期`（`J/PlayerData.java:513–534`），随 `cr_players.data` 整块保存。`addPeriodCount(name, period, n)` 写新周期时**删除同名的其它周期**；值 ≤ 0 时删除键（B2.142：缺键 = 0）。
2. **数量**：P1 侧 **70 个键族**（下表 §2，外观「当前选中」6 键分开算），旧系统 **11 个**（§3），合计 81（ARCH 估「约 70」）。周期写法有 6 种：`all`、日（`yyyy-MM-dd`，`DailyService.today()`）、两种周（P1 的 `w<n>` = `EmberRunRules.rotationWeekKey` / `EmberSeason.weekKey`；旧的 `yyyy-Www` = `DailyService.weekId()`）、月（`yyyy-MM`，签到）、内容版本（首通 `@v1`）、常量 `1`（投递 / 付款标记）。还有一类把**周期位当值用**（外观「当前选中」，§2.10）。
3. **5 个物品属性挂在主人身上**（ARCH R3 说 3 个，实际 5 个）：`p4_af_<uid>` 词条、`p4_afp_<uid>` 洗练保底、`p1_sig_<uid>` 签名、`p4_rrn_<uid>` 洗练付款序号、`p4_rro_<uid>` 洗练待结算。分解 / 入装备库 / 投递都不清它们（grep：这些前缀只出现在 `EmberGrowthService` 和 `EmberRunService:1771`），所以分解后永久留在 blob 里，`EmberAudit` / `cr_p1_txn` 看不到变化，HMAC 不覆盖。S1 迁回物品时 5 个一起迁（`p4_rrn_ / p4_rro_` 是同一笔洗练事务的恢复状态，迁移时必须和 `cr_p1_txn` 的 `rerollRid` 对齐，`EmberGrowthService.recoverRolls:1497`）。
4. **资产类键**（值本身就是可花的东西或防转移的额度）：`p1_mark_t1..3` 锻造印记、`p1_sigmark_<map>` 首领徽记、`p3_badge` 余烬徽、`p1_vbound_<ni>` 账号绑定数量、`p1_afk_acc_<res>` 挂机未满 1 的累积。它们和余烬币一样是钱，但**不在 `cr_p1_txn` 流水里**，只有 `p1_mark_t` 的投递走 `EmberDelivery`（`EmberPayRules.MARK_COUNTER` 白名单）。
5. **首通版本问题（ARCH R3）确认**：`p1_first_clear_<map>@<content_version>`（`EmberRunService:1521`）。因为 `addPeriodCount` 写新周期会删同名旧周期，**改 `content_version` 后第一次重通会把旧版本的键删掉**——所以不只是「重发一次首通包」，旧版本首通记录本身也没了。读取有两条：`firstCleared` 只看当前版本（`:200`），`progressFlag` 再兜底看 `@all` 管理员桩（`:196`）。线上 10 张图全是 `content_version: v1`（`P/ember-v1-runs.yml`）。建议见 §4 第 3 条。
6. **前缀互相包含**：`p1_codex_`（图录条目）是 `p1_codex_stage_`（图录阶段奖励）的前缀。现在没有代码按 `p1_codex_` 扫描，所以没出错；S1 注册表测试应禁止「一个前缀是另一个前缀的开头」，或给这两个改名。其余前缀两两不包含（已逐对核：`p1_sig_` / `p1_sigmark_` / `p1_sign_*`、`p4_honor_` / `p4_honortest_`、`p4_rush` / `p4_rush_claim`、`p4_spec_row` / `p4_spec_resets`）。
7. **时钟回拨**：日 / 周 / 月键在服务器时钟往回调时会「换到另一个周期」并删掉当前周期的计数，等于重开上限。只有签到写了防护（`p1_sign_last`）。没有真玩家时不是问题，记在 S1 里做统一防护（§4 第 5 条）。

## 1. 列说明

- **周期**：`all` 永久；`日` = `DailyService.today()`；`P周` = `w<weekIndex>`（P1 轮换周）；`旧周` = `DailyService.weekId()`；`月` = `yyyy-MM`；`ver` = 地图 `content_version`；`1` = 常量标记。
- **类别**：`资产`（可花 / 可转化 / 防转移额度）· `领取`（幂等或上限计数，防重复领）· `进度`（解锁 / 首通 / 学会）· `物品`（属于某件装备）· `事务`（进行中的付款 / 投递恢复状态）· `设置`（玩家开关）· `统计`（只显示或估算）· `提示`（只防重复提示）· `管理`（只给管理员 / 测试）。
- **清理**：现在什么时候会消失。「同名换期」= 下一周期第一次写入时被 `addPeriodCount` 删掉；「从不」= 永久留在 blob。
- **S1 建议**：保留 / 迁物品 / 改名 / 合并 / 删。

## 2. P1 键族（70）

### 2.1 主线、团本、连战（`EmberRunService`，`EmberRunMaps`）

| 键 | 周期 | 类别 | 写入 / 读取 | 清理 | S1 建议 |
|---|---|---|---|---|---|
| `p1_first_clear_<map>` | `ver`（真实首通）/ `all`（管理员桩 `/corerpg p1 flag`，`EmberForgeService:640`） | 进度 + 领取 | 结算 `:1521` 写；`firstCleared:200`、`progressFlag:196`、锻造门 `EmberForgeService:249` 读 | 同名换期（改版本时删旧版本） | 改：拆成「首通事实」`@all`（永不删，解锁读它）+「首通包已领」`@ver`（只管发奖），见 §4-3 |
| `p1_unlock_<id>` | all | 进度 | 结算发放解锁（`:1717`，`EmberRunMaps` 的 `unlocks`）；管理员开关 `:3386` | 从不 | 保留 |
| `p1_target` | all | 设置 | 当前锻造目标 | 从不 | 保留 |
| `p1_starter` | all | 领取 | 起步包已领（`:3` 处写） | 从不 | 保留 |
| `p1_mark_t<1..3>` | all | **资产** | 结算发、`EmberPay` 扣（`:106`）、`EmberDelivery` 补发（`markCounter`）、外观商店扣（`EmberCosmetics`） | 值到 0 时删 | 保留；S2 时进流水（见 §4-4） |
| `p2_rotation` | P周 | 领取 | 精选周额外次数（`weekly_cap 3`） | 同名换期 | 保留 |
| `p2_raid_<capKey>` | P周 | 领取 | 团本周上限（`capKey` = `cap_group` 或图 key，合计上限） | 同名换期 | 保留 |
| `p2_abyss_best` | all | 统计 | 深渊最高层 | 从不 | 保留 |
| `p2_bounty` | 日 | 领取 | 每日委托 | 同名换期 | 保留 |
| `p4_vb_<kind>` | 日 | 领取 | 花样委托（D191 等）当天已结算次数 | 同名换期 | 保留 |
| `p4_rush` | P周 | 统计 | 连战尝试（D160 起只统计） | 同名换期 | 保留（或删，纯统计） |
| `p4_rush_claim` + 配置的 `p4_*` 认领键 | P周 | 领取 | 连战每周领奖一次；非主线连战必须有自己的 `p4_*` 键（`EmberRunMaps:1322` 校验） | 同名换期 | 保留；注册表里登记「配置可新增的 `p4_*` 认领键」这条规则 |
| `p1_pledge_<rule>` | all | 设置 | Q06 自选誓约（重打普通本时带上的规则） | 从不 | 保留；与旧 `CovenantService`「誓约」改名区分（ARCH O8） |

### 2.2 签名传奇、首领徽记、调律（`EmberSignature`，`EmberGrowthService`）

| 键 | 周期 | 类别 | 说明 | 清理 | S1 建议 |
|---|---|---|---|---|---|
| `p1_sig_<uid>` | all | **物品** | 这件装备的签名（`Def.code`，0 = 无）；结算盖章 `EmberRunService:1771`、烙印 `EmberGrowthService:867 / 1175`、卸下 `:1188` | 从不（分解不清） | **迁物品**（NBT `ember_v1` + `cr_p1_item` 列，进 HMAC 与流水） |
| `p1_sigmark_<map>` | all | **资产** | 首领徽记，6 条来源（重打 / 首通 / 残响 / 前哨 / 誓约 / 签到） | 值到 0 时删 | 保留；改名避开 `p1_mark_t`（ARCH O8，如 `p1_insignia_`）；S2 进流水 |
| `p1_sigfc_<map>` | all | 领取 | 首通徽记已发（每图一次，**不随**内容版本） | 从不 | 保留（这是正确的做法，首通也应照此，见 §4-3） |
| `p1_sigaltu_<def>` | all | 进度 | 调律版本已解锁（付费一次） | 从不 | 保留 |
| `p1_sigalt_<def>` | all | 设置 | 当前用调律版本（1）还是原版（0） | 值 0 删 | 保留 |
| `p1_sigoff_<slot>` | all | 设置 | 玩家在菜单里关掉该槽签名 | 值 0 删 | 保留 |
| `p1_sigseen_<def>` | all | 进度 | 图录「获得过」；另有一处兜底扫描 `p1_sig_*` 值（`:718–721`） | 从不 | 保留；迁物品后去掉兜底扫描 |

### 2.3 成长：天赋专精、勋记、挑战首通、洗练（`EmberGrowthService`，`EmberPayRules`）

| 键 | 周期 | 类别 | 说明 | 清理 | S1 建议 |
|---|---|---|---|---|---|
| `p4_spec_learn_<node>` | all | 进度 | 节点已学（币只付一次） | 从不 | 保留 |
| `p4_spec_row<row>` | all | 设置 | 该行当前选中的节点 | 从不 | 保留（注意前缀没有下划线分隔，`p4_spec_row1` …） |
| `p4_spec_resets` | all | 统计 + 领取 | 重置次数（首次免费） | 从不 | 保留 |
| `p4_chal_<map>` | all | 进度 + 领取 | 挑战难度首通（D141 天赋点来源）；有两处前缀扫描（`:122`、`:429`） | 从不 | 保留 |
| `p4_honor_<id>` | all | 提示 | 「勋记解锁」只提示一次 | 从不 | 保留 |
| `p4_honortest_<id>` | all | 管理 | 管理员测试授予，视为条件达成 | 从不 | 保留但标 `admin`；注册表测试应确认普通玩家路径不写它 |
| `p4_af_<uid>` | all | **物品** | 词条（`code*10 + tier`） | 从不 | **迁物品** |
| `p4_afp_<uid>` | all | **物品** | 洗练保底（连续低于品质上限的次数） | 从不 | **迁物品** |
| `p4_rrn_<uid>` | all | **物品 / 事务** | 这件装备已付款的洗练序号 n | 从不 | **迁物品**（与 `cr_p1_txn` 的 `rerollRid(uid, n)` 一起） |
| `p4_rro_<uid>` | all | **事务** | 已付款但结果未落地（`n*2 + lock`）；登录时 `recoverRolls` 按前缀扫描恢复 | 结果落地后清 0 | 迁物品或迁到 `cr_p1_txn` 行状态；恢复逻辑跟着走 |

### 2.4 赛季、周目标、余烬徽（`EmberSeason`）

| 键 | 周期 | 类别 | 说明 | 清理 | S1 建议 |
|---|---|---|---|---|---|
| `p3_goal_<goal>` | P周 | 进度 | 周目标进度 | 同名换期 | 保留 |
| `p3_goalpay_<goal>` / `p3_goalpay_all` | P周 | 领取 | 单项 / 全勤奖已领 | 同名换期 | 保留；`all` 是保留字，注册表应禁止目标 id 叫 `all` |
| `p3_badge` | all | **资产** | 余烬徽余额（D142；节日也写，`EmberFestival`） | 值 0 删 | 保留；S2 进流水 |
| `p3_grad` | all | 进度 | 本人 Q07 首通的纪元日（D134） | 从不 | 保留 |
| `p3_season_<award>` | all | 统计 + 领取 | 赛季奖获得次数（外观商店当解锁条件读） | 从不 | 保留 |
| `p3_seasonlast_<award>` | all | 领取 | 最近一次获得的赛季号（防同季重复） | 从不 | 保留 |

### 2.5 签到与在线时长（`EmberSignService`，D180 rev 2）

| 键 | 周期 | 类别 | 说明 | 清理 | S1 建议 |
|---|---|---|---|---|---|
| `p1_sign_mask` | 月 | 领取 | 位图：第 d 天已签（含补签） | 同名换期 | 保留 |
| `p1_sign_mk` | 月 | 领取 | 本月补签次数 | 同名换期 | 保留 |
| `p1_sign_mkd` | 日 | 领取 | 今天已补签 | 同名换期 | 保留 |
| `p1_sign_last` | all | 领取 | 最近真实签到日 `yyyymmdd`（时钟回拨防护） | 从不 | 保留；推广成通用防护（§4-5） |
| `p1_on_min` | 日 | 进度 | 今日计入的在线分钟（挂机区计入，上限 120） | 同名换期 | 保留 |
| `p1_on_claim` | 日 | 领取 | 位图：第 i 档在线奖已领 | 同名换期 | 保留 |
| `p1_on_last` | all | 事务 | `p1_on_min / p1_on_claim` 属于哪一天（跨天结算用） | 从不 | 保留 |

发奖本身另走 YAML 账本（`SIGN_RUN = "p1sign-"`、`ON_RUN = "p1online-"`，`rs.grantRow`），不是计数器。

### 2.6 挂机庭（`EmberAfkService`，D177 rev 2）

| 键 | 周期 | 类别 | 说明 | 清理 | S1 建议 |
|---|---|---|---|---|---|
| `p1_afk_kill` | 日 | 领取 | 今日计奖击杀（在线 + 离线） | 同名换期 | 保留 |
| `p1_afk_offk` | 日 | 领取 | 今日离线补算击杀 | 同名换期 | 保留 |
| `p1_afk_acc_<res>` | 日 | **资产（零头）** | 每种资源未满 1 个的累积（单位 1/daily_kills） | 同名换期（**跨天丢零头**） | 保留；注册表注明「跨天丢弃零头是设计」 |
| `p1_afk_quit` | all | 事务 | 上次下线的纪元分钟（离线补算起点） | 覆盖写 | 保留 |
| `p1_afk_kpm` | all | 统计 | 实测每分钟击杀 ×100（EMA，离线补算用） | 覆盖写 | 保留 |
| `p1_afk_fmin` | all | 统计 | 实测自动战斗分钟（封顶 10000） | 覆盖写 | 保留 |
| `p1_afk_lt` | all | 设置 / 统计 | 上次自动战斗的层 | 覆盖写 | 保留 |

挂机发奖另走账本前缀 `p1afk-`（不是计数器）。`bmat:` 只是奖励字符串格式（`EmberAfkService:594`、`EmberRunRules:418`），`failrefund@<day>` 是运行账本 id（`EmberRunRules:373`），二者都**不是**计数器键——ARCH R3 把它们列进前缀表，这里更正。

### 2.7 仓库、装备库（`EmberVault`，`EmberGearLib`）

| 键 | 周期 | 类别 | 说明 | 清理 | S1 建议 |
|---|---|---|---|---|---|
| `p1_vbound_<ni>` | all | **资产（绑定额度）** | 材料仓里不能离开账号的数量；旧 `WarehouseService` 也读（`WarehouseService` 取出只扣绑定部分，D177） | 值 0 删 | 保留；S1 起只允许 `EmberVault` 写（旧类只读），配合 S0-10 |
| `p5_vault_off` | all | 设置 | 自动入仓关 | 值 0 删 | 保留 |
| `p5_glib_mode` | all | 设置 | 装备库模式（1 总是收 / 2 关） | 值 0 删 | 保留 |

### 2.8 投递与付款标记（`EmberDelivery`，`EmberPay`，`EmberForgeService`）

| 键 | 周期 | 类别 | 说明 | 清理 | S1 建议 |
|---|---|---|---|---|---|
| `p1dlv_<deliveryId>` | 1 | **事务** | 本地已发、DB 未确认时的「已发」标记（防重发） | 确认后清 0 | 保留；注册表标「必须同 tick 存盘」 |
| `p1paid_<hex(hashCode(rid))>` | 1 | **事务** | 锻造扣费与退款对账：有它才算真付过（D162） | 退款时清 | 保留；32 位 `hashCode` 理论上可碰撞（两个 rid 撞同一标记 → 一笔退款被另一笔的标记放行）。概率极低，S1 改用完整 rid 或 64 位哈希 |

### 2.9 图录（`EmberCodex`）

| 键 | 周期 | 类别 | 说明 | 清理 | S1 建议 |
|---|---|---|---|---|---|
| `p1_codex_<family>_<slot>_t<tier>` | all | 进度 | 见过这件基础件 | 从不 | 改名（如 `p1_cdx_e_`），避免与下一行前缀包含 |
| `p1_codex_stage_<n>` | all | 领取 | 图录阶段奖已领 | 从不 | 改名（如 `p1_cdx_s_`） |

### 2.10 外观（`EmberCosmetics`，**已暂停**，只登记不改）

| 键 | 周期 | 类别 | 说明 |
|---|---|---|---|
| `p2_cosbuy_<id>` | all | 进度 | 已购买 |
| `p2_title_<raid>` | all | 统计 + 进度 | 团本通关次数（称号解锁；节日也写） |
| `p2_colorsel` / `p2_flairsel` / `p2_glowsel` / `p2_animsel` / `p2_titlesel` / `p2_trailsel` | **周期位 = 选中的 id**，值 1 | 设置 | `selected()` 按 `name@` 前缀扫描取周期位（`:215–220`）。这是把值塞进周期位的特例，注册表要单列「值在周期位」类型，S1 若做通用周期校验要排除它们 |

### 2.11 节日（`EmberFestival`，D146 / 国庆）

| 键 | 周期 | 类别 | 说明 |
|---|---|---|---|
| `p3_fest_entry_<id>` | 日 | 领取 | 今日节日活动次数 |
| `p3_fest_charm_<id>` / `p3_fest_charmon_<id>` | all | 进度 / 设置 | 节日饰品拥有 / 佩戴 |
| `p3_fest_trail_<id>` | all | 进度 | 节日拖尾（外观，暂停） |
| `p3_fest_xbadge_<event>` | all | 领取 | 该活动已换过余烬徽（D146 上限） |

活动结束后这些键从不清；数量随活动数线性增长（每个活动几项），可以接受，注册表标「活动键：可在活动下线 N 个月后清理」。

## 3. 旧系统键（11，仍会被写）

| 键 | 周期 | 写入者 | P1 开着时是否还会写 |
|---|---|---|---|
| `afk_coin`、`afk_<item>` | 日 | `CoreRpgPlugin.onDeath`（旧击杀币封顶）、`AfkTierService` | S0-4（D200）后 P1 下旧击杀币不发 → 基本不写 |
| `arena_coin_matches` | 日 | `ArenaService`（D201 S0-5） | 竞技场 P1 下被 S0-3 关 → 不写 |
| `calamity_weekly_first` | 旧周 | `CalamityService` | S0-4 后公共灾厄结算不发 → 不写 |
| `ever_forge` / `ever_anvil` / `ever_enchant` | all | 旧 `ForgeService` / `QuestService`（旧任务条件） | 旧命令 P1 下关 → 不写 |
| `life_<id>`、`life_xp` | 日 / 旧周 / all | `LifeService`（生活技能） | 仍可写（生活菜单在 P1 入口里，D202）；不发 P1 资产 |
| `mig_quest_1110` | all | `QuestService` 一次性迁移标记 | 只读 |
| `abyss_run_floor` | all | 旧 `AbyssSettleService` | 旧深渊 P1 下关 → 不写 |

旧键与 P1 键之间没有前缀冲突（P1 全部以 `p1_ / p2_ / p3_ / p4_ / p5_ / p1dlv_ / p1paid_` 开头）。

## 4. 给 S1 的规格（建议，按顺序）

1. **`EmberCounters` 注册表类**（纯 Java，无 Bukkit 依赖）：每个键族一行 = 前缀、所属类、周期类型（上面 6 种 + 「值在周期位」）、类别、是否资产、谁可写、清理规则。所有 `C_*` 常量改为引用注册表条目。
2. **注册表单测**：扫描 `src/main/java` 里 `periodCount(` / `addPeriodCount(` 的第一个参数和 `"p[0-9]_…"` 字面量，未登记的前缀 → 失败；任意两个前缀互相包含 → 失败（先给 `p1_codex_` 改名，或在注册表里显式豁免）；配置里的 `rush.claim`（`EmberRunMaps:1211`）在加载时也要查注册表。
3. **首通拆分**（修 R3，仍不动数值）：真实首通写两个键——`p1_first_clear_<map>@all`（事实，永不删，解锁 / 门槛 / 图录只读它）+ `p1_fcpay_<map>@<ver>`（首通包已领）。改 `content_version` 时只会重发首通包，不会让解锁倒退或删历史；是否真要「改版本重发首通包」由那次改动的设计自己决定（参照 `p1_sigfc_` 已经是「每图一次、不随版本」）。读取兼容：旧键 `@v1` 视为两键都有（测试号一次性迁移，服主 10-02：无老玩家）。
4. **5 个物品键迁回物品**：`p4_af_ / p4_afp_ / p1_sig_ / p4_rrn_` 进 `ember_v1` NBT + `cr_p1_item` 列并纳入 HMAC；`p4_rro_` 进 `cr_p1_txn` 行状态。动到物品 / 洗练恢复 / 分解路径 → 必须跑 `tools/p1map/persist-roundtrip.sh`（POLICY 01:53）。资产计数（`p1_mark_t*`、`p1_sigmark_*`、`p3_badge`）的增减写一条 `cr_p1_txn`，供 `EmberAudit` 对账——这一步属于 S2（与 N3 来源表一起）。
5. **统一时钟回拨防护**：`PlayerData` 记最近一次写入的日 / 周 / 月；新周期比记录还早时拒绝换期（沿用 `p1_sign_last` 的做法），只对 `领取` 类生效。
   → 已完成（D207，CoreRpg 1.65.41）：防护放在 `PlayerData.periodCount` / `addPeriodCount` 一处，只管周期性领取类键族（`EmberCounters.clockGuarded`）；旧周期读作饱和、写入被拒。
6. **两种周写法收敛**：P1 用 `w<n>`，旧系统用 `yyyy-Www`。P1 内部已统一；旧系统不改（S0 后不再写），注册表里标明即可。
7. **验证**：`balance_version` 不变、p1sim 输出逐位不变；单测覆盖注册表扫描与首通兼容读取；短冒烟（Q01 首通 → 解锁 Q02，重启后仍解锁）；第 4 条才需要 persist-roundtrip。

## 5. 计数

| 组 | 键族数 |
|---|---|
| 主线 / 团本 / 连战 | 13 |
| 签名 / 徽记 / 调律 | 7 |
| 成长 | 10 |
| 赛季 | 6 |
| 签到 / 在线 | 7 |
| 挂机 | 7 |
| 仓库 / 装备库 | 3 |
| 投递 / 付款 | 2 |
| 图录 | 2 |
| 外观（暂停） | 8 |
| 节日 | 5 |
| 旧系统 | 11 |
| **合计** | **81**（P1 70 + 旧 11；配置里可新增的 `p4_*` 连战认领键不单列） |
