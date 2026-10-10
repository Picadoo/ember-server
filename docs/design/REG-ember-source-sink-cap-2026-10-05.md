# REG · 来源 / 消耗 / 上限总表（ARCH N3 · S2 规格）

> 2026-10-05 · D204 · 只写文档，不改代码 / 配置 / p1sim。对照 ARCH §6 N3：「从 `EmberRunRules`、`ember-v1*.yml`、`EmberSignature`、`EmberSignService`、`EmberAfkService` 读出每条发放与消耗，对照 `tools/p1sim/p2econ.py` 实际建模了哪些，列出未建模项（含 §3 O1–O3 的旧来源），作为 S2 的规格。」
>
> 读源：`CoreRpg` 1.65.38 / `balance_version` 57；`plugins/CoreRpg/ember-v1.yml` · `ember-v1-runs.yml` · `ember-v1-growth.yml` · `ember-v1-festival.yml`；`plugins/CoreGacha/config.yml`；`tools/p1sim/{p1sim,p2econ,afk,signin,rushsim,festsim,realcost,mainline,rules}.py`。旧路径以 AUDIT（D197）+ S0-1～S0-5（D198–D201）为准。

## 0. 一页结论

1. **P1 主循环已基本进模型**：主线结算（币 / 材料 / 经验 / 印记 / 装备）、首通包、词缀 / 事件加料、委托、挑战 / 深渊 / 团本、精选周印记、连战、挂机庭、签到 / 在线、天赋 / 洗练 / 勋记、国庆本，都有对应的 `tools/p1sim` 脚本读真实配置。
2. **S2 真正的缺口在「旁路」和「上限」两处**：
   - **旁路未建模**（真人生效、p1sim 不算）：首领徽记的 6 条来源里有 4 条（首通徽记、重打徽记、誓约、残响）进不了 `p1sim` / `p2econ`；烙印 / 调律消耗；生活玩法币 / 核心；生活产出的旧材料；扭蛋兑券；外观商店徽 / 币。
   - **上限分散**：同一种资源有自己的日 / 周 / 月 / all 计数器（见 N2），但「全服经济」没有一张「谁发多少、谁吃多少、封顶多少」的运行时表——本文件就是它的离线规格。
3. **旧来源（O1–O3）在路由层已关（S0-1～S0-5）**：玩家入口默认拒绝；纵深防御关掉旧经验 / 击杀币。**p1sim 本来就没建模它们**——这是对的；S2 只需保证「关了之后不会再开」，并把剩余白名单（`quest` 旧线已停、`life` / `mail` / `warehouse`）里仍能动币 / 材料的口子写进本表。
4. **S2 实现建议（不在本窗做）**：抽一个 `EmberEconomy`（或扩展 `EmberRunRules.Grant`）统一 `source → account → sink`，p1sim 只读同一份 yaml 表；未进表的发放 / 消耗一律拒。

图例：

| 列 | 含义 |
|---|---|
| **模型** | `✓` = 该脚本读真实配置并计入账户；`△` = 部分 / 环境开关 / 上界近似；`✗` = 完全没建模 |
| **上限键** | `PlayerData` 计数器族（N2）；`—` = 无独立计数（只靠日体力 / 日击杀等间接） |
| **周期** | `run` = 每局；`day` = 体力日 / 日历日（Asia/Shanghai）；`week` = 周锚；`month` = 月；`all` = 永久；`once` = 每图 / 每内容版本一次 |

---

## 1. 账户（资产列）

| 账户 | 存哪 | 单位 | 备注 |
|---|---|---|---|
| 余烬币 | `PlayerData.coin`（与旧系统同字段） | 整数 | CoreGacha 反射读写 |
| 余烬经验 / 等级 | `ProgressService`（`progress.yml ember_xp.curve`） | 整数 / 等级 | P1 用 `grantFlatEmberXp`；等级仍是旧本门槛（已由 S0 封口） |
| 碎片 / 核心碎片 / 骨尘 / 胚料 | NI → `EmberVault` → `cr_warehouse` | 整数 | 白名单 `ember-v1.yml storage.vault` |
| 绑定材料 | `bmat:` 账本 → `p1_vbound_<id>` | 整数 | 挂机庭材料；不能取出 |
| 锻造印记 T1–T3 | `p1_mark_t<n>` | 整数 | 8 枚兑换一件 |
| 首领徽记（每图） | `p1_sigmark_<map>` | 整数 | 烙印 5 / 调律 10 |
| 余烬徽 | `p3_badge` | 整数 | 只买外观 / 兑扭蛋券 |
| 体力 | `StaminaService` + `cash.yml stamina` | 整数 | `base_max 90`；月卡 +30 |
| 扭蛋券 / 光屑 / 火花 | CoreGacha 表 | 整数 | 独立库表 |
| 国庆币 | NI `ember_fest_coin_gq26` | 整数 | 活动期内 |

---

## 2. 来源总表（P1）

### 2.1 主线 / 挑战 / 深渊 / 团本（`EmberRunRules` + `EmberRunService`）

| # | 来源 | 发什么 | 数量 / 规则 | 周期 / 上限 | 上限键 | 模型 |
|---|---|---|---|---|---|---|
| S01 | 主线 / 挑战结算基线 | 币 / 碎片 / 骨尘 / 核心 / 经验 / 印记(本阶) / 装备 1 | `BASE_*`：300 / 24 / 6 / 2 / 120 / 1；装备按品质权重 | `run`；体力 30 / 局（挑战同） | 体力日 | ✓ `p1sim` / `p2econ` |
| S02 | 宝箱怪 | 币 | +100 | `run` 内触发 | — | ✓ `p1sim` |
| S03 | 精英房 | 碎片 / 核心 | +10 / +1 | `run` 内触发 | — | ✓ `p1sim` |
| S04 | 词缀精英结算 | 碎片 | `variety.affix_shard` = 2 | 仅全员已首通的普通重打 | — | ✓ `p1sim` |
| S05 | 房间事件达标 | 核心 | `variety.event_core` = 1 | 同上 | — | ✓ `p1sim` |
| S06 | 首通包 | 自选装备或材料包 | Q01 自选族 T1 护符；Q02 一次免费定向兑换（自选族 × 部位，T1 刃或护符，`choice: piece`，D110）；Q03–Q07 材料+币（见下） | `once` / 内容版本 | `p1_first_clear_<map>@<ver>`（N2：换版会删旧键） | ✓ `p1sim`（材料/币）；自选走脚本策略 |
| S07 | 首通徽记 | 首领徽记 | 3 / 图 | `once` / 图（不跟 content_version） | `p1_sigfc_<map>` | ✗（`p1sim` 不计徽记库存） |
| S08 | 重打普通主线（签名图） | 首领徽记 + 12% 签名烙印在基装上（仅当该图有适配基装部位 / 家族的签名时） | 徽记 1；`STAMP_RATE=0.12`（无适配签名 = 0） | `run`（需已首通该图） | `p1_sigmark_<map>` 累加 | ✗ 徽记；签名掉落 △ `mainline.py` |
| S09 | 自选誓约 | 首领徽记 | +1 / 条已誓约规则（D243：E1 `S09.per_rule` = 1）（池里 `normal: true` 目前 lean / reverse = 最多 +2） | `run`：队长本人已首通 **Q06**；本局无周规则（`mod == null`）；可誓约 **Q01–Q07** 签名图；队长已誓约且全员已首通该图 | `p1_pledge_<id>`（all） | △ `insignia.py --pledge` 上界（不计战斗惩罚）；`P1_PLEDGE=` 环境上界 |
| S10 | 精选周挑战加印 | 印记(本阶) | +1；每周最多 3 次（与 S11 共用） | `week` × 3 | `p2_rotation` / 周 | ✓ `p2econ` / `p1sim` |
| S11 | 精选周普通重打加印 | 印记(地图阶，仅 T1/T2) | +1；与 S10 共用周上限 3；本人已通 Q07 后不再发也不占名额 | `week` × 3 | 同上 | ✓ `p1sim --normal-mods` |
| S12 | 团本结算 | T3 装备(地板精良) + 印记 T3 ×1 | `raid_item` + `raid_mark`；无首通包 | `week` × 3（R01–R03 共用 `cap_group: raid`）；体力 50 | `p2_raid_<group>` / 周 | ✓ `p2econ --raid` |
| S13 | 深渊层结算 | 同 S01 基线数量 + 层品质表 | 无首通 / 无精选加印；层费见 C08；**D229 起 `sourceForGrant(base_*, <map>a<n>-…)` → S13**（数量仍读 S01） | 层 1–10（10 层硬上限，无无尽层） | `p2_abyss_best` | ✓ `p2econ --abyss` |
| S14 | 失败退体力 | 体力 | 当天首次挑战 / 深渊失败退 `fail_refund=0.5` × 30 | `day` × 1 | `failrefund@<day>`（账本，非 counters） | ✓ `p2econ` |
| S15 | 当日首次倒下退药 | 回复药（绑定） | 最多 `death_refund.max_potions=5` | `day` × 1 | 账本行 | ✓ `p1sim` |

**首通包明细（S06）**（`ember-v1-runs.yml maps.*.first_clear`）：

| 图 | 包 |
|---|---|
| Q01 | 自选 T1 护符 |
| Q02 | 自选 T1 刃（piece） |
| Q03 | 碎片 30 + 核心 4 + 币 600 |
| Q04 | 胚料 6 + 核心 6 + 币 2100 |
| Q05 | 骨尘 20 + 胚料 6 + 币 900 |
| Q06 | 核心 10 + 币 1200 |
| Q07 | 胚料 12 + 核心 12 + 币 1800 |

### 2.2 周期玩法（连战 / 前哨 / 残响 / 周目标 / 委托）

| # | 来源 | 发什么 | 数量 / 规则 | 周期 / 上限 | 上限键 | 模型 |
|---|---|---|---|---|---|---|
| S16 | 余烬连战（rush） | T3 印记 1 + 余烬徽 20 + 称号 | 每周首次结算通关领奖；失败可无限重试；不扣体力 | `week` × 1 领奖 | `p4_rush_claim`（尝试 `p4_rush`） | ✓ `p2econ --rush` / `rushsim`（印记）；徽 ✗ |
| S17 | 连战·前哨（outpost） | 每条链图徽记 2 + T2 印记 1 | 每周首次结算；之后练习无奖；需 Q05 首通 | `week` × 1 | `p4_outpost_claim` | △ `P1_OUTPOST` 只加 T2 印记上界；徽 ✗ |
| S18 | 首领残响（echo_q01–q07） | 该图徽记 ×2 / 次 | 七条入口共用每周 3 次领奖；Q01–Q04 需 Q04 首通，Q05–Q07 需本图首通（D227 option B；周发放总量不变） | `week` × 3 | `p4_echo_claim` | ✗ |
| S19 | 周目标 | 余烬徽 | 每项目标 15；全满再 +20（合计最多 15×5+20=95 / 周）；仅本人已通 Q07 | `week`；毕业周委托目标按剩余天数折算 | 周目标进度（见 N2 REG） | ✓ `p2econ --goals`（行为）；徽本身不进战力模型（设计如此） |
| S20 | 每日委托 | 币 / 碎片 | 1 局：30 币；3 局：60 币 + 碎片 6 | `day` | `p2_bounty`（局数） | ✓ `p1sim.bounty` |
| S21 | 花样委托 | 币 | 词缀精英 ×2 → 20；房间事件 ×1 → 20 | `day` | `p4_vb_<kind>` | ✓ `p1sim` |

### 2.3 挂机庭 / 签到 / 在线（`EmberAfkService` / `EmberSignService`）

| # | 来源 | 发什么 | 数量 / 规则 | 周期 / 上限 | 上限键 | 模型 |
|---|---|---|---|---|---|---|
| S22 | 挂机庭击杀 | 币 / 经验 / 绑定碎片·骨尘·核心·胚料 | 打满 `daily_kills=2400` 按层：币 60–120、经验 10–25、材料见 `afk.tiers`；离线 = 实测速 × 0.25，日最多 1200 只 | `day` 击杀封顶 2400（在线+离线） | `p1_afk_kill` / `p1_afk_offk` / `p1_afk_acc_<res>` + `bmat:` 账本 | ✓ `afk.py` |
| S23 | 每日签到 | 币 / 经验 / 印记 / 徽记 | 普通格 20 币 + 5 经验；第 7/21 次：40 币 + 徽记 1 → **本人已首通的最高签名图**（`EmberSignService.sigMap`；尚无任一首通签名图 → `sigmark_fallback_coin` 60 币）；第 14/28 次：40/80 币 + 印记 1；第 29–31：10 币 + 5 经验 | `month` 累计格；补签 ≤ 3 / 月且需当日在线 ≥ 60 | `p1_sign_mask` / `p1_sign_mk` / `p1_sign_mkd` | ✓ `signin.py`（徽记钩 `signin.INS_HOOK`） |
| S24 | 在线时长档 | 币 / 经验 | 15′→10 币；30′→15+5xp；60′→20+5xp；120′→25+10xp（合计 70 币 + 20 xp / 日）；挂机庭分钟计入；挂机战斗算活动 | `day`；过 0 点未领自动补发 | `p1_on_min` / `p1_on_claim` | ✓ `signin.py` |

### 2.4 成长旁路 / 活动 / 其它

| # | 来源 | 发什么 | 数量 / 规则 | 周期 / 上限 | 上限键 | 模型 |
|---|---|---|---|---|---|---|
| S25 | 勋记（honors） | 结算时额外币% / 碎片 | 挑战 3/7、团本 1/3、深渊 5/10、图录全满 → `coin` 最高 ×1.04、`shard_bonus` +1 等（`growth.yml honors.caps`） | `all`（达成即永久） | 由挑战 / 团本 / 深渊 / 图录进度推出（`p4_chal_` 等） | ✓ `p1sim`（`GROWTH`）/ `realcost` |
| S26 | 国庆本 gq26 | 国庆币 | 清房 / 精英 / 宝箱 / 通关：见 `ember-v1-festival.yml drops`；日进本 3 | 活动窗 2026-10-03～10-08；日 3 次 | 活动进本计数（见 N2 REG） | ✓ `festsim` / `p1sim` fest |
| S27 | 国庆兑换徽 | 余烬徽 | 国庆币 → 徽，比率 5，帽 40 | 活动期 | 兑换帽（见 N2 REG） | △ `festsim`（兑换）；徽库存 ✗ |
| S28 | 8 印记兑换 | 装备（标准成色） | 花 8 枚同阶印记换同族同部位一件 | 无次数上限（印记存量） | — | ✓ `p1sim` 兑换策略 |
| S29 | 分解装备 | 胚料 | 仅 `source=drop` 的 T1–T3：产量 = tier | — | — | ✓ `p1sim` |
| S30 | 生活玩法产出 | 回复药 / 重铸石 / 稳固符 / 魂尘 / 宠物蛋 / 食物 | 见 `life.yml`；稳固符吃 3 核心 + 300 币 / 周 1；宠物蛋 600 币 + 10 魂尘 / 周 1 | 日 / 周 offer 上限 | `life_<id>` | ✗ |
| S31 | 旧任务线 | 经验 / 旧材料 / 体力药 | `quest.yml` 合计约 9560 平坦经验 + 多段 `mat_ember_*`；**P1 开着时旧链不推进（`QuestService:238` `EmberMode.active()` 即 return，`:465` 引导改走 P1）**【码，未单独实测】 | — | — | ✗（且 P1 下死） |
| S32 | 邮件附件 | 币 / NI | 通道；P1 下旧 `pass free/claim` 入口已拒，残留是 OP / 控制台发件 | — | — | ✗ |
| S33 | 图录阶段奖励（D243，原 G1 / X01） | 币 | 集齐 5 / 10 / 15 / 20 种：200 / 400 / 600 / 1000（`EmberCodex.STAGE_COIN`；E1 `S33.at5.coin`…`at20.coin`）；`/corerpg p1 codex claim`，账本 `codex/stage<n>` → 带标签 `grantCoin(S33)` | 每角色每阶段一次（`p1_codex_stage_<n>@all`） | `p1_codex_stage_` | ✓ `p1sim`（D243 `sourcemap.py` `codex_stage`） |
| S34 | 宝箱额外装备（D243，原 G2，从 S01 拆出） | 装备 1 | 宝箱 5 % 额外一件（`EXTRA_WEIGHTS[3]`；E1 `S34.chest_weight`）；账本键 `extra_chest_item` | `run` | — | ✓ `p1sim`（原 S01 内） |
| S35 | 起步包（D243，原 G3 / X02） | T0 刃 + T0 护符（`source=quest`，不可分解）+ 回复药 5 | E1 `S35.pieces` 2、`S35.potions` 5（= `ember-v1.yml starter.heal_potions`）；账本 `starter/starter_<slot>` | 每角色一次（`p1_starter@all`） | `p1_starter` | ✓ `p1sim`（D243 `sourcemap.py` `starter_kit`） |
| S36 | 钓鱼产出（D244，G10；CoreFish） | 鱼 / 宝藏 / 垃圾 NI 件（`fish_ember_*` / `treasure_ember_*` / `junk_ember_*`） | `plugins/CoreFish/config.yml` `category_weights` + `tables`；只进背包，不进 vault 白名单；卖 / 熬药走 C17 | 无周期帽（体力外；靠钓鱼时长） | — | ✗（`EmberEconomy` 登记，`LIFE_ITEM`，标签 only，无金样，无路由） |
| S37 | 扭蛋券发放（D244，G10；CoreGacha） | 扭蛋券（CoreGacha 钱包） | `CoreGacha/config.yml tickets`：`welcome` 5；当日在线 30 / 90 分钟各 1（`afk_worlds` 不计）；当日委托结算 1 / 3 局各 1；实物券 `ember_gacha_ticket` 右键入包。兑换（币 / 徽）仍是 C16 | 日 | — | ✗（`GACHA_TICKET`，标签 only，无金样） |
| S38 | 扭蛋抽取产出（D244，G10；CoreGacha） | 外观（badge / tag / aura / pet / show / corerpg 外观店件） | `CoreGacha/gacha.yml items`，只放外观、无属性 | 日（随 C19 抽数） | — | OUT（外观不进战力模型） |
| S39 | 六槽护甲掉落（D318） | 护甲 1 / 局 | 开关 `gear.six_slot.enabled`；默认关时不发 | `run` | — | △ PART（p1sim SIX） |
| S40 | 短征通关 sx01（D391） | 币 / 碎片 / 骨尘 / 胚料 | 有奖通关：80 / 4 / 3；生涯首通另加 200 / 8 / 胚料 1；日有奖帽 3（`p1_sx01_day`）；帽后无奖仍可进 | `day` × 3 有奖 | `p1_sx01_day` | ✗（p1sim 另号；E1 `ember-v1-economy.yml` S40） |
| S41 | 短征通关 sx02（D392） | 币 / 碎片 / 骨尘 / 胚料 | 有奖通关：80 / 4 / 3（同 S40 量级）；生涯首通另加 180 / 6 / 胚料 1（略薄）；日有奖帽 3（`p1_sx02_day`，与 sx01 分开）；帽后无奖仍可进 | `day` × 3 有奖 | `p1_sx02_day` | ✗（p1sim 另号；E1 `ember-v1-economy.yml` S41） |
| S42 | 短征通关 sx03（D393） | 币 / 碎片 / 骨尘 / 胚料 | 有奖通关：80 / 4 / 3（同 S40/S41 量级）；生涯首通另加 160 / 8 / 胚料 1（略薄）；日有奖帽 3（`p1_sx03_day`，与 sx01/sx02 分开）；帽后无奖仍可进 | `day` × 3 有奖 | `p1_sx03_day` | ✗（p1sim 另号；E1 `ember-v1-economy.yml` S42） |
| S43 | 短征通关 sx04（D397） | 币 / 碎片 / 骨尘 / 胚料 | 有奖通关：80 / 4 / 3（同 S40–S42 量级）；生涯首通另加 150 / 6 / 胚料 1（略薄）；日有奖帽 3（`p1_sx04_day`，与 sx01–sx03 分开）；帽后无奖仍可进 | `day` × 3 有奖 | `p1_sx04_day` | ✗（p1sim 另号；E1 `ember-v1-economy.yml` S43） |
| S44 | 短征通关 sx05（D400） | 币 / 碎片 / 骨尘 / 胚料 | 有奖通关：80 / 4 / 3（同 S40–S43 量级）；生涯首通另加 140 / 6 / 胚料 1（略薄）；日有奖帽 3（`p1_sx05_day`，与 sx01–sx04 分开）；帽后无奖仍可进 | `day` × 3 有奖 | `p1_sx05_day` | ✗（p1sim 另号；E1 `ember-v1-economy.yml` S44） |
| S45 | 短征通关 sx06（D403） | 币 / 碎片 / 骨尘 / 胚料 | 有奖通关：80 / 4 / 3（同 S40–S44 量级）；生涯首通另加 130 / 6 / 胚料 1（略薄）；日有奖帽 3（`p1_sx06_day`，与 sx01–sx05 分开）；帽后无奖仍可进 | `day` × 3 有奖 | `p1_sx06_day` | ✗（p1sim 另号；E1 `ember-v1-economy.yml` S45） |
| S46 | 短征通关 sx07（D407） | 币 / 碎片 / 骨尘 / 胚料 | 有奖通关：80 / 4 / 3（同 S40–S45 量级）；生涯首通另加 120 / 6 / 胚料 1（略薄）；日有奖帽 3（`p1_sx07_day`，与 sx01–sx06 分开）；帽后无奖仍可进 | `day` × 3 有奖 | `p1_sx07_day` | ✗（p1sim 另号；E1 `ember-v1-economy.yml` S46） |
| S47 | 短征通关 sx08（D410） | 币 / 碎片 / 骨尘 / 胚料 | 有奖通关：80 / 4 / 3（同 S40–S46 量级）；生涯首通另加 110 / 6 / 胚料 1（略薄）；日有奖帽 3（`p1_sx08_day`，与 sx01–sx07 分开）；帽后无奖仍可进 | `day` × 3 有奖 | `p1_sx08_day` | ✗（p1sim 另号；E1 `ember-v1-economy.yml` S47） |
| S48 | 短征通关 sx09（D412） | 币 / 碎片 / 骨尘 / 胚料 | 有奖通关：80 / 4 / 3（同 S40–S47 量级）；生涯首通另加 100 / 6 / 胚料 1（略薄）；日有奖帽 3（`p1_sx09_day`，与 sx01–sx08 分开）；帽后无奖仍可进 | `day` × 3 有奖 | `p1_sx09_day` | ✗（p1sim 另号；E1 `ember-v1-economy.yml` S48） |
| S49 | 短征通关 sx10（D414） | 币 / 碎片 / 骨尘 / 胚料 | 有奖通关：80 / 4 / 3（同 S40–S48 量级）；生涯首通另加 90 / 6 / 胚料 1（略薄）；日有奖帽 3（`p1_sx10_day`，与 sx01–sx09 分开）；帽后无奖仍可进 | `day` × 3 有奖 | `p1_sx10_day` | ✗（p1sim 另号；E1 `ember-v1-economy.yml` S49） |
| S50 | 短征通关 sx11（D417） | 币 / 碎片 / 骨尘 / 胚料 | 有奖通关：80 / 4 / 3（同 S40–S49 量级）；生涯首通另加 80 / 6 / 胚料 1（略薄）；日有奖帽 3（`p1_sx11_day`，与 sx01–sx10 分开）；帽后无奖仍可进 | `day` × 3 有奖 | `p1_sx11_day` | ✗（p1sim 另号；E1 `ember-v1-economy.yml` S50） |
| S51 | 短征通关 sx12（D419） | 币 / 碎片 / 骨尘 / 胚料 | 有奖通关：80 / 4 / 3（同 S40–S50 量级）；生涯首通另加 70 / 6 / 胚料 1（略薄）；日有奖帽 3（`p1_sx12_day`，与 sx01–sx11 分开）；帽后无奖仍可进 | `day` × 3 有奖 | `p1_sx12_day` | ✗（p1sim 另号；E1 `ember-v1-economy.yml` S51） |
| S52 | 短征通关 sx13（D421） | 币 / 碎片 / 骨尘 / 胚料 | 有奖通关：80 / 4 / 3（同 S40–S51 量级）；生涯首通另加 60 / 6 / 胚料 1（略薄）；日有奖帽 3（`p1_sx13_day`，与 sx01–sx12 分开）；帽后无奖仍可进 | `day` × 3 有奖 | `p1_sx13_day` | ✗（p1sim 另号；E1 `ember-v1-economy.yml` S52） |
| S53 | 短征通关 sx14（D423） | 币 / 碎片 / 骨尘 / 胚料 | 有奖通关：80 / 4 / 3（同 S40–S52 量级）；生涯首通另加 50 / 6 / 胚料 1（更薄）；日有奖帽 3（`p1_sx14_day`，与 sx01–sx13 分开）；帽后无奖仍可进 | `day` × 3 有奖 | `p1_sx14_day` | ✗（p1sim 另号；E1 `ember-v1-economy.yml` S53） |

### 2.5 旧来源（O1–O3，S0 后状态）

| # | 来源 | 原发放 | S0 后 | 模型 |
|---|---|---|---|---|
| L-S1 | `/dp start` / `/corerpg enter` 旧本 | P1 材料 + 旧装 + 不封顶击杀币 | S0-1/S0-2 拒进；S0-4 纵深不发旧 XP / 击杀币 / mmgive | ✗（正确：不应进 p1sim） |
| L-S2 | 竞技场对战币 / 日箱 | 胜 25 + 负 10；日箱 80 | S0-3 拒 `arena`；S0-5 即使放开也对战币日帽 5 场 | ✗ |
| L-S3 | `pass free/claim` / `vip claim` / 月卡登录币 | 日 80+碎片、战令档、勋阶 0 也 20 | S0-3 拒；`pass` 只留 show/info | ✗ |
| L-S4 | 灾厄 / 旧击杀币 | 参战奖 + 不封顶击杀币 | S0-3 拒 join；S0-4 公共灾厄不结算、非 P1 世界不发旧击杀 | ✗ |
| L-S5 | `scrap` / `reforge` / 旧 `enhance` | 旧装 → P1 材料 | S0-3 拒 | ✗ |

> S2 验收：上表 L-S* 在「`EmberMode.active()` + 非 OP」下必须继续为 0；任何重新放行都要先改本表并进 p1sim。

---

## 3. 消耗总表（P1）

| # | 消耗 | 吃什么 | 数量 / 规则 | 周期 / 上限 | 模型 |
|---|---|---|---|---|---|
| C01 | 进主线 / 挑战 | 体力 | 30 / 局 | 日体力 90（月卡 +30）→ 约 3–4 局 | ✓ |
| C02 | 进团本 | 体力 | 50 / 局；周通关帽 3 | 同上 | ✓ |
| C03 | 强化 | 碎片 / 核心 / 币 | `EmberUpgradeRules`：碎片 4…72、核心 0…6、币 40…1080（按当前强化等级） | 强化等级 0→10 | ✓ `p1sim` |
| C04 | 升阶 | 碎片 / 核心 / 胚料 / 币 | T1→T2：碎片 60 + 核心 12 + 胚料 6 + 1500 币；T2→T3：60 + 15 + 6 + 1800 币；需本人首通 Q04 / Q07 | `all`（件） | ✓ `p1sim` |
| C05 | 精工 | 胚料 / 骨尘 / 币 | 0→1：胚料 3 + 骨尘 5 + 300 币；1→2：6 + 10 + 600；2→3：12 + 20 + 1200 | 精工 0→3 | ✓ `p1sim` / `p2econ.forge_sink`（只计币） |
| C06 | 成色 | 胚料 / 骨尘 / 币 | 0→1：胚料 8 + 骨尘 8 + 800 币；1→2：16 + 16 + 1600；极品不可养成 | 成色 0→2 | ✓ 同上 |
| C07 | 8 印记兑换 | 印记 ×8 | 见 S28 | — | ✓ |
| C08 | 深渊层费 | 币（不足时可用超额 T3 印记，1 枚 = 200 币，保留 8 枚不换） | 层 2–10：60 / 120 / 180 / 240 / 300 / 360 / 400 / 440 / 480 | 每层每次进入 | ✓ `p2econ` / `abysscoin` |
| C09 | 天赋学习 | 币 | 行 1/2/3：800 / 2000 / 4000；点数预算 1+2+3 | `all`（点数） | ✓ `realcost` |
| C10 | 天赋重置 | 币 | `respec_coin: 2000` | — | △ `realcost`（有字段，默认路径少用） |
| C11 | 洗练 | 币 + 碎片（或重复件） | T1–T3：币 300/600/1000；碎片 40/80/120；锁定另加同量 `lock_shard`；保底 5 | 件上词条槽 | ✓ `realcost` / `rerollsim` |
| C12 | 烬炉烙印 | 徽记 5 + 币 `300 × tier` | 需本人首通 **Q02**，且**该签名所属主线图已首通**（`mapCleared`）；消耗该图徽记 5 + 币 `300×件阶`；把签名烙到自有件（换件丢失、不退） | — | ✓ `insignia.py`（opt-in） |
| C13 | 签名调律 | 徽记 10（`ALT_MARKS`） | 需本人首通 Q07（`ALT_UNLOCK`）；6 件调律版（L01 / L02 / L06 / L08 / L10 / L12，`EmberSignature.ALTS`；L11b 不开放）；解锁记 `p1_sigaltu_` | — | ✗ |
| C14 | 回复药购买 | 币 | `shop.heal_potion.price: 10` | — | ✓ `p1sim` 带药 |
| C15 | 外观商店 | 币 或 徽（1 徽 = 50 币标价点）或印记点 | `EmberCosmetics`：深渊称号等按层价；足迹 / 称号多为通关解锁（0 价） | — | ✗（外观不进战力；化妆品暂停） |
| C16 | 扭蛋兑券 | 币 1200 或 徽 20 / 张 | 日帽 5 张（`CoreGacha exchange.daily_cap`） | `day` × 5 | ✗ |
| C17 | 生活 offer | 币 + 材料 + 生活件（D244：账户加 `LIFE_ITEM`） | 面包 40、钓竿 60、熬药 10+鱼、稳固符 300+3 核心 / 周 1、宠物蛋 600 / 周 1 等 | 日 / 周 | ✗ |
| C18 | 国庆商店 | 国庆币 / 活动后币+徽 | 符 60 国庆币（售后 15000 币 + 300 徽）；足迹 / 纪念见 `festival.yml` | 活动期 | △ `festsim` |
| C19 | 扭蛋抽取（D244，G10；CoreGacha） | 扭蛋券 | `gacha.yml` `cost_per_pull` 1、`daily_pull_cap` 50 → 产出 S38 | 日 50 抽 | OUT（外观；标签 only） |

---

## 4. 上限一览（按账户）

| 账户 | 硬上限（配置 / 代码） | 软上限（间接） | 备注 |
|---|---|---|---|
| 余烬币 | 无账户顶 | 日：挂机满额 60–120 + 签到/在线 ≤ ~150 + 委托 90 + 主线局数 ×300；周：深渊层费 / 养成把存量压下去 | 旧竞技场无帽已由 S0-5 堵 |
| 余烬经验 | 曲线等级顶 | 日：主线局 ×120 + 挂机 10–25 + 签到/在线 | 旧 XP 源 S0-4 = 0 |
| 碎片 / 核心 / 骨尘 / 胚料 | 仓库无槽上限 | 日：主线基线 + 词缀/事件 + 委托 + 挂机绑定 | 绑定材料不能取出 |
| 印记 T1–T3 | 无 | 周：精选 +3、团本 +3、连战 +1 T3、前哨 +1 T2、签到月 2 枚 | 兑换 8 枚是主要出口 |
| 首领徽记 | 无 | 周：残响 ≤ 3×2、前哨 2×3 图、誓约 ≤ 2 / 局、重打每局 1、首通一次 3 | **烙印 / 调律是唯二出口；p1sim 完全没建模库存** |
| 余烬徽 | 无 | 周目标 ≤ 95；连战 20；国庆兑 ≤ 40 | 只出外观 / 扭蛋 |
| 体力 | `base_max 90` + 月卡 30 | 日局数 | 失败半退一天一次 |
| 扭蛋券 | 日兑 5 | — | 外观暂停期可暂缓 |
| 国庆币 | 活动结束停产 | 日进本 3 | 售后只留兑换 |

---

## 5. 模型覆盖矩阵（p1sim 族）

| 主题 | p1sim | p2econ | afk | signin | rushsim | festsim | realcost | mainline | 缺口 |
|---|---|---|---|---|---|---|---|---|---|
| 主线基线 / 首通材料币 | ✓ | ✓（经 to_q07） | | | | | | | 首通自选策略简化 |
| 词缀 / 事件加料 | ✓ | | | | | | | | |
| 委托 / 花样委托 | ✓ | ✓ | | | | | | | |
| 挑战 / 深渊 / 团本 / 精选印 | ✓ | ✓ | | | | | | | |
| 连战印记 | | ✓ | | | ✓ | | | | 徽 20 ✗ |
| 前哨 | | △ 印记上界 | | | | | | | 徽记 ✗ → D222 `insignia.py`（opt-in）+ D244 `stocks.insignia` |
| 残响 | | | | | | | | | **全无** |
| 誓约徽记 | △ 环境 | | | | | | | | 徽记库存 ✗ → D222 `insignia.py`（opt-in）+ D244 `stocks.insignia` |
| 签名掉落率 | | | | | | | | △ | 烙印 / 调律消耗 ✗ |
| 挂机 | | | ✓ | | | | | | |
| 签到 / 在线 | | | | ✓ | | | | | |
| 勋记 | ✓ | ✓ | | | | | ✓ | | |
| 天赋 / 洗练 | | | | | | | ✓ | | 重置少用 |
| 国庆 | ✓ | | | | | ✓ | | | |
| 余烬徽库存 | | 行为 ✓ | | | | | | | 库存 / 外观店 ✗；**D244 已登记**：source map `stocks.badge`（S16 / S19 / S27 → C15 / C16 / C18） |
| 扭蛋兑券 | | | | | | | | | 不建模（外观）；**D244 已登记**：S37 / C16 → C19 → S38，`stocks.gacha_ticket` / `stocks.cosmetic` |
| 生活玩法 | | | | | | | | | 不建模；**D244 已登记**：S30 / S36 → C17，`stocks.life_item`（17 个 NI 件 = life.yml + CoreFish，单测钉死） |
| 旧 O1–O3 | | | | | | | | | 故意不建；靠 S0 |

**S2 优先补进模型的缺口（建议顺序）**：

1. **首领徽记账户**（来源 S07–S09 / S17–S18 + 消耗 C12–C13）——现在「徽记够不够烙印 / 调律」无法用离线 sim 回答。
2. **残响周产出**（S18）——与前哨对称，缺了会低估 Q04 后徽记。
3. **余烬徽库存**（S16 / S19 / S27 → C15 / C16）——不影响战力，但影响「外观 / 扭蛋是否破产」；化妆品重启前再做即可。
4. **生活玩法对核心 / 币的出口**（C17 稳固符）——周 1 次 ×3 核心，长期会偷核心；P1 若保留 `life` 白名单就应进表或关掉吃核心的 offer。
5. **扭蛋兑券**（C16）——化妆品暂停期可标 `DEFER`。

---

## 6. S2 规格草案（给实现窗）

1. **单一发放入口**：所有发币 / 材料 / 印记 / 徽记 / 徽 / 经验 / 体力的代码路径改为 `EmberEconomy.grant(player, SourceId, Grant…)`；`SourceId` 必须落在本表 §2 的编号（或显式 `LEGACY_*` 且仅当 `!EmberMode.active()`）。
2. **单一消耗入口**：对称的 `EmberEconomy.spend(…, SinkId, Cost…)`；失败不写半态（沿用 `EmberPay` hold）。
3. **yaml 真源**：把 §2 / §3 的数量与上限迁到 `ember-v1-economy.yml`（或扩 `ember-v1.yml` 一节）；Java 常量 `BASE_*`、`CLEAR_MARKS` 等改为读表；`tools/p1sim/rules.py` 只读这一份。
4. **未登记即拒**：单元测试扫描 `addCoin` / `grantFlatEmberXp` / `addPeriodCount(p1_mark|p1_sigmark|p3_badge)` / vault 写入，调用栈必须经过 `EmberEconomy`（白名单测试钩子除外）。
   → 进度：`addCoin`（D216）/ `takeCoin`（D218）/ **`addPeriodCount(p1_mark_t|p1_sigmark_|p3_badge)` + `grantFlatEmberXp`（D228）** / **vault 写入 `autoDeposit`/`creditBound`/`credit`/`EmberVault.get().give`（D229，行级扫描，例外行须带 `econ-ok:` 理由）** 已上。
5. **旧路径**：保持 S0-1～S0-5；本表 §2.5 作为回归清单；重新放行必须同时加 p1sim 行。
6. **不做的事（本阶段）**：不改数值、不加永久战力、不重开宝石、不推化妆品；生活 / 扭蛋可先登记为 `DEFER` 或在 P1 白名单里关掉吃核心的 offer。

---

## 7. 验收清单（S2 落地时）

- [ ] §2 每一行都有 `SourceId` + 单元测试夹具能复现数量（D228：徽记 S07/S08/S09/S17/S18/S23、徽 S16/S19/S27、印记 S10/S11/S12/S16/S17；**D229：S13 深渊层经 `sourceForGrant` 与 S01 分开**；S14/S15 体力/药未经 grant*）
- [ ] §3 每一行都有 `SinkId` + 余额不足时 0 副作用
- [x] §5 缺口 1–2（徽记账户 + 残响）已进 `p1sim` / `p2econ`，W30 报告多一列「徽记结余 / 烙印次数」 — D222：`tools/p1sim/insignia.py`（opt-in `p2econ --insignia`；S23 经 `signin.py` 钩子，缺口 3 一并补上），报告 `tools/p1sim/out-insignia-d222-w30*.md`，状态 `docs/status/STATUS-ember-p1sim-insignia-2026-10-06.md`
- [ ] §2.5 L-S* 在 FreshQ 非 OP 上冒烟仍为 0
- [ ] `tools/p1sim` 与线上 `balance_version` 同源（改表必 bump）

---

## 8. 变更记录

- **D391（2026-10-10）**：新增 **S40 短征通关（sx01）** — 日有奖帽 3 · 有奖包币/碎片/骨尘 · 首通包币/碎片/胚料；不抬 `afk.tiers` / `daily_kills`；不放开 `gate_daily`。
- **D392（2026-10-10）**：新增 **S41 短征通关（sx02）** — 有奖同 S40 量级 · 首通略薄 · 日帽 `p1_sx02_day` 独立；首航含 rooms×3+boss；不抬挂机、不放 `gate_daily`。
- **D393（2026-10-10）**：新增 **S42 短征通关（sx03）** — 有奖同 S40/S41 量级 · 首通略薄 160/8/1 · 日帽 `p1_sx03_day` 独立；首航含 rooms×3+boss；不抬挂机、不放 `gate_daily`。
- **D397（2026-10-10）**：新增 **S43 短征通关（sx04）** — 有奖同量级 · 首通略薄 150/6/1 · 日帽 `p1_sx04_day` 独立；首航含 rooms×3+boss；日帽 PAPI 扩第四本；不抬挂机、不放 `gate_daily`。
- **D400（2026-10-10）**：新增 **S44 短征通关（sx05）** — 有奖同量级 · 首通略薄 140/6/1 · 日帽 `p1_sx05_day` 独立；首航含 rooms×3+boss；日帽 PAPI 扩第五本；不抬挂机、不放 `gate_daily`。
- **D403（2026-10-10）**：新增 **S45 短征通关（sx06）** — 有奖同量级 · 首通略薄 130/6/1 · 日帽 `p1_sx06_day` 独立；首航含 rooms×3+boss；日帽 PAPI 扩第六本；不抬挂机、不放 `gate_daily`。
- **D407（2026-10-10）**：新增 **S46 短征通关（sx07）** — 有奖同量级 · 首通略薄 120/6/1 · 日帽 `p1_sx07_day` 独立；首航含 rooms×3+boss；日帽/首通合计扩第七本；不抬挂机、不放 `gate_daily`。
- **D410（2026-10-10）**：新增 **S47 短征通关（sx08）** — 有奖同量级 · 首通略薄 110/6/1 · 日帽 `p1_sx08_day` 独立；首航含 rooms×3+boss；日帽/首通合计扩第八本；不抬挂机、不放 `gate_daily`。
- **D412（2026-10-10）**：新增 **S48 短征通关（sx09）** — 有奖同量级 · 首通略薄 100/6/1 · 日帽 `p1_sx09_day` 独立；首航含 rooms×3+boss；日帽/首通合计扩第九本；不抬挂机、不放 `gate_daily`。
- **D414（2026-10-10）**：新增 **S49 短征通关（sx10）** — 有奖同量级 · 首通略薄 90/6/1 · 日帽 `p1_sx10_day` 独立；首航含 rooms×3+boss；日帽/首通合计扩第十本；不抬挂机、不放 `gate_daily`。
- **D417（2026-10-10）**：新增 **S50 短征通关（sx11）** — 有奖同量级 · 首通略薄 80/6/1 · 日帽 `p1_sx11_day` 独立；首航含 rooms×3+boss；日帽/首通合计扩第十一本；不抬挂机、不放 `gate_daily`。
- **D419（2026-10-10）**：新增 **S51 短征通关（sx12）** — 有奖同量级 · 首通略薄 70/6/1 · 日帽 `p1_sx12_day` 独立；首航含 rooms×3+boss；日帽/首通合计扩第十二本；不抬挂机、不放 `gate_daily`。
- **D421（2026-10-10）**：新增 **S52 短征通关（sx13）** — 有奖同量级 · 首通略薄 60/6/1 · 日帽 `p1_sx13_day` 独立；首航含 rooms×3+boss；日帽/首通合计扩第十三本；不抬挂机、不放 `gate_daily`。
| 日 | 项 |
|---|---|
| 2026-10-05 | D204 初稿：来源 32 + 旧 5、消耗 18、上限表、模型矩阵、S2 规格六条。CoreRpg 不发版（仍 1.65.38 / bv57）。 |
| 2026-10-06 | D213（CoreRpg 1.65.44）：本表进代码 `EmberEconomy`（S01–S32 / L-S1～L-S5 / C01–C18，账户、周期、键族、模型覆盖、金样数量），`EmberEconomyTest` 把金样钉在 Java 常量与部署 yml 上、把 §5 未建模清单钉死。§6 第 1–2 条的"编号"已就位；`grant` / `spend` 改走编号（S2-2）尚未做。 |
| 2026-10-06 | D215（CoreRpg 1.65.46）：S2-2 第一步 — `EmberEconomy.amount` / `grantCoin` / `spendCoin`；`EmberRunRules.settle` S01–S03 数量改读登记表；通关币发放走 `grantCoin`（base/treasure/elite/bounty/fc/honor）；C14 回复药价与扣币走登记表。数量不变（bv57）。 |
| 2026-10-06 | D216（CoreRpg 1.65.47）：S2-3 — S23/S24 数量与发放走登记表；grantMark/grantXp/grantMat；deliver 扩材料/印记/XP；p1 addCoin 扫描；徽记 p1sim 缺口 TODO。数量不变（bv57）。 |
| 2026-10-06 | D218（CoreRpg 1.65.48）：S2-4 — 消耗 C03–C13 走登记表（工坊 C03–C06 / 洗练 C11 / 烙印 C12 经 `EmberPay.Price.at`；C07 兑换数量读 amount；C08 层费币+T3 印记；C09/C10 天赋币，加金样钉 growth yml；C13 调律 `spendInsignia`）；p1 takeCoin 扫描。数量不变（bv57）。 |
| 2026-10-06 | D220（文档）：§6.3 yaml 真源草案 `DESIGN-ember-v1-economy-yml-2026-10-06.md`（E0）；CoreRpg 仍 1.65.49。C18/S22 实现留给 E2。 |
| 2026-10-06 | D221（CoreRpg 1.65.50）：C18 国庆商店 spend 走 EmberEconomy；S22 挂机庭 grant 经 `sourceForGrant(p1afk-)` + grant*；E1 yml 仍草案。 |
| 2026-10-06 | D222（p1sim，无发版；CoreRpg 不动 / bv57）：§5 缺口 1–3 进模型 — `tools/p1sim/insignia.py` 首领徽记分图账户（S07/S08/S09/S17/S18/S23 → C12/C13，花费策略为假设），`p2econ --insignia` / `insignia.py report`；默认输出逐位不变。§7 第 3 条打勾。 |
| 2026-10-06 | D223（CoreRpg 1.65.51）：EmberDelivery 负额扣币标签化（`spendCoinDelivery`）；E1 `ember-v1-economy.yml` 镜像防漂移（yml 尚未真源）。 |
| 2026-10-06 | D224（CoreRpg 1.65.52）：E1 `amount()` 以 `ember-v1-economy.yml` 为真源；缺/坏/漂移 SEVERE fail-closed；Java golden 二次断言；数量不变。 |
| 2026-10-06 | D225（p1sim + 文档；CoreRpg 不动 / bv57）：E3 p1sim 同读 `ember-v1-economy.yml`；徽记高图周来源设计笔记（研究 only，含 REG S09/S23/C12 文案重写提案）。 |
| 2026-10-06 | D226（p1sim + 文档；CoreRpg 不动 / bv57）：R1-sim 徽记 what-if A/B/C（`insignia.py --whatif`）；W30 对照 `out-insignia-r1-*.md`；**推荐 B**（残响厅扩 Q05–Q07、共用周帽）；本表 S09/S23/C12 **只改措辞**对齐代码（不改发放逻辑 / 数量）。 |
| 2026-10-06 | D227（CoreRpg 1.65.53 / bv58）：S18 残响厅扩 echo_q05–q07，共用 `p4_echo_claim`=3、`S18.insignia`=2 **数量不变**（只改可领图集合 / retarget）；本表 S18 行同步。 |
| 2026-10-06 | D228（CoreRpg 1.65.54 / bv58）：S2-8 — `grantInsignia` / `grantBadge`；账本 MARK / SIGMARK 行经 `creditMarkLedger` / `creditInsigniaLedger`（sig_mark S08、fc_sigmark S07、pledge_sigmark S09、raid_mark S12、rot_mark S10/S11、rush_mark S16/S17、rush_sig_* S17/S18；未知键 untagged 照发）；连战徽 S16 / 周目标徽 S19 / 国庆兑换徽 S27 走 grantBadge；§6.4 扫描扩到 `p1_mark_t` / `p1_sigmark_` / `p3_badge` 直写 + `grantFlatEmberXp`。数量不变。 |
| 2026-10-06 | D229（CoreRpg 1.65.55 / bv58）：S2-9 — 深渊层结算 SourceId 与 S01 分开（`sourceForGrant(base_*, <map>a<n>-…)` → S13；数量仍读 S01 BASE_*）；§6.4 vault 写入扫描（`autoDeposit` / `creditBound` / `credit` / `EmberVault.get().give`，例外行 `econ-ok:`）。数量不变。 |
| 2026-10-06 | D242（ARCH S4-1，文档 + 测试；CoreRpg 不发版 / 1.65.67 / bv58）：装备结构权威文档 `DESIGN-ember-gear-structure-2026-10-06.md` + 机器可读来源表 `ember-source-map.yml`（S01–S32 / LS1–LS5 / C01–C18 + 未登记 X01 图录币 · X02 起步包 · X03 管理员发放），`EmberSourceMapTest` 防漂移。本表文案修：S06 Q02 = 自选族 × 部位、S08 12% 仅适配时、S13 无无尽层、C13 调律 6 件。缺口 G1–G11 见新文档 §8。 |
| 2026-10-06 | D243（ARCH S4-2，CoreRpg 1.65.68 / bv58）：登记 S33 图录阶段奖励 / S34 宝箱额外装备（从 S01 拆出）/ S35 起步包，数量不变只加标签（`EmberEconomy` S01–S35、E1 yml 加块、图录币走 `grantCoin(S33)`）；S09 加 `per_rule` 键（值 1）；OP `givedup` 改 `source=admin`。p1sim 经 `ember-source-map.yml` 计入 S33 / S35（21 格 A/B 全部 ±2pp 内）。 |
| 2026-10-06 | D244（ARCH S4-3，CoreRpg 1.65.69 / bv58）：G10 物品级缺口登记 — S36 钓鱼产出（CoreFish，`LIFE_ITEM`）、S37 扭蛋券发放、S38 扭蛋抽取产出（外观，OUT）、C19 扭蛋抽取（耗券）；C17 账户加 `LIFE_ITEM`。**只登记 / 打标签**：无金样、无路由、数量全不变。`ember-source-map.yml` 新 `stocks:` 块（徽记 / 余烬徽 / 生活件 / 扭蛋券 / 外观），`EmberSourceMapTest.stocksMatchEconomyAndItemConfigs` 把每个库存的来源 ∪ 消耗钉在 `EmberEconomy.touching()`、生活件清单钉在 life.yml + CoreFish、券来源钉在 CoreGacha `tickets`、扭蛋件 kind 钉成外观。§5 矩阵 3 行改「已登记」。 |
- **D423（2026-10-10）**：新增 **S53 短征通关（sx14）** — 有奖同量级 · 首通更薄 50/6/1 · 日帽 `p1_sx14_day` 独立；首航含 rooms×3+boss；日帽/首通合计扩第十四本；不抬挂机、不放 `gate_daily`。
