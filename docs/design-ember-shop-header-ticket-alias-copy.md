# B2.89 · shop 文件头 → 体力药/周体力包 SKU 备忘

- **STATUS：待批 A**（总控 · 2026-09-30 22:31 Asia/Shanghai） · 未批准前不改 `plugins/TrMenu/menus/ember_shop.yml`，不改玩法 YAML。
- **范围：**仅 `plugins/TrMenu/menus/ember_shop.yml` 约 L2–L3 文件头注释两行（维护者可见，非玩家 UI）。**L1 零改。**
- **tip 路径：**`docs/design-ember-shop-header-ticket-alias-copy.md`
- **施工岗：**批后交 **插件岗 / TrMenu 菜单文案**。
- **前序对齐：**B2.88 已 PASS · 勾销（设计 `3ddf5fd` · 批准 `f8a899d` · 插件 `45308ff` · 测 `e4b52a4` · close `cd4e07c`）；L151 周体力包 lore 本轨归零，本窗**勿重开 L151**。B2.52 已去在售摘要教命令斜杠，本窗**勿回退斜杠**。
- **本窗纪律：**只改 L2 与 L3；价/命令/tell/Icons/其它注释段/L151/玩法 YAML 零改；勿 git push。

## 1. 现况与范围

已读 `plugins/TrMenu/menus/ember_shop.yml` 文件头约 20 行，确认 L1–L3 与 L151 现网。目标是把 L2「原日票/周票」改成更清晰的**体力药/周体力包 SKU 备忘**口径；可保留别名说明。去掉易读成未结迁移/票废的措辞。

旧两行（live，L2–L3）：

```yaml
# S0：体力药 / 周体力包（原日票/周票）
# 在售：月卡、战令解锁、体力药、周体力包（SKU 别名仍 daily_ticket/weekly_ticket）
```

锁定项：

- **L1** 保持原文：`# 余烬商城 — DESIGN-ember-economy-monetization.md §5 + DESIGN-ember-rpg-systems.md §8.1`
- 仅替换上述 L2、L3；Icons / Open / Layout / 价 / 命令 / tell / L151 lore / 其它槽位零改。
- 保留展示名「体力药 / 周体力包」与 SKU 别名 `daily_ticket` / `weekly_ticket` 事实；不扩写价、发放或命令。

## 2. 荐案

批后仅替换 L2、L3：

```yaml
# S0：体力药 / 周体力包（展示名；SKU 别名仍 daily_ticket / weekly_ticket）
# 在售：月卡、战令解锁、体力药、周体力包
```

### 2.1 锁定口径

- L2：去掉「原日票/周票」；改为「展示名；SKU 别名仍 daily_ticket / weekly_ticket」，一眼可读为展示名与 SKU 对照。
- L3：只留在售摘要（月卡、战令解锁、体力药、周体力包）；别名说明已收进 L2，勿回退 B2.52 已去掉的教命令斜杠。
- 不把禁词写进文件头注释；不宣称玩法已清或票物已废。
- 不捆绑 L151 lore、cash、progress、已结窗、断塔/霜锈/AFK 或精英壳旁记。

## 3. 施工与验收

- [ ] 批 A 前 `plugins/TrMenu/menus/ember_shop.yml` 零 diff；批后仅由**插件岗 / TrMenu 菜单文案**替换约 L2、L3 两行。
- [ ] 仅目标文件头两行变化；L1 / 价 / 命令 / tell / Icons / L151 / 其它注释段 / 其它槽位零改。
- [ ] 静态核对旧两行与荐案、展示名、SKU 别名、在售摘要、无斜杠教命令；不做长测/挑刺。
- [ ] 本设计提交仅包含本 tip 与 backlog；工作区其它脏文件不纳入。

## 4. 回报与禁项

- 回报 tip 路径、STATUS、旧 L2–L3 与荐案全文、施工岗、`ember_shop.yml` 零改确认、范围零捆绑确认、验收、pull、commit/ahead。
- 禁写文件头：「票已废」「B0.1 已清」及易读成未结迁移/票废的「原日票/周票」；禁宣称玩法已清。
- 禁顺手改玩法 YAML、L151 lore、cash、progress、断塔/霜锈/AFK、已结窗；禁长测/挑刺；精英壳勿硬开。
- **勿 git push。**
