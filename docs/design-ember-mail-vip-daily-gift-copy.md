# B2.74 · CoreRpg mail `vip_daily_gift` 日礼文案维护备忘薄窗

- **STATUS：待批 A。**
- **范围：**仅 `plugins/CoreRpg/mail.yml` 模板 `vip_daily_gift` 的 **body**；未批前不改目标 mail.yml，不改玩法 YAML。
- **tip 路径：**`docs/design-ember-mail-vip-daily-gift-copy.md`
- **施工岗：**批后交 **插件岗（CoreRpg mail）**。
- **前序对齐：**B2.73 已批 A `87e40af`；本窗承接 backlog 的 B2.74，不回改已批事实。
- **本窗纪律：**不改键名 `vip_daily_gift`；`title`、`attachments` 与其它模板零改；禁长测/挑刺；精英壳勿硬开；勿 git push。

## 1. 现况与范围

已读 `plugins/CoreRpg/mail.yml` 中 `vip_daily_gift` 全文：

```yaml
vip_daily_gift:
  title: "勋阶·日礼"
  body: "轻量 QoL 日礼（勋阶正式累进前）。"
  attachments:
    coin: 120
    mat_ember_shard: 1
```

本窗只提出 body 的维护备忘口吻；不宣称勋阶完整落地，不施工 mail.yml。

## 2. 旧文案 / 荐案

旧（live）：

```yaml
body: "轻量 QoL 日礼（勋阶正式累进前）。"
```

新（荐案，批后）：

```yaml
body: "轻量 QoL 日礼仍在维护；勋阶累进口径未定稿。"
```

### 2.1 锁定口径

- `vip_daily_gift` 键名保持不变；如需改键，另对源码，不在本窗处理。
- 仅替换该模板的 `body` 字符串；`title: "勋阶·日礼"` 与 `attachments`（`coin: 120`、`mat_ember_shard: 1`）逐字保留。
- `maintenance_comp`、`season_pass_stub`、`pass_track_free`、`pass_track_paid_welcome`、`event_box` 等其它模板零改。
- 荐案只说明日礼仍轻量、勋阶累进尚未定稿；不写「正式已上线」类口径，不宣称勋阶完整落地。

## 3. 施工与验收

- [ ] 批 A 后仅由**插件岗（CoreRpg mail）**更新 `plugins/CoreRpg/mail.yml` 的 `vip_daily_gift.body` 为荐案。
- [ ] `vip_daily_gift` 键名不变；`title` 与 `attachments`（`coin: 120`、`mat_ember_shard: 1`）零改。
- [ ] 其它 mail 模板零改；mail.yml 除目标 body 外零 diff，YAML 结构可正常解析。
- [ ] 本设计提交仅包含本 tip 与 backlog；未批前 mail.yml 零 diff，不改玩法 YAML。
- [ ] 验收做目标文案与范围静态复核；不扩成长测/挑刺。

## 4. 回报与禁项

- 回报 tip 路径、STATUS、施工岗、旧 body 与荐案、键名/title/attachments/其它模板零改确认、验收结果、pull 与 commit/ahead。
- 禁写「票已废」「B0.1 已清」或等价完成/废止宣称；禁宣称勋阶完整落地或正式已上线。
- 禁长测/挑刺；精英壳勿硬开；勿顺手改其它 mail 模板、玩法 YAML 或键名；勿 git push。
