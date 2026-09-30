# B2.82 · cash `daily` 段注释「日本/发票」→遗留票物/体力口径维护备忘

- **STATUS：已批 A（总控 · 2026-09-30 21:38 Asia/Shanghai）· 交插件岗**
- **tip 路径：**`docs/design-ember-cash-daily-section-comment-copy.md`
- **目标范围：**仅 `plugins/CoreRpg/cash.yml` 约 L45 的 `daily` 段注释；未批前不改 `cash.yml`，不改玩法 YAML。
- **施工岗：**批 A 后交 **插件岗（CoreRpg cash 注释）**。
- **前序对齐：**B2.81 已批 `30b739b`；本窗承接 cash `daily` 段注释，backlog 对齐 B2.82。

## 1. 现况与锁定范围

已读 `plugins/CoreRpg/cash.yml` 约 L40–L55：

```yaml
# 日本：免费张数 + 总硬顶（进本/发票共用）
daily:
  free_tickets: 0  # S0 维护备忘：遗留票物停发；现行按体力日回满
  hard_cap: 6
  ticket_ni_id: ticket_ember_daily
```

原文「日本」可能是「日票」笔误/缩写；本稿不据此推断玩法，不改任何键值。`free_tickets`、`hard_cap`、`ticket_ni_id` 以及 `daily` 其它键值均锁定零改。

## 2. 荐案（批 A 后仅替换注释行）

```yaml
# 遗留票物/体力口径维护备忘：免费张数 + 总硬顶（进本/发放共用）
```

- 保留「免费张数 + 总硬顶」事实；「进本/发放共用」保留原注释的共用口径。
- 「遗留票物/体力口径维护备忘」仅作维护备注，不表示改动发放、消耗、数值或玩法逻辑。
- 不写票物废止或 B0.1 清账结论；不把本稿写成玩法已落地。

## 3. 施工与验收

- [ ] 批 A 前 `plugins/CoreRpg/cash.yml` 零 diff；批后仅由插件岗替换约 L45 这一条注释。
- [ ] `daily.free_tickets`、`daily.hard_cap`、`daily.ticket_ni_id` 及其它键值零改；`cash.yml` 除目标注释外零 diff。
- [ ] 静态核对旧注释与荐案、免费张数/总硬顶事实、进本/发放共用口径；不做长测/挑刺。
- [ ] 施工后交插件岗验收；本设计提交仅包含本 tip 与 backlog，工作区其它脏文件不纳入。

## 4. 旁记与禁项

- 不捆 schema `dailyTicketsGranted`、断塔/霜锈/AFK、`event_box`（已结）或其它旁记。
- 不改 `daily.free_tickets`、`hard_cap`、`ticket_ni_id` 数值/键名；不改其它玩法 YAML。
- 不扩写精英壳，不做长测/挑刺；不作票物废止、B0.1 清账或玩法已落地宣称。
- **勿 git push。**
