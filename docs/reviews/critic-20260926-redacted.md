> Redacted copy (2026-09-27): account names replaced with Tester1–4 / <acct>; the leaked AuthMe account list removed.

# 余烬服 · 挑刺玩家「新手 → 中期」体验验收（2026-09-26 夜）

> 执行人：critic 执行器（非 OP，全程自己注册新号，没用 secrets/，没发过物，没改过任何文件/配置，没重启过服务器）。
> 时间：2026-09-26 23:10 – 09-27 00:23（CST）。
> **重要前提**：测试期间有其他 agent 在同时改服、重启服，不是我做的：游戏服在 23:51:54、00:02:40、00:10:04 被重启过；CoreRpg.jar 23:51 升到 1.8.0（新增主线 quest.yml、新手送 `gear_ember_blade`、NPC 引路人·灰烛），00:10 又换过一次 jar；quest.yml 00:09、Multiverse worlds.yml 00:10、ember_hub 区块 r.-1.0.mca 00:20 都有改动；23:13 新增进本等级门槛（progress.yml `level_gates` + DP option.yml 里的 `%corerpg_gate_x%`）。所以 **23:16–23:50 的结论对应 1.7.0（没有新手刃），00:00 以后对应 1.8.0**。每条都标了时间。

---

## 最要紧 5 条

1. **出生格被封死，新号一步都走不动（00:21 实测）。** 新号 Tester4 出生在 ember_hub (-18.5,58,110.5)，往 W/N/S/E/NW/SW 六个方向边走边跳各 1.5 秒，每个方向最多只挪了 0.2–0.28 格。脚边西侧 (-20,58,110) 现在是一块荧石，北、南、东三面头顶 y=59 都是石头。结果：主线第一步「右键出生点北边的灰烛」、工坊（附魔台距离 7.4 格）都够不着；右键灰烛（距离 4.2，隔着墙）也没反应。别的 agent 的冒烟脚本全都用 OP `/tp -22 58 106` 把人传出去，所以测不出这个问题。23:38 我用 Tester2 走「先往西 → 再往北」还能到工坊，说明是 23:40 之后地形被改了。
   复现：注册新号 → 进服 → 按住任意方向走并跳。日志 `mineflayer-tests/logs/critic-20260926-spawntrap.log`、`critic-20260926-probe.log`（y=58/59/60 方块网格）、`critic-20260926-npc.log`。
2. **挂机庭死亡循环 + 死亡掉落 + 没法回城，主线第 1 章还主动把新手送进去。** 挂机庭重生点 (-46.5,70,250.5) 离刷怪笼 EmberAfk_Z1（-55,70,250，半径 6，MaxMobs 6）约 8.5 格，三个刷怪笼合计上限 16 只；ember_afk 的 level.dat 里 `keepInventory=false`。`/spawn` `/hub` 提示未知命令，`/mv spawn` `/mvtp` 没权限，`/corerpg spawn` 提示「需要 corerpg.admin」，菜单里也没有回城按钮。Tester1 进去 3 分钟死了 21 次，日票、周票、深渊票全部掉光；Tester3（1.8.0）按主线杀完 8 只游尸后 4 分钟死了 11–13 次，**新手刃和所有票一起掉了**，任务卡在「骨鸣 0/4」。
   复现：/ember → 挂机庭（或 /corerpg quest 第 1 章第 2 步）→ 站着别动。日志 `critic-20260926-onboard-Tester1.log`、`critic-20260926-fresh-Tester3.log`、`critic-20260926-spawncmd.log`。
3. **六张 DP 副本模板里都烤进了一只「余烬地窟骷髅」，开局就射新手，还抢副本怪的尾刀。** 扫 `plugins/DungeonPlus/map/*/region/r.-1.0.mca` 第 541 块，每张图都有一个 CustomName=`§e余烬地窟骷髅` 的实体，位置约 (-39.3,65,271.7)，离玩家落点 (-40,65,270) 只有 1.7 格。日本多局开局 1–4 秒就「shot by 余烬地窟骷髅」。它打死的副本怪（包括 Boss 蛮兵）掉落和经验都归「Unknown」：latest.log 00:12:15–00:13:06 有多条 `NeigeItems > 玩家 Unknown 不在线` 和 `玩家不在线：Unknown`。我两次通关后「精英/首领经验」都还是 0/150，几率掉落（T0 12% / 护符 8% / T1 3% / 核心）也一件没见到。
   复现：拿票 `/dp start EmberDaily`，看落点东北 1.7 格。日志 `critic-20260926-daily-Tester2.log`，服务端 `server-runtime/logs/latest.log`。
4. **团本票全服没有常规来源，团本和 T3 等于不存在；菜单却写「进服发放」。** 团本菜单写「每周团本票×1（进服发放）」，但 TicketGrantService 只发周票和深渊票，cash.yml 商城没有团本票，1.8.0 主线第 5 章也只一次性给 1 张。T3 唯一的自然来源是团本 Boss 尾刀（RaidBoss T3 刃 .08 / T3 护符 .06）；公共灾厄的 MM 掉落表里没有 T3，但灾厄菜单还写着「低概率T3」。
   复现：/ember → 团本（看 lore）；`/corerpg tickets`；看 cash.yml `skus`。
5. **「宝石 / T2 几率压得较低」被每日保底冲掉了。** 深渊满 8 层 COMPLETE：通关箱保底 T2 刃 + T2 护符，再加 abyss settle 5–9 档保底 `gem_ember_sharp`×1，每天 1 票免费；AbyssWatcher 汲取石 .18 / 疾风石 .17。周本通关箱保底 T1 刃 + T1 护符。灾厄尾刀锋利石 100%。按配置算，深渊一局期望约 3.2 颗宝石，装备一共 3 个孔，一天就能填满；**T2 第一次开深渊就能拿一整套**。
   复现：看 DungeonPlus EmberAbyss/EmberWeekly 的 reward 配置、abyss.yml `settle`、MythicMobs AbyssWatcher 掉落表（具体见「算账」）。

---

## 必改

### 必改-1 出生格封死（见最要紧 1）
- 00:21 Tester4（新注册）：`spawn pos {-18.5,58,110.5}` → `dir W moved 0.2`、`N 0.28`、`S 0.28`、`E 0.28`、`NW 0.28`、`SW 0.28`（`critic-20260926-spawntrap.log`）。
- 00:20 方块网格（`critic-20260926-probe.log`）：y=58 的 (-20,110) 是 `glowstone`；y=59 的 (-19,109)、(-19,111)、(-18,110) 都是 `stone`；(-19,59,110) 是 wall_sign。出生格变成了 1×1 的坑。
- Multiverse `worlds.yml` 里 ember_hub 的 `spawnLocation: x -18.5 / y 58 / z 110.5` 没变，是周围地形变了。
- 灰烛右键无效：`activated villager`，但 `/corerpg quest` 还停在「▶ 与枢纽的 引路人·灰烛 交谈」（`critic-20260926-npc.log`）。
- 另外（23:33 就有）：出生点头顶那面墙本来就只有 1 格高的缝，直接斜着往工坊走会卡住（`critic-20260926-workshop-Tester2.log` 第 15 行 `walk ms 20023 pos -18.7,58,110.3`）。只有先往西再往北才走得通（23:38，第 103 行）。

### 必改-2 挂机庭死亡循环（见最要紧 2）
- 配置：ember_afk / ember_hub / ember_event 三张图的 level.dat 里 `keepInventory: false`（副本图是 true）。
- 回城手段：`/spawn`、`/hub` → Unknown command；`/corerpg spawn` → 「需要 corerpg.admin」（00:22，`critic-20260926-spawncmd.log`）。
- 主线把人送进去：quest.yml 第 1 章「在挂机庭击败 余烬游尸 / 余烬骨鸣」。**名字也不对**：实际怪物显示名是「挂机庭僵尸 / 挂机庭骷髅」。
- 菜单写「无限刷碎片与骨尘，供养成闭环」，对新手来说实际是「无限刷自己的尸体」。

### 必改-3 模板里烤进去的骷髅（见最要紧 3）
- 这是模板地图数据的问题，六个本（日 / 周 / 深渊 / 团 / 盟 / 灾厄）都有，每开一个实例都会带一只。
- 连带影响：xpreward 用的是 MM `<trigger.name>`，也就是只给尾刀的人（progress.yml 注释也这么写），尾刀被这只骷髅抢走，就发给了 Unknown。
- 实测：第 3 局日本进本时 hp 只有 2.0（上一局的血量带进来了），开局就被射死。

### 必改-4 1.7.0 下新号没有武器（1.8.0 已修，但只修了一半）
- 23:16 Tester1 / Tester2 背包只有：日票 3 / 周票 1 / 深渊票 1，**没有武器**。全服唯一的 T0 刃来源是日本蛮兵掉落 12%。空手进日本约 9 秒就死，带 2 次复活也只打过第 1 波，票不退（`critic-20260926-daily-Tester2.log`）。
- 1.8.0 主线会送刃，但刃在挂机庭死一次就掉了，**而且没有第二次领取的途径**（Tester3 背包清空后 Lv13 手里什么都没有）。

### 必改-5 团本票没来源（见最要紧 4）
- TicketGrantService 只发 weekly / abyss；cash.yml `skus` 只有 daily_ticket / pass_unlock / weekly_ticket；quest.yml 第 5 章一次性给 1 张。
- 团本菜单原文：「每周团本票×1（进服发放）」。

### 必改-6 保底把「几率压低」冲掉了（见最要紧 5，数字见算账）

### 必改-7 菜单给的信息是错的
- 日本菜单：「3 波小怪 → 蛮兵」「时限约 8～12 分钟」。实测是 2 波小怪 + Boss，**约 1 分钟**（00:12:09→00:13:07 共 58 秒；第 2 局 62 秒）。
- 深渊菜单：结算档「1～4 / 5～9 / 10～14 / 15～19 / 20+」。abyss.yml 只有 3 档，副本也只有 8 层。
- 灾厄菜单：「低概率T3」。公共灾厄 Boss 的 MM 掉落表里没有 T3，只有 OP 能开的灾厄 DP 实例才有 T3 护符。
- 周本菜单：「周限一次」。cash.yml 里 `weekly_ticket.hard_cap: 2  # 免费1+氪≤2`。
- 深渊入口：菜单先 tell「深渊已开启。今日只有一次下潜…」，然后才被等级门槛拒掉（23:4x，`critic-20260926-gates-Tester2.log`）。
- 入服提示：「目标：击杀地窟亡灵」「深渊：/dp start EmberAbyss」。EmberCrypt* 地窟怪在全服没有一个刷怪点；深渊对新号有 Lv25 门槛（00:21，`critic-20260926-npc.log` 开头）。

### 必改-8 登录时泄露同 IP 的全部账号名
- 00:21 AuthMe：`You own NN accounts: <redacted account list>` 同 IP 下所有账号名直接打在聊天里。还有 `Welcome Tester4 on Unknown Server server`（服名没配）。

---

## 别扭

- **等级几乎没用。** talent.yml 整棵树总花费 blaze 8 / ash 7 / warden 7。新号 Lv10 就有 9 点，**出生就能点满**；`max_spendable: 20` 在这棵树上没有意义，菜单 lore 写的「约 15～20 点可分配」也对不上。余烬等级除了进本门槛（最高 raid 35），只剩天梯分（LadderService：等级×5）有用。**35 级以后升级什么都不解锁**，60 级封顶也就是个数字。
- **战令 30 级要 30 天零缺勤**（见算账）。战令说明和 `/corerpg pass` 的输出只写了「签到 / 日本 / 周本」，漏了深渊、团本、盟 Boss 和悬赏（悬赏领取 +15，见 docs/level-gates-bounty-20260926.md）。
- **菜单里的施工痕迹**：打开 /ember 时的 tell 还有「逻辑待接线」（使魔 / 邮寄 / 好友 / 设置 / 分解 / 盟约）；lore 里有「周 Boss（壳）」「三榜壳」「排队壳」「浏览/上架壳」「扩展页待接线」「全息：docs/ember-holograms.md」；ember_pass 里有「占位价 48 晶钻」「累计日票×5（规划）」「本期：查余额 + 欢迎礼邮件演示」。
- **菜单点了不跳转**：主菜单「帮助」「附魔」都只弹一行字，比如「附魔：携带余烬附魔晶，在附魔台强化余烬刃 / 护符。」，不带路，也没有坐标。
- **工坊附魔台旁边一个书架都没有**，原版附魔台只能亮 1 级选项，得靠服务端逻辑兜底（CoreEnchant/config.yml 注释说以服务端为准）。23:38 打开附魔台，手里拿着碎片时 `enchOffersForShard: [0,0,0]`。
- **副本外执行 `/corerpg abyss evacuate`** 会回「最高层 0 <1，无结算箱（已标记结算）· 前往 hub」：没进本也能「标记结算」，看着吓人。
- **盟 Boss 门槛**：「你不在任何盟约中」；建盟要 5000 币或 40 晶钻。新号每天能拿 签到 100 + 悬赏 60–120 + 活跃 25 档 50 ≈ 210–270 币（战令 free 的 80 币是一次性邮件，不算），F2P 建盟要 **约 19–24 天**（5000/270 ~ 5000/210）。
- **enhance.yml 里 +10 配的是 `fail: set8`**，但 EnhanceService 需要稳固符的失败分支写死成「停在 +9」，set8 是死配置，玩家看到的配置和实际行为不一致。
- **挂机庭掉落无上限**：EmberAfkZombie 碎片 100%、EmberAfkSkeleton 骨尘 100%。CoreCraft 3 碎片 + 1 骨尘 → 1 晶，附魔晶等于无限（前提是你别被循环杀死），「附魔晶稀缺」这层设计直接没了。
- **日本太短**：1 分钟一局，每天 3 局，3 分钟就做完了日常，之后只剩挂机庭。

## 还行

- 签到 / 活跃 / 悬赏的闭环能跑：签到 +100 币、活跃 +10、战令 +10、余烬 +20，重复签到会被拒；活跃 25 档 +50 币；悬赏随机（地窟扫荡 20 杀 60 币 / 余烬猎杀 50 杀 120 币 / 00:22 看到「清剿亡灵 0/30」），领取后余烬 +40、战令 +15。00:00 跨天：票补发、签到重置、击杀和精英经验计数归零（0/100、0/150），都实测过。
- 击杀经验 +2/只，不刷屏，`/corerpg level` 看得到（例如 82/100），日上限 100 生效。
- 等级门槛拒绝时**不扣票**，提示清楚：「周常本需要余烬等级 Lv.20」「需要余烬等级 Lv.30（当前 Lv.10）」「团本人数 3～5，当前 (1)」。
- 工坊（第一次设计）位置很好：出生点西北 6.7–8.5 格，有全息字「余烬工坊 | 工作台·附魔台·铁砧」「附魔用余烬附魔晶 · 修理用碎片/核心碎片」。**前提是出生格没被封。**
- 拿刃打日本 1 分钟一局、单人能过，通关奖励固定（核心 1 / 晶 1 / 碎片 5 / 3 级原版经验 / 余烬 +60 / 战令 +20），两次通关各发一次，没有重复发。
- 强化 +1 实测 3 碎片 100% 成功，`enhance info` 说明清楚（下一档 +2 90% / 5 碎片；孔 1 空、孔 2 需 +5），重登后 lore 保留「强化 +1」。

---

## 算账

符号说明：Lv = 余烬等级；数据来源写成「文件 → 字段」。

### 1. 升级要多少天
- 曲线：progress.yml → `ember_xp.base 60 / step 12 / step_from 10 / max_level 60`。
  每级所需 need(L) = 60 + 12·(L−10)；从 Lv10 到 Lv N 的累计 cum(N) = Σ_{L=10}^{N−1} need(L) = 60·(N−10) + 6·(N−10)(N−11)。
  → Lv20 **1140**，Lv25 2160，Lv30 **3480**，Lv35 5100，Lv40 7020，Lv50 11760，Lv60 **17700**。
- 日收入：progress.yml → `sources`：sign 20、daily_clear 60、weekly_clear 200、abyss_clear 80、raid_clear 250、guild_boss_clear 100、elite 15、boss 40、kill 2；上限 `kill_daily_cap 100`、`combat_daily_cap 150`。
  - F2P 满勤（不做主线）：Lv20 前每天 20 + 3×60 + 3×15（精英）+ 100（击杀上限）= **345**；Lv20 后每周再加 周本 200 + 2×15；Lv25 后每天再加 深渊 80 + 4×15。
    逐日模拟：**Lv20 第 4 天、Lv25 第 7 天、Lv30 第 9 天、Lv35 第 13 天、Lv60 约第 37 天**。
  - 氪满 6 张日票（cash.yml → `daily.hard_cap 6`）：Lv20 第 2 天，Lv60 第 24 天。
  - 休闲（每天 1 次日本 + 签到 + 击杀上限）：Lv20 第 11 天，Lv30 第 27 天，Lv60 约第 125 天。
  - progress.yml 注释写「Lv20 满勤 2～3 天」，比我算的 4 天乐观。**另外实测精英经验被骷髅抢走（0/150），真实值更低。**
  - 主线（quest.yml 各步 `xp`，不受日上限）：第 1 章 60+100+100+60+200 = 520，第 2 章 100+100+80+300 = 580，合计 1100 ≈ 直接到 Lv20；六章合计 4710 ≈ Lv33。但后面几章要打被门槛挡住的内容，前提是能从出生格和挂机庭活着出来。

### 2. 战令要多久
- progress.yml → `pass_xp`：sign 10、daily 20、weekly 40、abyss 20、raid 40、guild 20；`daily_cap 100`、`xp_per_level 100`、`max_level 30`。悬赏 +15（docs/level-gates-bounty-20260926.md）。
- 满级需要 30×100 = 3000；受每日上限 100 限制，**理论最少 30 天**。赛季「约 30 天」= 一天都不能漏。
- F2P 在 Lv25 前每天最多 10 + 3×20 = 70（加悬赏 85）。模拟结果：第 30 天约 Lv26；休闲玩家 30 天约 Lv10；氪 6 张日票刚好 30 天满级。实测 Tester2 当天战令最高 50/100，`/corerpg pass claim` → 「没有可领取的等级奖励」。

### 3. 日本 / 周本期望产出
- 日本通关箱（DP EmberDaily reward，实测一致）：核心 1 / 晶 1 / 碎片 5 / 3L，每天 3 次 → **晶 3、核心 3、碎片 15、原版 9 级**。
- DailyBrute 几率（MM）：T0 刃 .12、护符 .08、T1 刃 .03（只给尾刀）。T1 期望 1/0.03 ≈ 33 局 ≈ 11 天；实测被骷髅抢了尾刀，为 0。
- 周本通关箱：核心 3 / 晶 2 / 碎片 8 / 骨尘 4 / **T1 刃 + T1 护符保底** / 6L。

### 4. 宝石 / T2 / T3 期望多少次出一件
- T2：深渊 COMPLETE 通关箱 **保底 T2 刃 + T2 护符**，每天 1 票（cash.yml → `abyss.free 1`）→ **1 天 1 套**。怪物掉率：AbyssBrute T2 刃 .01、AbyssWatcher T2 刃 .03 / 护符 .02、RaidElite .02、WeeklyBruteA .03、RaidBoss 刃 .15 / 护符 .12。
- 宝石：深渊一局怪物期望 ≈ 12 潮尸×.05 + 11 骨潮×.05 + 5 混潮×.05 + 2 蛮层×.05 + 2 看守×(.18+.17) ≈ **2.2**，加 abyss settle 5–9 档保底 `gem_ember_sharp` 1 → **≈ 3.2 颗/天**。另外团本、盟 Boss 通关箱各保底 1 颗，灾厄尾刀 sharp 1.0。装备孔位：刃 2 孔 + 护符 1 孔 = 3 → **一天填满**。
- T3：唯一的自然来源是 RaidBoss 尾刀，P(至少出一件 T3) = 1 − (1−.08)(1−.06) = **13.5%** → 期望约 7.4 次通关；团本票没有常规来源（每人一辈子 1 张主线票）；就算有每周 1 票，3–5 人抢尾刀 → 每人约 22–37 周。
- 结论：**T2 / 宝石太松，T3 等于没有。** 这和上线说明「几率压得较低」相反：T2 和宝石根本不靠几率。

### 5. 附魔水晶需求 vs 日产出
- 需求：CoreEnchant/config.yml → `extra_crystals: T0 0 / T1 1 / T2 2 / T3 3`，`cost 1/2/3`，一件只能附一次。单件 T0 需 1–3 晶，T3 需 4–6 晶；全套 T3 刃 + 护符最多 12 晶。
- 供给：日本 3/天 + 周本 2/周 + 团本 2 + 灾厄日箱 1（calamity.yml `daily_chest`）+ 免费战令每 5 级 1 + **挂机庭无限**（CoreCraft：3 碎片 + 1 骨尘 → 1 晶，4 只怪 ≈ 1 晶）。
- 结论：晶不是瓶颈，**原版经验等级才是**：附魔每次花 1–3 级，CoreAnvil 修理每片 +1 级（`material_repair_extra_cost 1`），日本每天只给 9L，另外 progress.yml → `kill_levels.daily_cap 6`。

### 6. 强化期望（enhance.yml 各档成功率和材料，按代码行为 +10 失败停在 +9）
- 不用保护券：到 +7 约 33 次，碎片 368.5 / 骨尘 79.8 / 核心 2.9；到 +9 约 408 次，碎片 4928 / 骨尘 978 / 核心 100；+10 成功率 8%，再加核心 37.5 + 稳固符 12.5。
- 用保护券：到 +9 需碎片 389 / 核心 20.2 / 保护券 10.5；到 +10 需核心 57.7 / 稳固符 12.5。
- 稳固符来源：AbyssWatcher .05×2/天 + 灾厄尾刀 .05 + 付费战令 Lv30 1 张 → F2P 攒 12.5 张约 **125 天**。+10 就是给氪佬准备的。

---

## 上线清单逐条核对

| # | 条目 | 结论 | 证据 |
|---|---|---|---|
| 1 | 日本 / 周本 / 深渊 / 灾厄 / 盟 Boss / 团本都能从头打到尾，奖励只发一次 | **FAIL** | 日本：空手（1.7.0）9 秒死、只过第 1 波；拿刃（1.8.0）单人两次通关（58s / 62s），奖励每次各发一次、没有重复；第 3 局被模板骷髅开局射死。周本 Lv20 / 深渊 Lv25 / 灾厄 Lv30 / 团本 Lv35 + 3 人被门槛挡住（不扣票）；团本票没有来源；盟 Boss 要建盟 5000 币。六个模板都烤进了一只骷髅（`r.-1.0.mca` 第 541 块）。「奖励只发一次」只实测了日本，其余只按配置核对（每次 COMPLETE 每人一份，团戒按每周首通去重）。 |
| 2 | 新号出生在主城，主城有工作台 / 附魔台 / 铁砧 | **FAIL（23:38 前是 PASS）** | 出生点 ember_hub -18.5,58,110.5 ✔；工作台 -24,58,104 / 附魔台 -22,58,104 / 铁砧 -20,58,104 ✔，23:38 走到过，三个窗口都能打开（workshop 日志第 165 行）。**00:21 新号出生格被封死，走不到工坊**（spawntrap 日志）。没有书架。 |
| 3 | T0–T3 都能附魔，阶越高要的水晶越多 | **PARTIAL（只按配置核对）** | CoreEnchant/config.yml `extra_crystals 0/1/2/3` ✔ 递增。实机：23:38 附魔台能打开，但手里没刃；00:1x 拿刃 + 2 晶想去附魔时 bot 卡在出生格走不过去；00:21 出生格封死。T1–T3 装备自然拿不到。**实机附魔没做成。** |
| 4 | 新号 10 级，60 级封顶；打本 / 签到 / 精英 / Boss 给经验，有日上限 | **PARTIAL** | 新号 Lv10 ✔；签到 +20 ✔；日本通关 +60 ✔（Lv10 28/60 → Lv11 34/72 → Lv12 34/84）；击杀 +2、上限 100 ✔；跨天重置 ✔。精英 / Boss 经验实测一直 0/150，被骷髅抢走、发给了 Unknown ✘。60 级封顶只看了配置（`max_level 60`）。 |
| 5 | 战令 1–30 级有奖励，`/corerpg pass claim` 领 | **PARTIAL** | progress.yml `pass_rewards` free / paid 1–30 都有配置 ✔；命令能用 ✔（Lv0 时回「没有可领取的等级奖励」）；`pass free` 邮件发了 80 币 + 2 碎片 ✔。一天之内升不到 Lv1（最高 50/100），**等级奖励的领取没验到**。 |
| 6 | 勋阶按累计充值升 | **未验** | 没有支付通道，要 OP 执行 `cash give`（不允许）。配置 vip_tiers `[68,198,…,19988]`、`/corerpg vip` 显示「下一阶需 68」，vip claim 勋阶 0 给 20 币 ✔。 |
| 7 | 几率掉落真正生效，宝石 / T2 / T3 压得较低 | **FAIL** | MM 掉落表已改成纯几率 ✔（配置）。但深渊每天保底 T2 一套 + 宝石，周本保底 T1，看守 .18 / .17，灾厄尾刀宝石 1.0 → 宝石和 T2 没有被压低；T3 只剩团本尾刀，团本票又没来源 → 实际拿不到；实测几率掉落全部被模板骷髅的 Unknown 尾刀吃掉，一件都没见到。 |

---

## 测试环境与日志
- 入口：Waterfall 127.0.0.1:25565（offline，1.12.2）→ 登录服 AuthMe → 游玩服 Paper 1.12.2。CoreRpg 1.7.0（23:51 前）→ 1.8.0（23:51 / 00:10 被别的 agent 替换）。服务器被别人重启过 3 次（23:51:54 / 00:02:40 / 00:10:04）。
- 同时在线的其他机器人：RpgBot、Main*、MainB*（服务端日志有「MainB suffocated in a wall」）、Gb*、Rd*、calbot 等。它们用 OP /tp 移动。
- 我的账号（测试号，已脱敏）：Tester1（挂机庭循环，背包清空）、Tester2（Lv12，blade +1）、Tester3（1.8.0 新号，挂机庭掉光）、Tester4（00:20 注册，困在出生格）。
- 脚本：`mineflayer-tests/critic-20260926-{lib,onboard,daily,gates,workshop,probe,duo,fresh,enchant,spawntrap,npc,spawncmd}.js`
- 日志：`mineflayer-tests/logs/critic-20260926-*.log / *.json`（onboard-Tester1、daily-Tester1/2、gates-Tester2、workshop-Tester2、duo、fresh-Tester3、enchant-Tester2、probe、spawntrap、npc、spawncmd）
- 服务端：`server-runtime/logs/latest.log`（00:12–00:15 的「Unknown 不在线」）
- 引用的配置：plugins/CoreRpg/{progress,cash,quest,talent,enhance,abyss,calamity,scrap}.yml、CoreEnchant/config.yml、CoreAnvil、CoreCraft、MythicMobs 掉落表、DungeonPlus map/*/option.yml 和 reward、Multiverse-Core/worlds.yml、TrMenu menus（ember / ember_pass 等）。
