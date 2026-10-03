# STATUS · 网游壳 RPG Shell（CoreRpg + TrMenu hub）

**日期：** 2026-09-12（Asia/Shanghai）  
**验收：** 服正在重启验证 CoreRpg 指令；本笔记记录菜单与命令面

## CoreRpg（勿覆盖 jar）

- **已部署：** `plugins/CoreRpg.jar` **1.1.0**（约 27078B，含 coin/sign/activity/bounty/PAPI）
- **维护方：** 余烬-插件岗；**不要覆盖**该 jar
- Runtime config：`plugins/CoreRpg/config.yml`
- Player YAML：`plugins/CoreRpg/players/<uuid>.yml`

### 指令一览

| 指令 | 说明 |
|------|------|
| `/corerpg help` | 帮助 |
| `/corerpg status` | 币 / 签到 / 活跃 / 击杀 / 材料 / 悬赏 |
| `/corerpg spawn [mob] [n]` | MM 刷怪（保留） |
| `/corerpg coin` | 查询余烬币 |
| `/corerpg coin give <玩家> <数量>` | 管理给予（`corerpg.admin`） |
| `/corerpg sign` | 上海日历日签到 |
| `/corerpg activity` / `activity claim` | 活跃查看与 25/50/75/100 箱领取 |
| `/corerpg bounty` / `bounty claim` | 今日悬赏查看 / 完成后领奖 |
| `/corerpg reload` | 重载配置（admin） |

别名：`/crpg` `/rpg`  
可选 PAPI：`%corerpg_coin%` `%corerpg_signed%` `%corerpg_activity%`

## TrMenu `/ember` 主界面

- **文案依据：** `docs/ember-hub-copy.md`（6×6 全入口）
- **文件：** `plugins/TrMenu/menus/ember_hub.yml`
- **同步：** `server-runtime/plugins/TrMenu/menus/` 与 plugins **同路径**（已一致）
- **Title：** `§6余烬 · 冒险枢纽`
- **可点击图标：** **36**（布局 6 行 × 6 列居中）
- **占位菜单：** `ember_shop.yml`、`ember_coming.yml`（仍可用）

### 菜单入口与动作

| 行 | 入口 | 状态 / 动作 |
|----|------|-------------|
| 1 | 角色 / 誓约 / 天赋 / 使魔 / 御兽 / 图录 | lore 含「即将点燃」+ tell |
| 2 | 挂机庭 | tp `-40 65 270` |
| 2 | 日常 · 余烬窟 | `menu: ember_daily` |
| 2 | 周常 · 深核 | `menu: ember_weekly` |
| 2 | 深渊 / 团本 / 灾厄 | 即将点燃 |
| 3 | 悬赏 | `command: corerpg bounty`（**无**即将点燃） |
| 3 | 活跃 | `command: corerpg activity`（**无**即将点燃） |
| 3 | 签到 | `command: corerpg sign`（**无**即将点燃） |
| 3 | 战令 / 勋阶 / 邮寄 | 即将点燃 |
| 4 | 强化 / 镶嵌 / 套装 / 分解 / 仓库 | 即将点燃 |
| 4 | 附魔 | tell 附魔提示（**无**即将点燃） |
| 5 | 盟约 / 天梯 / 竞技 / 寄售 / 好友 / 设置 | 即将点燃 |
| 6 | 商城 | tell + `menu: ember_shop`（壳，lore 即将点燃） |
| 6 | 礼包 / 月卡 / 外观 | 即将点燃 |
| 6 | 帮助 | 养成指引 lore（**无**即将点燃） |
| 6 | 关闭 | close |

未实装统一反馈：`tell: §8此功能即将点燃，敬请期待。` + bass 音效，**不强制 close**。

## 阻塞 / 注意

- **不要覆盖** `plugins/CoreRpg.jar`（插件岗已部署验收版）。
- 菜单热更：TrMenu reload 或等服起后 `/ember` 验收。
- 并行写源码曾出现竞态；以已部署 jar + 本 hub YAML 为准。
