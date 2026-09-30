# B2.81 · CoreRpg mail `event_box` 活动箱正文维护备忘薄窗

- **STATUS：PASS · 勾销（总控 · 2026-09-30 21:36 Asia/Shanghai）** · 设计 `85291bf` · 批准 `30b739b` · 插件 `699a7cb` · 测 `50ca64f`
- **范围：**仅 `plugins/CoreRpg/mail.yml` 模板 `event_box` 的 **body**；`title`、`attachments` 及其它邮件模板零改。
- **tip 路径：**`docs/design-ember-mail-event-box-body-copy.md`
- **施工岗：**批 A 后交 **插件岗（CoreRpg mail）**。
- **前序对齐：**B2.80 已批 A（`a046b99`）；本窗只做 B2.81 backlog 对齐，不回改已批事实。
- **本窗纪律：**禁长测/挑刺；精英壳勿硬开；不捆断塔、霜锈、AFK；schema 已结；勿 git push。

## 1. 现况与范围

已读 `plugins/CoreRpg/mail.yml` 中 `event_box` 全文：

```yaml
event_box:
  title: "活动箱"
  body: "活动奖励。"
  attachments:
    consumable_ember_stamina_30: 1
    gem_ember_sharp: 1
```

本窗仅提出玩家可见的短中文维护备忘口吻；不宣称活动系统完整落地，不施工 `mail.yml`。

## 2. 旧文案 / 荐案

旧（live）：

```yaml
body: "活动奖励。"
```

新（荐案，批 A 后）：

```yaml
body: "活动箱邮件仍在维护；奖励按活动发放。"
```

### 2.1 锁定口径

- `event_box` 键名保持不变；如需改键，另对源码，不在本窗处理。
- 仅替换该模板的 `body` 字符串；`title: "活动箱"` 与 `attachments`（`consumable_ember_stamina_30: 1`、`gem_ember_sharp: 1`）逐字保留。
- `maintenance_comp`、`vip_daily_gift`、`season_pass_stub`、`pass_track_free`、`pass_track_paid_welcome` 等其它邮件模板零改。
- 荐案只说明活动箱邮件仍在维护、奖励按活动发放；不宣称活动系统完整落地，不写玩家打指令。

## 3. 施工与验收

- [ ] 批 A 后仅由**插件岗（CoreRpg mail）**更新 `plugins/CoreRpg/mail.yml` 的 `event_box.body` 为荐案。
- [ ] `event_box` 键名不变；`title` 与 `attachments`（`consumable_ember_stamina_30: 1`、`gem_ember_sharp: 1`）零改。
- [ ] 其它邮件模板零改；`mail.yml` 除目标 body 外零 diff，YAML 结构可正常解析。
- [ ] 本设计提交仅包含本 tip 与 backlog；未批前 `mail.yml` 零 diff，不改玩法 YAML。
- [ ] 验收做目标文案与范围静态复核；不扩成长测/挑刺。

## 4. 回报与禁项

- 回报 tip 路径、STATUS、施工岗、旧 body 与荐案、title/attachments/其它邮件模板零改确认、验收结果、pull 与 commit/ahead。
- 禁捆断塔、霜锈、AFK；schema 已结；禁长测/挑刺；精英壳勿硬开；勿顺手改其它邮件模板、玩法 YAML 或键名；勿 git push。
- 禁写「票已废」「B0.1 已清」或等价完成/废止宣称；禁宣称活动系统完整落地；勿引导玩家打指令。
