# STATUS · 余烬体力 S0

**日期：** 2026-09-28 00:41（Asia/Shanghai）  
**岗：** 余烬-插件岗执行器  
**依据：** `docs/design/design-ember-stamina-dnf-daily.md` §A；派工 · S0 优先  
**CoreRpg 版本：** **1.15.12**（已部署 play：`plugins/CoreRpg.jar`）  
**Verdict：** **✅ S0 冒烟结案 PASS**（功能项全绿；`papi parse` 软债已于 2026-09-28 复测勾销，见 `docs/status/STATUS-ember-papi-stamina-parse-retest.md`）  
**S1：** **未开**（总控：等 S0 结案后再开）

---

## 一句话

票券进本改为 **余烬体力**：日 0:00（上海）回满；日常 30 / 周 45 / 精英 40 / 深渊 30 / 团 50 / 灾厄奔赴 0；周本·精英·团本周首次免费走账户 `weekly_grant_credit`；停发一切 `ticket_ember_*`；旧票菜单兑换 + T0+7 登录自动折算。

---

## 冒烟（`/tmp/stamina-s0-smoke.json` · 玩家 `St0_9555`）

| 检查 | 结果 | 证据要点 |
|------|------|----------|
| 进日常体力够扣 30 并进本 | ✅ PASS | `[日常] …（体力 -30）` · 落地 `(0,65,0)` · DP「已消耗体力」 |
| 扣后体力 60 | ✅ PASS | `[体力] 60/90` |
| 不足（20）拒绝且不扣 | ✅ PASS | `体力不足（需 30，当前 20/90）` · 仍在枢纽 · 仍 20/90 |
| 周本首次免费 | ✅ PASS | `[周本] …（本周首次免费）` |
| 周本二次扣 45 文案 | ✅ PASS | `[周本] …（体力 -45）`（随后 DP「挑战过快」冷却，扣费文案已验） |
| convert API | ✅ PASS | 无旧票时提示「没有可兑换的旧票」 |
| 进本失败退还（代码） | ✅ PASS | `TicketEntryService.refundEnter` 已接线（冒烟未造 start-console=false） |
| `ops.json` 终态 | ✅ **`[]`** | 测后 `/deop RpgBot` + 写空 |
| PAPI `%corerpg_stamina%` 等 | ✅ **已勾销** | Expansion 注册；在线玩家自解析 12 stub PASS（`docs/status/STATUS-ember-papi-stamina-parse-retest.md` · tip `114b7bc`）。跨玩家逐名 parse 仍可能 Failed-to-find-player，属测法脚本债，非产品缺占位。 |

**总判：** 功能冒烟 **PASS**；PAPI parse 软债 **已勾销**（菜单/进本基线仍采信 S0）。

---

## PAPI（设计 A.6）

`CoreRpgExpansion` identifier=`corerpg`，已实现：

- `%corerpg_stamina%` / `%corerpg_stamina_max%` / `%corerpg_stamina_bank%`
- `%corerpg_stamina_cost_daily|weekly|abyss|elite|raid%`
- `%corerpg_stamina_reset%`（文案「今日 0:00」）
- credit：`%corerpg_stamina_credit_weekly|elite|raid%`

启用日志含 `stamina` 特性串；jar 含 `StaminaService.class` + `CoreRpgExpansion.class`。  
菜单顶栏已写 `%corerpg_stamina%` / `_max` / `cost_*`（日常等）。

---

## 落地摘要

| 项 | 说明 |
|----|------|
| PlayerData | `stamina` / `staminaBank` / `staminaResetDate` / `potionStaminaToday` / `weeklyGrantCredit*` |
| `StaminaService` | 日切回满、consume/refund、月卡上限 +30→120、药剂日顶 90、旧票折算表 A.4 |
| `TicketEntryService` | 扣体力（或周免费抵扣）→ `dp start-console`；失败退还 |
| 停发票 | `cash.yml` 各 `free_tickets: 0`；Cash/TicketGrant 不再发 NI 票；月卡登录/开通 → +30 体力；商城日票→体力药/直加 |
| 奖励 YAML | `progress`/`quest`/`mail` 中 `ticket_ember_*` → `consumable_ember_stamina_30`（物品岗 NI；Mail/Quest 已接线 stamina/药 key） |
| 旧票 | `/corerpg stamina convert` + 生活菜单「旧票→体力」；`migration_t0=2026-09-28` +7 登录自动折算 |
| TrMenu | hub/daily/weekly/abyss/raid/shop/life 进本文案改体力；日常线 B/C 灰显「筹备中」（S1 前置 UI） |
| DP | EmberDaily/Weekly/Abyss 开始文案「已消耗体力」 |

关联物品岗：`docs/status/STATUS-ember-stamina-items-s0.md`（勿与本 STATUS 混淆）。

---

## 不做 / 未开

- **S1** 门廊→房1→房2→Boss 分房：**未开**（等总控结案令）  
- Paper/NMS、无限体力、一图一服、盲改 Boss HP  
- 本岗 **未** commit/push（让开总控审计执行器推送）

---

## 回报主代理 / 总控

- **版本：** CoreRpg **1.15.12**  
- **STATUS：** `docs/status/STATUS-ember-stamina-s0.md`  
- **冒烟：** **PASS**（进本扣/拒/周首免/二次 45/convert/ops=[]）；PAPI parse **已勾销**（2026-09-28 复测）  
- **S1：** 未开  
- **ops.json：** `[]`
