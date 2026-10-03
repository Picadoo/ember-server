# STATUS · Ember 免费门票发放 smoke — ready / waiting for CoreRpg 1.3.10

**日期：** 2026-09-13 12:34（Asia/Shanghai）  
**规格：** `docs/design/DESIGN-ember-economy-monetization.md` · `docs/design/DESIGN-ember-cash-monthly.md` · `docs/status/STATUS-ember-abyss.md` · NI `ember-dungeon-tickets.yml`  
**约束：** 未改 Paper；**未覆写** `CoreRpg.jar`；**未跑** server smoke

---

## 期望免费发放（验收基准）

| 票 | NI id | 显示名 | 免费额度 | 周期 |
|----|-------|--------|----------|------|
| 日票 | `ticket_ember_daily` | 余烬日票 | **3** | 每日（Asia/Shanghai） |
| 周票 | `ticket_ember_weekly` | 余烬周票 | **1** | 每周（ISO 周） |
| 深渊票 | `ticket_ember_abyss` | 余烬深渊票 | **1** | 每日 |

说明：日票仍受 `cash.yml` `daily.hard_cap: 6`（免费+氪+月卡赠）约束；深渊日免费 1、硬顶建议 ≤2（见 `docs/status/STATUS-ember-abyss.md`）。周票免费 1，氪金补票本期可不测。

---

## CoreRpg 部署检查

| 项 | 值 |
|----|-----|
| `plugins/CoreRpg.jar` | **202379** bytes · mtime **12:32 CST**（2026-09-13 04:32 UTC） |
| `plugin.yml` version（已部署） | **1.3.9** |
| `CashService` join 发日票 | **有** · `daily.free_tickets: 3` · `ticket_ember_daily` |
| jar 内 `cash.yml` | 仅 **daily** 免费；**无** weekly / abyss free 块 |
| `/corerpg ticket` · `ticket status` | **无**（usage 无 ticket 子命令） |
| 周票 / 深渊票 join 免费发放 | **未接线**（需手动 `/ni give`） |
| NI `ember-dungeon-tickets.yml` | 日/周/深渊/团本票定义 **已有** |

**结论：** 1.3.9 仅 join 发 **日票 ×3**；周票 ×1、深渊票 ×1 免费发放与 `/corerpg ticket status` **尚未进 jar** → **不跑** `ticket-grant-smoke.js`，等插件岗正式部署 **CoreRpg 1.3.10** 后再测。

---

## 已准备

| 路径 | 内容 |
|------|------|
| `mineflayer-tests/ticket-grant-smoke.js` | RpgBot · `/clear` 清票 → 探测 `/corerpg ticket status\|clear\|reset\|grant` → **重连**触发 onJoin → 背包计数 余烬日票/周票/深渊票（或缺命令时 fallback `cash` + `abyss`） |
| `plugins/NeigeItems/Items/ember-dungeon-tickets.yml` | `ticket_ember_daily` / `_weekly` / `_abyss` |
| `docs/design/DESIGN-ember-economy-monetization.md` | `daily.free_tickets: 3` · `weekly.free_tickets: 1` |
| `docs/status/STATUS-ember-abyss.md` | 深渊日免费 1 |

### 脚本期望命令面（1.3.10）

```
/clear RpgBot
/corerpg ticket status              # 若存在：展示日/周/深渊免费与持有
/corerpg ticket clear|reset         # 若存在：清计数/防重入，便于重测
/corerpg ticket grant               # 若存在：显式触发免费发放
# 否则：quit + 重连，走 onJoin 发放
# 验收背包（或 status）：
#   余烬日票 ×3 · 余烬周票 ×1 · 余烬深渊票 ×1
/corerpg cash                       # fallback：granted/硬顶
/corerpg abyss                      # fallback：背包深渊票计数
```

---

## 等待插件岗

1. 将 **周票 ×1 / 深渊票 ×1** 免费发放编入 **`CoreRpg.jar` 1.3.10**（join 或等价 `/corerpg ticket grant`；日票 ×3 保持）  
2. 建议提供 `/corerpg ticket status`（及可选 `clear`/`reset`/`grant`）便于冒烟；`plugin.yml` version **1.3.10**  
3. **不要**用本机随意覆盖 jar；由插件岗正式部署后再测  
4. 服重启（新类不可热重载）后跑 smoke；注意 `dailyFreeGranted` / 周键防重入——重测需跨日/跨周或 admin reset  

---

## 部署后复跑

```bash
cd /workspace/minecraft/mineflayer-tests
# 确保无其它 RpgBot 在线；CoreRpg.jar == 1.3.10 且周/深渊免费已接线
node ticket-grant-smoke.js
```

期望：清票并触发发放后，背包（或 `ticket status`）可见 **日票 ≥3 · 周票 ≥1 · 深渊票 ≥1**；日志 `TICKET_GRANT_SMOKE_PASS`。若仅日票到账、周/深渊仍 0 → 记 `TICKET_GRANT_SMOKE_INCOMPLETE`。

---

## 未改

Paper / `CoreRpg.jar` / 其它 jar · 未执行 ticket-grant smoke · 未改 NI 门票 YAML
