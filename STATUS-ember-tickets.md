# STATUS · 日/周/深渊票自动发放（CoreRpg 1.3.10）

## 版本
- CoreRpg **1.3.10** · jar `plugins/CoreRpg.jar` ≈ **208404** bytes（~204K）
- Paper **未改** · `./start.sh custom` 已确认日志 `CoreRpg 1.3.10 enabled … tickets …`

## 行为
- Join：`ensureCashDay` → 免费日票 → 月卡登录礼 → 周票 → 深渊票
- NI 未就绪：`+20t` / `+60t` 重试（同一 idempotent `runJoinGrants`）
- 新发合并提示：`[门票] 日票×3 · 周票×1 · 深渊票×1`（仅本轮新发项）
- **不**自动发 raid 票

## PlayerData / YAML
- `weeklyTicketGrantWeekId`（对齐 `DailyService.weekId()`）
- `abyssTicketGrantDate`（对齐 `DailyService.today()`）

## cash.yml
```yaml
weekly:
  free_tickets: 1
  ticket_ni_id: ticket_ember_weekly
abyss:
  free_tickets: 1
  ticket_ni_id: ticket_ember_abyss
```

## 命令
- `/corerpg tickets` · `/corerpg ticket` — 背包日/周/深渊票 + 今日/本周已发标志

## 类
- 新增 `TicketGrantService`（由 `CashService.onJoin` 调用）
