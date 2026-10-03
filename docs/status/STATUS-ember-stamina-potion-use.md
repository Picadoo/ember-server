# STATUS · S0 体力药使用闭环（插件岗）

- **时间：** 2026-09-28 04:20（Asia/Shanghai）
- **执行：** 余烬-插件岗执行器
- **依据：** `docs/status/STATUS-ember-stamina-items-s0.md`；设计 `docs/design/design-ember-stamina-dnf-daily.md` §A.5；总控派工「S0 体力药使用闭环」
- **CoreRpg：** **1.15.16**（已部署 play：`plugins/CoreRpg.jar`）
- **Verdict：** **✅ PASS**
- **未改：** 体力日回上限（`base_max`/`monthly_bonus`/`bank_cap`）、进本费用（`stamina.costs.*`）、`potion_daily_cap=90`
- **Git：** **未** commit / push（jar 仅部署，未入库）

---

## 一句话

手持 NI `consumable_ember_stamina_30` / `_45` 饮用 → `addPotionStamina(+30/+45)` 并计入 `potionStaminaToday`；超顶取消饮用且不扣瓶；商城/发放给 NI **不再**预扣日顶（真正使用时计入）。

---

## 改动

| 类/文件 | 变更 |
|---------|------|
| `StaminaService.java` | 实现 `Listener`；`POTION_NI_ID_45`；`onPotionConsume` 仅 `ni.getNiId` 匹配；`grantPotionOrStamina` 发 NI 不预加 `potionStaminaToday`；`grantPotionNi` / `tryUsePotion`；奖励 key 支持 `_45` |
| `CoreRpgPlugin.java` | `registerEvents(staminaService, this)` |
| `CashService.java` | 日票商城购买去掉「药剂今日已达上限」预检（限购仍走 `sku_limit`；日顶在饮用时拦） |
| `plugin.yml` / `pom.xml` | **1.15.15 → 1.15.16** |

**匹配红线：** 只认 NI id（`getNiId`），禁显示名匹配。

**`_45`：** 与 `_30` 同池计入 `potion_daily_cap`（90）；`cash.yml` 周限购字段未发明新经济（周店直加路径保留）。

---

## 用法（玩家 / 验收）

```text
/ni give <玩家> consumable_ember_stamina_30 1
/ni give <玩家> consumable_ember_stamina_45 1
# 手持饮用（POTION）→ 体力涨、瓶减、药剂今日 +30/+45
/corerpg stamina show   # 查看「药剂今日 N/90」
```

超顶：`药剂今日 + 本瓶 > 90` → 提示「今日药剂回体已达上限 90」，事件取消，瓶不扣。

发放：`/corerpg shop buy daily_ticket` 或奖励 key → 只给 NI，**不**预加 `potionStaminaToday`。

---

## 自检证据

| 检查 | 结果 | 证据 |
|------|------|------|
| `_30` 饮用 | ✅ | `Pu_9732`：体力 20→50；`回复 +30…药剂今日 30/90`；瓶 count 0 |
| `_45` 饮用 | ✅ | 体力 20→65；药剂 30→75；瓶 count 0 |
| 超顶拒且不扣瓶 | ✅ | 药剂 75 时再饮 `_30` →「已达上限 90」；瓶 before=1 after=1 |
| 发放不预扣 | ✅ | `Pg_8287`：药剂 75 时商城购药成功；`pot 75→75`；得 NI×1；再饮仍拒且瓶留 |
| ops 终态 | ✅ | `server-runtime/ops.json`=`[]`；`login-runtime/ops.json`=`[]` |

JSON：`/tmp/stamina-potion-use-smoke.json`、`/tmp/stamina-potion-grant-smoke.json`  
脚本：`mineflayer-tests/stamina-potion-use-smoke.js`、`stamina-potion-grant-smoke.js`

Play 日志：`CoreRpg 1.15.16 enabled`（约 04:19 CST）。

---

## 请总控

S0 体力药使用闭环交卷；请派 **余烬-测试** 正式短验收（priority true）。本岗未开 S1、未改日回/进本费用、未 git commit。
