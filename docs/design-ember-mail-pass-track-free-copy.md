# B2.75 · CoreRpg mail `pass_track_free` 领取文案薄窗

- **STATUS：待批 A**（总控待批）
- **范围：**仅 `plugins/CoreRpg/mail.yml` 模板 `pass_track_free` 的 **body**；未批准前不改目标 mail.yml，不改玩法 YAML。
- **tip 路径：**`docs/design-ember-mail-pass-track-free-copy.md`
- **施工岗：**批后交 **插件岗（CoreRpg mail）**。
- **前序对齐：**B2.74 已批 A `607ced3`；本窗承接 backlog 的 B2.75，不回改已批事实。
- **本窗纪律：**不改键名 `pass_track_free`；`title`、`attachments` 与其它邮件模板零改；禁长测/挑刺；勿宣称 B0.1 已清或战令已完整落地；勿 git push。

## 1. 现况与范围

已读 `plugins/CoreRpg/mail.yml` 中 `pass_track_free` 全文：

```yaml
pass_track_free:
  title: "战令·免费轨补给"
  body: "赛季免费轨轻量补给（菜单一键领取）。重复点击会再发一封，请及时 claim。"
  attachments:
    coin: 80
    mat_ember_shard: 2
```

本窗只处理 body 末尾英文 `claim` 的玩家可见文案；不施工 mail.yml。

## 2. 旧文案 / 荐案

旧（live）：

```yaml
body: "赛季免费轨轻量补给（菜单一键领取）。重复点击会再发一封，请及时 claim。"
```

新（荐案，批后）：

```yaml
body: "赛季免费轨轻量补给（菜单一键领取）。重复点击会再发一封，请及时领取。"
```

### 2.1 锁定口径

- `pass_track_free` 键名保持不变；如需改键，另对源码，不在本窗处理。
- 仅替换该模板的 **body** 字符串；`title: "战令·免费轨补给"` 与 `attachments`（`coin: 80`、`mat_ember_shard: 2`）逐字保留。
- `maintenance_comp`、`season_pass_stub`、`vip_daily_gift`、`pass_track_paid_welcome`、`event_box` 等其它邮件模板零改。
- 荐案只将英文 `claim` 改成纯中文「领取」提醒；不新增指令，不宣称战令已完整落地，不宣称 B0.1 已清。

## 3. 施工与验收

- [ ] 批 A 后仅由**插件岗（CoreRpg mail）**更新 `plugins/CoreRpg/mail.yml` 的 `pass_track_free.body` 为荐案。
- [ ] `pass_track_free` 键名不变；`title` 与 `attachments`（`coin: 80`、`mat_ember_shard: 2`）零改。
- [ ] 其它邮件模板零改；mail.yml 除目标 body 外零 diff，YAML 结构可正常解析。
- [ ] 本设计提交仅包含本 tip 与 backlog；未批前 mail.yml 零 diff，不改玩法 YAML；其它脏文件不纳入。
- [ ] 验收做目标文案与范围静态复核；不扩成长测/挑刺。

## 4. 回报与禁项

- 回报 tip 路径、STATUS、施工岗、旧 body 与荐案全文、键名/title/attachments/其它模板零改确认、验收结果、pull 与 commit/ahead。
- 禁写「B0.1 已清」或等价完成宣称；禁宣称战令已完整落地。
- 禁长测/挑刺；勿顺手改其它邮件模板、玩法 YAML 或键名；勿新增指令；勿 git push。
