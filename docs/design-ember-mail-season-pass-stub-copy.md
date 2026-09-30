# B2.73 · CoreRpg mail `season_pass_stub` 占位文案维护备忘薄窗

- **STATUS：PASS · 勾销（总控 · 2026-09-30 20:58 Asia/Shanghai）** · 设计 `53b92d1` · 批准 `87e40af` · 插件 `7290f4d` · 测 `e166783`
- **范围：**仅 `plugins/CoreRpg/mail.yml` 模板 `season_pass_stub` 的 `title` / `body`；未批准前不改目标 mail.yml，不改玩法 YAML。
- **tip 路径：**`docs/design-ember-mail-season-pass-stub-copy.md`
- **施工岗：**批后交 **插件岗（CoreRpg mail）**。
- **前序对齐：**B2.72 DP README 表说明列已 PASS · 勾销；本窗只承接 `season_pass_stub` 文案维护，不回改已批事实。
- **本窗纪律：**不改键名 `season_pass_stub`；`attachments` 与其它模板零改；勿 git push。

## 1. 现况与范围

已读 `plugins/CoreRpg/mail.yml` 的 `season_pass_stub` 全文（L11–L16）：当前为战令结算邮件模板。目标只将 `title` / `body` 的「占位」措辞改成维护备忘口吻；键名、附件及其它模板不在本窗施工。

## 2. 旧文案 / 荐案

旧（live）：

```yaml
season_pass_stub:
  title: "战令结算（占位）"
  body: "赛季占位奖励。"
  attachments:
    ember_crystal_cash: 30
    coin: 200
```

新（荐案，批后）：

```yaml
season_pass_stub:
  title: "战令结算（维护备忘）"
  body: "赛季奖励尚未开放；本邮件仅作维护备忘。"
  attachments:
    ember_crystal_cash: 30
    coin: 200
```

### 2.1 锁定口径

- `season_pass_stub` 键名保持不变；如需改键，另对源码，不在本窗处理。
- 仅替换 `title` / `body` 两个字符串；`attachments` 的键和值逐字保留。
- 其它 mail 模板逐字保留；不宣称战令已完整落地。
- 荐案短、清楚，去掉玩家感「占位」措辞，保留尚未开放与维护用途说明。

## 3. 施工与验收

- [ ] 批 A 后仅由**插件岗（CoreRpg mail）**更新 `plugins/CoreRpg/mail.yml` 的 `season_pass_stub.title` / `body` 为荐案。
- [ ] `season_pass_stub` 键名不变；`attachments`（`ember_crystal_cash: 30`、`coin: 200`）零改。
- [ ] `maintenance_comp`、`vip_daily_gift`、`pass_track_free`、`pass_track_paid_welcome`、`event_box` 等其它模板零改。
- [ ] mail.yml 除上述两处文案外零 diff；YAML 结构可正常解析。
- [ ] 本设计提交仅包含本 tip 与 backlog；未批前 mail.yml 零 diff，不改玩法 YAML。
- [ ] 验收做目标文案与范围静态复核；不扩成长测/挑刺。

## 4. 回报与禁项

- 回报 tip 路径、STATUS、施工岗、旧 title/body 与荐案、键名及 attachments/其它模板零改确认、验收结果、pull 与 commit/ahead。
- 禁写「票已废」「B0.1 已清」或等价完成/废止宣称；禁宣称战令已完整落地。
- 禁长测/挑刺；精英壳勿硬开；勿顺手改其它 mail 模板、玩法 YAML 或键名；勿 git push。
