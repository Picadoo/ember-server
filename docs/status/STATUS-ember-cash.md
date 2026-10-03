# STATUS · 余烬晶钻 / 日票 / 月卡（CoreRpg 1.3.1 落地）

**日期：** 2026-09-12（Asia/Shanghai）  
**规格：** `docs/design/DESIGN-ember-cash-monthly.md`（对齐 `docs/design/DESIGN-ember-economy-monetization.md` §5.2）  
**本岗：** 插件岗增量编译 — **DONE**（未改 Paper；未抹 enhance/covenant/talent/calamity）

---

## 已落地

| 产物 | 路径 |
|------|------|
| 设计规格 | `docs/design/DESIGN-ember-cash-monthly.md` |
| YAML stub | `plugins/CoreRpg/cash.yml` + `CoreRpg/src/main/resources/cash.yml` |
| 实现 | `CashService` + PlayerData/Store 字段 + 命令 + join 发放 + PAPI |
| 构建 | CoreRpg **1.3.1** · jar **91493** bytes |
| 部署 | `plugins/CoreRpg.jar` · `server-runtime/plugins/CoreRpg.jar` |
| `_golden` | 已同步 src + pom + jar |

### 命令

| 命令 | 行为 |
|------|------|
| `/corerpg cash` | `[晶钻]` 余额 + 今日日票 granted/硬顶 + 晶钻限购 |
| `/corerpg cash give <player> <n>` | 管理加减晶钻（可负） |
| `/corerpg shop buy daily_ticket` | 扣价发票（SKU/硬顶/余额校验） |
| `/corerpg monthly` | 月卡状态 / 到期 / 今日礼 |
| `/corerpg monthly buy` | 扣价开卡/续期 + 立即日票尝试 |

聊天前缀：`[晶钻]` `[商城]` `[月卡]`（设计 §5.1）

### PAPI

`%corerpg_cash%` `%corerpg_monthly%` `%corerpg_daily_tickets%` `%corerpg_daily_cap%`

### 玩家 YAML 键

`ember_crystal_cash`（写契约；读优先，回退 `crystalCash` 并双写）  
`monthlyCard` `monthlyExpireDate` `monthlyLastGrantDate`  
`dailyTicketsGranted` `dailyTicketsBought` `dailyEntriesUsed` `dailyCashResetDate` `dailyFreeGranted`

### cash.yml 旋钮（未写死在 Java）

- daily.free_tickets **3** · hard_cap **6** · ticket_ni_id `ticket_ember_daily`
- shop.daily_ticket price **60** · sku_limit **3**
- monthly price **68** · duration **30** · login coin **200** + ticket **1**

---

## 插件岗交接清单

- [x] 实现子命令：`cash` / `cash give` / `shop buy daily_ticket` / `monthly` / `monthly buy`
- [x] 读取 `cash.yml`；挂到现有 `/corerpg reload`
- [x] 玩家 YAML 字段（设计 §4）
- [x] 读余额：`ember_crystal_cash` 优先，回退 `crystalCash`；洗约/洗点同一账户
- [x] 登录：免费 3 张；月卡有效则每日币 200 + 日票 +1（`granted >= 6` 跳过票）
- [x] 聊天前缀对齐设计 §5.1
- [x] 权限：`corerpg.cash` / `corerpg.shop` / `corerpg.monthly`（default true）+ `corerpg.use` / `corerpg.admin`
- [x] PAPI：`%corerpg_cash%` `%corerpg_monthly%` `%corerpg_daily_tickets%` `%corerpg_daily_cap%`
- [x] 增量编译部署；**需重启 Paper**（新类不可热重载）
- [x] 保留 coin/sign/activity/bounty/enhance/socket/covenant/talent/calamity
- [x] `_golden/` 已对账同步

---

## 未改

Paper · MythicMobs · AttributePlus · DungeonPlus 次数引擎 · 强化/镶嵌/誓约/天赋/灾厄逻辑语义 · 周票/战令发货

---

## 重启

**Restart required** — 部署了含 `CashService` 的新 `CoreRpg.jar`（1.3.1），Paper 必须重启后命令才可用。菜单壳可另 `/trmenu reload`。

---

## 阻塞

无（命令已实装；验收见设计 §8 命令落地后清单，待 RpgBot / 重启后冒烟）
