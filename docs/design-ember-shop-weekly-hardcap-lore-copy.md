# B2.88 · shop 周体力包 lore → 玩家可读周硬顶/限购口径

- **STATUS：已批 A（总控 · 2026-09-30 22:23 Asia/Shanghai）· 交插件岗** · 批后仅插件岗改 `plugins/TrMenu/menus/ember_shop.yml` 约 L151 lore，不改玩法 YAML。设计 tip `3ddf5fd`。
- **范围：**仅 `plugins/TrMenu/menus/ember_shop.yml` 约 L151 周体力包 lore 一行（玩家可见）。
- **tip 路径：**`docs/design-ember-shop-weekly-hardcap-lore-copy.md`
- **施工岗：**批后交 **插件岗 / TrMenu 菜单文案**。
- **前序对齐：**B2.87 已 PASS · 勾销（设计 `e4900d6` · 批准 `5881af9` · 插件 `5b41046` · 测 `89bbd57`/`b9c8f9e` · close `1330cad`）；B2.86 cash `shop.weekly_ticket.hard_cap` 已是维护备忘轨。本窗**只动菜单 lore**，不对齐写「维护备忘」到玩家可见文案。
- **本窗纪律：**只改该 lore 一行；价/命令/tell/其它 lore/文件头/其它槽位零改；勿 git push。

## 1. 现况与范围

已读 `plugins/TrMenu/menus/ember_shop.yml` 约 L140–L170，确认周体力包槽位 E。同文件日槽体力药 lore 参考风格：`'§8限购 3/日 · 硬顶总进本 ≤6'`（玩家可读、短）。目标是把「硬顶免费1+氪≤2」改成玩家一眼懂的周硬顶/限购口径。

旧行（live，L151）：

```yaml
        - '§8限购 1/周 · 硬顶免费1+氪≤2'
```

锁定项：

- 仅涉及上述 lore 一行；`name`、价 `180`、`tell`、`command: corerpg shop buy weekly_ticket`、其它 lore 行、文件头、体力药槽位 D 及其它槽位锁定零改。
- 保留「限购 1/周」与「免费1+氪/购买≤2」硬顶事实；措辞改为玩家可读，不扩写价、发放数值或命令。

## 2. 荐案

批后仅替换上述 lore 行：

```yaml
        - '§8限购 1/周 · 周硬顶 免费1+购买≤2'
```

### 2.1 锁定口径

- 参考日槽 `'§8限购 3/日 · 硬顶总进本 ≤6'`：保留 `§8`、限购 1/周；「硬顶免费1+氪≤2」→「周硬顶 免费1+购买≤2」（体力包语境；「氪」改「购买」便于玩家一眼懂）。
- 不把「维护备忘」「遗留票物」「票已废」「B0.1」写进玩家 lore；不宣称玩法已清或票物已废。
- 不捆绑 cash、progress、已结窗、断塔/霜锈/AFK 或精英壳旁记；不加长 lore 列表。

## 3. 施工与验收

- [ ] 批 A 前 `plugins/TrMenu/menus/ember_shop.yml` 零 diff；批后仅由**插件岗 / TrMenu 菜单文案**替换约 L151 这一行。
- [ ] 仅目标 lore 变化；价/命令/tell/其它 lore/文件头/其它槽位零改。
- [ ] 静态核对旧行与荐案、限购 1/周、周硬顶 免费1+购买≤2、§8 色码；不做长测/挑刺。
- [ ] 本设计提交仅包含本 tip 与 backlog；工作区其它脏文件不纳入。

## 4. 回报与禁项

- 回报 tip 路径、STATUS、旧 lore 与荐案全文、施工岗、`ember_shop.yml` 零改确认、范围零捆绑确认、验收、pull、commit/ahead。
- 禁写玩家 lore：「票已废」「B0.1」「维护备忘」「遗留票物」；禁宣称玩法已清。
- 禁顺手改玩法 YAML、cash、progress、断塔/霜锈/AFK、已结窗；禁长测/挑刺；精英壳勿硬开。
- **勿 git push。**
