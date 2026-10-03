# STATUS · S0 体力药 NI + 旧票 lore 迁移（物品岗）

- 时间：2026-09-28（Asia/Shanghai）
- 执行：余烬-物品
- 依据：`docs/design/design-ember-stamina-dnf-daily.md` §A.4～A.5；总控派工「S0 物品」
- 未改：体力数值/日限硬顶（插件）；cash/progress 停发（插件）；Paper / MM / DP

## 结论

**体力药 NI 已建；旧 `ticket_ember_*` lore 改为迁移期兑换文案；ID 全部稳定，只认 ItemManager NI id，禁显示名匹配。**

---

## NI id 列表

| NI id | 显示名 | 效果 / 折算 | 文件 |
|-------|--------|-------------|------|
| `consumable_ember_stamina_30` | 余烬体力药·小 | 消耗后 +30 体力（插件施加） | `plugins/NeigeItems/Items/ember-stamina.yml` |
| `consumable_ember_stamina_45` | 余烬体力药·周 | 消耗后 +45 体力（插件施加） | 同上 |
| `ticket_ember_daily` | 余烬日票 | 迁移兑换 +30 | `plugins/NeigeItems/Items/ember-dungeon-tickets.yml` |
| `ticket_ember_weekly` | 余烬周票 | 迁移兑换 +45 | 同上 |
| `ticket_ember_abyss` | 余烬深渊票 | 迁移兑换 +30 | 同上 |
| `ticket_ember_raid` | 余烬团本票 | 迁移兑换 +50 | 同上 |
| `ticket_ember_elite` | 余烬精英票 | 迁移兑换 +40 | 同上 |

`server-runtime/plugins/NeigeItems/Items` → 与 `plugins/...` 同源（symlink），已可见同文件。

---

## 物品岗已做

1. 新建 `ember-stamina.yml`：小药 + 周剂（设计 A.5 商城周票 SKU 需要，一并落 id）。
2. 五票 lore：改为「迁移期可兑换为体力」+ 折算数值 + 生活菜单提示；**无指令、无裸 id**。
3. 票 ID **未改名**，插件 `consumeExact(player, ticket_ember_*, 1)` 兑换仍可用。

---

## 联调说明（请插件岗）

| 点 | 约定 |
|----|------|
| 体力药使用 | 监听/consumable 表挂 **NI id** `consumable_ember_stamina_30` / `_45`；右键或菜单使用后 `addStamina`；**禁止**按显示名「余烬体力药·小」匹配 |
| 旧票兑换 | `NiBridge.consumeExact` + 折算表（日30/周45/深30/团50/精英40）；菜单文案勿写 `/corerpg …` |
| 停发 | `cash.yml` / `progress.yml` / 月卡战令：停 `ticket_ember_*` 发放，改体力或发 `consumable_ember_stamina_30` |
| lore | 物品侧不写指令；日限购 3、药剂回体 ≤90 由插件/cash 执行 |

验收：`/ni reload` → `/ni give <玩家> consumable_ember_stamina_30 1` 与 `_45`；旧票 lore 可见「可兑换为体力」。

## 请总控

物品 S0 交卷；体力账户 / 使用监听 / 停发与兑换菜单派 **余烬-插件**。
