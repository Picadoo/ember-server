# 余烬 · 额度内内容 / 可维护性 Backlog


## Progress snapshot — 2026-09-29 01:03 Asia/Shanghai (总控)

### 用户拍板（高优先）
- **票→体力**：否决票券观感；日回体力 + 按玩法消耗；菜单显示，玩家不打指令。
- **日常 DNF 式地下城**：选本→进门→分房清怪→Boss；要好玩，非换皮短廊。
- **同服多世界**：已有 Multiverse-Core(+Portals) + DP 实例图；**禁止一图一服**。

### 已结（近期）
- **S0 体力账户/进本扣**：PASS（PAPI parse 软债已勾销）
- **S1 余烬窟·庭院**：门2→Boss **PASS**（`87e71a8`）
- **S2 焦骨甬道 / 残誓地窖**：设计→怪→图→DP/菜单→独立验收 **PASS**（测报 `0a8dbdd`）
- **S3 潮蚀水道 / 断塔回廊**：设计→怪→图→DP/菜单→独立验收 **PASS**（`ae0ecd4` · 测报待推 · CoreRpg 1.15.17）
- 体力药使用闭环 **PASS**（1.15.16）；B0.3 菜单去指令 **PASS**；周本菜单 YAML 复测 **PASS**
- 枢纽工坊 NPC（烬砧/余晶/灰粮）**已落地**（1.15.6 起）

### 进行中
- 文案薄窗暂缓；**B-flex-2 可装配轻技**已批 A · 待插件（荐 A · 位移 · `design-ember-flex-skill-slot-pilot.md`；批准 `d1e88e4`）；与 B2.x / 文案薄窗 **不捆**。

### 刚结
- ~~**B-flex-1 副手/饰品位试点**~~ → **PASS · 勾销**（设计 `7dda194` · 批准 `eb3c843` · NI `52f817a` · 插件 `08b4cd4` · 测 `1453a7c` · close `d333b01`；报告 `docs/STATUS-ember-offhand-slot-pilot-test.md`；CoreRpg **1.15.24**；轻测 PASS；技能装配已升 **B-flex-2**；烬砧材料→部件仍 soft；四件甲/锻炉大改未做；**勿宣称 B0.1 已清**）
- **B2.17 天赋菜单同句英词 `cost`→消耗**：**PASS · 勾销**（设计 `3a3a226` · 批准 `0eb15c2` · 施工 `9631343` · 测 `dabf579` · close 本提交；报告 `docs/STATUS-ember-talent-cost-copy-test.md`；13 行类型 lore，数值 2/3/4 不变；不改 talent.yml / unlock / cost 数值；不附 NI/套装/精英壳）
- **B2.16 天赋菜单类型英词 `passive`/`skill` 人话化**：**PASS · 勾销**（设计 `34e61dd` · 批准 `7c1c408` · 施工 `39ca8f6` · 测试 tips `e342990` / `7db1160` · close `22c2d40`；报告 `docs/STATUS-ember-talent-type-copy-test.md`；13 行类型 lore：`passive`→被动、`skill` 去冗余；cost 数字不变，方案 B 已由 **B2.17** 勾销）
- **B2.15 天赋菜单裸属性键人话化**：**PASS · 勾销**（设计 `26d7895` · 批准 `f4e2bcd` · 施工 `da17196` · 测 `790dc32` · close `a43010d`；报告 `docs/STATUS-ember-talent-attr-copy-test.md`；奖励页 NI id / 套装 `gear_ember_*` 未宣称已清）
- **B2.14 天赋菜单 §8 `nodeId`/前置人话化**：**PASS · 勾销**（设计 `9e129cc` · 批准 `1460fa0` · 施工 `37c4bd6` · 测 `2f6120b` · close `3b00eff`；报告 `docs/STATUS-ember-talent-nodeid-copy-test.md`；裸属性键已挂 **B2.15**；奖励页 NI id / 套装 `gear_ember_*` 未宣称已清）
- **B2.13 天赋菜单 `*_cap` 文案人话化**：**PASS · 勾销**（设计 `c86ebd6` · 批准 `aa4a7bb` · 施工 `89b7432` · 测 `f600687` · close `33218c8`；报告 `docs/STATUS-ember-talent-cap-copy-test.md`；§8 nodeId/前置 已挂 B2.14；奖励页 NI id、套装 `gear_ember_*` 未宣称已清）
- **B2.12 灾厄 OP 调试拒门去斜杠**：**PASS · 勾销**（设计 `1e109b3` · 批准 `708c62a` · 施工 `9be6ae4` · 测 `095be05` · close `483215f`；不改 `text=`/人数/loot；灾厄 OP #6 已闭环）
- **B2.11 主线 quest「深渊票」两句薄扫**：**PASS · 勾销**（设计 `4a2a671` · 批准 `ca68b64` · 施工 `8605d10` · 测 `a538d26` · close `5378b23` · 双路径 live+src；灾厄 OP #6 已由 B2.12 闭环）
- **B2.10 DP 局内/超时「票」→体力文案**：**PASS · 勾销**（设计 `f2eb738` · 批准 `bf174fc` · 施工 `9311a72` · 测 `95c9fbc` · 九句 DP 玩家 message 已对齐；Q1/Q2 保留为残余，灾厄 OP #6 已由 B2.12 闭环）
- **B2.9 挂机庭/天梯去内部代号**：**PASS · 勾销**（设计 `a8b8ef2` · 批准 `ed4f54c` · 施工 `d0c9ef4` · 测 `796dc1b` · backlog close `79f810f` · HD 四板说明行 + `ember_ladder` lore/tell 已去内部代号；location/数值/PAPI 键不动；灾厄 OP #6 已由 B2.12 闭环）
- **B2.8 DP 进本拒门去斜杠**：**PASS · 勾销**（设计 `83d641a` · 批准 `4c99c21` · 施工 `e7bc1eb` · 测 `12846c7` · backlog close `944c017` · 周/团/深渊 level 拒门 + 盟 Boss 拒门/开场文案已去斜杠；灾厄 OP #6 已由 B2.12 闭环）
- **B2.7 主线 quest「日票/周票」台词薄扫**：**PASS · 勾销**（设计 `b7ccbb8` · 批准 `a6c48bc` · 施工 `4eb9af8` · 测 `f2edc25` · backlog close `e950efe` · 双路径 6 句 · live/src 无「日票/周票」）
- **S0 菜单「日票」假文案对齐**：**PASS · 勾销**（施工 `71473da` · 测 `8340ac3` · TrMenu 三页 · quest 台词已随 B2.7 勾销）
- **灾厄奖励预览真分页**：**PASS · 勾销**（施工 `62f444a` · 测 `da75e9e` · 日箱≠尾刀 · 空壳 tell 已删 · calamity/MM/loot 未动）
- **深渊奖励预览真分页**：**PASS · 勾销**（施工 `4805c11` · 测 `efed44b` · TrMenu 三档摘要 · 空壳 tell 已删 · abyss/MM/loot 未动）
- **周本/团本奖励预览真分页**：**PASS · 勾销**（施工 `991e863` · 测 `22616e5` · TrMenu 双真页 · 空壳 tell 已删 · DP/MM/loot 未动）
- **非日常进本体力灰显**：**PASS · 勾销**（施工 `23a816e` · 测 `6bda99e` · CoreRpg **1.15.23** · 深渊/周本三态/团本/精英 · cost 30/45/50/40 · over_chance* 未动）
- **挂机二档菜单 UX 对齐**：**PASS · 勾销**（施工 `0deda12` · 测 `f0ce1fd` · 枢纽/规则双软顶 · over_chance* 未动）
- **B0.4 / B2.5 挂机二档施工+短样**：**PASS · 勾销**（施工 `5301556` · 测 `99898cc` · CoreRpg **1.15.22** · 一档 0.25 / 二档 0.08 @≥2×cap · 短样 0.263/0.080 · 外推 ~3.3×→~2.4× · **不宣称封死通胀** · 禁墙钟 2h）
- **B0.4 / B2.5 挂机二档定稿设计**：已批 **A**（`design-ember-afk-b04-tier2.md` · `over_chance_2=0.08` @≥2×cap · 短样外推验收）
- **B0.4 挂机 2h 产出基线 A2**：**PASS · 计算外推**（`STATUS-ember-afk-b04-2h-baseline.md` · tip `09aa341` · 用户否决墙钟 2h · 短样→表 · **未改** `over_chance`）
- **B0.4 挂机 2h 基线采数设计**：已批 **A**（`design-ember-afk-b04-2h-baseline.md` · 只采数不改 `afk_caps`）
- **B1.3 精英 Boss TTK 关账采数 A**：**PASS · 勾销**（`STATUS-ember-b13-elite-ttk-close.md` · tip `996f035` · Boss TTK **65.5s** · 全本 **535.3s** · 剩血 62% 观察 · 保持 5200/12 · 非砍血）
- **B1.3 精英 Boss TTK 关账设计**：已批 **A**（`design-ember-b13-elite-boss-ttk.md` · 默认不动 5200/12）
- **Raid/GuildBoss kill-any live 复测**：**PASS**（`STATUS-ember-killany-live-retest.md` · tip `87b9193`/`7c8e61b` · 静态+live 可关）
- **Raid/GuildBoss kill-any live 测法设计**：已批 **A**（`design-ember-raid-guildboss-live-harness.md` · 零玩法改）
- **团本使徒 TTK 校准**：校准 ΔTTK **PASS**（L1 63.7s / L2 56.8s · Δ=+10.83% · 门已还原 min=3 · `aff517c`）
- **团本使徒 TTK 可测性设计**：已批 **A**（`design-ember-raid-apostle-ttk-calib.md` · 临时 min=1 校准）
- **日常第三拍双线收口**：霜晶霜暴 + 锈轨砸地 **均 PASS**（霜 `3e8c969`/`0de8c50` · 锈 `d1e57ef`/`affd058`）
- **锈轨第三拍同构设计**：已批 **A**（`design-ember-daily-rail-third-beat.md` · 矿监砸地读条）
- **日常第三拍方案 A（霜晶霜暴）**：**PASS**（施工 `3e8c969` / 测 `0de8c50` · 五条全过）
- **日常第三拍试点设计**：已批 **A**（`design-ember-daily-third-beat.md` · 霜晶霜暴读条）
- **周本深室 Boss 前压评估**：已批 **B 不施工**（`design-ember-weekly-boss-prep.md`）
- **PAPI 体力 parse 软债**：**勾销**（`STATUS-ember-papi-stamina-parse-retest.md` · 在线自解析 12 stub PASS）
- **精英厅二链式评估**：已批 **B 不施工**（`design-ember-elite-hall2-chain.md`）
- **B2.2 断塔 wave2b 坐标同步**（`55d1b58` · 对齐防坠落地）
- **断塔环廊防坠方案 A**：**PASS**（施工 `29fb133` / 测 `870aba0` · 近阶偶发掉底厅软观察不挡）
- **断塔环廊防坠设计**：已批方案 A（`design-ember-daily-spire-ringfall.md`）
- **日常节奏全杠杆里程碑挑刺**：**无挡级**（`STATUS-ember-daily-rhythm-leverage-closeout-review.md` · 软债1～3还清）
- **B2.2 DP 坐标总表刷新**（`8c8c38e` · 七线分表 + 房2/前压/周本中核/精英厅一）
- **Boss 前压扩线（断塔/焦骨/地窖）**：**PASS**（施工 `8a69b97` / 测 `10cdc36` · 五条全过 · 合计 +6）
- **庭院/潮蚀房2 start链式**：**PASS**（施工 `eb81942` / 测 `b310aa4`）
- **庭院/潮蚀房2 start链式设计**：已批方案 A（`design-ember-daily-courtyard-tide-room2-chain.md` / `f62bb35`）
- **消 kill-any 剩债**：**PASS**（施工 `3194355` / 测 `79d4ccb`；Raid/GuildBoss live SKIP 不挡）
- **消 kill-any 剩债设计**：已批方案 A（`design-ember-killany-debt-cleanup.md` / `082dc0e`）
- **精英周本厅一链式+消 kill-any**：**PASS**（施工 `03ee076` / 测 `44f8ac1`）
- **精英周本波次差异设计**：已批方案 A（`design-ember-elite-wave-variance.md` / `eb2d042`）
- **周本中核链式重叠**：**PASS**（`75e06b0` / 测 `a63bbfb`）
- **周本波次差异设计**：已批方案 A（`design-ember-weekly-wave-variance.md` / `6a1f8c2`）
- **霜晶房2链式重叠**：**PASS**（`36e98c8` / 测 `91eee00`）
- **日常七线第二房节奏扩线收口挑刺**：**无挡级**（`STATUS-ember-daily-room2-expand-closeout-review.md`）
- **源码 join / 灾厄广播去指令同步**：**PASS**（`30886e2` / 测 `ec7dc6f`）
- **B1.4 软抛光**（8 条等级 hint）：**PASS**（`ee3cfbc` / 测 `30261bd`）
- **B1.4 硬验收**（quest/join 无斜杠命令教学）：**PASS**（`design-ember-b14-quest-join-copy.md`）
- **地窖房2链式重叠**：**PASS**（`dd99538`）
- **焦骨房2链式重叠**：**PASS**（`2ad874e` / 测 `495c1ea`）
- **日常第二房/Boss前压试点**（庭院·潮蚀·断塔）：**PASS**（`3459d53` / 测 `cce784b`）
- **日常软尾巴「第二房复印机」扩线**：七线杠杆齐
- **日常软债 · 奖励预览真分页**：**PASS**（`ember_daily_rewards` · `4517021` / 测 `6e2abc6`）
- 锈轨真侧袭 + enter 冷却 chat **PASS**（CoreRpg 1.15.21）
- 日常体验收口挑刺：**无挡级必改**（`STATUS-ember-daily-ux-closeout-review.md`）
- 出本 5s 冷却提示 + 庭院门宽 ×9 **PASS**（CoreRpg 1.15.20 · `3299ae6` / 测 `faee945`）
- 枢纽 hitbox purge + 灰烛 quest 去指令 **PASS**（CoreRpg 1.15.19）
- **门吏 · 灰钥** `ember_dungeon_clerk`：设计→落地→验收 **PASS**（`7bf3de9` / 测报）
- **S4 霜晶裂隙 / 锈轨矿道**：设计→怪→图→DP/菜单→独立验收 **PASS**（CoreRpg 1.15.18）
- 挑刺必改 1～3：Boss 装掉 / hub 七线文案 / 体力灰显 **PASS**
- 挑刺必改 4 波次差异：七线落地；霜晶 door1 链式返工复测 **PASS**（`14464c6`）

### 仍挂
- ~~**S0 菜单「日票」假文案**~~ → **PASS · 勾销**（`71473da` / `8340ac3`）；~~quest 台词 B2.7~~ → **PASS · 勾销**（`4eb9af8` / `f2edc25` · close `e950efe`）
- ~~**周本/团本奖励预览空壳 tell**~~ → **PASS · 勾销**（`991e863` / `22616e5`）
- ~~**深渊奖励预览空壳 tell**~~ → **PASS · 勾销**（`4805c11` / `efed44b`）
- ~~**灾厄奖励预览空壳 tell**~~ → **PASS · 勾销**（`62f444a` / `da75e9e`）
- 精英奖励预览：hub 一点进本、**无 P / 无独立菜单** → 本轮不挂「空壳 P」债（若要做须另开子菜单壳；证据不足勿硬开厚壳）
- ~~**B-flex-1 副手/饰品位试点**~~ → **PASS · 勾销**（设计 `7dda194` · 批准 `eb3c843` · NI `52f817a` · 插件 `08b4cd4` · 测 `1453a7c` · close `d333b01`；报告 `docs/STATUS-ember-offhand-slot-pilot-test.md`；CoreRpg **1.15.24**；轻测 PASS；技能装配已升 **B-flex-2**；烬砧材料→部件仍 soft；四件甲/技能与锻炉大改未做；**勿宣称 B0.1 已清**）
- **B-flex-2 可装配轻技 1 槽试点** → **已批 A · 待插件**（设计 `d1e88e4`；批准 `docs/STATUS-ember-flex-skill-slot-pilot-approve.md`；荐 A · **位移**「余烬踏步」· `docs/design-ember-flex-skill-slot-pilot.md`；1 槽 TrMenu；勿两套一起上；不动誓约主动/体力门/四件甲/锻炉/刃护符；验收静态 rg+菜单轻测；禁 wall-clock/DPS；不叫挑刺；软挂烬砧材料→部件；与文案薄窗无关；**勿捆 B2.x**；**勿宣称 B0.1 已清**）
- ~~**B2.17 天赋同句 `cost`→消耗**~~ → **PASS · 勾销**（设计 `3a3a226` · 批准 `0eb15c2` · 施工 `9631343` · 测 `dabf579` · close 本提交；报告 `docs/STATUS-ember-talent-cost-copy-test.md`；与 B-flex 不捆；文案薄窗暂缓）
- 奖励页 NI id / 套装 `gear_ember_*` 未宣称已清
- 断塔近阶偶发掉底厅（软观察 · 日刷频繁再升方案 B）
- 霜晶/锈轨无 Boss前压（设计刻意 · 口碑点名再动）
- AFK 二档未封死通胀（软残余）
- B0.1 无新证据不重开
- ~~**B2.13 天赋 `*_cap` 文案**~~ → **PASS · 勾销**（设计 `c86ebd6` · 批准 `aa4a7bb` · 施工 `89b7432` · 测 `f600687` · close `33218c8`；报告 `docs/STATUS-ember-talent-cap-copy-test.md`）
- ~~**B2.14 天赋 §8 nodeId/前置**~~ → **PASS · 勾销**（设计 `9e129cc` · 批准 `1460fa0` · 施工 `37c4bd6` · 测 `2f6120b` · close `3b00eff`；报告 `docs/STATUS-ember-talent-nodeid-copy-test.md`）
- ~~**B2.15 天赋裸属性键人话化**~~ → **PASS · 勾销**（设计 `26d7895` · 批准 `f4e2bcd` · 施工 `da17196` · 测 `790dc32` · close `a43010d`；报告 `docs/STATUS-ember-talent-attr-copy-test.md`；奖励页 NI id / 套装 `gear_ember_*` 未宣称已清）
- ~~**B2.16 天赋类型英词 `passive`/`skill` 人话化**~~ → **PASS · 勾销**（设计 `34e61dd` · 批准 `7c1c408` · 施工 `39ca8f6` · 测试 tips `e342990` / `7db1160` · close `22c2d40`；报告 `docs/STATUS-ember-talent-type-copy-test.md`；方案 B `cost`→`消耗`已挂 **B2.17**）
- ~~**B2.17 天赋同句英词 `cost`→消耗**~~ → **PASS · 勾销**（设计 `3a3a226` · 批准 `0eb15c2` · 施工 `9631343` · 测 `dabf579` · close 本提交；报告 `docs/STATUS-ember-talent-cost-copy-test.md`；奖励页 NI id / 套装 `gear_ember_*` / 精英预览壳未宣称已清）
- ~~**B2.12 灾厄 OP #6**~~ → **PASS · 勾销**（设计 `1e109b3` · 批准 `708c62a` · 施工 `9be6ae4` · 测 `095be05` · close `483215f`）
- ~~**B2.11** quest「深渊票」两句~~ → **PASS · 勾销**（设计 `4a2a671` · 批准 `ca68b64` · 施工 `8605d10` · 测 `a538d26` · 双路径 live+src；灾厄 OP #6 已由 B2.12 闭环）
- ~~**B2.7 主线 `quest.yml`「日票/周票」台词**~~ → **PASS · 勾销**（设计 `b7ccbb8` · 批准 `a6c48bc` · 施工 `4eb9af8` · 测 `f2edc25` · close `e950efe`）
- ~~**挂机二档菜单 UX**（非改数）~~ → **PASS · 勾销**（`0deda12` / `f0ce1fd`）
- ~~进本 `start-interval` 冷却 chat 转发~~ → **PASS · 勾销**（CoreRpg 1.15.21 启发式 tell+退还 · 测 `STATUS-ember-daily-rail-flank-cooldown-chat-test.md` · 非捕 DP 回执）
- ~~**非日常进本体力灰显**~~ → **PASS · 勾销**（`23a816e` / `6bda99e` · CoreRpg 1.15.23）
- ~~**B0.4 / B2.5 挂机二档数值**~~ → **施工+短样 PASS · 勾销**（`5301556` / `99898cc` · 0.25/0.08 · 外推 ~2.4× · 不封死通胀）
- ~~**B1.3 精英 Boss TTK**~~ → **关账采数 PASS · 勾销**（`STATUS-ember-b13-elite-ttk-close.md` · tip `996f035` · 65.5s / 535.3s · 5200/12 不动 · 剩血 62% 观察不砍血）
- ~~团本使徒 TTK（人数门 SKIP）~~ → **校准 PASS**（正式门仍 3～5）
- ~~B0.2 周本/深渊/使徒 ΔTTK~~ → **均 PASS**（正式团本人数门仍 3～5）
- ~~Raid/GuildBoss kill-any live~~ → **PASS**（静态+live · `STATUS-ember-killany-live-retest.md`）

### 多世界现状（确认）
- `plugins/Multiverse-Core.jar` + Portals；常驻 `ember_hub` / `ember_afk` / `ember_event` 等
- 副本：DungeonPlus `map/ember_*` 同 play 服实例化

**状态：仅文档**（2026-09-27 Asia/Shanghai）  
**作者岗：** 余烬-策划  
**用途：** 总控排期用的短清单——**优先玩法闭环与可维护性**，不铺新系统。  
**约束：** 不改本文件里的「目标数值」本身（TTK 带是验收窗，不是改掉落）；地图大件见 multiworld / P4 / P5 已批稿。  
**专岗代号：** 策划 · 插件 · 物品 · 怪物 · 测试 · 总控


## 进度快照（截至 2026-09-28，Asia/Shanghai）

- **B0.3：** 已完成；**B0.1：** 仍挂，无新证据不重开。
- **B0.2：** 周本 / 深渊 10·12 / **使徒校准** ΔTTK 均 **PASS**（使徒见 `STATUS-ember-raid-apostle-ttk-debt.md` · 门已还原 3～5）。
- **B0.4：** **勾销**（文档债 + 计算基线 + 二档施工/短样 · tip `99898cc`）；菜单 UX **亦勾销**（`0deda12` / `f0ce1fd`）。
- **B1.1～B1.4、B2.1～B2.4：** 已完成；相关交付记录：模板 `5f19c31`、坐标 `97d4029`、告示 `8a22023` / `193ab82`。
- **B2.5：** 等同 B0.4；**勾销**（同 `99898cc`）；菜单 UX 同上已勾销。
- **B2.9：** **PASS · 勾销**（设计 `a8b8ef2` · 批准 `ed4f54c` · 施工 `d0c9ef4` · 测 `796dc1b` · close `79f810f`）；挂机庭/天梯玩家可见文案已去内部代号。
- **B2.10：** **PASS · 勾销**（设计 `f2eb738` · 批准 `bf174fc` · 施工 `9311a72` · 测 `95c9fbc` · close `ddaeb46`）。
- **B2.11：** **PASS · 勾销**（设计 `4a2a671` · 批准 `ca68b64` · 施工 `8605d10` · 测 `a538d26` · close `5378b23` · 双路径 live+src；灾厄 OP #6 已由 B2.12 闭环）。
- **B2.12：** **PASS · 勾销**（设计 `1e109b3` · 批准 `708c62a` · 施工 `9be6ae4` · 测 `095be05` · close `483215f`；EmberCalamity OP 拒门去 `/ember`）。
- **B2.13：** **PASS · 勾销**（设计 `c86ebd6` · 批准 `aa4a7bb` · 施工 `89b7432` · 测 `f600687` · close `33218c8`；天赋菜单 `*_cap` 人话化）。
- **B2.14：** **PASS · 勾销**（设计 `9e129cc` · 批准 `1460fa0` · 施工 `37c4bd6` · 测 `2f6120b` · close `3b00eff`；天赋菜单 §8 nodeId/前置人话化）。
- **B2.15：** **PASS · 勾销**（设计 `26d7895` · 批准 `f4e2bcd` · 施工 `da17196` · 测 `790dc32` · close `a43010d`；天赋裸属性键人话化）。
- **B2.16：** **PASS · 勾销**（设计 `34e61dd` · 批准 `7c1c408` · 施工 `39ca8f6` · 测试 tips `e342990` / `7db1160` · close `22c2d40`；报告 `docs/STATUS-ember-talent-type-copy-test.md`；方案 B `cost`→`消耗`已由 **B2.17** 勾销）。
- **B2.17：** **PASS · 勾销**（设计 `3a3a226` · 批准 `0eb15c2` · 施工 `9631343` · 测 `dabf579` · close 本提交；报告 `docs/STATUS-ember-talent-cost-copy-test.md`）。

---

## 0. 怎么用

| 优先级 | 含义 |
|--------|------|
| **B0** | 已知债务 / 易翻车；额度一有就还 |
| **B1** | UX/可维护性；与地图施工可并行 |
| **B2** | 体验增强；不挡现网玩法 |

每项：**问题 → 建议动作 → 验收硬条 → 建议专岗**。  
地图 P0～P5、工坊四人 NPC 已有专稿，此处只挂索引，避免重复派工。

---

## 1. B0 · 已知债务（总控点名 + 阶段 4 挂账）

### B0.1 票扣次绑「显示名」过脆

| | |
|--|--|
| **问题** | DP `option.yml` 用 `<item:余烬日票 *1 *true>` 等 **去色显示名** 扣票（日/周/深渊/团/精英皆然）。NI 改名、颜色码、翻译不一致 → 进本条件假失败或误扣。Lore 里还印裸 ID（`ticket_ember_*`），玩家可见噪音。 |
| **建议** | 插件侧改为按 **NI id** 检测/扣除（或 CoreRpg 统一 `corerpg ticket consume <id>` 再进本）；物品岗 lore **去掉**玩家可见的 `ticket_ember_*` 行（管理用文档保留）。显示名可继续好看，但逻辑不依赖显示名。 |
| **验收硬条** | ① 改显示名颜色/文案后，五本扣票仍正确；② 无票拒绝文案仍说人话；③ 玩家物品 lore **不再**出现裸 `ticket_ember_` 字符串；④ 数值（日 3 / 周 1 / 深 1 / 团 1 / 精英 1）不变。 |
| **专岗** | **插件**（主）· **物品**（lore）· 测试冒烟进本 |

### B0.2 阶段 4「7.2.4」天赋二层 TTK — **已勾销**

| | |
|--|--|
| **问题** | ~~满二层 vs 满一层 ΔTTK 未实测~~ → 周本 / 深渊 10·12 / 使徒校准均已采数。 |
| **结案** | ΔTTK 均 **PASS**（\|Δ\|&lt;15%）：周本见同步 tip `94d3bd9`；深渊 `STATUS-ember-b02-abyss-ttk.md`；使徒 `STATUS-ember-raid-apostle-ttk-debt.md`（门已还原 3～5）。**未**借机改掉落/票。 |
| **专岗** | **测试**（已结） |

### B0.3 菜单「去指令」残留（玩家可见）

| | |
|--|--|
| **问题** | UX 要求玩家不靠聊天打指令；多份 TrMenu 仍在 **lore / tell** 教 `/dp start`、`/corerpg …`、`/mvtp`。已知脏点（抽查）：`ember_hub` 日票行、`ember_daily` tell、`ember_raid` lore/tell、`ember_calamity` 测试钮文案过亮、`ember_covenant`/`arena`/`shop`/`settings`/`enhance`/`socket`/`ladder` 等「命令：/corerpg …」「建议执行」。 |
| **建议** | 玩家可见文案改为「点击即可」；底层 `command:` **可保留**。测试/管理入口（如灾厄 DP）lore 必须标 **「仅管理/测试」** 且勿当主按钮。优先清：**进本类**（日/周/深渊/团/精英/灾厄）→ 养成类（誓约/强化/镶嵌）→ 其它。 |
| **验收硬条** | ① `plugins/TrMenu/menus/ember_*.yml` 中，玩家主路径 lore/tell **无** `/dp start`、`/mvtp`、`请执行 /corerpg`；② 进本一点仍成功；③ 管理测试句仅出现在明确测试图标。 |
| **专岗** | **策划**出替换文案表（可附本 STATUS）· **插件**改 YAML · 测试点开回归 |

### B0.4 挂机日顶 25% 无第二档（阶段 3 挂账）— **勾销（二档 PASS）**

| | |
|--|--|
| **问题** | `afk_caps` 超顶后固定 25% 递减，无第二档 → 超长挂机仍近似线性（约 1/4 产速）。 |
| **进度** | 文档债 A 已闭环；**2h 计算基线 PASS**（`STATUS-ember-afk-b04-2h-baseline.md` · ④ 期望 ~490～501 shard ≈3.3×日顶 · **非**墙钟实挂）。 |
| **建议** | 数值定稿+施工+短样已 **PASS 勾销**；菜单 UX 亦 **PASS 勾销**（`0deda12` / `f0ce1fd`）。 |
| **验收硬条** | 数值窗：短样外推 PASS（已结）。菜单 UX：见专稿验收；禁宣称封死通胀。 |
| **专岗** | 数值已结；菜单 UX 已结 |

---

## 2. B1 · 与已批内容咬合（可并行）

### B1.1 工坊四人 Ady（已批稿）

| | |
|--|--|
| **索引** | `docs/design-ember-hub-workshop-npcs.md`（纠偏：烬砧方案 A；余晶必出引导薄菜单） |
| **验收硬条** | 见该稿 §6；另：无四人可见不得再标「工坊场景完成」。 |
| **专岗** | **插件**（Ady + TrMenu）· 测试 · 排在 **P3 地图后**（总控已定） |

### B1.2 地图里程碑 P0～P5（已批 / 施工中）

| 项 | 文档 | 专岗 |
|----|------|------|
| P0 挂机去空岛 | `design-ember-afk-p0-surface.md` | 插件+地图 |
| P1 hub | STATUS-ember-hub-maps-p1 | 已最小验收 |
| P2 深渊竖井 | multiworld + abyss 相关 STATUS | 地图+插件 |
| P3 周本 + `ember_elite` | multiworld | 地图+插件 |
| P4 灰烬庭院 | `design-ember-daily-maps-p4.md`（已批，插件施工中） | 插件 |
| P5 灾厄+团本 | `design-ember-calamity-raid-maps-p5.md`（已批，P4 后派） | 插件 |

**硬条：** 一律「禁换皮算过」；玩法可测 ≠ 观感完成（复盘 §4）。

### B1.3 精英 Boss TTK 初值未 bot 精调 — **已勾销**（2026-09-28）

| | |
|--|--|
| **问题** | ~~初值待精调~~ → 现网已是 4.4f 锁数 5200/12；缺正式关账采数。 |
| **结案** | 关账采数 A **PASS**（`STATUS-ember-b13-elite-ttk-close.md`）：Boss TTK 65.5s · 全本 535.3s · 无掉崖；**保持 5200/12**，非砍血。 |
| **专岗** | **测试**（已结） |

### B1.4 主线第一卷 hint / join 文案扫尾

| | |
|--|--|
| **问题** | 第二卷 hint 已菜单化；第一卷/join 可能仍残留指令口吻（如旧 skill 提示）。 |
| **建议** | 扫 `quest.yml` + `config.yml` join_message：玩家句只留菜单/右键 NPC。 |
| **验收硬条** | 玩家可见主线 hint / 进服提示 **无**「请打 /corerpg|/dp|/hub」。 |
| **专岗** | **策划**标句 · **插件**改 YAML |

---

## 3. B2 · 可维护性 / 体验（额度允许再做）

| ID | 项 | 验收硬条（摘要） | 专岗 |
|----|----|------------------|------|
| B2.1 | 设计交稿模板固化（站岗表+动线+UX 否决条；模板见 [`TEMPLATE-ember-design-handoff.md`](TEMPLATE-ember-design-handoff.md)） | 新稿缺站岗/动线不得报可验收 | 策划 |
| B2.2 | DP map 本地坐标系统一文档（出生/刷点表） | 每本一张坐标表进 STATUS | 策划+插件 |
| B2.3 | 灾厄/团本菜单测试钮视觉降权 | 主按钮仅正式门；测试灰且折叠 | 插件 |
| B2.4 | 告示全面降级为指路（有 NPC/菜单后） | 工坊牌不写「打开/ember→」当唯一入口 | 插件+策划 |
| B2.5 | `afk_caps` 第二档（若总控开经济） | 数值 **勾销**（同 B0.4 · `99898cc`）；菜单 UX **勾销**（`0deda12`/`f0ce1fd`） | 已结 |
| B2.6 | S0 迁后菜单「日票」假文案对齐 | 战令/角色/寄售玩家句无「日票/日周票」；与商城体力药口径一致；零改数 | **PASS · 勾销** `71473da`/`8340ac3` |
| B2.7 | 主线 quest「日票/周票」台词薄扫 | live `quest.yml` 玩家句无「日票/周票」；不改步骤/items 键；与日回体力口径一致 | **PASS · 勾销** 设计 `b7ccbb8` · 批准 `a6c48bc` · 施工 `4eb9af8` · 测 `f2edc25` · close `e950efe` |
| B2.8 | DP 进本拒门去斜杠（B0.3 域外） | 周/团/深渊/盟 Boss 玩家 message 无 `/corerpg`；不改门控 text/人数；对齐日常/精英；灾厄 OP #6 后由 B2.12 闭环 | **PASS · 勾销** 设计 `83d641a` · 批准 `4c99c21` · 施工 `e7bc1eb` · 测 `12846c7` · close `944c017` |
| B2.9 | 挂机庭/天梯全息·菜单去内部代号 | HD 说明行 + `ember_ladder` lore/tell 无 `MM→NI`/`EmberAfk*`/`EmberAbyss`/`EmberWeekly`/字面 board id；不改 location/数值/PAPI 键 | **PASS · 勾销** 设计 `a8b8ef2` · 批准 `ed4f54c` · 施工 `d0c9ef4` · 测 `796dc1b` · close `79f810f` |
| B2.10 | DP 局内/超时「票」→体力文案 | 日/周/深/团/精 玩家 `$message`/timeout 无日票·周票·团本票·深渊票·票不退·票已扣；对齐开场「已消耗体力」；零改 cost/门控 | **PASS · 勾销** 设计 `f2eb738` · 批准 `bf174fc` · 施工 `9311a72` · 测 `95c9fbc` |
| B2.11 | 主线 quest「深渊票」两句薄扫 | live+src `quest.yml` 无「深渊票」「票还是一天一张」；对齐耗体力/体力日回；不改步骤/items/cost；灾厄 OP #6 已由 B2.12 闭环 | **PASS · 勾销** 设计 `4a2a671` · 批准 `ca68b64` · 施工 `8605d10` · 测 `a538d26` · close `5378b23` |
| B2.12 | 灾厄 OP 调试拒门去斜杠 | `EmberCalamity` OP gate message 无 `/ember`；改为枢纽菜单 → 灾厄；不改 `text=`/人数/loot | **PASS · 勾销** 设计 `1e109b3` · 批准 `708c62a` · 施工 `9be6ae4` · 测 `095be05` · close `483215f` |
| B2.13 | 天赋菜单 `*_cap` 文案人话化 | `ember_talent.yml` 玩家 lore/tell 无字面 `*_cap`；改为一层顶「燎原/烟幕/永护」；不改 talent 键/unlock command/cost | **PASS · 勾销**（设计 `c86ebd6` · 批准 `aa4a7bb` · 施工 `89b7432` · 测 `f600687` · close `33218c8`） |
| B2.14 | 天赋菜单 §8 `nodeId`/前置人话化 | `ember_talent.yml` 无 `§8nodeId:`；前置无英 id（→中文 display）；不改 talent 键/unlock/cost；裸属性键不动，不做方案 B | **PASS · 勾销**（设计 `9e129cc` · 批准 `1460fa0` · 施工 `37c4bd6` · 测 `2f6120b` · close `3b00eff`） |
| B2.15 | 天赋菜单裸属性键人话化 | `ember_talent.yml` 二层效果 lore 无字面 `phys_damage`/`crit_*`/…（→物攻/暴伤/…）；不改 talent.yml stats 键/数值/unlock；类型英词默认不动（方案 B 另附） | **PASS · 勾销**（设计 `26d7895` · 批准 `f4e2bcd` · 施工 `da17196` · 测 `790dc32` · close `a43010d`） |
| B2.16 | 天赋菜单类型英词 `passive`/`skill` 人话化 | `ember_talent.yml` 13 行类型 lore 无字面 `passive`/`skill`（→被动 / 去冗余）；不改 talent.yml / unlock / cost 数值；同句 `cost`→消耗不做（方案 B） | **PASS · 勾销**（设计 `34e61dd` · 批准 `7c1c408` · 施工 `39ca8f6` · 测试 tips `e342990` / `7db1160` · close `22c2d40`；报告 `docs/STATUS-ember-talent-type-copy-test.md`） |
| B2.17 | 天赋菜单同句英词 `cost`→消耗 | `ember_talent.yml` 13 行类型 lore 无字面 `cost`（→消耗）；数值 2/3/4 原样；不改 talent.yml / unlock / cost 数值；不附 NI/套装/精英壳 | **PASS · 勾销**（设计 `3a3a226` · 批准 `0eb15c2` · 施工 `9631343` · 测 `dabf579` · close 本提交；报告 `docs/STATUS-ember-talent-cost-copy-test.md`） |

---

## 3b. B-flex · 装备/操作灵活试点（用户拍板 · 优先于非文案内容窗）

| ID | 项 | 验收硬条（摘要） | 状态 |
|----|----|------------------|------|
| **B-flex-1** | 刃+护符之外 +1 **副手/饰品位**（NI 白名单 + 菜单展示 + 极简属性） | 静态 `rg` 试点 id / offhand 白名单 / Stat OffHand 钩子；TrMenu hub 或 set 目视「副手」说明；**禁** wall-clock / DPS 盲调；**禁**动 T0–T3 刃护符数值、体力日周门；**不做**四件甲/技能大改/锻炉产线重做 | **PASS · 勾销**（设计 `7dda194` · 批准 `eb3c843` · NI `52f817a` · 插件 `08b4cd4` · 测 `1453a7c` · close `d333b01`；报告 `docs/STATUS-ember-offhand-slot-pilot-test.md`；CoreRpg **1.15.24**） |
| **B-flex-2** | **可装配轻技 1 槽**（位移 **或** 保命择一；荐 A · 位移「余烬踏步」；TrMenu 点击；零体力） | 静态 `rg` 轻技 id / 1 槽字段；菜单装配+释放轻测；**禁** wall-clock / DPS；不叫挑刺；**禁**动誓约三主动数值、体力日周门、四件甲、锻炉大改、T0–T3 刃护符；**勿**位移+保命双上 | **已批 A · 待插件**（设计 `d1e88e4`；`docs/STATUS-ember-flex-skill-slot-pilot-approve.md`） |

**并行不捆：** **B2.17** 已 **PASS · 勾销**；文案薄窗暂缓；**勿捆 B2.x**；B-flex-1 已结（`d333b01`）；B-flex-2 **已批 A · 待插件**。

**本窗软挂（标「不捆」）：**
- ~~技能可装配~~ → 已升 **B-flex-2**（本表）
- 烬砧材料→部件产线（现网=升阶+强化/镶嵌/分解入口，非多部位锻炉）→ **仍 soft · 后续**

**明确未做/未清：** 四件甲 · 誓约主动大改 · 锻炉重做 · 位移+保命双上 · **勿宣称 B0.1 已清**

---

## 4. 建议排期（额度内）


```
可维护性：  **B0.1 仍挂** / B0.3 TrMenu / B1.4 / B2.3 / B2.4 已结；挂机菜单 UX 勾销；**B2.8～B2.17 已结**（B2.17 PASS · close 本提交）
测窗：      B0.2 ΔTTK · B1.3 精英关账 · kill-any live · 使徒校准 · 冷却 chat 均 PASS
地图窗：    P4 七线 + 节奏杠杆收口；P5 已有专稿
经济窗：    B0.4/B2.5 二档数值+菜单 UX 均 PASS 勾销
内容灵活窗：**B-flex-1** 副手 **PASS · 勾销**（close `d333b01`）；**B-flex-2** 可装配轻技 **已批 A · 待插件**（荐 A · 位移 · `design-ember-flex-skill-slot-pilot.md`；批准 `d1e88e4`；优先于非文案内容窗；与文案薄窗无关；**勿捆 B2.x**）
文案窗：**B2.17** 已 **PASS · 勾销**；**暂缓再挑文案薄窗**。
软挂不捆：  烬砧材料→部件产线（技能装配已升 B-flex-2）
软观察：    奖励页 NI id / 套装 `gear_ember_*` / 精英预览壳（证据不足勿硬开） / 断塔观察 / 霜锈前压 / AFK 通胀未封死 / B0.1；**勿宣称 B0.1 已清**
策划挑选排除：刚结 B2.6–B2.17 / B-flex-1；五入口奖励预览；非日常体力灰显；挂机 over_chance*/二档 UX；B1.3；墙钟 2h；霜锈前压；断塔无证据升 B；B0.1 unless new evidence；精英预览壳若证据不足则勿硬开厚壳；四件甲/誓约主动大改/锻炉重做；位移+保命双上（B-flex-2 明确择一）。
```

**不建议本额度新开：** 新养成线、新货币、新副本类型、Citizens、改 Paper/NMS。

---

## 5. 回总控摘要

- 文档：`docs/design-ember-content-backlog.md`  
- **B0 硬债：** B0.1 **仍挂**；B0.3～B0.4 / B2.5 数值+挂机菜单 UX 已结；进本冷却 chat **勾销**（1.15.21）；B0.1 无新证据不重开
- **B1/B2：** 工坊/地图/精英TTK/文案/测试钮/B2.6～B2.17 等已结；**B2.17 PASS · 勾销**（设计 `3a3a226` · 批准 `0eb15c2` · 施工 `9631343` · 测 `dabf579` · close 本提交）
- **内容灵活窗：** **B-flex-1** 副手 **PASS · 勾销**（close `d333b01`）；**B-flex-2** 可装配轻技 **已批 A · 待插件**（荐 A · 位移「余烬踏步」· `design-ember-flex-skill-slot-pilot.md`；批准 `d1e88e4`；1 槽；勿两套一起上；软挂烬砧材料→部件；与文案薄窗无关；**勿捆 B2.x**）
- **文案窗：** **B2.17** 已 **PASS · 勾销**；**暂缓再挑文案薄窗**；优先 B-flex-2（内容灵活）
- 软观察：奖励页 NI id / 套装 `gear_ember_*` / 精英预览壳 / 断塔观察 / 霜锈前压 / AFK 通胀未封死 / B0.1；**勿宣称 B0.1 已清**

