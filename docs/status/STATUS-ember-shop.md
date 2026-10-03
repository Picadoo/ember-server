# STATUS · 余烬商城 / 战令 / 勋阶

**日期：** 2026-09-13（Asia/Shanghai）  
**插件：** CoreRpg **1.4.4**（storage=mysql）  
**规格：** `docs/design/DESIGN-ember-economy-monetization.md` §5、`docs/design/DESIGN-ember-cash-monthly.md`

## 已可玩

| 入口 | 行为 |
|------|------|
| 商城 · 月卡 | `corerpg monthly buy` + 查 `monthly`（价 68 晶钻） |
| 商城 · 日票 | `corerpg shop buy daily_ticket`（价 60 · 硬顶 6） |
| 商城 · 周票 | `corerpg shop buy weekly_ticket`（价 **180** · sku_limit **1** · hard_cap **2** · NI `ticket_ember_weekly`） |
| 商城 · 余烬币 | `corerpg coin` |
| 战令 · 免费轨 | 邮件模板 `pass_track_free` → 自动 `mail claim all` |
| 战令 · 付费轨 | `/corerpg shop buy pass_unlock`（价 48 晶钻；`seasonPassPaid`；邮件 `pass_track_paid_welcome`） |
| 战令 · 邮寄 | `/corerpg mail` / `ember_mail` |
| 勋阶 stub | `/corerpg vip` · `/corerpg vip claim`（日礼币 120 · `vipDailyClaimDate` · 无 LuckPerms） |

## 周票计数（PlayerData）

- `weeklyTicketsGranted` / `weeklyTicketsBought` / `weeklyTicketShopWeekId`（`DailyService.weekId()`）
- 免费发放（`TicketGrantService`）同步 `weeklyTicketsGranted += free`
- `ensureWeeklyTicketWeek`：换周重置；若本周 `weeklyTicketGrantWeekId` 已发免费则 `granted = max(granted, free)`

## 构建 / 部署

- jar `plugins/CoreRpg.jar` ≈ **4713098** bytes
- `./start.sh custom` → 日志 `CoreRpg 1.4.4 enabled (storage=mysql…)`
- Paper **未改** · MySQL **保持**

## Smoke（RpgBot）

- `cash give` → `shop buy weekly_ticket` → `[商城] 已购买周票 ×1` · 背包周票 +1
- 同周再买 → `[商城] 周票限购已满`
- `/corerpg tickets` → `granted 2 · 购 1`（含免费同步）

## 仍占位

- 战令赛季经验 / 等级阶梯发奖
- 外观 / 首充真支付
- 勋阶 LuckPerms 组累进 / 分档日礼

## CoreRpg 1.4.4

- `/corerpg shop buy weekly_ticket`：180 晶钻 · 周限1 · 硬顶2 · NI `ticket_ember_weekly` — WeekBot PASS
- `/corerpg vip` / `vip claim`：轻量日礼币 120 — PASS
- 材料仓：开发中 → 预计 1.4.5
