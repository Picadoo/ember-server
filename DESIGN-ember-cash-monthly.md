# 余烬晶钻 · 日票限购 · 月卡 — 落地规格

**日期：** 2026-09-12（Asia/Shanghai）  
**状态：** 规格锁定（YAML + 菜单壳；**不**改 `CoreRpg.jar` / Paper）  
**承接：** `DESIGN-ember-economy-monetization.md` §2 E / §4 / §5.2（红线以彼为准，本文定存档键与命令面）  
**总纲：** `DESIGN-ember-rpg-systems.md` §8.2  
**菜单：** `plugins/TrMenu/menus/ember_shop.yml`  
**旋钮 stub：** `plugins/CoreRpg/cash.yml`  
**硬约束：** 不改方块；不直售 A/B 材料与核心；日票硬顶是 **总进本次数 6**（免费 + 晶钻购 + 月卡赠）；奖励票走 NI `ticket_ember_daily`。

---

## 0. 范围与职责

| 岗 | 活 |
|----|----|
| 总控 / 本规格 | 定玩家 YAML 键、硬顶算法、命令面、`cash.yml` 形状、商城日票接线、验收 |
| 余烬-插件 | CoreRpg：读 `cash.yml`；`cash` / `shop buy daily_ticket` / `monthly`；登录发放；**勿覆盖无关模块** |
| 余烬-菜单 | 日票图标已改：`command: corerpg shop buy daily_ticket` + tell 回退 |
| 余烬-测试 (RpgBot) | §8 冒烟（命令落地后） |

**本期不做：** 充值渠道 / 支付网关、周票购买、战令经验、外观发货、`economy.yml` 全旋钮、改 DungeonPlus 次数引擎（进本仍走现有 `dp start EmberDaily`；CoreRpg 负责 **发票 + 硬顶计数**）。

---

## 1. 硬通货 `ember_crystal_cash`（晶钻）

| 项 | 约定 |
|----|------|
| 经济层 | **E**（仅充值/礼包/管理发放；挂机不产） |
| 玩家 YAML 键 | **`ember_crystal_cash`**（整数 ≥0） |
| 展示名 | 余烬晶钻 |
| 软通货 | 仍用现有 `coin` / `/corerpg coin`（层 D） |

### 1.1 与现源码兼容

`PlayerData` / `PlayerDataStore` 已有内存字段与落盘键 `crystalCash`（camelCase）。插件岗落地时：

1. **读：** 优先 `ember_crystal_cash`；缺省回退 `crystalCash`。  
2. **写：** 写 **`ember_crystal_cash`**（契约键）。可选同时写 `crystalCash` 一季以免旧分支丢余额。  
3. 誓约洗约 / 天赋洗点已按 `crystalCash` 扣费——接线后两处必须读同一余额。

管理发放不走充值渠道：`/corerpg cash give`（`corerpg.admin`）。

---

## 2. 日票：晶钻购买 + 总进本硬顶 6

对齐货币稿：价 **60**；免费日票 **3**；晶钻 SKU 限购 **3/日**；**硬顶 = 当日总进本次数 6**（免费 3 + 氪金补票 + 月卡赠票，全部计入）。超过硬顶：拒售 / 月卡不发票（币仍发）。

### 2.1 计数（上海日历日，`DailyService.today()`）

| 玩家 YAML 键 | 含义 |
|--------------|------|
| `dailyTicketsGranted` | 今日已发票张数（免费 + 晶钻购 + 月卡赠） |
| `dailyTicketsBought` | 今日晶钻购买次数（SKU，≤ `shop.daily_ticket.sku_limit`） |
| `dailyEntriesUsed` | 今日已进日本次数（硬顶验收用；进本成功 +1，由插件或 DP 回调写入） |
| `dailyCashResetDate` | 上述日计数所属日 `yyyy-MM-dd`；跨日清零 |

**硬顶判定（买票 / 月卡赠票 / 建议进本前）：**

```
granted = dailyTicketsGranted
if granted >= cash.yml → daily.hard_cap (6): 拒绝再发票
```

进本侧目标：`dailyEntriesUsed < 6` 且持有/消耗 1 张 `ticket_ember_daily`（或与免费额度等价的内部计数）。  
**免费 3 张**：每日首次登录（或日重置）若 `dailyTicketsGranted == 0` 且当日尚未发免费票，发 **3** 张并 `dailyTicketsGranted += 3`（可用 `dailyFreeGranted: true` 防重入）。免费票也占硬顶。

### 2.2 购买算法 `/corerpg shop buy daily_ticket`

1. 日重置（`dailyCashResetDate != today` → 清 `dailyTicketsGranted/Bought/EntriesUsed`，`dailyFreeGranted=false`）。  
2. 若尚未发免费票：先走 §2.1 免费 3（不挡购买，只保证计数正确）。  
3. `ember_crystal_cash < 60` → 拒：晶钻不足。  
4. `dailyTicketsBought >= sku_limit(3)` → 拒：日票限购已满。  
5. `dailyTicketsGranted >= hard_cap(6)` → 拒：已达日票硬顶 6。  
6. 扣 60 晶钻 → `ni give <player> ticket_ember_daily 1` → `dailyTicketsBought++` → `dailyTicketsGranted++` → 落盘。  

价与上限全部来自 `cash.yml`，禁止写死在 Java。

---

## 3. 月卡

| 项 | 默认（`cash.yml`） |
|----|-------------------|
| 价 | 68 晶钻 |
| 时长 | 30 天（上海日历） |
| 同时有效 | 1 张；续费从 `max(today, monthlyExpireDate)` 起顺延 30 天 |
| 购买当时 | 立即日票 ×1（计入硬顶；满顶则只开卡不发票，并 tell） |
| 每日登录 | 余烬币 **200** + 日票 **×1（硬顶内）** |

### 3.1 玩家 YAML

| 键 | 含义 |
|----|------|
| `monthlyCard` | `true` 当 `monthlyExpireDate >= today`（也可只信日期，此键便于菜单） |
| `monthlyExpireDate` | 含当日有效，`yyyy-MM-dd`；空 = 无卡 |
| `monthlyLastGrantDate` | 上次登录礼日期；同日不重发 |

### 3.2 登录发放（`PlayerJoin` + `ensureDaily` 之后）

若 `monthlyExpireDate >= today` 且 `monthlyLastGrantDate != today`：

1. `coin += 200`（走现有 `PlayerData.addCoin`）。  
2. 若 `dailyTicketsGranted < hard_cap`：`ni give ticket_ember_daily 1`，`dailyTicketsGranted++`。  
3. 否则：只发币，tell「日票已达硬顶未发放」。  
4. `monthlyLastGrantDate = today`，`monthlyCard = true`。

过期：`monthlyExpireDate < today` → `monthlyCard = false`，不再发礼。

### 3.3 开卡（建议挂在 `/corerpg monthly buy`，查询仍是 `/corerpg monthly`）

扣 68 → 设/顺延 `monthlyExpireDate` → `monthlyCard=true` → 立即尝试赠日票 ×1（同硬顶规则）。本菜单月卡图标仍 tell 占位，命令面先做查询 + 管理/开卡。

---

## 4. 玩家存档字段（`plugins/CoreRpg/players/<uuid>.yml`）

在现有 coin / 誓约 / `crystalCash` 旁追加（插件岗写入）：

```yaml
# —— 晶钻 / 日票硬顶 / 月卡（DESIGN-ember-cash-monthly.md）——
ember_crystal_cash: 0          # 硬通货 E；读时兼容 crystalCash
monthlyCard: false
monthlyExpireDate: ""          # yyyy-MM-dd Asia/Shanghai（含当日）
monthlyLastGrantDate: ""       # 上次月卡登录礼
dailyCashResetDate: ""         # 日计数所属日
dailyFreeGranted: false        # 今日免费 3 张是否已发
dailyTicketsGranted: 0         # 今日已发票（免费+氪+月卡），硬顶 6
dailyTicketsBought: 0          # 今日晶钻购买次数，SKU 3
dailyEntriesUsed: 0            # 今日已进日本次数（硬顶验收）
```

可选 PAPI（落地后）：`%corerpg_cash%` `%corerpg_monthly%` `%corerpg_daily_tickets%` `%corerpg_daily_cap%`。

---

## 5. 命令面（CoreRpg · 插件岗实现）

权限：玩家 `corerpg.cash` / `corerpg.shop` / `corerpg.monthly`（默认真玩家有，或复用 `corerpg.use`）；管理 `corerpg.admin`。  
别名：挂在 `/crpg` `/rpg` 同一子树。热重载：`cash.yml` 进现有 `/corerpg reload`。

| 命令 | 行为 |
|------|------|
| `/corerpg cash` | 查询晶钻余额 + 今日已发票 / 硬顶 6 / 晶钻限购 |
| `/corerpg cash give <玩家> <n>` | 管理加晶钻（可负作扣除） |
| `/corerpg shop buy daily_ticket` | 扣 60 发票；失败不扣 |
| `/corerpg monthly` | 查询月卡是否有效、到期日、今日登录礼是否已领 |
| `/corerpg monthly buy` | 扣 68 开卡/续期 + 立即日票尝试（菜单未接前供 Bot / 指令） |

### 5.1 聊天前缀（便于 Bot 断言）

- `§b[晶钻] 余额：N`
- `§b[晶钻] 今日日票 §fgranted§7/6 §8（晶钻购 bought/3）`
- `§a[商城] 已购买日票 ×1（-60 晶钻）`
- `§c[商城] 晶钻不足（需 60）`
- `§c[商城] 日票限购已满`
- `§c[商城] 已达日票硬顶 6`
- `§e[月卡] 生效中 · 至 yyyy-MM-dd`
- `§7[月卡] 未开通 · /corerpg monthly buy`
- `§a[月卡] 登录礼：余烬币 +200 · 日票 +1`
- `§a[月卡] 登录礼：余烬币 +200 · 日票已达硬顶未发放`
- `§a[月卡] 已开通/续期至 yyyy-MM-dd`

### 5.2 菜单壳当前行为（命令未实装前）

日票图标：**tell 回退** + `command: corerpg shop buy daily_ticket`（未知命令属预期）。  
月卡图标：仍 tell 占位价 68（本期不改）。

---

## 6. YAML 旋钮形状 · `plugins/CoreRpg/cash.yml`

见同路径 stub（`src/main/resources/cash.yml` 镜像，供插件岗打进 jar）。禁止把 60 / 6 / 200 / 68 写死在 NMS 或 Java 常量。

---

## 7. TrMenu 接线

| 文件 | 动作 |
|------|------|
| `ember_shop.yml` 图标 `D`（日票） | `command: corerpg shop buy daily_ticket` + tell 回退（价 60 · 硬顶 6） |
| `ember_shop.yml` 图标 `$` | 仍 `command: corerpg coin`（软通货） |
| `ember_shop.yml` 图标 `A`（月卡） | 本期仍 tell；命令落地后可改 `corerpg monthly` / `monthly buy` |
| `server-runtime/plugins/TrMenu/menus/ember_shop.yml` | 与 plugins 同步 |

热更：`/trmenu reload`。命令实装后 **重启 Paper**（新类）；**不要覆盖**正在跑的无关 `CoreRpg.jar` 模块——由插件岗在现 jar 上增量编译。

---

## 8. 验收（RpgBot / 手工）

命令未实装前（菜单岗）：

- [ ] `/ember` → 商城 → 点日票：聊天含「60」与 `/corerpg shop buy daily_ticket`（或 tell 回退句）
- [ ] 命令未装时 `command:` 失败可接受，tell 必须出现
- [ ] 金粒仍走 `/corerpg coin`

命令落地后（插件岗）：

- [ ] `/corerpg cash` 输出余额；`cash give` 后余额增加并写入 `ember_crystal_cash`
- [ ] 晶钻 ≥60 且未满顶：`shop buy daily_ticket` 扣 60、背包 +`ticket_ember_daily`、`dailyTicketsGranted/Bought` +1
- [ ] 晶钻 <60：拒，不扣
- [ ] 连买至 `granted==6` 或 `bought==3`：再买拒（硬顶优先于 SKU）
- [ ] `monthly buy` 后 `monthlyCard=true`；同日再进服：币 +200，票 +1 或硬顶跳过票
- [ ] 跨日（改 `DailyService` 日或 YAML 日期）计数清零；月卡未过期仍发登录礼
- [ ] 刃基础伤害不因买票/开卡改变；无「买核心」命令

---

## 9. 明确不做（本期）

- 不改 Paper / **不覆盖** `CoreRpg.jar` 二进制（本任务只定规格与 YAML）  
- 不接真实充值、不卖周票/战令/外观  
- 不把免费 3 改成 0、不把硬顶改成无限  
- 不改誓约/天赋/强化已有命令语义（仅共享晶钻余额）

---

## 10. 与货币稿对齐备忘

| 货币稿旋钮 | 本文 / `cash.yml` |
|------------|-------------------|
| `cash.ticket_daily_price` 60 | `shop.daily_ticket.price` |
| `cash.ticket_daily_limit` 3/日 | `shop.daily_ticket.sku_limit`（购买次数） |
| `daily.hard_cap` 6 | `daily.hard_cap`（**总进本/发票**，含免费与月卡） |
| 月卡 68 / 登录币 200 + 日票×1 | `monthly.price` / `monthly.daily_login` |
| 层 E `ember_crystal_cash` | 玩家 YAML 同名键 |

周票 180 / 周硬顶 2 仍只在货币稿，**不**进本期 `cash.yml`。
