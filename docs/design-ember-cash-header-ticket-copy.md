# B2.76 · CoreRpg cash 文件头「日票」→体力口径维护备忘

- **STATUS：已批 A（总控 · 2026-09-30 21:10 Asia/Shanghai）** · 设计 tip `b39a214` · 交**插件岗（CoreRpg cash 注释）** · 仅改 L1/L3；L2 保持 live 原文
- **范围：**仅 `plugins/CoreRpg/cash.yml` 文件头约 L1、L3 的注释；未批准前不改目标 cash.yml，不改玩法 YAML。
- **tip 路径：**`docs/design-ember-cash-header-ticket-copy.md`
- **施工岗：**批后交 **插件岗（CoreRpg cash 注释）**。
- **前序对齐：**B2.75 已批 A `773f46d`，并已 PASS · 勾销（`1f7df85`）；本窗承接 backlog 的 B2.76，不回改已批事实。
- **本窗纪律：**只做文件头维护备忘；配置键值、体力段、商城段零改；禁长测/挑刺；精英壳勿硬开；勿宣称「票已废」或「B0.1 已清」；勿 git push。

## 1. 现况与范围

已读 `plugins/CoreRpg/cash.yml` 文件头约 20 行。当前仅处理文件头 L1、L3 的口径文字；L2 可保持不动，配置从 `enabled`、`timezone` 到体力段、商城键值均不施工。

旧注释（三行，live）：

```yaml
# CoreRpg cash — 晶钻 / 日票限购 / 月卡（DESIGN-ember-cash-monthly.md）
# 维护备忘：配置可先落盘；落地后纳入 CoreRpg reload
# 红线：不直售碎片/晶/核心；日票硬顶是总进本 6（免费+氪+月卡）
```

## 2. 荐案

新注释（荐案，批后仅替换 L1、L3）：

```yaml
# CoreRpg cash — 晶钻 / 体力 / 遗留票物 / 月卡（DESIGN-ember-cash-monthly.md）
# 维护备忘：当前进本按体力扣除；日票属遗留票物口径，配置可先落盘；落地后纳入 CoreRpg reload
# 红线：不直售碎片/晶/核心；总进本次数硬顶仍为 6（免费+氪+月卡），按体力口径维护
```

### 2.1 锁定口径

- 只把 L1 的「日票限购」改为「体力 / 遗留票物」短词，并把 L3 的说明改为体力口径维护备忘；不写「票已废」。
- 总进本次数硬顶仍明确为 **6（免费+氪+月卡）**；这里是次数红线的维护备忘，不改任何次数、发放或购买逻辑。
- L2「维护备忘：配置可先落盘；落地后纳入 CoreRpg reload」保持不动。
- `enabled`、`timezone`、`stamina` 全段、`currency`、`daily`、`shop`、`monthly` 及其它配置键值零改。
- 不捆绑 `progress.yml`「日票×5」注释、`event_box` body、断塔/霜锈/AFK 等旁记；这些另窗处理。

## 3. 施工与验收

- [ ] 批 A 后仅由**插件岗（CoreRpg cash 注释）**施工 `plugins/CoreRpg/cash.yml` 文件头：按荐案替换 L1、L3；L2 保持原文。
- [ ] `cash.yml` 除目标文件头注释外零 diff；`enabled`、`timezone`、体力段、商城键值及其它配置零改。
- [ ] 总进本次数硬顶仍为 6（免费+氪+月卡）的维护口径保留；不新增、不删除、不调整玩法逻辑。
- [ ] `progress.yml`「日票×5」注释、`event_box` body、断塔/霜锈/AFK 等旁记零捆绑。
- [ ] 本设计提交仅包含本 tip 与 backlog；未批前 `cash.yml` 零 diff，其它脏文件不纳入。
- [ ] 验收做文件头目标注释与范围静态复核；不扩成长测/挑刺。

## 4. 回报与禁项

- 回报 tip 路径、STATUS、施工岗、旧三行与荐案全文、配置键值零改确认、旁记零捆绑确认、cash.yml 零 diff、验收结果、pull 与 commit/ahead。
- 禁写「票已废」「B0.1 已清」或等价完成宣称。
- 禁长测/挑刺；精英壳勿硬开；勿顺手改玩法 YAML、`progress.yml`、`event_box` 或断塔/霜锈/AFK 旁记；勿 git push。
