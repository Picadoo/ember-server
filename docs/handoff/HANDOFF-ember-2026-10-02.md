# 余烬服 · 交接（2026-10-04 12:13 CST 更新）

这份文档写给接手的人，读完就能接着做。设计正文在 `docs/design/design-ember-v1.0-P1.md`（下文称「书」）。P2 草案在 `docs/design/design-ember-v1.1-P2-draft.md`。所有裁决记在源表 `docs/design/DESIGN-ember-v1.0-P1-source-table.md`（§13.x，D01–D162）。

## 1 当前状态

| 项 | 值 |
|---|---|
| 仓库 | `Picadoo/ember-server`，main 分支。工作树 `/workspace/ember-p1`（分支 p1-g01，推到 main）。服务器在 `/workspace/minecraft` 跑，这是 main 的检出 |
| CoreRpg | **1.65.16 线上**（10-04 21:25 CST，COORD-signin-online，D180 P1 每日签到 + 在线时长：主菜单「签到 · 在线」月历 + 15/30/60/120 分钟，balance_version 39，提交 `2bb75d6`；冒烟 FreshQ263 25/0；发布 `docs/status/RELEASE-ember-1.65.16.md`）。其上是 **1.65.15 线上**（10-04 20:41 CST，COORD-afk-p1，D177 rev 2 挂机庭自动战斗 `1e685d3`，balance_version 38；挂机材料账号绑定入仓库、不可取出 / 丢出 / 入箱）；之前 1.65.14 线上**（10-04 19:55 CST，COORD-mainline-unlocks，D174 第 2b 阶段：首领残响；1.65.11 L07–L12 签名 `c4bd355`、1.65.12 自选誓约 `90251d1`、1.65.13 连战·前哨 `fbd4176`、1.65.14 首领残响 `07b3ec3`；balance_version 37；状态 `docs/status/STATUS-ember-mainline-unlocks-1.65.14.md`；测号用到 FreshQ200（afk-p1 占 210–219 / 240–259，mainline 占 220–239）；下一版 1.65.15 afk-p1、1.65.16 signin-online）。其上是 **1.65.10**（10-04 18:35 CST，D174 stage 1.5，提交 `108a0a3`，包含 D177）。其上是 **1.65.9**（10-04 18:23 CST；D177 P1 挂机庭：复用 `ember_afk`，四层按首通 Q01/Q03/Q05/Q07，只发账号绑定余烬币 + 经验，10 分钟一轮、每日 12 轮、离线 1/4 最多 6 轮；P1 下旧挂机掉落与旧寄售关闭；balance_version 33；设计 `docs/design/DESIGN-ember-afk-p1-2026-10-04.md`，发布 `docs/status/RELEASE-ember-1.65.9.md`；**待办 Stage 1.1：W30 上界略超，降每轮币（设计 §7.3）**；测号用到 FreshQ145）。其上是 **1.65.8**（10-04 18:17 CST；D178 Weekly Modifier Pack 2：+bolters/shell/press，池 6→9，balance_version 32；冒烟 FreshQ139–141；Stage C p2econ --mods deferred；状态 `docs/status/STATUS-ember-weekly-mod-pack2-1.65.8.md`，发布 `docs/status/RELEASE-ember-1.65.8.md`；下一测号 FreshQ142，下一个裁决号 D179+）。其上是 **1.65.7 线上**（10-04 18:05 CST；D174 签名传奇第 1 阶段：Q01–Q03 首领各 2 件签名（刃 / 护符，已有修饰键）、首领徽记、烬炉烙印（Q02 首通）、双签名（Q03 首通），`/corerpg p1 sig`；balance_version 31；设计 `docs/design/DESIGN-ember-mainline-unlocks-2026-10-04.md`，发布凭证 `docs/status/RELEASE-ember-1.65.7.md`；测号用到 FreshQ138；下一个裁决号 D177+）。其上是 **1.65.6**（D176 Variety Pack 2，`docs/status/RELEASE-ember-1.65.6.md`）。其上是 **1.65.5**（10-04 16:33 CST 换 jar，17:10 CST 关钩子重启；D172 资产安全修复：撤销先扣胚料、印记兑换一笔事务 + `mark` 欠款、洗练耐久付款后才抽；数值 / 装备结构不变，balance_version 29；`CORERPG_TEST_FAULTS` 线上不带；状态 `docs/status/STATUS-ember-asset-fix-1.65.5.md`，发布凭证 `docs/status/RELEASE-ember-1.65.5.md`；下一测号 FreshQ121，下一个裁决号 D174）。其上是 **1.65.4**（10-04 15:53 CST 部署；D170 Q02–Q07 战术房提示，balance_version 29；D169 装备 6 槽 staged plan 文档定稿；状态 `docs/status/STATUS-ember-room-hints-1.65.4.md`，发布凭证 `docs/status/RELEASE-ember-1.65.4.md`；下一测号 FreshQ109）。其上是 **1.65.3 线上**（10-04 13:41 CST 部署，含未单独部署的 1.65.2 / D163；D164 天赋 t2c 拆分 → 破甲（厚甲精英 ×1.6、炽热 / 分裂 ×0.8）；D165 裂身纹移出洗练池（已有照常生效）；D166「仅主线重打生效」文案 + Q01 第一房 / 首领前留 2 瓶药 / R01 建议炽愈提示；模拟 `tools/p1sim/out-d164-armorbreak-check.md`，状态 `docs/status/STATUS-ember-armorbreak-1.65.3.md`，发布凭证 `docs/status/RELEASE-ember-1.65.3.md`；下一测号 FreshQ103，下一个裁决号 D167）。其上是 **1.65.1**（10-04 11:31 CST 部署，提交 64a0593，线上 jar = 该提交 JDK8 重编；D160 余烬连战失败不限次数重试、每周首通领奖、之后练习；D161 存仓 / 存库 / 取出后同 tick 存 .dat；D162 review §03 A01–A04 资产交付闭环（`cr_p1_delivery`、自动入库同步事务、工坊 hold + 对账、写失败暂停、离线恢复成功后才出队）；测试钩子 `CORERPG_TEST_FAULTS=1` 线上**不带**；持久化测试 g 52 / 0 见 `docs/tests/TEST-ember-persist-roundtrip-2026-10-04.md`，真实装备试玩见 `docs/tests/TEST-ember-realplay-2026-10-04.md`，状态 `docs/status/STATUS-ember-rush-persist-d160-d162.md`；下一测号 FreshQ81 / FreshG10，下一个裁决号 D163）。其上是 **1.64.2**（10-04 10:30 CST 部署；D159 洗练可用装备库重复件 + hub 扭蛋图标改 ender pearl + 扭蛋页#9文案；冒烟 `tools/p1map/d159-reroll-gearlib-smoke.sh FreshQ51` 7/7 PASS，见 `docs/status/STATUS-ember-reroll-gearlib-d159.md`；下一测号 FreshQ52 / FreshG07）。其上是 **1.64.1 线上**（10-04 10:01 CST 部署，10:09 又短重启一次（只换 CoreGacha）；D158 第二轮挑刺补完：InvSnap 按账本扣回（新表 `cr_vault_log`，`/corerpg invsnap preview`）、批量分解成色计数 + 不分解清单 + 整批撤销 `p1 undo batch`、周规则转化怪加成 balance_version 29；冒烟 `tools/p1map/review2-smoke.sh FreshQ50` 14/14 PASS，见 `docs/status/STATUS-ember-review-round2-fix-1.64.1.md`；下一测号 FreshQ51 / FreshG07）。其上是 **1.64.0**（10-04 09:48 CST 部署，D157 第二轮挑刺 InvSnap 材料/券防复制 + 批量分解跳过投入件 + hub L 活动后动作 + 文案；冒烟 FreshQ49 PASS，见 `docs/status/STATUS-ember-review-round2-d157.md`；下一测号 FreshQ50 / FreshG05。推迟评审 #3 火花折算、#7 批量撤销）。其上是 **1.63.2 线上**（10-04 08:52 CST 部署，日志 `Enabling CoreRpg v1.63.2` + `[storage] MySQL connected`；D155 铁卫第 6 条 P2-8 周规则，balance_version 28，冒烟 FreshQ46 PASS，见 `docs/status/STATUS-ember-modifier-wall-d155.md`）。其上是 **1.63.1**（10-04 08:21 CST 部署，日志 `Enabling CoreRpg v1.63.1` + `[storage] MySQL connected`；D154 卫士潮第 5 条 P2-8 周规则，balance_version 27，冒烟 FreshQ45 PASS，见 `docs/status/STATUS-ember-modifier-guards-d154.md`）。再上是 **1.63.0**（10-04 07:48 CST 部署，日志 `Enabling CoreRpg v1.63.0` + `[storage] MySQL connected`；D152 卸甲第 4 条 P2-8 周规则，balance_version 26，冒烟 FreshQ44 PASS，见 `docs/status/STATUS-ember-modifier-disarm-d152.md`）。再上是 **1.62.0**（10-04 04:03 CST 部署，日志 `MySQL connected`、`cr_p1_gearlib ready`、`cr_inv_snapshot ready`；包含 D150 连战失败行修复，04:05 实测在第一个首领现身后倒下时提示「本周的连战次数已用」；另外新增 P1 材料仓库 + 装备库、背包快照 `/corerpg invsnap`、数据库断线守卫 DbGuard，见 `docs/design/DESIGN-ember-data-protection.md`。**构建必须用 `JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01`**：用 JDK 21 编出来的包调用了 `Math.floorDiv(long,int)`，在服务器上会报 NoSuchMethodError；1.61.0 是 10-04 03:19 CST 部署，日志 `MySQL connected`：D144 花样委托 / 烬核同心 / 余烬连战 + 评审 10-04 Part D 整改 D145–D149，P2 草案 §5zb，p2econ 表 `tools/p1sim/out-p2econ-d144.md`；**main 上已有 D150 源码修复（连战失败行说错次数），还没部署，下次部署升 1.61.1**；1.60.1 = Part A 管理员钩子 `p1 honor test` / `p1 givedup`；1.60.0（10-03 18:23 CST）：D141 天赋专精 + D142 余烬勋记 + D143 词条洗练，P2 草案 §5za，参数 `ember-v1-growth.yml`，终验表 `tools/p1sim/out-growth-d141-d143.md`；1.59.0 = D140 七个主线首领各加一招 + D138 词缀精英改 +2 碎片；1.58.0 = D138 重复刷图花样 + D139 国庆活动；`CoreRpg/pom.xml` 第 8 行和 `plugin.yml` `version:` 一起改） |
| balance_version | 28（`ember-v1-runs.yml`；28 = D155 铁卫 `wall` 第 6 条 P2-8 周规则 melee→heavy、挑战专用；27 = D154 卫士潮 `guards` 第 5 条 P2-8 周规则 ranged→heavy、挑战专用；26 = D152 卸甲 `disarm` 第 4 条 P2-8 周规则 heavy→melee、挑战专用；25 = D144 余烬连战 `rush:` + 花样委托 `ember-v1.yml` `bounty.variety:` + 烬核同心 `weekly_goals.targets.core`，D146 国庆币剩余兑换（`ember-v1-festival.yml` `memo:` / `badge:`），D148 洗练锁定 `lock_shard`，D149 活动首领预警 1.2；24 = D141–D143 横向成长 `ember-v1-growth.yml`（天赋 / 勋记 / 词条，参考装上都是侧向取舍）；23 = D140 Q01–Q07 首领各加一招有预警的新招、主招放慢抵消（Q01 踏地接在重斩后、半血才有）；22 = D139 国庆限时活动 gq26 + 活动护符位（参数全在 `ember-v1-festival.yml`）；21 = D138 重复刷图花样 `variety:`（已首通的普通版：词缀精英 +3 碎片、30 秒限时清房 +1 核心）；20 = D137 团本 R03 `raids.r03`（烬核分摊）；19 = D134 毕业那周的周目标「委托 3 天」按剩下的天数算（周六 2、周日 1，`weekly_goals:` 上的注释，代码 `EmberSeason.target`）；18 = D128 每天第一次挑战 / 深渊失败退一半体力 `fail_refund: 0.5`；17 = D123 赛季深潜者改第 5 层 `season.deep_tier: 5`、D124 深渊层费可用多余 T3 印记抵 `abyss.fee_mark_coin: 200`、D122 Q04 `rails` + `fall_catch_y`；16 = D116 赛季 `season:`、D117 周目标 `weekly_goals:`、D118 团本最后阶段复活 `raid_revive:`（首领 ≤20% 且有人倒下，20 秒后复活一次）；15 = D109 深渊第 1 层 = 挑战版、线性到第 10 层（不变）、成色跟强度、第 8～10 层费 400/440/480；D110 Q02 首通 = 一次免费定向兑换（自选族 + 部位 T1）；14 = D108 精选图首通后重打普通版 +1 枚该图阶印记，与挑战版精选加成共用 3 次/周；13 = D104 挑战版减压 + 深渊层表换算 + T3 升阶减半 + Q05 首通加币） |
| CoreGacha | **1.0.1 线上**（10-04 10:09 CST；gq26 火花 30、只能换锦鲤灵 / 繁花烟火，`spark_leftover: carry` → 10-08 结束后剩余火花 1:1 转进 standard 火花（上线后没实测，10-08 后看 ledger `spark_carry`）；各池可单独设 `spark` / `spark_items` / `spark_leftover`；模拟 `docs/tests/TEST-gacha-sim-gq26-spark-2026-10-04.md`；冒烟 FreshG05 / FreshG06）。其上是 **1.0.0**（10-04 04:17 CST，日志 `[CoreGacha] [db] MySQL connected` + `[bridge] CoreRpg API ok`）：外观扭蛋，只出外观、零属性，券只从游戏里得（见面 5 张、每天在线 30 / 90 分钟、主线结算 1 / 3 局、余烬币 1200 / 余烬徽 20 换 1 张每天 5 张、NI 实物券 `ember_gacha_ticket`），每天最多 50 抽。设计 `docs/design/DESIGN-ember-gacha.md`，离线百万抽 `docs/tests/TEST-gacha-sim-2026-10-04.md`，冒烟 `docs/tests/smoke-gacha-2026-10-04.md` + craft/spark/exchange `docs/status/STATUS-ember-gacha-craft-spark-smoke.md`（FreshG03 PASS）。表 `gacha_*` 在 ember 库（用 CoreRpg 的 MySQL 凭据），CoreRpg 外观商店的件通过反射调 CoreRpg 公开 API 发（`p2_cosbuy_<id>@all`），**CoreRpg 源码没动** |
| 模式 | P1 是默认模式（D61–D65），旧玩法藏在 `ember_hub_legacy`。**不要关 P1** |
| 菜单 | TrMenu 共 59 个（10-04 CoreGacha 加了 `ember_gacha` / `_rates` / `_history` / `_shop` / `_reveal`，由 `CoreGacha/tools/genmenus.py` 生成，主菜单 `ember_hub` 第 34 格是入口；D141–D143 加了 `ember_p1_spec` / `ember_p1_honor` / `ember_p1_reroll`；D139 加了 `ember_p1_fest`），`trmenu reload` 就能重载 |
| 套件 | `mineflayer-tests` gameplay 9/9（1.61.0，10-04 03:41；主菜单检查认合并后的「团本 · R01–R03」「成长 · 天赋 / 勋记 / 洗练」；「天赋专精」不算旧版「天赋」；钓鱼偶尔随机失败，重跑即过） |
| 玩家 | 没有老玩家，**不做数据迁移**。测试号 P1Fox、RaidA–E、NewbieQ、FreshA1–N1、FreshO1–O9、FreshP1–P6、FreshQ1–Q33（全新号；**下次从 FreshQ48 起（FreshQ47 = D156 国庆结束后路径冒烟，见 `docs/status/STATUS-ember-fest-after-end-smoke.md`；FreshQ46 = D155 铁卫冒烟，见 `docs/status/STATUS-ember-modifier-wall-d155.md`；FreshQ45 = D154 卫士潮冒烟，见 `docs/status/STATUS-ember-modifier-guards-d154.md`；FreshQ44 = D152 卸甲冒烟 PASS，见 `docs/status/STATUS-ember-modifier-disarm-d152.md`；FreshQ43 = D144 限时清房花样委托冒烟 PASS；FreshQ42 = D142 勋记解锁冒烟 PASS）**（FreshQ40 = 1.62.0 背包快照冒烟，FreshQ41 = 余烬连战倒下 + 仓库 / 装备库冒烟，本周连战已用）；FreshQ35 = 1.61.0 冒烟：管理员首通 Q01–Q07、Q01 普通版打过一次（花样委托 1/2）、本周连战已用（第三个首领倒下失败）、纪念称号「烟火纪念」+ 国庆币换满 40 徽（背包还剩 110 国庆币）、T1 精良炽愈刃猎缀纹 2 档、有余币和碎片；FreshQ36 = 管理员首通 Q07，连战通关一次（称号「连战不息」、20 徽、本周已用）；FreshQ34 = 1.60.1 Part A 钩子冒烟；FreshQ33 = 1.60.0 冒烟：管理员首通 Q01–Q07（专精点 3/6）、点过回身斩→重置（免费那次已用）→稳桩、T1 卓越烬爆刃洗过两次（拆分 1 档，保底 0/5）、有余币和碎片；国庆活动的测试号 FestQ01–Q02（FestQ01 有盛世烟火符、红金烟火足迹和称号 gq26；下次从 FestQ03 起）；FreshQ28–Q30 打过 R03（通关 1 次 + 一局首领测试未打完）；FreshQ27 是管理员写的 Q01–Q07 首通，测过商店返回键；FreshQ17 是复查人的号；FreshQ18/Q19 兑换过 T1/T2 焚烬刃（D130/D131 检查），FreshQ20 是管理员写的 Q01–Q07 首通，FreshQ21–Q26 打过 R01 全倒测试（Q24–Q26 开战后全倒，周次数 0/3）；FreshQ10–Q12、Q14–Q16 打过 R01 倒下测试，FreshQ13 当天两次挑战失败；FreshQ8 真打通关过 Q04、买过素白，FreshQ9 是管理员写的 Q01 + Q07 首通；FreshQ4 有赛季奖励 season_deep / season_crown 和余烬徽，是管理员发的测试数据）、RevMidA/B（点评用）。RevNewA 的游戏数据已删（D100），AuthMe 账号还在。**测试号不上榜**：`ember-v1.yml` 的 `leaderboard_exclude`（名字 / 前缀 / 正则，D102）挡住排行榜、主城悬浮字和荣誉陈列，加新机器人名字时记得补进去 |
| 内容 | 七张主线图 Q01–Q07 + 挑战版。P2-1 每周挑战轮换。P2-2 深渊·余烬层（10 层）。P2-5/6 团本 R01 锈轨矿道·团 + R02 霜封哨所·团 + D137 R03 断塔回廊·团（Q05 地图，招牌机制烬核分摊；3～5 人，三本合计每周 3 次）。P2-7 每日委托。P2-8 精选图周规则（限药 / 术者换防 / 逆行 / 卸甲 / 卫士潮 / **铁卫**；D94 起限药、逆行也用于重打已首通的普通版；D152 卸甲、D154 卫士潮与 D155 铁卫与术者换防同为挑战专用）。P2-9 每图掉落偏向、团本额外装备按目标族定向（保底精良）、称号和团本足迹（只做展示）。P2-10 排行榜（`/corerpg p1 top`）。新手提示：回复药放快捷栏、生命低提醒、「下一步」（1.34.1–1.36.1）。新手第一周（1.39.x，D85–D87）：护符自动生效、T1 自动顶替起步件、Q01 普通版减压、首通自选 / 目标族改成可点按钮；1.40.x（D88–D92）：印记兑换按钮、房间敌人提示、结算物品不占快捷栏、踏步默认装配、好友页组队打开队伍面板；1.41.x（D94–D95）：普通版周规则、锻造 / 补领 / 荣誉改成按钮；1.42.0（D96）：第一周复查，Q02 缺 T1 件时提醒、锻造缺料给来源；1.43.0（D97）：枢纽氛围（闲话、指路牌、粒子、排行榜、荣誉陈列，只做展示）；1.44.0–1.46.0（D98–D100，第一周点评整改）：Q01 首通自选护符、Q02 首通自选刃（balance_version 12），Q02 提醒带 [回 Q01]；旧系统移出主菜单、Q07 前内容合成一个图标、术语和公式清理、进服消息去重；好友 / 组队全按钮、DP 队伍上限 5；锻造确认与预览同列、T0 黄字；1.47.0–1.48.1（D101–D105，中后期点评整改）：主手 / 攻击缓存改成事件驱动刷新（D101）、测试号不上榜 + 主菜单「荣誉与排行」+ 盟约移出（D102）、里程碑称号（D103）、挑战版减压与深渊换算 + T3 升阶减半 + 两条换阶路互相标注（D104）、团本算 2 局委托 + 团本招募按钮（D105）；1.49.0（D106–D108）：团本倒下观战队友、下一个房间开打 / 首领现身 / 首领转阶段自动复活 50% 生命（D106）、外观商店（称号颜色 / 朴素足迹 / 名牌标记，2000～20000 币，只做展示，D107）、精选图重打普通版 +1 枚 T1/T2 印记（D108）；1.50.0（D109–D115，后期复查）；1.58.0（D139）：国庆 2026 限时活动「烟火庙会」（10-03 → 10-08 0 点，Q04 换色副本、国庆币、活动护符位、限时称号 / 足迹，§7）；1.57.0（D138）：重复刷图花样（已首通的普通版每局 1 只词缀精英炽热 / 分裂 / 护盾 +3 碎片，约一半的局 30 秒限时清房 +1 核心）；1.56.0（D137）：团本 R03 断塔回廊·团（烬核分摊、焚烬偏向、专属称号足迹、R03 榜）；1.55.0（D135–D136）：深渊层费拍板不调、外观商店返回键回到打开它的那一页；1.54.0（D130–D134，快速复查整改）：起步刃不再挡住新刃自动换上、起步刃真的进背包、背包里更好的同阶件也会换上 / 「下一次突破」给 [换上]、团本失败说明周次数没扣、进服消息加 [外观商店]、毕业那周委托目标按剩下天数；1.53.0（D128–D129）：每天第一次挑战 / 深渊失败退一半体力（不给掉落和币），刷怪点检查脚本不再误报门；1.52.x（D120–D127，全流程验收整改）：更好的件自动换上 + 「下一次突破」给最省路线、外观商店一个来源 + 付款页、一套赛季 / 排行 / 商店页面、Q04 封口和掉落传回、深渊榜按用时破并列 + 深潜者第 5 层、深渊层费可用 T3 印记抵、团本倒下提示、聊天去世界名 + 不广播成就、旧物品名重写；1.51.0（D116–D119）：4 周赛季 + 本周 / 赛季榜（深渊最高层、精选挑战、团本通关、R01/R02 最快通关）和季末赛季称号 / ❖ 名牌框（只做展示，归档 `p1-runs/season-archive/`），首通 Q07 后的周目标（4 个，奖励余烬徽，只能买外观），团本最后阶段多一次复活，外观商店菜单页 |

## 2 启停

- **全栈**：`scripts/ember-up.sh` 和 `scripts/ember-down.sh`（登录服、游戏服、代理）。
- **只重启游戏服**：`server-runtime/stop.sh; sleep 2; server-runtime/start.sh`，等大约 40 秒。日志 `server-runtime/logs/stdout.log` 里要看到「Enabling CoreRpg vX」「50 个菜单已加载」「Done」。
- **控制台**：`scripts/console.sh play "<cmd>" [等待秒数]`。
- **聊天不带世界名（D126）**：Multiverse 的设置要在服务器开着时改：`scripts/console.sh play "mv config prefixchat false"`（看到 `SUCCESS! Values were updated successfully!`，文件 `plugins/Multiverse-Core/config.yml` 里变成 `prefixchat: 'false'`）。**不要直接改文件**：Multiverse 关服时会把内存里的值写回去，改了也白改。检查：机器人说一句话，聊天里应是「[称号] <名字> …」，没有 `[ember_hub]`。原版成就广播由 CoreRpg 每次启动和世界加载时关掉（`gamerule announceAdvancements` 应为 false），不用手动管。
- **构建**：
  ```
  cd CoreRpg && JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01 /workspace/minecraft/tools/apache-maven-3.9.16/bin/mvn -q -o package
  ```
  构建会跑单元测试。**先看输出里有没有 `ERROR]`**（测试失败时 target 里还是旧 jar，照样能复制过去），没有再 `cp target/CoreRpg.jar /workspace/minecraft/plugins/CoreRpg.jar` 并重启。
- **CoreGacha 构建**：`cd CoreGacha && JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01 /workspace/minecraft/tools/apache-maven-3.9.16/bin/mvn -q -o package`（**一定用 JDK8**，服务器是 Java 8），`cp target/CoreGacha.jar /workspace/minecraft/plugins/CoreGacha.jar`，备份 `/workspace/backup/CoreGacha-<ver>.jar`，重启。改了 `gacha.yml`（池 / 物品 / 概率）只要 `/gacha admin reload`，但菜单是生成的：`python3 CoreGacha/tools/genmenus.py` 重写 `plugins/TrMenu/menus/ember_gacha*.yml`（TrMenu 自动重载），改完先跑离线模拟 `java -cp target/classes:$HOME/.m2/repository/org/yaml/snakeyaml/1.19/snakeyaml-1.19.jar town.sunshine.coregacha.sim.GachaSim ../plugins/CoreGacha/gacha.yml 1000000`（全 PASS 才上线）。`CoreGacha/src/main/resources/` 和 `plugins/CoreGacha/` 各有一份 yml，**两份要一起改**。
- **每次部署**：
  1. 升版本号，备份 jar 到 `/workspace/backup/CoreRpg-<ver>.jar`。
  2. 冒烟，再跑套件 `cd mineflayer-tests && timeout 600 npm run gameplay`，结果看 `PASS=`。
  3. 跑 `scripts/db-dump.sh`，把最新的 `…-manual.sql` 改名为 `…-after-<ver>.sql`。
  4. 套件会改写 live 里的 `docs/status/STATUS-gameplay-suite.md`：复制到工作树提交，然后在 live 里 `git checkout --` 还原。

## 3 架构地图（P1/P2）

| 层 | 位置 | 说明 |
|---|---|---|
| 模式和战斗 | `CoreRpg/.../p1/EmberMode`、`EmberCombatListener`、`EmberFormula`、`EmberHeal`、`EmberDamageTrace` | P1 世界判定、统一伤害、回复只能走 HealLedger、伤害追踪 |
| 装备 | `EmberItems`、`EmberItemData`、`EmberItemStore`（MySQL）、`EmberLoadout(Service)`、`EmberForgeService`、`EmberUpgradeRules`、`EmberCodex`、`EmberCompare` | T0–T3、成色和精工、强化和升阶、图鉴 |
| 套装 | `EmberSetRules`、`EmberSetEngine`、`EmberSetService`、`EmberBurnBook` | 烬爆 / 焚烬 / 炽愈；动作栏 HUD（生命低时显示喝药提示） |
| 赛季 / 周目标 | `EmberSeason`（D116–D117） | 榜单、季末结算（每分钟 `tick`）、待发奖励（进服 `apply`）、周目标与余烬徽；数据在 `plugins/CoreRpg/p1-runs/season.yml`（运行文件） |
| 副本 | `EmberRunService`（入口、结算、账本）、`EmberRunSession` / `EmberRunStore`、`EmberRunDirector`（房间和首领；顶层招式可按 `below` 分阶段）、`EmberRunMaps`（读图、挑战、深渊、团本）、`EmberRunRules`（纯规则）、`EmberRunBridges`（反射调 DungeonPlus） | 体力预留和退还、断线重连、重启后中止并退还 |
| 补给 | `EmberSupplyService` | 回复药商店、起步药（放进快捷栏右侧）、喝药提示 |
| 地图建造 | `p1/map/P1MapBuilder`、`P1MapLayout`；`resources/p1-book-maps.yml` | 按书里的章节生成白盒地图 |
| 命令 | `EmberCommand`（`/corerpg p1 …`） | 玩家：enter / run / abyss / shop / charm / title / trail / cosmetic（外观商店，D107；`buy <id> [coin\|t1\|t2\|t3]`、`try <id>`、`color\|flair\|glow\|anim <id\|off>`，D112）/ firstclear [族] [图] [blade\|charm]（D110）/ recruit [r01\|r02\|list]（D113）/ watch（团本倒下后换观战队友，D106）/ season [week\|season\|last]（D116）/ goals（D117）/ cosmetic pick <id>（商店页点击，D119）/ cosmetic buy <id> badge（余烬徽付）/ equip <uid> [swap [confirm]]（换上 / 免费互换强化，D120）/ route（下一次突破路线，D120）/ cosmetic（不带参数开商店页；`list` 聊天版；`pick <id>` 没有就开付款页；`buysel <coin|badge|t1|t2|t3|try>` 付款页按钮；`buy <id>` 不带付款方式开付款页，`buy <id> chat` 聊天预览，D121）/ top（玩家开赛季页，D121）。管理：runs firstclear / weaken [比例] / modifier <id>（下一局挑战强制周规则）/ heal / runs season preview（季末结算预览，不发奖）\| award <玩家> <season_id> \| badges <玩家> <n> \| goal <玩家> <featured\|abyss\|raid\|bounty> <n> |
| 参数源 | `plugins/CoreRpg/ember-v1.yml`、`plugins/CoreRpg/ember-v1-runs.yml`。`CoreRpg/src/main/resources/` 下各有一份，**两份要一起改** | 图（含 `loot:`）、`loot_bias:`、`raid_item:`、挑战、`rotation:`（含 `modifiers:`）、`abyss:`、`raids:`（`cap_group`）；`ember-v1.yml` 里有 `bounty:` |
| 怪物 | `plugins/MythicMobs/Mobs/EmberP1Main.yml` | 生成时的生命以 MM 为准 |
| 副本实例 | `plugins/DungeonPlus/dungeon/EmberQ01..Q07`、`EmberQ0R1` / `EmberQ0R2` / `EmberQ0R3`（团本；D106 起 `revive=true;number=0`，复活只由 CoreRpg 用 `dp revive <玩家> true true` 触发）；`config.yml` 里的 precache | DP 队伍：`/dp team` GUI，接受用 `/dungeon-team request accept <名>`；离队 `/dungeon-team quit`，队长解散 `/dungeon-team disband`（**没有** `leave`） |
| 菜单 | `plugins/TrMenu/menus/ember_hub.yml`（主菜单）、`ember_p1_adventure` / `_challenge` / `_abyss` / `_codex` / `_codex_gear` / `_forge` / `_gear` / `_season`（赛季 · 排行 · 周目标，唯一的称号 / 排行页，D116/D121）/ `_shop`（外观商店页，唯一的商店，D119/D121）/ `_shop_buy`（付款页，D121） | 进本一律走 `/corerpg p1 enter <key>`；`/corerpg enter raid` 是旧团本 |
| PAPI | `CoreRpgExpansion`：`p1_*`（`p1_pass_<key>`、`p1_raid_r01/r02`、`p1_abyss_*`、`p1_bounty`、`p1_next`、`p1_modifier`、`p1_loot_<图>`、`p1_title`、`p1_honors`、`p1_season`、`p1_goals`、`p1_goal_<g>`、`p1_badges`、`p1_season_last`、`p1_sboard_<w|s>_<榜>_<i>`、`p1_srank_<w|s>_<榜>`、`p1_shop_<id>`、`p1_shopprice_<id>` / `shopname_` / `shophow_`、`p1_shopmarks`、`p1_shopsel_*`（付款页）、`p1_awaken_route` …） | 菜单和 DP 进入条件都用它 |

## 4 工具

| 工具 | 用途 |
|---|---|
| `tools/p1sim/p1sim.py`、`selfcheck.py` | 单人从 Q01 推到 Q07 的节奏模型，读真实配置；`--normal-mods` 开普通版周规则（D94）；`--no-feat-normal` 关掉精选普通版印记、`--feat-farm` 专门刷精选图（D108）。selfcheck 应该 0 failed |
| `tools/p1sim/p2econ.py` | 60 人 8 周经济模型，**默认计入 §5.3 精工/成色币消耗**（`--no-forge-sink` 关；表多两列「累计精工/成色币」「精工均值」；`--abyss` 时附深渊各层通关率表，见 `docs/status/STATUS-ember-p2econ-forge-sink.md`），开关 `--abyss` / `--trade` / `--raid` / `--mods` / `--no-loot` / `--old-raid-item` / `--no-swap` / `--no-bounty` / `--normal-mods` / `--every-week` / `--ch-hp` / `--ch-atk`（D104）/ `--abyss-tiers` / `--abyss-quality` / `--abyss-fees`（E-review 试调深渊层表；输出带第 12 周层数分布和卡币天数）/ `--no-feat-normal` / `--feat-farm` / `--feat-separate`（D108）/ `--raid-rate b12|b13|revive`（团本通关率表，默认 revive，D106）/ `--goals`（D116 周目标两列：团本 + 周目标、深渊 + 周目标）/ `--fee-mark N`（D124 多余 T3 印记抵层费的币值，0 = 关，默认读 yml）/ `--fail-refund X`（D128 每天第一次挑战 / 深渊失败退的体力比例，0 = 关，默认读 yml）。卡币天数从 1.54.0 起拆成「没进下一层」和「已到第 10 层但当天层费不够」两项，降层不再透支币 / 印记（复查 #5）。看两件极品的提速要用 600 人以上、看各列最快路线（单列噪声约 ±1 周）。**凡是发奖励的改动都要先跑这个** |
| `tools/p1sim/chrate.py` | 首通 Q07 那一刻的挑战版通关率（刚首通 / 免费互换后 / 刃 T3 / 两件 T3），`--scale-hp` / `--scale-atk` 试调（D104） |
| `tools/p1sim/bossmoves.py` | D140 首领招式检查：每张图普通（书参考装）/ 挑战（T3+6）× 躲避 0.3/0.5/0.7 的通关率，5 种子 × 4000 局平均，旧 runs yml 对比当前（`git show <rev>:plugins/CoreRpg/ember-v1-runs.yml > /tmp/runs-old.yml`）。改首领招式时用它守 ±3 |
| `tools/p1sim/p1party.py` | 3～5 人团本模型，`--raid r01|r02|r03`，`--share-dmg`（D137 烬核分摊伤害扫描）；p1sim / p2econ 有 `--no-variety`（D138 对照）。参数 `--pool --weeks --dodge --trials --boss-hp --atk --k --j`；`--no-revive` 关掉 D106 复活；`--last-revive-hp` / `--last-revive-delay`（D118 最后阶段复活，默认读 yml `raid_revive:`） |
| `tools/p1map/book.py`、`gen.py` | 解析书里的地图章节，生成地图 |
| `tools/p1map/chal-smoke.sh`、`abyss-smoke.sh`、`raid-smoke.sh LEADER "成员…" [q07|q06|q05]`（r01 / r02 / r03 的地图）、`boss-test.sh LEADER "成员…" QMAP [比例]`（首领不 weaken 或只削到比例，看阶段和击杀用时） | 机器人冒烟：按书里的路线走，每个房间先 admin weaken，再由机器人击杀，结算是真的 |
| `tools/p1map/norm-smoke.sh NAME qNN`（普通版，每个房间削弱）、`newbie-run.sh NAME qNN`（不削弱、生存模式、喝药，死了就停）；`fight.sh` 加环境变量 `DRINK=1`，生命低于 11 时喝快捷栏的药 | 新手视角冒烟 |
| `scripts/check-dp-spawns.py`、`dp_fix_spawns.py`、`dp_map_reach.py` | 检查 DP 刷怪点（卡墙、走不到）。用法 `DP_MAP_ROOT=/workspace/minecraft/plugins/DungeonPlus/map python3 scripts/check-dp-spawns.py /workspace/minecraft/plugins/DungeonPlus/dungeon/EmberQ0*`，不带参数跑全部副本，应该 25 个都是 ok（D144 起多了余烬连战 EmberQ0B1；D150 起 runs yml 按任何两格缩进的图键切块，`rush:` 段不再混进 q07）。D129 起门格只要是空气或铁栏就算对（模板存档时门开着，director attach 时把空气 / 铁栏变成铁栏），别的方块才报错；团本 r01 / r02 按自己的块检查，不再混进 q07 |
| `scripts/db-dump.sh` | MySQL 备份到 `/workspace/backup/` |
| `tools/p1map/starter-equip-check.sh FRESHBOT` | D130/D131 实机检查：兑换 T1 刃看起步刃进背包，再把起步刃放回快捷栏、兑换 T2 刃看是否自动到手上（打印 PASS/FAIL，要新号） |
| `mineflayer-tests/tmp-p1/b.sh` | 机器人：join / quit / eval / chat。另有 `walk.sh`、`fight.sh`。**10-04 03:16 前后 `tmp-p1/` 整个被删了**（没进库），botd（`127.0.0.1:8765`，PID 71411）还在内存里跑。接口：`GET /join?name=X`、`GET /quit?name=X`、`GET /log?name=X&from=N`、`POST /eval?name=X`（body = async JS，作用域有 `bot` `bots` `wait` `con` `require`）、`GET /list`。1.61.0 冒烟用的临时客户端在 `/tmp/c144s/`（`b.sh`、`drive.py` = 按点 tp + weaken + 攻击直到日志出现 settle、`menus.py` = 开 TrMenu 页 dump lore）；botd 重启前要先从 git 历史外重建 botd.js（源码就是上面那 5 个接口，`require('../lib/proxy-login').joinPlay`） |
| `tools/p1sim/rushsim.py`、`rerollsim.py` | D144 余烬连战通关率（读 runs yml `rush:`，p2econ `--rush` 也用它）；D148 洗练到指定词条满档的期望次数 / 花费（锁 / 不锁） |

`tools/*` 被 gitignore 了，新文件要 `git add -f`。

## 5 备份和发布

- jar 备份在 `/workspace/backup/CoreRpg-<ver>.jar`，最新是 **1.63.2**（另有 `CoreRpg-1.63.1-pre-d155.jar` / `CoreRpg-1.63.1.jar` / `CoreRpg-1.63.0-pre-d154.jar` / `CoreRpg-1.63.0.jar`，以及 `CoreRpg-1.62.0-pre-d152.jar`），然后是 1.62.0（`CoreRpg-1.62.0-jdk21-broken-0355.jar` 是用错 JDK 编的坏包，别用），再是 1.61.0（还有 1.60.1、1.60.0、还有 1.59.0、 1.58.0 由国庆那边备份、1.57.0、1.56.0、1.55.0、1.54.0、1.53.0、1.52.1、1.52.0、1.51.0、1.50.0、1.49.0、 1.48.1、1.48.0、1.47.0）。
- 数据库备份是 `/workspace/backup/db-ember-authme-*-after-<ver>.sql`，最新是 after-1.62.0（`db-ember-authme-20261004-040856-after-1.62.0.sql`；部署前另有 `db-ember-authme-20261004-035614-before-1.62.0-restart.sql`），然后是 after-1.61.0（`db-ember-authme-20261004-031608-after-1.61.0.sql`；另有每小时的自动备份循环 `scripts/ember-backup-loop.sh`（8ee5349，日拷到 `/home/box/ember-db-backups`）；after-1.60.0、部署前另有 pre-1.60.0；还有 after-1.59.0、 after-1.55.0、after-1.54.0、after-1.53.0、after-1.52.1、after-1.52.0、after-1.51.0、after-1.50.0、after-1.49.0、 after-1.48.1、after-1.48.0、after-1.47.0、after-1.46.0、after-1.45.0、after-1.44.0、after-1.43.0、after-1.42.0，删 RevNewA 前的 pre-revnewa-delete；1.41.1 只在线 2 分钟，没有单独备份）。
- 发布就是推到 main，然后在 live 里 `git pull --rebase --autostash`。没有单独的 release 产物。

## 6 规则（必须遵守）

1. **数值纪律（书 §23.3）**：改数先改参数源（两份），再重跑模型，登记到源表，然后升 balance_version 和插件版本。不能自行增加奖励。
2. **绝不提交**：CoreRpg `config.yml`、AuthMe 配置（含数据库密码，不能打印）、`ladder.yml`、`calamity-state.yml`、`players/**`、`worlds.yml`、jar、世界、日志、`tmp-p1/`、Adyeshach npc json、`ember-v1-item.key`、`login-runtime/*`、HolographicDisplays `database.yml`、MythicMobs SavedData、`plugins/CoreRpg/p1-runs/`（含 leaderboard.yml、season.yml、season-archive/）。
3. `server-runtime/ops.json` 保持 `[]`。
4. **只按确切 PID 杀进程**，绝不用 `pkill -f` 或 `pgrep -f`。
5. 游戏内的击杀命令只在副本里用，而且要限定半径。机器人死了就停掉 fight.sh 或让它下线。
6. 提交署名 `Picadoo <Picadoo@users.noreply.github.com>`。推送用 `git pull --rebase --autostash -q && git push -q origin HEAD:main`。提交小、推送勤。

## 7 未完成

- **D163–D166 已上线（1.65.3，10-04 13:41）**：破甲替代拆分、裂身纹退池、范围 / 新手提示文案；冒烟 FreshQ100（Q01 kite 首通，提示出现）、FreshQ101（强制厚甲精英 ×1.600）、FreshQ102（R01 招募提示）全 PASS。B02 剩第三排 / 余烬纹 / 定身纹。
- **D160–D162 已上线（1.65.1，10-04 11:31）**：连战改为失败可无限重试、每周首通领奖（实测 FreshQ74：失败后再进 → 通关 claim → 再通关 practice）；资产交付闭环。仍未测：真实断库、自动入库 + 故障、快照恢复失败重试、多人同一件；.dat 与 DB 之间仍有亚 tick 窗口（完整回滚另有裁决）。真实装备试玩 Q01–Q07 + 连战：不躲招的 bot 全部倒下（与 D85 记录的「dodge-0 会死在 Q01 第三房」一致），见试玩报告。`tools/p1map/realgear-run.sh NAME qNN BT BE CT CE LV` 跑一局（会先给首通 / 解锁 / 等级 / 体力 / 装备）。
- **D159 洗练装备库重复件已上线（1.64.2，10-04 10:30；FreshQ51 冒烟 PASS）**：背包没有重复件时可用装备库里未锁定/未收藏的掉落件；hub 扭蛋图标改 ender pearl；扭蛋页补「外观商店可用币买 / 已买抽到返光屑」；见 `docs/status/STATUS-ember-reroll-gearlib-d159.md`。

- **外观扭蛋（CoreGacha 1.0.0，10-04）**：
  - 管理员：`/gacha admin give-tickets|give-shards <玩家> <n>`、`inspect <玩家>`、`simulate <池> <n>`、`failnext <玩家>`（测试：下一次 CoreRpg 发放失败 → 整批回滚退券）、`reload`。所有券 / 光屑变动都在 `gacha_ledger`，每一抽在 `gacha_pull`。
  - 测试号 FreshG01（有 1880 光屑、余烬灯灵宠物、锦鲤灵等）、FreshG02（只进过服）、**FreshG03**（04:51 craft/spark/exchange 冒烟）；**下次从 FreshG04 起**（`Fresh` 前缀已不上榜）。
  - 已知限制：主扭蛋页把两个池写死（下个季节换限定池要改 `gacha.yml` 再跑 genmenus）；扭蛋称号只在聊天显示（不改头顶名牌，免得和 CoreRpg 的称号打架）；宠物只在主城 `ember_hub`；硬保底 80 实际几乎碰不到（每件传说 0.00079%，软保底从第 61 抽起很快就出）；CoreRpg 外观「已拥有」是快照，发放时再查一次。
  - **已冒烟（10-04 04:51，FreshG03，未重启）**：光屑兑换 `/gacha craft`、火花兑换 `/gacha spark`（含已拥有拒绝不扣火花）、币/徽换券 `/gacha exchange coin|badge`、每日换券上限 5；见 `docs/status/STATUS-ember-gacha-craft-spark-smoke.md`。先前 FreshG01 也过了 craft + exchange_badge（`docs/tests/smoke-gacha-2026-10-04.md`）。
  - 没测：在线 / 主线结算发券（真时长）；10-08 限定结束后剩余火花折光屑（第一次到点后看 `gacha_ledger` 的 `spark_leftover`，勿为测而改 gq26 全服日期）。

- **新内容批 10-04 + Part D（D144–D150，1.61.0）**：设计 / 验收在 P2 草案 §5zb，源表 13.70–13.72。
  - p2econ 两件极品最快（600 人 12 周，关 / 开）+0.25 / −0.06 / −0.03 周，通过，没砍奖励。
  - 线上冒烟过：花样委托进度、连战进本 → 三首领 → 结算、国庆币兑换（称号 + 5:1 封顶 40）、锁定洗练、各菜单、词条池四分位百分比（§5zb）。
  - **D150 已随 1.62.0 部署并验证**（原来的说明：）连战失败行在已扣次数时也说「还没开打，本周次数没有扣」，源码修在 main（9af8556，构建 + 单测过）。下次部署：pom / plugin.yml 升 1.61.1，备份 jar，重启后看日志 `MySQL connected`，再用新号打一局连战故意倒在第一个首领之后，看失败行说「本周的连战次数已用」。
  - **限时清房花样委托已冒烟（10-04 06:47，FreshQ43，`tools/p1map/timed-bounty-smoke.sh`）**：管理员首通 Q01 + `runs variety event:r1` → 普通版重打，12 秒清完限时房，结算「限时清房完成」+「花样委托 … ✔ 限时清房达标 1/1」+20 币；见 `docs/status/STATUS-ember-variety-timed-bounty-smoke.md`。
  - **D153 深渊层费复查（10-04 08:10，纯离线，不改）**：打深渊的人第 8 周约 1.4k 币是自愿换层费，不是卡穷；未重启、未改 jar，FreshQ45 未用。见 `docs/status/STATUS-ember-abyss-coin-2026-10-04.md`。
  - **D155 铁卫已上线（1.63.2，10-04 08:52；FreshQ46 冒烟 PASS）**：第 6 条 P2-8 周规则 `wall` melee→heavy、挑战专用；模型 `out-p2econ-mods-d155.md` 通关率差 +0；STATUS `docs/status/STATUS-ember-modifier-wall-d155.md`。
  - **D154 卫士潮已上线（1.63.1，10-04 08:21；FreshQ45 冒烟 PASS）**：第 5 条 P2-8 周规则 `guards` ranged→heavy、挑战专用；模型 `out-p2econ-mods-d154.md` 通关率差 +0；STATUS `docs/status/STATUS-ember-modifier-guards-d154.md`。
  - **D152 卸甲已上线（1.63.0，10-04 07:48；FreshQ44 冒烟 PASS）**：第 4 条 P2-8 周规则 `disarm` heavy→melee、挑战专用；模型 `out-p2econ-mods-d152.md` 通关率差 +0；STATUS `docs/status/STATUS-ember-modifier-disarm-d152.md`。
  - 没测：烬核同心（要 R03 多人）；三人连战。活动结束后兑换已在 D156 冒烟（临时改日期，见上国庆节）。
  - 冒烟时机器人倒下：连战伤害 ×1.45 下 T1 号一定会倒，创造模式挡不住（伤害走 ledger），要每秒 `corerpg p1 heal <号>`。

- **横向成长（D141–D143，1.60.0）**：参数全在 `ember-v1-growth.yml`（两份一致），设计 / 终验数字在 P2 草案 §5za，全表 `tools/p1sim/out-growth-d141-d143.md`。
  - 数值已**冻结**（调参 v6 + 余烬词条 ×0.5）；之前几轮反复调参，别再微调，除非有真人数据。Q03 / Q04 普通 0.5 档 +3.3～3.6 是按「约 3 点」接受的。
  - **勋记解锁已冒烟（10-04 04:20，FreshQ42，`tools/p1map/honor-smoke.sh`）**：`honor test all` → 7/7，封顶后币 +4% / 碎片 +1 / 层费 −5% / 深渊受伤 −2% / 洗练币 −14.5%（0.90×0.95），`clear` → 0/7。
  - **重复件洗练 + 保底已冒烟（FreshQ34，1.60.1，`docs/tests/smoke-2026-10-04-growth-hooks.md`）**：`givedup blade` 发 src=drop 重复件 → 洗练预览 / 确认 ok；碎片连洗到第 6 次保底必出上限档。勿再当成「未测」。
  - 两件极品最快路线（p2econ 600 人 12 周）已跑完：−0.20 / +0.06 / −0.42 周（0.3 / 0.5 / 0.7），在 ±0.5 内。
  - 下一个测试号从 **FreshQ45** / FreshG04 起。

- **国庆活动（D139，1.58.0）**：
  - 全部参数在 `plugins/CoreRpg/ember-v1-festival.yml`（两份要一致）。10-08 00:00 CST 后自动关门、不用重启；提前关：`enabled: false` + `/corerpg p1 fest reload`。
  - **地图不在 git 里**（DP 地图都不进库）：新检出要先 `/corerpg p1 fest mapbuild`（从 `ember_daily_tide_v1` 换色生成 `plugins/DungeonPlus/map/ember_fest_gq26_v1`），然后重启让 DP 预缓存。
  - **活动结束后路径已冒烟（D156，10-04 09:17，FreshQ47，临时改 end→reload→立刻还原）**：主菜单「国庆纪念」入口、装备页烟火符格、常驻价 15000 币购符、结束后足迹剩币购、剩币换徽、徽价门闩（要 300）；见 `docs/status/STATUS-ember-fest-after-end-smoke.md`。仍没测：徽付满 300 真成交；真人打活动本用时；三人活动本。
  - 国庆币活动后没有用处（NI 物品留在背包，可留作纪念）。下次活动：复制 festival yml 改 id / 日期 / 地图，`EmberCosmetics.ALL` 里的称号足迹现在是写死的 gq26 两条，换活动要加新条目。
  - 管理员：`/corerpg p1 fest coins <玩家> <数量>`（发币）、`fest reset <玩家>`（清护符 / 足迹 / 今日次数）；测试脚本 `tools/p1map/fest-tp-smoke.sh NAME [削弱比例]`。

- **spigot 生命上限（1.36.3 已修）**：`spigot.yml` 的 `maxHealth.max` 原来是 20000，团本首领（3 人 35100）和高层深渊首领都被悄悄截断，所以之前团本通关比模型简单。现在是 1000000，scaleHealth 截断时会打警告。
- **团本 TTK**：书 §18.4 第 4 条要求模型和实测差不超过 20%。已经实测：
  - R02 第二阶段实机出现（首领削到 60%，日志 `boss phase 2 at 50% (砸地)`）。
  - 单个机器人对首领的 DPS 185，模型 190（−3%）。
  - 完整 3 人击杀用时还没测成：机器人不躲技能，1 秒一次治疗也扛不住，R01 削到 40% 时团灭了。要真人或会躲的机器人。
- **后期币堆积**：深渊层费仍是大额出口（D72）。**10-04 07:25**：p2econ 已默认计入精工/成色重铸币消耗（与 `EmberUpgradeRules` 同价；`--no-forge-sink` 复现旧表），无深渊第 8 周币中位约 4.0 万→3.3 万（累计精工/成色约 7.8k）；有深渊时币仍约 1.4k，层费为主、精工次之。**10-04 08:10 复查（D153）**：这 1.4k 是模型策略把余币全换层费，不是卡穷——先精工的深渊玩家精工做满周 / 卡精工周数 / B 都和不打深渊一样，两件极品更快；降层费余币也不涨。不改，见 `docs/status/STATUS-ember-abyss-coin-2026-10-04.md`（工具 `tools/p1sim/abysscoin.py`、`p2econ.py --abyss-forge-first`）。深渊通关率表见 `docs/status/STATUS-ember-p2econ-forge-sink.md` / `tools/p1sim/out-p2econ-forge-sink-d050.md`。要发币的新内容必须先过 p2econ。D107 外观商店是只做展示的花币出口（全套 7.8 万），入口在**装备页第 43 格**（不是 44；D136 起返回键回到打开它的那一页，按钮走 `/corerpg p1 cosmetic from gear|season`），D121 起第 43 格、进服消息（毕业玩家的 [外观商店] 按钮，1.54.0 才补上按钮）、赛季页都打开同一个商店页 `ember_p1_shop`（付款在 `ember_p1_shop_buy`），价格和说法只在 EmberCosmetics；D112 起也能用多余印记付（T1/T2/T3 = 1/2/4 点，1 点 = 50 币，每阶留 8 枚），另有印记专属的主城刃辉光和称号动效。
- **全流程验收（`docs/reviews/review-fullpath-2026-10-03.md`）**：D120–D127 已做（P2 草案 §5s）。没改的：
  - 后期 #6 在 1.53.0 做了（D128）：每天第一次挑战 / 深渊失败退一半体力，两者共用一次（P2 草案 §5t）。
  - D125 团本倒下提示 1.53.0 实机复测过（R01，§5t）。全员倒下后 DP 的结束字幕「本局失败，即将返回」这轮没截到（只截到全员离开的那条）。
  - 旧的仅管理员可见菜单（`ember_hub_legacy` 一系）没动。
- **快速复查（`docs/reviews/review-recheck-2026-10-03.md`）**：全部处理（D130–D134，P2 草案 §5u）。没改的：
  - 深渊卡币（部分 #5）：**D135 拍板不调**（用户 10-03 09:51）。第 8～10 层费和每阶留 8 枚不动，第 10 层反复打就是后期花币出口；模型见 §5u（卡币时 T3 印记都在保留数内，`fee_mark_coin` 用不上）。
  - 商店返回键在 1.55.0 修了（D136，§5v）：回到打开商店的那一页，从聊天栏打开就关闭。
  - D134 毕业周按比例只有单测，没有实打一次周六的 Q07 首通。
- **后期复查（`docs/reviews/review-endgame-2026-10-03.md`）**：D109–D115 已做（P2 草案 §5q）；原来留着的最后阶段复活、商店页、赛季榜和周目标在 1.51.0 做了（D116–D119，§5r）。旧物品名重写在 D127 做了（进服重写显示）。
- **赛季（D116）**：第 1 赛季 2026-09-28～10-25，10-26 0 点后第一分钟自动结算（日志 `[P1 season] S1 settled`、全服广播、`season-archive/S1.yml`）。目前榜上只有测试号会打本，全都被 `leaderboard_exclude` 挡掉，所以 S1 很可能是空结算；真实玩家第一次上榜时看一眼 `/corerpg p1 season`。`runs season preview` 可以随时预览。
- **留给策划（中后期点评）**：团本体力 60 的备选（D105 用了「算 2 局」）。团本倒下复活（D106）、花币出口（D107）、精选给中期奖励（D108）已做。
- **D106 已知限制**：团本倒下后 `/dp leave` 10 秒内输两次仍然能走（算放弃，不复活不结算）；断线走 DP 的离线保护。复活点是「下一个房间开打 / 首领现身 / 首领转阶段」，D118 起首领所有转阶段之后降到 20% 以下、有人倒下时，20 秒后再复活一次（每局一次）。
- **交易**：D73 否决开市，只有规则。
- 旧玩法试玩遗留：`docs/reviews/PLAYTEST-2026-10-01-newplayer.md` 的「未解决问题」，P1 默认模式下大多已经不在主路上。
- 日志噪音很小。MythicMobs `ExampleItems.yml` 的 `GOLDEN_HELMET`→`GOLD_HELMET`（1.12）已在 **822468e** 修过。
- 剩下的候选：
  - 精选周规则：D152「卸甲」+ D154「卫士潮」+ D155「铁卫」已上线（6 条）；计时 / 黑暗仍否决（原因见 P2 草案 §5e）。若再加，优先 remap / potion_cap / swap_rooms 一类现有字段。
  - 主城氛围：书 §1 只要求「一句背景」。
  - 机器人点 TrMenu 时用 `bot.clickWindow(…).catch(()=>{})`，不要 await（TrMenu 不回确认包，await 会超时）。
- 团本 DP 队伍：冒险页团本图标右键 = `/corerpg p1 recruit r01|r02`（D105），给在线的已首通 Q07 玩家发可点的 [申请入队]，队长收到 DP 自带的 [同意] [拒绝]（只这一条，D113），拒绝后有「已拒绝 X」。招募挂 10 分钟：图标 lore `%corerpg_p1_recruits%`、`/corerpg p1 recruit list`，已首通 Q07 的人上线时也会看到。也可以用 GUI（`/dp team`）。
