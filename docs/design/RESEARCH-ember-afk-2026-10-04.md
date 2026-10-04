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
