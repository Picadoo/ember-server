# RESEARCH — 挂机 / 离线收益参考（P1 挂机庭，D177）

日期：2026-10-04 · 作者：afk-p1 执行者 · 用于 `DESIGN-ember-afk-p1-2026-10-04.md`
业主原话（17:56）：「挂机这部分怎么样了？现在很多人没时间，挂机服很火。」

先研究、后设计：下面每条都写清**机制、数值、它怎么防止挂机压过主动玩法或养号刷号**，并标出我们照抄哪一条。
标「低」的来源可信度一般（私服论坛或社区文章），只拿来说明「品类惯例」，不拿来定数值。

## 1. 参考样例

### 1.1 魔兽世界 · 休息经验（Rested XP）
- 来源：Warcraft Wiki「Rest」<https://warcraft.wiki.gg/wiki/Rest>（含 Talk 页对离线 1/4 速率的讨论）；暴雪官方论坛讨论
  <https://us.forums.blizzard.com/en/wow/t/boosted-rested-xp/1000685>。
- 机制：在旅店 / 主城下线，每 8 小时攒 5% 当前级经验条（一个「泡」）；**不在休息区下线只按 1/4 速率**（每 32 小时一泡）；
  上限 150%（30 泡，约 10 天）。休息经验不是白给，是**打怪经验翻倍**，打完就没了。
- 平衡：休息只给追赶，不给新东西；上限约等于一个半等级，长期不上线也追不上天天玩的人。
- **我们借用**：离线时间按 **1/4 折算**（WoW 非休息区速率），并设上限。

### 1.2 Lost Ark · 休息奖励（Rest Bonus）
- 来源：官方指南 Dungeons <https://www.playlostark.com/en-us/game/guide/dungeons-guide>；
  Fandom Chaos Dungeon <https://lostark.fandom.com/wiki/Chaos_Dungeon>；Maxroll <https://maxroll.gg/lost-ark/resources/general-chaos-dungeon-guide>。
- 机制：当天没用掉的混沌地牢次数（每 100 共鸣）记 20 点休息值，攒满 40 点下一次通关掉落翻倍，上限 200 点（约 5 天）。
  守护者讨伐同理（每次未用 10 点、20 点双倍、上限 100）。
- 平衡：错过的那天能补回一部分，但**必须真去打本才兑现**，而且有天数上限，不能无限囤。
- **我们借用**：放到 Stage 2 候选（「休息加成」只作用在通关上），Stage 1 不做。

### 1.3 AFK Arena · 挂机宝箱 + 快速奖励
- 来源：AFK Global 升级指南 <https://www.afk.global/afk-arena/guides/leveling>；Fandom VIP Rank
  <https://afk-arena.fandom.com/wiki/VIP_Rank>；AFK Guide 快速奖励 <https://afk.guide/fast-rewards/>。
- 机制：挂机宝箱离线累积，**12 小时封顶**（VIP 才能延长）；收益**按已通过的最高主线关卡**定档；
  「快速奖励」每天限次。
- 平衡：收益跟主线进度挂钩，挂机是推主线的「副产品」，推得越远挂得越多；封顶让一天不上线和三天不上线一样。
- **我们借用**：**挂机层按主线首通开放、收益按已解锁的最高层计**（灰坡 Q01 → 烬原深处 Q07）；离线有每日封顶。

### 1.4 冒险岛 M（MapleStory M）· 自动战斗时长
- 来源：MapleStory M 官方 Wiki 存档「Auto Battle」<https://maplestorym-archive.fandom.com/wiki/Auto_Battle>；
  Fandom「Tickets」<https://maplestorym.fandom.com/wiki/Tickets>。
- 机制：每天免费 2 小时（120 分钟）自动战斗，没用完的可攒到 360 分钟；更多时长要用券。
- 平衡：**每日时长封顶**才是挂机真正的闸门；可攒但封顶，忙的人隔天补一点，不会无限累积。
- **我们借用**：**每日 120 分钟（12 轮 × 10 分钟）**的计数上限，在线离线合计。

### 1.5 仙境传说 M（Ragnarok M: Eternal Love）· 战斗时间
- 来源：官方 Facebook 维护公告（Episode 6 战斗时间调整）
  <https://www.facebook.com/PlayRagnarokM.EN/posts/dear-adventurers-we-have-scheduled-server-maintenance-on-the-30th-of-october-fro/1411184119033204/>；
  GamingPH 说明 <https://gamingph.com/2018/10/what-is-combat-time-or-stamina-in-ragnarok-m-eternal-love/>；
  官方封禁第三方程序公告转载 <https://www.enduins.com/news/ragnarok-m-eternal-love-to-suspend-and-ban-accounts-using-third-party-programs>。
- 机制：每日战斗时间 300 → **150 分钟**，最多累积 900 → 450 分钟；用完后经验和掉落大幅下降。
  调整时官方把非 MVP 怪经验和掉落提高 220% 作为补偿——**缩时长、提单位收益**。
- 平衡：明说是针对**自动挂机和多开刷号**：时间封顶后，多开每个号也只有那点额度，挂机脚本的收益被压平。
- **我们借用**：「封顶 + 封顶后就没有」。我们比 RO 更狠：封顶后**为零**（不是打折），因为我们的挂机本来就是副收益。

### 1.6 传奇类 · 泡点（安全区定时加经验）
- 来源（低）：64GM 论坛「土城安全区泡点脚本」<https://www.64gm.com/thread-1134-1-1.html>；
  「泡点地图安全区与非安全区的转换」<https://www.64gm.com/thread-25091-1-1.html>；
  博客园「传奇泡点地图制作脚本」<https://www.cnblogs.com/tutublogs/p/8341677.html>。
- 机制：进入泡点地图（NPC 传送），定时器每秒 / 每分钟加经验或货币；安全区泡点**收益低**、非安全区高但会被 PK；
  常见配置有等级上下限、**每日经验上限**、离开地图即停止。
- 平衡：泡点是「不在线打怪的人也有点进度」，数值远低于打怪；靠每日上限和「人必须在图里」限制。
- **我们借用**：**人在挂机庭任意位置**每满一轮结算（不用打怪），离开即停表；收益刻意低于打本。

### 1.7 我的世界 挂机池插件（国内挂机服主流做法）
| 插件 | 来源 | 与我们相关的做法 |
|---|---|---|
| YiyunAFKpond | <https://github.com/Chun2919089965/YiyunAFKpond>；MineBBS <https://www.minebbs.com/resources/yiyunafkpond-folia.16220/> | 选区挂机池，经验 / 金币 / 点券奖励；**每池每日上限**（`moneyMaxDaily` / `expMaxDaily`）；拦截传送；审计日志 |
| Lapis-AFKPool | LapisWorks/Lapis-AFKPool（GitHub 镜像 <https://github.laiyagushi.com/LapisWorks/Lapis-AFKPool>） | **单池 + 全局**每日上限；统计与审计日志 |
| IdlePool | <https://github.com/SenalMC/IdlePool>；MineBBS <https://www.minebbs.com/resources/idlepool-v-gui-ia.17164/> | **结算单号唯一 + 账本防重复发放**；奖励先进「暂存箱」再领取（事务安全） |
| AFKRoom | <https://github.com/jw00b/AFKRoom> | 每分钟固定币；`max-afk-duration`（例 120 分钟）封顶；检测转头刷动作 / 宏 |

- 国内挂机服惯例：**人进区域就发、零操作**；收益是币 / 经验 / 点券这类**账号内货币**；每日封顶；审计日志。
- **我们借用**：区域挂机 + 每日封顶（YiyunAFKpond / Lapis）；**唯一结算号写进账本、先记账后发放**（IdlePool）；
  **计时封顶 120 分钟 / 天**（AFKRoom `max-afk-duration` 同量级）。我们**不做**动作检测（反 AFK 机）——见设计 §6：收益设计成「AFK 机挂满也只有这么多」，就不需要检测。

## 2. 共同规律（设计照这几条走）
1. **挂机给的是追赶，不是第二条主线**：WoW、Lost Ark、AFK Arena 都让挂机 / 休息收益 < 主动玩。RO 明说挂机多了要压。
2. **闸门是时间上限，不是检测**：MSM 120 分钟、RO 150 分钟、AFK Arena 12 小时、挂机池每日上限——封顶之后多挂没收益，
   AFK 机就失去意义。
3. **离线比在线少**：WoW 非休息区 1/4；AFK Arena 只在宝箱时长内累积。
4. **跟主线进度绑定**：AFK Arena 按最高关卡定档，推主线才能挂更好的档。
5. **防重复靠账本**：IdlePool 的唯一结算号 / 暂存箱——与我们现有的 `EmberRunRules.Ledger`（唯一键）同一思路。
6. **防多开靠「给不了别人」**：RO 封时长、MC 挂机池给账号货币；可交易的材料一旦能挂出来，就会有人多开挂材料卖。

## 3. 本服现状（研究时核对的事实）
- 旧 `ember_afk` 世界（AfkTierService，`config.yml afk_tiers` / `afk_caps`，MythicMobs `EmberAfk.yml`，菜单 `ember_afk`）：
  四层 灰坡 Lv10 / 荒原 20 / 焦土 30 / 烬原深处 40；怪掉 `mat_ember_shard` / `mat_ember_bone_dust` /
  `mat_ember_core_fragment` / `mat_ember_soul_dust`（每日碎片 150、骨尘 80、核心 10，达顶后 25% / 8% 软顶）。
- D63 把它从 P1 主菜单拿掉（只在 `ember_hub_legacy.yml`），但**命令仍可达**：`/corerpg afk <n>` 只要 `corerpg.use`（默认有），Lv10 就能进。
- **这几种材料 id 和 P1 主线本发的是同一批**（碎片 / 骨尘 / 核心碎片）。P1 一天三次通关约 84 碎片；旧挂机一天 150 碎片
  ——**旧挂机已经是一个超过主动玩法的漏洞**（目前没有真实玩家，所以还没出事）。
- 旧寄售行（`auction.yml enabled: true`，币计价，白名单含核心碎片）`/corerpg ah` 也命令可达，是跨账号转币 / 核心的通道（与 D73「不开市场」冲突）。
- 余烬币（`PlayerData.addCoin`）其它地方不能转给别人；邮件发送要 `corerpg.mail.send`（玩家没有）。NI 材料物品可以丢地上被别人捡。
- `player-idle-timeout=0`（服务器不踢挂机）。

## 4. 结论（交给设计）
- 复用旧 `ember_afk` 世界与四层，**改为按主线首通开放**（AFK Arena）。
- 只付**账号绑定的余烬币 + 余烬经验**（MC 挂机池惯例 + 防多开），**不付材料 / 装备 / 印记**——主线本仍是唯一的装备与材料来源。
- **每日 12 轮 × 10 分钟**上限（MSM / RO / 挂机池），在线离线合计；**离线按 1/4**（WoW），离线最多 6 轮（AFK Arena 封顶）。
- **满一天挂机 ≤ 一次通关的基础币和经验**（泡点「远低于打怪」的惯例；RO「挂机不压过主动」）。
- 账本唯一键先记账后发放（IdlePool）。
- 同时**关掉旧漏洞**：P1 下旧挂机怪不再掉材料，旧寄售行关闭。

---

## 5. 第二轮研究：自动战斗挂机（D177 rev 2，2026-10-04 18:50 起）

业主原话（18:49）：「你挂机 就直接在那里站着就算挂机吗」。§1.6 泡点 / §1.7 挂机池那种「人在区域里就按时间发」被否决。
国内挂机服说的「挂机」是**角色自己打怪**（传奇的内挂 / 自动战斗、MC 1.12 RPG 挂机服的自动攻击插件、手游的离线收益），
下面按同样格式写机制、数值和防滥用，并标出我们照抄哪一条。

### 5.1 MC 1.12 挂机服 · AutoAttack 类服务端自动攻击插件
- 来源：MineBBS「AutoAttack 自动攻击（MM 支持）」<https://www.minebbs.com/resources/autoattack-mm.17644/>（支持 1.8–1.16.5，标注 1.12.2 最佳）。
- 机制：**服务端**替玩家出手，不靠客户端连点器；按范围自动选最近的怪（可指定 MythicMobs 怪 id），站桩在攻击距离内打；
  「自动追怪」放在付费版。攻击走的是正常的玩家攻击，所以服务器的伤害插件 / 装备属性照常生效。
- 平衡：插件本身不定收益，收益来自怪的掉落和服里的每日上限；挂机图一般单独一张，怪按图分级。
- **我们借用**：服务端自动攻击、**真实玩家攻击事件**（我们的 P1 普攻公式 / 暴击 / 套装触发全部照常结算）、站桩不追怪（怪自己过来）、只在挂机图生效。

### 5.2 传奇 / 热血传奇 · 内挂（自动战斗辅助）
- 来源（低）：雾都游戏网传奇技术文章 <http://www.wuduy.com/html/jishuwenzhang/chuanqi/205129.html>、<http://www.wuduy.com/html/jishuwenzhang/chuanqi/231728.html>；
  145z「热血传奇内挂设置教程」<http://www.145z.com/html/rexuechuanqi/jishujiaocheng/207487.html>。
- 机制与常见数值：近战挂机范围 3–5 格；攻击间隔 800–1200 毫秒；技能**冷却好就自动放**；血量 30–50% 自动吃药；
  **自动拾取**半径 3–5 格并可按物品过滤；血量过低自动回城；**部分地图禁止内挂**。
- 平衡：挂机效率完全由角色属性决定——装备差的人只能挂低级图，挂高级图会死、会被迫回城；所以玩家仍要去打装备。
- **我们借用**：攻击距离 3.2 格、0.7 秒一刀（P1 满蓄力上限 0.55 秒以内的保守值）；烬斩冷却好就放；
  「死太多就停」代替「低血回城」（P1 不让喝无限药，见 §5.6）；**只在挂机庭能自动战斗**；战利品直接进账（=自动拾取，且没有地面物品可被别人捡）。

### 5.3 传奇永恒（盛趣官方资料站）· 自动战斗
- 来源：传奇永恒官方资料站「自动战斗」<https://actcq.web.sdo.com/project/2016/web/0428dataStation/content.aspx?aid=342158>。
- 机制：开启后在设定范围内自动寻找并攻击怪物；**自动拾取只在自动战斗时生效**；可设优先攻击目标；**进入安全区自动关闭**。
- **我们借用**：自动战斗是开关（菜单里一键暂停 / 继续），离开挂机庭自动停；只打「自己的」怪（我们的个人怪 = 天然的优先目标）。

### 5.4 决战沙城（微信小游戏）/ 热血传说 · 内置挂机 + 离线收益
- 来源（低）：中手游攻略「决战沙城离线挂机收益说明」<https://shengli.cmge.com/gametips/detail/id/2476.html>；
  4399「热血传说挂机系统」<https://news.4399.com/jzscrxcs/zixun/m/872377.html>。
- 机制：离线时按**当前关卡 / 地图的挂机效率**累积经验、金币、装备；关卡越高效率越高；离线收益有时长上限，上线一次性领取。
- 平衡：离线效率跟着「你在线时能打到哪」走，推图（主线）才能提高挂机效率。
- **我们借用**：离线 = **你在线自动战斗的实测击杀速度** × 折算比例，按你最后挂的那一层算，封顶。

### 5.5 放置类 RPG 的离线收益公式
- 来源：MapleStory Idle RPG Wiki「Experience」<https://idle.maplestorywiki.net/w/Experience>（每分钟击杀数超过约 50 后在线明显强于离线）；
  Game Developer「The Math of Idle Games, Part III」<https://www.gamedeveloper.com/design/the-math-of-idle-games-part-iii>；
  以及社区文章（低）：solana.garden 放置游戏离线收益指南、tideward.app、dev.to 上的离线进度实现文。
- 通用公式：**离线收益 = min(离线时长, 上限 4–24 小时) × 在线速率 × 离线效率（常见 10–50%）**；在线永远 ≥ 离线。
- 我的世界「咸鱼之王」式插件（低，bbs.mc9y.net/resources/1694）：挂机奖励 = 已到达层数 × 在线分钟 × 0.2。
- **我们借用**：离线效率 25%、窗口最多 12 小时、每日离线最多 300 只（日上限的一半），在线才能拿满。

### 5.6 共同规律（rev 2 设计照这几条走）
1. **挂机 = 角色自己打怪**，不是站着计时；打得快不快、能不能活，看角色属性（传奇内挂、AutoAttack、决战沙城）。
2. **挂机图按强度分级**，装备差挂低图、装备好挂高图——挂机收益反过来推玩家去刷主线装备（传奇「挂不动就回去打装备」）。
3. **只在指定地图自动战斗**（传奇部分图禁内挂、传奇永恒安全区关闭）。
4. **离线跟在线效率挂钩，打折 + 封顶**（决战沙城、放置 RPG 通用公式）。
5. **掉落要防「多开挂材料给大号」**：§2 第 6 条仍成立——自动战斗掉的材料必须账号绑定，不能丢 / 不能进箱子。
6. **个人怪 / 抢怪规则**：公共刷怪点会被多人堆怪、抢怪、拉怪集中刷；国内挂机服常用「个人挂机点 / 分线」。我们给每人一组**只打你、只能被你打**的个人怪，堆在一起也不会多刷。

### 5.7 结论（交给 rev 2 设计）
- 挂机庭每层刷**个人怪**（主线同款 MM 模型、挂机专用血量 / 攻击），角色**自动普攻 + 自动烬斩 + 自动拾取（直接进账）**，用真实 P1 属性。
- 收益**按击杀**：余烬币 / 经验 / P1 材料（碎片、骨尘、核心碎片）/ 少量装备胚料；**每日击杀封顶**，远低于主动打本（数值由 p1sim 定）。
- 离线 = 在线实测击杀速度 × 25%，按最后挂的层，12 小时窗口，每日最多 300 只。
- 防滥用：材料账号绑定（不能丢、不能进容器）、个人怪（不能堆怪 / 抢怪）、自动战斗只在挂机庭、P1 下旧挂机怪不刷。
