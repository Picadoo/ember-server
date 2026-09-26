# STATUS · 余烬 RPG 总进度（CoreRpg 1.3.8）

**日期：** 2026-09-13（Asia/Shanghai）  
**基线：** `plugins/CoreRpg.jar` **1.3.8**（198825 bytes）· Paper **未改** · AttributePlus **已停放**  
**约束：** 本文只记状态；**不改 jar**。

**图例**

| 标签 | 含义 |
|------|------|
| **LIVE** | 命令/DP/配置已接线，冒烟可走主路径 |
| **MENU-STUB** | TrMenu/命令壳有，深逻辑未开或 tell「暂未开放」 |
| **DESIGN-ONLY** | 仅 DESIGN / 文档，无运行时实现 |

---

## 一句话

CoreRpg **1.3.8** 已覆盖日常养成 + 社交轻量 + 盟约（含 Boss 门控）+ 使魔跟随 + 天梯/PAPI + **竞技 queue/leave/claim** + **寄售 sell/list/cancel（及 buy）**。副本五本与地图拆分、HD 天梯板、hub 无「即将点燃」均为 LIVE。使魔魂尘、师徒深度与 AttributePlus 实伤仍 stub / 停放；**晶钻未进寄售白名单**、**pvp 别名缺失**。

---

## LIVE（已冒烟 / 已接线）

### CoreRpg 命令面

| 模块 | 命令要点 | 备注 |
|------|----------|------|
| 币 / 签到 / 活跃 / 悬赏 | `coin` `sign` `activity` `bounty` | 早期冒烟通过 |
| 强化 / 镶嵌 | `enhance` `socket` | lore `#ember_en` / `#ember_sk` |
| 分解 / 重铸 | `scrap` `reforge` | 白名单刃/护符 |
| 晶钻 / 商城日票 / 月卡 | `cash` `shop buy daily_ticket` `monthly` | join 发日票 |
| 誓约 / 天赋 | `covenant` `talent` | blaze/ash/warden |
| 邮寄 / 好友 / 设置 | `mail` `friend` `settings` | 师徒见 STUB |
| 天梯 | `ladder` + PAPI `%ember_ladder_*%` | HD 三板已 create |
| 使魔 | `pet` list/summon/dismiss/unlock | 短名 ashling/cinder；feed→STUB |
| 盟约 | `guild`（别名 alliance）create/info/invite/donate… | **`guild boss` 门控 LIVE**（1.3.7：在盟·周限·扣贡献·`dp start EmberGuildBoss`） |
| **竞技** | `arena` queue 1v1\|2v2 / leave / stats / **claim** | **LIVE**（1.3.8；匹配 stub 结算；日箱币+外观碎片） |
| **寄售** | `auction`（别名 `ah`）**list** / **sell** / buy / **cancel** | **LIVE**（1.3.8；税 10%；列表持久化 `auction.yml`） |

### DungeonPlus

| 本 | 状态 |
|----|------|
| EmberDaily / EmberWeekly | LIVE |
| EmberAbyss | LIVE（进本·清层推进·出本冒烟 PASS） |
| EmberCalamity | LIVE（单独进本·Boss 降临） |
| EmberRaid | LIVE（票+3～5 人数限制行为 PASS） |
| EmberGuildBoss | LIVE（盟约周 Boss；CoreRpg 门控 + DP 本） |

### 基建

| 项 | 状态 |
|----|------|
| 地图拆分 `ember_{daily,weekly,abyss,calamity,raid}` | LIVE（MCA 主题标记；正式美术待 WE） |
| HD `ember_ladder_{power,abyss,speed}` | LIVE（`/hd create` 冒烟 PASS） |
| TrMenu `/ember` hub | LIVE · lore **无「即将点燃」**（计数 0） |
| PAPI `%corerpg_*%` / `%ember_*%` | LIVE（coin 等已验；天梯占位符已注册） |

---

## 已知缺口 / 备注（1.3.8）

| 项 | 说明 |
|----|------|
| **晶钻非白名单** | `auction.yml` 白名单无晶钻类 NI；不可寄售上架 |
| **`pvp` 别名缺失** | 仅 `arena`；无 `/corerpg pvp …` |
| 竞技匹配 | 内存队列 + stub 胜负结算；无独立 PvP 世界 / CoreCombat |
| 寄售深度 | 禁强化≥7、刃/护符不在白名单；无 `collect` 等扩展 |

---

## MENU-STUB（壳 / 浅接线）

| 项 | 现状 | 未开 |
|----|------|------|
| **使魔 feed** | `pet feed` →「暂未开放魂尘」 | 魂尘升级曲线 |
| **师徒深度** mentor | `friend mentor` 绑定/接受/解除有 | 师徒任务/奖励/深度养成 |
| 角色 / 套装 / 仓库 | `ember_character` / `set` / `storage` | 套装战斗效果、多页仓 |
| 战令 / 勋阶 | `ember_pass` / `ember_vip` | 经验发奖、LuckPerms 组 |
| 图录 / 商城其余 SKU | 菜单壳 + tell | 图鉴登记、周票/外观发货 |
| AttributePlus | jar 在 `plugins/_parked/` | 实伤属性管线（刻意停放） |

---

## DESIGN-ONLY（未落地运行时）

| 域 | 文档 | 说明 |
|----|------|------|
| 竞技真实对战 / 赛季积分箱 | `DESIGN-ember-arena-auction.md` | 现为 stub 匹配 |
| 寄售晶钻白名单 / 禁毕业校验补全 | 同上 | 晶钻未入白名单 |
| 全盟仓库 / 盟等级升 cap | `DESIGN-ember-guild-ladder.md` | 人数 cap 仅 stub |
| 周结算外观发放 | 同上 | — |
| 使魔图录深度 / 战力宠 | `DESIGN-ember-pet-bestiary.md` | **不卖满级战力宠**（产品红线） |
| 正式分图美术 | `DESIGN-ember-map-plan.md` | 现仅为识别标记 |
| 经济总纲未接线 SKU | `DESIGN-ember-economy-monetization.md` | 战令付费轨等 |

---

## 版本锚点

| 版本 | 增量（摘要） |
|------|----------------|
| ≤1.2.x | coin/sign/activity/bounty · enhance/socket |
| 1.3.0 | covenant/talent |
| 1.3.1 | cash/shop/monthly |
| 1.3.2 | scrap/reforge |
| 1.3.3 | mail/friend/settings |
| 1.3.4 | ladder + ember PAPI |
| 1.3.5 | pet（feed stub） |
| 1.3.6 | guild 轻量（boss stub）· pet 短名别名 |
| 1.3.7 | **guild boss 门控 LIVE** + EmberGuildBoss DP |
| **1.3.8** | **arena** queue/leave/claim · **auction** sell/list/cancel（+buy/ah） |

---

## 相关 STATUS（细账）

`STATUS-ember-{cash,covenant,enhance-socket,disassemble,mail-friends,ladder-papi,pet,guild-lightweight,guild-boss,maps,holograms-live,abyss-raid-smoke,arena-auction,arena-auction-impl}.md`

---

## 下步（不改本文范围外 jar）

1. 寄售白名单补晶钻（若产品要）；`pvp` 别名（可选）  
2. 竞技真实对战世界 / CoreCombat（现 stub）  
3. 使魔魂尘 feed；师徒深度奖励（可选）  
4. WorldEdit 正式分图；AttributePlus 若再启需独立评估  

**重启提醒：** 换 CoreRpg 大版本新类须重启 Paper；TrMenu / NI / DP 热更通常 `/trmenu reload` `/ni reload` `/dp reload`。
