# 余烬服 · 交接（2026-10-03 08:15 CST 更新）

这份文档写给接手的人，读完就能接着做。设计正文在 `docs/design-ember-v1.0-P1.md`（下文称「书」）。P2 草案在 `docs/design-ember-v1.1-P2-draft.md`。所有裁决记在源表 `docs/DESIGN-ember-v1.0-P1-source-table.md`（§13.x，D01–D134）。

## 1 当前状态

| 项 | 值 |
|---|---|
| 仓库 | `Picadoo/ember-server`，main 分支。工作树 `/workspace/ember-p1`（分支 p1-g01，推到 main）。服务器在 `/workspace/minecraft` 跑，这是 main 的检出 |
| CoreRpg | **1.54.0**（`CoreRpg/pom.xml` 第 8 行和 `plugin.yml` `version:` 一起改） |
| balance_version | 19（`ember-v1-runs.yml`，规则 g04-1/b19；19 = D134 毕业那周的周目标「委托 3 天」按剩下的天数算（周六 2、周日 1，`weekly_goals:` 上的注释，代码 `EmberSeason.target`）；18 = D128 每天第一次挑战 / 深渊失败退一半体力 `fail_refund: 0.5`；17 = D123 赛季深潜者改第 5 层 `season.deep_tier: 5`、D124 深渊层费可用多余 T3 印记抵 `abyss.fee_mark_coin: 200`、D122 Q04 `rails` + `fall_catch_y`；16 = D116 赛季 `season:`、D117 周目标 `weekly_goals:`、D118 团本最后阶段复活 `raid_revive:`（首领 ≤20% 且有人倒下，20 秒后复活一次）；15 = D109 深渊第 1 层 = 挑战版、线性到第 10 层（不变）、成色跟强度、第 8～10 层费 400/440/480；D110 Q02 首通 = 一次免费定向兑换（自选族 + 部位 T1）；14 = D108 精选图首通后重打普通版 +1 枚该图阶印记，与挑战版精选加成共用 3 次/周；13 = D104 挑战版减压 + 深渊层表换算 + T3 升阶减半 + Q05 首通加币） |
| 模式 | P1 是默认模式（D61–D65），旧玩法藏在 `ember_hub_legacy`。**不要关 P1** |
| 菜单 | TrMenu 共 50 个，`trmenu reload` 就能重载 |
| 套件 | `mineflayer-tests` gameplay 9/9（1.54.0；钓鱼偶尔随机失败，重跑即过） |
| 玩家 | 没有老玩家，**不做数据迁移**。测试号 P1Fox、RaidA–E、NewbieQ、FreshA1–N1、FreshO1–O9、FreshP1–P6、FreshQ1–Q26（全新号；下次从 FreshQ27 起；FreshQ17 是复查人的号；FreshQ18/Q19 兑换过 T1/T2 焚烬刃（D130/D131 检查），FreshQ20 是管理员写的 Q01–Q07 首通，FreshQ21–Q26 打过 R01 全倒测试（Q24–Q26 开战后全倒，周次数 0/3）；FreshQ10–Q12、Q14–Q16 打过 R01 倒下测试，FreshQ13 当天两次挑战失败；FreshQ8 真打通关过 Q04、买过素白，FreshQ9 是管理员写的 Q01 + Q07 首通；FreshQ4 有赛季奖励 season_deep / season_crown 和余烬徽，是管理员发的测试数据）、RevMidA/B（点评用）。RevNewA 的游戏数据已删（D100），AuthMe 账号还在。**测试号不上榜**：`ember-v1.yml` 的 `leaderboard_exclude`（名字 / 前缀 / 正则，D102）挡住排行榜、主城悬浮字和荣誉陈列，加新机器人名字时记得补进去 |
| 内容 | 七张主线图 Q01–Q07 + 挑战版。P2-1 每周挑战轮换。P2-2 深渊·余烬层（10 层）。P2-5/6 团本 R01 锈轨矿道·团 + R02 霜封哨所·团（3～5 人，两本合计每周 3 次）。P2-7 每日委托。P2-8 精选图周规则（限药 / 术者换防 / 逆行；D94 起限药、逆行也用于重打已首通的普通版）。P2-9 每图掉落偏向、团本额外装备按目标族定向（保底精良）、称号和团本足迹（只做展示）。P2-10 排行榜（`/corerpg p1 top`）。新手提示：回复药放快捷栏、生命低提醒、「下一步」（1.34.1–1.36.1）。新手第一周（1.39.x，D85–D87）：护符自动生效、T1 自动顶替起步件、Q01 普通版减压、首通自选 / 目标族改成可点按钮；1.40.x（D88–D92）：印记兑换按钮、房间敌人提示、结算物品不占快捷栏、踏步默认装配、好友页组队打开队伍面板；1.41.x（D94–D95）：普通版周规则、锻造 / 补领 / 荣誉改成按钮；1.42.0（D96）：第一周复查，Q02 缺 T1 件时提醒、锻造缺料给来源；1.43.0（D97）：枢纽氛围（闲话、指路牌、粒子、排行榜、荣誉陈列，只做展示）；1.44.0–1.46.0（D98–D100，第一周点评整改）：Q01 首通自选护符、Q02 首通自选刃（balance_version 12），Q02 提醒带 [回 Q01]；旧系统移出主菜单、Q07 前内容合成一个图标、术语和公式清理、进服消息去重；好友 / 组队全按钮、DP 队伍上限 5；锻造确认与预览同列、T0 黄字；1.47.0–1.48.1（D101–D105，中后期点评整改）：主手 / 攻击缓存改成事件驱动刷新（D101）、测试号不上榜 + 主菜单「荣誉与排行」+ 盟约移出（D102）、里程碑称号（D103）、挑战版减压与深渊换算 + T3 升阶减半 + 两条换阶路互相标注（D104）、团本算 2 局委托 + 团本招募按钮（D105）；1.49.0（D106–D108）：团本倒下观战队友、下一个房间开打 / 首领现身 / 首领转阶段自动复活 50% 生命（D106）、外观商店（称号颜色 / 朴素足迹 / 名牌标记，2000～20000 币，只做展示，D107）、精选图重打普通版 +1 枚 T1/T2 印记（D108）；1.50.0（D109–D115，后期复查）；1.54.0（D130–D134，快速复查整改）：起步刃不再挡住新刃自动换上、起步刃真的进背包、背包里更好的同阶件也会换上 / 「下一次突破」给 [换上]、团本失败说明周次数没扣、进服消息加 [外观商店]、毕业那周委托目标按剩下天数；1.53.0（D128–D129）：每天第一次挑战 / 深渊失败退一半体力（不给掉落和币），刷怪点检查脚本不再误报门；1.52.x（D120–D127，全流程验收整改）：更好的件自动换上 + 「下一次突破」给最省路线、外观商店一个来源 + 付款页、一套赛季 / 排行 / 商店页面、Q04 封口和掉落传回、深渊榜按用时破并列 + 深潜者第 5 层、深渊层费可用 T3 印记抵、团本倒下提示、聊天去世界名 + 不广播成就、旧物品名重写；1.51.0（D116–D119）：4 周赛季 + 本周 / 赛季榜（深渊最高层、精选挑战、团本通关、R01/R02 最快通关）和季末赛季称号 / ❖ 名牌框（只做展示，归档 `p1-runs/season-archive/`），首通 Q07 后的周目标（4 个，奖励余烬徽，只能买外观），团本最后阶段多一次复活，外观商店菜单页 |

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
- **每次部署**：
  1. 升版本号，备份 jar 到 `/workspace/backup/CoreRpg-<ver>.jar`。
  2. 冒烟，再跑套件 `cd mineflayer-tests && timeout 600 npm run gameplay`，结果看 `PASS=`。
  3. 跑 `scripts/db-dump.sh`，把最新的 `…-manual.sql` 改名为 `…-after-<ver>.sql`。
  4. 套件会改写 live 里的 `STATUS-gameplay-suite.md`：复制到工作树提交，然后在 live 里 `git checkout --` 还原。

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
| 副本实例 | `plugins/DungeonPlus/dungeon/EmberQ01..Q07`、`EmberQ0R1` / `EmberQ0R2`（团本；D106 起 `revive=true;number=0`，复活只由 CoreRpg 用 `dp revive <玩家> true true` 触发）；`config.yml` 里的 precache | DP 队伍：`/dp team` GUI，接受用 `/dungeon-team request accept <名>`；离队 `/dungeon-team quit`，队长解散 `/dungeon-team disband`（**没有** `leave`） |
| 菜单 | `plugins/TrMenu/menus/ember_hub.yml`（主菜单）、`ember_p1_adventure` / `_challenge` / `_abyss` / `_codex` / `_codex_gear` / `_forge` / `_gear` / `_season`（赛季 · 排行 · 周目标，唯一的称号 / 排行页，D116/D121）/ `_shop`（外观商店页，唯一的商店，D119/D121）/ `_shop_buy`（付款页，D121） | 进本一律走 `/corerpg p1 enter <key>`；`/corerpg enter raid` 是旧团本 |
| PAPI | `CoreRpgExpansion`：`p1_*`（`p1_pass_<key>`、`p1_raid_r01/r02`、`p1_abyss_*`、`p1_bounty`、`p1_next`、`p1_modifier`、`p1_loot_<图>`、`p1_title`、`p1_honors`、`p1_season`、`p1_goals`、`p1_goal_<g>`、`p1_badges`、`p1_season_last`、`p1_sboard_<w|s>_<榜>_<i>`、`p1_srank_<w|s>_<榜>`、`p1_shop_<id>`、`p1_shopprice_<id>` / `shopname_` / `shophow_`、`p1_shopmarks`、`p1_shopsel_*`（付款页）、`p1_awaken_route` …） | 菜单和 DP 进入条件都用它 |

## 4 工具

| 工具 | 用途 |
|---|---|
| `tools/p1sim/p1sim.py`、`selfcheck.py` | 单人从 Q01 推到 Q07 的节奏模型，读真实配置；`--normal-mods` 开普通版周规则（D94）；`--no-feat-normal` 关掉精选普通版印记、`--feat-farm` 专门刷精选图（D108）。selfcheck 应该 0 failed |
| `tools/p1sim/p2econ.py` | 60 人 8 周经济模型，开关 `--abyss` / `--trade` / `--raid` / `--mods` / `--no-loot` / `--old-raid-item` / `--no-swap` / `--no-bounty` / `--normal-mods` / `--every-week` / `--ch-hp` / `--ch-atk`（D104）/ `--abyss-tiers` / `--abyss-quality` / `--abyss-fees`（E-review 试调深渊层表；输出带第 12 周层数分布和卡币天数）/ `--no-feat-normal` / `--feat-farm` / `--feat-separate`（D108）/ `--raid-rate b12|b13|revive`（团本通关率表，默认 revive，D106）/ `--goals`（D116 周目标两列：团本 + 周目标、深渊 + 周目标）/ `--fee-mark N`（D124 多余 T3 印记抵层费的币值，0 = 关，默认读 yml）/ `--fail-refund X`（D128 每天第一次挑战 / 深渊失败退的体力比例，0 = 关，默认读 yml）。看两件极品的提速要用 600 人以上、看各列最快路线（单列噪声约 ±1 周）。**凡是发奖励的改动都要先跑这个** |
| `tools/p1sim/chrate.py` | 首通 Q07 那一刻的挑战版通关率（刚首通 / 免费互换后 / 刃 T3 / 两件 T3），`--scale-hp` / `--scale-atk` 试调（D104） |
| `tools/p1sim/p1party.py` | 3～5 人团本模型，`--raid r01|r02`。参数 `--pool --weeks --dodge --trials --boss-hp --atk --k --j`；`--no-revive` 关掉 D106 复活；`--last-revive-hp` / `--last-revive-delay`（D118 最后阶段复活，默认读 yml `raid_revive:`） |
| `tools/p1map/book.py`、`gen.py` | 解析书里的地图章节，生成地图 |
| `tools/p1map/chal-smoke.sh`、`abyss-smoke.sh`、`raid-smoke.sh LEADER "成员…" [q07|q06]`、`boss-test.sh LEADER "成员…" QMAP [比例]`（首领不 weaken 或只削到比例，看阶段和击杀用时） | 机器人冒烟：按书里的路线走，每个房间先 admin weaken，再由机器人击杀，结算是真的 |
| `tools/p1map/norm-smoke.sh NAME qNN`（普通版，每个房间削弱）、`newbie-run.sh NAME qNN`（不削弱、生存模式、喝药，死了就停）；`fight.sh` 加环境变量 `DRINK=1`，生命低于 11 时喝快捷栏的药 | 新手视角冒烟 |
| `scripts/check-dp-spawns.py`、`dp_fix_spawns.py`、`dp_map_reach.py` | 检查 DP 刷怪点（卡墙、走不到）。用法 `DP_MAP_ROOT=/workspace/minecraft/plugins/DungeonPlus/map python3 scripts/check-dp-spawns.py /workspace/minecraft/plugins/DungeonPlus/dungeon/EmberQ0*`，应该 9 个都是 ok。D129 起门格只要是空气或铁栏就算对（模板存档时门开着，director attach 时把空气 / 铁栏变成铁栏），别的方块才报错；团本 r01 / r02 按自己的块检查，不再混进 q07 |
| `scripts/db-dump.sh` | MySQL 备份到 `/workspace/backup/` |
| `tools/p1map/starter-equip-check.sh FRESHBOT` | D130/D131 实机检查：兑换 T1 刃看起步刃进背包，再把起步刃放回快捷栏、兑换 T2 刃看是否自动到手上（打印 PASS/FAIL，要新号） |
| `mineflayer-tests/tmp-p1/b.sh` | 机器人：join / quit / eval / chat。另有 `walk.sh`、`fight.sh` |

`tools/*` 被 gitignore 了，新文件要 `git add -f`。

## 5 备份和发布

- jar 备份在 `/workspace/backup/CoreRpg-<ver>.jar`，最新是 1.54.0（还有 1.53.0、1.52.1、1.52.0、1.51.0、1.50.0、1.49.0、 1.48.1、1.48.0、1.47.0）。
- 数据库备份是 `/workspace/backup/db-ember-authme-*-after-<ver>.sql`，最新是 after-1.54.0（还有 after-1.53.0、after-1.52.1、after-1.52.0、after-1.51.0、after-1.50.0、after-1.49.0、 after-1.48.1、after-1.48.0、after-1.47.0、after-1.46.0、after-1.45.0、after-1.44.0、after-1.43.0、after-1.42.0，删 RevNewA 前的 pre-revnewa-delete；1.41.1 只在线 2 分钟，没有单独备份）。
- 发布就是推到 main，然后在 live 里 `git pull --rebase --autostash`。没有单独的 release 产物。

## 6 规则（必须遵守）

1. **数值纪律（书 §23.3）**：改数先改参数源（两份），再重跑模型，登记到源表，然后升 balance_version 和插件版本。不能自行增加奖励。
2. **绝不提交**：CoreRpg `config.yml`、AuthMe 配置（含数据库密码，不能打印）、`ladder.yml`、`calamity-state.yml`、`players/**`、`worlds.yml`、jar、世界、日志、`tmp-p1/`、Adyeshach npc json、`ember-v1-item.key`、`login-runtime/*`、HolographicDisplays `database.yml`、MythicMobs SavedData、`plugins/CoreRpg/p1-runs/`（含 leaderboard.yml、season.yml、season-archive/）。
3. `server-runtime/ops.json` 保持 `[]`。
4. **只按确切 PID 杀进程**，绝不用 `pkill -f` 或 `pgrep -f`。
5. 游戏内的击杀命令只在副本里用，而且要限定半径。机器人死了就停掉 fight.sh 或让它下线。
6. 提交署名 `Picadoo <Picadoo@users.noreply.github.com>`。推送用 `git pull --rebase --autostash -q && git push -q origin HEAD:main`。提交小、推送勤。

## 7 未完成

- **spigot 生命上限（1.36.3 已修）**：`spigot.yml` 的 `maxHealth.max` 原来是 20000，团本首领（3 人 35100）和高层深渊首领都被悄悄截断，所以之前团本通关比模型简单。现在是 1000000，scaleHealth 截断时会打警告。
- **团本 TTK**：书 §18.4 第 4 条要求模型和实测差不超过 20%。已经实测：
  - R02 第二阶段实机出现（首领削到 60%，日志 `boss phase 2 at 50% (砸地)`）。
  - 单个机器人对首领的 DPS 185，模型 190（−3%）。
  - 完整 3 人击杀用时还没测成：机器人不躲技能，1 秒一次治疗也扛不住，R01 削到 40% 时团灭了。要真人或会躲的机器人。
- **后期币堆积**：深渊层费是目前唯一的大额出口（D72）。要发币的新内容必须先过 p2econ。D107 外观商店是只做展示的花币出口（全套 7.8 万），入口在**装备页第 43 格**（不是 44），D121 起第 43 格、进服消息（毕业玩家的 [外观商店] 按钮，1.54.0 才补上按钮）、赛季页都打开同一个商店页 `ember_p1_shop`（付款在 `ember_p1_shop_buy`），价格和说法只在 EmberCosmetics；D112 起也能用多余印记付（T1/T2/T3 = 1/2/4 点，1 点 = 50 币，每阶留 8 枚），另有印记专属的主城刃辉光和称号动效。
- **全流程验收（`docs/review-fullpath-2026-10-03.md`）**：D120–D127 已做（P2 草案 §5s）。没改的：
  - 后期 #6 在 1.53.0 做了（D128）：每天第一次挑战 / 深渊失败退一半体力，两者共用一次（P2 草案 §5t）。
  - D125 团本倒下提示 1.53.0 实机复测过（R01，§5t）。全员倒下后 DP 的结束字幕「本局失败，即将返回」这轮没截到（只截到全员离开的那条）。
  - 旧的仅管理员可见菜单（`ember_hub_legacy` 一系）没动。
- **快速复查（`docs/review-recheck-2026-10-03.md`）**：全部处理（D130–D134，P2 草案 §5u）。没改的：
  - 深渊卡币（部分 #5）没调数，原因和模型结果见 §5u：卡币时手里的 T3 印记都在每阶保留的 8 枚以内，`fee_mark_coin` 调多少都用不上。
  - 小出入：从装备页第 43 格打开外观商店后，商店的「返回」回到赛季页，不是装备页。
  - D134 毕业周按比例只有单测，没有实打一次周六的 Q07 首通。
- **后期复查（`docs/review-endgame-2026-10-03.md`）**：D109–D115 已做（P2 草案 §5q）；原来留着的最后阶段复活、商店页、赛季榜和周目标在 1.51.0 做了（D116–D119，§5r）。旧物品名重写在 D127 做了（进服重写显示）。
- **赛季（D116）**：第 1 赛季 2026-09-28～10-25，10-26 0 点后第一分钟自动结算（日志 `[P1 season] S1 settled`、全服广播、`season-archive/S1.yml`）。目前榜上只有测试号会打本，全都被 `leaderboard_exclude` 挡掉，所以 S1 很可能是空结算；真实玩家第一次上榜时看一眼 `/corerpg p1 season`。`runs season preview` 可以随时预览。
- **留给策划（中后期点评）**：团本体力 60 的备选（D105 用了「算 2 局」）。团本倒下复活（D106）、花币出口（D107）、精选给中期奖励（D108）已做。
- **D106 已知限制**：团本倒下后 `/dp leave` 10 秒内输两次仍然能走（算放弃，不复活不结算）；断线走 DP 的离线保护。复活点是「下一个房间开打 / 首领现身 / 首领转阶段」，D118 起首领所有转阶段之后降到 20% 以下、有人倒下时，20 秒后再复活一次（每局一次）。
- **交易**：D73 否决开市，只有规则。
- 旧玩法试玩遗留：`docs/PLAYTEST-2026-10-01-newplayer.md` 的「未解决问题」，P1 默认模式下大多已经不在主路上。
- 日志噪音很小。MythicMobs `ExampleItems.yml` 里的 `GOLDEN_HELMET` 在 1.12 不存在，没修。
- 剩下的候选：
  - 精选周规则可以再加几条，比如计时或黑暗（没做的原因见 P2 草案 §5e）。
  - 主城氛围：书 §1 只要求「一句背景」。
  - 机器人点 TrMenu 时用 `bot.clickWindow(…).catch(()=>{})`，不要 await（TrMenu 不回确认包，await 会超时）。
- 团本 DP 队伍：冒险页团本图标右键 = `/corerpg p1 recruit r01|r02`（D105），给在线的已首通 Q07 玩家发可点的 [申请入队]，队长收到 DP 自带的 [同意] [拒绝]（只这一条，D113），拒绝后有「已拒绝 X」。招募挂 10 分钟：图标 lore `%corerpg_p1_recruits%`、`/corerpg p1 recruit list`，已首通 Q07 的人上线时也会看到。也可以用 GUI（`/dp team`）。
