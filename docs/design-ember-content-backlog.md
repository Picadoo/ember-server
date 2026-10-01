# 余烬 · 额度内内容 / 可维护性 Backlog


## Progress snapshot — 2026-10-01 13:37 Asia/Shanghai (总控 · B2.111 PASS · 勾销 · 流水线暂停，等用户/GPT 策划意见；下一默认窗 B2.113)

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
- **暂停 · 下一默认窗 B2.113**（用户 13:35 要求先交接、请 GPT 协助策划；见 `docs/HANDOFF-ember-grokbot-2026-10-01.md`。B2.113 = TrMenu `ember_hub.yml` L215/L266/L280 写死体力 45/30/50 → `%corerpg_stamina_cost_weekly/abyss/raid%`；默认余项顺序：B2.113 → B2.118 ember_raid.yml L22/L109/L118 → B2.120 ember_abyss.yml L49/L54/L130/L139 → B2.121 ember_abyss.yml L132 → B2.115 Weekly timeout:6 → B2.116 Raid timeout:7 → B2.117 quest.yml L377 → B2.103 CoreRpgPlugin cmdAbyss → B2.104 EliteService status → B2.105 set.yml → B2.106 cash.yml → B2.107 src 模板(可不排；解挂时同步 src quest.yml:389) → B2.108 CoreRpgExpansion → B2.119 TicketEntryService 退还提示 → B2.109 mineflayer；只记不排：Daily:7/Abyss:8 timeout、level_gates 缺键、quest.yml L134/L73/L243、blocked_raid/abyss L41 灰显不区分 OP、ember_abyss L78 撤离句与 DP L23 重复）。~~B2.111~~ → TrMenu ember_abyss.yml L77「[深渊] 尝试下潜……（需余烬 Lv.25）」PASS · 勾销（设计 `227bec9` · 批准 `4e0c11a` · 插件 `4cd3be3` · 测 `7ef8d00` · close 本提交）。B2.109 追加：深渊菜单点击用例（断言新 tell、无「体力 30/等级不足不扣」，覆盖发起者等级不足/付费/OP 私聊、队员等级不足先扣后退）。NI 票物保留（ticket_convert 需要）。精英壳勿硬开；**勿宣称 B0.1 已清**/票已废。
- 灵活三窗挑刺 **无挡级**（`43bf843` / `STATUS-ember-flex-trilogy-picky.md`）；**勿**无证据重开 B0.1；**勿**四件甲/锻炉重做。

### 刚结
- ~~**B2.36 NI 疾风石 lore 裸 id**~~ → **PASS · 勾销**（设计 `eca16cd` · 批准 `dd582c8` · 施工 `3e3c63b` · 测 `a531ebf` · close `54ef5c4`；报告 `docs/STATUS-ember-gem-gale-ni-lore-copy-test.md`；live Items `&7gem_*` **本轨归零**；下一窗升 **B2.37** cosmetic；斜杠 / pet 仍 soft；**勿宣称 B0.1 已清**）
- ~~**B2.37 NI 灾厄外观碎片 lore 裸 id**~~ → **PASS · 勾销**（设计 `3717031` · 批准 `d786875` · 施工 `3c49b53` · 测 `7f21335` · close `da8088d`；报告 `docs/STATUS-ember-cosmetic-calamity-shard-ni-lore-copy-test.md`；live Items `&7cosmetic_*` **本轨归零**；下一窗升 **B2.38** pet_ashling；斜杠 / 另一 pet 仍 soft；**勿宣称 B0.1 已清**）
- ~~**B2.38 NI 余烬灰灵 lore 裸 id**~~ → **PASS · 勾销**（设计 `965ab2b` · 批准 `bbded5f` · 施工 `7dbf09c` · 测 `72b544e` · close `b59d150`；报告 `docs/STATUS-ember-pet-ashling-ni-lore-copy-test.md`；`&7pet_` 现仅 cinder=1；下一窗升 **B2.39** pet_cinder；斜杠仍 soft；**勿宣称 B0.1 已清**）
- ~~**B2.39 NI 余烬烬火 lore 裸 id**~~ → **PASS · 勾销**（设计 `88c53bd` · 批准 `8e2ef70` · 施工 `6e08d32` · 测 `a188a8b` · close `f0f6255`；报告 `docs/STATUS-ember-pet-cinder-ni-lore-copy-test.md`；live `&7pet_*` **本轨归零**；下一窗升 **B2.40** 誓约重置券斜杠；其它斜杠仍 soft；**勿宣称 B0.1 已清**）
- ~~**B2.40 NI 誓约重置券 lore 去斜杠**~~ → **PASS · 勾销**（设计 `d49dd7b` · 批准 `1a0f820` · 施工 `c7d55d1` · 测 `9affd15` · close `611f999`；报告 `docs/STATUS-ember-covenant-reset-slash-copy-test.md`；下一窗升 **B2.41** 天赋重置券斜杠；pet 斜杠仍 soft；**勿宣称 B0.1 已清**）
- ~~**B2.41 NI 天赋重置券 lore 去斜杠**~~ → **PASS · 勾销**（设计 `5e6d605` · 批准 `506ea1a` · 施工 `067002d` · 测 `960ac62` · close `af616a3`；报告 `docs/STATUS-ember-talent-reset-slash-copy-test.md`；下一窗升 **B2.42** 灰灵 summon 斜杠；cinder summon / feed 仍 soft；**勿宣称 B0.1 已清**）
- ~~**B2.42 NI 余烬灰灵 lore summon 去斜杠**~~ → **PASS · 勾销**（设计 `696c80c` · 批准 `48dc3d1` · 施工 `1dbcb88` · 测 `d855ce3` · close `ef6ff87`；报告 `docs/STATUS-ember-pet-ashling-summon-slash-copy-test.md`；下一窗升 **B2.43** 烬火 summon 斜杠；soul_dust feed 仍 soft；**勿宣称 B0.1 已清**）
- ~~**B2.43 NI 余烬烬火 lore summon 去斜杠**~~ → **PASS · 勾销**（设计 `e8d3704` · 批准 `2d2fcf8` · 施工 `94ad273` · 测 `3c30405` · close `7a00524`；报告 `docs/STATUS-ember-pet-cinder-summon-slash-copy-test.md`；下一窗升 **B2.44** 魂尘 feed 斜杠；pet summon 本轨已清 · 剩 feed；disassemble 管理注释仍 soft；**勿宣称 B0.1 已清**）
- ~~**B2.44 NI 余烬魂尘 lore feed 去斜杠**~~ → **PASS · 勾销**（设计 `8ef2acb` · 批准 `a82321d` · 施工 `513bf8b` · 测 `4f628b5` · close 本提交；报告 `docs/STATUS-ember-soul-dust-feed-slash-copy-test.md`；live Items 玩家可见 `/corerpg` lore **本轨归零**；下一窗升 **B-flex-3** 副手薄获取；disassemble 管理注释仍 soft；**勿宣称 B0.1 已清**）
- ~~**B-flex-3 副手守腕/生坠薄获取（灰粮 stub）**~~ → **PASS · 勾销**（设计 `8a605f3` · 批准 `440e632` · 施工 `58baffe` · 测 `4a3b373` · close 本提交；报告 `docs/STATUS-ember-flex-offhand-thin-acquire-test.md`；下一窗升 **B2.45** 灰箍抢口文案；踏步热键仍 soft；**勿宣称 B0.1 已清**）
- ~~**B2.45 烬砧灰箍抢口文案**~~ → **PASS · 勾销**（设计 `ab67790` · 批准 `a040ce2` · 插件 `3066746` · 测 `d6768d9`/`c69bd4d` · close 本提交；报告 `docs/STATUS-ember-ash-brace-contention-copy-test.md`；下一窗升 **B-flex-4**；踏步热键升窗；**勿宣称 B0.1 已清**）
- ~~**B-flex-4 踏步少开菜单热键**~~ → **PASS · 勾销**（设计 `a9311ac` · 批准 `7b4a8b6` · 插件 `005a03b` · 测 `a1d5f7c` · close `6b0ce3d`；报告 `docs/STATUS-ember-flex-hotkey-sneak-drop-test.md`；CoreRpg **1.15.28**；下一窗升 **B2.46**；**勿宣称 B0.1 已清**）
- ~~**B2.46 拆解菜单管理注释人话化**~~ → **PASS · 勾销**（设计 `50a272d` · 批准 `b2b2f7a` · 插件 `530c2f8` · 测 `89b1117` · close 本提交；报告 `docs/STATUS-ember-disassemble-rule-copy-test.md`；下一窗升 **B2.47**；**勿宣称 B0.1 已清**）
- ~~**B2.35 NI 汲取石 lore 裸 id**~~ → **PASS · 勾销**（设计 `c9cdb28` · 批准 `bf3bf4b` · 施工 `1ef8a41` · 测 `9b2333f` · close `bab3b39`；报告 `docs/STATUS-ember-gem-drain-ni-lore-copy-test.md`；`&7gem_*` 剩 1；下一窗升 **B2.36**；斜杠 / cosmetic / pet 仍 soft；**勿宣称 B0.1 已清**）
- ~~**B2.34 NI 稳固石 lore 裸 id**~~ → **PASS · 勾销**（设计 `baa2705` · 批准 `19a8982` · 施工 `c5647d3` · 测 `7bc605a` · close `5b47896`；报告 `docs/STATUS-ember-gem-steady-ni-lore-copy-test.md`；`&7gem_*` 剩 2；下一窗升 **B2.35**；斜杠 / cosmetic / pet 仍 soft；**勿宣称 B0.1 已清**）
- ~~**B2.33 NI 锋利石 lore 裸 id**~~ → **PASS · 勾销**（设计 `48b5218` · 批准 `6a43ab1` · 施工 `fbef72e` · 测 `90797d7` · close `973e941`；报告 `docs/STATUS-ember-gem-sharp-ni-lore-copy-test.md`；`&7gem_*` 剩 3；下一窗升 **B2.34**；斜杠 / cosmetic / pet 仍 soft；**勿宣称 B0.1 已清**）
- ~~**B2.32 NI 余烬稳固符 lore 裸 id**~~ → **PASS · 勾销**（设计 `cf7eaaf` · 批准 `c4194e4` · 施工 `1cdf73b` · 测 `0c70273` · close `42054ad`；报告 `docs/STATUS-ember-mat-stable-charm-ni-lore-copy-test.md`；live `&7mat_*` **本轨归零**；下一薄窗升 **B2.33** gem；斜杠 / gem / cosmetic / pet 仍 soft；**勿宣称 B0.1 已清**）
- ~~**B2.31 NI 余烬保护券 lore 裸 id**~~ → **PASS · 勾销**（设计 `1bdacd8` · 批准 `4359e89` · 施工 `c0e8fcc` · 测 `b2d9773` · close `c8ab22c`；报告 `docs/STATUS-ember-mat-protect-scroll-ni-lore-copy-test.md`；其余 `&7mat_*` 已升 **B2.32**；斜杠仍 soft；**勿宣称 B0.1 已清**）
- ~~**B2.30 NI 灾厄余烬 lore 裸 id**~~ → **PASS · 勾销**（设计 `3e7e1ba` · 批准 `a9f95e5` · 施工 `b9b1786` · 测 `2f2ba60` · close `72d37b3`；报告 `docs/STATUS-ember-mat-calamity-ember-ni-lore-copy-test.md`；其余 `&7mat_*` 已升 **B2.31**；斜杠仍 soft；**勿宣称 B0.1 已清**）
- ~~**B2.29 NI 余烬魂尘 lore 裸 id**~~ → **PASS · 勾销**（设计 `bc58aaf` · 批准 `c87423c` · 施工 `9875d1b` · 测 `0420ef4` · close `91adf76`；报告 `docs/STATUS-ember-mat-soul-dust-ni-lore-copy-test.md`；其余 `&7mat_*` 已升 **B2.30**；斜杠仍 soft；**勿宣称 B0.1 已清**）
- ~~**B2.28 NI 天赋重置券 lore 裸 id**~~ → **PASS · 勾销**（设计 `2c50336` · 批准 `8ff1b4d` · 施工 `c74dfdf` · 测 `4825cae` · close `edc0d9b`；报告 `docs/STATUS-ember-mat-talent-reset-ni-lore-copy-test.md`；其余 `&7mat_*` 已升 **B2.29**；斜杠仍 soft；**勿宣称 B0.1 已清**）
- ~~**B2.27 NI 誓约重置券 lore 裸 id**~~ → **PASS · 勾销**（设计 `8bb16c9` · 批准 `1d659d0` · 施工 `d0830a7` · 测 `1d71c6c` · close `2aaffe3`；报告 `docs/STATUS-ember-mat-covenant-reset-ni-lore-copy-test.md`；其余 `&7mat_*` 已升 **B2.28**；斜杠仍 soft；**勿宣称 B0.1 已清**）
- ~~**B2.26 NI 余烬核心碎片 lore 裸 id**~~ → **PASS · 勾销**（设计 `0e76453` · 批准 `28a5048` · 施工 `ba460a7` · 测 `75d0831` · close `f796f3a`；报告 `docs/STATUS-ember-mat-core-fragment-ni-lore-copy-test.md`；其余 `&7mat_*` 已升 **B2.27**；斜杠仍 soft；**勿宣称 B0.1 已清**）
- ~~**B2.25 NI 余烬骨尘 lore 裸 id**~~ → **PASS · 勾销**（设计 `355603e` · 批准 `1b52ef9` · 施工 `e6df747` · 测 `220cb2a` · close `35abc98`；报告 `docs/STATUS-ember-mat-bone-dust-ni-lore-copy-test.md`；其余 `&7mat_*` 已升 **B2.26**；斜杠仍 soft；**勿宣称 B0.1 已清**）
- ~~**B2.24 NI 余烬碎片 lore 裸 id**~~ → **PASS · 勾销**（设计 `9931476` · 批准 `60f43b7` · 施工 `e004458` · 测 `f004152` · close `95783b3`；报告 `docs/STATUS-ember-mat-shard-ni-lore-copy-test.md`；其余 `&7mat_*` 已升 **B2.25**；斜杠仍 soft；**勿宣称 B0.1 已清**）
- ~~**B2.23 NI 重铸石 lore 去斜杠**~~ → **PASS · 勾销**（设计 `6485a89` · 批准 `75ff5e7` · 施工 `47baea0` · 测 `c509e57` · close `7da4dbf`；报告 `docs/STATUS-ember-reforge-ni-slash-copy-test.md`；其它 `&7mat_*` 已升 **B2.24**；斜杠仍 soft；**勿宣称 B0.1 已清**）
- ~~**B2.22 NI 重铸石 lore 裸 id**~~ → **PASS · 勾销**（设计 `115ebab` · 批准 `7724338` · 施工 `b24510c` · 测 `eff6037` · close `48647e0`；报告 `docs/STATUS-ember-reforge-ni-lore-copy-test.md`；斜杠已升 **B2.23**；其它 `&7mat_*` 仍 soft；**勿宣称 B0.1 已清**）
- ~~**B2.21 CoreRpg 重铸缺料 chat**~~ → **PASS · 勾销**（设计 `e81c904` · 批准 `92d6ee3` · 施工 `16b5334` · 测 `5b255e9` · close `79f5d7e`；报告 `docs/STATUS-ember-reforge-need-copy-test.md`；CoreRpg **1.15.27**；NI lore 已升 **B2.22**；**勿宣称 B0.1 已清**）
- ~~**B2.20 拆解菜单重铸石灰字**~~ → **PASS · 勾销**（设计 `c6d8b8b` · 批准 `e7574a3` · 施工 `3ab2b19` · 测 `492f996` · close 本提交；报告 `docs/STATUS-ember-disassemble-reforge-copy-test.md`；CoreRpg 缺料 tell / NI lore 仍 soft；**勿宣称 B0.1 已清**）
- ~~**B2.19 套装菜单 gear/戒人话**~~ → **PASS · 勾销**（设计 `0687fe5` · 批准 `157543b` · 施工 `24eb1b5` · 测 `1706a98` · close `b3a00b8`；报告 `docs/STATUS-ember-set-gear-id-copy-test.md`；拆解旁附已升 **B2.20**；**勿宣称 B0.1 已清**）
- ~~**B2.18 奖励预览页 NI 灰字对齐**~~ → **PASS · 勾销**（设计 `5c2f7f2` · 批准 `cef1017` · 施工 `dc8ebb8` · 测 `6783618` · close 本提交；报告 `docs/STATUS-ember-reward-ni-id-align-test.md`；五入口 34 行 `§8NI` 已删；套装 `gear_ember_*` 仍旁附；**勿宣称 B0.1 已清**）
- ~~**B-anvil-1 烬砧材料→部件（余烬灰箍）**~~ → **PASS · 勾销**（设计 `ab33c21` · 批准 `f10779b` · NI `57a6540` · 插件 `72281d9` · 测 `0ab09ef` · close 本提交；报告 `docs/STATUS-ember-anvil-mat-to-part-pilot-test.md`；CoreRpg **1.15.26**；轻测 PASS；`forge.yml` 升阶 ZERO；四件甲/骨饰 B/锻炉重做未做；**勿宣称 B0.1 已清**）
- ~~**B-flex-2 可装配轻技 1 槽试点**~~ → **PASS · 勾销**（设计 `d1e88e4` · 批准 `7d7bf5a` · 插件 `fc89225` · 补记 `3841f4f` · 测 `1c36873` · close 本提交；报告 `docs/STATUS-ember-flex-skill-slot-pilot-test.md`；CoreRpg **1.15.25**；轻测 PASS；烬砧材料→部件仍 soft；四件甲/誓约主动大改/锻炉重做未做；**勿宣称 B0.1 已清**）
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
- ~~**B-flex-2 可装配轻技 1 槽试点**~~ → **PASS · 勾销**（设计 `d1e88e4` · 批准 `7d7bf5a` · 插件 `fc89225` · 补记 `3841f4f` · 测 `1c36873` · close 本提交；报告 `docs/STATUS-ember-flex-skill-slot-pilot-test.md`；CoreRpg **1.15.25**；烬砧材料→部件仍 soft；**勿宣称 B0.1 已清**）
- ~~**B2.17 天赋同句 `cost`→消耗**~~ → **PASS · 勾销**（设计 `3a3a226` · 批准 `0eb15c2` · 施工 `9631343` · 测 `dabf579` · close 本提交；报告 `docs/STATUS-ember-talent-cost-copy-test.md`；与 B-flex 不捆；文案薄窗暂缓）
- ~~**B2.18** 奖励页 NI 灰字~~ → **PASS · 勾销**（设计 `5c2f7f2` · 批准 `cef1017` · 施工 `dc8ebb8` · 测 `6783618` · close `91061b3`；套装旁附已升 **B2.19**）
- ~~**B2.19** 套装菜单 gear/戒人话~~ → **PASS · 勾销**（设计 `0687fe5` · 批准 `157543b` · 施工 `24eb1b5` · 测 `1706a98` · close `b3a00b8`；拆解旁附已升 **B2.20**）
- ~~**B2.20** 拆解菜单重铸石灰字~~ → **PASS · 勾销**（设计 `c6d8b8b` · 批准 `e7574a3` · 施工 `3ab2b19` · 测 `492f996` · close `5bbaab0`）
- ~~**B2.21** CoreRpg 重铸缺料 chat~~ → **PASS · 勾销**（设计 `e81c904` · 批准 `92d6ee3` · 施工 `16b5334` · 测 `5b255e9` · close `79f5d7e`；CoreRpg **1.15.27**；NI lore 已升 **B2.22**）
- ~~**B2.22** NI 重铸石 lore 裸 id~~ → **PASS · 勾销**（设计 `115ebab` · 批准 `7724338` · 施工 `b24510c` · 测 `eff6037` · close `48647e0`；斜杠已升 **B2.23**）
- ~~**B2.23** NI 重铸石 lore 去斜杠~~ → **PASS · 勾销**（设计 `6485a89` · 批准 `75ff5e7` · 施工 `47baea0` · 测 `c509e57` · close `7da4dbf`；其它 `&7mat_*` 已升 **B2.24**）
- ~~**B2.24** NI 余烬碎片 lore 裸 id~~ → **PASS · 勾销**（设计 `9931476` · 批准 `60f43b7` · 施工 `e004458` · 测 `f004152` · close `95783b3`；报告 `docs/STATUS-ember-mat-shard-ni-lore-copy-test.md`；其余 `&7mat_*` 已升 **B2.25**）
- ~~**B2.25** NI 余烬骨尘 lore 裸 id~~ → **PASS · 勾销**（设计 `355603e` · 批准 `1b52ef9` · 施工 `e6df747` · 测 `220cb2a` · close `35abc98`；报告 `docs/STATUS-ember-mat-bone-dust-ni-lore-copy-test.md`；其余 `&7mat_*` 已升 **B2.26**）
- ~~**B2.26** NI 余烬核心碎片 lore 裸 id~~ → **PASS · 勾销**（设计 `0e76453` · 批准 `28a5048` · 施工 `ba460a7` · 测 `75d0831` · close `f796f3a`；报告 `docs/STATUS-ember-mat-core-fragment-ni-lore-copy-test.md`；其余 `&7mat_*` 已升 **B2.27**）
- ~~**B2.27** NI 誓约重置券 lore 裸 id~~ → **PASS · 勾销**（设计 `8bb16c9` · 批准 `1d659d0` · 施工 `d0830a7` · 测 `1d71c6c` · close `2aaffe3`；报告 `docs/STATUS-ember-mat-covenant-reset-ni-lore-copy-test.md`；其余 `&7mat_*` 已升 **B2.28**）
- ~~**B2.28** NI 天赋重置券 lore 裸 id~~ → **PASS · 勾销**（设计 `2c50336` · 批准 `8ff1b4d` · 施工 `c74dfdf` · 测 `4825cae` · close `edc0d9b`；报告 `docs/STATUS-ember-mat-talent-reset-ni-lore-copy-test.md`；其余 `&7mat_*` 已升 **B2.29**）
- ~~**B2.29** NI 余烬魂尘 lore 裸 id~~ → **PASS · 勾销**（设计 `bc58aaf` · 批准 `c87423c` · 施工 `9875d1b` · 测 `0420ef4` · close `91adf76`；报告 `docs/STATUS-ember-mat-soul-dust-ni-lore-copy-test.md`；其余 `&7mat_*` 已升 **B2.30**）
- ~~**B2.30** NI 灾厄余烬 lore 裸 id~~ → **PASS · 勾销**（设计 `3e7e1ba` · 批准 `a9f95e5` · 施工 `b9b1786` · 测 `2f2ba60` · close `72d37b3`；报告 `docs/STATUS-ember-mat-calamity-ember-ni-lore-copy-test.md`；其余 `&7mat_*` 已升 **B2.31**）
- ~~**B2.31** NI 保护券 lore 裸 id~~ → **PASS · 勾销**（设计 `1bdacd8` · 批准 `4359e89` · 施工 `c0e8fcc` · 测 `b2d9773` · close `c8ab22c`；报告 `docs/STATUS-ember-mat-protect-scroll-ni-lore-copy-test.md`；其余 `&7mat_*` 已升 **B2.32**）
- ~~**B2.32** NI 稳固符 lore 裸 id~~ → **PASS · 勾销**（设计 `cf7eaaf` · 批准 `c4194e4` · 施工 `1cdf73b` · 测 `0c70273` · close `42054ad`；报告 `docs/STATUS-ember-mat-stable-charm-ni-lore-copy-test.md`；live `&7mat_*` 本轨归零）
- ~~**B2.33** NI 锋利石 lore 裸 id~~ → **PASS · 勾销**（设计 `48b5218` · 批准 `6a43ab1` · 施工 `fbef72e` · 测 `90797d7` · close `973e941`；报告 `docs/STATUS-ember-gem-sharp-ni-lore-copy-test.md`；`&7gem_*` 剩 3）
- ~~**B2.34** NI 稳固石 lore 裸 id~~ → **PASS · 勾销**（设计 `baa2705` · 批准 `19a8982` · 施工 `c5647d3` · 测 `7bc605a` · close `5b47896`；报告 `docs/STATUS-ember-gem-steady-ni-lore-copy-test.md`；`&7gem_*` 剩 2）
- ~~**B2.35** NI 汲取石 lore 裸 id~~ → **PASS · 勾销**（设计 `c9cdb28` · 批准 `bf3bf4b` · 施工 `1ef8a41` · 测 `9b2333f` · close `bab3b39`；报告 `docs/STATUS-ember-gem-drain-ni-lore-copy-test.md`；`&7gem_*` 剩 1）
- ~~**B2.36** NI 疾风石 lore 裸 id~~ → **PASS · 勾销**（设计 `eca16cd` · 批准 `dd582c8` · 施工 `3e3c63b` · 测 `a531ebf` · close `54ef5c4`；报告 `docs/STATUS-ember-gem-gale-ni-lore-copy-test.md`；live `&7gem_*` **本轨归零**）
- ~~**B2.37**~~ NI 灾厄外观碎片 lore 裸 id → **PASS · 勾销**（见上）
- ~~**B2.38**~~ NI 余烬使魔·灰灵 lore 裸 id → **PASS · 勾销**（见上）
- ~~**B2.39**~~ NI 余烬使魔·烬火 lore 裸 id → **PASS · 勾销**（见上）
- ~~**B2.40**~~ NI 誓约重置券 lore 去斜杠 → **PASS · 勾销**（见上）
- ~~**B2.41**~~ NI 天赋重置券 lore 去斜杠 → **PASS · 勾销**（见上）
- ~~**B2.42**~~ NI 余烬灰灵 lore summon 去斜杠 → **PASS · 勾销**（见上）
- ~~**B2.43**~~ NI 余烬烬火 lore summon 去斜杠 → **PASS · 勾销**（见上）
- ~~**B2.44**~~ NI 余烬魂尘 lore feed 去斜杠 → **PASS · 勾销**（见上）
- ~~**B-flex-3**~~ 副手守腕/生坠薄获取 → **PASS · 勾销**（见上）
- ~~**B2.45** 烬砧灰箍抢口文案~~ → **PASS · 勾销**（设计 `ab67790` · 批准 `a040ce2` · 插件 `3066746` · 测 `d6768d9`/`c69bd4d` · close 本提交；报告 `docs/STATUS-ember-ash-brace-contention-copy-test.md`；下一窗升 **B-flex-4** 踏步热键；disassemble 管理注释仍 soft；**勿宣称 B0.1 已清**）
- ~~**B-flex-4** 踏步少开菜单热键~~ → **PASS · 勾销**（设计 `a9311ac` · 批准 `7b4a8b6` · 插件 `005a03b` · 测 `a1d5f7c` · close `6b0ce3d`；报告 `docs/STATUS-ember-flex-hotkey-sneak-drop-test.md`；CoreRpg **1.15.28**；下一窗升 **B2.46** 拆解管理注释；**勿宣称 B0.1 已清**）
- ~~**B2.46** 拆解菜单管理注释人话化~~ → **PASS · 勾销**（设计 `50a272d` · 批准 `b2b2f7a` · 插件 `530c2f8` · 测 `89b1117` · close 本提交；报告 `docs/STATUS-ember-disassemble-rule-copy-test.md`；下一窗升 **B2.47** 拆解 Open tell；**勿宣称 B0.1 已清**）
- ~~**B2.47 拆解菜单 Open tell 管理口吻**~~ → **PASS · 勾销**（设计 `e9370a8` · 批准 `78c815e` · 插件 `8c09391` · 测 `6952749` · close 本提交；报告 `docs/STATUS-ember-disassemble-open-tell-copy-test.md`；下一窗升 **B2.48**；**勿宣称 B0.1 已清**）
- ~~**B2.48 公会菜单 Open tell 管理口吻**~~ → **PASS · 勾销**（设计 `6b0e743` · 批准 `811dc78` · 插件 `cd4f6c0` · 测 `8d3f3cf` · close 本提交；报告 `docs/STATUS-ember-guild-open-tell-copy-test.md`；下一窗升 **B2.49**；**勿宣称 B0.1 已清**）
- ~~**B2.49 friends/mail 文件头管理口吻**~~ → **PASS · 勾销**（设计 `d9c5f02` · 批准 `c79e480` · 插件 `7af95b6` · 测 `4f52d81` · close 本提交；报告 `docs/STATUS-ember-friends-mail-header-copy-test.md`；下一窗升 **B2.50**；**勿宣称 B0.1 已清**）
- ~~**B2.50 arena/settings/set 文件头管理口吻**~~ → **PASS · 勾销**（设计 `da20e44` · 批准 `d808398` · 插件 `4e84455` · 测 `b84e948` · close 本提交；报告 `docs/STATUS-ember-shell-menu-header-copy-test.md`；menus「逻辑待」本轨归零；下一窗升 **B2.51**；**勿宣称 B0.1 已清**）
- ~~**B2.51 enhance/socket 文件头教斜杠**~~ → **PASS · 勾销**（设计 `aa4c825` · 批准 `25d5871` · 插件 `84dd76d` · 测 `929206e` · close 本提交；报告 `docs/STATUS-ember-enhance-socket-header-copy-test.md`；下一窗升 **B2.52**；**勿宣称 B0.1 已清**）
- ~~**B2.52 shop 文件头教斜杠**~~ → **PASS · 勾销**（设计 `63db349` · 批准 `037b6ad` · 插件 `838d492` · 测 `38eeecc` · close 本提交；报告 `docs/STATUS-ember-shop-header-copy-test.md`；下一窗升 **B2.53**；**勿宣称 B0.1 已清**）
- ~~**B2.53 hub AFK 注释教斜杠**~~ → **PASS · 勾销**（设计 `0273967` · 批准 `d4c8121` · 插件 `7a9886a` · 测 `8c76d55` · close 本提交；报告 `docs/STATUS-ember-hub-afk-comment-copy-test.md`；下一窗升 **B2.54**；**勿宣称 B0.1 已清**）
- ~~**B2.54 NI disassemble 文件头教斜杠**~~ → **PASS · 勾销**（设计 `0da490f` · 批准 `8e57a22` · 物品 `da31d54` · 测 `c642e4f` · 报告 `docs/STATUS-ember-ni-disassemble-header-copy-test.md`；close `ee02ed6`；下一窗升 **B2.55**；**勿宣称 B0.1 已清**）
- ~~**B2.55 NI pets 文件头管理口吻**~~ → **PASS · 勾销**（设计 `4835aab` · 批准 `96e493e` · 物品 `07c918e` · 测 `7765927` · close 本提交；报告 `docs/STATUS-ember-ni-pets-header-copy-test.md`；下一窗升 **B2.56**；**勿宣称 B0.1 已清**）
- ~~**B2.56 bestiary 阶段奖 tell 管理口吻**~~ → **PASS · 勾销**（设计 `33bd3ed` · 批准 `6be040f` · 施工 `ff654c5` · 测 `707f6f7` · close 本提交；报告 `docs/STATUS-ember-bestiary-stage-tell-copy-test.md`；下一窗升 **B2.57**；**勿宣称 B0.1 已清**）
- ~~**B2.57 NI gear-t1 文件头管理口吻**~~ → **PASS · 勾销**（设计 `58b9a27` · 批准 `325b2e0` · 物品 `8c5bb51` · 测 `01a41b2` · close `c1e7202`；报告 `docs/STATUS-ember-ni-gear-t1-header-copy-test.md`；下一窗升 **B2.58**；**勿宣称 B0.1 已清**）
- ~~**B2.58 NI gear-t1 L35 分隔注释 stub**~~ → **PASS · 勾销**（设计 `7ddcbbe` · 批准 `31f4653` · 物品 `813af94` · 测 `162cae0` · close 本提交；报告 `docs/STATUS-ember-ni-gear-t1-section-stub-copy-test.md`；下一窗升 **B2.59**；**勿宣称 B0.1 已清**）
- ~~**B2.59 TrMenu README 占位段管理口吻**~~ → **PASS · 勾销**（设计 `57558b3` · 批准 `ffde8fc` · 插件 `5fea72e` · 测 `2ae758f` · close 本提交；报告 `docs/STATUS-ember-trmenu-readme-placeholder-copy-test.md`；下一窗升 **B2.60**；**勿宣称 B0.1 已清**）
- ~~**B2.60 CoreRpg cash/covenant/talent 文件头维护备忘**~~ → **PASS · 勾销**（设计 `262bfa8` · 批准 `45f2dcf` · 插件 `96b931a` · 测 `5abd67d` · close 本提交；报告 `docs/STATUS-ember-corerpg-stub-header-copy-test.md`；下一窗升 **B2.61**；**勿宣称 B0.1 已清**）
- ~~**B2.61 CoreRpg `_schema-example.yml` 分区注释「插件岗写入」**~~ → **PASS · 勾销**（设计 `72ad8a7` · 批准 `6c5fd45` · 插件 `8421383` · 测 `0098df7` · close 本提交；报告 `docs/STATUS-ember-corerpg-schema-header-copy-test.md`；下一窗升 **B2.62**；**勿宣称 B0.1 已清**）
- ~~**B2.62 CoreRpg `cash.yml` 勋阶 stub 注释**~~ → **PASS · 勾销**（设计 `2cf9328` · 批准 `bd87894` · 插件 `a65c6ee` · 测 `8a0aec7` · close 本提交；报告 `docs/STATUS-ember-corerpg-vip-stub-copy-test.md`；下一窗升 **B2.63**；**勿宣称 B0.1 已清**）
- ~~**B2.63 DP `EmberCalamity/monster.yml` 灾厄 Boss stub 注释**~~ → **PASS · 勾销**（设计 `2c3f524` · 批准 `07edb1c` · 插件 `eba7d59` · 测 `8088429` · close 本提交；报告 `docs/STATUS-ember-dp-calamity-stub-copy-test.md`；下一窗升 **B2.64**；**勿宣称 B0.1 已清**）
- ~~**B2.64 DP `README-ember-dungeons.md` 表内 stub 注释**~~ → **PASS · 勾销**（设计 `8a6ba77` · 批准 `2f1a25f` · 插件 `b589616` · 测 `2f517c4` · close 本提交；报告 `docs/STATUS-ember-dp-readme-stub-copy-test.md`；下一窗升 **B2.65**；**勿宣称 B0.1 已清**）
- ~~**B2.65 DP `README-ember-dungeons.md` L4 进本口径**~~ → **PASS · 勾销**（设计 `2c9b58c` · 批准 `48003da` · 插件 `a6d0851` · 测 `1c099c5` · close 本提交；报告 `docs/STATUS-ember-dp-readme-enter-copy-test.md`；下一窗升 **B2.66**；**勿宣称 B0.1 已清**）
- ~~**B2.66 DP `README-ember-dungeons.md` 次数段发放备忘**~~ → **PASS · 勾销**（设计 `9499ef8` · 批准 `faa3748` · 插件 `5fe43ee` · 测 `a1e776f` · close 本提交；报告 `docs/STATUS-ember-dp-readme-ticket-issue-copy-test.md`；下一窗升 **B2.67**；**勿宣称 B0.1 已清**）
- ~~**B2.67 DP `README-ember-dungeons.md` 票表规则列**~~ → **PASS · 勾销**（设计 `f4d646e` · 批准 `7195926` · 插件 `32ce94f` · 测 `1f2770f` · close 本提交；报告 `docs/STATUS-ember-dp-readme-ticket-table-copy-test.md`；下一窗升 **B2.68**；**勿宣称 B0.1 已清**）
- ~~**B2.68 DP `README-ember-dungeons.md` DEBT 段维护备忘**~~ → **PASS · 勾销**（设计 `e371b80` · 批准 `f73627e` · 插件 `4963bfd` · 测 `e56ee7d` · close 本提交；报告 `docs/STATUS-ember-dp-readme-ticket-debt-copy-test.md`；下一窗升 **B2.69**；**勿宣称 B0.1 已清**）
- ~~**B2.69 DP `README-ember-dungeons.md` 次数段导语「入场卷」**~~ → **PASS · 勾销**（设计 `e805e35` · 批准 `9c833a7` · 插件 `c9e72b5` · 测 `a8dfda7` · close 本提交；报告 `docs/STATUS-ember-dp-readme-ticket-intro-copy-test.md`；下一窗升 **B2.70**；**勿宣称 B0.1 已清**）
- ~~**B2.70 DP `README-ember-dungeons.md` `## 已知占位` 段标题**~~ → **PASS · 勾销**（设计 `9d23f6e` · 批准 `54669ac` · 插件 `91c03c8` · 测 `71eb7cd` · close 本提交；报告 `docs/STATUS-ember-dp-readme-known-placeholder-copy-test.md`；下一窗升 **B2.71**；**勿宣称 B0.1 已清**）
- ~~**B2.71 DP `README-ember-dungeons.md` `## 次数（入场券）` 段标题**~~ → **PASS · 勾销**（设计 `aadfe77` · 批准 `1227e0c` · 插件 `a888348` · 测 `62abf4b` · close 本提交；报告 `docs/STATUS-ember-dp-readme-ticket-section-heading-copy-test.md`；下一窗升 **B2.72**；**勿宣称 B0.1 已清**）
- ~~**B2.72 DP `README-ember-dungeons.md` L11「入场券条件」**~~ → **PASS · 勾销**（设计 `a5d1ce1` · 批准 `9a7bd68` · 插件 `db12737` · 测 `f092865` · close 本提交；报告 `docs/STATUS-ember-dp-readme-table-entry-copy-test.md`；下一窗升 **B2.73**；**勿宣称 B0.1 已清**）
- ~~**B2.73 CoreRpg `mail.yml` `season_pass_stub` title/body「占位」**~~ → **PASS · 勾销**（设计 `53b92d1` · 批准 `87e40af` · 插件 `7290f4d` · 测 `e166783` · close 本提交；报告 `docs/STATUS-ember-mail-season-pass-stub-copy-test.md`；下一窗升 **B2.74**；**勿宣称 B0.1 已清**）
- ~~**B2.74 CoreRpg `mail.yml` `vip_daily_gift` body「勋阶正式累进前」**~~ → **PASS · 勾销**（设计 `d0c7f27` · 批准 `607ced3` · 插件 `e7ee555` · 测 `7b58d70` · close 本提交；报告 `docs/STATUS-ember-mail-vip-daily-gift-copy-test.md`；下一窗升 **B2.75**；**勿宣称 B0.1 已清**）
- ~~**B2.75 CoreRpg `mail.yml` `pass_track_free` body 英文 `claim`**~~ → **PASS · 勾销**（设计 `41e0140` · 批准 `773f46d` · 插件 `07e427d` · 测 `63019e7` · close 本提交；报告 `docs/STATUS-ember-mail-pass-track-free-copy-test.md`；下一窗升 **B2.76**；**勿宣称 B0.1 已清**）
- ~~**B2.76 CoreRpg `cash.yml` 文件头「日票」注释**~~ → **PASS · 勾销**（设计 `b39a214` · 批准 `6e7456a` · 插件 `9fb576f` · 测 `ea56bb5` · close 本提交；报告 `docs/STATUS-ember-cash-header-ticket-copy-test.md`；下一窗升 **B2.77**；**勿宣称 B0.1 已清**）
- ~~**B2.77 CoreRpg `progress.yml` 战令注释「付费轨累计日票 ×5」**~~ → **PASS · 勾销**（设计 `65a16bc` · 批准 `d579edf` · 插件 `6cb958e` · 测 `fc171a2` · close 本提交；报告 `docs/STATUS-ember-progress-pass-ticket-comment-copy-test.md`；下一窗升 **B2.78**；**勿宣称 B0.1 已清**）
- ~~**B2.78 CoreRpg `cash.yml` `daily.free_tickets` 行内「停发日票」注释**~~ → **PASS · 勾销**（设计 `8146e9c` · 批准 `811984d` · 插件 `b531320` · 测 `e9a3cf0`/`095639e` · close 本提交；报告 `docs/STATUS-ember-cash-free-tickets-comment-copy-test.md`；下一窗升 **B2.79**；**勿宣称 B0.1 已清**）
- ~~**B2.79 CoreRpg `cash.yml` Stage 4.4「不进商城日票池」注释**~~ → **PASS · 勾销**（设计 `f6c7eb7` · 批准 `358a306` · 插件 `8919053` · 测 `a20689f`/`483f627` · close 本提交；报告 `docs/STATUS-ember-cash-elite-ticket-pool-comment-copy-test.md`；下一窗升 **B2.80**；**勿宣称 B0.1 已清**）
- ~~**B2.80 CoreRpg `players/_schema-example.yml` 分区注释「日票硬顶」**~~ → **PASS · 勾销**（设计 `6a9070e` · 批准 `a046b99` · 插件 `5a8e340` · 测 `b289812`/`d46303c` · close 本提交；报告 `docs/STATUS-ember-schema-ticket-cap-comment-copy-test.md`；下一窗升 **B2.81**；**勿宣称 B0.1 已清**）
- ~~**B2.81 CoreRpg `mail.yml` `event_box` body**~~ → **PASS · 勾销**（设计 `85291bf` · 批准 `30b739b` · 插件 `699a7cb` · 测 `50ca64f` · close 本提交；报告 `docs/STATUS-ember-mail-event-box-body-copy-test.md`；下一窗升 **B2.82**；**勿宣称 B0.1 已清**）
- ~~**B2.82 CoreRpg `cash.yml` `daily` 段注释「日本/发票」**~~ → **PASS · 勾销**（设计 `af9d95d` · 批准 `efecbc6` · 插件 `a7cadbd` · 测 `195033a` · close 本提交；报告 `docs/STATUS-ember-cash-daily-section-comment-copy-test.md`；下一窗升 **B2.83**；**勿宣称 B0.1 已清**）
- ~~**B2.83 CoreRpg `players/_schema-example.yml` `dailyTicketsGranted` 行内「今日已发票」**~~ → **PASS · 勾销**（设计 `1da0023` · 批准 `81ce2be` · 插件 `37e5170` · 测 `8799866` · close 本提交；报告 `docs/STATUS-ember-schema-daily-tickets-granted-comment-copy-test.md`；下一窗升 **B2.84**；**勿宣称 B0.1 已清**）
- ~~**B2.84 CoreRpg `cash.yml` 约 L8「停发票」**~~ → **PASS · 勾销**（设计 `2b6d3ef` · 批准 `9051ebe` · 插件 `d29ea14` · 测 `c5d483a`/`3a756a2` · close 本提交；报告 `docs/STATUS-ember-cash-stamina-header-stop-issue-comment-copy-test.md`；下一窗升 **B2.85**；**勿宣称 B0.1 已清**）
- ~~**B2.85 CoreRpg `cash.yml` 约 L86 `raid.free_tickets` 行内**~~ → **PASS · 勾销**（设计 `dae3743` · 批准 `a0cf518` · 插件 `f568433` · 测 `66ed681`/`1bbcdd0` · close 本提交；报告 `docs/STATUS-ember-cash-raid-free-tickets-comment-copy-test.md`；下一窗升 **B2.86**；**勿宣称 B0.1 已清**）
- ~~**B2.86 CoreRpg `cash.yml` 约 L67 `shop.weekly_ticket.hard_cap` 行内**~~ → **PASS · 勾销**（设计 `bb16ce5` · 批准 `2e81466` · 插件 `7cd818b` · 测 `fc6d159`/`a116ef1` · close 本提交；报告 `docs/STATUS-ember-cash-weekly-shop-hardcap-comment-copy-test.md`；下一窗升 **B2.87**；**勿宣称 B0.1 已清**）
- ~~**B2.87 CoreRpg `progress.yml` 约 L137 等级门槛旁注「扣票」**~~ → **PASS · 勾销**（设计 `e4900d6` · 批准 `5881af9` · 插件 `5b41046` · 测 `89bbd57`/`b9c8f9e` · close 本提交；报告 `docs/STATUS-ember-progress-gate-ticket-comment-copy-test.md`；下一窗升 **B2.88**；**勿宣称 B0.1 已清**）
- ~~**B2.88 TrMenu `ember_shop.yml` 约 L151 周体力包 lore「硬顶免费1+氪」**~~ → **PASS · 勾销**（设计 `3ddf5fd` · 批准 `f8a899d` · 插件 `45308ff` · 测 `e4b52a4` · close 本提交；报告 `docs/STATUS-ember-shop-weekly-hardcap-lore-copy-test.md`；下一窗升 **B2.89**；**勿宣称 B0.1 已清**）
- ~~**B2.89 TrMenu `ember_shop.yml` 约 L2–3 文件头「原日票/周票」**~~ → **PASS · 勾销**（设计 `21121c0` · 批准 `b6ff3d6` · 插件 `245fe40` · 测 `b04af5b` · close 本提交；报告 `docs/STATUS-ember-shop-header-ticket-alias-copy-test.md`；下一窗升 **B2.90**；**勿宣称 B0.1 已清**）
- ~~**B2.90 CoreRpg `cash.yml` 约 L83 `weekly.free_tickets` 无旁注**~~ → **PASS · 勾销**（设计 `838cb7d` · 批准 `74509b6` · 插件 `57cd7de` · 测 `fb06ed1` · close 本提交；报告 `docs/STATUS-ember-cash-weekly-free-tickets-comment-copy-test.md`；**不升 B2.91**；交接 `docs/HANDOFF-ember-grokbot-2026-09-30.md`；**勿宣称 B0.1 已清**）
- ~~**B2.91 CoreRpg `cash.yml` L89 `abyss.free_tickets` 无旁注**~~ → **PASS · 勾销**（设计 `759c7d7` · 批准 `fde150f` · 插件 `93724a4` · 测 `96baff6` · close 本提交；报告 `docs/STATUS-ember-cash-abyss-free-tickets-comment-copy-test.md`；下一窗升 **B2.92** config.yml 密码复位；**勿宣称 B0.1 已清**）
- ~~**B2.92 CoreRpg `config.yml` L76 `mysql.password` 真实样式密码**~~ → **PASS · 勾销**（备选 B：5 文件 · 设计 `ebc41ce` · 批准 `d20dd57` · 插件 `1cd6d0f` · 测 `011594b` · close 本提交；报告 `docs/TEST-B2.92-mysql-password-reset.md`；后续代码窗候选：`MysqlStorage` 读到 `CHANGE_ME` 时改读环境变量 `MYSQL_PASSWORD`）
- ~~**B2.93 CoreRpg `cash.yml` L94 `elite.free_tickets` 无旁注**~~ → **PASS · 勾销**（设计 `b53fa71` · 批准 `df742c8` · 插件 `6203a87` · 测 `869fd38` · close 本提交；报告 `docs/TEST-B2.93-cash-elite-free-tickets-comment.md`；cash 四个 free_tickets 行内旁注本轨清零）
- ~~**B2.94 DP `EmberWeekly/option.yml` 历史扣票/B0.1 注释**~~ → **PASS · 勾销**（设计 `9d20149` · 批准 `4c7e7eb` · 插件 `ef7e350` · 测 `b6501af` · close 本提交；报告 `docs/TEST-B2.94-dp-weekly-option-comment.md`）
- ~~**B2.95 DP `EmberAbyss/option.yml` 历史扣票/B0.1 注释**~~ → **PASS · 勾销**（设计 `37a01ca` · 批准 `e384dab` · 插件 `50a946c` · 测 `3c82e34` · close 本提交；报告 `docs/TEST-B2.95-dp-abyss-option-comment.md`）
- ~~**B2.96 DP `EmberRaid/option.yml` 历史扣票/B0.1 注释 + L30 团票注释**~~ → **PASS · 勾销**（设计 `5552822` · 批准 `273a4f6` · 插件 `179a7a9` · 测 `d62e20c` · close 本提交；报告 `docs/TEST-B2.96-dp-raid-option-comment.md`；测岗更正：团戒配置在 `set.yml:29-32`，与代码缺省一致）
- ~~**B2.97 DP `EmberEliteWeekly/option.yml` 历史扣票/B0.1/有票注释**~~ → **PASS · 勾销**（设计 `aeb2bc1` · 批准 `810f0cd` · 插件 `79257fd` · 测 `8e12ca3` · close 本提交；报告 `docs/TEST-B2.97-dp-elite-option-comment.md`）
- **B2.98** DP `EmberWeekly/option.yml` L20 开本提示「已消耗体力 ×1」（**PASS · 勾销** · 设计 `58103e9` · 批准 `baccdf4` · 插件 `94d8759` · 测 `b4cec8c` · tip `docs/design-ember-dp-weekly-start-text-copy.md`；玩家可见 `text=`；不写数字；NI 票物保留）
- 断塔近阶偶发掉底厅（软观察 · 日刷频繁再升方案 B）
- 霜晶/锈轨无 Boss前压（设计刻意 · 口碑点名再动）
- AFK 二档未封死通胀（软残余）
- 灵活三窗挑刺软债（无挡级 · tip `docs/STATUS-ember-flex-trilogy-picky.md`）：~~守腕/生坠薄获取~~ → **B-flex-3 PASS · 勾销**（close 本提交）；~~灰箍抢口文案~~ → **B2.45 PASS · 勾销**（close 本提交）；~~踏步少开菜单热键愿望~~ → **B-flex-4 PASS · 勾销**（close `6b0ce3d` · CoreRpg **1.15.28**）——灵活三窗挑刺软债 **本轨清**；**勿**据此开四件甲大改
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
- ~~**B2.18**~~ → **PASS · 勾销**（`dc8ebb8` / `6783618` · close `91061b3`）。
- ~~**B2.19**~~ → **PASS · 勾销**（`24eb1b5` / `1706a98` · close `b3a00b8`）。
- ~~**B2.20**~~ → **PASS · 勾销**（`3ab2b19` / `492f996` · close `5bbaab0`）。
- ~~**B2.21**~~ → **PASS · 勾销**（`16b5334` / `5b255e9` · close `79f5d7e`）。
- ~~**B2.22**~~ → **PASS · 勾销**（`b24510c` / `eff6037` · close `48647e0`）。
- ~~**B2.23**~~ → **PASS · 勾销**（`47baea0` / `c509e57` · close `7da4dbf`）。
- ~~**B2.24**~~ → **PASS · 勾销**（`e004458` / `f004152` · close `95783b3`）。
- ~~**B2.25**~~ → **PASS · 勾销**（`e6df747` / `220cb2a` · close `35abc98`）。
- ~~**B2.26**~~ → **PASS · 勾销**（`ba460a7` / `75d0831` · close `f796f3a`）。
- ~~**B2.27**~~ → **PASS · 勾销**（`d0830a7` / `1d71c6c` · close `2aaffe3`）。
- ~~**B2.28**~~ → **PASS · 勾销**（`c74dfdf` / `4825cae` · close `edc0d9b`）。
- ~~**B2.29**~~ → **PASS · 勾销**（设计 `bc58aaf` · 批准 `c87423c` · 施工 `9875d1b` · 测 `0420ef4` · close `91adf76`）。
- ~~**B2.30**~~ → **PASS · 勾销**（设计 `3e7e1ba` · 批准 `a9f95e5` · 施工 `b9b1786` · 测 `2f2ba60` · close `72d37b3`）。
- ~~**B2.31**~~ → **PASS · 勾销**（设计 `1bdacd8` · 批准 `4359e89` · 施工 `c0e8fcc` · 测 `b2d9773` · close `c8ab22c`）。
- ~~**B2.32**~~ → **PASS · 勾销**（`1cdf73b` / `0c70273` · close `42054ad`；live `&7mat_*` 本轨归零）。
- ~~**B2.33**~~ → **PASS · 勾销**（`fbef72e` / `90797d7` · close `973e941`；`&7gem_*`=3）。
- ~~**B2.34**~~ → **PASS · 勾销**（`c5647d3` / `7bc605a` · close `5b47896`；`&7gem_*`=2）。
- ~~**B2.35**~~ → **PASS · 勾销**（`1ef8a41` / `9b2333f` · close `bab3b39`；`&7gem_*`=1）。
- ~~**B2.36**~~ → **PASS · 勾销**（`3e3c63b` / `a531ebf` · close `54ef5c4`；live `&7gem_*`=0）。
- ~~**B2.37**~~ → **PASS · 勾销**（close `da8088d`）。
- ~~**B2.38**~~ → **PASS · 勾销**（close 本提交）。
- ~~**B2.39**~~ → **PASS · 勾销**（close `f0f6255`）。
- ~~**B2.40**~~ → **PASS · 勾销**（close `611f999`）。
- ~~**B2.41**~~ → **PASS · 勾销**（close `af616a3`）。
- ~~**B2.42**~~ → **PASS · 勾销**（close `ef6ff87`）。
- ~~**B2.43**~~ → **PASS · 勾销**（close `7a00524`）。
- ~~**B2.44**~~ → **PASS · 勾销**（close 本提交）。
- ~~**B-flex-3**~~ → **PASS · 勾销**（close 本提交）。
- ~~**B2.45**~~ → 烬砧灰箍抢口文案（**PASS · 勾销** · close `7dc96c9`）。
- ~~**B-flex-4**~~ → 踏步少开菜单热键（**PASS · 勾销** · close `6b0ce3d` · CoreRpg **1.15.28**）。
- ~~**B2.46**~~ → 拆解菜单管理注释人话化（**PASS · 勾销** · close 本提交）。
- ~~**B2.47**~~ → 拆解菜单 Open tell **PASS · 勾销**（测 `6952749` · close 本提交）。
- ~~**B2.48**~~ → 公会菜单 Open tell **PASS · 勾销**（测 `8d3f3cf` · close 本提交）。
- ~~**B2.49**~~ → friends/mail 文件头 **PASS · 勾销**（测 `4f52d81` · close 本提交）。
- ~~**B2.50**~~ → arena/settings/set 文件头 **PASS · 勾销**（测 `b84e948` · close 本提交）。
- ~~**B2.51**~~ → enhance/socket 文件头 **PASS · 勾销**（测 `929206e` · close 本提交）。
- ~~**B2.52**~~ → shop 文件头 **PASS · 勾销**（测 `38eeecc` · close 本提交）。
- ~~**B2.53**~~ → hub AFK 注释 **PASS · 勾销**（测 `8c76d55` · close 本提交）。
- ~~**B2.54**~~ → NI disassemble 文件头 **PASS · 勾销**（测 `c642e4f` · close `ee02ed6`）。
- ~~**B2.55**~~ → NI pets 文件头 **PASS · 勾销**（测 `7765927` · close 本提交）。
- ~~**B2.56**~~ → bestiary 阶段奖 tell **PASS · 勾销**（测 `707f6f7` · close 本提交）。
- ~~**B2.57**~~ → NI gear-t1 文件头 **PASS · 勾销**（测 `01a41b2` · close `c1e7202`）。
- ~~**B2.58**~~ → NI gear-t1 L35 stub **PASS · 勾销**（测 `162cae0` · close 本提交）。
- ~~**B2.59**~~ → TrMenu README 占位段 **PASS · 勾销**（测 `2ae758f` · close 本提交）。
- ~~**B2.60**~~ → CoreRpg STUB 文件头 **PASS · 勾销**（测 `5abd67d` · close 本提交）。
- ~~**B2.61**~~ → schema 分区注释 **PASS · 勾销**（测 `0098df7` · close 本提交）。
- ~~**B2.62**~~ → cash 勋阶 stub 注释 **PASS · 勾销**（测 `8a0aec7` · close 本提交）。
- ~~**B2.63**~~ → 灾厄 monster stub 注释 **PASS · 勾销**（测 `8088429` · close 本提交）。
- ~~**B2.64**~~ → DP README 表内 stub **PASS · 勾销**（测 `2f517c4` · close 本提交）。
- ~~**B2.65**~~ → DP README L4 进本口径 **PASS · 勾销**（测 `1c099c5` · close 本提交）。
- ~~**B2.66**~~ → DP README 次数段发放备忘 **PASS · 勾销**（测 `a1e776f` · close 本提交）。
- ~~**B2.67**~~ → DP README 票表规则列 **PASS · 勾销**（测 `1f2770f` · close 本提交）。
- ~~**B2.68**~~ → DP README DEBT 段维护备忘 **PASS · 勾销**（测 `e56ee7d` · close 本提交）。
- ~~**B2.69**~~ → DP README 次数段导语「入场卷」 **PASS · 勾销**（测 `a8dfda7` · close 本提交）。
- ~~**B2.70**~~ → DP README `## 已知占位` 段标题 **PASS · 勾销**（测 `71eb7cd` · close 本提交）。
- ~~**B2.71**~~ → DP README `## 次数（入场券）` 段标题 **PASS · 勾销**（测 `62abf4b` · close 本提交）。
- ~~**B2.72**~~ → DP README L11「入场券条件」 **PASS · 勾销**（测 `f092865` · close 本提交）。
- ~~**B2.73**~~ → mail `season_pass_stub` title/body「占位」 **PASS · 勾销**（测 `e166783` · close 本提交）。
- ~~**B2.74**~~ → mail `vip_daily_gift` body「勋阶正式累进前」 **PASS · 勾销**（测 `7b58d70` · close 本提交）。
- ~~**B2.75**~~ → mail `pass_track_free` body 英文 `claim` **PASS · 勾销**（测 `63019e7` · close 本提交）。
- ~~**B2.76**~~ → cash.yml 文件头「日票」注释 **PASS · 勾销**（测 `ea56bb5` · close 本提交）。
- ~~**B2.77**~~ → progress.yml 战令注释「日票 ×5」 **PASS · 勾销**（测 `fc171a2` · close 本提交）。
- ~~**B2.78**~~ → cash.yml `daily.free_tickets`「停发日票」注释 **PASS · 勾销**（测 `e9a3cf0`/`095639e` · close 本提交）。
- ~~**B2.79**~~ → cash.yml Stage 4.4「不进商城日票池」注释 **PASS · 勾销**（测 `a20689f`/`483f627` · close 本提交）。
- ~~**B2.80**~~ → schema「日票硬顶」分区注释 **PASS · 勾销**（测 `b289812`/`d46303c` · close 本提交）。
- ~~**B2.81**~~ → mail `event_box` body **PASS · 勾销**（测 `50ca64f` · close 本提交）。
- ~~**B2.82**~~ → cash `daily` 段注释「日本/发票」 **PASS · 勾销**（测 `195033a` · close 本提交）。
- ~~**B2.83**~~ → schema `dailyTicketsGranted` 行内「今日已发票」 **PASS · 勾销**（测 `8799866` · close 本提交）。
- ~~**B2.84**~~ → cash.yml 约 L8「停发票」 **PASS · 勾销**（测 `c5d483a`/`3a756a2` · close 本提交）。
- ~~**B2.85**~~ → cash.yml 约 L86 `raid.free_tickets` 行内 **PASS · 勾销**（测 `66ed681`/`1bbcdd0` · close 本提交）。
- ~~**B2.86**~~ → cash.yml 约 L67 `shop.weekly_ticket.hard_cap` 行内 **PASS · 勾销**（测 `fc6d159`/`a116ef1` · close 本提交）。
- ~~**B2.87**~~ → progress.yml 约 L137 等级门槛旁注「扣票」 **PASS · 勾销**（测 `89bbd57`/`b9c8f9e` · close 本提交）。
- ~~**B2.88**~~ → ember_shop.yml 约 L151 周体力包 lore **PASS · 勾销**（测 `e4b52a4` · close 本提交）。
- ~~**B2.89**~~ → ember_shop.yml 约 L2–3 文件头 **PASS · 勾销**（测 `b04af5b` · close 本提交）。
- ~~**B2.90**~~ → cash.yml 约 L83 `weekly.free_tickets` **PASS · 勾销**（测 `fb06ed1` · close 本提交；**不升 B2.91**；交接停工）。
- ~~**B2.91**~~ → cash.yml L89 `abyss.free_tickets` **PASS · 勾销**（测 `96baff6` · close 本提交）。
- ~~**B2.92**~~ → mysql.password 同值 5 文件复位 **PASS · 勾销**（测 `011594b` · close 本提交）。
- ~~**B2.93**~~ → cash.yml L94 `elite.free_tickets` **PASS · 勾销**（测 `869fd38` · close 本提交）。
- ~~**B2.94**~~ → DP EmberWeekly option.yml 注释 **PASS · 勾销**（测 `b6501af` · close 本提交）。
- ~~**B2.95**~~ → DP EmberAbyss option.yml 注释 **PASS · 勾销**（测 `3c82e34` · close 本提交）。
- ~~**B2.96**~~ → DP EmberRaid option.yml 注释 **PASS · 勾销**（测 `d62e20c` · close 本提交）。
- ~~**B2.97**~~ → DP EmberEliteWeekly option.yml 注释 **PASS · 勾销**（测 `8e12ca3` · close 本提交）；DP option 注释四份清零。
- ~~**B2.98**~~ → DP EmberWeekly L20 开本提示（**PASS · 勾销** · 插件 `94d8759` · 测 `b4cec8c`）。
- ~~**B2.99**~~ → DP EmberRaid L22 开本提示（**PASS · 勾销** · 插件 `b90e999` · 测 `16d74cb`）。
- ~~**B2.100**~~ → DP EmberAbyss L22 开本提示（**PASS · 勾销** · 插件 `252fe8e` · 测 `c3d662c`）。
- ~~**B2.101**~~ → DP EmberEliteWeekly L20 开本提示（**PASS · 勾销** · 插件 `e55540a` · 测 `713b407`）。
- ~~**B2.102**~~ → TrMenu ember_hub.yml L249+L250 精英 lore（**PASS · 勾销** · 插件 `862d21c` · 测 `0884f55`）。
- ~~**B2.112**~~ → DP EmberEliteWeekly/task/timeout.yml L6（**PASS · 勾销** · 插件 `684ede2` · 测 `4adadc4`）。
- ~~**B2.114**~~ → CoreRpg quest.yml L389 hint（**PASS · 勾销** · 插件 `6817eb9` · 测 `13771d1`）。
- ~~**B2.110**~~ → TrMenu ember_raid.yml L76-77 进本 tell（**PASS · 勾销** · 插件 `22b1084` · 测 `e405013`；静态 PASS，实测发现方括号丢失，B2.122 已修）。
- ~~**B2.111**~~ → TrMenu ember_abyss.yml L77 进本 tell（**PASS · 勾销** · 插件 `4cd3be3` · 测 `7ef8d00`；静态 PASS，实测发现方括号丢失，B2.122 已修）。
- ~~**B2.122**~~ → TrMenu 全部 click `tell:` 行 `[..]`→`【..】`（TabooLib 组件语法吞方括号+颜色；25 菜单 91 行；**实测 PASS · 勾销** · `00cba0d` · 报告 `docs/TEST-B2.122-trmenu-brackets-live.md`）。
- ~~**B2.113**~~ → TrMenu ember_hub.yml L215/L266/L280 硬编码体力 45/30/50 → `%corerpg_stamina_cost_weekly/abyss/raid%`（**实测 PASS · 勾销** · 本提交 · 2026-10-01 19:32 CST 机器人 /ember 实测：周常「进本消耗 §e45 §8体力」、深渊「消耗 §e30」、团本「消耗 §e50」均由 %corerpg_stamina_cost_weekly/abyss/raid% 解析（papi parse 45/30/50），unreplacedPAPI=0）
- ~~**B2.118**~~ → TrMenu ember_raid.yml L22/L109/L118 写死 50 → `%corerpg_stamina_cost_raid%`（**实测 PASS · 勾销** · 本提交 · 2026-10-01 19:35 CST 机器人实测：打开团本 tell「【团本】 需 3～5 人 · 消耗 50 体力（本周首次免费）」、次数说明 lore「其后 50 体力」、点击 tell「其后 50 体力；需 3～5 人。」均由 %corerpg_stamina_cost_raid% 解析，unreplacedPAPI=0）
- ~~**B2.120**~~ → TrMenu ember_abyss.yml L49/L54/L130/L139 写死 30 → `%corerpg_stamina_cost_abyss%`（**实测 PASS · 勾销** · 本提交 · 2026-10-01 19:38 CST 机器人实测：体力 10 时灰显 lore「需要 §e30 §7体力 · 当前 10/90」+ 点击 tell「体力不足，需 30 点体力」；体力满时次数说明 lore「消耗 §e30 §7体力（与日常同池）」+ 点击 tell「深渊消耗 30 体力」均由 %corerpg_stamina_cost_abyss% 解析；L41 condition 字面量 30 按注释保留）
- **B2.121** → TrMenu ember_abyss.yml L132 旧票制「硬顶建议 ≤2/日（免费+购）」（排在 B2.120 后；待出稿）。
- **B2.119** → CoreRpg TicketEntryService 进本失败退还提示几处例外（:189/:209/首免/人数不足）（需 build；排在 B2.108 后；待出稿）。
- ~~**B2.115**~~ → DP EmberWeekly/task/timeout.yml L6「周常超时失败（体力不返还）」首免/OP 不准（**PASS · 勾销（DP 载入实测；超时触发未实测）** · `731ef8d` · L6 → 「§c周常超时失败。§7这次不算通关。」（去掉首免/OP 时不准的「体力不返还」，与 B2.112 精英口径一致、不写扣费）；2026-10-01 19:35 CST dp reload「[EmberWeekly] 地牢内容导入完毕」；超时实测未触发：bot 挂机周常 17 分钟时副本被判通关（weekly_clear），未到 1500s；同机制 Elite 720s 超时 20:00:51 实测包 {"color":"red","text":"试炼失败。"},{"color":"gray","text":"这次不算通关，本周还能再来。"} 渲染正常）
- ~~**B2.116**~~ → DP EmberRaid/task/timeout.yml L7「团本超时失败（体力不返还）」首免/OP 不准（**PASS · 勾销（DP 载入实测；超时触发未实测）** · 本提交 · L7 → 「§c团本超时失败。§7这次不算通关。」（同 B2.115 口径）；2026-10-01 19:35 CST dp reload「[EmberRaid] 地牢内容导入完毕」；超时实测未触发：3 名无敌挂机 bot 在团本内 30 分钟（20:12）被判通关 raid_clear（并发了团戒），未到 2400s；同机制 Elite 超时实测见 B2.109）
- ~~**B2.117**~~ → `plugins/CoreRpg/quest.yml:377` done「试炼每周一次」→ 每人每周限通关口径（**实测 PASS · 勾销** · 本提交 · 2026-10-01 19:43 CST 机器人实测（quest set 9 0 → /corerpg quest talk → 复原 1 0）：包 {"color":"gold","text":"灰烛："},{"color":"white","text":"试炼每人每周限通关一次。稳定符在周首通里。"}）
- ~~**B2.105**~~ → plugins/CoreRpg/set.yml L27 注释「团票每周 1 张 ⇒ 每角色每周至多 1 次 COMPLETE」旧票制口径 → 体力进本、同周可多次通关、团戒靠周首通去重（仅注释）（**实测 PASS · 勾销** · 本提交 · 2026-10-01 19:45 CST corerpg reload「SetService loaded: 余烬同袍 blades=4」+ 机器人 /corerpg set 输出正常）。
- ~~**B2.106**~~ → plugins/CoreRpg/cash.yml L86、L92 段首 注释：raid「菜单承诺每周团本票×1」→ 现行 0、体力进本首免；elite 段首「持有硬顶 1」→ hard_cap 现无代码消费（TicketGrantService 只读不用），键保留（仅注释，键值零改）（**实测 PASS · 勾销** · 本提交 · 2026-10-01 19:47 CST corerpg reload 后机器人 /corerpg stamina「消耗 日常30 · 周45 · 精英40 · 深渊30 · 团50 · 本周免费抵扣 周本×1 · 精英×1 · 团×1」不变）。
- ~~**B2.109**~~ → mineflayer-tests 补用例：menu-copy-check.js（hub 周常/深渊/团本体力 lore = 实时单价；团本打开/次数说明/开始协作 tell；深渊次数说明、开始下潜、低体力灰显 lore+tell，共 12 断言）+ timeout-live.js（周常/团本/精英超时实测，LINE=weekly|raid|elite）；旧脚本迁移见前序 6a1e1f9/a8dbdcb（**实测 PASS · 勾销** · 本提交 · 2026-10-01 19:52 CST menu-copy-check PASS=12 FAIL=0；精英超时 20:00:51 实测包「试炼失败。这次不算通关，本周还能再来。」+「精英试炼超时失败」，718s）。
- ~~**B2.103**~~ → CoreRpgPlugin.cmdAbyss（/corerpg abyss 兜底）「日限 1 次 · 进本扣余烬深渊票」「背包深渊票 / 软计数 /1（以票为准）」「/dp start EmberAbyss」「无票时 /ni give…」票制旧文 → 「无尽波次下潜 · 进本消耗 {costOf(abyss)} 体力（与日常同池）」+ 当前体力/历史最深层 + 「进本：打开枢纽菜单 → 深渊」；CoreRpg 1.15.28→1.15.29（**PASS · 勾销（构建/部署实测；兜底路径 live 不可达）** · 本提交 · 2026-10-01 20:20 CST 构建并部署 1.15.29，启用日志「CoreRpg 1.15.29 enabled」；该兜底只在 AbyssSettleService 缺失时可达，live 常驻 settle 服务，无法用 bot 触发（静态核对））。

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
| B2.18 | 奖励预览页 `§8NI id:`/`§8NI:` 人话对齐 | 五份 `ember_*_rewards.yml` 玩家 lore 无字面 `§8NI`；中文主名/概率句保留；零改 loot/DP/MM/体力；套装 `gear_ember_*` 不做（B 旁附）；不开精英预览壳 | **PASS · 勾销**（设计 `5c2f7f2` · 批准 `cef1017` · 施工 `dc8ebb8` · 测 `6783618` · close `91061b3`） |
| B2.19 | 套装菜单 `gear_ember_*` / 团戒 NI 人话对齐 | `ember_set.yml` 4 句玩家可见无字面 `gear_ember_*`/`acc_ember_raid_ring`（→中文）；零改套装数值/掉落/体力；拆解重铸石灰字不做（B soft）；不开精英预览壳；与 B-flex/B-anvil 不捆 | **PASS · 勾销**（设计 `0687fe5` · 批准 `157543b` · 施工 `24eb1b5` · 测 `1706a98` · close `b3a00b8`） |
| B2.20 | 拆解菜单重铸石灰字 `mat_ember_reforge_stone` 人话对齐 | `ember_disassemble.yml` 重铸 lore 无字面 `mat_ember_reforge_stone`（荐删 L67；上行已有「余烬重铸石」）；零改拆解数值/掉落/体力；NI 物品 lore 不做（B soft）；不开精英预览壳；与 B-flex/B-anvil 不捆 | **PASS · 勾销**（设计 `c6d8b8b` · 批准 `e7574a3` · 施工 `3ab2b19` · 测 `492f996` · close `5bbaab0`） |
| B2.21 | CoreRpg 重铸缺料 chat 裸 id 人话对齐 | `ScrapService.cmdReforge` 缺料 sendMessage 无字面 `mat_ember_reforge_stone`（→「余烬重铸石」）；零改消耗 ×1 / `stone_ni_id` / 词缀逻辑；NI 物品 lore 不做（B soft）；不开精英预览壳；与 B-flex/B-anvil 不捆 | **PASS · 勾销**（设计 `e81c904` · 批准 `92d6ee3` · 施工 `16b5334` · 测 `5b255e9` · close `79f5d7e`；CoreRpg **1.15.27**） |
| B2.22 | NI 重铸石物品 lore 裸 id 人话对齐 | `ember-disassemble.yml` 内 `mat_ember_reforge_stone` lore 无字面 id（荐删 L9；显示名已有「余烬重铸石」）；零改 NI 数值/配方/给物/`stone_ni_id`/消耗；其它 `&7mat_*` 不做（B soft）；不开精英预览壳；与 B-flex/B-anvil 不捆 | **PASS · 勾销**（设计 `115ebab` · 批准 `7724338` · 施工 `b24510c` · 测 `eff6037` · close `48647e0`） |
| **B2.23** | NI 重铸石物品 lore 斜杠去指令化 | `ember-disassemble.yml` 内 `mat_ember_reforge_stone` lore 无字面 `/corerpg`（荐 L11 →「用于枢纽 · 拆解 → 重铸」）；零改 NI 数值/配方/给物/`stone_ni_id`/消耗；其它 `&7mat_*` / 其它斜杠不做（B soft）；不开精英预览壳；与 B-flex/B-anvil 不捆 | **PASS · 勾销**（设计 `6485a89` · 批准 `75ff5e7` · 施工 `47baea0` · 测 `c509e57` · close `7da4dbf`） |
| **B2.24** | NI 余烬碎片物品 lore 裸 id 人话对齐 | `ember-dungeon.yml` 内 `mat_ember_shard` lore 无字面 id（荐删 L9；显示名已有「余烬碎片」）；零改 NI 数值/配方/给物/掉落；其它 8 件 `&7mat_*` / 其它斜杠不做（B soft）；不开精英预览壳；与 B-flex/B-anvil 不捆 | **PASS · 勾销**（设计 `9931476` · 批准 `60f43b7` · 施工 `e004458` · 测 `f004152` · close `95783b3`） |
| **B2.25** | NI 余烬骨尘物品 lore 裸 id 人话对齐 | `ember-dungeon.yml` 内 `mat_ember_bone_dust` lore 无字面 id（荐删 L15；显示名已有「余烬骨尘」）；零改 NI 数值/配方/给物/掉落；其它 7 件 `&7mat_*` / 其它斜杠不做（B soft）；不开精英预览壳；与 B-flex/B-anvil 不捆 | **PASS · 勾销**（设计 `355603e` · 批准 `1b52ef9` · 施工 `e6df747` · 测 `220cb2a` · close `35abc98`） |
| **B2.26** | NI 余烬核心碎片物品 lore 裸 id 人话对齐 | `ember-dungeon.yml` 内 `mat_ember_core_fragment` lore 无字面 id（荐删 L21；显示名已有「余烬核心碎片」）；零改 NI 数值/配方/给物/掉落；其它 6 件 `&7mat_*` / 其它斜杠不做（B soft）；不开精英预览壳；与 B-flex/B-anvil 不捆 | **PASS · 勾销**（设计 `0e76453` · 批准 `28a5048` · 施工 `ba460a7` · 测 `75d0831` · close `f796f3a`） |
| **B2.27** | NI 誓约重置券物品 lore 裸 id 人话对齐 | `ember-covenant-talent.yml` 内 `mat_ember_covenant_reset` lore 无字面 id（荐删 L8；显示名已有「誓约重置券」）；零改 NI 数值/配方/给物；不动 L10 `/corerpg`；其它 5 件 `&7mat_*` / 其它斜杠不做（B soft）；不开精英预览壳；与 B-flex/B-anvil 不捆 | **PASS · 勾销**（设计 `8bb16c9` · 批准 `1d659d0` · 施工 `d0830a7` · 测 `1d71c6c` · close `2aaffe3`） |
| **B2.28** | NI 天赋重置券物品 lore 裸 id 人话对齐 | `ember-covenant-talent.yml` 内 `mat_ember_talent_reset` lore 无字面 id（荐删 L19；显示名已有「天赋重置券」）；零改 NI 数值/配方/给物；不动 L21 `/corerpg`；其它 4 件 `&7mat_*` / 其它斜杠不做（B soft）；不开精英预览壳；与 B-flex/B-anvil 不捆 | **PASS · 勾销**（设计 `2c50336` · 批准 `8ff1b4d` · 施工 `c74dfdf` · 测 `4825cae` · close `edc0d9b`） |
| **B2.29** | NI 余烬魂尘物品 lore 裸 id 人话对齐 | `ember-pets.yml` 内 `mat_ember_soul_dust` lore 无字面 id（荐删 L35；显示名已有「余烬魂尘」）；零改 NI 数值/配方/给物；不动 L37 `/corerpg`；其它 3 件 `&7mat_*` / 其它斜杠不做（B soft）；不开精英预览壳；与 B-flex/B-anvil 不捆 | **PASS · 勾销**（设计 `bc58aaf` · 批准 `c87423c` · 施工 `9875d1b` · 测 `0420ef4` · close `91adf76`） |
| **B2.30** | NI 灾厄余烬物品 lore 裸 id 人话对齐 | `ember-abyss-calamity.yml` 内 `mat_calamity_ember` lore 无字面 id（荐删 L8；显示名已有「灾厄余烬」）；零改 NI 数值/配方/给物/掉落；其它 2 件 `&7mat_*`、`cosmetic_*`、其它斜杠不做（B soft）；不开精英预览壳；与 B-flex/B-anvil 不捆 | **PASS · 勾销**（设计 `3e7e1ba` · 批准 `a9f95e5` · 施工 `b9b1786` · 测 `2f2ba60` · close `72d37b3`） |
| **B2.31** | NI 余烬保护券物品 lore 裸 id 人话对齐 | `ember-enhance-gems.yml` 内 `mat_ember_protect_scroll` lore 只删 L9 裸 id；显示名「余烬保护券」不改，L10/L11 保留；零改 NI 数值/配方/给物/强化逻辑；stable charm、`gem_*`、其它斜杠不做（B soft）；不开精英预览壳 | **PASS · 勾销**（设计 `1bdacd8` · 批准 `4359e89` · 施工 `c0e8fcc` · 测 `b2d9773` · close `c8ab22c`） |
| **B2.32** | NI 余烬稳固符物品 lore 裸 id 人话对齐 | `ember-enhance-gems.yml` 内 `mat_ember_stable_charm` lore 只删实际 L20 灰字裸 id；显示名「余烬稳固符」不改，L21/L22 两行说明保留；零改 NI 数值/配方/给物/强化逻辑；`gem_*`、其它 `&7mat_*`、其它斜杠不做（B soft）；精英壳勿硬开；B0.1 仍挂 | **PASS · 勾销**（设计 `cf7eaaf` · 批准 `c4194e4` · 施工 `1cdf73b` · 测 `0c70273` · close `42054ad`；live `&7mat_*` 本轨归零） |
| **B2.33** | NI 余烬锋利石物品 lore 裸 id 人话对齐 | `ember-enhance-gems.yml` 内 `gem_ember_sharp` lore 只删实际 L32 灰字裸 id；显示名「锋利石」不改，L33/L34 两行说明保留；零改 NI 数值/配方/给物/镶嵌逻辑；其余 3 件 gem、其它 `&7gem_*`、斜杠、cosmetic/pet 不做（B soft）；精英壳勿硬开；B0.1 仍挂 | **PASS · 勾销**（设计 `48b5218` · 批准 `6a43ab1` · 施工 `fbef72e` · 测 `90797d7` · close `973e941`；`&7gem_*`=3） |
| **B2.34** | NI 余烬稳固石物品 lore 裸 id 人话对齐 | `ember-enhance-gems.yml` 内 `gem_ember_steady` lore 只删实际 L43 灰字裸 id；显示名「稳固石」不改，L44/L45 两行说明保留；零改 NI 数值/配方/给物/镶嵌逻辑；`gem_ember_drain` / `gem_ember_gale`、其它 `&7gem_*`、斜杠、cosmetic/pet 不做（B soft）；精英壳勿硬开；B0.1 仍挂 | **PASS · 勾销**（设计 `baa2705` · 批准 `19a8982` · 施工 `c5647d3` · 测 `7bc605a` · close `5b47896`；`&7gem_*`=2） |
| **B2.35** | NI 余烬汲取石物品 lore 裸 id 人话对齐 | `ember-enhance-gems.yml` 内 `gem_ember_drain` lore 只删实际 L54 灰字裸 id；显示名「汲取石」不改，L55/L56 两行说明保留；零改 NI 数值/配方/给物/镶嵌逻辑；`gem_ember_gale`、斜杠、cosmetic/pet 不做（B soft）；精英壳勿硬开；B0.1 仍挂 | **PASS · 勾销**（设计 `c9cdb28` · 批准 `bf3bf4b` · 施工 `1ef8a41` · 测 `9b2333f` · close `bab3b39`；`&7gem_*`=1） |
| **B2.36** | NI 余烬疾风石物品 lore 裸 id 人话对齐 | `ember-enhance-gems.yml` 内 `gem_ember_gale` lore 只删实际 L65 灰字裸 id；显示名「疾风石」不改，L66/L67 两行说明保留；零改 NI 数值/配方/给物/镶嵌逻辑；斜杠、cosmetic/pet 不做（B soft）；精英壳勿硬开；B0.1 仍挂 | **PASS · 勾销**（设计 `eca16cd` · 批准 `dd582c8` · 施工 `3e3c63b` · 测 `a531ebf` · close `54ef5c4`；live `&7gem_*`=0） |
| ~~**B2.37**~~ | NI 灾厄外观碎片物品 lore 裸 id 人话对齐 | 只删 lore 灰字裸 id；显示名与两行说明保留 | **PASS · 勾销**（设计 `3717031` · 批准 `d786875` · 施工 `3c49b53` · 测 `7f21335` · close 本提交；`&7cosmetic_`=0） |
| ~~**B2.38**~~ | NI 余烬使魔·灰灵物品 lore 裸 id 人话对齐 | 只删 lore 灰字裸 id；显示名与说明、summon 斜杠保留 | **PASS · 勾销**（设计 `965ab2b` · 批准 `bbded5f` · 施工 `7dbf09c` · 测 `72b544e` · close 本提交；`&7pet_` 仅剩 cinder） |
| ~~**B2.39**~~ | NI 余烬使魔·烬火物品 lore 裸 id 人话对齐 | 只删 lore 灰字裸 id；显示名与说明、summon 斜杠保留 | **PASS · 勾销**（设计 `88c53bd` · 批准 `8e2ef70` · 施工 `6e08d32` · 测 `a188a8b` · close `f0f6255`；`&7pet_*` 本轨归零） |
| ~~**B2.40**~~ | NI 誓约重置券物品 lore 斜杠去指令化 | L9 → `&7用于枢纽 · 誓约`；L8 保留 | **PASS · 勾销**（设计 `d49dd7b` · 批准 `1a0f820` · 施工 `c7d55d1` · 测 `9affd15` · close `611f999`） |
| ~~**B2.41**~~ | NI 天赋重置券物品 lore 斜杠去指令化 | L20 → `&7用于枢纽 · 天赋（日免费用尽后）`；L19 保留 | **PASS · 勾销**（设计 `5e6d605` · 批准 `506ea1a` · 施工 `067002d` · 测 `960ac62` · close `af616a3`） |
| ~~**B2.42**~~ | NI 余烬灰灵物品 lore summon 斜杠去指令化 | L10 → `&7用于枢纽 · 使魔`；L8/L9 保留 | **PASS · 勾销**（设计 `696c80c` · 批准 `48dc3d1` · 施工 `1dbcb88` · 测 `d855ce3` · close `ef6ff87`） |
| ~~**B2.43**~~ | NI 余烬烬火物品 lore summon 斜杠去指令化 | L22 → `&7用于枢纽 · 使魔`；L20/L21 保留 | **PASS · 勾销**（设计 `e8d3704` · 批准 `2d2fcf8` · 施工 `94ad273` · 测 `3c30405` · close `7a00524`） |
| ~~**B2.44**~~ | NI 余烬魂尘物品 lore feed 斜杠去指令化 | L34 → `&a用于枢纽 · 使魔 · 投喂`；L33/L35 保留 | **PASS · 勾销**（设计 `8ef2acb` · 批准 `a82321d` · 施工 `513bf8b` · 测 `4f628b5` · close 本提交） |
| **B2.45** | 烬砧灰箍抢口文案（优先沉铁锭） | forge P 插主句；part A 插主句；part I 插旁句；消耗 ×12/×2 保留；**勿**改 part.yml | **PASS · 勾销**（设计 `ab67790` · 批准 `a040ce2` · 插件 `3066746` · 测 `d6768d9`/`c69bd4d` · close 本提交；报告 `docs/STATUS-ember-ash-brace-contention-copy-test.md`） |
| **B-flex-4** | 踏步少开菜单热键（挑刺 #2） | 潜行+Q → cancel+cast；菜单口保留；**勿**改 CD14/距离5 | **PASS · 勾销**（设计 `a9311ac` · 批准 `7b4a8b6` · 插件 `005a03b` · 测 `a1d5f7c` · close `6b0ce3d`；报告 `docs/STATUS-ember-flex-hotkey-sneak-drop-test.md`；CoreRpg **1.15.28**） |
| **B2.46** | 拆解菜单管理注释人话化 | L89→`§8点左侧即可分解或重铸`；零改拆解/重铸数值 | **PASS · 勾销**（设计 `50a272d` · 批准 `b2b2f7a` · 插件 `530c2f8` · 测 `89b1117` · close 本提交；报告 `docs/STATUS-ember-disassemble-rule-copy-test.md`） |
| **B2.47** | 拆解菜单 Open tell 管理口吻 | L22→持装+「点左侧即可」；无「逻辑待 CoreRpg」；零改 scrap/reforge | **PASS · 勾销**（测 `6952749` · close 本提交） |
| **B2.48** | 公会菜单 Open tell 管理口吻 | L22→创建/日捐/周盟 Boss +「点下方即可」；无「逻辑待 CoreRpg」 | **PASS · 勾销**（测 `8d3f3cf` · close 本提交） |
| **B2.49** | friends/mail 文件头管理口吻 | friends/mail L2 删「；逻辑待 CoreRpg 接线」 | **PASS · 勾销**（测 `4f52d81` · close 本提交） |
| **B2.50** | arena/settings/set 文件头管理口吻 | arena/settings/set L2 删管理尾；set 无路径 | **PASS · 勾销**（测 `b84e948` · close 本提交） |
| **B2.51** | enhance/socket 文件头教斜杠 | enhance/socket L2 去「跑 /corerpg」；shop/hub/NI 不捆 | **PASS · 勾销**（测 `929206e` · close 本提交） |
| **B2.52** | shop 文件头教斜杠 | shop L3 去「/corerpg coin」；hub/NI 不捆 | **PASS · 勾销**（测 `38eeecc` · close 本提交） |
| **B2.53** | hub AFK 注释教斜杠 | hub L188 去「/corerpg afk」；NI 不捆 | **PASS · 勾销**（测 `8c76d55` · close 本提交） |
| **B2.54** | NI disassemble 文件头教斜杠 | NI L3 改功能摘要；pets 不捆 | **PASS · 勾销**（测 `c642e4f` · close `ee02ed6`） |
| **B2.55** | NI pets 文件头管理口吻 | NI L1 改功能摘要 | **PASS · 勾销**（测 `7765927` · close 本提交） |
| **B2.56** | bestiary 阶段奖 tell 管理口吻 | 阶段奖 tell 对齐暂未开放 | **PASS · 勾销**（测 `707f6f7` · close 本提交） |
| **B2.57** | NI gear-t1 文件头管理口吻 | NI L1–L2 改功能摘要 | **PASS · 勾销**（测 `01a41b2` · close `c1e7202`） |
| **B2.58** | NI gear-t1 L35 分隔注释 stub | L35 去 stub | **PASS · 勾销**（测 `162cae0` · close 本提交） |
| **B2.59** | TrMenu README 占位段管理口吻 | README「占位 / 待接线备忘」 | **PASS · 勾销**（测 `2ae758f` · close 本提交） |
| **B2.60** | CoreRpg cash/covenant/talent 文件头维护备忘 | 三文件头改为「维护备忘」 | **PASS · 勾销**（测 `5abd67d` · close 本提交） |
| **B2.61** | CoreRpg `_schema-example.yml` 分区注释 | L16/L27 去「插件岗写入」 | **PASS · 勾销**（测 `0098df7` · close 本提交） |
| **B2.62** | CoreRpg `cash.yml` 勋阶 stub 注释 | L98/L104 去 stub 口吻 | **PASS · 勾销**（测 `8a0aec7` · close 本提交） |
| **B2.63** | DP EmberCalamity monster.yml stub 注释 | L3 去 stub 口吻 | **PASS · 勾销**（测 `8088429` · close 本提交） |
| **B2.64** | DP README-ember-dungeons 表内 stub | L19/L20 去 stub 口吻 | **PASS · 勾销**（测 `2f517c4` · close 本提交） |
| **B2.65** | DP README L4 进本口径 | 票/斜杠→菜单+体力备忘 | **PASS · 勾销**（测 `1c099c5` · close 本提交） |
| **B2.66** | DP README 次数段发放备忘 | 「岗」+`/ni give`→维护备忘 | **PASS · 勾销**（测 `a1e776f` · close 本提交） |
| **B2.67** | DP README 票表规则列 | 日发/周发→维护备忘 | **PASS · 勾销**（测 `1f2770f` · close 本提交） |
| **B2.68** | DP README DEBT 段维护备忘 | DEBT→维护备忘 | **PASS · 勾销**（测 `e56ee7d` · close 本提交） |
| **B2.69** | DP README 次数段导语「入场卷」 | 入场卷→维护备忘 | **PASS · 勾销**（测 `a8dfda7` · close 本提交） |
| **B2.70** | DP README `## 已知占位` 段标题 | 已知占位→维护备忘 | **PASS · 勾销**（测 `71eb7cd` · close 本提交） |
| **B2.71** | DP README `## 次数（入场券）` 段标题 | 入场券→维护备忘 | **PASS · 勾销**（测 `62abf4b` · close 本提交） |
| **B2.72** | DP README L11「入场券条件」 | 入场券条件→维护备忘 | **PASS · 勾销**（测 `f092865` · close 本提交） |
| **B2.73** | mail `season_pass_stub` 占位文案 | title/body 占位→维护备忘 | **PASS · 勾销**（测 `e166783` · close 本提交） |
| **B2.74** | mail `vip_daily_gift` body | 勋阶正式累进前→维护备忘 | **PASS · 勾销**（测 `7b58d70` · close 本提交） |
| **B2.75** | mail `pass_track_free` body | claim→领取 | **PASS · 勾销**（测 `63019e7` · close 本提交） |
| **B2.76** | cash.yml 文件头 | 日票→体力/维护备忘 | **PASS · 勾销**（测 `ea56bb5` · close 本提交） |
| **B2.77** | progress.yml 战令注释 | 日票×5→遗留票物备忘 | **PASS · 勾销**（测 `fc171a2` · close 本提交） |
| **B2.78** | cash.yml free_tickets 注释 | 停发日票→遗留票物备忘 | **PASS · 勾销**（测 `e9a3cf0`/`095639e` · close 本提交） |
| **B2.79** | cash.yml elite 周票注释 | 日票池→遗留票物备忘 | **PASS · 勾销**（测 `a20689f`/`483f627` · close 本提交） |
| **B2.80** | schema 日票硬顶分区注释 | 日票硬顶→遗留票物备忘 | **PASS · 勾销**（测 `b289812`/`d46303c` · close 本提交） |
| **B2.81** | mail `event_box` body | 极简→维护备忘 | **PASS · 勾销**（测 `50ca64f` · close 本提交） |
| **B2.82** | cash.yml daily 段注释 | 日本/发票→遗留票物/体力口径维护备忘 | **PASS · 勾销**（测 `195033a` · close 本提交） |
| **B2.83** | schema dailyTicketsGranted 行内 | 今日已发票→遗留票物/体力口径维护备忘 | **PASS · 勾销**（测 `8799866` · close 本提交） |
| **B2.84** | cash.yml L8 停发票注释 | 停发票→遗留票物停发 | **PASS · 勾销**（测 `c5d483a`/`3a756a2` · close 本提交） |
| **B2.85** | cash.yml raid free_tickets 行内 | 周登录团本票→遗留票物备忘 | **PASS · 勾销**（测 `66ed681`/`1bbcdd0` · close 本提交） |
| **B2.86** | cash.yml weekly shop hard_cap 行内 | 免费1+氪→遗留票物备忘 | **PASS · 勾销**（测 `fc6d159`/`a116ef1` · close 本提交） |
| **B2.87** | progress.yml 等级门槛旁注 | 扣票→遗留票物/体力备忘 | **PASS · 勾销**（测 `89bbd57`/`b9c8f9e` · close 本提交） |
| **B2.88** | ember_shop 周体力包 lore | 硬顶免费1+氪→周硬顶 免费1+购买≤2 | **PASS · 勾销**（测 `e4b52a4` · close 本提交） |
| **B2.89** | ember_shop 文件头 | 原日票/周票→展示名+SKU 别名 | **PASS · 勾销**（测 `b04af5b` · close 本提交） |
| **B2.90** | cash weekly free_tickets | 无旁注→遗留票物/体力备忘 | **PASS · 勾销**（测 `fb06ed1` · close 本提交；不升 B2.91） |
| **B2.91** | cash abyss free_tickets | 无旁注→遗留票物/体力备忘 | **PASS · 勾销**（测 `96baff6` · close 本提交） |
| ~~**B2.92**~~ | CoreRpg config mysql.password | 真实样式密码→`"CHANGE_ME"` | **PASS · 勾销**（5 文件 · 测 `011594b`） |
| ~~**B2.93**~~ | cash elite free_tickets | 无旁注→遗留票物/体力备忘 | **PASS · 勾销**（测 `869fd38`） |
| ~~**B2.94**~~ | DP EmberWeekly option.yml | 扣票/B0.1 注释→体力口径维护备忘 | **PASS · 勾销**（测 `b6501af`） |
| ~~**B2.95**~~ | DP EmberAbyss option.yml | 扣票/B0.1 注释→体力口径维护备忘 | **PASS · 勾销**（测 `3c82e34`） |
| ~~**B2.96**~~ | DP EmberRaid option.yml | 扣票/B0.1 + L30 团票注释→体力口径维护备忘 | **PASS · 勾销**（测 `d62e20c`） |
| ~~**B2.97**~~ | DP EmberEliteWeekly option.yml | 扣票/B0.1/有票注释→体力口径维护备忘 | **PASS · 勾销**（测 `8e12ca3`） |
| **B2.98** | DP EmberWeekly option.yml L20 | 开本「已消耗体力 ×1」→ 去扣费字样 | **PASS · 勾销**（设计 `58103e9` · 批准 `baccdf4` · 插件 `94d8759` · 测 `b4cec8c`） |
| **B2.99** | DP EmberRaid option.yml L22 | 仅扣费提示一行 → 去扣费字样（删行须证不依赖行序） | **PASS · 勾销**（设计 `4007fb3` · 批准 `27633ca` · 插件 `b90e999` · 测 `16d74cb` · 取 (b)） |
| **B2.100** | DP EmberAbyss option.yml L22 | 开本「已消耗体力 ×1」→ 去扣费字样，保留下潜提示 | **PASS · 勾销**（设计 `0ef491e` · 批准 `2033207` · 插件 `252fe8e` · 测 `c3d662c`） |
| **B2.101** | DP EmberEliteWeekly option.yml L20 | 开本「本周只有一次」→ 每人每周通关 1 次口径 | **PASS · 勾销**（设计 `86d83c1` · 批准 `910d284` · 插件 `e55540a` · 测 `713b407`） |
| **B2.102** | TrMenu ember_hub.yml L249+L250 | 「每周 1 次」→ 每人每周限通关 1 次；L250 硬编码「消耗 40 体力」 | **PASS · 勾销**（设计 `4045245` · 批准 `6c2913a` · 插件 `862d21c` · 测 `0884f55`） |
| **B2.112** | DP EmberEliteWeekly/task/timeout.yml L6 | 失败提示「体力已扣，下周再来」与现行（失败同周可再进、首免/OP 不扣）矛盾 | **PASS · 勾销**（设计 `e47a7ed` · 批准 `ed3e16e` · 插件 `684ede2` · 测 `4adadc4`） |
| **B2.114** | plugins/CoreRpg/quest.yml L389 | 任务 hint「精英试炼（每周 1 次）」→ 限通关口径（src 模板同句可不排） | **PASS · 勾销**（设计 `d7cdcc7` · 批准 `f6531db` · 插件 `6817eb9` · 测 `13771d1`） |
| **B2.110** | TrMenu ember_raid.yml L76-77 | 进本按钮 click tell 恒报「消耗 50 体力」「团本已点燃」，与首免/OP/等级被拒私聊冲突 → 改「尝试进入」、不写扣费 | **PASS · 勾销**（设计 `4aff2ee` · 批准 `a63f4ac` · 插件 `22b1084` · 测 `e405013`；静态 PASS，实测发现方括号丢失，B2.122 已修） |
| **B2.111** | TrMenu ember_abyss.yml L77 | 进本 click tell 硬编码「体力 30」，OP 免扣冲突、不跟 cash.yml | **PASS · 勾销**（设计 `227bec9` · 批准 `4e0c11a` · 插件 `4cd3be3` · 测 `7ef8d00`；静态 PASS，实测发现方括号丢失，B2.122 已修） |
| ~~**B2.122**~~ | TrMenu menus/*.yml 全部 click `tell:`（25 菜单 91 行） | TabooLib `[文本](参数)` 组件语法吞掉 `[团本]`/`[深渊]` 等方括号与颜色（B2.110/B2.111 静态 PASS 未发现）→ `【..】` | **实测 PASS · 勾销**（`00cba0d` · 报告 `docs/TEST-B2.122-trmenu-brackets-live.md` · 2026-10-01 18:35 CST） |
| ~~**B2.113**~~ | TrMenu ember_hub.yml L215/L266/L280 | 周本/深渊/团本 lore 硬编码体力 45/30/50 → 占位符 | **实测 PASS · 勾销**（本提交 · 2026-10-01 19:32 CST 机器人 /ember 实测：周常「进本消耗 §e45 §8体力」、深渊「消耗 §e30」、团本「消耗 §e50」均由 %corerpg_stamina_cost_weekly/abyss/raid% 解析（papi parse 45/30/50），unreplacedPAPI=0） |
| ~~**B2.118**~~ | TrMenu ember_raid.yml L22/L109/L118 | 团本菜单写死「50」体力（口径准确）→ `%corerpg_stamina_cost_raid%` | **实测 PASS · 勾销**（本提交 · 2026-10-01 19:35 CST 机器人实测：打开团本 tell「【团本】 需 3～5 人 · 消耗 50 体力（本周首次免费）」、次数说明 lore「其后 50 体力」、点击 tell「其后 50 体力；需 3～5 人。」均由 %corerpg_stamina_cost_raid% 解析，unreplacedPAPI=0） |
| ~~**B2.120**~~ | TrMenu ember_abyss.yml L49/L54/L130/L139 | 深渊菜单写死「30」体力 → `%corerpg_stamina_cost_abyss%` | **实测 PASS · 勾销**（本提交 · 2026-10-01 19:38 CST 机器人实测：体力 10 时灰显 lore「需要 §e30 §7体力 · 当前 10/90」+ 点击 tell「体力不足，需 30 点体力」；体力满时次数说明 lore「消耗 §e30 §7体力（与日常同池）」+ 点击 tell「深渊消耗 30 体力」均由 %corerpg_stamina_cost_abyss% 解析；L41 condition 字面量 30 按注释保留） |
| **B2.121** | TrMenu ember_abyss.yml L132 | 「硬顶建议 ≤2/日（免费+购）」旧票制口径，深渊无免费次数 | 待出稿（排 B2.120 后） |
| **B2.119** | CoreRpg TicketEntryService 退还提示 | 发起者 40 tick 内下线不退不提示（:189）；5.5s 内重试退了不提示（:209）；周首免退还仍写「体力已退还」；DP 人数不足拒绝报「缓存冷却」误导 | 待出稿（需 build；排 B2.108 后） |
| ~~**B2.115**~~ | DP EmberWeekly/task/timeout.yml L6 | 「体力不返还」首免/OP 进本时不准 | **PASS · 勾销（DP 载入实测；超时触发未实测）**（`731ef8d` · L6 → 「§c周常超时失败。§7这次不算通关。」（去掉首免/OP 时不准的「体力不返还」，与 B2.112 精英口径一致、不写扣费）；2026-10-01 19:35 CST dp reload「[EmberWeekly] 地牢内容导入完毕」；超时实测未触发：bot 挂机周常 17 分钟时副本被判通关（weekly_clear），未到 1500s；同机制 Elite 720s 超时 20:00:51 实测包 {"color":"red","text":"试炼失败。"},{"color":"gray","text":"这次不算通关，本周还能再来。"} 渲染正常） |
| ~~**B2.116**~~ | DP EmberRaid/task/timeout.yml L7 | 「体力不返还」首免/OP 进本时不准 | **PASS · 勾销（DP 载入实测；超时触发未实测）**（本提交 · L7 → 「§c团本超时失败。§7这次不算通关。」（同 B2.115 口径）；2026-10-01 19:35 CST dp reload「[EmberRaid] 地牢内容导入完毕」；超时实测未触发：3 名无敌挂机 bot 在团本内 30 分钟（20:12）被判通关 raid_clear（并发了团戒），未到 2400s；同机制 Elite 超时实测见 B2.109） |
| ~~**B2.117**~~ | plugins/CoreRpg/quest.yml L377 | 主线 done「试炼每周一次」→「试炼每人每周限通关一次。稳定符在周首通里。」 | **实测 PASS · 勾销**（本提交 · 2026-10-01 19:43 CST 机器人实测（quest set 9 0 → /corerpg quest talk → 复原 1 0）：包 {"color":"gold","text":"灰烛："},{"color":"white","text":"试炼每人每周限通关一次。稳定符在周首通里。"}） |
| ~~**B2.105**~~ | plugins/CoreRpg/set.yml L27 | 注释「团票每周 1 张 ⇒ 每角色每周至多 1 次 COMPLETE」旧票制口径 → 体力进本、同周可多次通关、团戒靠周首通去重（仅注释） | **实测 PASS · 勾销**（本提交 · 2026-10-01 19:45 CST corerpg reload「SetService loaded: 余烬同袍 blades=4」+ 机器人 /corerpg set 输出正常） |
| ~~**B2.106**~~ | plugins/CoreRpg/cash.yml L86、L92 段首 | 注释：raid「菜单承诺每周团本票×1」→ 现行 0、体力进本首免；elite 段首「持有硬顶 1」→ hard_cap 现无代码消费（TicketGrantService 只读不用），键保留（仅注释，键值零改） | **实测 PASS · 勾销**（本提交 · 2026-10-01 19:47 CST corerpg reload 后机器人 /corerpg stamina「消耗 日常30 · 周45 · 精英40 · 深渊30 · 团50 · 本周免费抵扣 周本×1 · 精英×1 · 团×1」不变） |
| ~~**B2.109**~~ | mineflayer-tests | 补用例：menu-copy-check.js（hub 周常/深渊/团本体力 lore = 实时单价；团本打开/次数说明/开始协作 tell；深渊次数说明、开始下潜、低体力灰显 lore+tell，共 12 断言）+ timeout-live.js（周常/团本/精英超时实测，LINE=weekly|raid|elite）；旧脚本迁移见前序 6a1e1f9/a8dbdcb | **实测 PASS · 勾销**（本提交 · 2026-10-01 19:52 CST menu-copy-check PASS=12 FAIL=0；精英超时 20:00:51 实测包「试炼失败。这次不算通关，本周还能再来。」+「精英试炼超时失败」，718s） |
| ~~**B2.103**~~ | CoreRpgPlugin.cmdAbyss（/corerpg abyss 兜底） | 「日限 1 次 · 进本扣余烬深渊票」「背包深渊票 / 软计数 /1（以票为准）」「/dp start EmberAbyss」「无票时 /ni give…」票制旧文 → 「无尽波次下潜 · 进本消耗 {costOf(abyss)} 体力（与日常同池）」+ 当前体力/历史最深层 + 「进本：打开枢纽菜单 → 深渊」；CoreRpg 1.15.28→1.15.29 | **PASS · 勾销（构建/部署实测；兜底路径 live 不可达）**（本提交 · 2026-10-01 20:20 CST 构建并部署 1.15.29，启用日志「CoreRpg 1.15.29 enabled」；该兜底只在 AbyssSettleService 缺失时可达，live 常驻 settle 服务，无法用 bot 触发（静态核对）） |

---

## 3b. B-flex · 装备/操作灵活试点（用户拍板 · 优先于非文案内容窗）

| ID | 项 | 验收硬条（摘要） | 状态 |
|----|----|------------------|------|
| **B-flex-1** | 刃+护符之外 +1 **副手/饰品位**（NI 白名单 + 菜单展示 + 极简属性） | 静态 `rg` 试点 id / offhand 白名单 / Stat OffHand 钩子；TrMenu hub 或 set 目视「副手」说明；**禁** wall-clock / DPS 盲调；**禁**动 T0–T3 刃护符数值、体力日周门；**不做**四件甲/技能大改/锻炉产线重做 | **PASS · 勾销**（设计 `7dda194` · 批准 `eb3c843` · NI `52f817a` · 插件 `08b4cd4` · 测 `1453a7c` · close `d333b01`；报告 `docs/STATUS-ember-offhand-slot-pilot-test.md`；CoreRpg **1.15.24**） |
| **B-flex-2** | **可装配轻技 1 槽**（位移 **或** 保命择一；荐 A · 位移「余烬踏步」；TrMenu 点击；零体力） | 静态 `rg` 轻技 id / 1 槽字段；菜单装配+释放轻测；**禁** wall-clock / DPS；不叫挑刺；**禁**动誓约三主动数值、体力日周门、四件甲、锻炉大改、T0–T3 刃护符；**勿**位移+保命双上 | **PASS · 勾销**（设计 `d1e88e4` · 批准 `7d7bf5a` · 插件 `fc89225` · 补记 `3841f4f` · 测 `1c36873` · close 本提交；报告 `docs/STATUS-ember-flex-skill-slot-pilot-test.md`；CoreRpg **1.15.25**） |
| ~~**B-flex-3**~~ | 副手 **守腕/生坠薄获取口**（灰粮 stub 币购） | life offhand_ward/vita + ember_life O/W | **PASS · 勾销**（设计 `8a605f3` · 批准 `440e632` · 施工 `58baffe` · 测 `4a3b373` · close 本提交） |
| **B-flex-4** | 踏步少开菜单热键（挑刺 #2） | 潜行+Q → cancel+cast；菜单口保留；**勿**改 CD14/距离5 | **PASS · 勾销**（设计 `a9311ac` · 批准 `7b4a8b6` · 插件 `005a03b` · 测 `a1d5f7c` · close `6b0ce3d`；报告 `docs/STATUS-ember-flex-hotkey-sneak-drop-test.md`；CoreRpg **1.15.28**） |

**并行不捆：** **B2.17** 已 **PASS · 勾销**；文案薄窗暂缓；**勿捆 B2.x**；B-flex-1 已结（`d333b01`）；B-flex-2 已结（close 本提交）。

**本窗软挂（标「不捆」）：**
- ~~技能可装配~~ → 已升 **B-flex-2**（本表）
- ~~烬砧材料→部件产线~~ → **B-anvil-1 PASS · 勾销**（close 本提交）
- ~~副手薄获取~~ → **B-flex-3 PASS · 勾销**（close 本提交）
- ~~灰箍碎片抢口文案~~ → **B2.45 PASS · 勾销**（文案表）
- ~~踏步热键~~ → **B-flex-4 PASS · 勾销**（潜行+Q）
- ~~拆解管理注释~~ → **B2.46 PASS · 勾销**
- ~~拆解 Open tell「逻辑待 CoreRpg 接线」~~ → **B2.47 PASS · 勾销**（测 `6952749`）
- ~~公会 Open tell「逻辑待 CoreRpg 接线」~~ → **B2.48 PASS · 勾销**（测 `8d3f3cf`）
- ~~friends/mail 文件头「逻辑待 CoreRpg 接线」~~ → **B2.49 PASS · 勾销**（测 `4f52d81`）
- ~~arena/settings/set 文件头同族管理口吻~~ → **B2.50 PASS · 勾销**（测 `b84e948`）；menus「逻辑待」本轨归零
- ~~enhance/socket 文件头「跑 /corerpg …」~~ → **B2.51 PASS · 勾销**（测 `929206e`）
- ~~shop 文件头「/corerpg coin」~~ → **B2.52 PASS · 勾销**（测 `38eeecc`）
- ~~hub L188 AFK「/corerpg afk」~~ → **B2.53 PASS · 勾销**（测 `8c76d55`）；TrMenu 本段旁记教斜杠清零
- ~~NI `ember-disassemble.yml` L3「/corerpg scrap · reforge」~~ → **B2.54 PASS · 勾销**（测 `c642e4f`）
- ~~NI `ember-pets.yml` L1「待 CoreRpg」~~ → **B2.55 PASS · 勾销**（测 `7765927`）
- ~~TrMenu `ember_bestiary.yml` 阶段奖 tell「插件岗按 DESIGN 接线」~~ → **B2.56 PASS · 勾销**（测 `707f6f7`）
- ~~NI `ember-gear-t1.yml` L1–L2 stub/「插件岗」~~ → **B2.57 PASS · 勾销**（测 `01a41b2`）
- ~~NI `ember-gear-t1.yml` L35「团本饰品 stub」~~ → **B2.58 PASS · 勾销**（测 `162cae0`）；gear-t1 stub 本轨归零
- ~~TrMenu `menus/README-ember.md`「占位待改（插件岗）」~~ → **B2.59 PASS · 勾销**（测 `2ae758f`）；README 占位本轨归零
- ~~CoreRpg `cash.yml` / `covenant.yml` / `talent.yml` 文件头 `STUB：插件岗`~~ → **B2.60 PASS · 勾销**（测 `5abd67d`）；三文件头本轨归零
- ~~CoreRpg `players/_schema-example.yml` L16/L27「插件岗写入」~~ → **B2.61 PASS · 勾销**（测 `0098df7`）；schema 指派本轨归零
- ~~CoreRpg `cash.yml` L98/L104 勋阶 stub 注释~~ → **B2.62 PASS · 勾销**（测 `8a0aec7`）；cash 勋阶 stub 本轨归零
- ~~DP `EmberCalamity/monster.yml` L3「灾厄 Boss 战 stub」~~ → **B2.63 PASS · 勾销**（测 `8088429`）；monster stub 本轨归零
- ~~DP `README-ember-dungeons.md` L19/L20 表内 stub~~ → **B2.64 PASS · 勾销**（测 `2f517c4`）；README 表 stub 本轨归零
- ~~DP `README-ember-dungeons.md` L4 票/斜杠旧口径~~ → **B2.65 PASS · 勾销**（测 `1c099c5`）；L4 进本口径本轨归零
- ~~DP `README-ember-dungeons.md` 次数段「发放：插件/CoreRpg 岗」+ `/ni give`~~ → **B2.66 PASS · 勾销**（测 `a1e776f`）；发放备忘本轨归零
- ~~DP `README-ember-dungeons.md` 票表「日发/周发」规则列~~ → **B2.67 PASS · 勾销**（测 `1f2770f`）；票表规则本轨归零
- ~~DP `README-ember-dungeons.md` DEBT 段~~ → **B2.68 PASS · 勾销**（测 `e56ee7d`）；DEBT 标签本轨归零
- ~~DP `README-ember-dungeons.md` 次数段导语「入场卷」~~ → **B2.69 PASS · 勾销**（测 `a8dfda7`）；次数段导语本轨归零
- ~~DP `README-ember-dungeons.md` `## 已知占位`~~ → **B2.70 PASS · 勾销**（测 `71eb7cd`）；已知占位标题本轨归零
- ~~DP `README-ember-dungeons.md` `## 次数（入场券）`~~ → **B2.71 PASS · 勾销**（测 `62abf4b`）；次数段标题本轨归零
- ~~DP `README-ember-dungeons.md` L11「入场券条件」~~ → **B2.72 PASS · 勾销**（测 `f092865`）；DP README 票务口吻本轨归零
- ~~CoreRpg `mail.yml` `season_pass_stub` title/body「占位」~~ → **B2.73 PASS · 勾销**（测 `e166783`）；season_pass_stub 占位本轨归零
- ~~CoreRpg `mail.yml` `vip_daily_gift` body「勋阶正式累进前」~~ → **B2.74 PASS · 勾销**（测 `7b58d70`）；vip_daily_gift body 本轨归零
- ~~CoreRpg `mail.yml` `pass_track_free` body 英文 `claim`~~ → **B2.75 PASS · 勾销**（测 `63019e7`）；pass_track_free claim 本轨归零
- ~~CoreRpg `cash.yml` 文件头「日票」注释~~ → **B2.76 PASS · 勾销**（测 `ea56bb5`）；cash 文件头日票本轨归零
- ~~CoreRpg `progress.yml` 战令注释「付费轨累计日票 ×5」~~ → **B2.77 PASS · 勾销**（测 `fc171a2`）；progress 战令日票注释本轨归零
- ~~CoreRpg `cash.yml` `daily.free_tickets`「停发日票」注释~~ → **B2.78 PASS · 勾销**（测 `e9a3cf0`/`095639e`）；free_tickets 停发日票本轨归零
- ~~CoreRpg `cash.yml` Stage 4.4「不进商城日票池」注释~~ → **B2.79 PASS · 勾销**（测 `a20689f`/`483f627`）；cash elite 日票池注释本轨归零
- ~~CoreRpg `players/_schema-example.yml` 分区注释「日票硬顶」~~ → **B2.80 PASS · 勾销**（测 `b289812`/`d46303c`）；schema 日票硬顶注释本轨归零
- ~~CoreRpg `mail.yml` `event_box` body~~ → **B2.81 PASS · 勾销**（测 `50ca64f`）；mail event_box body 本轨归零
- ~~CoreRpg `cash.yml` `daily` 段注释「日本/发票」~~ → **B2.82 PASS · 勾销**（测 `195033a`）；cash daily 段注释本轨归零
- ~~CoreRpg `players/_schema-example.yml` `dailyTicketsGranted` 行内「今日已发票」~~ → **B2.83 PASS · 勾销**（测 `8799866`）；schema dailyTicketsGranted 本轨归零
- ~~CoreRpg `cash.yml` 约 L8「停发票」~~ → **B2.84 PASS · 勾销**（测 `c5d483a`/`3a756a2`）；cash 停发票注释本轨归零
- ~~CoreRpg `cash.yml` 约 L86 `raid.free_tickets` 行内~~ → **B2.85 PASS · 勾销**（测 `66ed681`/`1bbcdd0`）；cash raid free_tickets 注释本轨归零
- ~~CoreRpg `cash.yml` 约 L67 `shop.weekly_ticket.hard_cap` 行内~~ → **B2.86 PASS · 勾销**（测 `fc6d159`/`a116ef1`）；cash weekly shop hard_cap 注释本轨归零
- ~~CoreRpg `progress.yml` 约 L137 等级门槛旁注「扣票」~~ → **B2.87 PASS · 勾销**（测 `89bbd57`/`b9c8f9e`）；progress gate 扣票注释本轨归零
- ~~TrMenu `ember_shop.yml` 约 L151 周体力包 lore「硬顶免费1+氪」~~ → **B2.88 PASS · 勾销**（测 `e4b52a4`）；周体力包 lore 本轨归零
- ~~TrMenu `ember_shop.yml` 约 L2–3 文件头「原日票/周票」~~ → **B2.89 PASS · 勾销**（测 `b04af5b`）；shop 文件头本轨归零
- ~~CoreRpg `cash.yml` 约 L83 `weekly.free_tickets` 无旁注~~ → **B2.90 PASS · 勾销**（测 `fb06ed1`）；**本号交接停工 · 不升 B2.91**（见 `docs/HANDOFF-ember-grokbot-2026-09-30.md`）
- CoreRpg `cash.yml` L89 `abyss.free_tickets` 无旁注 → 已升 **B2.91 PASS · 勾销**（测 `96baff6`）（tip `docs/design-ember-cash-abyss-free-tickets-comment-copy.md`）
- CoreRpg `config.yml` L76 `mysql.password` 真实样式密码 → ~~B2.92~~ **PASS · 勾销**（同值 5 文件一并复位；git 历史未清，归用户轮换）
- CoreRpg `cash.yml` L94 `elite.free_tickets` 无旁注 → ~~B2.93~~ **PASS · 勾销**
- DP `dungeon/*/option.yml` 历史「扣票 / B0.1」注释 → ~~B2.94 Weekly~~ ~~B2.95 Abyss~~ ~~B2.96 Raid~~ ~~B2.97 EliteWeekly~~ **全部 PASS · 勾销**（本轨清零）

**明确未做/未清：** 四件甲 · 誓约主动大改 · 锻炉重做 · 位移+保命双上 · **勿宣称 B0.1 已清**

---

## 3c. B-anvil · 烬砧材料→部件（soft 升窗 · 非锻炉重做）

| ID | 项 | 验收硬条（摘要） | 状态 |
|----|----|------------------|------|
| **B-anvil-1** | 烬砧 **材料→部件** 一条短链（荐 A：`mat_ember_shard`×12 + `ingot_ember_iron`×2 → `part_ember_ash_brace`「余烬灰箍」· 副手物防 +1；TrMenu `ember_forge` 点击；复用 B-flex-1 offhand） | 静态 `rg` 部件 id / 配方 / offhand 白名单；菜单轻测炼成；**禁** wall-clock / DPS；不叫挑刺；**禁**动四件甲、`forge.yml` 升阶、强化表、誓约、体力日周门、B-flex 已结物；**勿**灰箍+骨饰双上 | **PASS · 勾销**（设计 `ab33c21` · 批准 `f10779b` · NI `57a6540` · 插件 `72281d9` · 测 `0ab09ef` · close 本提交；报告 `docs/STATUS-ember-anvil-mat-to-part-pilot-test.md`；CoreRpg **1.15.26**） |

**并行不捆：** 文案薄窗暂缓；**勿捆 B2.x**；B-flex-1/2 已结不回改。

**明确不做：** 四件甲 · 多部位锻炉大改 · 誓约技 · 体力门 · 宣称 B0.1 已清

---

## 4. 建议排期（额度内）


```
可维护性：  **B0.1 仍挂** / B0.3 TrMenu / B1.4 / B2.3 / B2.4 已结；挂机菜单 UX 勾销；**B2.8～B2.24 已结**；**B2.24 PASS · 勾销**（close `95783b3`）；**B2.25 PASS · 勾销**（close `35abc98`）；**B2.26 PASS · 勾销**（close `f796f3a`）；**B2.27 PASS · 勾销**（close `2aaffe3`）；**B2.28 PASS · 勾销**（close `edc0d9b`）；**B2.29 PASS · 勾销**（close `91adf76`）；**B2.30 PASS · 勾销**（设计 `3e7e1ba` · 批准 `a9f95e5` · 施工 `b9b1786` · 测 `2f2ba60` · close `72d37b3`）；**B2.31 PASS · 勾销**；**B2.32 PASS · 勾销**（live `&7mat_*` 本轨归零）；**B2.33 PASS · 勾销**；**B2.34 PASS · 勾销**；**B2.35 PASS · 勾销**；**B2.36 PASS · 勾销**；**B2.37 PASS · 勾销**；**B2.38 PASS · 勾销**；**B2.39 PASS · 勾销**；**B2.40 PASS · 勾销**；**B2.41 PASS · 勾销**；**B2.42 PASS · 勾销**；**B2.43 PASS · 勾销**；**B2.44 PASS · 勾销**；**B-flex-3 PASS · 勾销**；**B2.45 PASS · 勾销**（close 本提交）
测窗：      B0.2 ΔTTK · B1.3 精英关账 · kill-any live · 使徒校准 · 冷却 chat 均 PASS
地图窗：    P4 七线 + 节奏杠杆收口；P5 已有专稿
经济窗：    B0.4/B2.5 二档数值+菜单 UX 均 PASS 勾销
内容灵活窗：**B-flex-1** 副手 **PASS · 勾销**（close `d333b01`）；**B-flex-2** 轻技踏步 **PASS · 勾销**（设计 `d1e88e4` · 批准 `7d7bf5a` · 插件 `fc89225` · 测 `1c36873` · close `ba47f3e`；CoreRpg **1.15.25**）；**B-flex-3** 副手薄获取 **PASS · 勾销**（close 本提交）；**B2.45** 灰箍抢口文案 **PASS · 勾销**；**B-flex-4** 踏步热键 **PASS · 勾销**（close `6b0ce3d` · CoreRpg **1.15.28**）；**B2.46** 拆解管理注释 **PASS · 勾销**；**B2.47** 拆解 Open tell **PASS · 勾销**；**B2.48** 公会 Open tell **PASS · 勾销**；**B2.49** friends/mail 文件头 **PASS · 勾销**；**B2.50** arena/settings/set 文件头 **PASS · 勾销**；**B2.51** enhance/socket 文件头 **PASS · 勾销**；**B2.52** shop 文件头 **PASS · 勾销**；**B2.53** hub AFK 注释 **PASS · 勾销**；**B2.54** NI disassemble 文件头 **PASS · 勾销**；**B2.55** NI pets 文件头 **PASS · 勾销**；**B2.56** bestiary 阶段奖 tell **PASS · 勾销**；**B2.57** NI gear-t1 文件头 **PASS · 勾销**；**B2.58** NI gear-t1 L35 stub **PASS · 勾销**；**B2.59** TrMenu README 占位段 **PASS · 勾销**；**B2.60** CoreRpg 文件头维护备忘 **PASS · 勾销**；**B2.61** schema 分区注释 **PASS · 勾销**；**B2.62** cash 勋阶 stub 注释 **PASS · 勾销**；**B2.63** 灾厄 monster stub 注释 **PASS · 勾销**；**B2.64** DP README stub **PASS · 勾销**；**B2.65** DP README 进本口径 **PASS · 勾销**；**B2.66** DP README 发放备忘 **PASS · 勾销**；**B2.67** DP README 票表规则 **PASS · 勾销**；**B2.68** DP README DEBT 备忘 **PASS · 勾销**；**B2.69** DP README 次数段导语 **PASS · 勾销**；**B2.70** DP README 已知占位 **PASS · 勾销**；**B2.71** DP README 次数段标题 **PASS · 勾销**；**B2.72** DP README L11 入场券条件 **PASS · 勾销**；**B2.73** mail season_pass_stub 占位 **PASS · 勾销**；**B2.74** mail vip_daily_gift body **PASS · 勾销**；**B2.75** mail pass_track_free claim **PASS · 勾销**；**B2.76** cash.yml 文件头日票 **PASS · 勾销**；**B2.77** progress 战令日票注释 **PASS · 勾销**；**B2.78** cash free_tickets 注释 **PASS · 勾销**；**B2.79** cash elite 日票池注释 **PASS · 勾销**；**B2.80** schema 日票硬顶注释 **PASS · 勾销**；**B2.81** mail event_box body **PASS · 勾销**；**B2.82** cash daily 段注释 **PASS · 勾销**；**B2.83** schema dailyTicketsGranted **PASS · 勾销**；**B2.84** cash 停发票注释 **PASS · 勾销**；**B2.85** cash raid free_tickets **PASS · 勾销**；**B2.86** cash weekly shop hard_cap **PASS · 勾销**；**B2.87** progress gate 扣票 **PASS · 勾销**；**B2.88** shop 周硬顶 lore **PASS · 勾销**；**B2.89** shop 文件头 SKU **PASS · 勾销**；**B2.90** cash weekly free_tickets **PASS · 勾销**；**B2.91** cash abyss free_tickets **待批 A**
烬砧窗：    **B-anvil-1** 灰箍 **PASS · 勾销**（设计 `ab33c21` · 批准 `f10779b` · NI `57a6540` · 插件 `72281d9` · 测 `0ab09ef` · close 本提交；CoreRpg **1.15.26**）
文案窗：**B2.18**～**B2.24** **PASS · 勾销**（B2.24：`e004458` / `f004152` · close `95783b3`）；**B2.25 PASS · 勾销**（close `35abc98`）；**B2.26 PASS · 勾销**（close `f796f3a`）；**B2.27 PASS · 勾销**（close `2aaffe3`）；**B2.28 PASS · 勾销**（close `edc0d9b`）；**B2.29 PASS · 勾销**（设计 `bc58aaf` · 批准 `c87423c` · 施工 `9875d1b` · 测 `0420ef4` · close `91adf76`）；**B2.30 PASS · 勾销**（设计 `3e7e1ba` · 批准 `a9f95e5` · 施工 `b9b1786` · 测 `2f2ba60` · close `72d37b3`）；**B2.31 PASS · 勾销**；**B2.32 PASS · 勾销**（live `&7mat_*` 本轨归零）；**B2.33 PASS · 勾销**；**B2.34 PASS · 勾销**；**B2.35 PASS · 勾销**；**B2.36 PASS · 勾销**；**B2.37 PASS · 勾销**；**B2.38 PASS · 勾销**；**B2.39 PASS · 勾销**；**B2.40 PASS · 勾销**；**B2.41 PASS · 勾销**；**B2.42 PASS · 勾销**；**B2.43 PASS · 勾销**；**B2.44 PASS · 勾销**；**B-flex-3 PASS · 勾销**；**B2.45 PASS · 勾销**（close 本提交）
软挂升窗：  ~~烬砧材料→部件~~ → B-anvil-1 已结；~~套装旁附~~ → B2.19 已结；~~拆解菜单旁附~~ → B2.20 已结；~~CoreRpg 重铸缺料 tell~~ → B2.21 已结；~~NI 重铸石 lore 裸 id~~ → B2.22 已结；~~同物 lore 斜杠~~ → B2.23 已结；~~碎片 lore 裸 id~~ → B2.24 已结；~~骨尘 lore 裸 id~~ → B2.25 已结；~~核心碎片 lore 裸 id~~ → **B2.26 PASS · 勾销**（close `f796f3a`）；~~誓约重置券 lore 裸 id~~ → **B2.27 PASS · 勾销**（close `2aaffe3`）；**B2.28 PASS · 勾销**（close `edc0d9b`）；**B2.29 PASS · 勾销**（close `91adf76`）；**B2.30 PASS · 勾销**（设计 `3e7e1ba` · 批准 `a9f95e5` · 施工 `b9b1786` · 测 `2f2ba60` · close `72d37b3`）；**B2.31 PASS · 勾销**；**B2.32 PASS · 勾销**（live `&7mat_*` 本轨归零）；**B2.33 PASS · 勾销**；**B2.34 PASS · 勾销**；**B2.35 PASS · 勾销**；**B2.36 PASS · 勾销**；**B2.37 PASS · 勾销**；**B2.38 PASS · 勾销**；**B2.39 PASS · 勾销**；**B2.40 PASS · 勾销**；**B2.41 PASS · 勾销**；**B2.42 PASS · 勾销**；**B2.43 PASS · 勾销**；**B2.44 PASS · 勾销**；**B-flex-3 PASS · 勾销**；**B2.45 PASS · 勾销**（close 本提交）
软观察：    ~~B2.18–32 mat~~ → PASS；~~B2.33 锋利石~~ → PASS；~~B2.34 稳固石~~ → PASS；~~B2.35 汲取石~~ → PASS；~~B2.36 疾风石~~ → PASS（gem 本轨归零）；~~B2.37 cosmetic~~ → PASS（cosmetic 本轨归零）；~~B2.38 ashling~~ → PASS；~~B2.39 cinder~~ → PASS（pet 本轨归零）；~~B2.40 covenant_reset~~ → PASS；~~B2.41 talent_reset~~ → PASS；~~B2.42 ashling summon~~ → PASS；~~B2.43 cinder summon~~ → PASS；~~B2.44 soul_dust feed~~ → PASS（Items 玩家可见 `/corerpg` lore 本轨归零）；~~B-flex-3~~ → PASS；~~B2.45~~ → PASS；~~B-flex-4~~ → PASS（close `6b0ce3d`）；~~B2.46~~ → PASS；**B2.47** 拆解 Open tell **PASS · 勾销**；**B2.48** 公会 Open tell **PASS · 勾销**；**B2.49** friends/mail 文件头 **PASS · 勾销**；**B2.50** arena/settings/set 文件头 **PASS · 勾销**；**B2.51** enhance/socket 文件头 **PASS · 勾销**；**B2.52** shop 文件头 **PASS · 勾销**；**B2.53** hub AFK 注释 **PASS · 勾销**；**B2.54** NI disassemble 文件头 **PASS · 勾销**；**B2.55** NI pets 文件头 **PASS · 勾销**；**B2.56** bestiary 阶段奖 tell **PASS · 勾销**；**B2.57** NI gear-t1 文件头 **PASS · 勾销**；**B2.58** NI gear-t1 L35 stub **PASS · 勾销**；**B2.59** TrMenu README 占位段 **PASS · 勾销**；**B2.60** CoreRpg 文件头维护备忘 **PASS · 勾销**；**B2.61** schema 分区注释 **PASS · 勾销**；**B2.62** cash 勋阶 stub 注释 **PASS · 勾销**；**B2.63** 灾厄 monster stub 注释 **PASS · 勾销**；**B2.64** DP README stub **PASS · 勾销**；**B2.65** DP README 进本口径 **PASS · 勾销**；**B2.66** DP README 发放备忘 **PASS · 勾销**；**B2.67** DP README 票表规则 **PASS · 勾销**；**B2.68** DP README DEBT 备忘 **PASS · 勾销**；**B2.69** DP README 次数段导语 **PASS · 勾销**；**B2.70** DP README 已知占位 **PASS · 勾销**；**B2.71** DP README 次数段标题 **PASS · 勾销**；**B2.72** DP README L11 入场券条件 **PASS · 勾销**；**B2.73** mail season_pass_stub 占位 **PASS · 勾销**；**B2.74** mail vip_daily_gift body **PASS · 勾销**；**B2.75** mail pass_track_free claim **PASS · 勾销**；**B2.76** cash.yml 文件头日票 **PASS · 勾销**；**B2.77** progress 战令日票注释 **PASS · 勾销**；**B2.78** cash free_tickets 注释 **PASS · 勾销**；**B2.79** cash elite 日票池注释 **PASS · 勾销**；**B2.80** schema 日票硬顶注释 **PASS · 勾销**；**B2.81** mail event_box body **PASS · 勾销**；**B2.82** cash daily 段注释 **PASS · 勾销**；**B2.83** schema dailyTicketsGranted **PASS · 勾销**；**B2.84** cash 停发票注释 **PASS · 勾销**；**B2.85** cash raid free_tickets **PASS · 勾销**；**B2.86** cash weekly shop hard_cap **PASS · 勾销**；**B2.87** progress gate 扣票 **PASS · 勾销**；**B2.88** shop 周硬顶 lore **PASS · 勾销**；**B2.89** shop 文件头 SKU **PASS · 勾销**；**B2.90** cash weekly free_tickets **PASS · 勾销**；**B2.91** cash abyss free_tickets **待批 A** **勿宣称 B0.1 已清**
策划挑选排除：刚结 B2.6–B2.44 / B-flex-1 / B-flex-2 / B-flex-3 / B-anvil-1；刚结 B2.45；刚结 B-flex-4；刚结 B2.46；刚结 B2.91；刚结 B2.90；刚结 B2.89；刚结 B2.88；刚结 B2.87；刚结 B2.86；刚结 B2.85；刚结 B2.84；刚结 B2.83；刚结 B2.82；刚结 B2.50；刚结 B2.49；刚结 B2.48；刚结 B2.47；其它 NI 厚批 mat 双上 / 精英壳；五入口奖励预览真分页+NI 灰字（已结）；非日常体力灰显；挂机 over_chance*/二档 UX；B1.3；墙钟 2h；霜锈前压；断塔无证据升 B；B0.1 unless new evidence；精英预览壳若证据不足则勿硬开厚壳；四件甲/誓约主动大改/锻炉重做；位移+保命双上；灰箍+骨饰双上。
```

**不建议本额度新开：** 新养成线、新货币、新副本类型、Citizens、改 Paper/NMS。

---

## 5. 回总控摘要

- 文档：`docs/design-ember-content-backlog.md`  
- **B0 硬债：** B0.1 **仍挂**；B0.3～B0.4 / B2.5 数值+挂机菜单 UX 已结；进本冷却 chat **勾销**（1.15.21）；B0.1 无新证据不重开
- **B1/B2：** 工坊/地图/精英TTK/文案/测试钮/B2.6～B2.24 等已结；**B2.18～B2.24 PASS · 勾销**（B2.24：`e004458` / `f004152` · close `95783b3`）；**B2.25 PASS · 勾销**（close `35abc98`）；**B2.26 PASS · 勾销**（close `f796f3a`）；**B2.27 PASS · 勾销**（close `2aaffe3`）；**B2.28 PASS · 勾销**（close `edc0d9b`）；**B2.29 PASS · 勾销**（close `91adf76`）；**B2.30 PASS · 勾销**（设计 `3e7e1ba` · 批准 `a9f95e5` · 施工 `b9b1786` · 测 `2f2ba60` · close `72d37b3`）；**B2.31 PASS · 勾销**；**B2.32 PASS · 勾销**（live `&7mat_*` 本轨归零）；**B2.33 PASS · 勾销**；**B2.34 PASS · 勾销**；**B2.35 PASS · 勾销**；**B2.36 PASS · 勾销**；**B2.37 PASS · 勾销**；**B2.38 PASS · 勾销**；**B2.39 PASS · 勾销**；**B2.40 PASS · 勾销**；**B2.41 PASS · 勾销**；**B2.42 PASS · 勾销**；**B2.43 PASS · 勾销**；**B2.44 PASS · 勾销**；**B-flex-3 PASS · 勾销**；**B2.45 PASS · 勾销**（close 本提交）
- **内容灵活窗：** **B-flex-1** 副手 **PASS · 勾销**（close `d333b01`）；**B-flex-2** 轻技踏步 **PASS · 勾销**（close `ba47f3e`；CoreRpg **1.15.25**）；**B-flex-3** 副手薄获取 **PASS · 勾销**（close 本提交）；**B2.45** 灰箍抢口文案 **PASS · 勾销**；**B-flex-4** 踏步热键 **PASS · 勾销**（close `6b0ce3d` · CoreRpg **1.15.28**）；**B2.46** 拆解管理注释 **PASS · 勾销**；**B2.47** 拆解 Open tell **PASS · 勾销**；**B2.48** 公会 Open tell **PASS · 勾销**；**B2.49** friends/mail 文件头 **PASS · 勾销**；**B2.50** arena/settings/set 文件头 **PASS · 勾销**；**B2.51** enhance/socket 文件头 **PASS · 勾销**；**B2.52** shop 文件头 **PASS · 勾销**；**B2.53** hub AFK 注释 **PASS · 勾销**；**B2.54** NI disassemble 文件头 **PASS · 勾销**；**B2.55** NI pets 文件头 **PASS · 勾销**；**B2.56** bestiary 阶段奖 tell **PASS · 勾销**；**B2.57** NI gear-t1 文件头 **PASS · 勾销**；**B2.58** NI gear-t1 L35 stub **PASS · 勾销**；**B2.59** TrMenu README 占位段 **PASS · 勾销**；**B2.60** CoreRpg 文件头维护备忘 **PASS · 勾销**；**B2.61** schema 分区注释 **PASS · 勾销**；**B2.62** cash 勋阶 stub 注释 **PASS · 勾销**；**B2.63** 灾厄 monster stub 注释 **PASS · 勾销**；**B2.64** DP README stub **PASS · 勾销**；**B2.65** DP README 进本口径 **PASS · 勾销**；**B2.66** DP README 发放备忘 **PASS · 勾销**；**B2.67** DP README 票表规则 **PASS · 勾销**；**B2.68** DP README DEBT 备忘 **PASS · 勾销**；**B2.69** DP README 次数段导语 **PASS · 勾销**；**B2.70** DP README 已知占位 **PASS · 勾销**；**B2.71** DP README 次数段标题 **PASS · 勾销**；**B2.72** DP README L11 入场券条件 **PASS · 勾销**；**B2.73** mail season_pass_stub 占位 **PASS · 勾销**；**B2.74** mail vip_daily_gift body **PASS · 勾销**；**B2.75** mail pass_track_free claim **PASS · 勾销**；**B2.76** cash.yml 文件头日票 **PASS · 勾销**；**B2.77** progress 战令日票注释 **PASS · 勾销**；**B2.78** cash free_tickets 注释 **PASS · 勾销**；**B2.79** cash elite 日票池注释 **PASS · 勾销**；**B2.80** schema 日票硬顶注释 **PASS · 勾销**；**B2.81** mail event_box body **PASS · 勾销**；**B2.82** cash daily 段注释 **PASS · 勾销**；**B2.83** schema dailyTicketsGranted **PASS · 勾销**；**B2.84** cash 停发票注释 **PASS · 勾销**；**B2.85** cash raid free_tickets **PASS · 勾销**；**B2.86** cash weekly shop hard_cap **PASS · 勾销**；**B2.87** progress gate 扣票 **PASS · 勾销**；**B2.88** shop 周硬顶 lore **PASS · 勾销**；**B2.89** shop 文件头 SKU **PASS · 勾销**；**B2.90** cash weekly free_tickets **PASS · 勾销**；**B2.91** cash abyss free_tickets **待批 A**
- **烬砧窗：** **B-anvil-1** 灰箍 **PASS · 勾销**（close `e39172f`；CoreRpg **1.15.26**）；灵活三窗已结
- **文案窗：** **B2.18**～**B2.25** **PASS · 勾销**（B2.25 close `35abc98`）；**B2.26 PASS · 勾销**（close `f796f3a`）；**B2.27 PASS · 勾销**（close `2aaffe3`）；**B2.28 PASS · 勾销**（close `edc0d9b`）；**B2.29 PASS · 勾销**（设计 `bc58aaf` · 批准 `c87423c` · 施工 `9875d1b` · 测 `0420ef4` · close `91adf76`）；**B2.30 PASS · 勾销**（设计 `3e7e1ba` · 批准 `a9f95e5` · 施工 `b9b1786` · 测 `2f2ba60` · close `72d37b3`）；**B2.31 PASS · 勾销**；**B2.32 PASS · 勾销**（live `&7mat_*` 本轨归零）；**B2.33 PASS · 勾销**；**B2.34 PASS · 勾销**；**B2.35 PASS · 勾销**；**B2.36 PASS · 勾销**；**B2.37 PASS · 勾销**；**B2.38 PASS · 勾销**；**B2.39 PASS · 勾销**；**B2.40 PASS · 勾销**；**B2.41 PASS · 勾销**；**B2.42 PASS · 勾销**；**B2.43 PASS · 勾销**；**B2.44 PASS · 勾销**；**B-flex-3 PASS · 勾销**；**B2.45 PASS · 勾销**（close 本提交）
- 软观察：~~B2.18–32 mat~~ → PASS；~~B2.33 锋利石~~ → PASS；~~B2.34 稳固石~~ → PASS；~~B2.35 汲取石~~ → PASS；~~B2.36 疾风石~~ → PASS（gem 本轨归零）；~~B2.37 cosmetic~~ → PASS（cosmetic 本轨归零）；~~B2.38 ashling~~ → PASS；~~B2.39 cinder~~ → PASS（pet 本轨归零）；~~B2.40 covenant_reset~~ → PASS；~~B2.41 talent_reset~~ → PASS；~~B2.42 ashling summon~~ → PASS；~~B2.43 cinder summon~~ → PASS；~~B2.44 soul_dust feed~~ → PASS（Items 玩家可见 `/corerpg` lore 本轨归零）；~~B-flex-3~~ → PASS；~~B2.45~~ → PASS；~~B-flex-4~~ → PASS（close `6b0ce3d`）；~~B2.46~~ → PASS；**B2.47** 拆解 Open tell **PASS · 勾销**；**B2.48** 公会 Open tell **PASS · 勾销**；**B2.49** friends/mail 文件头 **PASS · 勾销**；**B2.50** arena/settings/set 文件头 **PASS · 勾销**；**B2.51** enhance/socket 文件头 **PASS · 勾销**；**B2.52** shop 文件头 **PASS · 勾销**；**B2.53** hub AFK 注释 **PASS · 勾销**；**B2.54** NI disassemble 文件头 **PASS · 勾销**；**B2.55** NI pets 文件头 **PASS · 勾销**；**B2.56** bestiary 阶段奖 tell **PASS · 勾销**；**B2.57** NI gear-t1 文件头 **PASS · 勾销**；**B2.58** NI gear-t1 L35 stub **PASS · 勾销**；**B2.59** TrMenu README 占位段 **PASS · 勾销**；**B2.60** CoreRpg 文件头维护备忘 **PASS · 勾销**；**B2.61** schema 分区注释 **PASS · 勾销**；**B2.62** cash 勋阶 stub 注释 **PASS · 勾销**；**B2.63** 灾厄 monster stub 注释 **PASS · 勾销**；**B2.64** DP README stub **PASS · 勾销**；**B2.65** DP README 进本口径 **PASS · 勾销**；**B2.66** DP README 发放备忘 **PASS · 勾销**；**B2.67** DP README 票表规则 **PASS · 勾销**；**B2.68** DP README DEBT 备忘 **PASS · 勾销**；**B2.69** DP README 次数段导语 **PASS · 勾销**；**B2.70** DP README 已知占位 **PASS · 勾销**；**B2.71** DP README 次数段标题 **PASS · 勾销**；**B2.72** DP README L11 入场券条件 **PASS · 勾销**；**B2.73** mail season_pass_stub 占位 **PASS · 勾销**；**B2.74** mail vip_daily_gift body **PASS · 勾销**；**B2.75** mail pass_track_free claim **PASS · 勾销**；**B2.76** cash.yml 文件头日票 **PASS · 勾销**；**B2.77** progress 战令日票注释 **PASS · 勾销**；**B2.78** cash free_tickets 注释 **PASS · 勾销**；**B2.79** cash elite 日票池注释 **PASS · 勾销**；**B2.80** schema 日票硬顶注释 **PASS · 勾销**；**B2.81** mail event_box body **PASS · 勾销**；**B2.82** cash daily 段注释 **PASS · 勾销**；**B2.83** schema dailyTicketsGranted **PASS · 勾销**；**B2.84** cash 停发票注释 **PASS · 勾销**；**B2.85** cash raid free_tickets **PASS · 勾销**；**B2.86** cash weekly shop hard_cap **PASS · 勾销**；**B2.87** progress gate 扣票 **PASS · 勾销**；**B2.88** shop 周硬顶 lore **PASS · 勾销**；**B2.89** shop 文件头 SKU **PASS · 勾销**；**B2.90** cash weekly free_tickets **PASS · 勾销**；**B2.91** cash abyss free_tickets **待批 A** **勿宣称 B0.1 已清**

